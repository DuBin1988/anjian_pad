package com.aofeng.safecheck.modelview;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import gueei.binding.validation.validators.MaxLength;
import gueei.binding.validation.validators.RegexMatch;
import gueei.binding.validation.validators.Required;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.activity.RepairActivity;
import com.aofeng.safecheck.activity.ShootActivity;
import com.aofeng.utils.HttpMultipartPost;
import com.aofeng.utils.RemoteReader;
import com.aofeng.utils.RemoteReaderListener;
import com.aofeng.utils.ScrubblePane;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

/**
 * 入户安检model
 * 
 * @author lgy
 * 
 */
public class RepairModel {
	private final RepairActivity mContext;
	// 入户安检计划ID
	private String indoorInpsectID = "test";

	public RepairModel(RepairActivity Context) {
		this.mContext = Context;
		Bundle bundle = mContext.getIntent().getExtras();
		if (bundle != null)
			indoorInpsectID = bundle.getString("ID");

		InspectionDate.set(Util.FormatDateToday("yyyy-MM-dd"));
		ArrivalTime.set(Util.FormatTimeNow("HH:mm:ss"));
		this.StructureTypeList.setArray(new String[] { "平房", "多层", "小高层", "高层",
		"别墅" });
		this.HeatedTypeList.setArray(new String[] { "热力公司集中供暖", "小区集中供暖",
				"客户自行供暖", "其他供暖" });

	}

	/**
	 * 加亮当前选择
	 * 
	 * @param imgId
	 */
	private void HilightChosenImg(int imgId) {
		basicImgId.set(R.drawable.basic);
		meterImgId.set(R.drawable.meter);
		plumImgId.set(R.drawable.plum);
		cookerImgId.set(R.drawable.cooker);
		heaterImgId.set(R.drawable.heater);
		precautionImgId.set(R.drawable.precaution);
		feedbackImgId.set(R.drawable.feedback);
		repairImgId.set(R.drawable.repair);
		if (imgId == R.drawable.basic_selected) {
			basicImgId.set(imgId);
			muteOthers(R.id.basicPane);
		} else if (imgId == R.drawable.meter_selected) {
			meterImgId.set(imgId);
			muteOthers(R.id.meterPane);
		} else if (imgId == R.drawable.plum_selected) {
			plumImgId.set(imgId);
			muteOthers(R.id.plumPane);
		} else if (imgId == R.drawable.cooker_selected) {
			cookerImgId.set(imgId);
			muteOthers(R.id.cookerPane);
		}		
		else if(imgId == R.drawable.heater_selected)
		{
			heaterImgId.set(imgId);
			muteOthers(R.id.heaterPane);
		}
		else if(imgId == R.drawable.precaution_selected)
		{
			precautionImgId.set(imgId);
			muteOthers(R.id.precautionPane);
		} else if (imgId == R.drawable.feedback_selected) {
			feedbackImgId.set(imgId);
			muteOthers(R.id.feedbackPane);
		} else if (imgId == R.drawable.repair_selected) {
			repairImgId.set(imgId);
			muteOthers(R.id.repairPane);
		}
	}

	/**
	 * 隐藏除paneId外的所有LinearLayout
	 * 
	 * @param paneId
	 */
	public void muteOthers(int paneId) {
		int[] panes = {R.id.basicPane, R.id.meterPane, R.id.plumPane, R.id.cookerPane,R.id.heaterPane, R.id.precautionPane, R.id.feedbackPane, R.id.repairPane};
		for(int i=0; i<panes.length; i++)
			if(paneId == panes[i])
				mContext.findViewById(panes[i]).setVisibility(LinearLayout.VISIBLE);
			else
				mContext.findViewById(panes[i]).setVisibility(LinearLayout.GONE);
	}

