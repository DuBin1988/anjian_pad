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
	// �뻧����ƻ�ID
	private IndoorInspectModel model;
	
	//sh��
	private boolean status;
	
	//������ʱ���ɵ�UUID
	public String uuid;
	private boolean inspected;
	
	//�������ʱ��
	//�����δ�죬����ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��ʱ�ӿ�����
	//                       ����ʱ����¼model�е�ʱ�䣬 ����ʱ��Ϊʱ��ʱ��
	//                       ����ʱ���ָ���ʼʱ�䡢����ʱ��
	//                       �ϴ�ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��Ϊ��ǰʱ��
	//                       �����٣���ͬ����ʱ
	//   �����Ѽ죬����ʱ����ʼʱ��Ϊ����ʱ�䣬����ʱ��ʱ�ӽ�ֹ������ʱ��Ϊ����ʱ�䣬ͬʱ��ֹ�����ˡ��ܼ졢���ò���ѡ�
	//                       ����ʱ����¼model�е�ʱ�䣬 ����ʱ��Ϊʱ��ʱ��
	//                       ����ʱ���ָ���ʼʱ�䡢����ʱ��
	//                       �ϴ�ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��Ϊ��ǰʱ��
	//                       �����٣���ͬ����ʱ
	//  �ܾ�/���ˣ�����ʱ����ʼʱ��Ϊ����ʱ�䣬����ʱ��ʱ�ӽ�ֹ������ʱ��Ϊ����ʱ�䣬�������ˡ��ܼ졢���ò���ѡ�
	//                       �޸����ˡ��ܼ�״̬ʱ�����ÿ�ʼʱ��(�������ʱ��)������ʱ�䣬��������ʱ��
	//                       ����ʱ����¼model�е�ʱ�䣬 ����ʱ��ʱ��ʱ��
	//                       ����ʱ���ָ���ʼʱ�䡢����ʱ��
	//                       �ϴ�ʱ����ʼʱ��Ϊ�������ʱ�䣬����ʱ��Ϊ��ǰʱ��
	//                       �����٣���ͬ����ʱ
	private Date entryDateTime;

	//�Ƿ��ɷ�ά��
	private CheckBox IsDispatchRepair;
	
	//���浱ǰ�������������Ƿ��Ѿ����浽����
	public boolean localSaved;
	public String paperId = "test";
	public String planId="";
	
	// ------------------------����------------------------------------
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
	// ---------------------ȼ������Ϣ---------------------------
	// ���
	public RadioButton MeterOnTheLeft;
	// �ұ�
	public RadioButton MeterOnTheRight;
	// ��������
	public CheckBox MeterNormal;
	// ��������
	public CheckBox MeterWrapped;
	// ����©��
	public CheckBox MeterLeakage;
	// ��ͨ��
	public CheckBox MeterFallThrough;
	// ����
	public CheckBox MeterDead;
	// ������
	public CheckBox MeterByPass;
	// ��-����
	public CheckBox MeterOther;
	// �������ͺ� ����
	public RadioButton MeterMakerDanDong;
	// �������ͺ� �ؼ�
	public RadioButton MeterMakerChongJian;
	// �������ͺ� ����
	public RadioButton MeterMakerSaiFu;
	// �������ͺ� ��ǰ
	public RadioButton MeterMakerChongQian;
	// �������ͺ� ɽ��
	public RadioButton MeterMakerShanCheng;
	// �������ͺ� ����Զ���
	public RadioButton MeterMakerTianJinZiDongHua;
	// �������ͺ� ����
	public RadioButton MeterMakerOtherBox;
	// IC�������ͺ� ����
	public RadioButton ICMeterMakerOtherBox;
	// IC�������ͺ� ����
	public RadioButton ICMeterMakerHuaJie;
	// IC�������ͺ� ����
	public RadioButton ICMeterMakerSaiFu;
	// IC�������ͺ� �ش�
	public RadioButton ICMeterMakerQinChuan;
	// IC�������ͺ� �ظ�
	public RadioButton ICMeterMakerQinGang;
	// IC�������ͺ� ����
	public RadioButton ICMeterMakerZhiLi;
	// ���� G2.5
	public RadioButton MeterTypeG25;
	// ���� G4
	public RadioButton MeterTypeG4;
	// ���� G6
	public RadioButton MeterTypeG6;
	// ���� G10
	public RadioButton MeterTypeG10;
	// ���� G16
	public RadioButton MeterTypeG16;
	// ���� G25
	public RadioButton MeterTypeg25;
	// ���� G40
	public RadioButton MeterTypeG40;
	// ����������
	public RadioButton MeterTypeOther;
	// ------------------------------����-------------------------
	// ��������
	public CheckBox PlumNormal;
	// ���ܰ���
	public CheckBox PlumWrapped;
	// ���ܸ�ʴ
	public CheckBox PlumEroded;
	public RadioButton PlumErosionSevere;
	public RadioButton PlumErosionModerate;
	public RadioButton PlumErosionSlight;
	// ����©��
	public CheckBox PlumLeakage;
	// ��������
	public CheckBox PlumOther;
	// ���������� ���� ©��
	public CheckBox PlumProofNormal;
	public CheckBox PlumProofLeakage;
	// ���ܾ�ֹѹ�� ���� ©��
	public CheckBox PlumPressureNormal;
	public CheckBox PlumPressureAbnormal;
	// ��ǰ�� ��������©��©�����򷧡�������������
	public CheckBox MeterValveNormal;
	public CheckBox MeterValveInnerLeakage;
	public CheckBox MeterValveLeakage;
	public CheckBox MeterValveBall;
	public CheckBox MeterValvePlug;
	public CheckBox MeterValveWrapped;
	// ��ǰ�� ��������©��©������������װ����
	public CheckBox CookerValveNormal;
	public CheckBox CookerValveInnerLeakage;
	public CheckBox CookerValveLeakage;
	public CheckBox CookerValveWrapped;
	public CheckBox CookerValveTooHigh;
	// �Աշ� ��������©��©����������ʧ��
	public CheckBox AutoValveNormal;
	public CheckBox AutoValveInnerLeakage;
	public CheckBox AutoValveLeakage;
	public CheckBox AutoValveNotWork;
	public CheckBox AutoValveWrapped;
	// ���ڹ� ���� ©�� ������ ���·������ ���� ˽�� ��������
	public CheckBox HomePlumNormal;
	public CheckBox HomePlumLeakage;
	public CheckBox HomePlumThroughSittingRoom;
	public CheckBox HomePlumThroughBedRoom;
	public CheckBox HomePlumNearAppliance;
	public CheckBox HomePlumWrapped;
	public CheckBox HomePlumModified;
	public CheckBox HomePlumOtherUse;

	// ©�������©������ˮ��©�����ڹҹ�¯©�����ѷ��Ű�ȫ��֪��
	public CheckBox LeakageCooker;
	public CheckBox LeakageHeater;
	public CheckBox LeakageBoiler;
	public CheckBox LeakageNotified;
	// ----------------------------------------���------------------------
	// ��� �ۻ��Ͼ� ��� ����� ���� ���� ����˹�� ӣ�� ���� ����
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

	// ������� ̨ʽ���� ̨ʽ˫�� ��Ƕ˫��
	public RadioButton CookeTypeTabletSingle;
	public RadioButton CookerTypeTabletDouble;
	public RadioButton CookerTypeEmbedDouble;

	// ������ ���� ©�� �ϻ� �а�ȫ����
	public CheckBox CookerPipeNormal;
	public CheckBox CookerPipeLeakage;
	public CheckBox CookerPipeFatigue;
	public CheckBox CookerPipePrecaution;

	// ��ˮ��Ʒ��
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
	// ��ˮ�����
	public CheckBox HeaterPipeNormal;
	public CheckBox HeaterPipeLeakage;
	public CheckBox HeaterPipeFatigue;
	public CheckBox HeaterPipePrecaution;
	public CheckBox HeaterPipePlastic;
	// ��ˮ���̵�
	public RadioButton VentilationBalanced; // ƽ��
	public RadioButton VentilationForce; // ǿ��
	public RadioButton VentilationPath; // �̵�
	public RadioButton VentilationStraight; // ֱ��
	// ��ˮ������
	public CheckBox HeaterPrecautionNone; // ��
	public CheckBox HeaterPrecautionStraight; // ֱ����ˮ��
	public CheckBox HeaterPrecautionNoVentilation; // δ��װ�̵�
	public CheckBox HeaterPrecautionTrapped; // �̵�δ�ӵ�����
	public CheckBox HeaterPrecautionProhibited; // �Ͻ�ʹ��
	public CheckBox HeaterPrecautionInHome; // ��װ������
	public CheckBox HeaterPrecautionBrokenVent; // �̵�����
	public CheckBox HeaterPrecautionWrappedVent; // �̵�����

	// �ڹҹ�¯
	public RadioButton BoilerGangHuaZiJing; // �ۻ��Ͼ�
	public RadioButton BoilerWanHe; // ���
	public RadioButton BoilerWanJiaLe; // �����
	public RadioButton BoilerLinNei; // ����
	public RadioButton BoilerHaiEr; // ����
