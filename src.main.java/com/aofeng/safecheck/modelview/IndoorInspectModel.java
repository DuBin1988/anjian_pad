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
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.aofeng.safecheck.activity.AutographActivity;
import com.aofeng.safecheck.activity.IndoorInspectActivity;
import com.aofeng.safecheck.activity.QueryUserInfoActivity;
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
public class IndoorInspectModel {
	private final IndoorInspectActivity mContext;
	// 入户安检计划ID
	private String indoorInpsectPlanID = "test";

	public IndoorInspectModel(IndoorInspectActivity Context) {
		this.mContext = Context;
		Bundle bundle = mContext.getIntent().getExtras();
		if (bundle != null)
			indoorInpsectPlanID = bundle.getString("ID");

		InspectionDate.set(Util.FormatDateToday("yyyy-MM-dd"));
		ArrivalTime.set(Util.FormatTimeNow("HH:mm:ss"));
		this.StructureTypeList.setArray(new String[] { "高层", "多层", "小高层", 
		"别墅","平房"});
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
		}
	}

	/**
	 * 隐藏除paneId外的所有LinearLayout
	 * 
	 * @param paneId
	 */
	public void muteOthers(int paneId) {
		int[] panes = {R.id.basicPane, R.id.meterPane, R.id.plumPane, R.id.cookerPane,R.id.heaterPane, R.id.precautionPane, R.id.feedbackPane};
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
			IndoorInspectModel.this.HilightChosenImg(R.drawable.meter_selected);
		}
	};

	public IntegerObservable plumImgId = new IntegerObservable(R.drawable.plum);
	public Command PlumImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.plum_selected);
		}
	};

	public IntegerObservable cookerImgId = new IntegerObservable(
			R.drawable.cooker);
	public Command CookerImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this
			.HilightChosenImg(R.drawable.cooker_selected);
		}
	};


	public IntegerObservable heaterImgId = new IntegerObservable(R.drawable.heater);
	public Command HeaterImgClicked = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.heater_selected);
		}
	};

	public IntegerObservable precautionImgId = new IntegerObservable(R.drawable.precaution);
	public Command PrecautionImgClicked = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this
			.HilightChosenImg(R.drawable.precaution_selected);
		}
	};

	public IntegerObservable meterImgId = new IntegerObservable(
			R.drawable.meter);
	public Command BasicImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this.HilightChosenImg(R.drawable.basic_selected);
		}
	};

	public IntegerObservable feedbackImgId = new IntegerObservable(
			R.drawable.feedback);
	public Command FeedbackImgClicked = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			IndoorInspectModel.this
			.HilightChosenImg(R.drawable.feedback_selected);
		}
	};
	
	public Command saveInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			preUpload(false);
		}
	};

	
	/**
	 * 上传安检记录
	 */
	public Command UploadInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//判断单子是否已经保存过，保存过则提示，如果在此处上传，则入户时间、离开时间会被改变
			if(Util.TestIfSaved(mContext,  mContext.uuid))
			{
				TextView textView = new TextView(mContext);
				textView.setBackgroundColor(Color.WHITE);
				textView.setTextColor(Color.BLACK);
				textView.setMaxWidth(300);
				textView.setMaxLines(5);
				textView.setTextSize(25);
				textView.setText("本安检单已经保存过，如需上传请到上传界面。如在此上传，将会造成到达时间、离开时间重新计算。是否在此上传？");
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setCancelable(false);
				builder.setView(textView);
				builder.setTitle("警告");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setPositiveButton("确定", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						preUpload(true);
					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
			}
			else
				preUpload(true);
		}
	};
	
	/**
	 * 上传前判断隐患
	 */
	private void preUpload(boolean isUpload)
	{
		ArrayList<String> severeList = new ArrayList<String>();
		ArrayList<String> generalList = new ArrayList<String>();
		GetPrecautionMap(severeList, generalList);
		if(!(severeList.size()==0 && generalList.size()==0))
			PushDialog(severeList, generalList, isUpload);
		else
		{
			boolean needsRepair = ((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked();
			Upload(needsRepair, false, isUpload);
		}
	}

	public void GetRepairPerson(final String uuid, final String inspectTable) {
				SQLiteDatabase db = null;
				//设置界面
				try {
					RepairManList.clear();
					db = mContext.openOrCreateDatabase("safecheck.db",
							Context.MODE_PRIVATE, null);
					Cursor c = db.rawQuery("SELECT ID,CODE,NAME FROM T_PARAMS WHERE ID=?", new String[]{"维修人员"} );
					while(c.moveToNext())
					{
						RepairMan rm = new RepairMan();
						rm.id = c.getString(1);
						rm.name = c.getString(2);
						RepairManList.add(rm);
					}
					 c = db.rawQuery(
							"SELECT * FROM " + inspectTable  + " where id=?",
							new String[] { uuid });
					while (c.moveToNext()) {
						String id = c.getString(c.getColumnIndex("REPAIRMAN_ID"));
						if(id==null)
							return;
						((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).setChecked(false);
						for(int i=0; i<RepairManList.size(); i++)
						{
							RepairMan rm = RepairManList.get(i);
							if(rm.id.equals(id))
							{
								Spinner spinner = ((Spinner)mContext.findViewById(R.id.RepairManList));
								spinner.setSelection(i);
								((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).setChecked(true);
								break;
							}
						}
					}
					db.close();
					}
				catch(Exception e)
				{
					if(db != null)
						db.close();
				}
	}
		
	
	
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
	//@Required(ErrorMessage = "卡号不能为空")
	//@RegexMatch(Pattern = "[0-9]{15}$", ErrorMessage = "卡号必须为15位数字！")
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
	public StringObservable SignTelephone = new StringObservable("");
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
	//用户状态
	public StringObservable UserState = new StringObservable("");
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
	
	//用户编号
	public StringObservable UserID = new StringObservable("");

	// 维修人
	public ArrayListObservable<RepairMan> RepairManList = new ArrayListObservable<RepairMan>(
			RepairMan.class);

	/**
	 * 按卡号搜索相关信息
	 */
	/**
	 * sh20140106注释
	 */
	/*
	public Command SearchByICCardNo = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			// 从服务器获取IC卡号对应的用户信息
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String url = Vault.DB_URL + "one/"
								+ URLEncoder
								.encode("from t_userfiles user where user.f_cardid='"
										+ IndoorInspectModel.this.ICCardNo
										.get() + "'", "UTF8")
										.replace("+", "%20");
						HttpGet getMethod = new HttpGet(url);
						HttpClient httpClient = new DefaultHttpClient();
						HttpResponse response = httpClient.execute(getMethod);

						int code = response.getStatusLine().getStatusCode();

						// 数据下载完成
						if (code == 200) {
							String strResult = EntityUtils.toString(
									response.getEntity(), "UTF8");
							Message msg = new Message();
							msg.obj = strResult;
							msg.what = 1;
							listHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 2;
							listHandler.sendMessage(msg);
						}
					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 0;
						listHandler.sendMessage(msg);
					}
				}
			});
			th.start();
		}
	};
*/
	// 显示用户信息
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					JSONObject obj = new JSONObject((String) msg.obj);
					if (!obj.has("f_cardid")) {
						// 查不到此IC卡用户
						Toast.makeText(mContext, "无此用户信息！", Toast.LENGTH_SHORT)
						.show();
					} else {
						//String userMsgNow = "";
						String userMsgNow = obj.getString("f_address");
						String METER_TYPES_NAME = (String) obj
								.get("f_gaswatchbrand");

						String USER_NAME = obj.getString("f_username");
						String USER_LINK = obj.getString("f_phone");


						ArchiveAddress.set(userMsgNow);

						UserName.set(USER_NAME.equals("null")?"":USER_NAME);
						Telephone.set(USER_LINK.equals("null")?"":USER_LINK);

						//金卡、金卡非民用、金卡工业、金卡民用、先锋、先锋民用、NULL
						if ("金卡".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("金卡非民用".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("金卡工业".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("金卡民用".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("先锋".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerSaiFu.setChecked(true);
						}else if ("先锋民用".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerSaiFu.setChecked(true);
						}
//						else if ("秦川".equals(METER_TYPES_NAME)) {
//							mContext.ICMeterMakerQinChuan.setChecked(true);
//						} else if ("秦港".equals(METER_TYPES_NAME)) {
//							mContext.ICMeterMakerQinGang.setChecked(true);
//						} else if ("致力".equals(METER_TYPES_NAME)) {
//							mContext.ICMeterMakerZhiLi.setChecked(true);
//						}
						else {
							mContext.ICMeterMakerOtherBox.setChecked(true);
							ICMeterMakerOther.set(METER_TYPES_NAME);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (0 == msg.what) {
				Toast.makeText(mContext, "请检查网络或者与管理员联系。", Toast.LENGTH_LONG)
				.show();
				ArchiveAddress.set("");
				UserName.set("");
				Telephone.set("");
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "无此用户。", Toast.LENGTH_LONG).show();
				ArchiveAddress.set("");
				UserName.set("");
				Telephone.set("");
			}
		}
	};

	/**
	 * 20150106
	 * sh
	 * 
	 */
	
	//public StringObservable f_consumerphone = new StringObservable("");
	
	public Command SearchByICCardNo = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent(mContext,QueryUserInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("userName", UserName.get());
			bundle.putString("telephone", Telephone.get());
			bundle.putString("address", ArchiveAddress.get());
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 1);
		}
	};
	
	
	// ----------------------燃气表信息----------------------------------------
	// 基表数
	//@Required(ErrorMessage = "基表数不能为空")
	public StringObservable BaseMeterQuantity = new StringObservable("");
	// 表内剩余气量
	public StringObservable RemainGasQuantity = new StringObservable("");
	//用气量
	public StringObservable GasQuantity = new StringObservable("");
	//累计购气量
	public StringObservable BuyGasQuantity = new StringObservable("");
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
//	//灶具
//	public StringObservable Cooker = new StringObservable("");
//	//灶具老板
//	public StringObservable CookerGangHuaZiJing = new StringObservable("");
//	//灶具万和
//	public StringObservable CookerWanHe = new StringObservable("");
//	//灶具万家乐
//	public StringObservable CookerWanJiaLe = new StringObservable("");
//	//灶具方太
//	public StringObservable CookerLinNei = new StringObservable("");
//	//灶具海尔
//	public StringObservable CookerHaiEr = new StringObservable("");
//	//灶具帅康
//	public StringObservable CookerALiSiDun = new StringObservable("");
//	//灶具樱花
//	public StringObservable CookerYinhHua = new StringObservable("");
//	//灶具西门子
//	public StringObservable CookerXiMenZi = new StringObservable("");
//	//灶具华帝
//	public StringObservable CookerHuaDi = new StringObservable("");
//	//灶具其他
//	public StringObservable CookerOther = new StringObservable("");
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
	//备注
	public StringObservable Remark = new StringObservable("");

	/**
	 * 签名
	 */
	public Command sign = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent();
			// 利用包袱传递参数给Activity
			Bundle bundle = new Bundle();
			bundle.putString("ID", mContext.uuid + "_sign");
			intent.setClass(mContext, AutographActivity.class);
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 6);
		}
	};


	/**
	 * 获取隐患信息
	 */
	public void GetPrecautionMap(ArrayList<String> severePrecaution, ArrayList<String> generalPrecaution) {
		String precautionString = "";
		// 使用直排热水器
		if (mContext.HeaterPrecautionStraight.isChecked()) {
			precautionString = "使用直排热水器";
			severePrecaution.add(precautionString);
		}
		// 热水器烟道(无烟道，烟道破损，烟道未伸到室外，包裹烟道)
		if (mContext.HeaterPrecautionNoVentilation.isChecked()) {
			precautionString = "热水器烟道：无烟道 ";
			severePrecaution.add(precautionString);
		}
		if (mContext.HeaterPrecautionBrokenVent.isChecked()) {
			precautionString = "热水器烟道：烟道破损";
			severePrecaution.add(precautionString);
		}
		if (mContext.HeaterPrecautionTrapped.isChecked()) {
			precautionString = "热水器烟道：烟道未伸到室外 ";
			severePrecaution.add(precautionString);
		}
		if (mContext.HeaterPrecautionWrappedVent.isChecked()) {
			precautionString = "热水器烟道：包裹烟道";
			severePrecaution.add(precautionString);
		}
		// 热水器或壁挂锅炉安装在卧室
		if (mContext.HeaterPrecautionInHome.isChecked() || mContext.BoilerPrecautionInBedRoom.isChecked()) {
			precautionString = "热水器或壁挂锅炉安装在卧室";
			severePrecaution.add(precautionString);
		}
		// 燃气设施安装在卧室
		if (mContext.PrecautionInBedRoom.isChecked()) {
			precautionString = "燃气设施安装在卧室";
			severePrecaution.add(precautionString);
		}
		// 燃气表，户内管道，表前阀，自闭阀，灶前阀，包裹于密闭空间内
		if (mContext.MeterWrapped.isChecked()) {
			precautionString = "燃气表 ";
			generalPrecaution.add( precautionString + "包裹于密闭空间内" );
		}
		if (mContext.HomePlumWrapped.isChecked()) {
			precautionString = "户内管道 ";
			generalPrecaution.add( precautionString + "包裹于密闭空间内" );
		}
		if (mContext.MeterValveWrapped.isChecked()) {
			precautionString = "表前阀 ";
			generalPrecaution.add( precautionString + "包裹于密闭空间内" );		
		}
		if (mContext.AutoValveWrapped.isChecked()) {
			precautionString = "自闭阀 ";
			generalPrecaution.add( precautionString + "包裹于密闭空间内" );
		}
		if (mContext.CookerValveWrapped.isChecked()) {
			precautionString = "灶前阀 ";
			generalPrecaution.add( precautionString + "包裹于密闭空间内" );		
		}
		// 燃气管上挂物，接触电线等
		if (mContext.PrecautionElectricWire.isChecked()) {
			precautionString = "燃气管上挂物，接触电线等";
			generalPrecaution.add(precautionString);
		}
		// 连接软管有安全隐患
		if (mContext.PrecautionPipeInDark.isChecked()) {
			precautionString = "连接软管有安全隐患:软管暗敷 ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionLongPipe.isChecked()) {
			precautionString = "连接软管有安全隐患:软管过长 ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionLoosePipe.isChecked()) {
			precautionString = "连接软管有安全隐患:没有固定";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionThreeWay.isChecked()) {
			precautionString = "连接软管有安全隐患:私接三通 ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionPipeOutside.isChecked()) {
			precautionString = "连接软管有安全隐患:连接软管在户外 ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionThroughFurniture.isChecked()) {
			precautionString = "连接软管有安全隐患:穿过家具，屋顶  ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionThroughWall.isChecked()) {
			precautionString = "连接软管有安全隐患:穿过墙壁 ";
			generalPrecaution.add( precautionString);
		}
		// 燃气具链接气源位置漏气
		if (mContext.CookerPipeLeakage.isChecked()) {
			precautionString = "燃气具链接气源位置漏气:灶具软管漏气 ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.HeaterPipeLeakage.isChecked()) {
			precautionString = "燃气具链接气源位置漏气:热水器软管漏气 ";
			generalPrecaution.add( precautionString);
		}
		// 立管及户内管道漏气
		if (mContext.PlumLeakage.isChecked()
				|| mContext.HomePlumLeakage.isChecked()) {
			precautionString = "立管及户内管道漏气";
			generalPrecaution.add(precautionString);
		}
		// 使用非安全燃气具
		if (mContext.PrecautionUnsafeDevice.isChecked()) {
			precautionString = "使用非安全燃气具";
			generalPrecaution.add(precautionString);
		}
		// 燃气具安装，摆放位置不正确
		if (mContext.PrecautionMalPosition.isChecked()) {
			precautionString = "燃气具安装，摆放位置不规范";
			generalPrecaution.add(precautionString);
		}
		// 厨房它用
		if (mContext.HomePlumOtherUse.isChecked()) {
			precautionString = "厨房它用";
			generalPrecaution.add(precautionString);
		}
	}

	/**
	 * 弹出隐患对话框
	 */
	public void PushDialog(ArrayList<String> severePrecaution,ArrayList<String> generalPrecaution, final boolean isUpload)
	{
		
		LayoutInflater inflater = mContext.getLayoutInflater();
		View layout = inflater.inflate(R.layout.safehiddle,null);
		AlertDialog.Builder builder = new Builder(mContext);
		
		builder.setCancelable(false);

		builder.setPositiveButton("确认", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean needsRepair = ((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked();
				Upload(needsRepair, true, isUpload);
			}
		});
		
		//获取严重隐患的Layout
		LinearLayout SevereList = (LinearLayout)(layout.findViewById(R.id.Severe));		
		//对每一个严重隐患，产生TextView，添加到严重隐患布局中

		for(String name : severePrecaution) {
			TextView v = new TextView(mContext);
			v.setText(name);
			v.setTextSize(20);
			SevereList.addView(v);
		}
		
		//获取一般隐患的Layout
		LinearLayout ModerateLise = (LinearLayout)(layout.findViewById(R.id.Moderate));
		//对于每一个一般隐患，产生TextView，添加到一般隐患布局中
		for(String name : generalPrecaution) {
			TextView v = new TextView(mContext);
			v.setText(name);
			v.setTextSize(20);
			ModerateLise.addView(v);
		}
				
		builder.setView(layout);
		builder.create().show();
	}

	/**
	 * 本地保存并上传
	 */
	private void Upload(boolean saveRepair, boolean precautionNotified, boolean isUpload) {
		// 找所有的图片
		// 上传图片
		ArrayList<String> imgs = new ArrayList<String>();
		if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid
				+ "_sign.png")) {
			imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png");
			imgs.add("签名图片");
			imgs.add(mContext.uuid + "_sign");
		}
		for (int i = 1; i < 8; i++) {
			if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid
					+ "_" + i + ".jpg")) {
				imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i
						+ ".jpg");
				imgs.add("隐患照片" + i);
				imgs.add(mContext.uuid + "_" + i);
			}
		}
		//验证
		if(!mContext.validate())
			return;

		//本地保存
		String row = mContext.SaveToJSONString(saveRepair, true);
		//是否下发安全隐患整改通知书，弹出隐患对话框就算有
		try
		{
			JSONObject rowObj = new JSONObject(row);
			rowObj.put("PRECAUTION_NOTIFIED", precautionNotified?"是":"否");
			row = rowObj.toString();
		}
		catch(Exception e)
		{
			//ignore
		}
		if(!mContext.Save(row, "T_INSPECTION", "T_IC_SAFECHECK_HIDDEN", false))
		{
			mContext.localSaved = false;
			Toast.makeText(mContext, "保存安检记录到平板失败!", Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			mContext.localSaved = true;
			if(!isUpload)
				Toast.makeText(mContext, "保存安检记录成功!", Toast.LENGTH_SHORT).show();				
		}
		imgs.add(row);
		if(isUpload)
		{
			HttpMultipartPost poster = new HttpMultipartPost(mContext);
			poster.execute(imgs.toArray(new String[imgs.size()]));
		}
	}




}


