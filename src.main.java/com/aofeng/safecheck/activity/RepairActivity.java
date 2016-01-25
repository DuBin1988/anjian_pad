package com.aofeng.safecheck.activity;

import gueei.binding.app.BindingActivity;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.aofeng.safecheck.R;
import com.aofeng.safecheck.modelview.RepairMan;
import com.aofeng.safecheck.modelview.RepairModel;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;

public class RepairActivity extends BindingActivity {
	// �뻧����ƻ�ID
	private RepairModel model;
	//������ʱ���ɵ�UUID
	public String uuid;
	private boolean inspected;

	public String paperId = "test";
	public String planId="";
	
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
	public RadioButton PlumPressureNormal;
	public RadioButton PlumPressureAbnormal;
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
	public RadioButton HeaterALiSiDun;
	public RadioButton HeaterYingHua;
	public RadioButton HeaterHuaDi;
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
	public RadioButton BoilerALiSiDun; // ����˹��
	public RadioButton BoilerYingHua; // ӣ��
	public RadioButton BoilerHuaDi; // ����
	public RadioButton BoilerOther; // ����
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
	
	private CheckBox IsDispatchRepair;

	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private ImageView img4;
	private ImageView img5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new RepairModel(this);
		this.setAndBindRootView(R.layout.repair, model);
		model.muteOthers(R.id.basicPane);
		Bundle bundle;
		if(savedInstanceState != null)
			bundle = savedInstanceState;
		else
			bundle = getIntent().getExtras();
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
		uuid = paperId;
		CreateRepairCheckBoxes();
		InitControls();
		if(readonly)
			DisableLayouts();
	}

