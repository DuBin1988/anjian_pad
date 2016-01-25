package com.aofeng.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aofeng.safecheck.activity.*;
import com.aofeng.utils.CustomMultiPartEntity.ProgressListener;

public class HttpMultipartPost extends
		AsyncTask<String, ProgressIndicator, Boolean> {
	ProgressDialog pd;
	long totalSize;
	Context context;
	public static String UPLOAD_URL = Vault.files_url + "savefile";

	public HttpMultipartPost(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("上传开始......");
		pd.setCancelable(false);
		pd.show();
	}

	// appease compiler complaing
	private int i;

	@Override
	protected Boolean doInBackground(final String... fileNames) {
		final int n = fileNames.length /3;
		for (i = 0; i < fileNames.length-1; i = i + 3) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext httpContext = new BasicHttpContext();
			String url = this.UPLOAD_URL + "?FileName="
					+ URLEncoder.encode(fileNames[i]) + "&BlobId="
					+ URLEncoder.encode(fileNames[i + 2])
					+ "&EntityName=t_blob";
			HttpPost httpPost = new HttpPost(url);

			try {
		
				CountableFileEntity entity = new CountableFileEntity(new File(	fileNames[i]), "binary/octet-stream",
						new ProgressListener() {
							@Override
							public void transferred(long num) {
								ProgressIndicator idc = new ProgressIndicator();
								idc.progress = (int)((100.0*i/3/n)+(num / (float) totalSize)* 100/n);
								idc.hint = "上传文件:" + fileNames[i + 1];
								publishProgress(idc);
							}
						});
				totalSize= entity.getContentLength();
				httpPost.setEntity(entity);
				
				HttpResponse response = httpClient.execute(httpPost, httpContext);
				response.getEntity();

			} catch (Exception e) {
				return false;
			}
		}
		
		HttpPost httpPost = new HttpPost(Vault.IIS_URL + "update");
		try {
			httpPost.setEntity(new StringEntity(fileNames[fileNames.length-1], "UTF8"));
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			String result =  EntityUtils.toString(response.getEntity(), "UTF8");
			JSONObject obj = new JSONObject(result);
			if(obj.getString("ok").equals("nok"))
				return false;
			else
				return true;
		} catch (Exception e) {
			ProgressIndicator idc = new ProgressIndicator();
			idc.progress = 100;
			idc.hint = "上传安检记录出错！";
			publishProgress(idc);
			return false;
		} 
	}

	@Override
	protected void onProgressUpdate(ProgressIndicator... indicator) {
		ProgressIndicator idc = indicator[0];
		pd.setProgress(idc.progress);
		pd.setMessage(idc.hint);
	}

	@Override
	protected void onPostExecute(Boolean done) {
		pd.dismiss();
		if (done) {
			Toast.makeText(context, "上传成功！", Toast.LENGTH_SHORT).show();
			Util.SetBit(context, Vault.UPLOAD_FLAG, ((IndoorInspectActivity)(context)).paperId);
		} else {
			Toast.makeText(context, "数据已保存在平板。上传失败，请检查是否网络原因！", Toast.LENGTH_SHORT).show();
			Util.ClearBit(context, Vault.UPLOAD_FLAG, ((IndoorInspectActivity)(context)).paperId);
		}
	}
}

class ProgressIndicator {
	public int progress;
	public String hint;
}
