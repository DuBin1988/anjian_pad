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
 * �뻧����model
 * 
 * @author lgy
 * 
 */
public class IndoorInspectModel {
	private final IndoorInspectActivity mContext;
	// �뻧����ƻ�ID
	private String indoorInpsectPlanID = "test";

	public IndoorInspectModel(IndoorInspectActivity Context) {
		this.mContext = Context;
		Bundle bundle = mContext.getIntent().getExtras();
		if (bundle != null)
			indoorInpsectPlanID = bundle.getString("ID");

		InspectionDate.set(Util.FormatDateToday("yyyy-MM-dd"));
		ArrivalTime.set(Util.FormatTimeNow("HH:mm:ss"));
		this.StructureTypeList.setArray(new String[] { "�߲�", "���", "С�߲�", 
		"����","ƽ��"});
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
	 * ���س�paneId�������LinearLayout
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

	// ÿ�����͵İ�����Ϣһ��
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
	 * �ϴ������¼
	 */
	public Command UploadInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//�жϵ����Ƿ��Ѿ�����������������ʾ������ڴ˴��ϴ������뻧ʱ�䡢�뿪ʱ��ᱻ�ı�
			if(Util.TestIfSaved(mContext,  mContext.uuid))
			{
				TextView textView = new TextView(mContext);
				textView.setBackgroundColor(Color.WHITE);
				textView.setTextColor(Color.BLACK);
				textView.setMaxWidth(300);
				textView.setMaxLines(5);
				textView.setTextSize(25);
				textView.setText("�����쵥�Ѿ�������������ϴ��뵽�ϴ����档���ڴ��ϴ���������ɵ���ʱ�䡢�뿪ʱ�����¼��㡣�Ƿ��ڴ��ϴ���");
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setCancelable(false);
				builder.setView(textView);
				builder.setTitle("����");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setPositiveButton("ȷ��", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						preUpload(true);
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.create().show();
			}
			else
				preUpload(true);
		}
	};
	
