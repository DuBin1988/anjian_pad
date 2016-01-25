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
 * �뻧����model
 * 
 * @author lgy
 * 
 */
public class RepairModel {
	private final RepairActivity mContext;
	// �뻧����ƻ�ID
	private String indoorInpsectID = "test";

	public RepairModel(RepairActivity Context) {
		this.mContext = Context;
		Bundle bundle = mContext.getIntent().getExtras();
		if (bundle != null)
			indoorInpsectID = bundle.getString("ID");

		InspectionDate.set(Util.FormatDateToday("yyyy-MM-dd"));
		ArrivalTime.set(Util.FormatTimeNow("HH:mm:ss"));
		this.StructureTypeList.setArray(new String[] { "ƽ��", "���", "С�߲�", "�߲�",
		"����" });
		this.HeatedTypeList.setArray(new String[] { "������˾���й�ů", "С�����й�ů",
				"�ͻ����й�ů", "������ů" });

	}

	/**
	 * ������ǰѡ��
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
	 * ���س�paneId�������LinearLayout
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

	// ÿ�����͵İ�����Ϣһ��
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

	
	// -------------------------------�û�������Ϣ----------------------------------------
	// ����Ӧ��
	public BooleanObservable IsNoAnswer = new BooleanObservable(false);

	// �ܾ��뻧
	public BooleanObservable IsEntryDenied = new BooleanObservable(false);

	// ��������
	public StringObservable InspectionDate = new StringObservable("");
	// ����ʱ��
	public StringObservable ArrivalTime = new StringObservable("");
	// �뿪ʱ��
	public StringObservable DepartureTime = new StringObservable("");

	// IC����
	@Required(ErrorMessage = "���Ų���Ϊ��")
	@RegexMatch(Pattern = "[0-9]{10}$", ErrorMessage = "���ű���Ϊ10λ���֣�")
	public StringObservable ICCardNo = new StringObservable("");

	// @EqualsTo(Observable = "Password",
	// ErrorMessage="Confirm Password must match Password."
	// ��������
	@MaxLength(Length = 20, ErrorMessage = "�����������Ȳ��ܳ���20")
	@Required(ErrorMessage = "������������Ϊ��")
	public StringObservable UserName = new StringObservable("");
	// �绰
	@MaxLength(Length = 20, ErrorMessage = "�绰���Ȳ��ܳ���20")
	@Required(ErrorMessage = "�绰����Ϊ��")
	public StringObservable Telephone = new StringObservable("");
	// �ѷ����ò�����
	public BooleanObservable HasNotified = new BooleanObservable(false);
	// С������
	public StringObservable ResidentialAreaName = new StringObservable("");
	// С����ַ
	public StringObservable ResidentialAreaAddress = new StringObservable("");
	// ¥��
	public StringObservable BuildingNo = new StringObservable("");
	// ��Ԫ
	public StringObservable UnitNo = new StringObservable("");
	// ���
	public StringObservable LevelNo = new StringObservable("");
	// ����
	public StringObservable RoomNo = new StringObservable("");
	// �û�������ַ
	public StringObservable ArchiveAddress = new StringObservable("");
	// ���ݽṹ
	public ArrayListObservable<String> StructureTypeList = new ArrayListObservable<String>(
			String.class);
	// ���ȷ�ʽ
	public ArrayListObservable<String> HeatedTypeList = new ArrayListObservable<String>(
			String.class);
	// ������ů��ʽ
	public StringObservable OtherHeatedType = new StringObservable("");

	// ά����
	public ArrayListObservable<RepairMan> RepairManList = new ArrayListObservable<RepairMan>(
			RepairMan.class);


	// ----------------------ȼ������Ϣ----------------------------------------
	// ������
	public StringObservable BaseMeterQuantity = new StringObservable("");
	// ����ʣ������
	public StringObservable RemainGasQuantity = new StringObservable("");

	// ����ұ���
	// �����
	public StringObservable BaseMeterID = new StringObservable("");
	// ȼ�����������
	public StringObservable MeterMadeYear = new StringObservable("");
	// ����������
	public StringObservable MeterMakerOther = new StringObservable("");
	// IC��������
	public StringObservable ICMeterMakerOther = new StringObservable("");
	// ��������
	//public StringObservable MeterTypeOther = new StringObservable("");
	// --------------------------------����----------------------------
	// ���ܾ���ѹ��
	public StringObservable PlumPressure = new StringObservable("");
	// ©��λ��
	public StringObservable LeakagePlace = new StringObservable("");
	// ---------------- ���
	// ��߹���ʱ��
	public StringObservable CookerBoughtTime = new StringObservable("");
	// ��߰�װ�ܿ�����
	public StringObservable CookerPipeClampCount = new StringObservable("");
	// ��߸����������
	public StringObservable CookerPipeLength = new StringObservable("");
	// ��ˮ���ͺ�
	public StringObservable HeaterType = new StringObservable("");
	// ��ˮ����������
	public StringObservable HeaterBoughtTime = new StringObservable("");
	// ��ˮ����װ�ܿ�����
	public StringObservable HeaterPipeClampCount = new StringObservable("");
	// �ڹҹ�¯����ʱ��
	public StringObservable BoilerBoughtTime = new StringObservable("");
	// �ڹҹ�¯�ͺ�
	public StringObservable BoilerType = new StringObservable("");
	// --------------------�ͻ�����-----------------
	public StringObservable UserSuggestion = new StringObservable("");

	/**
	 * �ϴ�
	 */
	public Command upload = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			mContext.SaveAndUploadRepairResult();
		}
	};

}


