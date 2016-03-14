package com.stone.panoramaschool;


import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MyApplication extends Application {
	private DefaultHttpClient mHttpClient = null;
	private static final String CHARSET = HTTP.UTF_8;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(this);
		mHttpClient = this.createHttpClient();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		this.shutdownHttpClient();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		this.shutdownHttpClient();
	}
	
	/**����HttpClientʵ��
	 * @return
	 */
	public DefaultHttpClient createHttpClient(){
		HttpParams params = new BasicHttpParams();
		//���û�������
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		//��ʱ����
		/*�����ӳ���ȡ���ӵĳ�ʱʱ��*/
		ConnManagerParams.setTimeout(params, 1000);
		/*���ӳ�ʱ*/
		HttpConnectionParams.setConnectionTimeout(params, 7000);
		/*����ʱ*/
		HttpConnectionParams.setSoTimeout(params, 7000);
		//����HttpClient֧��HTTp��HTTPS����ģʽ
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		//ʹ���̰߳�ȫ�����ӹ���������HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		DefaultHttpClient client = new DefaultHttpClient(conMgr, params);
		return client;
	}
	private void shutdownHttpClient(){
		if(mHttpClient != null && mHttpClient.getConnectionManager() != null){
			mHttpClient.getConnectionManager().shutdown();
		}
	}
	public DefaultHttpClient getHttpClient(){
		return mHttpClient;
	}

}