//	public RadioButton BoilerALiSiDun; // ����˹��
	public RadioButton BoilerYingHua; // ӣ��
	public RadioButton BoilerHuaDi; // ����
	public RadioButton BoilerOther; // ����
	public RadioButton BoilerXiaoSongShu;
	public RadioButton BoilerShiMiSi;
	//-------------------------------�ڹҹ�¯����------------------------
	public CheckBox BoilerPrecautionNormal; // ��������
	public CheckBox BoilerPrecautionInBedRoom; // ��װ������
	public CheckBox BoilerPrecautionNotified; // �ѷ���֪��

	// ------------------------------------��ȫ����-----------------------
	public CheckBox PrecautionInBathRoom; // ȼ����ʩ��װ������/������
	public CheckBox PrecautionInBedRoom; // ȼ���豸��װ������
	public CheckBox PrecautionLongPipe; // ��ܹ���
	public CheckBox PrecautionElectricWire; // ȼ���ܹ���Ӵ���Դ��
	public CheckBox PrecautionThreeWay; // ��ܽ���ͨ
	public CheckBox PrecautionThroughFurniture; // ��ܴ���/�Ŵ�/����
	public CheckBox PrecautionThroughWall; // ��ܴ�ǽ/����
	public CheckBox PrecautionValidPipe; // ʹ�÷���Ȼ��ר�����
	public CheckBox PrecautionWithConnector; // ������н�ͷ
	public CheckBox PrecautionNearFire; // ������Դ̫��
	public CheckBox PrecautionWrapped; // ��ܰ���
	public CheckBox PrecautionNotified; // �ѷ����ڰ��챨����
	public CheckBox PrecautionNoClamp; // �޹ܿ�
	public CheckBox PrecautionMalPosition; // ȼ���߰�װ���ڷ�λ�ò��淶
	public CheckBox PrecautionNearLiquefiedGas; // ��Һ��������һ��
	public CheckBox PrecautionPipeInDark; // ��ܰ���
	public CheckBox PrecautionPipeOutside; // ��������ڻ���
	public CheckBox PrecautionUnsafeDevice; // ʹ�÷ǰ�ȫȼ����
	public CheckBox PrecautionLoosePipe; // �������û�й̶�

	// -------------------------------------�û�����-------------------------------
	public RadioButton FeebackSatisfied; // �޹ܿ�
	public RadioButton FeebackOK; // ȼ���߰�װ���ڷ�λ�ò��淶
	public RadioButton FeebackUnsatisfied; // ��Һ��������һ��

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
				// ���ð������ݲ�����Activity
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
				// ���ð������ݲ�����Activity
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
				// ���ð������ݲ�����Activity
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
				// ���ð������ݲ�����Activity
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
				// ���ð������ݲ�����Activity
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
				// ���ð������ݲ�����Activity
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
				// ���ð������ݲ�����Activity
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
					cook_brand = "����";
				if (cook_brand.equals("�ϰ�")) {
					this.CookerGangHuaZiJing.setChecked(true);
				} else if (cook_brand.equals("���")) {
					this.CookerWanHe.setChecked(true);
				} else if (cook_brand.equals("�����")) {
					this.CookerWanJiaLe.setChecked(true);
				} else if (cook_brand.equals("��̫")) {
					this.CookerLinNei.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerHaiEr.setChecked(true);
				} else if (cook_brand.equals("˧��")) {
					this.CookerALiSiDun.setChecked(true);
				} else if (cook_brand.equals("ӣ��")) {
					this.CookerYinhHua.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerHuaDi.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerOther.setChecked(true);
				} else if (cook_brand.equals("������")) {
					this.CookerXiMenZi.setChecked(true);
				}
				
				String cook_type = c.getString(c.getColumnIndex("COOK_TYPE"));
				if(cook_type == null)
					cook_type = "";
				if (cook_type.equals("̨ʽ����")) {
					this.CookeTypeTabletSingle.setChecked(true);
				} else if (cook_type.equals("̨ʽ˫��")) {
					this.CookerTypeTabletDouble.setChecked(true);
				} else if (cook_type.equals("��Ƕ˫��")) {
					this.CookerTypeEmbedDouble.setChecked(true);
				}
				
				model.CookerBoughtTime.set(c.getString(c.getColumnIndex("COOK_DATE")));
				
				String water_brand = c.getString(c
						.getColumnIndex("WATER_BRAND"));
				if(water_brand == null)
					water_brand = "";
				if (water_brand.equals("����")) {
					this.HeaterGangHuaZiJing.setChecked(true);
				} else if (water_brand.equals("���")) {
					this.HeaterWanHe.setChecked(true);
				} else if (water_brand.equals("�����")) {
					this.HeaterWanJiaLe.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterLinNei.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterHaiEr.setChecked(true);
				} else if (water_brand.equals("ӣ��")) {
					this.HeaterYingHua.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterHuaDi.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterOther.setChecked(true);
				} else if (water_brand.equals("ʷ��˹")) {
					this.HeaterShiMiSi.setChecked(true);
				} else if (water_brand.equals("С����")) {
					this.HeaterXiaoSongShu.setChecked(true);
				}
				
				if(c.getString(c.getColumnIndex("WATER_TYPE")) != null)
					model.HeaterType.set(c.getString(c.getColumnIndex("WATER_TYPE")));
				else
					model.HeaterType.set("");
				
				if(c.getString(c.getColumnIndex("WATER_FLUE")) != null)
				{
					if (c.getString(c.getColumnIndex("WATER_FLUE")).equals("ƽ��")) {
						this.VentilationBalanced.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"ǿ��")) {
						this.VentilationForce.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"�̵�")) {
						this.VentilationPath.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"ֱ��")) {
						this.VentilationStraight.setChecked(true);
					}
				}
				
				model.HeaterBoughtTime.set(c.getString(c.getColumnIndex("WATER_DATE")));
				
				if(c.getString(c.getColumnIndex("WHE_BRAND")) != null)
				{
					if (c.getString(c.getColumnIndex("WHE_BRAND")).equals("����")) {
						this.BoilerGangHuaZiJing.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"���")) {
						this.BoilerWanHe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"�����")) {
						this.BoilerWanJiaLe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerLinNei.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerHaiEr.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"С����")) {
						this.BoilerXiaoSongShu.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"ӣ��")) {
						this.BoilerYingHua.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerHuaDi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"ʷ��˹")) {
						this.BoilerShiMiSi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
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
				if (ic_menter_name.equals("��")) {
					this.ICMeterMakerHuaJie.setChecked(true);
				} else if (ic_menter_name.equals("�ȷ�")) {
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
				if (jb_menter_name.equals("����")) {
					this.MeterMakerDanDong.setChecked(true);
				} else if (jb_menter_name.equals("�ؼ�")) {
					this.MeterMakerChongJian.setChecked(true);
				} else if (jb_menter_name.equals("����")) {
					this.MeterMakerSaiFu.setChecked(true);
				} else if (jb_menter_name.equals("��ǰ")) {
					this.MeterMakerChongQian.setChecked(true);
				} else if (jb_menter_name.equals("ɽ��")) {
					this.MeterMakerShanCheng.setChecked(true);
				} else if (jb_menter_name.equals("����Զ���")) {
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
				}else if (menter_type.equals("������")) {
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
	 * ���ݰ����·���������ַ���ҷ����������û�
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

	//��ʾͼƬ�Ի���
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
			Toast.makeText(this, "��ȡͼƬʧ�ܡ����� " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * ������ع���
	 */
	private void preDisplayUIWork() {
		findViewById(R.id.chkHasNotified).setEnabled(false);
		//����
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
		//�ܾ��뻧
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
		//��ʴ
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
		
		//��ů��ʽ
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
		//������ů��ʽ
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
		
		//�������ͺ� ����
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
		
		//IC�������ͺ�
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
		//���δ��
		((CheckBox)findViewById(R.id.UnusedCooker)).setOnCheckedChangeListener(ClearCookerInfo);
		//��ˮ��δ��
		((CheckBox)findViewById(R.id.UnusedWaterHeater)).setOnCheckedChangeListener(ClearWaterHeaterInfo);
		//�ڹҹ�¯δ��
		((CheckBox)findViewById(R.id.UnusedWallBoiler)).setOnCheckedChangeListener(ClearWallBoilerInfo);
		
		//�������ͺ� ����
		MeterMakerDanDong.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerChongJian.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerSaiFu.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerChongQian.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerShanCheng.setOnCheckedChangeListener(ClearMeterMakerOther);
		MeterMakerTianJinZiDongHua.setOnCheckedChangeListener(ClearMeterMakerOther);
		//IC�������ͺ�
		ICMeterMakerHuaJie.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerSaiFu.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerQinChuan.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerQinGang.setOnCheckedChangeListener(ICClearMeterMakerOther);
		ICMeterMakerZhiLi.setOnCheckedChangeListener(ICClearMeterMakerOther);
	}
	
	/**
	 * �����д�������Ϣ
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
	 * �����д����ˮ����Ϣ
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
	 * �����д�ıڹҹ�¯��Ϣ
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
	 * ����������ͺ����������
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
	 * ���IC�������ͺ����������
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
	 * ���쵥�е�2���ѷ����ڰ����֪��״̬����һ�� 
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
	 * �����ұ߲���Ϊ��ֹʹ��
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
	 * 20150107sh����
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
			Toast.makeText(this, "��ȡͼƬʧ�ܡ����� " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * ������֤
	 * 
	 * @return
	 */
	public boolean validate() {
		// ���������ˡ��ܾ��뻧ѡ��
		if (model.IsNoAnswer.get() && model.IsEntryDenied.get()) {
			Toast.makeText(this, "����������ܾ��뻧ì��", Toast.LENGTH_LONG).show();
			return false;
		}
		// ���������˺͵��ò������Ƿ�ì��
		if (!model.IsNoAnswer.get() && model.HasNotified.get()) {
			Toast.makeText(this, "�������˺͵��ò�����ì�ܡ�", Toast.LENGTH_LONG).show();
			return false;
		}
		if(model.IsNoAnswer.get() || model.IsEntryDenied.get())
			return true;
		// �Ƚ�������У��
		String output = "";
		ValidationResult result = ModelValidator.ValidateModel(model);
		if (!result.isValid()) {
			output = "����:  \n";
			for (String msg : result.getValidationErrors()) {
				output += msg + "\n";
			}
		}
		if (output.length() > 0) {
			Toast.makeText(this, output, Toast.LENGTH_LONG).show();
			return false;
		} 


		// ȼ������Ϣ
		if(!this.MeterOnTheLeft.isChecked() && !this.MeterOnTheRight.isChecked() )
		{
			Toast.makeText(this, "��ѡ�����ұ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(this.MeterNormal.isChecked() ||this.MeterWrapped.isChecked() ||this.MeterLeakage.isChecked() ||this.MeterFallThrough.isChecked() ||this.MeterDead.isChecked() ||this.MeterByPass.isChecked() ||this.MeterOther.isChecked()))
		{
			Toast.makeText(this, "��ѡ��ȼ����ѡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!this.MeterTypeG25.isChecked() && !this.MeterTypeG4.isChecked() && !this.MeterTypeG6.isChecked() && !this.MeterTypeG10.isChecked() && !this.MeterTypeG16.isChecked() && !this.MeterTypeg25.isChecked() && !this.MeterTypeG40.isChecked() && !this.MeterTypeOther.isChecked())
		{
			Toast.makeText(this, "��ѡ����͡�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(this.MeterMakerDanDong.isChecked() ||this.MeterMakerChongJian.isChecked() ||this.MeterMakerSaiFu.isChecked() ||this.MeterMakerChongQian.isChecked() ||this.MeterMakerShanCheng.isChecked() ||this.MeterMakerTianJinZiDongHua.isChecked() ||this.MeterMakerOtherBox.isChecked()))
		{
			Toast.makeText(this, "��ѡ��������ͺš�", Toast.LENGTH_LONG).show();
			return false;			
		}
		if(!(this.ICMeterMakerHuaJie.isChecked() ||this.ICMeterMakerSaiFu.isChecked() ||this.ICMeterMakerQinChuan.isChecked() ||this.ICMeterMakerQinGang.isChecked() ||this.ICMeterMakerZhiLi.isChecked() ||this.ICMeterMakerOtherBox.isChecked()))
		{
			Toast.makeText(this, "��ѡ��IC�������ͺš�", Toast.LENGTH_LONG).show();
			return false;			
		}
		
		if(!(this.FeebackSatisfied.isChecked() ||this.FeebackOK.isChecked() ||this.FeebackUnsatisfied.isChecked()))
		{
			Toast.makeText(this, "��ѡ��ͻ����ۡ�", Toast.LENGTH_LONG).show();
			return false;			
		}
//		if(!(this.PlumPressureNormal.isChecked() ||this.PlumPressureAbnormal.isChecked()))
//		{
//			Toast.makeText(this, "��ѡ�����ܾ�ֹѹ����", Toast.LENGTH_LONG).show();
//			return false;			
//		}
		if ((this.MeterWrapped.isChecked() || this.MeterLeakage.isChecked()
				|| this.MeterFallThrough.isChecked()
				|| this.MeterDead.isChecked() || this.MeterByPass.isChecked() || this.MeterOther
				.isChecked()) && MeterNormal.isChecked()) {
			Toast.makeText(this, "ȼ������Ϣì��", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.MeterWrapped.isChecked() || this.MeterLeakage.isChecked()
				|| this.MeterFallThrough.isChecked()
				|| this.MeterDead.isChecked() || this.MeterByPass.isChecked() || this.MeterOther
				.isChecked() || MeterNormal.isChecked()) {
		}
		else
		{
			Toast.makeText(this, "ȼ������Ϣȱʧ��", Toast.LENGTH_LONG).show();
			return false;
		}


		// ����
		if ((this.PlumWrapped.isChecked() || this.PlumErosionSevere.isChecked()
				|| this.PlumErosionModerate.isChecked()
				|| this.PlumErosionSlight.isChecked()
				|| this.PlumLeakage.isChecked() || this.PlumOther.isChecked())
				&& PlumNormal.isChecked()) {
			Toast.makeText(this, "���ܰ�����Ϣì��", Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "������Ϣȱʧ��", Toast.LENGTH_LONG).show();
			return false;
		}

		// �����Բ���
//		if (this.PlumProofLeakage.isChecked()
//				&& this.PlumProofNormal.isChecked()) {
//			Toast.makeText(this, "�����Բ��Խ��ì��", Toast.LENGTH_LONG).show();
//			return false;
//		}
//		if (this.PlumProofLeakage.isChecked()
//				|| this.PlumProofNormal.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "�����Բ�����Ϣȱʧ��", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// ��ǰ��,������У��
//		if ((this.MeterValveInnerLeakage.isChecked()
//				|| this.MeterValveLeakage.isChecked()
//				|| this.MeterValveBall.isChecked()
//				|| this.MeterValvePlug.isChecked() || this.MeterValveWrapped
//				.isChecked()) && this.MeterValveNormal.isChecked()) {
//			Toast.makeText(this, "��ǰ����Ϣì��", Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "��ǰ����Ϣȱʧ��", Toast.LENGTH_LONG).show();
			return false;
		}

		// ��ǰ��
		if ((this.CookerValveInnerLeakage.isChecked()
				|| this.CookerValveLeakage.isChecked()
				|| this.CookerValveWrapped.isChecked() || this.CookerValveTooHigh
				.isChecked()) && this.CookerValveNormal.isChecked()) {
			Toast.makeText(this, "��ǰ����Ϣì��", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.CookerValveInnerLeakage.isChecked()
				|| this.CookerValveLeakage.isChecked()
				|| this.CookerValveWrapped.isChecked() || this.CookerValveTooHigh
				.isChecked()|| this.CookerValveNormal.isChecked()) {
		}else {
			Toast.makeText(this, "��ǰ����Ϣȱʧ��", Toast.LENGTH_LONG).show();
			return false;
		}

		// �Աշ�
		if ((this.AutoValveInnerLeakage.isChecked()
				|| this.AutoValveLeakage.isChecked()
				|| this.AutoValveNotWork.isChecked() || this.AutoValveWrapped
				.isChecked()) && this.AutoValveNormal.isChecked()) {
			Toast.makeText(this, "�Աշ���Ϣì��", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.AutoValveInnerLeakage.isChecked()
				|| this.AutoValveLeakage.isChecked()
				|| this.AutoValveNotWork.isChecked() || this.AutoValveWrapped
				.isChecked() || this.AutoValveNormal.isChecked()) {
		}
		else {
			Toast.makeText(this, "�Աշ���Ϣȱʧ��", Toast.LENGTH_LONG).show();
			return false;

		}

		// ���ڹ�
		if ((this.HomePlumLeakage.isChecked()
				|| this.HomePlumThroughSittingRoom.isChecked()
				|| this.HomePlumThroughBedRoom.isChecked()
				|| this.HomePlumNearAppliance.isChecked()
				|| this.HomePlumWrapped.isChecked()
				|| this.HomePlumModified.isChecked() || this.HomePlumOtherUse
				.isChecked()) && this.HomePlumNormal.isChecked()) {
			Toast.makeText(this, "���ڹ���Ϣì��", Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "���ڹ���Ϣȱʧ��", Toast.LENGTH_LONG).show();
			return false;
		}
		// ������
		if ((this.CookerPipeLeakage.isChecked()
				|| this.CookerPipeFatigue.isChecked() || this.CookerPipePrecaution
				.isChecked()) && this.CookerPipeNormal.isChecked()) {
			Toast.makeText(this, "��������Ϣì��", Toast.LENGTH_LONG).show();
			return false;
		}

//		if (this.CookerPipeLeakage.isChecked()
//				|| this.CookerPipeFatigue.isChecked() || this.CookerPipePrecaution
//				.isChecked() || this.CookerPipeNormal.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "��������Ϣȱʧ��", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// ��ˮ�����
//		if ((this.HeaterPipeLeakage.isChecked()
//				|| this.HeaterPipeFatigue.isChecked()
//				|| this.HeaterPipePrecaution.isChecked() || this.HeaterPipePlastic
//				.isChecked()) && this.HeaterPipeNormal.isChecked()) {
//			Toast.makeText(this, "��ˮ�������Ϣì��", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// ��ˮ����ȫ����
		if ((this.HeaterPrecautionStraight.isChecked()
				|| this.HeaterPrecautionNoVentilation.isChecked()
				|| this.HeaterPrecautionTrapped.isChecked()
				|| this.HeaterPrecautionProhibited.isChecked() || this.HeaterPrecautionInHome
				.isChecked()) && this.HeaterPrecautionNone.isChecked()) {
			Toast.makeText(this, "��ˮ����ȫ������Ϣì��", Toast.LENGTH_LONG).show();
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
//			Toast.makeText(this, "��ˮ����ȫ������Ϣȱʧ��", Toast.LENGTH_LONG).show();
//			return false;
//		}

		// �ڹҹ�¯��ȫ����
		if ((this.BoilerPrecautionInBedRoom.isChecked()
				|| this.BoilerPrecautionNotified.isChecked()
				)&& this.BoilerPrecautionNormal.isChecked()) {
			Toast.makeText(this, "�ڹҹ�¯��ȫ������Ϣì��", Toast.LENGTH_LONG).show();
			return false;
		}

//		if (this.BoilerPrecautionInBedRoom.isChecked()
//				|| this.BoilerPrecautionNotified.isChecked()
//				|| this.BoilerPrecautionNormal.isChecked()) {
//		}
//		else {
//			Toast.makeText(this, "�ڹҹ�¯��ȫ������Ϣȱʧ��", Toast.LENGTH_LONG).show();
//			return false;
//		}
		// ��ʱ���У��
		@SuppressWarnings("static-access")
		Calendar c = Calendar.getInstance();
		int yearNow = c.get(Calendar.YEAR);

		String yearString = model.MeterMadeYear.get();
		// ȼ�����������
		if (yearString != null && !yearString.equals("")) {
			int year = Integer.parseInt(yearString);
			if (year < 1970 || year > yearNow) {
				Toast.makeText(this, "ȼ���������������", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		// ѹ��ֵ
		if (model.PlumPressure.get() != null && !model.PlumPressure.get().equals("")) {
			int pressure = Integer.parseInt(model.PlumPressure.get());
			if (pressure < 0 || pressure > 1000) {
				Toast.makeText(this, "ѹ��ֵ����", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		// ��߹���ʱ��
		yearString = model.CookerBoughtTime.get();
		if (yearString != null && !yearString.equals("")) {
			try
			{
			int year = Integer.parseInt(yearString);
			if (year < 1970 || year > yearNow) {
				Toast.makeText(this, "��߹�����ݱ���С�ڵ���" + yearNow + "���Ҵ���1970��", Toast.LENGTH_LONG).show();
				return false;
			}
			}
			catch(Exception e)
			{
				Toast.makeText(this, "��߹�����ݱ���Ϊ���֡�", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		// �ڹҹ�¯����ʱ��
		yearString = model.BoilerBoughtTime.get();
		if (yearString != null && !yearString.equals("")) {
			int year = Integer.parseInt(yearString);
			if (year < 1970 || year > yearNow) {
				Toast.makeText(this, "�ڹҹ�¯�����������", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		//����ů����ѡ��
		if((model.OtherHeatedType.get()==null || model.OtherHeatedType.get().trim().length()==0) && ((Spinner)findViewById(R.id.HeatedTypeList)).getSelectedItemPosition()==3)
		{
			Toast.makeText(this, "����д������ů��ʽ��", Toast.LENGTH_LONG).show();
			return false;
		}
		//����������ͺ�����ѡ��
		if((model.MeterMakerOther.get() == null || model.MeterMakerOther.get().trim().length()==0) && this.MeterMakerOtherBox.isChecked())
		{
			Toast.makeText(this, "����д�����������ͺš�", Toast.LENGTH_LONG).show();
			return false;
		}
		//����IC�������������ͺ�
		if((model.ICMeterMakerOther.get() == null || model.ICMeterMakerOther.get().trim().length()==0) && this.ICMeterMakerOtherBox.isChecked())
		{
			Toast.makeText(this, "����д����IC�������ͺš�", Toast.LENGTH_LONG).show();
			return false;
		}
		ArrayList<String> severeList = new ArrayList<String>();
		ArrayList<String> generalList = new ArrayList<String>();
//ȡ�����ά��
//		model.GetPrecautionMap(severeList, generalList);
//		if(!(severeList.size()==0 && generalList.size()==0) && !((CheckBox)findViewById(R.id.IsDispatchRepair)).isChecked())
//		{
//			Toast.makeText(this, "���������������ѡ��ά���ˡ�", Toast.LENGTH_LONG).show();
//			return false;
//		}
		
		//TODO ��� �ܿ�������������������� ��ˮ�� �����ܿ�����������ʣ��������������������� �ȴ�У��
		if (!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_1.jpg"))		
		{
			Toast.makeText(this, "���ȼ�������ա�", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid	+ "_2.jpg"))		
		{
			Toast.makeText(this, "����������ա�", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// ���ر��氲���¼
	public boolean Save(String objStr, String inspectionTable, String precautionTable, boolean isTemp) {
		try {
			boolean isEntryInspection;
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);
			JSONObject row = new JSONObject(objStr);
			String uuid = row.getString("ID");
			String paperId = row.getString("CHECKPAPER_ID");
			isEntryInspection = row.getString("CONDITION").equals("����");
			// �ֱ��д��������
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

			// ɾ������
			db.execSQL(
					"delete from " + precautionTable + " where id in (select id from " + inspectionTable  + " where CHECKPAPER_ID=?)",
					new Object[] { paperId });
			// ɾ�������
			db.execSQL("DELETE FROM " + inspectionTable + "  where CHECKPAPER_ID='" + paperId
					+ "'");
			String sql1 = "INSERT INTO " + inspectionTable + " (ID";
			String sql2 = ") VALUES('" + uuid + "'";
			// �������¼
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
			// ����Ӽ�¼
			if(isEntryInspection)
				InsertPrecaution(uuid, redundantCols, slaveMap, db, precautionTable);
			db.close();
			if(!isTemp)
			{
				//���°���״̬		
				String state = row.getString("CONDITION");
				boolean needsRepair = false;
				if(row.has("NEEDS_REPAIR"))
					 needsRepair = row.getString("NEEDS_REPAIR").equals("��");
				SetInspectionState(paperId, state, needsRepair);
			}
			return true;

		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
			return false;
		}
	}

	/**
	 * ���°���״̬
	 * @param paperId
	 * @param state
	 * @param needsRepair 
	 */
	private void SetInspectionState(String paperId, String state, boolean needsRepair) {

		if(state.equals("����"))
		{
			Util.SetBit(this, Vault.NOANSWER_FLAG, paperId);
		}
		else if(state.equals("�ܾ�"))
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
	 * ������������
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
	 * ��ҳ���ռ������ֶε�ֵ
	 */
	public String SaveToJSONString(boolean saveRepair, boolean upload) {
		JSONObject row = new JSONObject();
		try {
			// �û����
			row.put("f_userid", model.UserID.get());
			// uuid
			row.put("ID", uuid);
			// ���쵥ID
			row.put("CHECKPAPER_ID", this.paperId);
			//����ID
			row.put("CHECKPLAN_ID", this.planId);
			//������
			row.put("SAVE_PEOPLE", Util.getSharedPreference(this, Vault.CHECKER_NAME));

			if (model.IsNoAnswer.get())
				// ��������
				row.put("CONDITION", "����");
			if (model.IsEntryDenied.get())
				// �ܾ��뻧
				row.put("CONDITION", "�ܾ�");
			// �ѷ����ò�����
			if (model.HasNotified.get())
				row.put("HasNotified", "�ѷ�");
			else
				row.put("HasNotified", "");
			//�����ϴ�
			if(!upload)
			{
				// ����ʱ��
				row.put("ARRIVAL_TIME", model.InspectionDate.get() + " "
						+ model.ArrivalTime.get());
				// �뿪ʱ��
				String tm = ((MyDigitalClock)this.findViewById(R.id.digitalClock)).getText().toString();
				row.put("DEPARTURE_TIME",
						model.InspectionDate.get() + " " + tm);
			}
			else
			{
				// ����ʱ��Ϊ�������ʱ��
				row.put("ARRIVAL_TIME", Util.FormatDate("yyyy-MM-dd HH:mm:ss", entryDateTime.getTime()));
				//�뿪ʱ��Ϊ��ǰʱ��
				row.put("DEPARTURE_TIME", Util.FormatDate("yyyy-MM-dd HH:mm:ss", new Date().getTime()));
			}
			// С������
			row.put("UNIT_NAME", model.ResidentialAreaName.get());
			// С����ַ
			row.put("ROAD", model.ResidentialAreaAddress.get());
			// ¥��
			row.put("CUS_DOM", model.BuildingNo.get());
			// ��Ԫ
			row.put("CUS_DY", model.UnitNo.get());
			// ¥��
			row.put("CUS_FLOOR", model.LevelNo.get());
			// ����
			row.put("CUS_ROOM", model.RoomNo.get());
			if (!row.has("CONDITION"))
				// ������
				row.put("CONDITION", "����");
			else
			{
				return row.toString();
			}
			
			// �û�����
			row.put("USER_NAME", model.UserName.get());
			// IC����
			row.put("CARD_ID", model.ICCardNo.get());
			// �绰����
			row.put("TELPHONE", model.Telephone.get());
			//ǩ���˵绰
			row.put("SIGNTELEPHONE", model.SignTelephone.get());
			// �û�������ַ
			row.put("OLD_ADDRESS", model.ArchiveAddress.get());
			// ���ݽṹ
			Spinner spinner1 = (Spinner) this
					.findViewById(R.id.StructureTypeList);
			row.put("ROOM_STRUCTURE", spinner1.getSelectedItem().toString());
			// ��ů��ʽ
			if (model.OtherHeatedType.get().trim().equals("")) {
				Spinner spinner2 = (Spinner) this
						.findViewById(R.id.HeatedTypeList);
				row.put("WARM", spinner2.getSelectedItem().toString());
			} else
				row.put("WARM", model.OtherHeatedType.get());
			// ������
			row.put("JB_NUMBER", model.BaseMeterQuantity.get());
			// ʣ������
			row.put("SURPLUS_GAS", model.RemainGasQuantity.get());
			//������
			row.put("gas_quantity", model.GasQuantity.get());
			//�ۼƹ�����
			row.put("buy_gas_quantity", model.BuyGasQuantity.get());
			// ȼ�������ұ�
			if (this.MeterOnTheLeft.isChecked())
				row.put("RQB_AROUND", "���");
			else
				row.put("RQB_AROUND", "�ұ�");
			// ȼ��������
			row.put("RQB_JBCODE", model.BaseMeterID.get());
			// ȼ�����������
			row.put("METERMADEYEAR", model.MeterMadeYear.get());
			// ȼ����
			if (this.MeterNormal.isChecked())
				row.put("RQB", "����");
			else 
				row.put("RQB", "������");
			// ����
			if (this.MeterWrapped.isChecked())
				row.put("ȼ����_1", "����");
			// ©��
			if (this.MeterLeakage.isChecked())
				row.put("ȼ����_2", "©��");
			// ��ͨ��
			if (this.MeterFallThrough.isChecked())
				row.put("ȼ����_3", "��ͨ��");
			// ����
			if (this.MeterDead.isChecked())
				row.put("ȼ����_4", "����");
			// ������
			if (this.MeterByPass.isChecked())
				row.put("ȼ����_5", "������");
			// ����
			if (this.MeterOther.isChecked())
				row.put("ȼ����_6", "����");
			// �������ͺ�
			if (model.MeterMakerOther.get().trim().equals("")) {
				if (MeterMakerDanDong.isChecked())
					row.put("JB_METER_NAME", "����");
				else if (MeterMakerChongJian.isChecked())
					row.put("JB_METER_NAME", "�ؼ�");
				else if (MeterMakerSaiFu.isChecked())
					row.put("JB_METER_NAME", "����");
				else if (MeterMakerChongQian.isChecked())
					row.put("JB_METER_NAME", "��ǰ");
				else if (MeterMakerShanCheng.isChecked())
					row.put("JB_METER_NAME", "ɽ��");
				else if (MeterMakerTianJinZiDongHua.isChecked())
					row.put("JB_METER_NAME", "����Զ���");
			} else
				row.put("JB_METER_NAME", model.MeterMakerOther.get());
			// IC�����һ����ͺ�

			if (model.ICMeterMakerOther.get().trim().equals("")) {
				if (ICMeterMakerHuaJie.isChecked())
					row.put("IC_METER_NAME", "��");
				else if (ICMeterMakerSaiFu.isChecked())
					row.put("IC_METER_NAME", "�ȷ�");
//				else if (ICMeterMakerQinChuan.isChecked())
//					row.put("IC_METER_NAME", "�ش�");
//				else if (ICMeterMakerQinGang.isChecked())
//					row.put("IC_METER_NAME", "�ظ�");
//				else if (ICMeterMakerZhiLi.isChecked())
//					row.put("IC_METER_NAME", "����");
			} else
				row.put("IC_METER_NAME", model.ICMeterMakerOther.get());
			// ����
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
				row.put("METER_TYPE", "������");
			// ����
			if (this.PlumNormal.isChecked())
				row.put("STANDPIPE", "����");
			else 
				row.put("STANDPIPE", "������");
			// ����
			if (this.PlumWrapped.isChecked())
				row.put("����_1", "����");
			// ©��
			if (this.PlumErosionSevere.isChecked())
				row.put("����_2", "����");
			// ��ͨ��
			if (this.PlumErosionModerate.isChecked())
				row.put("����_3", "�ж�");
			// ����
			if (this.PlumErosionSlight.isChecked())
				row.put("����_4", "��΢");
			// ������
			if (this.PlumLeakage.isChecked())
				row.put("����_5", "©��");
			// ����
			if (this.PlumOther.isChecked())
				row.put("����_6", "����");
			// �����Բ���
			if (this.PlumProofNormal.isChecked())
				row.put("RIGIDITY", "����");
			else
				row.put("RIGIDITY", "������");

			if (this.PlumProofLeakage.isChecked())
				row.put("RIGIDITY", "©��");
			// ��ֹѹ��
			if (this.PlumPressureNormal.isChecked())
				row.put("STATIC ", "�ϸ�");
			else if (this.PlumPressureAbnormal.isChecked())
				row.put("STATIC", "���ϸ�");
			else 
				row.put("STATIC", "");
			row.put("STATIC_DATA", model.PlumPressure.get());
			// ����
			// ��ǰ��
			if (this.MeterValveNormal.isChecked())
				row.put("TABLE_TAP", "����");
			else
				row.put("TABLE_TAP", "������");

			if (this.MeterValveInnerLeakage.isChecked())
				row.put("���ű�ǰ��_1", "��©");
			if (this.MeterValveLeakage.isChecked())
				row.put("���ű�ǰ��_2", "©��");
			if (this.MeterValveBall.isChecked())
				row.put("���ű�ǰ��_3", "��");
			if (this.MeterValvePlug.isChecked())
				row.put("���ű�ǰ��_4", "����");
			if (this.MeterValveWrapped.isChecked())
				row.put("���ű�ǰ��_5", "����");

			// ��ǰ��
			if (this.CookerValveNormal.isChecked())
				row.put("COOK_TAP", "����");
			else
				row.put("COOK_TAP", "������");

			if (this.CookerValveInnerLeakage.isChecked())
				row.put("������ǰ��_1", "��©");
			if (this.CookerValveLeakage.isChecked())
				row.put("������ǰ��_2", "©��");
			if (this.CookerValveWrapped.isChecked())
				row.put("������ǰ��_3", "����");
			if (this.CookerValveTooHigh.isChecked())
				row.put("������ǰ��_4", "��װ����");
			// �Աշ�
			if (this.AutoValveNormal.isChecked())
				row.put("CLOSE_TAP", "����");
			else
				row.put("CLOSE_TAP", "������");

			if (this.AutoValveInnerLeakage.isChecked())
				row.put("�����Աշ�_1", "��©");
			if (this.AutoValveLeakage.isChecked())
				row.put("�����Աշ�_2", "©��");
			if (this.AutoValveNotWork.isChecked())
				row.put("�����Աշ�_3", "ʧ��");
			if (this.AutoValveWrapped.isChecked())
				row.put("�����Աշ�_4", "����");
			// ���ڹ�
			if (this.HomePlumNormal.isChecked())
				row.put("INDOOR", "����");
			else
				row.put("INDOOR", "������");

			if (this.HomePlumLeakage.isChecked())
				row.put("���ڹ�_1", "©��");
			if (this.HomePlumThroughSittingRoom.isChecked())
				row.put("���ڹ�_2", "������");
			if (this.HomePlumThroughBedRoom.isChecked())
				row.put("���ڹ�_3", "������");
			if (this.HomePlumNearAppliance.isChecked())
				row.put("���ڹ�_4", "���������");
			if (this.HomePlumWrapped.isChecked())
				row.put("���ڹ�_5", "����");
			if (this.HomePlumModified.isChecked())
				row.put("���ڹ�_6", "˽��");
			if (this.HomePlumOtherUse.isChecked())
				row.put("���ڹ�_7", "��������");

			// ©��
			if (this.LeakageCooker.isChecked())
				row.put("LEAKAGE_COOKER", "���©��");
			if (this.LeakageHeater.isChecked())
				row.put("LEAKAGE_HEATER", "��ˮ��©��");
			if (this.LeakageBoiler.isChecked())
				row.put("LEAKAGE_BOILER", "�ڹ�¯©��");
			if (this.LeakageNotified.isChecked())
				row.put("LEAKAGE_NOTIFIED", "�����֪");

			// ©��λ��
			row.put("LEAKGEPLACE", model.LeakagePlace.get());

			// ���
			row.put("COOK_BRAND", "");
			if (this.CookerGangHuaZiJing.isChecked())
				row.put("COOK_BRAND", "�ϰ�");
			else if (this.CookerWanHe.isChecked())
				row.put("COOK_BRAND", "���");
			else if (this.CookerWanJiaLe.isChecked())
				row.put("COOK_BRAND", "�����");
			else if (this.CookerLinNei.isChecked())
				row.put("COOK_BRAND", "��̫");
			else if (this.CookerHaiEr.isChecked())
				row.put("COOK_BRAND", "����");
			else if (this.CookerALiSiDun.isChecked())
				row.put("COOK_BRAND", "˧��");
			else if (this.CookerYinhHua.isChecked())
				row.put("COOK_BRAND", "ӣ��");
			else if (this.CookerHuaDi.isChecked())
				row.put("COOK_BRAND", "����");
			else if (this.CookerOther.isChecked())
				row.put("COOK_BRAND", "����");
			else if (this.CookerXiMenZi.isChecked())
				row.put("COOK_BRAND", "������");
			// ����ͺ�
			row.put("COOK_TYPE", "");
			if (this.CookeTypeTabletSingle.isChecked())
				row.put("COOK_TYPE", "̨ʽ����");
			else if (this.CookerTypeTabletDouble.isChecked())
				row.put("COOK_TYPE", "̨ʽ˫��");
			else if (this.CookerTypeEmbedDouble.isChecked())
				row.put("COOK_TYPE", "��Ƕ˫��");
			// ����ʱ��
			row.put("COOK_DATE ", model.CookerBoughtTime.get());
			// ���
			row.put("COOKPIPE_NORMAL", "");
			if (this.CookerPipeNormal.isChecked())
				row.put("COOKPIPE_NORMAL", "����");
			else
				row.put("COOKPIPE_NORMAL", "������");

			if (this.CookerPipeLeakage.isChecked())
				row.put("������_2", "©��");
			if (this.CookerPipeFatigue.isChecked())
				row.put("������_3", "�ϻ�");
			if (this.CookerPipePrecaution.isChecked())
				row.put("������_4", "�а�ȫ����");
			if (this.HeaterPipePlastic.isChecked())
				row.put("������_5", "���ܹ�");

			row.put("COOKERPIPECLAMPCOUNT", model.CookerPipeClampCount.get());
			row.put("COOKERPIPYLENGTH", model.CookerPipeLength.get());

			// ��ˮ��
			row.put("WATER_BRAND", "");
			if (this.HeaterGangHuaZiJing.isChecked())
				row.put("WATER_BRAND", "����");
			else if (this.HeaterWanHe.isChecked())
				row.put("WATER_BRAND", "���");
			else if (this.HeaterWanJiaLe.isChecked())
				row.put("WATER_BRAND", "�����");
			else if (this.HeaterLinNei.isChecked())
				row.put("WATER_BRAND", "����");
			else if (this.HeaterHaiEr.isChecked())
				row.put("WATER_BRAND", "����");
//			else if (this.HeaterALiSiDun.isChecked())
//				row.put("WATER_BRAND", "����˹��");
			else if (this.HeaterYingHua.isChecked())
				row.put("WATER_BRAND", "ӣ��");
			else if (this.HeaterHuaDi.isChecked())
				row.put("WATER_BRAND", "����");
			else if (this.HeaterOther.isChecked())
				row.put("WATER_BRAND", "����");
			else if (this.HeaterShiMiSi.isChecked())
				row.put("WATER_BRAND", "ʷ��˹");
			else if (this.HeaterXiaoSongShu.isChecked())
				row.put("WATER_BRAND", "С����");
			if (model.HeaterType.get().trim().length() > 0)
				row.put("WATER_TYPE", model.HeaterType.get().trim());
			// ����ʱ��
			row.put("WATER_DATE", model.HeaterBoughtTime.get());
			// ��ˮ�����
			row.put("WATER_PIPE", "");
			if (this.HeaterPipeNormal.isChecked())
				row.put("WATER_PIPE", "����");
			else
				row.put("WATER_PIPE", "������");
			if (this.HeaterPipeLeakage.isChecked())
				row.put("��ˮ�����_2", "©��");
			if (this.HeaterPipeFatigue.isChecked())
				row.put("��ˮ�����_3", "�ϻ�");
			if (this.HeaterPipePrecaution.isChecked())
				row.put("��ˮ�����_4", "�а�ȫ����");
			if (this.HeaterPipePlastic.isChecked())
				row.put("��ˮ�����_5", "���ܹ�");
			// �����ܿ�
			row.put("WATER_NUME", model.HeaterPipeClampCount.get());
			// �̵�
			row.put("WATER_FLUE", "");
			if (this.VentilationBalanced.isChecked())
				row.put("WATER_FLUE", "ƽ��");
			else if (this.VentilationForce.isChecked())
				row.put("WATER_FLUE", "ǿ��");
			else if (this.VentilationPath.isChecked())
				row.put("WATER_FLUE", "�̵�");
			else if (this.VentilationStraight.isChecked())
				row.put("WATER_FLUE", "ֱ��");
			// ��ȫ����
			row.put("WATER_HIDDEN", "");
			if (this.HeaterPrecautionNone.isChecked())
				row.put("WATER_HIDDEN", "����");
			else
				row.put("WATER_HIDDEN", "������");

			if (this.HeaterPrecautionStraight.isChecked())
				row.put("��ˮ����ȫ����_1", "ֱ����ˮ��");
			if (this.HeaterPrecautionNoVentilation.isChecked())
				row.put("��ˮ����ȫ����_2", "δ��װ�̵�");
			if (this.HeaterPrecautionTrapped.isChecked())
				row.put("��ˮ����ȫ����_3", "�̵�δ�ŵ�����");
			if (this.HeaterPrecautionProhibited.isChecked())
				row.put("��ˮ����ȫ����_4", "�Ͻ�ʹ��");
			if (this.HeaterPrecautionInHome.isChecked())
				row.put("��ˮ����ȫ����_5", "��װ������");
			if (this.HeaterPrecautionBrokenVent.isChecked())
				row.put("��ˮ����ȫ����_6", "�̵�����");
			if (this.HeaterPrecautionWrappedVent.isChecked())
				row.put("��ˮ����ȫ����_7", "�����̵�");

			// �ڹҹ�¯
			row.put("WHE_BRAND", "");
			if (this.BoilerGangHuaZiJing.isChecked())
				row.put("WHE_BRAND", "����");
			if (this.BoilerWanHe.isChecked())
				row.put("WHE_BRAND", "���");
			if (this.BoilerWanJiaLe.isChecked())
				row.put("WHE_BRAND", "�����");
			if (this.BoilerLinNei.isChecked())
				row.put("WHE_BRAND", "����");
			if (this.BoilerHaiEr.isChecked())
				row.put("WHE_BRAND", "����");
			if (this.BoilerXiaoSongShu.isChecked())
				row.put("WHE_BRAND", "С����");
			if (this.BoilerYingHua.isChecked())
				row.put("WHE_BRAND", "ӣ��");
			if (this.BoilerHuaDi.isChecked())
				row.put("WHE_BRAND", "����");
			if (this.BoilerShiMiSi.isChecked())
				row.put("WHE_BRAND", "ʷ��˹");
			if (this.BoilerOther.isChecked())
				row.put("WHE_BRAND", "����");
			// �ͺ�û��
			if (model.BoilerType.get().trim().length() > 0)
				row.put("WHE_TYPE", model.BoilerType.get().trim());
			// ����ʱ��
			row.put("WHE_DATE", model.BoilerBoughtTime.get());

			// �ڹҹ�¯��ȫ����
			row.put("WHE_HIDDEN", "");
			if (this.BoilerPrecautionNormal.isChecked())
				row.put("WHE_HIDDEN", "����");
			else
				row.put("WHE_HIDDEN", "������");
			if (this.BoilerPrecautionInBedRoom.isChecked())
				row.put("�ڹҹ�¯��ȫ����_1", "�ڹ�¯��װ������");
			if (this.BoilerPrecautionNotified.isChecked())
				row.put("�ڹҹ�¯��ȫ����_2", "�ѷ����ڰ����֪��");

			// ��ȫ����
			if (this.PrecautionInBathRoom.isChecked())
				row.put("��ȫ����_1", "ȼ����ʩ��װ������/������");
			if (this.PrecautionInBedRoom.isChecked())
				row.put("��ȫ����_2", "ȼ���豸��װ������");
			if (this.PrecautionLongPipe.isChecked())
				row.put("��ȫ����_3", "��ܹ���");
			if (this.PrecautionElectricWire.isChecked())
				row.put("��ȫ����_4", "ȼ���ܹ���Ӵ���Դ��");
			if (this.PrecautionThroughFurniture.isChecked())
				row.put("��ȫ����_5", "��ܴ���/�Ŵ�/����");
			if (this.PrecautionThroughWall.isChecked())
				row.put("��ȫ����_6", "��ܴ�ǽ/����");
			if (this.PrecautionValidPipe.isChecked())
				row.put("��ȫ����_7", "ʹ�÷���Ȼ��ר�����");
			if (this.PrecautionWithConnector.isChecked())
				row.put("��ȫ����_8", "������н�ͷ");
			if (this.PrecautionNearFire.isChecked())
				row.put("��ȫ����_9", "������Դ̫��");
			if (this.PrecautionWrapped.isChecked())
				row.put("��ȫ����_10", "��ܰ���");
			if (this.PrecautionNotified.isChecked())
				row.put("��ȫ����_11", "�ѷ����ڰ��챨����");
			if (this.PrecautionNoClamp.isChecked())
				row.put("��ȫ����_12", "�޹ܿ�");
			if (this.PrecautionMalPosition.isChecked())
				row.put("��ȫ����_13", "ȼ���߰�װ���ڷ�λ�ò��淶");
			if (this.PrecautionNearLiquefiedGas.isChecked())
				row.put("��ȫ����_14", "��Һ��������һ��");
			if (this.PrecautionUnsafeDevice.isChecked())
				row.put("��ȫ����_15", "ʹ�÷ǰ�ȫȼ����");
			if (this.PrecautionLoosePipe.isChecked())
				row.put("��ȫ����_16", "�������û�й̶�");
			if (this.PrecautionPipeInDark.isChecked())
				row.put("��ȫ����_17", "��ܰ���");
			if (this.PrecautionPipeOutside.isChecked())
				row.put("��ȫ����_18", "��������ڻ���");
			if (this.PrecautionThreeWay.isChecked())
				row.put("��ȫ����_19", "��ܽ���ͨ");
			// �û����
			if (model.UserSuggestion.get().trim().length() > 0)
				row.put("USER_SUGGESTION", model.UserSuggestion.get().trim());
			row.put("Remark", model.Remark.get().trim());
			// �û�����
			if (this.FeebackSatisfied.isChecked())
				row.put("USER_SATISFIED", "����");
			else if (this.FeebackOK.isChecked())
				row.put("USER_SATISFIED", "��������");
			else if (this.FeebackUnsatisfied.isChecked())
				row.put("USER_SATISFIED", "������");
			// ǩ��
			if (Util.fileExists(Util.getSharedPreference(IndoorInspectActivity.this, "FileDir") + uuid
					+ "_sign.png"))
				row.put("USER_SIGN", uuid + "_sign");
			// ͼƬ
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
			//ά�����
			if(this.IsDispatchRepair.isChecked() && saveRepair)
			{
				row.put("NEEDS_REPAIR", "��");
				Spinner spinner = ((Spinner)findViewById(R.id.RepairManList));
				RepairMan repairMan = (RepairMan)model.RepairManList.get((int)spinner.getSelectedItemId());
				row.put("REPAIRMAN",repairMan.name);
				row.put("REPAIRMAN_ID", repairMan.id);
				row.put("REPAIR_STATE", "δά��" );
			}
			else
			{
				row.put("NEEDS_REPAIR", "��");
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
	 * �ӱ������ݿ��ȡ�����ֶβ����ֶθ�ֵ
	 */
	private void ReadFromDB(String id,  String inspectionTable, String precautionTable ) {
		// ��ȡ�����������ݣ���������ֶθ�ֵ
		// �����ݿ�
		try {

			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);

			Cursor c = db.rawQuery(
					"SELECT * FROM " + inspectionTable  + " where id=?",
					new String[] { id });
			while (c.moveToNext()) {
				// �ѷ����ò�����
				if (c.getString(c.getColumnIndex("HasNotified")).length() > 0)
					model.HasNotified.set(true);

				// ����ʱ��
				String dt = c.getString(c.getColumnIndex("ARRIVAL_TIME"));
				model.InspectionDate.set(dt.substring(0, 10));
				model.ArrivalTime.set(dt.substring(dt.length()-8, dt.length()));
				// �뿪ʱ��
				dt = c.getString(c.getColumnIndex("DEPARTURE_TIME"));
				String stopAt = dt.substring(dt.length()-8, dt.length());
				model.DepartureTime.set(stopAt);
				//�����������ǽ���ʱ
				if(inspectionTable.equals("T_INSPECTION"))
				{
					((MyDigitalClock)this.findViewById(R.id.digitalClock)).stopAt(stopAt);
				}
				// С����ַ
				model.ResidentialAreaAddress.set(c.getString(c
						.getColumnIndex("ROAD")));
				// ¥��
				model.BuildingNo.set(c.getString(c.getColumnIndex("CUS_DOM")));
				// ��Ԫ
				model.UnitNo.set(c.getString(c.getColumnIndex("CUS_DY")));
				// ¥��
				model.LevelNo.set(c.getString(c.getColumnIndex("CUS_FLOOR")));
				// ����
				model.RoomNo.set(c.getString(c.getColumnIndex("CUS_ROOM")));
				// ������
				if (c.getString(c.getColumnIndex("CONDITION")).equals("����")) {
					model.IsNoAnswer.set(true);
					if(inspectionTable.equals("T_INSPECTION"))
					{
						db.close();
						return;
					}
				} else if (c.getString(c.getColumnIndex("CONDITION")).equals(
						"�ܾ�")) {
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
					// ��������
					//20150108shע��1
					model.UserName.set(c.getString(c.getColumnIndex("USER_NAME")));
					// �绰
					//20150108shע��2
					model.Telephone.set(c.getString(c.getColumnIndex("TELPHONE")));
					//ǩ���˵绰
					model.SignTelephone.set(c.getString(c.getColumnIndex("SIGNTELEPHONE")));
					// С������
					model.ResidentialAreaName.set(c.getString(c
							.getColumnIndex("UNIT_NAME")));
					// �û�������ַ
					//20150108shע��3
					model.ArchiveAddress.set(c.getString(c.getColumnIndex("OLD_ADDRESS")));
					// IC����
					//20150108shע��
					model.ICCardNo.set(c.getString(c.getColumnIndex("CARD_ID")));
					//�ۼƹ�����
					if(c.getString(c.getColumnIndex("buy_gas_quantity")) != null )
						model.BuyGasQuantity.set(c.getString(c.getColumnIndex("buy_gas_quantity")));
					else
						model.BuyGasQuantity.set("");

					this.status = false;
				}
				
				// ���ݽṹ
				String roomStruct = c.getString(c.getColumnIndex("ROOM_STRUCTURE"));
				if(roomStruct == null)
					roomStruct = "";
				Spinner spinnerStructureTypeList = (Spinner) this
						.findViewById(R.id.StructureTypeList);
				if (roomStruct.equals("ƽ��"))
					spinnerStructureTypeList.setSelection(4);
				else if (roomStruct.equals("���"))
					spinnerStructureTypeList.setSelection(1);
				else if (roomStruct.equals("С�߲�"))
					spinnerStructureTypeList.setSelection(2);
				else if (roomStruct.equals("�߲�"))
					spinnerStructureTypeList.setSelection(0);
				else if (roomStruct.equals("����"))
					spinnerStructureTypeList.setSelection(3);

				// ��ů��ʽ
				String warm = c.getString(c.getColumnIndex("WARM"));
				if(warm == null)
					warm = "";
				Spinner spinnerHeatedTypeList = (Spinner) this
						.findViewById(R.id.HeatedTypeList);
				if (warm.equals("������˾���й�ů"))
					spinnerHeatedTypeList.setSelection(0);
				else if (warm.equals("С�����й�ů"))
					spinnerHeatedTypeList.setSelection(1);
				else if (warm.equals("�ͻ����й�ů"))
					spinnerHeatedTypeList.setSelection(2);
				else if (warm.equals("������ů")) {
					spinnerHeatedTypeList.setSelection(3);
				} else {
					spinnerHeatedTypeList.setSelection(3);
					model.OtherHeatedType.set(warm);
				}

				// �������ͺ�
				String jb_menter_name = c.getString(c
						.getColumnIndex("JB_METER_NAME"));
				if(jb_menter_name == null)
					jb_menter_name = "";
				if (jb_menter_name.equals("����")) {
					this.MeterMakerDanDong.setChecked(true);
				} else if (jb_menter_name.equals("�ؼ�")) {
					this.MeterMakerChongJian.setChecked(true);
				} else if (jb_menter_name.equals("����")) {
					this.MeterMakerSaiFu.setChecked(true);
				} else if (jb_menter_name.equals("��ǰ")) {
					this.MeterMakerChongQian.setChecked(true);
				} else if (jb_menter_name.equals("ɽ��")) {
					this.MeterMakerShanCheng.setChecked(true);
				} else if (jb_menter_name.equals("����Զ���")) {
					this.MeterMakerTianJinZiDongHua.setChecked(true);
				} else {
					this.MeterMakerOtherBox.setChecked(true);
					model.MeterMakerOther.set(jb_menter_name);
				}

				// IC�������ͺ�
				String ic_menter_name = c.getString(c
						.getColumnIndex("IC_METER_NAME"));
				if(ic_menter_name == null)
					ic_menter_name = "";
				if (ic_menter_name.equals("��")) {
					this.ICMeterMakerHuaJie.setChecked(true);
				} else if (ic_menter_name.equals("�ȷ�")) {
					this.ICMeterMakerSaiFu.setChecked(true);
				} 
//				else if (ic_menter_name.equals("�ش�")) {
//					this.ICMeterMakerQinChuan.setChecked(true);
//				} else if (ic_menter_name.equals("�ظ�")) {
//					this.ICMeterMakerQinGang.setChecked(true);
//				} else if (ic_menter_name.equals("����")) {
//					this.ICMeterMakerZhiLi.setChecked(true);
//				} 
				else {
					this.ICMeterMakerOtherBox.setChecked(true);
					model.ICMeterMakerOther.set(ic_menter_name);
				}

				// ����
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
				}else if (menter_type.equals("������")) {
					this.MeterTypeOther.setChecked(true);
				}

				
				// ������
				if(c.getString(c.getColumnIndex("JB_NUMBER")) != null )
					model.BaseMeterQuantity.set(c.getString(c.getColumnIndex("JB_NUMBER")));
				else
					model.BaseMeterQuantity.set("");
				// ����ʣ������
				if(c.getString(c.getColumnIndex("SURPLUS_GAS")) != null )
					model.RemainGasQuantity.set(c.getString(c.getColumnIndex("SURPLUS_GAS")));
				else
					model.RemainGasQuantity.set("");
				//������
				if(c.getString(c.getColumnIndex("gas_quantity")) != null )
					model.GasQuantity.set(c.getString(c.getColumnIndex("gas_quantity")));
				else
					model.GasQuantity.set("");

				// ȼ�������ұ�
				if(c.getString(c.getColumnIndex("RQB_AROUND")) != null)
				{
					if (c.getString(c.getColumnIndex("RQB_AROUND")).equals("���")) {
						this.MeterOnTheLeft.setChecked(true);
					} else {
						this.MeterOnTheRight.setChecked(true);
					}
				}

				// ȼ��������
				if (c.getString(c.getColumnIndex("RQB_JBCODE")) != null) {
					model.BaseMeterID.set(c.getString(c
							.getColumnIndex("RQB_JBCODE")));
				}
				else
					model.BaseMeterID.set("");

				// ȼ�����������
				if (c.getString(c.getColumnIndex("METERMADEYEAR")) != null) {
					model.MeterMadeYear.set(c.getString(c.getColumnIndex("METERMADEYEAR")));
				}
				else
					model.MeterMadeYear.set("");


				// ȼ������Ϣ
				if(c.getString(c.getColumnIndex("RQB")) !=  null)
				if (c.getString(c.getColumnIndex("RQB")).equals("����")) {
					this.MeterNormal.setChecked(true);
				}

				// ����
				if(c.getString(c.getColumnIndex("STANDPIPE")) !=  null)
				if (c.getString(c.getColumnIndex("STANDPIPE")).equals("����")) {
					this.PlumNormal.setChecked(true);
				}

				// �����Բ���
				if (c.getString(c.getColumnIndex("RIGIDITY")) !=  null && c.getString(c.getColumnIndex("RIGIDITY")).equals("����")) {
					this.PlumProofNormal.setChecked(true);
				} else if (c.getString(c.getColumnIndex("RIGIDITY")) !=  null && c.getString(c.getColumnIndex("RIGIDITY")).equals("©��")){
					this.PlumProofLeakage.setChecked(true);
				}

				// ��ֹѹ��
				if (c.getString(c.getColumnIndex("STATIC")) !=  null && c.getString(c.getColumnIndex("STATIC")).equals("����")) {
					this.PlumPressureNormal.setChecked(true);
				} else if (c.getString(c.getColumnIndex("STATIC")) !=  null && c.getString(c.getColumnIndex("STATIC")).equals("������")){
					this.PlumPressureAbnormal.setChecked(true);
				}
				// ��ֹѹ��ֵ
				if(c.getString(c.getColumnIndex("STATIC_DATA")) !=  null)
				model.PlumPressure.set(c.getString(c
						.getColumnIndex("STATIC_DATA")));

				// ��ǰ��
				if(c.getString(c.getColumnIndex("TABLE_TAP")) !=  null)
				if (c.getString(c.getColumnIndex("TABLE_TAP")).equals("����")) {
					this.MeterValveNormal.setChecked(true);
				}
				// ��ǰ��
				if(c.getString(c.getColumnIndex("COOK_TAP")) !=  null)
				if (c.getString(c.getColumnIndex("COOK_TAP")).equals("����")) {
					this.CookerValveNormal.setChecked(true);
				}
				// �Աշ�
				if(c.getString(c.getColumnIndex("CLOSE_TAP")) !=  null)
				if (c.getString(c.getColumnIndex("CLOSE_TAP")).equals("����")) {
					this.AutoValveNormal.setChecked(true);
				}
				// ���ڹ�
				if(c.getString(c.getColumnIndex("INDOOR")) !=  null)
				if (c.getString(c.getColumnIndex("INDOOR")).equals("����")) {
					this.HomePlumNormal.setChecked(true);
				}

				// ���©��
				if ((c.getString(c.getColumnIndex("LEAKAGE_COOKER")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_COOKER"))
								.equals("���©��"))) {
					this.LeakageCooker.setChecked(true);
				}

				// ��ˮ��©��
				if ((c.getString(c.getColumnIndex("LEAKAGE_HEATER")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_HEATER"))
								.equals("��ˮ��©��"))) {
					this.LeakageHeater.setChecked(true);
				}
				// �ڹ�¯©��
				if ((c.getString(c.getColumnIndex("LEAKAGE_BOILER")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_BOILER"))
								.equals("�ڹ�¯©��"))) {
					this.LeakageBoiler.setChecked(true);
				}

				// �����֪
				if ((c.getString(c.getColumnIndex("LEAKAGE_NOTIFIED")) != null)
						&& (c.getString(c.getColumnIndex("LEAKAGE_NOTIFIED"))
								.equals("�����֪"))) {
					this.LeakageNotified.setChecked(true);
				}

				// ©��λ��
				if (c.getString(c.getColumnIndex("LEAKGEPLACE")) != null)
				model.LeakagePlace.set(c.getString(c
						.getColumnIndex("LEAKGEPLACE")));

				// ���Ʒ��
				String cook_brand = c.getString(c.getColumnIndex("COOK_BRAND"));
				if(cook_brand == null)
					cook_brand = "����";
				if (cook_brand.equals("�ϰ�")) {
					this.CookerGangHuaZiJing.setChecked(true);
				} else if (cook_brand.equals("���")) {
					this.CookerWanHe.setChecked(true);
				} else if (cook_brand.equals("�����")) {
					this.CookerWanJiaLe.setChecked(true);
				} else if (cook_brand.equals("��̫")) {
					this.CookerLinNei.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerHaiEr.setChecked(true);
				} else if (cook_brand.equals("˧��")) {
					this.CookerALiSiDun.setChecked(true);
				} else if (cook_brand.equals("ӣ��")) {
					this.CookerYinhHua.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerHuaDi.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerOther.setChecked(true);
				} else if (cook_brand.equals("������")) {
					this.CookerXiMenZi.setChecked(true);
				}

				// ����ͺ�
				String cook_type = c.getString(c.getColumnIndex("COOK_TYPE"));
				if(cook_type == null)
					cook_type = "";
				if (cook_type.equals("̨ʽ����")) {
					this.CookeTypeTabletSingle.setChecked(true);
				} else if (cook_type.equals("̨ʽ˫��")) {
					this.CookerTypeTabletDouble.setChecked(true);
				} else if (cook_type.equals("��Ƕ˫��")) {
					this.CookerTypeEmbedDouble.setChecked(true);
				}

				// ��װ�ܿ�
				model.CookerPipeClampCount.set(c.getString(c
						.getColumnIndex("COOKERPIPECLAMPCOUNT")));
				// �������
				model.CookerPipeLength.set(c.getString(c
						.getColumnIndex("COOKERPIPYLENGTH")));
				// ������
				if(c.getString(c.getColumnIndex("COOKPIPE_NORMAL")) != null)
				{
					if (c.getString(c.getColumnIndex("COOKPIPE_NORMAL")).equals(
							"����")) {
						this.CookerPipeNormal.setChecked(true);
					}
				}
				// ��߹�������
				model.CookerBoughtTime.set(c.getString(c
						.getColumnIndex("COOK_DATE")));

				// ��ˮ��Ʒ��
				String water_brand = c.getString(c
						.getColumnIndex("WATER_BRAND"));
				if(water_brand == null)
					water_brand = "";
				if (water_brand.equals("����")) {
					this.HeaterGangHuaZiJing.setChecked(true);
				} else if (water_brand.equals("���")) {
					this.HeaterWanHe.setChecked(true);
				} else if (water_brand.equals("�����")) {
					this.HeaterWanJiaLe.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterLinNei.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterHaiEr.setChecked(true);
//				} else if (water_brand.equals("����˹��")) {
//					this.HeaterALiSiDun.setChecked(true);
				} else if (water_brand.equals("ӣ��")) {
					this.HeaterYingHua.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterHuaDi.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterOther.setChecked(true);
				} else if (water_brand.equals("ʷ��˹")) {
					this.HeaterShiMiSi.setChecked(true);
				} else if (water_brand.equals("С����")) {
					this.HeaterXiaoSongShu.setChecked(true);
				}

				// ��ˮ���ͺ�
				if(c.getString(c.getColumnIndex("WATER_TYPE")) != null)
					model.HeaterType.set(c.getString(c.getColumnIndex("WATER_TYPE")));
				else
					model.HeaterType.set("");

				// ��ˮ���̵�
				if(c.getString(c.getColumnIndex("WATER_FLUE")) != null)
				{
					if (c.getString(c.getColumnIndex("WATER_FLUE")).equals("ƽ��")) {
						this.VentilationBalanced.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"ǿ��")) {
						this.VentilationForce.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"�̵�")) {
						this.VentilationPath.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WATER_FLUE")).equals(
							"ֱ��")) {
						this.VentilationStraight.setChecked(true);
					}
				}

				// ��ˮ�����
				if (c.getString(c.getColumnIndex("WATER_PIPE")) != null)
				{
					if (c.getString(c.getColumnIndex("WATER_PIPE")).equals("����")) {
						this.HeaterPipeNormal.setChecked(true);
					}
				}
				// ��ˮ����װʱ��
				model.HeaterBoughtTime.set(c.getString(c
						.getColumnIndex("WATER_DATE")));
				// �����ܿ�
				model.HeaterPipeClampCount.set(c.getString(c
						.getColumnIndex("WATER_NUME")));
				// ��ˮ������
				String str = c.getString(c.getColumnIndex("WATER_HIDDEN"));
				if(str != null && str.equals("����")) {
					this.HeaterPrecautionNone.setChecked(true);
				}

				// �ڹ�¯Ʒ��
				if(c.getString(c.getColumnIndex("WHE_BRAND")) != null)
				{
					if (c.getString(c.getColumnIndex("WHE_BRAND")).equals("����")) {
						this.BoilerGangHuaZiJing.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"���")) {
						this.BoilerWanHe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"�����")) {
						this.BoilerWanJiaLe.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerLinNei.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerHaiEr.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"С����")) {
						this.BoilerXiaoSongShu.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"ӣ��")) {
						this.BoilerYingHua.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerHuaDi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"ʷ��˹")) {
						this.BoilerShiMiSi.setChecked(true);
					} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
							"����")) {
						this.BoilerOther.setChecked(true);
					}
				}

				// �ڹ�¯�ͺ�
				if(c.getString(c.getColumnIndex("WHE_TYPE")) != null)
					model.BoilerType.set(c.getString(c.getColumnIndex("WHE_TYPE")));
				else 
					model.BoilerType.set("");
				// ����ʱ�䣨�ڹҹ�¯��
				model.BoilerBoughtTime.set(c.getString(c	.getColumnIndex("WHE_DATE")));

				// �ڹҹ�¯����
				if(c.getString(c.getColumnIndex("WHE_HIDDEN")) != null)
				if (c.getString(c.getColumnIndex("WHE_HIDDEN")).equals("����")) {
					this.BoilerPrecautionNormal.setChecked(true);
				}

				// �û����
				if(c.getString(c.getColumnIndex("USER_SUGGESTION")) != null)
					model.UserSuggestion.set(c.getString(c.getColumnIndex("USER_SUGGESTION")));
				else 
					model.UserSuggestion.set("");
				//��ע
				if(c.getString(c.getColumnIndex("Remark")) != null)
					model.Remark.set(c.getString(c.getColumnIndex("Remark")));
				else 
					model.Remark.set("");
				// �û�����
				if(c.getString(c.getColumnIndex("USER_SATISFIED")) == null)
						this.FeebackSatisfied.setChecked(true);
				else
				{
					if (c.getString(c.getColumnIndex("USER_SATISFIED"))
							.equals("����")) {
						this.FeebackSatisfied.setChecked(true);
					} else if (c.getString(c.getColumnIndex("USER_SATISFIED"))
							.equals("��������")) {
						this.FeebackOK.setChecked(true);
					} else if (c.getString(c.getColumnIndex("USER_SATISFIED"))
							.equals("������")) {
						this.FeebackUnsatisfied.setChecked(true);
					}
				}

				// ��Ƭ
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
					Toast.makeText(this, "��ȡͼƬʧ�ܡ����� " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}

			// �ӱ��ѯ
			c = db.rawQuery("SELECT * FROM " + precautionTable
					+ " where id=?", new String[] { id });

			while (c.moveToNext()) {
				String device = c.getString(c.getColumnIndex("EQUIPMENT"));
				String box = c.getString(c.getColumnIndex("CONTENT"));

				// ȼ����
				if (device.equals("ȼ����")) {
					if (box.equals("����")) {
						this.MeterWrapped.setChecked(true);
					} else if (box.equals("©��")) {
						this.MeterLeakage.setChecked(true);
					} else if (box.equals("��ͨ��")) {
						this.MeterFallThrough.setChecked(true);
					} else if (box.equals("����")) {
						this.MeterDead.setChecked(true);
					} else if (box.equals("������")) {
						this.MeterByPass.setChecked(true);
					} else if (box.equals("����")) {
						this.MeterOther.setChecked(true);
					}
				}

				// ����
				else if (device.equals("����")) {
					if (box.equals("����")) {
						this.PlumWrapped.setChecked(true);
					} else if (box.equals("����")) {
						this.PlumErosionSevere.setChecked(true);
						this.PlumEroded.setChecked(true);
					} else if (box.equals("�ж�")) {
						this.PlumErosionModerate.setChecked(true);
						this.PlumEroded.setChecked(true);
					} else if (box.equals("��΢")) {
						this.PlumErosionSlight.setChecked(true);
						this.PlumEroded.setChecked(true);
					} else if (box.equals("©��")) {
						this.PlumLeakage.setChecked(true);
					} else if (box.equals("����")) {
						this.PlumOther.setChecked(true);
					}
				}

				// ��ǰ��
				else if (device.equals("���ű�ǰ��")) {
					if (box.equals("��©")) {
						this.MeterValveInnerLeakage.setChecked(true);
					} else if (box.equals("©��")) {
						this.MeterValveLeakage.setChecked(true);
					} else if (box.equals("��")) {
						this.MeterValveBall.setChecked(true);
					} else if (box.equals("����")) {
						this.MeterValvePlug.setChecked(true);
					} else if (box.equals("����")) {
						this.MeterValveWrapped.setChecked(true);
					}
				}

				// ��ǰ��
				else if (device.equals("������ǰ��")) {
					if (box.equals("��©")) {
						this.CookerValveInnerLeakage.setChecked(true);
					} else if (box.equals("©��")) {
						this.CookerValveLeakage.setChecked(true);
					} else if (box.equals("����")) {
						this.CookerValveWrapped.setChecked(true);
					} else if (box.equals("��װ����")) {
						this.CookerValveTooHigh.setChecked(true);
					}
				}

				// �Աշ�
				else if (device.equals("�����Աշ�")) {
					if (box.equals("��©")) {
						this.AutoValveInnerLeakage.setChecked(true);
					} else if (box.equals("©��")) {
						this.AutoValveLeakage.setChecked(true);
					} else if (box.equals("ʧ��")) {
						this.AutoValveNotWork.setChecked(true);
					} else if (box.equals("����")) {
						this.AutoValveWrapped.setChecked(true);
					}
				}

				// ���ڹ�
				else if (device.equals("���ڹ�")) {
					if (box.equals("©��")) {
						this.HomePlumLeakage.setChecked(true);
					} else if (box.equals("������")) {
						this.HomePlumThroughSittingRoom.setChecked(true);
					} else if (box.equals("������")) {
						this.HomePlumThroughBedRoom.setChecked(true);
					} else if (box.equals("���������")) {
						this.HomePlumNearAppliance.setChecked(true);
					} else if (box.equals("����")) {
						this.HomePlumWrapped.setChecked(true);
					} else if (box.equals("˽��")) {
						this.HomePlumModified.setChecked(true);
					} else if (box.equals("��������")) {
						this.HomePlumOtherUse.setChecked(true);
					}
				}

				// ������
				else if (device.equals("������")) {
					if (box.equals("©��")) {
						this.CookerPipeLeakage.setChecked(true);
					} else if (box.equals("�ϻ�")) {
						this.CookerPipeFatigue.setChecked(true);
					} else if (box.equals("�а�ȫ����")) {
						this.CookerPipePrecaution.setChecked(true);
					}
				}

				// ��ˮ�����
				else if (device.equals("��ˮ�����")) {
					if (box.equals("©��")) {
						this.HeaterPipeLeakage.setChecked(true);
					} else if (box.equals("�ϻ�")) {
						this.HeaterPipeFatigue.setChecked(true);
					} else if (box.equals("�а�ȫ����")) {
						this.HeaterPipePrecaution.setChecked(true);
					} else if (box.equals("���ܹ�")) {
						this.HeaterPipePlastic.setChecked(true);
					}
				}

				// ��ˮ������
				else if (device.equals("��ˮ����ȫ����")) {
					if (box.equals("ֱ����ˮ��")) {
						this.HeaterPrecautionStraight.setChecked(true);
					} else if (box.equals("δ��װ�̵�")) {
						this.HeaterPrecautionNoVentilation.setChecked(true);
					} else if (box.equals("�̵�δ�ŵ�����")) {
						this.HeaterPrecautionTrapped.setChecked(true);
					} else if (box.equals("�Ͻ�ʹ��")) {
						this.HeaterPrecautionProhibited.setChecked(true);
					} else if (box.equals("��װ������")) {
						this.HeaterPrecautionInHome.setChecked(true);
					} else if (box.equals("�̵�����")) {
						this.HeaterPrecautionBrokenVent.setChecked(true);
					} else if (box.equals("�����̵�")) {
						this.HeaterPrecautionWrappedVent.setChecked(true);
					}
				}

				// �ڹҹ�¯����
				else if (device.equals("�ڹҹ�¯��ȫ����")) {
					if (box.equals("�ڹ�¯��װ������")) {
						this.BoilerPrecautionInBedRoom.setChecked(true);
					} else if (box.equals("�ѷ����ڰ����֪��")) {
						this.BoilerPrecautionNotified.setChecked(true);
					}
				}

				// ��ȫ����
				else if (device.equals("��ȫ����")) {
					if (box.equals("ȼ����ʩ��װ������/������")) {
						this.PrecautionInBathRoom.setChecked(true);
					} else if (box.equals("ȼ���豸��װ������")) {
						this.PrecautionInBedRoom.setChecked(true);
					} else if (box.equals("��ܹ���")) {
						this.PrecautionLongPipe.setChecked(true);
					} else if (box.equals("ȼ���ܹ���Ӵ���Դ��")) {
						this.PrecautionElectricWire.setChecked(true);
					} else if (box.equals("��ܽ���ͨ")) {
						this.PrecautionThreeWay.setChecked(true);
					} else if (box.equals("��ܴ���/�Ŵ�/����")) {
						this.PrecautionThroughFurniture.setChecked(true);
					} else if (box.equals("��ܴ�ǽ/����")) {
						this.PrecautionThroughWall.setChecked(true);
					} else if (box.equals("ʹ�÷���Ȼ��ר�����")) {
						this.PrecautionValidPipe.setChecked(true);
					} else if (box.equals("������н�ͷ")) {
						this.PrecautionWithConnector.setChecked(true);
					} else if (box.equals("������Դ̫��")) {
						this.PrecautionNearFire.setChecked(true);
					} else if (box.equals("��ܰ���")) {
						this.PrecautionWrapped.setChecked(true);
					} else if (box.equals("�ѷ����ڰ��챨����")) {
						this.PrecautionNotified.setChecked(true);
					} else if (box.equals("�޹ܿ�")) {
						this.PrecautionNoClamp.setChecked(true);
					} else if (box.equals("ʹ�÷ǰ�ȫȼ����")) {
						this.PrecautionUnsafeDevice.setChecked(true);
					} else if (box.equals("�������û�й̶�")) {
						this.PrecautionLoosePipe.setChecked(true);
					} else if (box.equals("��ܰ���")) {
						this.PrecautionPipeInDark.setChecked(true);
					} else if (box.equals("��������ڻ���")) {
						this.PrecautionPipeOutside.setChecked(true);
					} else if (box.equals("ȼ���߰�װ���ڷ�λ�ò��淶")) {
						this.PrecautionMalPosition.setChecked(true);
					} else if (box.equals("��Һ��������һ��")) {
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
	 * ��ֹ�����ˡ��ܷá����͵��ò�����ѡ��
	 */
	private void DisableOtherCondition() {
		findViewById(R.id.noAnswerSwitch).setEnabled(false);
		findViewById(R.id.DenialSwitch).setEnabled(false);
		findViewById(R.id.chkHasNotified).setEnabled(false);
	}

	/**
	 * �Ӳ����ҵ����еĿؼ�
	 */
	private void InitControls() {
		//!!!!��ʱ   @@@@@@@ά��
		this.IsDispatchRepair = (CheckBox)findViewById(R.id.IsDispatchRepair);
		((Spinner)findViewById(R.id.RepairManList)).setEnabled(false);
		this.IsDispatchRepair.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((Spinner)findViewById(R.id.RepairManList)).setEnabled(isChecked);
			}
		});
		//!!!!!!!!@@@@@@@@@@@
		// ���
		MeterOnTheLeft = (RadioButton) findViewById(R.id.MeterOnTheLeft);
		// �ұ�
		MeterOnTheRight = (RadioButton) findViewById(R.id.MeterOnTheRight);
		// ��������
		MeterNormal = (CheckBox) findViewById(R.id.MeterNormal);
		// ��������
		MeterWrapped = (CheckBox) findViewById(R.id.MeterWrapped);
		// ����©��
		MeterLeakage = (CheckBox) findViewById(R.id.MeterLeakage);
		// ��ͨ��
		MeterFallThrough = (CheckBox) findViewById(R.id.MeterFallThrough);
		// ����
		MeterDead = (CheckBox) findViewById(R.id.MeterDead);
		// ������
		MeterByPass = (CheckBox) findViewById(R.id.MeterByPass);
		// ��-����
		MeterOther = (CheckBox) findViewById(R.id.MeterOther);
		// �������ͺ� ����
		MeterMakerDanDong = (RadioButton) findViewById(R.id.MeterMakerDanDong);
		// �������ͺ� �ؼ�
		MeterMakerChongJian = (RadioButton) findViewById(R.id.MeterMakerChongJian);
		// �������ͺ� ����
		MeterMakerSaiFu = (RadioButton) findViewById(R.id.MeterMakerSaiFu);
		// �������ͺ� ��ǰ
		MeterMakerChongQian = (RadioButton) findViewById(R.id.MeterMakerChongQian);
		// �������ͺ� ɽ��
		MeterMakerShanCheng = (RadioButton) findViewById(R.id.MeterMakerShanCheng);
		// �������ͺ� ����Զ���
		MeterMakerTianJinZiDongHua = (RadioButton) findViewById(R.id.MeterMakerTianJinZiDongHua);
		//���������ͺ�����
		MeterMakerOtherBox = (RadioButton) findViewById(R.id.MeterMakerOtherBox);
		// IC�������ͺ� ����
		ICMeterMakerHuaJie = (RadioButton) findViewById(R.id.ICMeterMakerHuaJie);
		// IC�������ͺ� ����
		ICMeterMakerSaiFu = (RadioButton) findViewById(R.id.ICMeterMakerSaiFu);
		// IC�������ͺ� �ش�
		ICMeterMakerQinChuan = (RadioButton) findViewById(R.id.ICMeterMakerQinChuan);
		// IC�������ͺ� �ظ�
		ICMeterMakerQinGang = (RadioButton) findViewById(R.id.ICMeterMakerQinGang);
		// IC�������ͺ� ����
		ICMeterMakerZhiLi = (RadioButton) findViewById(R.id.ICMeterMakerZhiLi);
		//IC�������ͺ�����
		ICMeterMakerOtherBox = (RadioButton) findViewById(R.id.ICMeterMakerOtherBox);
		
		// ���� G2.5
		MeterTypeG25 = (RadioButton) findViewById(R.id.MeterTypeG25);
		// ���� G4
		MeterTypeG4 = (RadioButton) findViewById(R.id.MeterTypeG4);
		// ���� G6
		MeterTypeG6 = (RadioButton) findViewById(R.id.MeterTypeG6);
		// ���� G10
		MeterTypeG10 = (RadioButton) findViewById(R.id.MeterTypeG10);
		// ���� G16
		MeterTypeG16 = (RadioButton) findViewById(R.id.MeterTypeG16);
		// ���� G25
		MeterTypeg25 = (RadioButton) findViewById(R.id.MeterTypeg25);
		// ���� G40
		MeterTypeG40 = (RadioButton) findViewById(R.id.MeterTypeG40);
		// ���� ������
		MeterTypeOther = (RadioButton) findViewById(R.id.MeterTypeOther);
		// ------------------------------����-------------------------
		// ��������
		PlumNormal = (CheckBox) findViewById(R.id.PlumNormal);
		// ���ܰ���
		PlumWrapped = (CheckBox) findViewById(R.id.PlumWrapped);
		//���ܸ�ʴ
		PlumEroded = (CheckBox)findViewById(R.id.PlumEroded);
		// ���ܸ�ʴ
		PlumErosionSevere = (RadioButton) findViewById(R.id.PlumErosionSevere);
		PlumErosionModerate = (RadioButton) findViewById(R.id.PlumErosionModerate);
		PlumErosionSlight = (RadioButton) findViewById(R.id.PlumErosionSlight);
		// ����©��
		PlumLeakage = (CheckBox) findViewById(R.id.PlumLeakage);
		// ��������
		PlumOther = (CheckBox) findViewById(R.id.PlumOther);
		// ���������� ���� ©��
		PlumProofNormal = (CheckBox) findViewById(R.id.PlumProofNormal);
		PlumProofLeakage = (CheckBox) findViewById(R.id.PlumProofLeakage);
		// ���ܾ�ֹѹ�� ���� ©��
		PlumPressureNormal = (CheckBox) findViewById(R.id.PlumPressureNormal);
		PlumPressureAbnormal = (CheckBox) findViewById(R.id.PlumPressureAbnormal);
		// ��ǰ�� ��������©��©�����򷧡�������������
		MeterValveNormal = (CheckBox) findViewById(R.id.MeterValveNormal);
		MeterValveInnerLeakage = (CheckBox) findViewById(R.id.MeterValveInnerLeakage);
		MeterValveLeakage = (CheckBox) findViewById(R.id.MeterValveLeakage);
		MeterValveBall = (CheckBox) findViewById(R.id.MeterValveBall);
		MeterValvePlug = (CheckBox) findViewById(R.id.MeterValvePlug);
		MeterValveWrapped = (CheckBox) findViewById(R.id.MeterValveWrapped);
		// ��ǰ�� ��������©��©������������װ����
		CookerValveNormal = (CheckBox) findViewById(R.id.CookerValveNormal);
		CookerValveInnerLeakage = (CheckBox) findViewById(R.id.CookerValveInnerLeakage);
		CookerValveLeakage = (CheckBox) findViewById(R.id.CookerValveLeakage);
		CookerValveWrapped = (CheckBox) findViewById(R.id.CookerValveWrapped);
		CookerValveTooHigh = (CheckBox) findViewById(R.id.CookerValveTooHigh);
		// �Աշ� ��������©��©����������ʧ��
		AutoValveNormal = (CheckBox) findViewById(R.id.AutoValveNormal);
		AutoValveInnerLeakage = (CheckBox) findViewById(R.id.AutoValveInnerLeakage);
		AutoValveLeakage = (CheckBox) findViewById(R.id.AutoValveLeakage);
		AutoValveNotWork = (CheckBox) findViewById(R.id.AutoValveNotWork);
		AutoValveWrapped = (CheckBox) findViewById(R.id.AutoValveWrapped);
		// ©�������©������ˮ��©�����ڹҹ�¯©�����ѷ��Ű�ȫ��֪��
		LeakageCooker = (CheckBox) findViewById(R.id.LeakageCooker);
		LeakageHeater = (CheckBox) findViewById(R.id.LeakageHeater);
		LeakageBoiler = (CheckBox) findViewById(R.id.LeakageBoiler);
		LeakageNotified = (CheckBox) findViewById(R.id.LeakageNotified);
		// ----------------------------------------���------------------------
		// ��� �ۻ��Ͼ� ��� ����� ���� ���� ����˹�� ӣ�� ���� ����
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

		// ������� ̨ʽ���� ̨ʽ˫�� ��Ƕ˫��
		CookeTypeTabletSingle = (RadioButton) findViewById(R.id.CookeTypeTabletSingle);
		CookerTypeTabletDouble = (RadioButton) findViewById(R.id.CookerTypeTabletDouble);
		CookerTypeEmbedDouble = (RadioButton) findViewById(R.id.CookerTypeEmbedDouble);

		// ������ ���� ©�� �ϻ� �а�ȫ����
		CookerPipeNormal = (CheckBox) findViewById(R.id.CookerPipeNormal);
		CookerPipeLeakage = (CheckBox) findViewById(R.id.CookerPipeLeakage);
		CookerPipeFatigue = (CheckBox) findViewById(R.id.CookerPipeFatigue);
		CookerPipePrecaution = (CheckBox) findViewById(R.id.CookerPipePrecaution);

		// ��ˮ��Ʒ��
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
		// ��ˮ�����
		HeaterPipeNormal = (CheckBox) findViewById(R.id.HeaterPipeNormal);
		HeaterPipeLeakage = (CheckBox) findViewById(R.id.HeaterPipeLeakage);
		HeaterPipeFatigue = (CheckBox) findViewById(R.id.HeaterPipeFatigue);
		HeaterPipePrecaution = (CheckBox) findViewById(R.id.HeaterPipePrecaution);
		HeaterPipePlastic = (CheckBox) findViewById(R.id.HeaterPipePlastic);
		// ��ˮ���̵�
		VentilationBalanced = (RadioButton) findViewById(R.id.VentilationBalanced); // ƽ��
		VentilationForce = (RadioButton) findViewById(R.id.VentilationForce); // ǿ��
		VentilationPath = (RadioButton) findViewById(R.id.VentilationPath); // �̵�
		VentilationStraight = (RadioButton) findViewById(R.id.VentilationStraight); // ֱ��
		// ��ˮ������
		HeaterPrecautionNone = (CheckBox) findViewById(R.id.HeaterPrecautionNone); // ��
		HeaterPrecautionStraight = (CheckBox) findViewById(R.id.HeaterPrecautionStraight); // ֱ����ˮ��
		HeaterPrecautionNoVentilation = (CheckBox) findViewById(R.id.HeaterPrecautionNoVentilation); // δ��װ�̵�
		HeaterPrecautionTrapped = (CheckBox) findViewById(R.id.HeaterPrecautionTrapped); // �̵�δ�ӵ�����
		HeaterPrecautionProhibited = (CheckBox) findViewById(R.id.HeaterPrecautionProhibited); // �Ͻ�ʹ��
		HeaterPrecautionInHome = (CheckBox) findViewById(R.id.HeaterPrecautionInHome); // ��װ������
		HeaterPrecautionBrokenVent = (CheckBox) findViewById(R.id.HeaterPrecautionBrokenVent); // �̵�����
		HeaterPrecautionWrappedVent = (CheckBox) findViewById(R.id.HeaterPrecautionWrappedVent); // �̵�����
		// �ڹҹ�¯
		BoilerGangHuaZiJing = (RadioButton) findViewById(R.id.BoilerGangHuaZiJing); // �ۻ��Ͼ�
		BoilerWanHe = (RadioButton) findViewById(R.id.BoilerWanHe); // ���
		BoilerWanJiaLe = (RadioButton) findViewById(R.id.BoilerWanJiaLe); // �����
		BoilerLinNei = (RadioButton) findViewById(R.id.BoilerLinNei); // ����
		BoilerHaiEr = (RadioButton) findViewById(R.id.BoilerHaiEr); // ����
//		BoilerALiSiDun = (RadioButton) findViewById(R.id.BoilerALiSiDun); // ����˹��
		BoilerYingHua = (RadioButton) findViewById(R.id.BoilerYingHua); // ӣ��
		BoilerHuaDi = (RadioButton) findViewById(R.id.BoilerHuaDi); // ����
		BoilerOther = (RadioButton) findViewById(R.id.BoilerOther); // ����
		BoilerShiMiSi = (RadioButton) findViewById(R.id.BoilerShiMiSi);
		BoilerXiaoSongShu = (RadioButton) findViewById(R.id.BoilerXiaoSongShu);

		//�ڹҹ�¯����
		BoilerPrecautionNormal = (CheckBox)findViewById(R.id.BoilerPrecautionNormal);
		BoilerPrecautionInBedRoom = (CheckBox)findViewById(R.id.BoilerPrecautionInBedRoom);
		BoilerPrecautionNotified = (CheckBox)findViewById(R.id.BoilerPrecautionNotified);

		// ------------------------------------��ȫ����-----------------------
		PrecautionInBathRoom = (CheckBox) findViewById(R.id.PrecautionInBathRoom); // ȼ����ʩ��װ������/������
		PrecautionInBedRoom = (CheckBox) findViewById(R.id.PrecautionInBedRoom); // ȼ���豸��װ������
		PrecautionLongPipe = (CheckBox) findViewById(R.id.PrecautionLongPipe); // ��ܹ���
		PrecautionElectricWire = (CheckBox) findViewById(R.id.PrecautionElectricWire); // ȼ���ܹ���Ӵ���Դ��
		PrecautionThreeWay = (CheckBox) findViewById(R.id.PrecautionThreeWay); // ��ܽ���ͨ
		PrecautionThroughFurniture = (CheckBox) findViewById(R.id.PrecautionThroughFurniture); // ��ܴ���/�Ŵ�/����
		PrecautionThroughWall = (CheckBox) findViewById(R.id.PrecautionThroughWall); // ��ܴ�ǽ/����
		PrecautionValidPipe = (CheckBox) findViewById(R.id.PrecautionValidPipe); // ʹ�÷���Ȼ��ר�����
		PrecautionWithConnector = (CheckBox) findViewById(R.id.PrecautionWithConnector); // ������н�ͷ
		PrecautionNearFire = (CheckBox) findViewById(R.id.PrecautionNearFire); // ������Դ̫��
		PrecautionWrapped = (CheckBox) findViewById(R.id.PrecautionWrapped); // ��ܰ���
		PrecautionNotified = (CheckBox) findViewById(R.id.PrecautionNotified); // �ѷ����ڰ��챨����
		PrecautionNoClamp = (CheckBox) findViewById(R.id.PrecautionNoClamp); // �޹ܿ�
		PrecautionMalPosition = (CheckBox) findViewById(R.id.PrecautionMalPosition); // ȼ���߰�װ���ڷ�λ�ò��淶
		PrecautionNearLiquefiedGas = (CheckBox) findViewById(R.id.PrecautionNearLiquefiedGas); // ��Һ��������һ��
		PrecautionUnsafeDevice = (CheckBox) findViewById(R.id.PrecautionUnsafeDevice); // �ǰ�ȫȼ����
		PrecautionLoosePipe = (CheckBox) findViewById(R.id.PrecautionLoosePipe); // �������û�й̶�
		PrecautionPipeInDark = (CheckBox) findViewById(R.id.PrecautionPipeInDark); // ��ܰ���
		PrecautionPipeOutside = (CheckBox) findViewById(R.id.PrecautionPipeOutside); // ����ڻ���

		// ----------------------------------------���ڹ�------------------------
		HomePlumNormal = (CheckBox) findViewById(R.id.HomePlumNormal);
		HomePlumLeakage = (CheckBox) findViewById(R.id.HomePlumLeakage);
		HomePlumThroughSittingRoom = (CheckBox) findViewById(R.id.HomePlumThroughSittingRoom);
		HomePlumThroughBedRoom = (CheckBox) findViewById(R.id.HomePlumThroughBedRoom);
		HomePlumNearAppliance = (CheckBox) findViewById(R.id.HomePlumNearAppliance);
		HomePlumWrapped = (CheckBox) findViewById(R.id.HomePlumWrapped);
		HomePlumModified = (CheckBox) findViewById(R.id.HomePlumModified);
		HomePlumOtherUse = (CheckBox) findViewById(R.id.HomePlumOtherUse);

		// ------------------------------------�ͻ��������-----------------------
		FeebackSatisfied = (RadioButton) findViewById(R.id.FeebackSatisfied);
		FeebackOK = (RadioButton) findViewById(R.id.FeebackOK);
		FeebackUnsatisfied = (RadioButton) findViewById(R.id.FeebackUnsatisfied);
	}


	/**
	 * �����Ѿ��������Ƭ
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//		if(!localSaved)
		//			Util.deleteFiles(uuid);
	}

	/**
	 * ��ʼ������ѡ��
	 */
	public void InitChoice() {
		// ���
		this.MeterOnTheLeft.setChecked(true);
		// �������ͺ�
		this.MeterMakerDanDong.setChecked(true);
		// IC�������ͺ�
		this.ICMeterMakerHuaJie.setChecked(true);
		// ���� 
		this.MeterTypeG25.setChecked(true);
		// ��ֹѹ��
		//this.PlumPressureNormal.setChecked(true);
		// ���
		//this.CookerOther.setChecked(true);
		// �ͺ�
		//this.CookeTypeTabletSingle.setChecked(true);
		// ��ˮ��
		//this.HeaterOther.setChecked(true);
		// �̵�
		//this.VentilationBalanced.setChecked(true);
		// �ڹҹ�¯
		//this.BoilerOther.setChecked(true);
		// ����
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
			//�첽��ȡά����Ա
			model.GetRepairPerson(uuid, "T_INSPECTION");
			//��¼�������ʱ��
			entryDateTime = new Date();
		}
	}


}