	// 每种类型的安检信息一个
	public IntegerObservable basicImgId = new IntegerObservable(
			R.drawable.basic_selected);
	public Command MeterImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this.HilightChosenImg(R.drawable.meter_selected);
		}
	};

	public IntegerObservable plumImgId = new IntegerObservable(R.drawable.plum);
	public Command PlumImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this.HilightChosenImg(R.drawable.plum_selected);
		}
	};

	public IntegerObservable cookerImgId = new IntegerObservable(
			R.drawable.cooker);
	public Command CookerImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this
			.HilightChosenImg(R.drawable.cooker_selected);
		}
	};


	public IntegerObservable heaterImgId = new IntegerObservable(R.drawable.heater);
	public Command HeaterImgClicked = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this.HilightChosenImg(R.drawable.heater_selected);
		}
	};

	public IntegerObservable precautionImgId = new IntegerObservable(R.drawable.precaution);
	public Command PrecautionImgClicked = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this
			.HilightChosenImg(R.drawable.precaution_selected);
		}
	};

	public IntegerObservable meterImgId = new IntegerObservable(
			R.drawable.meter);
	public Command BasicImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this.HilightChosenImg(R.drawable.basic_selected);
		}
	};

	public IntegerObservable feedbackImgId = new IntegerObservable(
			R.drawable.feedback);
	public Command FeedbackImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this
			.HilightChosenImg(R.drawable.feedback_selected);
		}
	};

	public IntegerObservable repairImgId = new IntegerObservable(
			R.drawable.repair);
	public Command RepairImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			RepairModel.this
			.HilightChosenImg(R.drawable.repair_selected);
		}
	};

	
	// -------------------------------用户基本信息----------------------------------------
	// 无人应答
	public BooleanObservable IsNoAnswer = new BooleanObservable(false);

	// 拒绝入户
	public BooleanObservable IsEntryDenied = new BooleanObservable(false);

	// 安检日期
	public StringObservable InspectionDate = new StringObservable("");
	// 到达时间
	public StringObservable ArrivalTime = new StringObservable("");
	// 离开时间
	public StringObservable DepartureTime = new StringObservable("");

	// IC卡号
	@Required(ErrorMessage = "卡号不能为空")
	@RegexMatch(Pattern = "[0-9]{10}$", ErrorMessage = "卡号必须为10位数字！")
	public StringObservable ICCardNo = new StringObservable("");

	// @EqualsTo(Observable = "Password",
	// ErrorMessage="Confirm Password must match Password."
	// 户主姓名
	@MaxLength(Length = 20, ErrorMessage = "户主姓名长度不能超过20")
	@Required(ErrorMessage = "户主姓名不能为空")
	public StringObservable UserName = new StringObservable("");
	// 电话
	@MaxLength(Length = 20, ErrorMessage = "电话长度不能超过20")
	@Required(ErrorMessage = "电话不能为空")
	public StringObservable Telephone = new StringObservable("");
	// 已发到访不遇卡
	public BooleanObservable HasNotified = new BooleanObservable(false);
	// 小区名称
	public StringObservable ResidentialAreaName = new StringObservable("");
	// 小区地址
	public StringObservable ResidentialAreaAddress = new StringObservable("");
	// 楼号
	public StringObservable BuildingNo = new StringObservable("");
	// 单元
	public StringObservable UnitNo = new StringObservable("");
	// 层号
	public StringObservable LevelNo = new StringObservable("");
	// 房号
	public StringObservable RoomNo = new StringObservable("");
	// 用户档案地址
	public StringObservable ArchiveAddress = new StringObservable("");
	// 房屋结构
	public ArrayListObservable<String> StructureTypeList = new ArrayListObservable<String>(
			String.class);
	// 供热方式
	public ArrayListObservable<String> HeatedTypeList = new ArrayListObservable<String>(
			String.class);
	// 其他供暖方式
	public StringObservable OtherHeatedType = new StringObservable("");

	// 维修人
	public ArrayListObservable<RepairMan> RepairManList = new ArrayListObservable<RepairMan>(
			RepairMan.class);


	// ----------------------燃气表信息----------------------------------------
	// 基表数
	public StringObservable BaseMeterQuantity = new StringObservable("");
	// 表内剩余气量
	public StringObservable RemainGasQuantity = new StringObservable("");

	// 左表、右表不绑定
	// 基表号
	public StringObservable BaseMeterID = new StringObservable("");
	// 燃气表生产年份
	public StringObservable MeterMadeYear = new StringObservable("");
	// 基表厂家其他
	public StringObservable MeterMakerOther = new StringObservable("");
	// IC表厂家其他
	public StringObservable ICMeterMakerOther = new StringObservable("");
	// 表型其他
	//public StringObservable MeterTypeOther = new StringObservable("");
	// --------------------------------立管----------------------------
	// 立管静力压力
	public StringObservable PlumPressure = new StringObservable("");
	// 漏气位置
	public StringObservable LeakagePlace = new StringObservable("");
	// ---------------- 灶具
	// 灶具购置时间
	public StringObservable CookerBoughtTime = new StringObservable("");
	// 灶具安装管卡个数
	public StringObservable CookerPipeClampCount = new StringObservable("");
	// 灶具更换软管米数
	public StringObservable CookerPipeLength = new StringObservable("");
	// 热水器型号
	public StringObservable HeaterType = new StringObservable("");
	// 热水器购置日期
	public StringObservable HeaterBoughtTime = new StringObservable("");
	// 热水器安装管卡个数
	public StringObservable HeaterPipeClampCount = new StringObservable("");
	// 壁挂锅炉购置时间
	public StringObservable BoilerBoughtTime = new StringObservable("");
	// 壁挂锅炉型号
	public StringObservable BoilerType = new StringObservable("");
	// --------------------客户建议-----------------
	public StringObservable UserSuggestion = new StringObservable("");

	/**
	 * 上传
	 */
	public Command upload = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			mContext.SaveAndUploadRepairResult();
		}
	};

}


