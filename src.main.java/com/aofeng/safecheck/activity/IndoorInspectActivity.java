package com.aofeng.safecheck.activity;

import gueei.binding.app.BindingActivity;
import gueei.binding.observables.StringObservable;
import gueei.binding.validation.ModelValidator;
import gueei.binding.validation.ValidationResult;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.IndoorInspectModel;
import com.aofeng.safecheck.modelview.RepairMan;
import com.aofeng.utils.MyDigitalClock;
import com.aofeng.utils.ScrubblePane;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class IndoorInspectActivity extends BindingActivity {
	// 入户安检计划ID
	private IndoorInspectModel model;
	
	//sh加
	private boolean status;
	
	//保存临时生成的UUID
	public String uuid;
	private boolean inspected;
	
	//进入界面时间
	//如果是未检，进入时，开始时间为进入界面时间，结束时间时钟开启。
	//                       锁屏时，记录model中的时间， 结束时间为时钟时间
	//                       解屏时，恢复开始时间、结束时间
	//                       上传时，开始时间为进入界面时间，结束时间为当前时间
	//                       被销毁，则同进入时
	//   正常已检，进入时，开始时间为库中时间，结束时间时钟禁止，结束时间为库中时间，同时禁止掉无人、拒检、到访不遇选项。
	//                       锁屏时，记录model中的时间， 结束时间为时钟时间
	//                       解屏时，恢复开始时间、结束时间
	//                       上传时，开始时间为进入界面时间，结束时间为当前时间
	//                       被销毁，则同进入时
	//  拒绝/无人，进入时，开始时间为库中时间，结束时间时钟禁止，结束时间为库中时间，允许无人、拒检、到访不遇选项。
	//                       修改无人、拒检状态时，重置开始时间(进入界面时间)、结束时间，启动结束时钟
	//                       锁屏时，记录model中的时间， 结束时间时钟时间
	//                       解屏时，恢复开始时间、结束时间
	//                       上传时，开始时间为进入界面时间，结束时间为当前时间
	//                       被销毁，则同进入时
	private Date entryDateTime;

	//是否派发维修
	private CheckBox IsDispatchRepair;
	
	//保存当前窗体输入内容是否已经保存到本地
	public boolean localSaved;
	public String paperId = "test";
	public String planId="";
	
	// ------------------------拍照------------------------------------
	private Button shoot1;
	private ImageView img1;
	private Button shoot2;
	private ImageView img2;
	private Button shoot3;
	private ImageView img3;
	private Button shoot4;
	private ImageView img4;
	private Button shoot5;
	private ImageView img5;
	private Button shoot6;
	private ImageView img6;
	private Button shoot7;
	private ImageView img7;
	// ---------------------燃气具信息---------------------------
	// 左表
	public RadioButton MeterOnTheLeft;
	// 右表
	public RadioButton MeterOnTheRight;
	// 气表正常
	public CheckBox MeterNormal;
	// 气表被包裹
	public CheckBox MeterWrapped;
	// 气表漏气
	public CheckBox MeterLeakage;
	// 长通表
	public CheckBox MeterFallThrough;
	// 死表
	public CheckBox MeterDead;
	// 表不过气
	public CheckBox MeterByPass;
	// 表-其他
	public CheckBox MeterOther;
	// 基表厂家型号 丹东
	public RadioButton MeterMakerDanDong;
	// 基表厂家型号 重检
	public RadioButton MeterMakerChongJian;
	// 基表厂家型号 赛福
	public RadioButton MeterMakerSaiFu;
	// 基表厂家型号 重前
	public RadioButton MeterMakerChongQian;
	// 基表厂家型号 山城
	public RadioButton MeterMakerShanCheng;
	// 基表厂家型号 天津自动化
	public RadioButton MeterMakerTianJinZiDongHua;
	// 基表厂家型号 其他
	public RadioButton MeterMakerOtherBox;
	// IC卡表厂家型号 其他
	public RadioButton ICMeterMakerOtherBox;
	// IC卡表厂家型号 华捷
	public RadioButton ICMeterMakerHuaJie;
	// IC卡表厂家型号 赛福
	public RadioButton ICMeterMakerSaiFu;
	// IC卡表厂家型号 秦川
	public RadioButton ICMeterMakerQinChuan;
	// IC卡表厂家型号 秦港
	public RadioButton ICMeterMakerQinGang;
	// IC卡表厂家型号 致力
	public RadioButton ICMeterMakerZhiLi;
	// 表型 G2.5
	public RadioButton MeterTypeG25;
	// 表型 G4
	public RadioButton MeterTypeG4;
	// 表型 G6
	public RadioButton MeterTypeG6;
	// 表型 G10
	public RadioButton MeterTypeG10;
	// 表型 G16
	public RadioButton MeterTypeG16;
	// 表型 G25
	public RadioButton MeterTypeg25;
	// 表型 G40
	public RadioButton MeterTypeG40;
	// 表型流量计
	public RadioButton MeterTypeOther;
	// ------------------------------立管-------------------------
	// 立管正常
	public CheckBox PlumNormal;
	// 立管包裹
	public CheckBox PlumWrapped;
	// 立管腐蚀
	public CheckBox PlumEroded;
	public RadioButton PlumErosionSevere;
	public RadioButton PlumErosionModerate;
	public RadioButton PlumErosionSlight;
	// 立管漏气
	public CheckBox PlumLeakage;
	// 立管其他
	public CheckBox PlumOther;
	// 立管严密性 正常 漏气
	public CheckBox PlumProofNormal;
	public CheckBox PlumProofLeakage;
	// 立管静止压力 正常 漏气
	public CheckBox PlumPressureNormal;
	public CheckBox PlumPressureAbnormal;
	// 表前阀 正常、内漏、漏气、球阀、旋塞阀、包裹
	public CheckBox MeterValveNormal;
	public CheckBox MeterValveInnerLeakage;
	public CheckBox MeterValveLeakage;
	public CheckBox MeterValveBall;
	public CheckBox MeterValvePlug;
	public CheckBox MeterValveWrapped;
	// 灶前阀 正常、内漏、漏气、包裹、安装过高
	public CheckBox CookerValveNormal;
	public CheckBox CookerValveInnerLeakage;
	public CheckBox CookerValveLeakage;
	public CheckBox CookerValveWrapped;
	public CheckBox CookerValveTooHigh;
	// 自闭阀 正常、内漏、漏气、包裹、失灵
	public CheckBox AutoValveNormal;
	public CheckBox AutoValveInnerLeakage;
	public CheckBox AutoValveLeakage;
	public CheckBox AutoValveNotWork;
	public CheckBox AutoValveWrapped;
	// 户内管 正常 漏气 穿客厅 与电路器过进 包裹 私改 厨房他用
	public CheckBox HomePlumNormal;
	public CheckBox HomePlumLeakage;
	public CheckBox HomePlumThroughSittingRoom;
	public CheckBox HomePlumThroughBedRoom;
	public CheckBox HomePlumNearAppliance;
	public CheckBox HomePlumWrapped;
	public CheckBox HomePlumModified;
	public CheckBox HomePlumOtherUse;

	// 漏气、灶具漏气、热水器漏气、壁挂锅炉漏气、已发放安全告知书
	public CheckBox LeakageCooker;
	public CheckBox LeakageHeater;
	public CheckBox LeakageBoiler;
	public CheckBox LeakageNotified;
	// ----------------------------------------灶具------------------------
	// 灶具 港华紫荆 万和 万家乐 林内 海尔 阿里斯顿 樱花 华帝 其他
	public RadioButton CookerGangHuaZiJing;
	public RadioButton CookerWanHe;
	public RadioButton CookerWanJiaLe;
	public RadioButton CookerLinNei;
	public RadioButton CookerHaiEr;
	public RadioButton CookerALiSiDun;
	public RadioButton CookerYinhHua;
	public RadioButton CookerHuaDi;
	public RadioButton CookerXiMenZi;
	public RadioButton CookerOther;

	// 灶具类型 台式单眼 台式双眼 镶嵌双眼
	public RadioButton CookeTypeTabletSingle;
	public RadioButton CookerTypeTabletDouble;
	public RadioButton CookerTypeEmbedDouble;

	// 灶具软管 正常 漏气 老化 有安全隐患
	public CheckBox CookerPipeNormal;
	public CheckBox CookerPipeLeakage;
	public CheckBox CookerPipeFatigue;
	public CheckBox CookerPipePrecaution;

	// 热水器品牌
	public RadioButton HeaterGangHuaZiJing;
	public RadioButton HeaterWanHe;
	public RadioButton HeaterWanJiaLe;
	public RadioButton HeaterLinNei;
	public RadioButton HeaterHaiEr;
//	public RadioButton HeaterALiSiDun;
	public RadioButton HeaterYingHua;
	public RadioButton HeaterHuaDi;
	public RadioButton HeaterXiaoSongShu;
	public RadioButton HeaterShiMiSi;
	public RadioButton HeaterOther;
	// 热水器软管
	public CheckBox HeaterPipeNormal;
	public CheckBox HeaterPipeLeakage;
	public CheckBox HeaterPipeFatigue;
	public CheckBox HeaterPipePrecaution;
	public CheckBox HeaterPipePlastic;
	// 热水器烟道
	public RadioButton VentilationBalanced; // 平衡
	public RadioButton VentilationForce; // 强排
	public RadioButton VentilationPath; // 烟道
	public RadioButton VentilationStraight; // 直排
	// 热水器隐患
	public CheckBox HeaterPrecautionNone; // 无
	public CheckBox HeaterPrecautionStraight; // 直排热水器
	public CheckBox HeaterPrecautionNoVentilation; // 未安装烟道
	public CheckBox HeaterPrecautionTrapped; // 烟道未接到室外
	public CheckBox HeaterPrecautionProhibited; // 严禁使用
	public CheckBox HeaterPrecautionInHome; // 安装在卧室
	public CheckBox HeaterPrecautionBrokenVent; // 烟道破损
	public CheckBox HeaterPrecautionWrappedVent; // 烟道包裹

	// 壁挂锅炉
	public RadioButton BoilerGangHuaZiJing; // 港华紫荆
	public RadioButton BoilerWanHe; // 万和
	public RadioButton BoilerWanJiaLe; // 万家乐
	public RadioButton BoilerLinNei; // 林内
	public RadioButton BoilerHaiEr; // 海尔
//	public RadioButton BoilerALiSiDun; // 阿里斯顿
	public RadioButton BoilerYingHua; // 樱花
	public RadioButton BoilerHuaDi; // 华帝
	public RadioButton BoilerOther; // 其他
	public RadioButton BoilerXiaoSongShu;
	public RadioButton BoilerShiMiSi;
	//-------------------------------壁挂锅炉隐患------------------------
	public CheckBox BoilerPrecautionNormal; // 隐患正常
	public CheckBox BoilerPrecautionInBedRoom; // 安装在卧室
	public CheckBox BoilerPrecautionNotified; // 已发告知书

	// ------------------------------------安全隐患-----------------------
	public CheckBox PrecautionInBathRoom; // 燃气设施安装在卧室/卫生间
	public CheckBox PrecautionInBedRoom; // 燃气设备安装在卧室
	public CheckBox PrecautionLongPipe; // 软管过长
	public CheckBox PrecautionElectricWire; // 燃气管挂物接触电源线
	public CheckBox PrecautionThreeWay; // 软管接三通
	public CheckBox PrecautionThroughFurniture; // 软管穿柜/门窗/顶棚
	public CheckBox PrecautionThroughWall; // 软管穿墙/地面
	public CheckBox PrecautionValidPipe; // 使用非天然气专用软管
	public CheckBox PrecautionWithConnector; // 软管上有接头
	public CheckBox PrecautionNearFire; // 软管离火源太近
	public CheckBox PrecautionWrapped; // 软管包裹
	public CheckBox PrecautionNotified; // 已发近期安检报告书
	public CheckBox PrecautionNoClamp; // 无管卡
	public CheckBox PrecautionMalPosition; // 燃气具安装，摆放位置不规范
	public CheckBox PrecautionNearLiquefiedGas; // 与液化气共处一室
	public CheckBox PrecautionPipeInDark; // 软管暗敷
	public CheckBox PrecautionPipeOutside; // 连接软管在户外
	public CheckBox PrecautionUnsafeDevice; // 使用非安全燃气具
	public CheckBox PrecautionLoosePipe; // 连接软管没有固定

	// -------------------------------------用户评价-------------------------------
	public RadioButton FeebackSatisfied; // 无管卡
	public RadioButton FeebackOK; // 燃气具安装，摆放位置不规范
	public RadioButton FeebackUnsatisfied; // 与液化气共处一室

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new IndoorInspectModel(this);
		this.setAndBindRootView(R.layout.indoor_inspect, model);
		model.muteOthers(R.id.basicPane);
		Bundle bundle = getIntent().getExtras();
		boolean readonly = false;
		if (bundle != null) {
			paperId = bundle.getString("ID");
			planId = bundle.getString("CHECKPLAN_ID");
			model.LevelNo.set(bundle.getString("CUS_FLOOR"));
			model.RoomNo.set(bundle.getString("CUS_ROOM"));
			model.ResidentialAreaName.set(bundle.getString("UNIT_NAME"));
			model.ResidentialAreaAddress.set(bundle.getString("ROAD"));
			model.BuildingNo.set(bundle.getString("CUS_DOM"));
			model.UnitNo.set(bundle.getString("CUS_DY"));
			inspected = bundle.getBoolean("INSPECTED");
			if(bundle.containsKey("READONLY"))
				readonly = bundle.getBoolean("READONLY");
		}
		uuid = Util.getSharedPreference(this, Vault.USER_ID) + "_" + paperId;
		shoot1 = (Button) findViewById(R.id.shoot1);
		shoot1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_1");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img1 = (ImageView) findViewById(R.id.image1);
		shoot2 = (Button) findViewById(R.id.shoot2);
		shoot2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_2");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img2 = (ImageView) findViewById(R.id.image2);
		shoot3 = (Button) findViewById(R.id.shoot3);
		shoot3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_3");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img3 = (ImageView) findViewById(R.id.image3);
		shoot4 = (Button) findViewById(R.id.shoot4);
		shoot4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_4");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img4 = (ImageView) findViewById(R.id.image4);
		shoot5 = (Button) findViewById(R.id.shoot5);
		shoot5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_5");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img5 = (ImageView) findViewById(R.id.image5);
		shoot6 = (Button) findViewById(R.id.shoot6);
		shoot6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_6");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img6 = (ImageView) findViewById(R.id.image6);
		shoot7 = (Button) findViewById(R.id.shoot7);
		shoot7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 利用包袱传递参数给Activity
				Bundle bundle = new Bundle();
				bundle.putString("ID", uuid + "_7");
				intent.setClass(IndoorInspectActivity.this, ShootActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
		});
		img7 = (ImageView) findViewById(R.id.image7);
		Button clear1 = (Button) findViewById(R.id.clear1);
		clear1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img1.setImageBitmap(null);
				Util.releaseBitmap(img1);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_1.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "1.jpg").delete();
			}
		});
		Button clear2 = (Button) findViewById(R.id.clear2);
		clear2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img2.setImageBitmap(null);
				Util.releaseBitmap(img2);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_2.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "2.jpg").delete();
			}
		});
		Button clear3 = (Button) findViewById(R.id.clear3);
		clear3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img3.setImageBitmap(null);
				Util.releaseBitmap(img3);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_3.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "3.jpg").delete();
			}
		});
		Button clear4 = (Button) findViewById(R.id.clear4);
		clear4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img4.setImageBitmap(null);
				Util.releaseBitmap(img4);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_4.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "4.jpg").delete();
			}
		});
		Button clear5 = (Button) findViewById(R.id.clear5);
		clear5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img5.setImageBitmap(null);
				Util.releaseBitmap(img5);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_5.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "5.jpg").delete();
			}
		});
		Button clear6 = (Button) findViewById(R.id.clear6);
		clear6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img6.setImageBitmap(null);
				Util.releaseBitmap(img6);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_6.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "6.jpg").delete();
			}
		});
		Button clear7 = (Button) findViewById(R.id.clear7);
		clear7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img7.setImageBitmap(null);
				Util.releaseBitmap(img7);
				if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
						+ "_7.jpg"))
					new File(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_"
							+ "7.jpg").delete();
			}
		});
		OnClickListener imgZoom = new OnClickListener()
		{
			@Override
			public void onClick(View v) {		
				int vid = v.getId();
				if(vid == R.id.image1)
					showZoomDialog(1);
				else if(vid == R.id.image2)
					showZoomDialog(2);
				else if(vid == R.id.image3)
					showZoomDialog(3);
				else if(vid == R.id.image4)
					showZoomDialog(4);
				else if(vid == R.id.image5)
					showZoomDialog(5);
				else if(vid == R.id.image6)
					showZoomDialog(6);
				else if(vid == R.id.image7)
					showZoomDialog(7);
			}
		};
		img1.setOnClickListener(imgZoom);
		img2.setOnClickListener(imgZoom);
		img3.setOnClickListener(imgZoom);
		img4.setOnClickListener(imgZoom);
		img5.setOnClickListener(imgZoom);
		img6.setOnClickListener(imgZoom);
		img7.setOnClickListener(imgZoom);
		InitControls();
		if(readonly)
			DisableLayouts();
		preDisplayUIWork();
		LinkNotified();
		SearchUserList();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		SearchInfoFromDB();
	}
	
	private void SearchInfoFromDB() {
		try {
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);
			Cursor c = db.rawQuery("select * from T_SAFECHECK where f_road = '" + model.ResidentialAreaAddress.get() + "'" +
					" and f_districtname = '" + model.ResidentialAreaName.get() + "'" +
							" and f_cusDom = '" + model.BuildingNo.get() + "' " +
									"and f_cusDy = '" + model.UnitNo.get() + "'" +
											" and f_cusFloor = '" + model.LevelNo.get() + "' " +
													"and f_apartment = '" + model.RoomNo.get() + "'", new String[] {});
			while (c.moveToNext()) {
				String cook_brand = c.getString(c.getColumnIndex("COOK_BRAND"));
				if(cook_brand == null)
					cook_brand = "其他";
				if (cook_brand.equals("老板")) {
					this.CookerGangHuaZiJing.setChecked(true);
				} else if (cook_brand.equals("万和")) {
					this.CookerWanHe.setChecked(true);
				} else if (cook_brand.equals("万家乐")) {
					this.CookerWanJiaLe.setChecked(true);
				} else if (cook_brand.equals("方太")) {
					this.CookerLinNei.setChecked(true);
				} else if (cook_brand.equals("海尔")) {
					this.CookerHaiEr.setChecked(true);
				} else if (cook_brand.equals("帅康")) {
					this.CookerALiSiDun.setChecked(true);
				} else if (cook_brand.equals("樱花")) {
					this.CookerYinhHua.setChecked(true);
				} else if (cook_brand.equals("华帝")) {
					this.CookerHuaDi.setChecked(true);
				} else if (cook_brand.equals("其他")) {
					this.CookerOther.setChecked(true);
				} else if (cook_brand.equals("西门子")) {
					this.CookerXiMenZi.setChecked(true);
				}
				
				String cook_type = c.getString(c.getColumnIndex("COOK_TYPE"));
				if(cook_type == null)
					cook_type = "";
				if (cook_type.equals("台式单眼")) {
					this.CookeTypeTabletSingle.setChecked(true);
				} else if (cook_type.equals("台式双眼")) {
					this.CookerTypeTabletDouble.setChecked(true);
				} else if (cook_type.equals("镶嵌双眼")) {
					this.CookerTypeEmbedDouble.setChecked(true);
				}
				
				model.CookerBoughtTime.set(c.getString(c.getColumnIndex("COOK_DATE")));
				
				String water_brand = c.getString(c
						.getColumnIndex("WATER_BRAND"));
				if(water_brand == null)
					water_brand = "";
				if (water_brand.equals("美的")) {
					this.HeaterGangHuaZiJing.setChecked(true);
				} else if (water_brand.equals("万和")) {
					this.HeaterWanHe.setChecked(true);
				} else if (water_brand.equals("万家乐")) {
					this.HeaterWanJiaLe.setChecked(true);
				} else if (water_brand.equals("林内")) {
					this.HeaterLinNei.setChecked(true);
				} else if (water_brand.equals("海尔")) {
					this.HeaterHaiEr.setChecked(true);
				} else if (water_brand.equals("樱花")) {
					this.HeaterYingHua.setChecked(true);
				} else if (water_brand.equals("华帝")) {
					this.HeaterHuaDi.setChecked(true);
				} else if (water_brand.equals("其他")) {
					this.HeaterOther.setChecked(true);
				} else if (water_brand.equals("史密斯")) {
					this.HeaterShiMiSi.setChecked(true);
				} else if (water_brand.equals("小松鼠")) {
					this.HeaterXiaoSongShu.setChecked(true);
				}
				
				if(c.getString(c.getColumnIndex("WATER_TYPE")) != null)
					model.HeaterType.set(c.getString(c.getColumnIndex("WATER_TYPE")));
				else
					model.HeaterType.set("");
				
				if(c.getString(c.getColumnIndex("WATER_FLUE")) != null)
				{
					if (c.getString(c.getColumnIndex("WATER_FLUE")).equals("平衡")) {
						this.VentilationBalanced.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"强排")) {
						this.VentilationForce.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"烟道")) {
						this.VentilationPath.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"直排")) {
						this.VentilationStraight.setChecked(true);
					}
				}
				
				model.HeaterBoughtTime.set(c.getString(c.getColumnIndex("WATER_DATE")));
				
				if(c.getString(c.getColumnIndex("WHE_BRAND")) != null)
				{
					if (c.getString(c.getColumnIndex("WHE_BRAND")).equals("美的")) {
						this.BoilerGangHuaZiJing.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"万和")) {
						this.BoilerWanHe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"万家乐")) {
						this.BoilerWanJiaLe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"林内")) {
						this.BoilerLinNei.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"海尔")) {
						this.BoilerHaiEr.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"小松鼠")) {
						this.BoilerXiaoSongShu.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"樱花")) {
						this.BoilerYingHua.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"华帝")) {
						this.BoilerHuaDi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"史密斯")) {
						this.BoilerShiMiSi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"其他")) {
						this.BoilerOther.setChecked(true);
					}
				}
				
				if(c.getString(c.getColumnIndex("WHE_TYPE")) != null)
					model.BoilerType.set(c.getString(c.getColumnIndex("WHE_TYPE")));
				else 
					model.BoilerType.set("");

				model.BoilerBoughtTime.set(c.getString(c.getColumnIndex("WHE_DATE")));
				
				String ic_menter_name = c.getString(c
						.getColumnIndex("IC_METER_NAME"));
				if(ic_menter_name == null)
					ic_menter_name = "";
				if (ic_menter_name.equals("金卡")) {
					this.ICMeterMakerHuaJie.setChecked(true);
				} else if (ic_menter_name.equals("先锋")) {
					this.ICMeterMakerSaiFu.setChecked(true);
				} 
				else {
					this.ICMeterMakerOtherBox.setChecked(true);
					model.ICMeterMakerOther.set(ic_menter_name);
				}
				
				String jb_menter_name = c.getString(c
						.getColumnIndex("JB_METER_NAME"));
				if(jb_menter_name == null)
					jb_menter_name = "";
				if (jb_menter_name.equals("丹东")) {
					this.MeterMakerDanDong.setChecked(true);
				} else if (jb_menter_name.equals("重检")) {
					this.MeterMakerChongJian.setChecked(true);
				} else if (jb_menter_name.equals("赛福")) {
					this.MeterMakerSaiFu.setChecked(true);
				} else if (jb_menter_name.equals("重前")) {
					this.MeterMakerChongQian.setChecked(true);
				} else if (jb_menter_name.equals("山城")) {
					this.MeterMakerShanCheng.setChecked(true);
				} else if (jb_menter_name.equals("天津自动化")) {
					this.MeterMakerTianJinZiDongHua.setChecked(true);
				} else {
					this.MeterMakerOtherBox.setChecked(true);
					model.MeterMakerOther.set(jb_menter_name);
				}
				
				String menter_type = c
						.getString(c.getColumnIndex("METER_TYPE"));
				if(menter_type == null)
					menter_type = "";
				if (menter_type.equals("G2.5")) {
					this.MeterTypeG25.setChecked(true);
				}else if (menter_type.equals("G4")) {
					this.MeterTypeG4.setChecked(true);
				}else if (menter_type.equals("G6")) {
					this.MeterTypeG6.setChecked(true);
				}else if (menter_type.equals("G10")) {
					this.MeterTypeG10.setChecked(true);
				}else if (menter_type.equals("G16")) {
					this.MeterTypeG16.setChecked(true);
				}else if (menter_type.equals("G25")) {
					this.MeterTypeg25.setChecked(true);
				}else if (menter_type.equals("G40")) {
					this.MeterTypeG40.setChecked(true);
				}else if (menter_type.equals("流量计")) {
					this.MeterTypeOther.setChecked(true);
				}
				
				if(c.getString(c.getColumnIndex("gas_quantity")) != null )
					model.GasQuantity.set(c.getString(c.getColumnIndex("gas_quantity")));
				else
					model.GasQuantity.set("");			
			}
		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
		}
	}

	/**
	 * 根据安检下发的六级地址查找符合条件的用户
	 */
	private void SearchUserList()
	{
		Intent intent = new Intent(this,QueryUserInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("UnitName", model.ResidentialAreaName.get());
		bundle.putString("BuildingNo", model.BuildingNo.get());
		bundle.putString("UnitNo", model.UnitNo.get());
		bundle.putString("LevelNo", model.LevelNo.get());
		bundle.putString("RoomNo", model.RoomNo.get());
		bundle.putString("userName", model.UserName.get());
		bundle.putString("telephone", model.Telephone.get());
		bundle.putString("address", model.ArchiveAddress.get());
		intent.putExtras(bundle);
		this.startActivityForResult(intent, 1);
	}

	//显示图片对话框
	private void showZoomDialog(int  vid)
	{
		if (!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid + "_" + vid + ".jpg"))
			return;
		final ImageView iv = new ImageView(this);
		iv.layout(0, 0, 600, 400);
		try
		{
			Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
					+ uuid + "_" + vid + ".jpg");
			iv.setImageBitmap(bmp);
			Dialog alertDialog = new AlertDialog.Builder(this).   
					setView(iv).
					setTitle("").
					setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							Util.releaseBitmap(iv);
						}
					}).
					setIcon(android.R.drawable.ic_dialog_info).
					create();   
			WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();  
			layoutParams.width = 600;
			layoutParams.height= 400;
			alertDialog.getWindow().setAttributes(layoutParams);
			alertDialog.show();
		}
		catch(Exception e)
		{
			Toast.makeText(this, "获取图片失败。错误： " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 界面相关工作
	 */
	private void preDisplayUIWork() {
		findViewById(R.id.chkHasNotified).setEnabled(false);
		//无人
		((Switch)findViewById(R.id.noAnswerSwitch)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					findViewById(R.id.DenialSwitch).setEnabled(!isChecked);
					findViewById(R.id.chkHasNotified).setEnabled(isChecked);
					model.IsNoAnswer.set(isChecked);
					if(isChecked)
						model.IsEntryDenied.set(false);
			}
		});
		//拒绝入户
		((Switch)findViewById(R.id.DenialSwitch)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					findViewById(R.id.noAnswerSwitch).setEnabled(!isChecked);
					findViewById(R.id.chkHasNotified).setEnabled(!isChecked);
					model.IsEntryDenied.set(isChecked);
					if(isChecked)
					{
						model.HasNotified.set(false);
						 model.IsNoAnswer.set(false);
					}
			}
		});
		//腐蚀
		PlumEroded.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					PlumErosionSevere.setChecked(isChecked);
				}
				else
				{
					PlumErosionSevere.setChecked(isChecked);
					PlumErosionModerate.setChecked(isChecked);
					PlumErosionSlight.setChecked(isChecked);
				}
			}
		});
		
		//供暖方式
		((Spinner)findViewById(R.id.HeatedTypeList)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				if(pos != 3)
					model.OtherHeatedType.set("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		//其他供暖方式
		((EditText)findViewById(R.id.OtherHeatedType)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				((Spinner)findViewById(R.id.HeatedTypeList)).setSelection(3);
			}
		});
		
		//基表厂家型号 其他
		((EditText)findViewById(R.id.MeterMakerOther)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				MeterMakerOtherBox.setChecked(true);
			}
		});
		
		//IC卡表厂家型号
		((EditText)findViewById(R.id.ICMeterMakerOther)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				ICMeterMakerOtherBox.setChecked(true);
			}
		});
		//灶具未用
		((CheckBox)findViewById(R.id.UnusedCooker)).setOnCheckedChangeListener(ClearCookerInfo);
		//热水器未用
		((CheckBox)findViewById(R.id.UnusedWaterHeater)).setOnCheckedChangeListener(ClearWaterHeaterInfo);
		//壁挂锅炉未用
		((CheckBox)findViewById(R.id.UnusedWallBoiler)).setOnCheckedChangeListener(ClearWallBoilerInfo);
		
		//基表厂家型号 其他
		MeterMakerDanDong.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerChongJian.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerSaiFu.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerChongQian.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerShanCheng.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerTianJinZiDongHua.setOnCheckedChangeListener(ClearMeterMakerOther);
		//IC卡表厂家型号
		ICMeterMakerHuaJie.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerSaiFu.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerQinChuan.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerQinGang.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerZhiLi.setOnCheckedChangeListener(ICClearMeterMakerOther);
	}
	
	/**
	 * 清除填写的灶具信息
	 */
	private OnCheckedChangeListener ClearCookerInfo = 
			new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						CookerGangHuaZiJing.setChecked(false);
						CookerWanHe.setChecked(false);
						CookerWanJiaLe.setChecked(false);
						CookerLinNei.setChecked(false);
						CookerHaiEr.setChecked(false);
						CookerALiSiDun.setChecked(false);
						CookerYinhHua.setChecked(false);
						CookerXiMenZi.setChecked(false);
						CookerHuaDi.setChecked(false);
						CookerOther.setChecked(false);
						CookeTypeTabletSingle.setChecked(false);
						CookerTypeTabletDouble.setChecked(false);
						CookerTypeEmbedDouble.setChecked(false);
						model.CookerBoughtTime.set("");
						CookerPipeNormal.setChecked(false);
						CookerPipeLeakage.setChecked(false);
						CookerPipeFatigue.setChecked(false);
						CookerPipePrecaution.setChecked(false);
						model.CookerPipeClampCount.set("");
						model.CookerPipeLength.set("");
						
						CookerGangHuaZiJing.setEnabled(false);
						CookerWanHe.setEnabled(false);
						CookerWanJiaLe.setEnabled(false);
						CookerLinNei.setEnabled(false);
						CookerHaiEr.setEnabled(false);
						CookerALiSiDun.setEnabled(false);
						CookerYinhHua.setEnabled(false);
						CookerXiMenZi.setEnabled(false);
						CookerHuaDi.setEnabled(false);
						CookerOther.setEnabled(false);
						CookeTypeTabletSingle.setEnabled(false);
						CookerTypeTabletDouble.setEnabled(false);
						CookerTypeEmbedDouble.setEnabled(false);
						model.CookerBoughtTime.set("");
						CookerPipeNormal.setEnabled(false);
						CookerPipeLeakage.setEnabled(false);
						CookerPipeFatigue.setEnabled(false);
						CookerPipePrecaution.setEnabled(false);
						model.CookerPipeClampCount.set("");
						model.CookerPipeLength.set("");
					}
					else
					{
						CookerGangHuaZiJing.setEnabled(true);
						CookerWanHe.setEnabled(true);
						CookerWanJiaLe.setEnabled(true);
						CookerLinNei.setEnabled(true);
						CookerHaiEr.setEnabled(true);
						CookerALiSiDun.setEnabled(true);
						CookerYinhHua.setEnabled(true);
						CookerXiMenZi.setEnabled(true);
						CookerHuaDi.setEnabled(true);
						CookerOther.setEnabled(true);
						CookeTypeTabletSingle.setEnabled(true);
						CookerTypeTabletDouble.setEnabled(true);
						CookerTypeEmbedDouble.setEnabled(true);
						CookerPipeNormal.setEnabled(true);
						CookerPipeLeakage.setEnabled(true);
						CookerPipeFatigue.setEnabled(true);
						CookerPipePrecaution.setEnabled(true);
					}
				}
			};
			
	/**
	 * 清除填写的热水器信息
	 */
	private OnCheckedChangeListener ClearWaterHeaterInfo = 
			new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						HeaterGangHuaZiJing.setChecked(false);
						HeaterWanHe.setChecked(false);
						HeaterWanJiaLe.setChecked(false);
						HeaterLinNei.setChecked(false);
						HeaterHaiEr.setChecked(false);
						HeaterYingHua.setChecked(false);
						HeaterHuaDi.setChecked(false);
						HeaterXiaoSongShu.setChecked(false);
						HeaterShiMiSi.setChecked(false);
						HeaterOther.setChecked(false);
						model.HeaterType.set("");
						model.HeaterBoughtTime.set("");
						HeaterPipeNormal.setChecked(false);
						HeaterPipeLeakage.setChecked(false);
						HeaterPipeFatigue.setChecked(false);
						HeaterPipePrecaution.setChecked(false);
						HeaterPipePlastic.setChecked(false);
						model.HeaterPipeClampCount.set("");
						VentilationBalanced.setChecked(false);
						VentilationForce.setChecked(false);
						VentilationPath.setChecked(false);
						VentilationStraight.setChecked(false);
						HeaterPrecautionNone.setChecked(false);
						HeaterPrecautionStraight.setChecked(false);
						HeaterPrecautionNoVentilation.setChecked(false);
						HeaterPrecautionTrapped.setChecked(false);
						HeaterPrecautionProhibited.setChecked(false);
						HeaterPrecautionInHome.setChecked(false);
						HeaterPrecautionBrokenVent.setChecked(false);
						HeaterPrecautionWrappedVent.setChecked(false);
						
						HeaterGangHuaZiJing.setEnabled(false);
						HeaterWanHe.setEnabled(false);
						HeaterWanJiaLe.setEnabled(false);
						HeaterLinNei.setEnabled(false);
						HeaterHaiEr.setEnabled(false);
						HeaterYingHua.setEnabled(false);
						HeaterHuaDi.setEnabled(false);
						HeaterXiaoSongShu.setEnabled(false);
						HeaterShiMiSi.setEnabled(false);
						HeaterOther.setEnabled(false);
						HeaterPipeNormal.setEnabled(false);
						HeaterPipeLeakage.setEnabled(false);
						HeaterPipeFatigue.setEnabled(false);
						HeaterPipePrecaution.setEnabled(false);
						HeaterPipePlastic.setEnabled(false);
						VentilationBalanced.setEnabled(false);
						VentilationForce.setEnabled(false);
						VentilationPath.setEnabled(false);
						VentilationStraight.setEnabled(false);
						HeaterPrecautionNone.setEnabled(false);
						HeaterPrecautionStraight.setEnabled(false);
						HeaterPrecautionNoVentilation.setEnabled(false);
						HeaterPrecautionTrapped.setEnabled(false);
						HeaterPrecautionProhibited.setEnabled(false);
						HeaterPrecautionInHome.setEnabled(false);
						HeaterPrecautionBrokenVent.setEnabled(false);
						HeaterPrecautionWrappedVent.setEnabled(false);
					}
					else
					{
						HeaterGangHuaZiJing.setEnabled(true);
						HeaterWanHe.setEnabled(true);
						HeaterWanJiaLe.setEnabled(true);
						HeaterLinNei.setEnabled(true);
						HeaterHaiEr.setEnabled(true);
						HeaterYingHua.setEnabled(true);
						HeaterHuaDi.setEnabled(true);
						HeaterXiaoSongShu.setEnabled(true);
						HeaterShiMiSi.setEnabled(true);
						HeaterOther.setEnabled(true);
						HeaterPipeNormal.setEnabled(true);
						HeaterPipeLeakage.setEnabled(true);
						HeaterPipeFatigue.setEnabled(true);
						HeaterPipePrecaution.setEnabled(true);
						HeaterPipePlastic.setEnabled(true);
						VentilationBalanced.setEnabled(true);
						VentilationForce.setEnabled(true);
						VentilationPath.setEnabled(true);
						VentilationStraight.setEnabled(true);
						HeaterPrecautionNone.setEnabled(true);
						HeaterPrecautionStraight.setEnabled(true);
						HeaterPrecautionNoVentilation.setEnabled(true);
						HeaterPrecautionTrapped.setEnabled(true);
						HeaterPrecautionProhibited.setEnabled(true);
						HeaterPrecautionInHome.setEnabled(true);
						HeaterPrecautionBrokenVent.setEnabled(true);
						HeaterPrecautionWrappedVent.setEnabled(true);
					}
				}
			};
			
	/**
	 * 清除填写的壁挂锅炉信息
	 */
	private OnCheckedChangeListener ClearWallBoilerInfo = 
			new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{//TODO
						BoilerGangHuaZiJing.setChecked(false);
						BoilerWanHe.setChecked(false);
						BoilerWanJiaLe.setChecked(false);
						BoilerLinNei.setChecked(false);
						BoilerHaiEr.setChecked(false);
						BoilerYingHua.setChecked(false);
						BoilerHuaDi.setChecked(false);
						BoilerOther.setChecked(false);
						BoilerXiaoSongShu.setChecked(false);
						BoilerShiMiSi.setChecked(false);
						model.BoilerType.set("");
						model.BoilerBoughtTime.set("");
						BoilerPrecautionNormal.setChecked(false);
						BoilerPrecautionInBedRoom.setChecked(false);
						BoilerPrecautionNotified.setChecked(false);
						
						BoilerGangHuaZiJing.setEnabled(false);
						BoilerWanHe.setEnabled(false);
						BoilerWanJiaLe.setEnabled(false);
						BoilerLinNei.setEnabled(false);
						BoilerHaiEr.setEnabled(false);
						BoilerYingHua.setEnabled(false);
						BoilerHuaDi.setEnabled(false);
						BoilerOther.setEnabled(false);
						BoilerXiaoSongShu.setEnabled(false);
						BoilerShiMiSi.setEnabled(false);
						BoilerPrecautionNormal.setEnabled(false);
						BoilerPrecautionInBedRoom.setEnabled(false);
						BoilerPrecautionNotified.setEnabled(false);
					}
					else
					{
						BoilerGangHuaZiJing.setEnabled(true);
						BoilerWanHe.setEnabled(true);
						BoilerWanJiaLe.setEnabled(true);
						BoilerLinNei.setEnabled(true);
						BoilerHaiEr.setEnabled(true);
						BoilerYingHua.setEnabled(true);
						BoilerHuaDi.setEnabled(true);
						BoilerOther.setEnabled(true);
						BoilerXiaoSongShu.setEnabled(true);
						BoilerShiMiSi.setEnabled(true);
						BoilerPrecautionNormal.setEnabled(true);
						BoilerPrecautionInBedRoom.setEnabled(true);
						BoilerPrecautionNotified.setEnabled(true);
					}
				}
			};
	
	/**
	 * 清除基表厂家型号其他输入框
	 */
	private OnCheckedChangeListener  ClearMeterMakerOther =
	new OnCheckedChangeListener() {	
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked)
				model.MeterMakerOther.set("");
		}
	};
	
	/**
	 * 清除IC卡表厂家型号其他输入框
	 */
	private OnCheckedChangeListener  ICClearMeterMakerOther =
	new OnCheckedChangeListener() {	
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked)
				model.ICMeterMakerOther.set("");
		}
	};

	/**
	 * 安检单中的2个已发定期安检告知书状态保持一致 
	 */
	private void LinkNotified()
	{
		BoilerPrecautionNotified.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == PrecautionNotified.isChecked())
					return;
				else
					PrecautionNotified.setChecked(isChecked);
			}
		});
		
		PrecautionNotified.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == BoilerPrecautionNotified.isChecked())
					return;
				else
					BoilerPrecautionNotified.setChecked(isChecked);
			}
		});
	}
	/**
	 * 设置右边布局为禁止使用
	 */
	private void DisableLayouts() {
		MyDigitalClock clock = (MyDigitalClock)findViewById(R.id.digitalClock);
		clock.stopAt(model.DepartureTime.get());
		int[] panes = {R.id.basicPane, R.id.meterPane, R.id.plumPane, R.id.cookerPane,R.id.heaterPane, R.id.precautionPane, R.id.feedbackPane};
		for(int i=0; i<panes.length; i++)
		{
			ViewGroup vg = (ViewGroup)findViewById(panes[i]);
			disable(vg);
		}
	}
	/**
	 * disable every view in the layout recursively
	 * @param layout
	 */
	private void disable(ViewGroup layout) {
		layout.setEnabled(false);
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ViewGroup) {
				disable((ViewGroup) child);
			} else {
				child.setEnabled(false);
			}
		}
	}
	
	/**
	 * 
	 * 20150107sh增加
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null)
			return;
		if (resultCode == 140) {
			this.status = true;
			model.UserName.set(intent.getStringExtra("userName"));
			model.Telephone.set(intent.getStringExtra("telephone"));
			model.ArchiveAddress.set(intent.getStringExtra("address"));
			model.ICCardNo.set(intent.getStringExtra("cardID"));
			model.BuyGasQuantity.set(intent.getStringExtra("tsum"));
			model.UserState.set(intent.getStringExtra("UserState"));
			model.UserID.set(intent.getStringExtra("userID"));
		}
		try
		{
		String result = intent.getStringExtra("result");
		Bitmap bmp;
		if(intent.hasExtra("signature"))
		{
			if(!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".png"))
				return;
			ImageView signPad = ((ImageView)findViewById(R.id.signPad));
			Util.releaseBitmap(signPad);
			bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".png");
			((ImageView)findViewById(R.id.signPad)).setImageBitmap(bmp);
		}
		else
		{
			if(!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".jpg"))
				return;
			bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + result + ".jpg");
			String idx = result.substring(result.length() - 1);
			if (idx.equals("1"))
			{
				Util.releaseBitmap(img1);
				img1.setImageBitmap(bmp);
			}
			else if (idx.equals("2"))
			{
				Util.releaseBitmap(img2);
				img2.setImageBitmap(bmp);
			}
			else if (idx.equals("3"))
			{
				Util.releaseBitmap(img3);
				img3.setImageBitmap(bmp);
			}
			else if (idx.equals("4"))
			{
				Util.releaseBitmap(img4);
				img4.setImageBitmap(bmp);
			}
			else if (idx.equals("5"))
			{
				Util.releaseBitmap(img5);
				img5.setImageBitmap(bmp);
			}
			else if (idx.equals("6"))
			{
				Util.releaseBitmap(img6);
				img6.setImageBitmap(bmp);
			}
			else if (idx.equals("7"))
			{
				Util.releaseBitmap(img7);
				img7.setImageBitmap(bmp);
			}
		}
		}
		catch(Exception e)
		{
			Toast.makeText(this, "获取图片失败。错误： " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * 输入验证
	 * 
	 * @return
	 */
	public boolean validate() {
		// 检查家中无人、拒绝入户选项
		if (model.IsNoAnswer.get() && model.IsEntryDenied.get()) {
			Toast.makeText(this, "家中无人与拒绝入户矛盾", Toast.LENGTH_LONG).show();
			return false;
		}
		// 检查家中无人和到访不遇卡是否矛盾
		if (!model.IsNoAnswer.get() && model.HasNotified.get()) {
			Toast.makeText(this, "家中无人和到访不遇卡矛盾。", Toast.LENGTH_LONG).show();
			return false;
		}
		if(model.IsNoAnswer.get() || model.IsEntryDenied.get())
			return true;
		// 先进行数据校验
		String output = "";
		ValidationResult result = ModelValidator.ValidateModel(model);
		if (!result.isValid()) {
			output = "错误:  \n";
			for (String msg : result.getValidationErrors()) {
				output += msg + "\n";
			}
		}
		if (output.length() > 0) {
			Toast.makeText(this, output, Toast.LENGTH_LONG).show();
			return false;
		} 


		// 燃气表信息
		if(!this.MeterOnTheLeft.isChecked() && !this.MeterOnTheRight.isChecked() )
		{
			Toast.makeText(this, "请选择左右表。", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(this.MeterNormal.isChecked() ||this.MeterWrapped.isChecked() ||this.MeterLeakage.isChecked() ||this.MeterFallThrough.isChecked() ||this.MeterDead.isChecked() ||this.MeterByPass.isChecked() ||this.MeterOther.isChecked()))
		{
			Toast.makeText(this, "请选择燃气表选项。", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!this.MeterTypeG25.isChecked() && !this.MeterTypeG4.isChecked() && !this.MeterTypeG6.isChecked() && !this.MeterTypeG10.isChecked() && !this.MeterTypeG16.isChecked() && !this.MeterTypeg25.isChecked() && !this.MeterTypeG40.isChecked() && !this.MeterTypeOther.isChecked())
		{
			Toast.makeText(this, "请选择表型。", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(this.MeterMakerDanDong.isChecked() ||this.MeterMakerChongJian.isChecked() ||this.MeterMakerSaiFu.isChecked() ||this.MeterMakerChongQian.isChecked() ||this.MeterMakerShanCheng.isChecked() ||this.MeterMakerTianJinZiDongHua.isChecked() ||this.MeterMakerOtherBox.isChecked()))
		{
			Toast.makeText(this, "请选择基表厂家型号。", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(this.ICMeterMakerHuaJie.isChecked() ||this.ICMeterMakerSaiFu.isChecked() ||this.ICMeterMakerQinChuan.isChecked() ||this.ICMeterMakerQinGang.isChecked() ||this.ICMeterMakerZhiLi.isChecked() ||this.ICMeterMakerOtherBox.isChecked()))
		{
			Toast.makeText(this, "请选择IC卡表厂家型号。", Toast.LENGTH_LONG).show();
			return false;			
		}
		
		if(!(this.FeebackSatisfied.isChecked() ||this.FeebackOK.isChecked() ||this.FeebackUnsatisfied.isChecked()))
		{
			Toast.makeText(this, "请选择客户评价。", Toast.LENGTH_LONG).show();
			return false;			
		}
//		if(!(this.PlumPressureNormal.isChecked() ||this.PlumPressureAbnormal.isChecked()))
//		{
//			Toast.makeText(this, "请选择立管静止压力。", Toast.LENGTH_LONG).show();
//			return false;			
//		}
		if ((this.MeterWrapped.isChecked() || this.MeterLeakage.isChecked()
				|| this.MeterFallThrough.isChecked()
				|| this.MeterDead.isChecked() || this.MeterByPass.isChecked() || this.MeterOther
				.isChecked()) && MeterNormal.isChecked()) {
			Toast.makeText(this, "燃气表信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.MeterWrapped.isChecked() || this.MeterLeakage.isChecked()
				|| this.MeterFallThrough.isChecked()
				|| this.MeterDead.isChecked() || this.MeterByPass.isChecked() || this.MeterOther
				.isChecked() || MeterNormal.isChecked()) {
		}
		else
		{
			Toast.makeText(this, "燃气表信息缺失！", Toast.LENGTH_LONG).show();
			return false;
		}


		// 立管
		if ((this.PlumWrapped.isChecked() || this.PlumErosionSevere.isChecked()
				|| this.PlumErosionModerate.isChecked()
				|| this.PlumErosionSlight.isChecked()
				|| this.PlumLeakage.isChecked() || this.PlumOther.isChecked())
				&& PlumNormal.isChecked()) {
			Toast.makeText(this, "立管安检信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.PlumWrapped.isChecked() 
				|| this.PlumErosionSevere.isChecked()
				|| this.PlumErosionModerate.isChecked()
				|| this.PlumErosionSlight.isChecked()
				|| this.PlumLeakage.isChecked() 
				|| this.PlumOther.isChecked()
				|| PlumNormal.isChecked()) {
		}
		else {
			Toast.makeText(this, "立管信息缺失！", Toast.LENGTH_LONG).show();
			return false;
		}

		// 严密性测试
//		if (this.PlumProofLeakage.isChecked()
//				&& this.PlumProofNormal.isChecked()) {
//			Toast.makeText(this, "严密性测试结果矛盾", Toast.LENGTH_LONG).show();
//			return false;
//		}
//		if (this.PlumProofLeakage.isChecked()
//				|| this.PlumProofNormal.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "严密性测试信息缺失！", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// 表前阀,不进行校验
//		if ((this.MeterValveInnerLeakage.isChecked()
//				|| this.MeterValveLeakage.isChecked()
//				|| this.MeterValveBall.isChecked()
//				|| this.MeterValvePlug.isChecked() || this.MeterValveWrapped
//				.isChecked()) && this.MeterValveNormal.isChecked()) {
//			Toast.makeText(this, "表前阀信息矛盾", Toast.LENGTH_LONG).show();
//			return false;
//		}

		if (this.MeterValveInnerLeakage.isChecked()
				|| this.MeterValveLeakage.isChecked()
				|| this.MeterValveBall.isChecked() 
				|| this.MeterValvePlug.isChecked() 
				|| this.MeterValveWrapped.isChecked()
				|| this.MeterValveNormal.isChecked()) {
		}
		else {
			Toast.makeText(this, "表前阀信息缺失！", Toast.LENGTH_LONG).show();
			return false;
		}

		// 灶前阀
		if ((this.CookerValveInnerLeakage.isChecked()
				|| this.CookerValveLeakage.isChecked()
				|| this.CookerValveWrapped.isChecked() || this.CookerValveTooHigh
				.isChecked()) && this.CookerValveNormal.isChecked()) {
			Toast.makeText(this, "灶前阀信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.CookerValveInnerLeakage.isChecked()
				|| this.CookerValveLeakage.isChecked()
				|| this.CookerValveWrapped.isChecked() || this.CookerValveTooHigh
				.isChecked()|| this.CookerValveNormal.isChecked()) {
		}else {
			Toast.makeText(this, "灶前阀信息缺失！", Toast.LENGTH_LONG).show();
			return false;
		}

		// 自闭阀
		if ((this.AutoValveInnerLeakage.isChecked()
				|| this.AutoValveLeakage.isChecked()
				|| this.AutoValveNotWork.isChecked() || this.AutoValveWrapped
				.isChecked()) && this.AutoValveNormal.isChecked()) {
			Toast.makeText(this, "自闭阀信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.AutoValveInnerLeakage.isChecked()
				|| this.AutoValveLeakage.isChecked()
				|| this.AutoValveNotWork.isChecked() || this.AutoValveWrapped
				.isChecked() || this.AutoValveNormal.isChecked()) {
		}
		else {
			Toast.makeText(this, "自闭阀信息缺失！", Toast.LENGTH_LONG).show();
			return false;

		}

		// 户内管
		if ((this.HomePlumLeakage.isChecked()
				|| this.HomePlumThroughSittingRoom.isChecked()
				|| this.HomePlumThroughBedRoom.isChecked()
				|| this.HomePlumNearAppliance.isChecked()
				|| this.HomePlumWrapped.isChecked()
				|| this.HomePlumModified.isChecked() || this.HomePlumOtherUse
				.isChecked()) && this.HomePlumNormal.isChecked()) {
			Toast.makeText(this, "户内管信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.HomePlumLeakage.isChecked()
				|| this.HomePlumThroughSittingRoom.isChecked()
				|| this.HomePlumThroughBedRoom.isChecked()
				|| this.HomePlumNearAppliance.isChecked()
				|| this.HomePlumWrapped.isChecked()
				|| this.HomePlumModified.isChecked() || this.HomePlumOtherUse
				.isChecked() || this.HomePlumNormal.isChecked()) {
		}
		else {
			Toast.makeText(this, "户内管信息缺失！", Toast.LENGTH_LONG).show();
			return false;
		}
		// 灶具软管
		if ((this.CookerPipeLeakage.isChecked()
				|| this.CookerPipeFatigue.isChecked() || this.CookerPipePrecaution
				.isChecked()) && this.CookerPipeNormal.isChecked()) {
			Toast.makeText(this, "灶具软管信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

//		if (this.CookerPipeLeakage.isChecked()
//				|| this.CookerPipeFatigue.isChecked() || this.CookerPipePrecaution
//				.isChecked() || this.CookerPipeNormal.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "灶具软管信息缺失！", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// 热水器软管
//		if ((this.HeaterPipeLeakage.isChecked()
//				|| this.HeaterPipeFatigue.isChecked()
//				|| this.HeaterPipePrecaution.isChecked() || this.HeaterPipePlastic
//				.isChecked()) && this.HeaterPipeNormal.isChecked()) {
//			Toast.makeText(this, "热水器软管信息矛盾", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// 热水器安全隐患
		if ((this.HeaterPrecautionStraight.isChecked()
				|| this.HeaterPrecautionNoVentilation.isChecked()
				|| this.HeaterPrecautionTrapped.isChecked()
				|| this.HeaterPrecautionProhibited.isChecked() || this.HeaterPrecautionInHome
				.isChecked()) && this.HeaterPrecautionNone.isChecked()) {
			Toast.makeText(this, "热水器安全隐患信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

//		if (this.HeaterPrecautionStraight.isChecked()
//				|| this.HeaterPrecautionNoVentilation.isChecked()
//				|| this.HeaterPrecautionTrapped.isChecked()
//				|| this.HeaterPrecautionBrokenVent.isChecked()
//				|| this.HeaterPrecautionProhibited.isChecked() || this.HeaterPrecautionInHome
//				.isChecked() || this.HeaterPrecautionNone.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "热水器安全隐患信息缺失！", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// 壁挂锅炉安全隐患
		if ((this.BoilerPrecautionInBedRoom.isChecked()
				|| this.BoilerPrecautionNotified.isChecked()
				)&& this.BoilerPrecautionNormal.isChecked()) {
			Toast.makeText(this, "壁挂锅炉安全隐患信息矛盾", Toast.LENGTH_LONG).show();
			return false;
		}

//		if (this.BoilerPrecautionInBedRoom.isChecked()
//				|| this.BoilerPrecautionNotified.isChecked()
//				|| this.BoilerPrecautionNormal.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "壁挂锅炉安全隐患信息缺失！", Toast.LENGTH_LONG).show();
//			return false;
//		}
		// 对时间的校对
		@SuppressWarnings("static-access")
		Calendar c = Calendar.getInstance();
		int yearNow = c.get(Calendar.YEAR);

		String yearString = model.MeterMadeYear.get();
		// 燃气表生产年份
		if (yearString != null && !yearString.equals("")) {
			int year = Integer.parseInt(yearString);
			if (year < 1970 || year > yearNow) {
				Toast.makeText(this, "燃气表生产年份有误。", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		// 压力值
		if (model.PlumPressure.get() != null && !model.PlumPressure.get().equals("")) {
			int pressure = Integer.parseInt(model.PlumPressure.get());
			if (pressure < 0 || pressure > 1000) {
				Toast.makeText(this, "压力值有误。", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		// 灶具购置时间
		yearString = model.CookerBoughtTime.get();
		if (yearString != null && !yearString.equals("")) {
			try
			{
			int year = Integer.parseInt(yearString);
			if (year < 1970 || year > yearNow) {
				Toast.makeText(this, "灶具购置年份必须小于等于" + yearNow + "并且大于1970。", Toast.LENGTH_LONG).show();
				return false;
			}
			}
			catch(Exception e)
			{
				Toast.makeText(this, "灶具购置年份必须为数字。", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		// 壁挂锅炉购置时间
		yearString = model.BoilerBoughtTime.get();
		if (yearString != null && !yearString.equals("")) {
			int year = Integer.parseInt(yearString);
			if (year < 1970 || year > yearNow) {
				Toast.makeText(this, "壁挂锅炉购置年份有误。", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		//处理供暖其他选项
		if((model.OtherHeatedType.get()==null || model.OtherHeatedType.get().trim().length()==0) && ((Spinner)findViewById(R.id.HeatedTypeList)).getSelectedItemPosition()==3)
		{
			Toast.makeText(this, "请填写其他供暖方式。", Toast.LENGTH_LONG).show();
			return false;
		}
		//处理基表厂家型号其他选项
		if((model.MeterMakerOther.get() == null || model.MeterMakerOther.get().trim().length()==0) && this.MeterMakerOtherBox.isChecked())
		{
			Toast.makeText(this, "请填写其他基表厂家型号。", Toast.LENGTH_LONG).show();
			return false;
		}
		//处理IC卡表其他厂家型号
		if((model.ICMeterMakerOther.get() == null || model.ICMeterMakerOther.get().trim().length()==0) && this.ICMeterMakerOtherBox.isChecked())
		{
			Toast.makeText(this, "请填写其他IC卡表厂家型号。", Toast.LENGTH_LONG).show();
			return false;
		}
		ArrayList<String> severeList = new ArrayList<String>();
		ArrayList<String> generalList = new ArrayList<String>();
//取消检查维修
//		model.GetPrecautionMap(severeList, generalList);
//		if(!(severeList.size()==0 && generalList.size()==0) && !((CheckBox)findViewById(R.id.IsDispatchRepair)).isChecked())
//		{
//			Toast.makeText(this, "安检存在隐患，请选择维修人。", Toast.LENGTH_LONG).show();
//			return false;
//		}
		
		//TODO 灶具 管卡个数、更换软管米数； 热水器 更换管卡个数、表内剩余气量、基表数、基表号 等待校验
		if (!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_1.jpg"))		
		{
			Toast.makeText(this, "请给燃气表拍照。", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_2.jpg"))		
		{
			Toast.makeText(this, "请给阀门拍照。", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// 本地保存安检记录
	public boolean Save(String objStr, String inspectionTable, String precautionTable, boolean isTemp) {
		try {
			boolean isEntryInspection;
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);
			JSONObject row = new JSONObject(objStr);
			String uuid = row.getString("ID");
			String paperId = row.getString("CHECKPAPER_ID");
			isEntryInspection = row.getString("CONDITION").equals("正常");
			// 字表中存的冗余列
			Map<String, String> redundantCols = new HashMap<String, String>();
			if(isEntryInspection)
			{
				redundantCols.put("CARD_ID", row.getString("CARD_ID"));
				redundantCols.put("USER_NAME", row.getString("USER_NAME"));
				redundantCols.put("ROAD", row.getString("ROAD"));
				redundantCols.put("UNIT_NAME", row.getString("UNIT_NAME"));
				redundantCols.put("CUS_DOM", row.getString("CUS_DOM"));
				redundantCols.put("CUS_DY", row.getString("CUS_DY"));
				redundantCols.put("CUS_FLOOR", row.getString("CUS_FLOOR"));
				redundantCols.put("CUS_ROOM", row.getString("CUS_ROOM"));
				redundantCols.put("TELPHONE", row.getString("TELPHONE"));
				redundantCols.put("SAVE_PEOPLE", row.getString("SAVE_PEOPLE"));
				redundantCols.put("SAVE_DATE", row.getString("DEPARTURE_TIME"));
				redundantCols.put("IC_METER_NAME", row.has("IC_METER_NAME") ? row.getString("IC_METER_NAME"):"");
				redundantCols.put("JB_METER_NAME", row.has("JB_METER_NAME")? row.getString("JB_METER_NAME"):"");
				redundantCols.put("JB_NUMBER", row.getString("JB_NUMBER"));
				redundantCols.put("SURPLUS_GAS", row.getString("SURPLUS_GAS"));
			}

			// 删除隐患
			db.execSQL(
					"delete from " + precautionTable + " where id in (select id from " + inspectionTable  + " where CHECKPAPER_ID=?)",
					new Object[] { paperId });
			// 删安检除单
			db.execSQL("DELETE FROM " + inspectionTable + "  where CHECKPAPER_ID='" + paperId
					+ "'");
			String sql1 = "INSERT INTO " + inspectionTable + " (ID";
			String sql2 = ") VALUES('" + uuid + "'";
			// 添加主记录
			Map<String, String> masterMap = new HashMap<String, String>();
			Map<String, String> slaveMap = new HashMap<String, String>();
			Iterator<String> itr = row.keys();
			while (itr.hasNext()) {
				String key = itr.next();
				if (key.equals("ID") ||key.equals("PRECAUTION_NOTIFIED"))
					continue;
				if (key.matches(".*_\\d{1,2}$"))
					slaveMap.put(key, row.getString(key));
				else {
					sql1 += "," + key;
					if (key.equals("JB_NUMBER") || key.equals("SURPLUS_GAS"))
						sql2 += ","
								+ (row.getString(key).length() > 0 ? row
										.getString(key) : "NULL");
					else
						sql2 += ",'" + row.getString(key).replace("'", "''")
						+ "'";
				}
			}
			sql1 += sql2 + ")";
			db.execSQL(sql1);
			// 添加子记录
			if(isEntryInspection)
				InsertPrecaution(uuid, redundantCols, slaveMap, db, precautionTable);
			db.close();
			if(!isTemp)
			{
				//更新安检状态		
				String state = row.getString("CONDITION");
				boolean needsRepair = false;
				if(row.has("NEEDS_REPAIR"))
					 needsRepair = row.getString("NEEDS_REPAIR").equals("是");
				SetInspectionState(paperId, state, needsRepair);
			}
			return true;

		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
			return false;
		}
	}

	/**
	 * 更新安检状态
	 * @param paperId
	 * @param state
	 * @param needsRepair 
	 */
	private void SetInspectionState(String paperId, String state, boolean needsRepair) {

		if(state.equals("无人"))
		{
			Util.SetBit(this, Vault.NOANSWER_FLAG, paperId);
		}
		else if(state.equals("拒绝"))
		{
			Util.SetBit(this, Vault.DENIED_FLAG , paperId);
		}
		else
		{
			Util.SetBit(this, Vault.INSPECT_FLAG, paperId);
			if(needsRepair)
				Util.SetBit(this, Vault.REPAIR_FLAG, paperId);
			else
				Util.ClearBit(this, Vault.REPAIR_FLAG, paperId);
		}
	}

	/**
	 * 插入隐患数据
	 * 
	 * @param uuid
	 * @param redundantCols
	 * @param slaveMap
	 * @param db
	 */
	private void InsertPrecaution(String uuid,
			Map<String, String> redundantCols, Map<String, String> slaveMap,
			SQLiteDatabase db, String precautionTable) {
		String snippet1 = "";
		String snippet2 = "";
		for (Map.Entry<String, String> entry : redundantCols.entrySet()) {
			snippet1 += "," + entry.getKey();
			if (entry.getKey().equals("JB_NUMBER")
					|| entry.getKey().equals("SURPLUS_GAS"))
				snippet2 += ","
						+ (entry.getValue().length() > 0 ? entry.getValue()
								: "NULL") + "";
			else
				snippet2 += ",'" + entry.getValue().replace("'", "''") + "'";
		}
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			int offset = 2;
			if (entry.getKey().charAt(entry.getKey().length() - 3) == '_')
				offset = 3;
			String sql = "INSERT INTO " + precautionTable + " (ID"
					+ snippet1
					+ ",EQUIPMENT,CONTENT"
					+ ") VALUES('"
					+ uuid
					+ "'"
					+ snippet2
					+ ",'"
					+ entry.getKey().substring(0,
							entry.getKey().length() - offset) + "','"
							+ entry.getValue().replace("'", "''") + "')";
			db.execSQL(sql);
		}
	}

	/**
	 * 从页面收集各个字段的值
	 */
	public String SaveToJSONString(boolean saveRepair, boolean upload) {
		JSONObject row = new JSONObject();
		try {
			// 用户编号
			row.put("f_userid", model.UserID.get());
			// uuid
			row.put("ID", uuid);
			// 安检单ID
			row.put("CHECKPAPER_ID", this.paperId);
			//安检ID
			row.put("CHECKPLAN_ID", this.planId);
			//安检人
			row.put("SAVE_PEOPLE", Util.getSharedPreference(this, Vault.CHECKER_NAME));

			if (model.IsNoAnswer.get())
				// 家中无人
				row.put("CONDITION", "无人");
			if (model.IsEntryDenied.get())
				// 拒绝入户
				row.put("CONDITION", "拒绝");
			// 已发到访不遇卡
			if (model.HasNotified.get())
				row.put("HasNotified", "已发");
			else
				row.put("HasNotified", "");
			//不是上传
			if(!upload)
			{
				// 到达时间
				row.put("ARRIVAL_TIME", model.InspectionDate.get() + " "
						+ model.ArrivalTime.get());
				// 离开时间
				String tm = ((MyDigitalClock)this.findViewById(R.id.digitalClock)).getText().toString();
				row.put("DEPARTURE_TIME",
						model.InspectionDate.get() + " " + tm);
			}
			else
			{
				// 到达时间为进入界面时间
				row.put("ARRIVAL_TIME", Util.FormatDate("yyyy-MM-dd HH:mm:ss", entryDateTime.getTime()));
				//离开时间为当前时间
				row.put("DEPARTURE_TIME", Util.FormatDate("yyyy-MM-dd HH:mm:ss", new Date().getTime()));
			}
			// 小区名称
			row.put("UNIT_NAME", model.ResidentialAreaName.get());
			// 小区地址
			row.put("ROAD", model.ResidentialAreaAddress.get());
			// 楼号
			row.put("CUS_DOM", model.BuildingNo.get());
			// 单元
			row.put("CUS_DY", model.UnitNo.get());
			// 楼层
			row.put("CUS_FLOOR", model.LevelNo.get());
			// 房号
			row.put("CUS_ROOM", model.RoomNo.get());
			if (!row.has("CONDITION"))
				// 检查情况
				row.put("CONDITION", "正常");
			else
			{
				return row.toString();
			}
			
			// 用户姓名
			row.put("USER_NAME", model.UserName.get());
			// IC卡号
			row.put("CARD_ID", model.ICCardNo.get());
			// 电话号码
			row.put("TELPHONE", model.Telephone.get());
			//签名人电话
			row.put("SIGNTELEPHONE", model.SignTelephone.get());
			// 用户档案地址
			row.put("OLD_ADDRESS", model.ArchiveAddress.get());
			// 房屋结构
			Spinner spinner1 = (Spinner) this
					.findViewById(R.id.StructureTypeList);
			row.put("ROOM_STRUCTURE", spinner1.getSelectedItem().toString());
			// 供暖方式
			if (model.OtherHeatedType.get().trim().equals("")) {
				Spinner spinner2 = (Spinner) this
						.findViewById(R.id.HeatedTypeList);
				row.put("WARM", spinner2.getSelectedItem().toString());
			} else
				row.put("WARM", model.OtherHeatedType.get());
			// 基表数
			row.put("JB_NUMBER", model.BaseMeterQuantity.get());
			// 剩余气量
			row.put("SURPLUS_GAS", model.RemainGasQuantity.get());
			//用气量
			row.put("gas_quantity", model.GasQuantity.get());
			//累计购气量
			row.put("buy_gas_quantity", model.BuyGasQuantity.get());
			// 燃气表左右表
			if (this.MeterOnTheLeft.isChecked())
				row.put("RQB_AROUND", "左表");
			else
				row.put("RQB_AROUND", "右表");
			// 燃气表基表号
			row.put("RQB_JBCODE", model.BaseMeterID.get());
			// 燃气表生产年份
			row.put("METERMADEYEAR", model.MeterMadeYear.get());
			// 燃气表
			if (this.MeterNormal.isChecked())
				row.put("RQB", "正常");
			else 
				row.put("RQB", "不正常");
			// 包裹
			if (this.MeterWrapped.isChecked())
				row.put("燃气表_1", "包裹");
			// 漏气
			if (this.MeterLeakage.isChecked())
				row.put("燃气表_2", "漏气");
			// 长通表
			if (this.MeterFallThrough.isChecked())
				row.put("燃气表_3", "长通表");
			// 死表
			if (this.MeterDead.isChecked())
				row.put("燃气表_4", "死表");
			// 表不过气
			if (this.MeterByPass.isChecked())
				row.put("燃气表_5", "表不过气");
			// 其他
			if (this.MeterOther.isChecked())
				row.put("燃气表_6", "其他");
			// 基表厂家型号
			if (model.MeterMakerOther.get().trim().equals("")) {
				if (MeterMakerDanDong.isChecked())
					row.put("JB_METER_NAME", "丹东");
				else if (MeterMakerChongJian.isChecked())
					row.put("JB_METER_NAME", "重检");
				else if (MeterMakerSaiFu.isChecked())
					row.put("JB_METER_NAME", "赛福");
				else if (MeterMakerChongQian.isChecked())
					row.put("JB_METER_NAME", "重前");
				else if (MeterMakerShanCheng.isChecked())
					row.put("JB_METER_NAME", "山城");
				else if (MeterMakerTianJinZiDongHua.isChecked())
					row.put("JB_METER_NAME", "天津自动化");
			} else
				row.put("JB_METER_NAME", model.MeterMakerOther.get());
			// IC卡表厂家基表型号

			if (model.ICMeterMakerOther.get().trim().equals("")) {
				if (ICMeterMakerHuaJie.isChecked())
					row.put("IC_METER_NAME", "金卡");
				else if (ICMeterMakerSaiFu.isChecked())
					row.put("IC_METER_NAME", "先锋");
//				else if (ICMeterMakerQinChuan.isChecked())
//					row.put("IC_METER_NAME", "秦川");
//				else if (ICMeterMakerQinGang.isChecked())
//					row.put("IC_METER_NAME", "秦港");
//				else if (ICMeterMakerZhiLi.isChecked())
//					row.put("IC_METER_NAME", "致力");
			} else
				row.put("IC_METER_NAME", model.ICMeterMakerOther.get());
			// 表型
			if (this.MeterTypeG25.isChecked())
				row.put("METER_TYPE", "G2.5");
			else if (this.MeterTypeG4.isChecked())
				row.put("METER_TYPE", "G4");
			else if (this.MeterTypeG6.isChecked())
				row.put("METER_TYPE", "G6");
			else if (this.MeterTypeG10.isChecked())
				row.put("METER_TYPE", "G10");
			else if (this.MeterTypeG16.isChecked())
				row.put("METER_TYPE", "G16");
			else if (this.MeterTypeg25.isChecked())
				row.put("METER_TYPE", "G25");
			else if (this.MeterTypeG40.isChecked())
				row.put("METER_TYPE", "G40");
			else if (this.MeterTypeOther.isChecked())
				row.put("METER_TYPE", "流量计");
			// 立管
			if (this.PlumNormal.isChecked())
				row.put("STANDPIPE", "正常");
			else 
				row.put("STANDPIPE", "不正常");
			// 包裹
			if (this.PlumWrapped.isChecked())
				row.put("立管_1", "包裹");
			// 漏气
			if (this.PlumErosionSevere.isChecked())
				row.put("立管_2", "严重");
			// 长通表
			if (this.PlumErosionModerate.isChecked())
				row.put("立管_3", "中度");
			// 死表
			if (this.PlumErosionSlight.isChecked())
				row.put("立管_4", "轻微");
			// 表不过气
			if (this.PlumLeakage.isChecked())
				row.put("立管_5", "漏气");
			// 其他
			if (this.PlumOther.isChecked())
				row.put("立管_6", "其他");
			// 严密性测试
			if (this.PlumProofNormal.isChecked())
				row.put("RIGIDITY", "正常");
			else
				row.put("RIGIDITY", "不正常");

			if (this.PlumProofLeakage.isChecked())
				row.put("RIGIDITY", "漏气");
			// 静止压力
			if (this.PlumPressureNormal.isChecked())
				row.put("STATIC ", "合格");
			else if (this.PlumPressureAbnormal.isChecked())
				row.put("STATIC", "不合格");
			else 
				row.put("STATIC", "");
			row.put("STATIC_DATA", model.PlumPressure.get());
			// 阀门
			// 表前阀
			if (this.MeterValveNormal.isChecked())
				row.put("TABLE_TAP", "正常");
			else
				row.put("TABLE_TAP", "不正常");

			if (this.MeterValveInnerLeakage.isChecked())
				row.put("阀门表前阀_1", "内漏");
			if (this.MeterValveLeakage.isChecked())
				row.put("阀门表前阀_2", "漏气");
			if (this.MeterValveBall.isChecked())
				row.put("阀门表前阀_3", "球阀");
			if (this.MeterValvePlug.isChecked())
				row.put("阀门表前阀_4", "旋塞");
			if (this.MeterValveWrapped.isChecked())
				row.put("阀门表前阀_5", "包裹");

			// 灶前阀
			if (this.CookerValveNormal.isChecked())
				row.put("COOK_TAP", "正常");
			else
				row.put("COOK_TAP", "不正常");

			if (this.CookerValveInnerLeakage.isChecked())
				row.put("阀门灶前阀_1", "内漏");
			if (this.CookerValveLeakage.isChecked())
				row.put("阀门灶前阀_2", "漏气");
			if (this.CookerValveWrapped.isChecked())
				row.put("阀门灶前阀_3", "包裹");
			if (this.CookerValveTooHigh.isChecked())
				row.put("阀门灶前阀_4", "安装过高");
			// 自闭阀
			if (this.AutoValveNormal.isChecked())
				row.put("CLOSE_TAP", "正常");
			else
				row.put("CLOSE_TAP", "不正常");

			if (this.AutoValveInnerLeakage.isChecked())
				row.put("阀门自闭阀_1", "内漏");
			if (this.AutoValveLeakage.isChecked())
				row.put("阀门自闭阀_2", "漏气");
			if (this.AutoValveNotWork.isChecked())
				row.put("阀门自闭阀_3", "失灵");
			if (this.AutoValveWrapped.isChecked())
				row.put("阀门自闭阀_4", "包裹");
			// 户内管
			if (this.HomePlumNormal.isChecked())
				row.put("INDOOR", "正常");
			else
				row.put("INDOOR", "不正常");

			if (this.HomePlumLeakage.isChecked())
				row.put("户内管_1", "漏气");
			if (this.HomePlumThroughSittingRoom.isChecked())
				row.put("户内管_2", "穿客厅");
			if (this.HomePlumThroughBedRoom.isChecked())
				row.put("户内管_3", "穿卧室");
			if (this.HomePlumNearAppliance.isChecked())
				row.put("户内管_4", "与电器过近");
			if (this.HomePlumWrapped.isChecked())
				row.put("户内管_5", "包裹");
			if (this.HomePlumModified.isChecked())
				row.put("户内管_6", "私改");
			if (this.HomePlumOtherUse.isChecked())
				row.put("户内管_7", "厨房他用");

			// 漏气
			if (this.LeakageCooker.isChecked())
				row.put("LEAKAGE_COOKER", "灶具漏气");
			if (this.LeakageHeater.isChecked())
				row.put("LEAKAGE_HEATER", "热水器漏气");
			if (this.LeakageBoiler.isChecked())
				row.put("LEAKAGE_BOILER", "壁挂炉漏气");
			if (this.LeakageNotified.isChecked())
				row.put("LEAKAGE_NOTIFIED", "安检告知");

			// 漏气位置
			row.put("LEAKGEPLACE", model.LeakagePlace.get());

			// 灶具
			row.put("COOK_BRAND", "");
			if (this.CookerGangHuaZiJing.isChecked())
				row.put("COOK_BRAND", "老板");
			else if (this.CookerWanHe.isChecked())
				row.put("COOK_BRAND", "万和");
			else if (this.CookerWanJiaLe.isChecked())
				row.put("COOK_BRAND", "万家乐");
			else if (this.CookerLinNei.isChecked())
				row.put("COOK_BRAND", "方太");
			else if (this.CookerHaiEr.isChecked())
				row.put("COOK_BRAND", "海尔");
			else if (this.CookerALiSiDun.isChecked())
				row.put("COOK_BRAND", "帅康");
			else if (this.CookerYinhHua.isChecked())
				row.put("COOK_BRAND", "樱花");
			else if (this.CookerHuaDi.isChecked())
				row.put("COOK_BRAND", "华帝");
			else if (this.CookerOther.isChecked())
				row.put("COOK_BRAND", "其他");
			else if (this.CookerXiMenZi.isChecked())
				row.put("COOK_BRAND", "西门子");
			// 灶具型号
			row.put("COOK_TYPE", "");
			if (this.CookeTypeTabletSingle.isChecked())
				row.put("COOK_TYPE", "台式单眼");
			else if (this.CookerTypeTabletDouble.isChecked())
				row.put("COOK_TYPE", "台式双眼");
			else if (this.CookerTypeEmbedDouble.isChecked())
				row.put("COOK_TYPE", "镶嵌双眼");
			// 购置时间
			row.put("COOK_DATE ", model.CookerBoughtTime.get());
			// 软管
			row.put("COOKPIPE_NORMAL", "");
			if (this.CookerPipeNormal.isChecked())
				row.put("COOKPIPE_NORMAL", "正常");
			else
				row.put("COOKPIPE_NORMAL", "不正常");

			if (this.CookerPipeLeakage.isChecked())
				row.put("灶具软管_2", "漏气");
			if (this.CookerPipeFatigue.isChecked())
				row.put("灶具软管_3", "老化");
			if (this.CookerPipePrecaution.isChecked())
				row.put("灶具软管_4", "有安全隐患");
			if (this.HeaterPipePlastic.isChecked())
				row.put("灶具软管_5", "铝塑管");

			row.put("COOKERPIPECLAMPCOUNT", model.CookerPipeClampCount.get());
			row.put("COOKERPIPYLENGTH", model.CookerPipeLength.get());

			// 热水器
			row.put("WATER_BRAND", "");
			if (this.HeaterGangHuaZiJing.isChecked())
				row.put("WATER_BRAND", "美的");
			else if (this.HeaterWanHe.isChecked())
				row.put("WATER_BRAND", "万和");
			else if (this.HeaterWanJiaLe.isChecked())
				row.put("WATER_BRAND", "万家乐");
			else if (this.HeaterLinNei.isChecked())
				row.put("WATER_BRAND", "林内");
			else if (this.HeaterHaiEr.isChecked())
				row.put("WATER_BRAND", "海尔");
//			else if (this.HeaterALiSiDun.isChecked())
//				row.put("WATER_BRAND", "阿里斯顿");
			else if (this.HeaterYingHua.isChecked())
				row.put("WATER_BRAND", "樱花");
			else if (this.HeaterHuaDi.isChecked())
				row.put("WATER_BRAND", "华帝");
			else if (this.HeaterOther.isChecked())
				row.put("WATER_BRAND", "其他");
			else if (this.HeaterShiMiSi.isChecked())
				row.put("WATER_BRAND", "史密斯");
			else if (this.HeaterXiaoSongShu.isChecked())
				row.put("WATER_BRAND", "小松鼠");
			if (model.HeaterType.get().trim().length() > 0)
				row.put("WATER_TYPE", model.HeaterType.get().trim());
			// 购置时间
			row.put("WATER_DATE", model.HeaterBoughtTime.get());
			// 热水器软管
			row.put("WATER_PIPE", "");
			if (this.HeaterPipeNormal.isChecked())
				row.put("WATER_PIPE", "正常");
			else
				row.put("WATER_PIPE", "不正常");
			if (this.HeaterPipeLeakage.isChecked())
				row.put("热水器软管_2", "漏气");
			if (this.HeaterPipeFatigue.isChecked())
				row.put("热水器软管_3", "老化");
			if (this.HeaterPipePrecaution.isChecked())
				row.put("热水器软管_4", "有安全隐患");
			if (this.HeaterPipePlastic.isChecked())
				row.put("热水器软管_5", "铝塑管");
			// 更换管卡
			row.put("WATER_NUME", model.HeaterPipeClampCount.get());
			// 烟道
			row.put("WATER_FLUE", "");
			if (this.VentilationBalanced.isChecked())
				row.put("WATER_FLUE", "平衡");
			else if (this.VentilationForce.isChecked())
				row.put("WATER_FLUE", "强排");
			else if (this.VentilationPath.isChecked())
				row.put("WATER_FLUE", "烟道");
			else if (this.VentilationStraight.isChecked())
				row.put("WATER_FLUE", "直排");
			// 安全隐患
			row.put("WATER_HIDDEN", "");
			if (this.HeaterPrecautionNone.isChecked())
				row.put("WATER_HIDDEN", "正常");
			else
				row.put("WATER_HIDDEN", "不正常");

			if (this.HeaterPrecautionStraight.isChecked())
				row.put("热水器安全隐患_1", "直排热水器");
			if (this.HeaterPrecautionNoVentilation.isChecked())
				row.put("热水器安全隐患_2", "未安装烟道");
			if (this.HeaterPrecautionTrapped.isChecked())
				row.put("热水器安全隐患_3", "烟道未排到室外");
			if (this.HeaterPrecautionProhibited.isChecked())
				row.put("热水器安全隐患_4", "严禁使用");
			if (this.HeaterPrecautionInHome.isChecked())
				row.put("热水器安全隐患_5", "安装在卧室");
			if (this.HeaterPrecautionBrokenVent.isChecked())
				row.put("热水器安全隐患_6", "烟道破损");
			if (this.HeaterPrecautionWrappedVent.isChecked())
				row.put("热水器安全隐患_7", "包裹烟道");

			// 壁挂锅炉
			row.put("WHE_BRAND", "");
			if (this.BoilerGangHuaZiJing.isChecked())
				row.put("WHE_BRAND", "美的");
			if (this.BoilerWanHe.isChecked())
				row.put("WHE_BRAND", "万和");
			if (this.BoilerWanJiaLe.isChecked())
				row.put("WHE_BRAND", "万家乐");
			if (this.BoilerLinNei.isChecked())
				row.put("WHE_BRAND", "林内");
			if (this.BoilerHaiEr.isChecked())
				row.put("WHE_BRAND", "海尔");
			if (this.BoilerXiaoSongShu.isChecked())
				row.put("WHE_BRAND", "小松鼠");
			if (this.BoilerYingHua.isChecked())
				row.put("WHE_BRAND", "樱花");
			if (this.BoilerHuaDi.isChecked())
				row.put("WHE_BRAND", "华帝");
			if (this.BoilerShiMiSi.isChecked())
				row.put("WHE_BRAND", "史密斯");
			if (this.BoilerOther.isChecked())
				row.put("WHE_BRAND", "其他");
			// 型号没有
			if (model.BoilerType.get().trim().length() > 0)
				row.put("WHE_TYPE", model.BoilerType.get().trim());
			// 购置时间
			row.put("WHE_DATE", model.BoilerBoughtTime.get());

			// 壁挂锅炉安全隐患
			row.put("WHE_HIDDEN", "");
			if (this.BoilerPrecautionNormal.isChecked())
				row.put("WHE_HIDDEN", "正常");
			else
				row.put("WHE_HIDDEN", "不正常");
			if (this.BoilerPrecautionInBedRoom.isChecked())
				row.put("壁挂锅炉安全隐患_1", "壁挂炉安装在卧室");
			if (this.BoilerPrecautionNotified.isChecked())
				row.put("壁挂锅炉安全隐患_2", "已发定期安检告知书");

			// 安全隐患
			if (this.PrecautionInBathRoom.isChecked())
				row.put("安全隐患_1", "燃气设施安装在卧室/卫生间");
			if (this.PrecautionInBedRoom.isChecked())
				row.put("安全隐患_2", "燃气设备安装在卧室");
			if (this.PrecautionLongPipe.isChecked())
				row.put("安全隐患_3", "软管过长");
			if (this.PrecautionElectricWire.isChecked())
				row.put("安全隐患_4", "燃气管挂物接触电源线");
			if (this.PrecautionThroughFurniture.isChecked())
				row.put("安全隐患_5", "软管穿柜/门窗/顶棚");
			if (this.PrecautionThroughWall.isChecked())
				row.put("安全隐患_6", "软管穿墙/地面");
			if (this.PrecautionValidPipe.isChecked())
				row.put("安全隐患_7", "使用非天然气专用软管");
			if (this.PrecautionWithConnector.isChecked())
				row.put("安全隐患_8", "软管上有接头");
			if (this.PrecautionNearFire.isChecked())
				row.put("安全隐患_9", "软管离火源太近");
			if (this.PrecautionWrapped.isChecked())
				row.put("安全隐患_10", "软管包裹");
			if (this.PrecautionNotified.isChecked())
				row.put("安全隐患_11", "已发近期安检报告书");
			if (this.PrecautionNoClamp.isChecked())
				row.put("安全隐患_12", "无管卡");
			if (this.PrecautionMalPosition.isChecked())
				row.put("安全隐患_13", "燃气具安装，摆放位置不规范");
			if (this.PrecautionNearLiquefiedGas.isChecked())
				row.put("安全隐患_14", "与液化气共处一室");
			if (this.PrecautionUnsafeDevice.isChecked())
				row.put("安全隐患_15", "使用非安全燃气具");
			if (this.PrecautionLoosePipe.isChecked())
				row.put("安全隐患_16", "软管连接没有固定");
			if (this.PrecautionPipeInDark.isChecked())
				row.put("安全隐患_17", "软管暗敷");
			if (this.PrecautionPipeOutside.isChecked())
				row.put("安全隐患_18", "连接软管在户外");
			if (this.PrecautionThreeWay.isChecked())
				row.put("安全隐患_19", "软管接三通");
			// 用户意见
			if (model.UserSuggestion.get().trim().length() > 0)
				row.put("USER_SUGGESTION", model.UserSuggestion.get().trim());
			row.put("Remark", model.Remark.get().trim());
			// 用户评价
			if (this.FeebackSatisfied.isChecked())
				row.put("USER_SATISFIED", "满意");
			else if (this.FeebackOK.isChecked())
				row.put("USER_SATISFIED", "基本满意");
			else if (this.FeebackUnsatisfied.isChecked())
				row.put("USER_SATISFIED", "不满意");
			// 签名
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_sign.png"))
				row.put("USER_SIGN", uuid + "_sign");
			// 图片
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_1.jpg"))
				row.put("PHOTO_FIRST", uuid + "_1");
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_2.jpg"))
				row.put("PHOTO_SECOND", uuid + "_2");
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_3.jpg"))
				row.put("PHOTO_THIRD", uuid + "_3");
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_4.jpg"))
				row.put("PHOTO_FOUTH", uuid + "_4");
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_5.jpg"))
				row.put("PHOTO_FIFTH", uuid + "_5");
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_6.jpg"))
				row.put("PHOTO_SIXTH", uuid + "_6");
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_7.jpg"))
				row.put("PHOTO_SEVENTH", uuid + "_7");
			//维修相关
			if(this.IsDispatchRepair.isChecked() && saveRepair)
			{
				row.put("NEEDS_REPAIR", "是");
				Spinner spinner = ((Spinner)findViewById(R.id.RepairManList));
				RepairMan repairMan = (RepairMan)model.RepairManList.get((int)spinner.getSelectedItemId());
				row.put("REPAIRMAN",repairMan.name);
				row.put("REPAIRMAN_ID", repairMan.id);
				row.put("REPAIR_STATE", "未维修" );
			}
			else
			{
				row.put("NEEDS_REPAIR", "否");
				row.put("REPAIRMAN",null);
				row.put("REPAIRMAN_ID", null);
				row.put("REPAIR_STATE", null );
			}
			return row.toString();
		} catch (JSONException e) {
			Log.d("IndoorInsppectoon", e.getMessage());
			return null;
		}
	}

	/**
	 * 从本地数据库读取各个字段并给字段赋值
	 */
	private void ReadFromDB(String id,  String inspectionTable, String precautionTable ) {
		// 读取该条安检数据，给界面各字段赋值
		// 打开数据库
		try {

			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);

			Cursor c = db.rawQuery(
					"SELECT * FROM " + inspectionTable  + " where id=?",
					new String[] { id });
			while (c.moveToNext()) {
				// 已发到访不遇卡
				if (c.getString(c.getColumnIndex("HasNotified")).length() > 0)
					model.HasNotified.set(true);

				// 到达时间
				String dt = c.getString(c.getColumnIndex("ARRIVAL_TIME"));
				model.InspectionDate.set(dt.substring(0, 10));
				model.ArrivalTime.set(dt.substring(dt.length()-8, dt.length()));
				// 离开时间
				dt = c.getString(c.getColumnIndex("DEPARTURE_TIME"));
				String stopAt = dt.substring(dt.length()-8, dt.length());
				model.DepartureTime.set(stopAt);
				//进入界面而不是解屏时
				if(inspectionTable.equals("T_INSPECTION"))
				{
					((MyDigitalClock)this.findViewById(R.id.digitalClock)).stopAt(stopAt);
				}
				// 小区地址
				model.ResidentialAreaAddress.set(c.getString(c
						.getColumnIndex("ROAD")));
				// 楼号
				model.BuildingNo.set(c.getString(c.getColumnIndex("CUS_DOM")));
				// 单元
				model.UnitNo.set(c.getString(c.getColumnIndex("CUS_DY")));
				// 楼层
				model.LevelNo.set(c.getString(c.getColumnIndex("CUS_FLOOR")));
				// 房号
				model.RoomNo.set(c.getString(c.getColumnIndex("CUS_ROOM")));
				// 检查情况
				if (c.getString(c.getColumnIndex("CONDITION")).equals("无人")) {
					model.IsNoAnswer.set(true);
					if(inspectionTable.equals("T_INSPECTION"))
					{
						db.close();
						return;
					}
				} else if (c.getString(c.getColumnIndex("CONDITION")).equals(
						"拒绝")) {
					model.IsEntryDenied.set(true);
					if(inspectionTable.equals("T_INSPECTION"))
					{
						db.close();
						return;
					}
				}
//				else
//				{
//					if(inspectionTable.equals("T_INSPECTION"))
//						DisableOtherCondition();
//				}
				if(!status)
				{
					// 户主姓名
					//20150108sh注释1
					model.UserName.set(c.getString(c.getColumnIndex("USER_NAME")));
					// 电话
					//20150108sh注释2
					model.Telephone.set(c.getString(c.getColumnIndex("TELPHONE")));
					//签名人电话
					model.SignTelephone.set(c.getString(c.getColumnIndex("SIGNTELEPHONE")));
					// 小区名称
					model.ResidentialAreaName.set(c.getString(c
							.getColumnIndex("UNIT_NAME")));
					// 用户档案地址
					//20150108sh注释3
					model.ArchiveAddress.set(c.getString(c.getColumnIndex("OLD_ADDRESS")));
					// IC卡号
					//20150108sh注释
					model.ICCardNo.set(c.getString(c.getColumnIndex("CARD_ID")));
					//累计购气量
					if(c.getString(c.getColumnIndex("buy_gas_quantity")) != null )
						model.BuyGasQuantity.set(c.getString(c.getColumnIndex("buy_gas_quantity")));
					else
						model.BuyGasQuantity.set("");

					this.status = false;
				}
				
				// 房屋结构
				String roomStruct = c.getString(c.getColumnIndex("ROOM_STRUCTURE"));
				if(roomStruct == null)
					roomStruct = "";
				Spinner spinnerStructureTypeList = (Spinner) this
						.findViewById(R.id.StructureTypeList);
				if (roomStruct.equals("平房"))
					spinnerStructureTypeList.setSelection(4);
				else if (roomStruct.equals("多层"))
					spinnerStructureTypeList.setSelection(1);
				else if (roomStruct.equals("小高层"))
					spinnerStructureTypeList.setSelection(2);
				else if (roomStruct.equals("高层"))
					spinnerStructureTypeList.setSelection(0);
				else if (roomStruct.equals("别墅"))
					spinnerStructureTypeList.setSelection(3);

				// 供暖方式
				String warm = c.getString(c.getColumnIndex("WARM"));
				if(warm == null)
					warm = "";
				Spinner spinnerHeatedTypeList = (Spinner) this
						.findViewById(R.id.HeatedTypeList);
				if (warm.equals("热力公司集中供暖"))
					spinnerHeatedTypeList.setSelection(0);
				else if (warm.equals("小区集中供暖"))
					spinnerHeatedTypeList.setSelection(1);
				else if (warm.equals("客户自行供暖"))
					spinnerHeatedTypeList.setSelection(2);
				else if (warm.equals("其他供暖")) {
					spinnerHeatedTypeList.setSelection(3);
				} else {
					spinnerHeatedTypeList.setSelection(3);
					model.OtherHeatedType.set(warm);
				}

				// 基表厂家型号
				String jb_menter_name = c.getString(c
						.getColumnIndex("JB_METER_NAME"));
				if(jb_menter_name == null)
					jb_menter_name = "";
				if (jb_menter_name.equals("丹东")) {
					this.MeterMakerDanDong.setChecked(true);
				} else if (jb_menter_name.equals("重检")) {
					this.MeterMakerChongJian.setChecked(true);
				} else if (jb_menter_name.equals("赛福")) {
					this.MeterMakerSaiFu.setChecked(true);
				} else if (jb_menter_name.equals("重前")) {
					this.MeterMakerChongQian.setChecked(true);
				} else if (jb_menter_name.equals("山城")) {
					this.MeterMakerShanCheng.setChecked(true);
				} else if (jb_menter_name.equals("天津自动化")) {
					this.MeterMakerTianJinZiDongHua.setChecked(true);
				} else {
					this.MeterMakerOtherBox.setChecked(true);
					model.MeterMakerOther.set(jb_menter_name);
				}

				// IC卡厂家型号
				String ic_menter_name = c.getString(c
						.getColumnIndex("IC_METER_NAME"));
				if(ic_menter_name == null)
					ic_menter_name = "";
				if (ic_menter_name.equals("金卡")) {
					this.ICMeterMakerHuaJie.setChecked(true);
				} else if (ic_menter_name.equals("先锋")) {
					this.ICMeterMakerSaiFu.setChecked(true);
				} 
//				else if (ic_menter_name.equals("秦川")) {
//					this.ICMeterMakerQinChuan.setChecked(true);
//				} else if (ic_menter_name.equals("秦港")) {
//					this.ICMeterMakerQinGang.setChecked(true);
//				} else if (ic_menter_name.equals("致力")) {
//					this.ICMeterMakerZhiLi.setChecked(true);
//				} 
				else {
					this.ICMeterMakerOtherBox.setChecked(true);
					model.ICMeterMakerOther.set(ic_menter_name);
				}

				// 表型
				String menter_type = c
						.getString(c.getColumnIndex("METER_TYPE"));
				if(menter_type == null)
					menter_type = "";
				if (menter_type.equals("G2.5")) {
					this.MeterTypeG25.setChecked(true);
				}else if (menter_type.equals("G4")) {
					this.MeterTypeG4.setChecked(true);
				}else if (menter_type.equals("G6")) {
					this.MeterTypeG6.setChecked(true);
				}else if (menter_type.equals("G10")) {
					this.MeterTypeG10.setChecked(true);
				}else if (menter_type.equals("G16")) {
					this.MeterTypeG16.setChecked(true);
				}else if (menter_type.equals("G25")) {
					this.MeterTypeg25.setChecked(true);
				}else if (menter_type.equals("G40")) {
					this.MeterTypeG40.setChecked(true);
				}else if (menter_type.equals("流量计")) {
					this.MeterTypeOther.setChecked(true);
				}

				
				// 基表数
				if(c.getString(c.getColumnIndex("JB_NUMBER")) != null )
					model.BaseMeterQuantity.set(c.getString(c.getColumnIndex("JB_NUMBER")));
				else
					model.BaseMeterQuantity.set("");
				// 表内剩余气量
				if(c.getString(c.getColumnIndex("SURPLUS_GAS")) != null )
					model.RemainGasQuantity.set(c.getString(c.getColumnIndex("SURPLUS_GAS")));
				else
					model.RemainGasQuantity.set("");
				//用气量
				if(c.getString(c.getColumnIndex("gas_quantity")) != null )
					model.GasQuantity.set(c.getString(c.getColumnIndex("gas_quantity")));
				else
					model.GasQuantity.set("");

				// 燃气表左右表
				if(c.getString(c.getColumnIndex("RQB_AROUND")) != null)
				{
					if (c.getString(c.getColumnIndex("RQB_AROUND")).equals("左表")) {
						this.MeterOnTheLeft.setChecked(true);
					} else {
						this.MeterOnTheRight.setChecked(true);
					}
				}

				// 燃气表基表号
				if (c.getString(c.getColumnIndex("RQB_JBCODE")) != null) {
					model.BaseMeterID.set(c.getString(c
							.getColumnIndex("RQB_JBCODE")));
				}
				else
					model.BaseMeterID.set("");

				// 燃气表生产年分
				if (c.getString(c.getColumnIndex("METERMADEYEAR")) != null) {
					model.MeterMadeYear.set(c.getString(c.getColumnIndex("METERMADEYEAR")));
				}
				else
					model.MeterMadeYear.set("");


				// 燃气表信息
				if(c.getString(c.getColumnIndex("RQB")) !=  null)
				if (c.getString(c.getColumnIndex("RQB")).equals("正常")) {
					this.MeterNormal.setChecked(true);
				}

				// 立管
				if(c.getString(c.getColumnIndex("STANDPIPE")) !=  null)
				if (c.getString(c.getColumnIndex("STANDPIPE")).equals("正常")) {
					this.PlumNormal.setChecked(true);
				}

				// 严密性测试
				if (c.getString(c.getColumnIndex("RIGIDITY")) !=  null && c.getString(c.getColumnIndex("RIGIDITY")).equals("正常")) {
					this.PlumProofNormal.setChecked(true);
				} else if (c.getString(c.getColumnIndex("RIGIDITY")) !=  null && c.getString(c.getColumnIndex("RIGIDITY")).equals("漏气")){
					this.PlumProofLeakage.setChecked(true);
				}

				// 静止压力
				if (c.getString(c.getColumnIndex("STATIC")) !=  null && c.getString(c.getColumnIndex("STATIC")).equals("正常")) {
					this.PlumPressureNormal.setChecked(true);
				} else if (c.getString(c.getColumnIndex("STATIC")) !=  null && c.getString(c.getColumnIndex("STATIC")).equals("不正常")){
					this.PlumPressureAbnormal.setChecked(true);
				}
				// 静止压力值
				if(c.getString(c.getColumnIndex("STATIC_DATA")) !=  null)
				model.PlumPressure.set(c.getString(c
						.getColumnIndex("STATIC_DATA")));

				// 表前阀
				if(c.getString(c.getColumnIndex("TABLE_TAP")) !=  null)
				if (c.getString(c.getColumnIndex("TABLE_TAP")).equals("正常")) {
					this.MeterValveNormal.setChecked(true);
				}
				// 灶前阀
				if(c.getString(c.getColumnIndex("COOK_TAP")) !=  null)
				if (c.getString(c.getColumnIndex("COOK_TAP")).equals("正常")) {
					this.CookerValveNormal.setChecked(true);
				}
				// 自闭阀
				if(c.getString(c.getColumnIndex("CLOSE_TAP")) !=  null)
				if (c.getString(c.getColumnIndex("CLOSE_TAP")).equals("正常")) {
					this.AutoValveNormal.setChecked(true);
				}
				// 户内管
				if(c.getString(c.getColumnIndex("INDOOR")) !=  null)
				if (c.getString(c.getColumnIndex("INDOOR")).equals("正常")) {
					this.HomePlumNormal.setChecked(true);
				}

				// 灶具漏气
				if ((c.getString(c.getColumnIndex("LEAKAGE_COOKER")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_COOKER"))
								.equals("灶具漏气"))) {
					this.LeakageCooker.setChecked(true);
				}

				// 热水器漏气
				if ((c.getString(c.getColumnIndex("LEAKAGE_HEATER")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_HEATER"))
								.equals("热水器漏气"))) {
					this.LeakageHeater.setChecked(true);
				}
				// 壁挂炉漏气
				if ((c.getString(c.getColumnIndex("LEAKAGE_BOILER")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_BOILER"))
								.equals("壁挂炉漏气"))) {
					this.LeakageBoiler.setChecked(true);
				}

				// 安检告知
				if ((c.getString(c.getColumnIndex("LEAKAGE_NOTIFIED")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_NOTIFIED"))
								.equals("安检告知"))) {
					this.LeakageNotified.setChecked(true);
				}

				// 漏气位置
				if (c.getString(c.getColumnIndex("LEAKGEPLACE")) != null)
				model.LeakagePlace.set(c.getString(c
						.getColumnIndex("LEAKGEPLACE")));

				// 灶具品牌
				String cook_brand = c.getString(c.getColumnIndex("COOK_BRAND"));
				if(cook_brand == null)
					cook_brand = "其他";
				if (cook_brand.equals("老板")) {
					this.CookerGangHuaZiJing.setChecked(true);
				} else if (cook_brand.equals("万和")) {
					this.CookerWanHe.setChecked(true);
				} else if (cook_brand.equals("万家乐")) {
					this.CookerWanJiaLe.setChecked(true);
				} else if (cook_brand.equals("方太")) {
					this.CookerLinNei.setChecked(true);
				} else if (cook_brand.equals("海尔")) {
					this.CookerHaiEr.setChecked(true);
				} else if (cook_brand.equals("帅康")) {
					this.CookerALiSiDun.setChecked(true);
				} else if (cook_brand.equals("樱花")) {
					this.CookerYinhHua.setChecked(true);
				} else if (cook_brand.equals("华帝")) {
					this.CookerHuaDi.setChecked(true);
				} else if (cook_brand.equals("其他")) {
					this.CookerOther.setChecked(true);
				} else if (cook_brand.equals("西门子")) {
					this.CookerXiMenZi.setChecked(true);
				}

				// 灶具型号
				String cook_type = c.getString(c.getColumnIndex("COOK_TYPE"));
				if(cook_type == null)
					cook_type = "";
				if (cook_type.equals("台式单眼")) {
					this.CookeTypeTabletSingle.setChecked(true);
				} else if (cook_type.equals("台式双眼")) {
					this.CookerTypeTabletDouble.setChecked(true);
				} else if (cook_type.equals("镶嵌双眼")) {
					this.CookerTypeEmbedDouble.setChecked(true);
				}

				// 安装管卡
				model.CookerPipeClampCount.set(c.getString(c
						.getColumnIndex("COOKERPIPECLAMPCOUNT")));
				// 更换软管
				model.CookerPipeLength.set(c.getString(c
						.getColumnIndex("COOKERPIPYLENGTH")));
				// 灶具软管
				if(c.getString(c.getColumnIndex("COOKPIPE_NORMAL")) != null)
				{
					if (c.getString(c.getColumnIndex("COOKPIPE_NORMAL")).equals(
							"正常")) {
						this.CookerPipeNormal.setChecked(true);
					}
				}
				// 灶具购置日期
				model.CookerBoughtTime.set(c.getString(c
						.getColumnIndex("COOK_DATE")));

				// 热水器品牌
				String water_brand = c.getString(c
						.getColumnIndex("WATER_BRAND"));
				if(water_brand == null)
					water_brand = "";
				if (water_brand.equals("美的")) {
					this.HeaterGangHuaZiJing.setChecked(true);
				} else if (water_brand.equals("万和")) {
					this.HeaterWanHe.setChecked(true);
				} else if (water_brand.equals("万家乐")) {
					this.HeaterWanJiaLe.setChecked(true);
				} else if (water_brand.equals("林内")) {
					this.HeaterLinNei.setChecked(true);
				} else if (water_brand.equals("海尔")) {
					this.HeaterHaiEr.setChecked(true);
//				} else if (water_brand.equals("阿里斯顿")) {
//					this.HeaterALiSiDun.setChecked(true);
				} else if (water_brand.equals("樱花")) {
					this.HeaterYingHua.setChecked(true);
				} else if (water_brand.equals("华帝")) {
					this.HeaterHuaDi.setChecked(true);
				} else if (water_brand.equals("其他")) {
					this.HeaterOther.setChecked(true);
				} else if (water_brand.equals("史密斯")) {
					this.HeaterShiMiSi.setChecked(true);
				} else if (water_brand.equals("小松鼠")) {
					this.HeaterXiaoSongShu.setChecked(true);
				}

				// 热水器型号
				if(c.getString(c.getColumnIndex("WATER_TYPE")) != null)
					model.HeaterType.set(c.getString(c.getColumnIndex("WATER_TYPE")));
				else
					model.HeaterType.set("");

				// 热水器烟道
				if(c.getString(c.getColumnIndex("WATER_FLUE")) != null)
				{
					if (c.getString(c.getColumnIndex("WATER_FLUE")).equals("平衡")) {
						this.VentilationBalanced.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"强排")) {
						this.VentilationForce.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"烟道")) {
						this.VentilationPath.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"直排")) {
						this.VentilationStraight.setChecked(true);
					}
				}

				// 热水器软管
				if (c.getString(c.getColumnIndex("WATER_PIPE")) != null)
				{
					if (c.getString(c.getColumnIndex("WATER_PIPE")).equals("正常")) {
						this.HeaterPipeNormal.setChecked(true);
					}
				}
				// 热水器安装时间
				model.HeaterBoughtTime.set(c.getString(c
						.getColumnIndex("WATER_DATE")));
				// 更换管卡
				model.HeaterPipeClampCount.set(c.getString(c
						.getColumnIndex("WATER_NUME")));
				// 热水器隐患
				String str = c.getString(c.getColumnIndex("WATER_HIDDEN"));
				if(str != null && str.equals("正常")) {
					this.HeaterPrecautionNone.setChecked(true);
				}

				// 壁挂炉品牌
				if(c.getString(c.getColumnIndex("WHE_BRAND")) != null)
				{
					if (c.getString(c.getColumnIndex("WHE_BRAND")).equals("美的")) {
						this.BoilerGangHuaZiJing.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"万和")) {
						this.BoilerWanHe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"万家乐")) {
						this.BoilerWanJiaLe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"林内")) {
						this.BoilerLinNei.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"海尔")) {
						this.BoilerHaiEr.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"小松鼠")) {
						this.BoilerXiaoSongShu.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"樱花")) {
						this.BoilerYingHua.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"华帝")) {
						this.BoilerHuaDi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"史密斯")) {
						this.BoilerShiMiSi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"其他")) {
						this.BoilerOther.setChecked(true);
					}
				}

				// 壁挂炉型号
				if(c.getString(c.getColumnIndex("WHE_TYPE")) != null)
					model.BoilerType.set(c.getString(c.getColumnIndex("WHE_TYPE")));
				else 
					model.BoilerType.set("");
				// 购置时间（壁挂锅炉）
				model.BoilerBoughtTime.set(c.getString(c	.getColumnIndex("WHE_DATE")));

				// 壁挂锅炉隐患
				if(c.getString(c.getColumnIndex("WHE_HIDDEN")) != null)
				if (c.getString(c.getColumnIndex("WHE_HIDDEN")).equals("正常")) {
					this.BoilerPrecautionNormal.setChecked(true);
				}

				// 用户意见
				if(c.getString(c.getColumnIndex("USER_SUGGESTION")) != null)
					model.UserSuggestion.set(c.getString(c.getColumnIndex("USER_SUGGESTION")));
				else 
					model.UserSuggestion.set("");
				//备注
				if(c.getString(c.getColumnIndex("Remark")) != null)
					model.Remark.set(c.getString(c.getColumnIndex("Remark")));
				else 
					model.Remark.set("");
				// 用户评价
				if(c.getString(c.getColumnIndex("USER_SATISFIED")) == null)
						this.FeebackSatisfied.setChecked(true);
				else
				{
					if (c.getString(c.getColumnIndex("USER_SATISFIED"))
							.equals("满意")) {
						this.FeebackSatisfied.setChecked(true);
					} else if (c.getString(c.getColumnIndex("USER_SATISFIED"))
							.equals("基本满意")) {
						this.FeebackOK.setChecked(true);
					} else if (c.getString(c.getColumnIndex("USER_SATISFIED"))
							.equals("不满意")) {
						this.FeebackUnsatisfied.setChecked(true);
					}
				}

				// 照片
				try
				{
				if (c.getString(c.getColumnIndex("USER_SIGN")) != null) {
					ImageView signPad = (ImageView) (findViewById(R.id.signPad));
					Util.releaseBitmap(signPad);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("USER_SIGN"))
							+ ".png");
					signPad.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIRST")) != null) {
					Util.releaseBitmap(img1);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FIRST"))
							+ ".jpg");
					img1.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_SECOND")) != null) {
					Util.releaseBitmap(img2);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_SECOND"))
							+ ".jpg");
					img2.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_THIRD")) != null) {
					Util.releaseBitmap(img3);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_THIRD"))
							+ ".jpg");
					img3.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FOUTH")) != null) {
					Util.releaseBitmap(img4);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FOUTH"))
							+ ".jpg");
					img4.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIFTH")) != null) {
					Util.releaseBitmap(img5);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_FIFTH"))
							+ ".jpg");
					img5.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_SIXTH")) != null) {
					Util.releaseBitmap(img6);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_SIXTH"))
							+ ".jpg");
					img6.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_SEVENTH")) != null) {
					Util.releaseBitmap(img7);
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir")
							+ c.getString(c.getColumnIndex("PHOTO_SEVENTH"))
							+ ".jpg");
					img7.setImageBitmap(bmp);
				}
				}
				catch(Exception e)
				{
					Toast.makeText(this, "获取图片失败。错误： " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}

			// 子表查询
			c = db.rawQuery("SELECT * FROM " + precautionTable
					+ " where id=?", new String[] { id });

			while (c.moveToNext()) {
				String device = c.getString(c.getColumnIndex("EQUIPMENT"));
				String box = c.getString(c.getColumnIndex("CONTENT"));

				// 燃气表
				if (device.equals("燃气表")) {
					if (box.equals("包裹")) {
						this.MeterWrapped.setChecked(true);
					} else if (box.equals("漏气")) {
						this.MeterLeakage.setChecked(true);
					} else if (box.equals("长通表")) {
						this.MeterFallThrough.setChecked(true);
					} else if (box.equals("死表")) {
						this.MeterDead.setChecked(true);
					} else if (box.equals("表不过气")) {
						this.MeterByPass.setChecked(true);
					} else if (box.equals("其他")) {
						this.MeterOther.setChecked(true);
					}
				}

				// 立管
				else if (device.equals("立管")) {
					if (box.equals("包裹")) {
						this.PlumWrapped.setChecked(true);
					} else if (box.equals("严重")) {
						this.PlumErosionSevere.setChecked(true);
						this.PlumEroded.setChecked(true);
					} else if (box.equals("中度")) {
						this.PlumErosionModerate.setChecked(true);
						this.PlumEroded.setChecked(true);
					} else if (box.equals("轻微")) {
						this.PlumErosionSlight.setChecked(true);
						this.PlumEroded.setChecked(true);
					} else if (box.equals("漏气")) {
						this.PlumLeakage.setChecked(true);
					} else if (box.equals("其他")) {
						this.PlumOther.setChecked(true);
					}
				}

				// 表前阀
				else if (device.equals("阀门表前阀")) {
					if (box.equals("内漏")) {
						this.MeterValveInnerLeakage.setChecked(true);
					} else if (box.equals("漏气")) {
						this.MeterValveLeakage.setChecked(true);
					} else if (box.equals("球阀")) {
						this.MeterValveBall.setChecked(true);
					} else if (box.equals("旋塞")) {
						this.MeterValvePlug.setChecked(true);
					} else if (box.equals("包裹")) {
						this.MeterValveWrapped.setChecked(true);
					}
				}

				// 灶前阀
				else if (device.equals("阀门灶前阀")) {
					if (box.equals("内漏")) {
						this.CookerValveInnerLeakage.setChecked(true);
					} else if (box.equals("漏气")) {
						this.CookerValveLeakage.setChecked(true);
					} else if (box.equals("包裹")) {
						this.CookerValveWrapped.setChecked(true);
					} else if (box.equals("安装过高")) {
						this.CookerValveTooHigh.setChecked(true);
					}
				}

				// 自闭阀
				else if (device.equals("阀门自闭阀")) {
					if (box.equals("内漏")) {
						this.AutoValveInnerLeakage.setChecked(true);
					} else if (box.equals("漏气")) {
						this.AutoValveLeakage.setChecked(true);
					} else if (box.equals("失灵")) {
						this.AutoValveNotWork.setChecked(true);
					} else if (box.equals("包裹")) {
						this.AutoValveWrapped.setChecked(true);
					}
				}

				// 户内管
				else if (device.equals("户内管")) {
					if (box.equals("漏气")) {
						this.HomePlumLeakage.setChecked(true);
					} else if (box.equals("穿客厅")) {
						this.HomePlumThroughSittingRoom.setChecked(true);
					} else if (box.equals("穿卧室")) {
						this.HomePlumThroughBedRoom.setChecked(true);
					} else if (box.equals("与电器过近")) {
						this.HomePlumNearAppliance.setChecked(true);
					} else if (box.equals("包裹")) {
						this.HomePlumWrapped.setChecked(true);
					} else if (box.equals("私改")) {
						this.HomePlumModified.setChecked(true);
					} else if (box.equals("厨房他用")) {
						this.HomePlumOtherUse.setChecked(true);
					}
				}

				// 灶具软管
				else if (device.equals("灶具软管")) {
					if (box.equals("漏气")) {
						this.CookerPipeLeakage.setChecked(true);
					} else if (box.equals("老化")) {
						this.CookerPipeFatigue.setChecked(true);
					} else if (box.equals("有安全隐患")) {
						this.CookerPipePrecaution.setChecked(true);
					}
				}

				// 热水器软管
				else if (device.equals("热水器软管")) {
					if (box.equals("漏气")) {
						this.HeaterPipeLeakage.setChecked(true);
					} else if (box.equals("老化")) {
						this.HeaterPipeFatigue.setChecked(true);
					} else if (box.equals("有安全隐患")) {
						this.HeaterPipePrecaution.setChecked(true);
					} else if (box.equals("铝塑管")) {
						this.HeaterPipePlastic.setChecked(true);
					}
				}

				// 热水器隐患
				else if (device.equals("热水器安全隐患")) {
					if (box.equals("直排热水器")) {
						this.HeaterPrecautionStraight.setChecked(true);
					} else if (box.equals("未安装烟道")) {
						this.HeaterPrecautionNoVentilation.setChecked(true);
					} else if (box.equals("烟道未排到室外")) {
						this.HeaterPrecautionTrapped.setChecked(true);
					} else if (box.equals("严禁使用")) {
						this.HeaterPrecautionProhibited.setChecked(true);
					} else if (box.equals("安装在卧室")) {
						this.HeaterPrecautionInHome.setChecked(true);
					} else if (box.equals("烟道破损")) {
						this.HeaterPrecautionBrokenVent.setChecked(true);
					} else if (box.equals("包裹烟道")) {
						this.HeaterPrecautionWrappedVent.setChecked(true);
					}
				}

				// 壁挂锅炉隐患
				else if (device.equals("壁挂锅炉安全隐患")) {
					if (box.equals("壁挂炉安装在卧室")) {
						this.BoilerPrecautionInBedRoom.setChecked(true);
					} else if (box.equals("已发定期安检告知书")) {
						this.BoilerPrecautionNotified.setChecked(true);
					}
				}

				// 安全隐患
				else if (device.equals("安全隐患")) {
					if (box.equals("燃气设施安装在卧室/卫生间")) {
						this.PrecautionInBathRoom.setChecked(true);
					} else if (box.equals("燃气设备安装在卧室")) {
						this.PrecautionInBedRoom.setChecked(true);
					} else if (box.equals("软管过长")) {
						this.PrecautionLongPipe.setChecked(true);
					} else if (box.equals("燃气管挂物接触电源线")) {
						this.PrecautionElectricWire.setChecked(true);
					} else if (box.equals("软管接三通")) {
						this.PrecautionThreeWay.setChecked(true);
					} else if (box.equals("软管穿柜/门窗/顶棚")) {
						this.PrecautionThroughFurniture.setChecked(true);
					} else if (box.equals("软管穿墙/地面")) {
						this.PrecautionThroughWall.setChecked(true);
					} else if (box.equals("使用非天然气专用软管")) {
						this.PrecautionValidPipe.setChecked(true);
					} else if (box.equals("软管上有接头")) {
						this.PrecautionWithConnector.setChecked(true);
					} else if (box.equals("软管离火源太近")) {
						this.PrecautionNearFire.setChecked(true);
					} else if (box.equals("软管包裹")) {
						this.PrecautionWrapped.setChecked(true);
					} else if (box.equals("已发近期安检报告书")) {
						this.PrecautionNotified.setChecked(true);
					} else if (box.equals("无管卡")) {
						this.PrecautionNoClamp.setChecked(true);
					} else if (box.equals("使用非安全燃气具")) {
						this.PrecautionUnsafeDevice.setChecked(true);
					} else if (box.equals("软管连接没有固定")) {
						this.PrecautionLoosePipe.setChecked(true);
					} else if (box.equals("软管暗敷")) {
						this.PrecautionPipeInDark.setChecked(true);
					} else if (box.equals("连接软管在户外")) {
						this.PrecautionPipeOutside.setChecked(true);
					} else if (box.equals("燃气具安装，摆放位置不规范")) {
						this.PrecautionMalPosition.setChecked(true);
					} else if (box.equals("与液化气共处一室")) {
						this.PrecautionNearLiquefiedGas.setChecked(true);
					}
				}
			}
			db.close();
		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
		}
	}

	/**
	 * 禁止掉无人、拒访、发送到访不遇卡选项
	 */
	private void DisableOtherCondition() {
		findViewById(R.id.noAnswerSwitch).setEnabled(false);
		findViewById(R.id.DenialSwitch).setEnabled(false);
		findViewById(R.id.chkHasNotified).setEnabled(false);
	}

	/**
	 * 从布局找到所有的控件
	 */
	private void InitControls() {
		//!!!!临时   @@@@@@@维修
		this.IsDispatchRepair = (CheckBox)findViewById(R.id.IsDispatchRepair);
		((Spinner)findViewById(R.id.RepairManList)).setEnabled(false);
		this.IsDispatchRepair.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((Spinner)findViewById(R.id.RepairManList)).setEnabled(isChecked);
			}
		});
		//!!!!!!!!@@@@@@@@@@@
		// 左表
		MeterOnTheLeft = (RadioButton) findViewById(R.id.MeterOnTheLeft);
		// 右表
		MeterOnTheRight = (RadioButton) findViewById(R.id.MeterOnTheRight);
		// 气表正常
		MeterNormal = (CheckBox) findViewById(R.id.MeterNormal);
		// 气表被包裹
		MeterWrapped = (CheckBox) findViewById(R.id.MeterWrapped);
		// 气表漏气
		MeterLeakage = (CheckBox) findViewById(R.id.MeterLeakage);
		// 长通表
		MeterFallThrough = (CheckBox) findViewById(R.id.MeterFallThrough);
		// 死表
		MeterDead = (CheckBox) findViewById(R.id.MeterDead);
		// 表不过气
		MeterByPass = (CheckBox) findViewById(R.id.MeterByPass);
		// 表-其他
		MeterOther = (CheckBox) findViewById(R.id.MeterOther);
		// 基表厂家型号 丹东
		MeterMakerDanDong = (RadioButton) findViewById(R.id.MeterMakerDanDong);
		// 基表厂家型号 重检
		MeterMakerChongJian = (RadioButton) findViewById(R.id.MeterMakerChongJian);
		// 基表厂家型号 赛福
		MeterMakerSaiFu = (RadioButton) findViewById(R.id.MeterMakerSaiFu);
		// 基表厂家型号 重前
		MeterMakerChongQian = (RadioButton) findViewById(R.id.MeterMakerChongQian);
		// 基表厂家型号 山城
		MeterMakerShanCheng = (RadioButton) findViewById(R.id.MeterMakerShanCheng);
		// 基表厂家型号 天津自动化
		MeterMakerTianJinZiDongHua = (RadioButton) findViewById(R.id.MeterMakerTianJinZiDongHua);
		//基本厂家型号其他
		MeterMakerOtherBox = (RadioButton) findViewById(R.id.MeterMakerOtherBox);
		// IC卡表厂家型号 华捷
		ICMeterMakerHuaJie = (RadioButton) findViewById(R.id.ICMeterMakerHuaJie);
		// IC卡表厂家型号 赛福
		ICMeterMakerSaiFu = (RadioButton) findViewById(R.id.ICMeterMakerSaiFu);
		// IC卡表厂家型号 秦川
		ICMeterMakerQinChuan = (RadioButton) findViewById(R.id.ICMeterMakerQinChuan);
		// IC卡表厂家型号 秦港
		ICMeterMakerQinGang = (RadioButton) findViewById(R.id.ICMeterMakerQinGang);
		// IC卡表厂家型号 致力
		ICMeterMakerZhiLi = (RadioButton) findViewById(R.id.ICMeterMakerZhiLi);
		//IC卡厂家型号其他
		ICMeterMakerOtherBox = (RadioButton) findViewById(R.id.ICMeterMakerOtherBox);
		
		// 表型 G2.5
		MeterTypeG25 = (RadioButton) findViewById(R.id.MeterTypeG25);
		// 表型 G4
		MeterTypeG4 = (RadioButton) findViewById(R.id.MeterTypeG4);
		// 表型 G6
		MeterTypeG6 = (RadioButton) findViewById(R.id.MeterTypeG6);
		// 表型 G10
		MeterTypeG10 = (RadioButton) findViewById(R.id.MeterTypeG10);
		// 表型 G16
		MeterTypeG16 = (RadioButton) findViewById(R.id.MeterTypeG16);
		// 表型 G25
		MeterTypeg25 = (RadioButton) findViewById(R.id.MeterTypeg25);
		// 表型 G40
		MeterTypeG40 = (RadioButton) findViewById(R.id.MeterTypeG40);
		// 表型 流量计
		MeterTypeOther = (RadioButton) findViewById(R.id.MeterTypeOther);
		// ------------------------------立管-------------------------
		// 立管正常
		PlumNormal = (CheckBox) findViewById(R.id.PlumNormal);
		// 立管包裹
		PlumWrapped = (CheckBox) findViewById(R.id.PlumWrapped);
		//立管腐蚀
		PlumEroded = (CheckBox)findViewById(R.id.PlumEroded);
		// 立管腐蚀
		PlumErosionSevere = (RadioButton) findViewById(R.id.PlumErosionSevere);
		PlumErosionModerate = (RadioButton) findViewById(R.id.PlumErosionModerate);
		PlumErosionSlight = (RadioButton) findViewById(R.id.PlumErosionSlight);
		// 立管漏气
		PlumLeakage = (CheckBox) findViewById(R.id.PlumLeakage);
		// 立管其他
		PlumOther = (CheckBox) findViewById(R.id.PlumOther);
		// 立管严密性 正常 漏气
		PlumProofNormal = (CheckBox) findViewById(R.id.PlumProofNormal);
		PlumProofLeakage = (CheckBox) findViewById(R.id.PlumProofLeakage);
		// 立管静止压力 正常 漏气
		PlumPressureNormal = (CheckBox) findViewById(R.id.PlumPressureNormal);
		PlumPressureAbnormal = (CheckBox) findViewById(R.id.PlumPressureAbnormal);
		// 表前阀 正常、内漏、漏气、球阀、旋塞阀、包裹
		MeterValveNormal = (CheckBox) findViewById(R.id.MeterValveNormal);
		MeterValveInnerLeakage = (CheckBox) findViewById(R.id.MeterValveInnerLeakage);
		MeterValveLeakage = (CheckBox) findViewById(R.id.MeterValveLeakage);
		MeterValveBall = (CheckBox) findViewById(R.id.MeterValveBall);
		MeterValvePlug = (CheckBox) findViewById(R.id.MeterValvePlug);
		MeterValveWrapped = (CheckBox) findViewById(R.id.MeterValveWrapped);
		// 灶前阀 正常、内漏、漏气、包裹、安装过高
		CookerValveNormal = (CheckBox) findViewById(R.id.CookerValveNormal);
		CookerValveInnerLeakage = (CheckBox) findViewById(R.id.CookerValveInnerLeakage);
		CookerValveLeakage = (CheckBox) findViewById(R.id.CookerValveLeakage);
		CookerValveWrapped = (CheckBox) findViewById(R.id.CookerValveWrapped);
		CookerValveTooHigh = (CheckBox) findViewById(R.id.CookerValveTooHigh);
		// 自闭阀 正常、内漏、漏气、包裹、失灵
		AutoValveNormal = (CheckBox) findViewById(R.id.AutoValveNormal);
		AutoValveInnerLeakage = (CheckBox) findViewById(R.id.AutoValveInnerLeakage);
		AutoValveLeakage = (CheckBox) findViewById(R.id.AutoValveLeakage);
		AutoValveNotWork = (CheckBox) findViewById(R.id.AutoValveNotWork);
		AutoValveWrapped = (CheckBox) findViewById(R.id.AutoValveWrapped);
		// 漏气、灶具漏气、热水器漏气、壁挂锅炉漏气、已发放安全告知书
		LeakageCooker = (CheckBox) findViewById(R.id.LeakageCooker);
		LeakageHeater = (CheckBox) findViewById(R.id.LeakageHeater);
		LeakageBoiler = (CheckBox) findViewById(R.id.LeakageBoiler);
		LeakageNotified = (CheckBox) findViewById(R.id.LeakageNotified);
		// ----------------------------------------灶具------------------------
		// 灶具 港华紫荆 万和 万家乐 林内 海尔 阿里斯顿 樱花 华帝 其他
		CookerGangHuaZiJing = (RadioButton) findViewById(R.id.CookerGangHuaZiJing);
		CookerWanHe = (RadioButton) findViewById(R.id.CookerWanHe);
		CookerWanJiaLe = (RadioButton) findViewById(R.id.CookerWanJiaLe);
		CookerLinNei = (RadioButton) findViewById(R.id.CookerLinNei);
		CookerHaiEr = (RadioButton) findViewById(R.id.CookerHaiEr);
		CookerALiSiDun = (RadioButton) findViewById(R.id.CookerALiSiDun);
		CookerYinhHua = (RadioButton) findViewById(R.id.CookerYinhHua);
		CookerHuaDi = (RadioButton) findViewById(R.id.CookerHuaDi);
		CookerOther = (RadioButton) findViewById(R.id.CookerOther);
		CookerXiMenZi = (RadioButton) findViewById(R.id.CookerXiMenZi);

		// 灶具类型 台式单眼 台式双眼 镶嵌双眼
		CookeTypeTabletSingle = (RadioButton) findViewById(R.id.CookeTypeTabletSingle);
		CookerTypeTabletDouble = (RadioButton) findViewById(R.id.CookerTypeTabletDouble);
		CookerTypeEmbedDouble = (RadioButton) findViewById(R.id.CookerTypeEmbedDouble);

		// 灶具软管 正常 漏气 老化 有安全隐患
		CookerPipeNormal = (CheckBox) findViewById(R.id.CookerPipeNormal);
		CookerPipeLeakage = (CheckBox) findViewById(R.id.CookerPipeLeakage);
		CookerPipeFatigue = (CheckBox) findViewById(R.id.CookerPipeFatigue);
		CookerPipePrecaution = (CheckBox) findViewById(R.id.CookerPipePrecaution);

		// 热水器品牌
		HeaterGangHuaZiJing = (RadioButton) findViewById(R.id.HeaterGangHuaZiJing);
		HeaterWanHe = (RadioButton) findViewById(R.id.HeaterWanHe);
		HeaterWanJiaLe = (RadioButton) findViewById(R.id.HeaterWanJiaLe);
		HeaterLinNei = (RadioButton) findViewById(R.id.HeaterLinNei);
		HeaterHaiEr = (RadioButton) findViewById(R.id.HeaterHaiEr);
//		HeaterALiSiDun = (RadioButton) findViewById(R.id.HeaterALiSiDun);
		HeaterYingHua = (RadioButton) findViewById(R.id.HeaterYingHua);
		HeaterHuaDi = (RadioButton) findViewById(R.id.HeaterHuaDi);
		HeaterOther = (RadioButton) findViewById(R.id.HeaterOther);
		HeaterShiMiSi = (RadioButton) findViewById(R.id.HeaterShiMiSi);
		HeaterXiaoSongShu = (RadioButton) findViewById(R.id.HeaterXiaoSongShu);
		// 热水器软管
		HeaterPipeNormal = (CheckBox) findViewById(R.id.HeaterPipeNormal);
		HeaterPipeLeakage = (CheckBox) findViewById(R.id.HeaterPipeLeakage);
		HeaterPipeFatigue = (CheckBox) findViewById(R.id.HeaterPipeFatigue);
		HeaterPipePrecaution = (CheckBox) findViewById(R.id.HeaterPipePrecaution);
		HeaterPipePlastic = (CheckBox) findViewById(R.id.HeaterPipePlastic);
		// 热水器烟道
		VentilationBalanced = (RadioButton) findViewById(R.id.VentilationBalanced); // 平衡
		VentilationForce = (RadioButton) findViewById(R.id.VentilationForce); // 强排
		VentilationPath = (RadioButton) findViewById(R.id.VentilationPath); // 烟道
		VentilationStraight = (RadioButton) findViewById(R.id.VentilationStraight); // 直排
		// 热水器隐患
		HeaterPrecautionNone = (CheckBox) findViewById(R.id.HeaterPrecautionNone); // 无
		HeaterPrecautionStraight = (CheckBox) findViewById(R.id.HeaterPrecautionStraight); // 直排热水器
		HeaterPrecautionNoVentilation = (CheckBox) findViewById(R.id.HeaterPrecautionNoVentilation); // 未安装烟道
		HeaterPrecautionTrapped = (CheckBox) findViewById(R.id.HeaterPrecautionTrapped); // 烟道未接到室外
		HeaterPrecautionProhibited = (CheckBox) findViewById(R.id.HeaterPrecautionProhibited); // 严禁使用
		HeaterPrecautionInHome = (CheckBox) findViewById(R.id.HeaterPrecautionInHome); // 安装在卧室
		HeaterPrecautionBrokenVent = (CheckBox) findViewById(R.id.HeaterPrecautionBrokenVent); // 烟道破损
		HeaterPrecautionWrappedVent = (CheckBox) findViewById(R.id.HeaterPrecautionWrappedVent); // 烟道包裹
		// 壁挂锅炉
		BoilerGangHuaZiJing = (RadioButton) findViewById(R.id.BoilerGangHuaZiJing); // 港华紫荆
		BoilerWanHe = (RadioButton) findViewById(R.id.BoilerWanHe); // 万和
		BoilerWanJiaLe = (RadioButton) findViewById(R.id.BoilerWanJiaLe); // 万家乐
		BoilerLinNei = (RadioButton) findViewById(R.id.BoilerLinNei); // 林内
		BoilerHaiEr = (RadioButton) findViewById(R.id.BoilerHaiEr); // 海尔
//		BoilerALiSiDun = (RadioButton) findViewById(R.id.BoilerALiSiDun); // 阿里斯顿
		BoilerYingHua = (RadioButton) findViewById(R.id.BoilerYingHua); // 樱花
		BoilerHuaDi = (RadioButton) findViewById(R.id.BoilerHuaDi); // 华帝
		BoilerOther = (RadioButton) findViewById(R.id.BoilerOther); // 其他
		BoilerShiMiSi = (RadioButton) findViewById(R.id.BoilerShiMiSi);
		BoilerXiaoSongShu = (RadioButton) findViewById(R.id.BoilerXiaoSongShu);

		//壁挂锅炉隐患
		BoilerPrecautionNormal = (CheckBox)findViewById(R.id.BoilerPrecautionNormal);
		BoilerPrecautionInBedRoom = (CheckBox)findViewById(R.id.BoilerPrecautionInBedRoom);
		BoilerPrecautionNotified = (CheckBox)findViewById(R.id.BoilerPrecautionNotified);

		// ------------------------------------安全隐患-----------------------
		PrecautionInBathRoom = (CheckBox) findViewById(R.id.PrecautionInBathRoom); // 燃气设施安装在卧室/卫生间
		PrecautionInBedRoom = (CheckBox) findViewById(R.id.PrecautionInBedRoom); // 燃气设备安装在卧室
		PrecautionLongPipe = (CheckBox) findViewById(R.id.PrecautionLongPipe); // 软管过长
		PrecautionElectricWire = (CheckBox) findViewById(R.id.PrecautionElectricWire); // 燃气管挂物接触电源线
		PrecautionThreeWay = (CheckBox) findViewById(R.id.PrecautionThreeWay); // 软管接三通
		PrecautionThroughFurniture = (CheckBox) findViewById(R.id.PrecautionThroughFurniture); // 软管穿柜/门窗/顶棚
		PrecautionThroughWall = (CheckBox) findViewById(R.id.PrecautionThroughWall); // 软管穿墙/地面
		PrecautionValidPipe = (CheckBox) findViewById(R.id.PrecautionValidPipe); // 使用非天然气专用软管
		PrecautionWithConnector = (CheckBox) findViewById(R.id.PrecautionWithConnector); // 软管上有接头
		PrecautionNearFire = (CheckBox) findViewById(R.id.PrecautionNearFire); // 软管离火源太近
		PrecautionWrapped = (CheckBox) findViewById(R.id.PrecautionWrapped); // 软管包裹
		PrecautionNotified = (CheckBox) findViewById(R.id.PrecautionNotified); // 已发近期安检报告书
		PrecautionNoClamp = (CheckBox) findViewById(R.id.PrecautionNoClamp); // 无管卡
		PrecautionMalPosition = (CheckBox) findViewById(R.id.PrecautionMalPosition); // 燃气具安装，摆放位置不规范
		PrecautionNearLiquefiedGas = (CheckBox) findViewById(R.id.PrecautionNearLiquefiedGas); // 与液化气共处一室
		PrecautionUnsafeDevice = (CheckBox) findViewById(R.id.PrecautionUnsafeDevice); // 非安全燃气具
		PrecautionLoosePipe = (CheckBox) findViewById(R.id.PrecautionLoosePipe); // 软管连接没有固定
		PrecautionPipeInDark = (CheckBox) findViewById(R.id.PrecautionPipeInDark); // 软管暗敷
		PrecautionPipeOutside = (CheckBox) findViewById(R.id.PrecautionPipeOutside); // 软管在户外

		// ----------------------------------------户内管------------------------
		HomePlumNormal = (CheckBox) findViewById(R.id.HomePlumNormal);
		HomePlumLeakage = (CheckBox) findViewById(R.id.HomePlumLeakage);
		HomePlumThroughSittingRoom = (CheckBox) findViewById(R.id.HomePlumThroughSittingRoom);
		HomePlumThroughBedRoom = (CheckBox) findViewById(R.id.HomePlumThroughBedRoom);
		HomePlumNearAppliance = (CheckBox) findViewById(R.id.HomePlumNearAppliance);
		HomePlumWrapped = (CheckBox) findViewById(R.id.HomePlumWrapped);
		HomePlumModified = (CheckBox) findViewById(R.id.HomePlumModified);
		HomePlumOtherUse = (CheckBox) findViewById(R.id.HomePlumOtherUse);

		// ------------------------------------客户意见反馈-----------------------
		FeebackSatisfied = (RadioButton) findViewById(R.id.FeebackSatisfied);
		FeebackOK = (RadioButton) findViewById(R.id.FeebackOK);
		FeebackUnsatisfied = (RadioButton) findViewById(R.id.FeebackUnsatisfied);
	}


	/**
	 * 销毁已经保存的照片
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//		if(!localSaved)
		//			Util.deleteFiles(uuid);
	}

	/**
	 * 初始化界面选择
	 */
	public void InitChoice() {
		// 左表
		this.MeterOnTheLeft.setChecked(true);
		// 基表厂家型号
		this.MeterMakerDanDong.setChecked(true);
		// IC卡厂家型号
		this.ICMeterMakerHuaJie.setChecked(true);
		// 表型 
		this.MeterTypeG25.setChecked(true);
		// 静止压力
		//this.PlumPressureNormal.setChecked(true);
		// 灶具
		//this.CookerOther.setChecked(true);
		// 型号
		//this.CookeTypeTabletSingle.setChecked(true);
		// 热水器
		//this.HeaterOther.setChecked(true);
		// 烟道
		//this.VentilationBalanced.setChecked(true);
		// 壁挂锅炉
		//this.BoilerOther.setChecked(true);
		// 评价
		this.FeebackSatisfied.setChecked(true);
	}


	@Override
	protected void onPause() {
		super.onPause();
		Save(SaveToJSONString(true, false), "T_INP", "T_INP_LINE", true);
		Util.setSharedPreference(this, "entryDateTime", Util.FormatDate("yyyy-MM-dd HH:mm:ss", entryDateTime.getTime()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Util.IsCached(this, uuid))
		{
			ReadFromDB(uuid, "T_INP", "T_INP_LINE");
			model.GetRepairPerson(uuid, "T_INP");
			String dt = Util.getSharedPreference(this, "entryDateTime");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
			{
				entryDateTime = formatter.parse(dt);
			}
			catch(Exception e)
			{
				
			}
		}
		else
		{
			if(inspected)
				this.ReadFromDB(uuid,"T_INSPECTION", "T_IC_SAFECHECK_HIDDEN");
			else
				InitChoice();
			//异步读取维修人员
			model.GetRepairPerson(uuid, "T_INSPECTION");
			//记录进入界面时间
			entryDateTime = new Date();
		}
	}


}