	/**
	 * �ϴ�ǰ�ж�����
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
				//���ý���
				try {
					RepairManList.clear();
					db = mContext.openOrCreateDatabase("safecheck.db",
							Context.MODE_PRIVATE, null);
					Cursor c = db.rawQuery("SELECT ID,CODE,NAME FROM T_PARAMS WHERE ID=?", new String[]{"ά����Ա"} );
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
	//@Required(ErrorMessage = "���Ų���Ϊ��")
	//@RegexMatch(Pattern = "[0-9]{15}$", ErrorMessage = "���ű���Ϊ15λ���֣�")
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
	public StringObservable SignTelephone = new StringObservable("");
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
	//�û�״̬
	public StringObservable UserState = new StringObservable("");
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
	
	//�û����
	public StringObservable UserID = new StringObservable("");

	// ά����
	public ArrayListObservable<RepairMan> RepairManList = new ArrayListObservable<RepairMan>(
			RepairMan.class);

	/**
	 * ���������������Ϣ
	 */
	/**
	 * sh20140106ע��
	 */
	/*
	public Command SearchByICCardNo = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			// �ӷ�������ȡIC���Ŷ�Ӧ���û���Ϣ
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

						// �����������
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
	// ��ʾ�û���Ϣ
	private final Handler listHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				super.handleMessage(msg);
				try {
					JSONObject obj = new JSONObject((String) msg.obj);
					if (!obj.has("f_cardid")) {
						// �鲻����IC���û�
						Toast.makeText(mContext, "�޴��û���Ϣ��", Toast.LENGTH_SHORT)
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

						//�𿨡��𿨷����á��𿨹�ҵ�������á��ȷ桢�ȷ����á�NULL
						if ("��".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("�𿨷�����".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("�𿨹�ҵ".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("������".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerHuaJie.setChecked(true);
						}else if ("�ȷ�".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerSaiFu.setChecked(true);
						}else if ("�ȷ�����".equals(METER_TYPES_NAME)) {
							mContext.ICMeterMakerSaiFu.setChecked(true);
						}
//						else if ("�ش�".equals(METER_TYPES_NAME)) {
//							mContext.ICMeterMakerQinChuan.setChecked(true);
//						} else if ("�ظ�".equals(METER_TYPES_NAME)) {
//							mContext.ICMeterMakerQinGang.setChecked(true);
//						} else if ("����".equals(METER_TYPES_NAME)) {
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
				Toast.makeText(mContext, "����������������Ա��ϵ��", Toast.LENGTH_LONG)
				.show();
				ArchiveAddress.set("");
				UserName.set("");
				Telephone.set("");
			} else if (2 == msg.what) {
				Toast.makeText(mContext, "�޴��û���", Toast.LENGTH_LONG).show();
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
	
	
	// ----------------------ȼ������Ϣ----------------------------------------
	// ������
	//@Required(ErrorMessage = "����������Ϊ��")
	public StringObservable BaseMeterQuantity = new StringObservable("");
	// ����ʣ������
	public StringObservable RemainGasQuantity = new StringObservable("");
	//������
	public StringObservable GasQuantity = new StringObservable("");
	//�ۼƹ�����
	public StringObservable BuyGasQuantity = new StringObservable("");
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
//	//���
//	public StringObservable Cooker = new StringObservable("");
//	//����ϰ�
//	public StringObservable CookerGangHuaZiJing = new StringObservable("");
//	//������
//	public StringObservable CookerWanHe = new StringObservable("");
//	//��������
//	public StringObservable CookerWanJiaLe = new StringObservable("");
//	//��߷�̫
//	public StringObservable CookerLinNei = new StringObservable("");
//	//��ߺ���
//	public StringObservable CookerHaiEr = new StringObservable("");
//	//���˧��
//	public StringObservable CookerALiSiDun = new StringObservable("");
//	//���ӣ��
//	public StringObservable CookerYinhHua = new StringObservable("");
//	//���������
//	public StringObservable CookerXiMenZi = new StringObservable("");
//	//��߻���
//	public StringObservable CookerHuaDi = new StringObservable("");
//	//�������
//	public StringObservable CookerOther = new StringObservable("");
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
	//��ע
	public StringObservable Remark = new StringObservable("");

	/**
	 * ǩ��
	 */
	public Command sign = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent();
			// ���ð������ݲ�����Activity
			Bundle bundle = new Bundle();
			bundle.putString("ID", mContext.uuid + "_sign");
			intent.setClass(mContext, AutographActivity.class);
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 6);
		}
	};


	/**
	 * ��ȡ������Ϣ
	 */
	public void GetPrecautionMap(ArrayList<String> severePrecaution, ArrayList<String> generalPrecaution) {
		String precautionString = "";
		// ʹ��ֱ����ˮ��
		if (mContext.HeaterPrecautionStraight.isChecked()) {
			precautionString = "ʹ��ֱ����ˮ��";
			severePrecaution.add(precautionString);
		}
		// ��ˮ���̵�(���̵����̵������̵�δ�쵽���⣬�����̵�)
		if (mContext.HeaterPrecautionNoVentilation.isChecked()) {
			precautionString = "��ˮ���̵������̵� ";
			severePrecaution.add(precautionString);
		}
		if (mContext.HeaterPrecautionBrokenVent.isChecked()) {
			precautionString = "��ˮ���̵����̵�����";
			severePrecaution.add(precautionString);
		}
		if (mContext.HeaterPrecautionTrapped.isChecked()) {
			precautionString = "��ˮ���̵����̵�δ�쵽���� ";
			severePrecaution.add(precautionString);
		}
		if (mContext.HeaterPrecautionWrappedVent.isChecked()) {
			precautionString = "��ˮ���̵��������̵�";
			severePrecaution.add(precautionString);
		}
		// ��ˮ����ڹҹ�¯��װ������
		if (mContext.HeaterPrecautionInHome.isChecked() || mContext.BoilerPrecautionInBedRoom.isChecked()) {
			precautionString = "��ˮ����ڹҹ�¯��װ������";
			severePrecaution.add(precautionString);
		}
		// ȼ����ʩ��װ������
		if (mContext.PrecautionInBedRoom.isChecked()) {
			precautionString = "ȼ����ʩ��װ������";
			severePrecaution.add(precautionString);
		}
		// ȼ�������ڹܵ�����ǰ�����Աշ�����ǰ�����������ܱտռ���
		if (mContext.MeterWrapped.isChecked()) {
			precautionString = "ȼ���� ";
			generalPrecaution.add( precautionString + "�������ܱտռ���" );
		}
		if (mContext.HomePlumWrapped.isChecked()) {
			precautionString = "���ڹܵ� ";
			generalPrecaution.add( precautionString + "�������ܱտռ���" );
		}
		if (mContext.MeterValveWrapped.isChecked()) {
			precautionString = "��ǰ�� ";
			generalPrecaution.add( precautionString + "�������ܱտռ���" );		
		}
		if (mContext.AutoValveWrapped.isChecked()) {
			precautionString = "�Աշ� ";
			generalPrecaution.add( precautionString + "�������ܱտռ���" );
		}
		if (mContext.CookerValveWrapped.isChecked()) {
			precautionString = "��ǰ�� ";
			generalPrecaution.add( precautionString + "�������ܱտռ���" );		
		}
		// ȼ�����Ϲ���Ӵ����ߵ�
		if (mContext.PrecautionElectricWire.isChecked()) {
			precautionString = "ȼ�����Ϲ���Ӵ����ߵ�";
			generalPrecaution.add(precautionString);
		}
		// ��������а�ȫ����
		if (mContext.PrecautionPipeInDark.isChecked()) {
			precautionString = "��������а�ȫ����:��ܰ��� ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionLongPipe.isChecked()) {
			precautionString = "��������а�ȫ����:��ܹ��� ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionLoosePipe.isChecked()) {
			precautionString = "��������а�ȫ����:û�й̶�";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionThreeWay.isChecked()) {
			precautionString = "��������а�ȫ����:˽����ͨ ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionPipeOutside.isChecked()) {
			precautionString = "��������а�ȫ����:��������ڻ��� ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionThroughFurniture.isChecked()) {
			precautionString = "��������а�ȫ����:�����Ҿߣ��ݶ�  ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.PrecautionThroughWall.isChecked()) {
			precautionString = "��������а�ȫ����:����ǽ�� ";
			generalPrecaution.add( precautionString);
		}
		// ȼ����������Դλ��©��
		if (mContext.CookerPipeLeakage.isChecked()) {
			precautionString = "ȼ����������Դλ��©��:������©�� ";
			generalPrecaution.add( precautionString);
		}
		if (mContext.HeaterPipeLeakage.isChecked()) {
			precautionString = "ȼ����������Դλ��©��:��ˮ�����©�� ";
			generalPrecaution.add( precautionString);
		}
		// ���ܼ����ڹܵ�©��
		if (mContext.PlumLeakage.isChecked()
				|| mContext.HomePlumLeakage.isChecked()) {
			precautionString = "���ܼ����ڹܵ�©��";
			generalPrecaution.add(precautionString);
		}
		// ʹ�÷ǰ�ȫȼ����
		if (mContext.PrecautionUnsafeDevice.isChecked()) {
			precautionString = "ʹ�÷ǰ�ȫȼ����";
			generalPrecaution.add(precautionString);
		}
		// ȼ���߰�װ���ڷ�λ�ò���ȷ
		if (mContext.PrecautionMalPosition.isChecked()) {
			precautionString = "ȼ���߰�װ���ڷ�λ�ò��淶";
			generalPrecaution.add(precautionString);
		}
		// ��������
		if (mContext.HomePlumOtherUse.isChecked()) {
			precautionString = "��������";
			generalPrecaution.add(precautionString);
		}
	}

	/**
	 * ���������Ի���
	 */
	public void PushDialog(ArrayList<String> severePrecaution,ArrayList<String> generalPrecaution, final boolean isUpload)
	{
		
		LayoutInflater inflater = mContext.getLayoutInflater();
		View layout = inflater.inflate(R.layout.safehiddle,null);
		AlertDialog.Builder builder = new Builder(mContext);
		
		builder.setCancelable(false);

		builder.setPositiveButton("ȷ��", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean needsRepair = ((CheckBox)mContext.findViewById(R.id.IsDispatchRepair)).isChecked();
				Upload(needsRepair, true, isUpload);
			}
		});
		
		//��ȡ����������Layout
		LinearLayout SevereList = (LinearLayout)(layout.findViewById(R.id.Severe));		
		//��ÿһ����������������TextView����ӵ���������������

		for(String name : severePrecaution) {
			TextView v = new TextView(mContext);
			v.setText(name);
			v.setTextSize(20);
			SevereList.addView(v);
		}
		
		//��ȡһ��������Layout
		LinearLayout ModerateLise = (LinearLayout)(layout.findViewById(R.id.Moderate));
		//����ÿһ��һ������������TextView����ӵ�һ������������
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
	 * ���ر��沢�ϴ�
	 */
	private void Upload(boolean saveRepair, boolean precautionNotified, boolean isUpload) {
		// �����е�ͼƬ
		// �ϴ�ͼƬ
		ArrayList<String> imgs = new ArrayList<String>();
		if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid
				+ "_sign.png")) {
			imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png");
			imgs.add("ǩ��ͼƬ");
			imgs.add(mContext.uuid + "_sign");
		}
		for (int i = 1; i < 8; i++) {
			if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid
					+ "_" + i + ".jpg")) {
				imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i
						+ ".jpg");
				imgs.add("������Ƭ" + i);
				imgs.add(mContext.uuid + "_" + i);
			}
		}
		//��֤
		if(!mContext.validate())
			return;

		//���ر���
		String row = mContext.SaveToJSONString(saveRepair, true);
		//�Ƿ��·���ȫ��������֪ͨ�飬���������Ի��������
		try
		{
			JSONObject rowObj = new JSONObject(row);
			rowObj.put("PRECAUTION_NOTIFIED", precautionNotified?"��":"��");
			row = rowObj.toString();
		}
		catch(Exception e)
		{
			//ignore
		}
		if(!mContext.Save(row, "T_INSPECTION", "T_IC_SAFECHECK_HIDDEN", false))
		{
			mContext.localSaved = false;
			Toast.makeText(mContext, "���氲���¼��ƽ��ʧ��!", Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			mContext.localSaved = true;
			if(!isUpload)
				Toast.makeText(mContext, "���氲���¼�ɹ�!", Toast.LENGTH_SHORT).show();				
		}
		imgs.add(row);
		if(isUpload)
		{
			HttpMultipartPost poster = new HttpMultipartPost(mContext);
			poster.execute(imgs.toArray(new String[imgs.size()]));
		}
	}




}