/**
 * ��̬���ɰ��츴ѡ��
 */
	private void CreateRepairCheckBoxes() {
		ViewGroup vg = (ViewGroup)this.findViewById(R.id.repairOptionPane);
		vg.removeAllViews();
		ArrayList<String> list = 	Util.getRepairList(this);
		for(String s : list)
		{
			CheckBox cb = new CheckBox(this);
			cb.setText(s);
			cb.setTextColor(Color.BLACK);
			cb.setTextSize(20);
			cb.setLeft(5);
			vg.addView(cb);
		}
	}



	/**
	 * �����ұ߲���Ϊ��ֹʹ��
	 */
	private void DisableLayouts() {
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
	 * ������֤
	 * 
	 * @return
	 */
	public boolean validate() {

		return true;
	}

	// ���ر��氲���¼
	public boolean Save(String objStr, String inspectionTable, String precautionTable, String ResultTable, boolean isTemp) {
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
				redundantCols.put("IC_METER_NAME", row.getString("IC_METER_NAME"));
				redundantCols.put("JB_METER_NAME", row.getString("JB_METER_NAME"));
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
				if (key.equals("ID") || key.equals("T_REPAIR_RESULT"))
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
			//����ά�޽��
			if(row.has("T_REPAIR_RESULT"))
			{
				JSONObject lines = row.getJSONObject("T_REPAIR_RESULT");
				SaveRepairResult(lines, ResultTable);
			}
			return true;

		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
			return false;
		}
	}

	public boolean SaveRepairResult(JSONObject lines, String ResultTable) {
		try
		{
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db",
					Context.MODE_PRIVATE, null);		
			db.execSQL("delete from " + ResultTable + " where id='" + uuid +"'");
			Iterator<String> itr = lines.keys();
			while (itr.hasNext()) {
				String key = itr.next();
				db.execSQL("insert into " + ResultTable + "(id, content) values (?,?)", new String[]{uuid, key});
			}
			db.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
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
	public String SaveToJSONString(boolean saveRepair) {
		JSONObject row = new JSONObject();
		try {
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
			// ����ʱ��
			row.put("ARRIVAL_TIME", model.InspectionDate.get() + " "
					+ model.ArrivalTime.get());
			// �뿪ʱ��
			row.put("DEPARTURE_TIME",
					model.InspectionDate.get() + " "
							+ Util.FormatTimeNow("HH:mm:ss"));
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
					row.put("IC_METER_NAME", "����");
				else if (ICMeterMakerSaiFu.isChecked())
					row.put("IC_METER_NAME", "����");
				else if (ICMeterMakerQinChuan.isChecked())
					row.put("IC_METER_NAME", "�ش�");
				else if (ICMeterMakerQinGang.isChecked())
					row.put("IC_METER_NAME", "�ظ�");
				else if (ICMeterMakerZhiLi.isChecked())
					row.put("IC_METER_NAME", "����");
			} else
				row.put("IC_METER_NAME", model.ICMeterMakerOther.get());
			// ����
			if (this.MeterTypeG25.isChecked())
				row.put("METER_TYPE", "G2.5");
			else if (this.MeterTypeG4.isChecked())
				row.put("METER_TYPE", "G4");
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
				row.put("���ڹ�_3", "���������");
			if (this.HomePlumNearAppliance.isChecked())
				row.put("���ڹ�_4", "����");
			if (this.HomePlumWrapped.isChecked())
				row.put("���ڹ�_5", "˽��");
			if (this.HomePlumModified.isChecked())
				row.put("���ڹ�_6", "��������");
			if (this.HomePlumThroughBedRoom.isChecked())
				row.put("���ڹ�_7", "������");

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
				row.put("COOK_BRAND", "�ۻ��Ͼ�");
			else if (this.CookerWanHe.isChecked())
				row.put("COOK_BRAND", "���");
			else if (this.CookerWanJiaLe.isChecked())
				row.put("COOK_BRAND", "�����");
			else if (this.CookerLinNei.isChecked())
				row.put("COOK_BRAND", " ����");
			else if (this.CookerHaiEr.isChecked())
				row.put("COOK_BRAND", "����");
			else if (this.CookerALiSiDun.isChecked())
				row.put("COOK_BRAND", "����˹��");
			else if (this.CookerYinhHua.isChecked())
				row.put("COOK_BRAND", "ӣ��");
			else if (this.CookerHuaDi.isChecked())
				row.put("COOK_BRAND", "����");
			else if (this.CookerOther.isChecked())
				row.put("COOK_BRAND", "����");
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
				row.put("WATER_BRAND", "�ۻ��Ͼ�");
			else if (this.HeaterWanHe.isChecked())
				row.put("WATER_BRAND", "���");
			else if (this.HeaterWanJiaLe.isChecked())
				row.put("WATER_BRAND", "�����");
			else if (this.HeaterLinNei.isChecked())
				row.put("WATER_BRAND", " ����");
			else if (this.HeaterHaiEr.isChecked())
				row.put("WATER_BRAND", "����");
			else if (this.HeaterALiSiDun.isChecked())
				row.put("WATER_BRAND", "����˹��");
			else if (this.HeaterYingHua.isChecked())
				row.put("WATER_BRAND", "ӣ��");
			else if (this.HeaterHuaDi.isChecked())
				row.put("WATER_BRAND", "����");
			else if (this.HeaterOther.isChecked())
				row.put("WATER_BRAND", "����");
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
				row.put("WHE_BRAND", "�ۻ��Ͼ�");
			if (this.BoilerWanHe.isChecked())
				row.put("WHE_BRAND", "���");
			if (this.BoilerWanJiaLe.isChecked())
				row.put("WHE_BRAND", "�����");
			if (this.BoilerLinNei.isChecked())
				row.put("WHE_BRAND", " ����");
			if (this.BoilerHaiEr.isChecked())
				row.put("WHE_BRAND", "����");
			if (this.BoilerALiSiDun.isChecked())
				row.put("WHE_BRAND", "����˹��");
			if (this.BoilerYingHua.isChecked())
				row.put("WHE_BRAND", "ӣ��");
			if (this.BoilerHuaDi.isChecked())
				row.put("WHE_BRAND", "����");
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
			if (this.HeaterPrecautionNoVentilation.isChecked())
				row.put("�ڹҹ�¯��ȫ����_2", "�ѷ����ڰ����֪��");

			// ��ȫ����
			if (this.PrecautionInBathRoom.isChecked())
				row.put("��ȫ����_1", "ȼ����ʩ��װ������/������");
			if (this.PrecautionInBedRoom.isChecked())
				row.put("��ȫ����_2", "��ܹ���");
			if (this.PrecautionLongPipe.isChecked())
				row.put("��ȫ����_3", "ȼ���ܹ���Ӵ���Դ��");
			if (this.PrecautionElectricWire.isChecked())
				row.put("��ȫ����_4", "��ܽ���ͨ");
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
			if (this.PrecautionInBedRoom.isChecked())
				row.put("��ȫ����_19", "ȼ���豸��װ������");
			// �û����
			if (model.UserSuggestion.get().trim().length() > 0)
				row.put("USER_SUGGESTION", model.UserSuggestion.get().trim());
			// �û�����
			if (this.FeebackSatisfied.isChecked())
				row.put("USER_SATISFIED", "����");
			else if (this.FeebackOK.isChecked())
				row.put("USER_SATISFIED", "��������");
			else if (this.FeebackUnsatisfied.isChecked())
				row.put("USER_SATISFIED", "������");
			// ǩ��
			if (Util.fileExists(Util.getSharedPreference(this, "FileDir") + "_" + uuid
					+ "_sign.png"))
				row.put("USER_SIGN", uuid + "_sign");
			// ͼƬ
			if (Util.fileExists(Util.getSharedPreference(this, "FileDir") + "_" + uuid
					+ "_1.jpg"))
				row.put("PHOTO_FIRST", uuid + "_1");
			if (Util.fileExists(Util.getSharedPreference(this, "FileDir") + "_" + uuid
					+ "_2.jpg"))
				row.put("PHOTO_SECOND", uuid + "_2");
			if (Util.fileExists(Util.getSharedPreference(this, "FileDir") + "_" + uuid
					+ "_3.jpg"))
				row.put("PHOTO_THIRD", uuid + "_3");
			if (Util.fileExists(Util.getSharedPreference(this, "FileDir") + "_" + uuid
					+ "_4.jpg"))
				row.put("PHOTO_FOUTH", uuid + "_4");
			if (Util.fileExists(Util.getSharedPreference(this, "FileDir") + "_" + uuid
					+ "_5.jpg"))
				row.put("PHOTO_FIFTH", uuid + "_5");
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
			//����ά��ѡ��
			row.put("T_REPAIR_RESULT", SaveRepairOptions());
			
			return row.toString();
		} catch (JSONException e) {
			Log.d("IndoorInsppectoon", e.getMessage());
			return null;
		}
	}

	/**
	 * ����ά��ѡ��
	 * @param row
	 */
	public JSONObject SaveRepairOptions() {
		try
		{
			JSONObject lines = new JSONObject();
			ViewGroup vg = (ViewGroup)this.findViewById(R.id.repairOptionPane);
			for(int i=0; i < vg.getChildCount(); i++)
			{
				CheckBox cb = (CheckBox)vg.getChildAt(i);
				if(cb.isChecked())
					lines.put((String) cb.getText(), (String) cb.getText());
			}
			return lines;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * �ӱ������ݿ��ȡ�����ֶβ����ֶθ�ֵ
	 */
	private void ReadFromDB(String id,  String inspectionTable, String precautionTable, String ResultTable) {
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
				String value = c.getString(c.getColumnIndex("HasNotified"));
				if (value!=null && value.length()>0)
					model.HasNotified.set(true);

				// ����ʱ��
				String dt = c.getString(c.getColumnIndex("ARRIVAL_TIME"));
				model.InspectionDate.set(dt.substring(0, 10));
				model.ArrivalTime.set(dt.substring(dt.length()-8, dt.length()));
				// �뿪ʱ��
				dt = c.getString(c.getColumnIndex("DEPARTURE_TIME"));
				model.DepartureTime.set(dt.substring(dt.length()-8, dt.length()));
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
					return;
				} else if (c.getString(c.getColumnIndex("CONDITION")).equals(
						"�ܾ�")) {
					model.IsEntryDenied.set(true);
					return;
				}
				else
				{
					DisableOtherCondition();
				}
				
				// ��������
				model.UserName.set(c.getString(c.getColumnIndex("USER_NAME")));
				// �绰
				model.Telephone.set(c.getString(c.getColumnIndex("TELPHONE")));
				// С������
				model.ResidentialAreaName.set(c.getString(c
						.getColumnIndex("UNIT_NAME")));
				// �û�������ַ
				model.ArchiveAddress.set(c.getString(c
						.getColumnIndex("OLD_ADDRESS")));

				// ���ݽṹ
				String roomStruct = c.getString(c
						.getColumnIndex("ROOM_STRUCTURE"));
				Spinner spinnerStructureTypeList = (Spinner) this
						.findViewById(R.id.StructureTypeList);
				if (roomStruct.equals("ƽ��"))
					spinnerStructureTypeList.setSelection(0);
				else if (roomStruct.equals("���"))
					spinnerStructureTypeList.setSelection(1);
				else if (roomStruct.equals("С�߲�"))
					spinnerStructureTypeList.setSelection(2);
				else if (roomStruct.equals("�߲�"))
					spinnerStructureTypeList.setSelection(3);
				else if (roomStruct.equals("����"))
					spinnerStructureTypeList.setSelection(4);

				// ��ů��ʽ
				String warm = c.getString(c.getColumnIndex("WARM"));
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
				if (ic_menter_name.equals("����")) {
					this.ICMeterMakerHuaJie.setChecked(true);
				} else if (ic_menter_name.equals("����")) {
					this.ICMeterMakerSaiFu.setChecked(true);
				} else if (ic_menter_name.equals("�ش�")) {
					this.ICMeterMakerQinChuan.setChecked(true);
				} else if (ic_menter_name.equals("�ظ�")) {
					this.ICMeterMakerQinGang.setChecked(true);
				} else if (ic_menter_name.equals("����")) {
					this.ICMeterMakerZhiLi.setChecked(true);
				} else {
					this.ICMeterMakerOtherBox.setChecked(true);
					model.ICMeterMakerOther.set(ic_menter_name);
				}

				// ����
				String menter_type = c
						.getString(c.getColumnIndex("METER_TYPE"));
				if (menter_type.equals("G2.5")) {
					this.MeterTypeG25.setChecked(true);
				} else {
					this.MeterTypeG4.setChecked(true);
				}

				// IC����
				model.ICCardNo.set(c.getString(c.getColumnIndex("CARD_ID")));

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
				// ȼ�������ұ�
				if (c.getString(c.getColumnIndex("RQB_AROUND")).equals("���")) {
					this.MeterOnTheLeft.setChecked(true);
				} else {
					this.MeterOnTheRight.setChecked(true);
				}

				// ȼ��������
				if (c.getString(c
						.getColumnIndex("RQB_JBCODE")) != null) {
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
				if (c.getString(c.getColumnIndex("RQB")).equals("����")) {
					this.MeterNormal.setChecked(true);
				}

				// ����
				if (c.getString(c.getColumnIndex("STANDPIPE")).equals("����")) {
					this.PlumNormal.setChecked(true);
				}

				// �����Բ���
				if (c.getString(c.getColumnIndex("RIGIDITY")).equals("����")) {
					this.PlumProofNormal.setChecked(true);
				} else {
					this.PlumProofLeakage.setChecked(true);
				}

				// ��ֹѹ��
				if (c.getString(c.getColumnIndex("STATIC")).equals("����")) {
					this.PlumPressureNormal.setChecked(true);
				} else {
					this.PlumPressureAbnormal.setChecked(true);
				}
				// ��ֹѹ��ֵ
				model.PlumPressure.set(c.getString(c
						.getColumnIndex("STATIC_DATA")));

				// ��ǰ��
				if (c.getString(c.getColumnIndex("TABLE_TAP")).equals("����")) {
					this.MeterValveNormal.setChecked(true);
				}
				// ��ǰ��
				if (c.getString(c.getColumnIndex("COOK_TAP")).equals("����")) {
					this.CookerValveNormal.setChecked(true);
				}
				// �Աշ�
				if (c.getString(c.getColumnIndex("CLOSE_TAP")).equals("����")) {
					this.AutoValveNormal.setChecked(true);
				}
				// ���ڹ�
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
				model.LeakagePlace.set(c.getString(c
						.getColumnIndex("LEAKGEPLACE")));

				// ���Ʒ��
				String cook_brand = c.getString(c.getColumnIndex("COOK_BRAND"));
				if (cook_brand.equals("�ۻ��Ͼ�")) {
					this.CookerGangHuaZiJing.setChecked(true);
				} else if (cook_brand.equals("���")) {
					this.CookerWanHe.setChecked(true);
				} else if (cook_brand.equals("�����")) {
					this.CookerWanJiaLe.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerLinNei.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerHaiEr.setChecked(true);
				} else if (cook_brand.equals("����˹��")) {
					this.CookerALiSiDun.setChecked(true);
				} else if (cook_brand.equals("ӣ��")) {
					this.CookerYinhHua.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerHuaDi.setChecked(true);
				} else if (cook_brand.equals("����")) {
					this.CookerOther.setChecked(true);
				}

				// ����ͺ�
				String cook_type = c.getString(c.getColumnIndex("COOK_TYPE"));
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
				if (c.getString(c.getColumnIndex("COOKPIPE_NORMAL")).equals(
						"����")) {
					this.CookerPipeNormal.setChecked(true);
				}
				// ��߹�������
				model.CookerBoughtTime.set(c.getString(c
						.getColumnIndex("COOK_DATE")));

				// ��ˮ��Ʒ��
				String water_brand = c.getString(c
						.getColumnIndex("WATER_BRAND"));
				if(water_brand==null)
					water_brand="";
				if (water_brand.equals("�ۻ��Ͼ�")) {
					this.HeaterGangHuaZiJing.setChecked(true);
				} else if (water_brand.equals("���")) {
					this.HeaterWanHe.setChecked(true);
				} else if (water_brand.equals("�����")) {
					this.HeaterWanJiaLe.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterLinNei.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterHaiEr.setChecked(true);
				} else if (water_brand.equals("����˹��")) {
					this.HeaterALiSiDun.setChecked(true);
				} else if (water_brand.equals("ӣ��")) {
					this.HeaterYingHua.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterHuaDi.setChecked(true);
				} else if (water_brand.equals("����")) {
					this.HeaterOther.setChecked(true);
				}

				// ��ˮ���ͺ�
				if(c.getString(c.getColumnIndex("WATER_TYPE")) != null)
					model.HeaterType.set(c.getString(c.getColumnIndex("WATER_TYPE")));
				else
					model.HeaterType.set("");

				// ��ˮ���̵�
				value = c.getString(c.getColumnIndex("WATER_FLUE"));
				if(value==null)
					value = "";
				if (value.equals("ƽ��")) {
					this.VentilationBalanced.setChecked(true);
				} else if (value.equals(
						"ǿ��")) {
					this.VentilationForce.setChecked(true);
				} else if (value.equals(
						"�̵�")) {
					this.VentilationPath.setChecked(true);
				} else if (value.equals(
						"ֱ��")) {
					this.VentilationStraight.setChecked(true);
				}

				// ��ˮ�����
				value = c.getString(c.getColumnIndex("WATER_PIPE"));
				if(value==null)
					value = "";
				if (value.equals("����")) {
					this.HeaterPipeNormal.setChecked(true);
				}
				// ��ˮ����װʱ��
				model.HeaterBoughtTime.set(c.getString(c
						.getColumnIndex("WATER_DATE")));
				// �����ܿ�
				model.HeaterPipeClampCount.set(c.getString(c
						.getColumnIndex("WATER_NUME")));
				// ��ˮ������
				String str = c.getString(c.getColumnIndex("WATER_HIDDEN"));
				if (c.getString(c.getColumnIndex("WATER_HIDDEN")).equals("����")) {
					this.HeaterPrecautionNone.setChecked(true);
				}

				// �ڹ�¯Ʒ��
				if (c.getString(c.getColumnIndex("WHE_BRAND")).equals("�ۻ��Ͼ�")) {
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
						"����˹��")) {
					this.BoilerALiSiDun.setChecked(true);
				} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
						"ӣ��")) {
					this.BoilerYingHua.setChecked(true);
				} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
						"����")) {
					this.BoilerHuaDi.setChecked(true);
				} else if (c.getString(c.getColumnIndex("WHE_BRAND")).equals(
						"����")) {
					this.BoilerOther.setChecked(true);
				}

				// �ڹ�¯�ͺ�
				if(c.getString(c.getColumnIndex("WHE_TYPE")) != null)
					model.BoilerType.set(c.getString(c.getColumnIndex("WHE_TYPE")));
				else 
					model.BoilerType.set("");
				// ����ʱ�䣨�ڹҹ�¯��
				model.BoilerBoughtTime.set(c.getString(c
						.getColumnIndex("WHE_DATE")));

				// �ڹҹ�¯����
				if (c.getString(c.getColumnIndex("WHE_HIDDEN")).equals("����")) {
					this.BoilerPrecautionNormal.setChecked(true);
				}

				// �û����
				if(c.getString(c.getColumnIndex("USER_SUGGESTION")) != null)
					model.UserSuggestion.set(c.getString(c.getColumnIndex("USER_SUGGESTION")));
				else 
					model.UserSuggestion.set("");
				// �û�����
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

				// ��Ƭ
				if (c.getString(c.getColumnIndex("USER_SIGN")) != null) {
					ImageView signPad = (ImageView) (findViewById(R.id.signPad));
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(this, "FileDir")  + "_"
							+ c.getString(c.getColumnIndex("USER_SIGN"))
							+ ".png");
					signPad.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIRST")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(this, "FileDir")  + "_"
							+ c.getString(c.getColumnIndex("PHOTO_FIRST"))
							+ ".jpg");
					img1.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_SECOND")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(this, "FileDir")  + "_"
							+ c.getString(c.getColumnIndex("PHOTO_SECOND"))
							+ ".jpg");
					img2.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_THIRD")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(this, "FileDir")  + "_"
							+ c.getString(c.getColumnIndex("PHOTO_THIRD"))
							+ ".jpg");
					img3.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FOUTH")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(this, "FileDir") + "_"
							+ c.getString(c.getColumnIndex("PHOTO_FOUTH"))
							+ ".jpg");
					img4.setImageBitmap(bmp);
				}
				if (c.getString(c.getColumnIndex("PHOTO_FIFTH")) != null) {
					Bitmap bmp = Util.getLocalBitmap(Util.getSharedPreference(this, "FileDir") + "_"
							+ c.getString(c.getColumnIndex("PHOTO_FIFTH"))
							+ ".jpg");
					img5.setImageBitmap(bmp);
				}
				this.IsDispatchRepair.setChecked(true);
				model.RepairManList.clear();
				RepairMan rm = new RepairMan();
				rm.name = c.getString(c.getColumnIndex("REPAIRMAN"));
				rm.id = c.getString(c.getColumnIndex("REPAIRMAN_ID"));
				model.RepairManList.add(rm);
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
			ReadRepairOptions(id, db, ResultTable);
			db.close();
		} catch (Exception e) {
			Log.d("IndoorInspection", e.getMessage());
		}
	}

	/**
	 * ��ȡά�޽��ѡ��
	 * @param db
	 * @param resultTable
	 */
	private void ReadRepairOptions(String id, SQLiteDatabase db, String resultTable) {
		ViewGroup vg = (ViewGroup)this.findViewById(R.id.repairOptionPane);
		Cursor c = db.rawQuery("select id, content from " + resultTable + " where id=?", new String[]{id});
		while(c.moveToNext())
		{
			for(int i=0; i < vg.getChildCount(); i++)
			{
				CheckBox cb = (CheckBox)vg.getChildAt(i);
				if(c.getString(1).equals(cb.getText()))
					cb.setChecked(true);
			}
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
		img1 = (ImageView) findViewById(R.id.image1);
		img2 = (ImageView) findViewById(R.id.image2);
		img3 = (ImageView) findViewById(R.id.image3);
		img4 = (ImageView) findViewById(R.id.image4);
		img5 = (ImageView) findViewById(R.id.image5);

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
		PlumPressureNormal = (RadioButton) findViewById(R.id.PlumPressureNormal);
		PlumPressureAbnormal = (RadioButton) findViewById(R.id.PlumPressureAbnormal);
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
		HeaterALiSiDun = (RadioButton) findViewById(R.id.HeaterALiSiDun);
		HeaterYingHua = (RadioButton) findViewById(R.id.HeaterYingHua);
		HeaterHuaDi = (RadioButton) findViewById(R.id.HeaterHuaDi);
		HeaterOther = (RadioButton) findViewById(R.id.HeaterOther);
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
		BoilerALiSiDun = (RadioButton) findViewById(R.id.BoilerALiSiDun); // ����˹��
		BoilerYingHua = (RadioButton) findViewById(R.id.BoilerYingHua); // ӣ��
		BoilerHuaDi = (RadioButton) findViewById(R.id.BoilerHuaDi); // ����
		BoilerOther = (RadioButton) findViewById(R.id.BoilerOther); // ����

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
		
		this.IsDispatchRepair = (CheckBox)findViewById(R.id.IsDispatchRepair);

		// ------------------------------------�ͻ��������-----------------------
		FeebackSatisfied = (RadioButton) findViewById(R.id.FeebackSatisfied);
		FeebackOK = (RadioButton) findViewById(R.id.FeebackOK);
		FeebackUnsatisfied = (RadioButton) findViewById(R.id.FeebackUnsatisfied);
	}


	@Override
	protected void onStop() {
		super.onStop();
		Save(SaveToJSONString(true), "T_INP", "T_INP_LINE", "T_REPAIR_RESULT2", true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Util.IsCached(this, uuid))
		{
			ReadFromDB(uuid, "T_INP", "T_INP_LINE", "T_REPAIR_RESULT2");
		}
		else
		{
			if(inspected)
				ReadFromDB(uuid,"T_REPAIR_TASK", "T_REPAIR_ITEM", "T_REPAIR_RESULT");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Bundle bundle = this.getIntent().getExtras();
		outState.putString("ID", bundle.getString("ID"));
		outState.putString("CUS_FLOOR", bundle.getString("CUS_FLOOR"));
		outState.putString("CUS_ROOM", bundle.getString("CUS_ROOM"));
		outState.putString("ROAD", bundle.getString("ROAD"));
		outState.putString("UNIT_NAME", bundle.getString("UNIT_NAME"));
		outState.putString("CUS_DOM", bundle.getString("CUS_DOM"));
		outState.putString("CUS_DY", bundle.getString("CUS_DY"));
		outState.putBoolean("INSPECTED",bundle.getBoolean("INSPECTED"));
		if(bundle.containsKey("READONLY"))
			outState.putBoolean("READONLY",bundle.getBoolean("READONLY"));
	}

	public void SaveAndUploadRepairResult()
	{
		final JSONObject row = SaveRepairOptions();
		if(row == null)
		{
			Toast.makeText(this, "��ѡ��ά�޽���", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!SaveRepairResult(row, "T_REPAIR_RESULT"))
		{
			Toast.makeText(this, "����ά�޽����ƽ��ʧ�ܡ�", Toast.LENGTH_SHORT).show();
			return;			
		}
		if(!SetRepairState(Vault.REPAIRED_UNUPLOADED))
		{
			Toast.makeText(this, "����ά�޽����ƽ��ʧ�ܡ�", Toast.LENGTH_SHORT).show();
			return;			
		}
		Thread th = new Thread(new Runnable() {	
			@Override
			public void run() {
				execute(row);
			}
		});
		th.start();
	}

/**
 * �ϴ�
 * @param row
 */
	private void execute(JSONObject row) {
		HttpPost httpPost = new HttpPost(Vault.IIS_URL + "saveRepair");
		try {
			row.put("ID", uuid);
			httpPost.setEntity(new StringEntity(row.toString(), "UTF8"));
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			String result =  EntityUtils.toString(response.getEntity(), "UTF8");
			JSONObject obj = new JSONObject(result);
			if(obj.getString("ok").equals("nok"))
			{
				Message msg = new Message();
				msg.what=-1;
				msg.obj = "�ϴ�ʧ�ܣ�";
				mHandler.sendMessage(msg);
			}
			else
			{
				Message msg = new Message();
				msg.what=0;
				msg.obj = "�ϴ��ɹ���";
				mHandler.sendMessage(msg);
			}
		}
		catch(Exception e)
		{
			Message msg = new Message();
			msg.what=-1;
			msg.obj = "�ϴ�ʧ�ܣ�";
			mHandler.sendMessage(msg);
		}		
	}	
	
	
	//�̷߳�����Ϣ��handler
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(RepairActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
			if(msg.what == 0)
			{
				if(!SetRepairState(Vault.REPAIRED_UPLOADED))
					Toast.makeText(RepairActivity.this, "�޸�ά�޽��״̬ʧ�ܡ�", Toast.LENGTH_SHORT).show();	
			}
		}
	};
	
	/**
	 * �޸�ά��״̬
	 * @param state
	 */
	public boolean SetRepairState(String state) {
		try
		{
			SQLiteDatabase db = openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
			db.execSQL("update T_REPAIR_TASK set REPAIR_STATE=? where id=?", new String[]{state, uuid} );
			db.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}		
	}
	
}
