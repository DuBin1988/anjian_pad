package com.aofeng.utils;

public class Vault {

	public static String files_url = "http://192.168.31.198:8080/rs/BMPService/";
	public static String SEARCH_URL = "http://192.168.31.198:8080/rs/";
	public static String DB_URL = "http://192.168.31.198:8080/rs/db/";
	public static String IIS_URL = "http://192.168.31.198:8080/rs/iis/";
	public static String AUTH_URL = "http://192.168.31.198:83/rs/user/";
	public static String downloadURL = "http://192.168.31.198:8080/safecheck.apk";
	public static String checkVersionURL = "http://192.168.31.198:8080/rs/db/one/from%20t_singlevalue%20where%20name='safecheck�汾��'";
	
	/**
	 * ��¼�û���
	 */
	public static String USER_NAME = "USER_NAME";
	/**
	 * ��¼����
	 */
	public static String PASSWORD ="ENCRYPT";	
	/**
	 * ������Ա����,ȡ��½��Ϣ���û���
	 */
	public  static String CHECKER_NAME ="CHECKER_NAME";
	/**
	 * �û�ID
	 */
	public static String USER_ID = "USER_ID";
	
//	public static String  SEARCH_URL = "http://60.214.209.189:83/safecheckDB/rs/";
//	public static String  DB_URL = "http://60.214.209.189:83/safecheckDB/rs/db/";
//	public static String  IIS_URL = "http://60.214.209.189:83/safecheckDB/rs/iis/";
//	public static String AUTH_URL = "http://60.214.209.189:83/safecheckres/rs/user/";
//	public static  String downloadURL = "http://60.214.209.189:83/safecheckDB/safecheck.apk";
//	public static  String checkVersionURL = "http://60.214.209.189:83/safecheckDB/rs/db/one/from%20t_singlevalue%20where%20name='safecheck�汾��'";

	/**
	 * �ϴ����
	 */
	public static int UPLOAD_FLAG = 1;
	
	/**
	 * �Ѽ���
	 */
	public static int INSPECT_FLAG = 2;
	
	/**
	 * �������
	 */
	public static int NEW_FLAG = 4;
	
	/**
	 * ɾ�����
	 */
	public static int DELETE_FLAG = 8;
	
	/**
	 * ά�ޱ��
	 */
	public static int REPAIR_FLAG = 16;
	
	/**
	 * �ܼ���
	 */
	public static int DENIED_FLAG = 32;
	
	/**
	 * ���˱��
	 */
	public static int NOANSWER_FLAG = 64;
	

	public static String REPAIRED_NOT="δά��";
	public static String REPAIRED_UNUPLOADED="δ�ϴ�";
	public static String REPAIRED_UPLOADED="���ϴ�";
	
	public static  String packageName= "com.aofeng.safecheck";
	public static  String apkName ="download.apk";
	public static String appID="qhsafecheck";
}
