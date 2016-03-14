package com.stone.panoramaschool.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.stone.panoramaschool.MyApplication;
import com.stone.panoramaschool.entity.AuthKey;
import com.stone.panoramaschool.entity.FormFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Http 相关实用类
 * 
 * @author stone
 */
@SuppressLint("SimpleDateFormat")
public class HttpUtils {

	public HttpUtils() {
		// TODO Auto-generated constructor stub
	}

	public String getJsonContent(String url_path) {

		try {
			URL url = new URL(url_path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(4000); // 请求超时时间4s
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			int code = connection.getResponseCode(); // 返回状态码
			if (code == 200) {
				// 或得到输入流，此时流里面已经包含了服务端返回回来的JSON数据了,此时需要将这个流转换成字符串
				String jsonString = changeInputStream(connection
						.getInputStream());
				return jsonString;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("", "ERROR:" + e.getMessage());
		}
		return "";
	}

	private String changeInputStream(InputStream inputStream) {
		// TODO Auto-generated method stub
		String jsonString = "";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int length = 0;
		byte[] data = new byte[1024];
		try {
			while (-1 != (length = inputStream.read(data))) {
				outputStream.write(data, 0, length);
			}
			// inputStream流里面拿到数据写到ByteArrayOutputStream里面,
			// 然后通过outputStream.toByteArray转换字节数组，再通过new String()构建一个新的字符串。
			jsonString = new String(outputStream.toByteArray());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return jsonString;
	}

	/**
	 * @param url
	 *            服务的完整地址
	 * @param data
	 *            数据对象
	 * @param contentType
	 *            Request的ContentType
	 * @return 返回的json字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String doPost(String url, JSONStringer params)
			throws Exception {

		String strResp = "";
		HttpPost request = new HttpPost(url);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		MyApplication app = new MyApplication();

		try {
			StringEntity entity = new StringEntity(params.toString(), "UTF-8");

			request.setEntity(entity);
			// 向WCF服务发送请求
			DefaultHttpClient httpClient = app.createHttpClient();
			// DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(request);

			// 判断是否成功
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(response.getEntity());
				// 解析json
				JSONObject json1 = new JSONObject(strResp);
				// 返回json值
				strResp = json1.getString("d");
			} else {

				strResp = "error";
			}
		} catch (Exception e) {
			throw new Exception("服务器响应异常，请稍后再试");
		}
		request.abort();
		Log.d("Response", strResp);
		return strResp;

	}

	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;

		try {
			myFileURL = new URL(url);

			// 获得连接
			HttpURLConnection conn;

			conn = (HttpURLConnection) myFileURL.openConnection();

			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (MalformedURLException e) {
			Log.d("BITMAPERROR", "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("BITMAPERROR", "" + e.getMessage());
			e.printStackTrace();
		}

		return bitmap;

	}

	public static File getHttpFile(String url, String name, String path) {
		url = url + name;
		URL myFileURL;
		// Bitmap bitmap=null;
		File file = null;
		SimpleDateFormat smf = new SimpleDateFormat("yyyMMddhhmmss");
		try {
			myFileURL = new URL(url);

			// 获得连接
			HttpURLConnection conn;

			conn = (HttpURLConnection) myFileURL.openConnection();

			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// byte[] content=null;
			// is.read(content);
			// 解析得到图片
			file = new File(path + smf.format(new Date()) + name);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);

				fos.write(IOUtils.toByteArray(is));
				fos.flush();
			} else {
				file = null;
			}

			// bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (MalformedURLException e) {
			Log.d("BITMAPERROR", "" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("BITMAPERROR", "" + e.getMessage());
			e.printStackTrace();
		}

		// return bitmap;
		return file;
	}

	/**   
	* 直接通过HTTP协议提交数据到服务器,实现表单提交功能   
	* @param actionUrl 上传路径   
	* @param params 请求参数 key为参数名,value为参数值   
	* @param file 上传文件   
	*/   
	public static String post(String actionUrl, Map<String, String> params, FormFile[] files) {    
	    try {               
	        String BOUNDARY ="---7d4a6d158c9"; //数据分隔线    
	        String MULTIPART_FORM_DATA = "multipart/form-data";    
	            
	        URL url = new URL(actionUrl);    
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();    
	        conn.setDoInput(true);//允许输入    
	        conn.setDoOutput(true);//允许输出    
	        conn.setUseCaches(false);//不使用Cache    
	        conn.setRequestMethod("POST");              
	        conn.setRequestProperty("Connection", "Keep-Alive");    
	        conn.setRequestProperty("Charset", "UTF-8");    
	        conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);    

	        StringBuilder sb = new StringBuilder();    
	            
	        //上传的表单参数部分，格式请参考文章    
	        for (Map.Entry<String, String> entry : params.entrySet()) {//构建表单字段内容    
	            sb.append("–");    
	            sb.append(BOUNDARY);    
	            sb.append("\r\n");    
	            sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");    
	            sb.append(entry.getValue());    
	            sb.append("\r\n");    
	        }    
	        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());    
	        outStream.write(sb.toString().getBytes());//发送表单字段数据    
	           
	        //上传的文件部分，格式请参考文章    
//	        for(FormFile file : files){    
//	            StringBuilder split = new StringBuilder();    
//	            split.append("–");    
//	            split.append(BOUNDARY);    
//	            split.append("\r\n");    
//	            split.append("Content-Disposition: form-data;name=\""+ file.getFormname()+"\";filename=\""+ file.getFilname() + "\"\r\n");    
//	            split.append("Content-Type: "+ file.getContentType()+"\r\n\r\n");    
//	            outStream.write(split.toString().getBytes());    
//	            outStream.write(file.getData(), 0, file.getData().length);    
//	            outStream.write("\r\n".getBytes());    
//	        }    
	        byte[] end_data = ("–" + BOUNDARY + "–\r\n").getBytes();//数据结束标志             
	        outStream.write(end_data);    
	        outStream.flush();
	        
	        int cah = conn.getResponseCode();    
	        if (cah != 200) throw new RuntimeException("请求url失败");    
	        InputStream is = conn.getInputStream();    
	        int ch;    
	        StringBuilder b = new StringBuilder();    
	        while( (ch = is.read()) != -1 ){    
	            b.append((char)ch);    
	        }    
	        outStream.close();    
	        conn.disconnect();    
	        return b.toString();    
	    } catch (Exception e) {    
	        throw new RuntimeException(e);    
	    }    
	}   
	 //登录保存session
	public static boolean login(Context con,String url, Map<String ,String> rawParams) {

		HttpPost post = new HttpPost(url); 
		HttpContext context = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();
		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		HttpResponse response;
		MyApplication app = new MyApplication();
		DefaultHttpClient httpClient = app.createHttpClient();
		//httpClient.getCookieStore().setCookies(cookie);
		Boolean bo=false;
		String strResp;
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();  
			           for(String key : rawParams.keySet())
			           {  
			                //封装请求参数  
			               params.add(new BasicNameValuePair(key , rawParams.get(key)));  
			           }  
			          // 设置请求参数  
			           post.setEntity(new UrlEncodedFormEntity(  
			              params,HTTP.UTF_8));

			response = httpClient.execute(post, context);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 根据你的逻辑，判断返回的值是不是表示已经登录成功
				strResp = EntityUtils.toString(response.getEntity());
				Log.d("HTML", strResp);
				//StringUtil.saveInfo(SpotActivity.this, "auth_key", strResp);
					List cookies = cookieStore.getCookies();
					if (!cookies.isEmpty()) {
						List<AuthKey>authList=new ArrayList<AuthKey>();
						for (int i = cookies.size(); i > 0; i--) {
							
							Cookie cookie = (Cookie) cookies.get(i - 1);
							
							AuthKey auth=new AuthKey();
							auth.setName(cookie.getName());
							auth.setValue(cookie.getValue());
							auth.setDomain(cookie.getDomain());
							authList.add(auth);
//							if (cookie.getName().equalsIgnoreCase("jsessionid")) {
//								// 使用一个常量来保存这个cookie，用于做session共享之用
//								bo=true;
//							}
						}
						bo=true;
						String cookiesJson=GsonUtil.getJsonValue(authList);
						Log.d("authList", cookiesJson);
						StringUtil.saveInfo(con, "authList", cookiesJson);
					}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			bo=false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bo=false;
		}
		return bo;
	}
	//提交form
	public static boolean postForm(Context con,String url, Map<String ,String> rawParams){
		
		 URL urI;
		 boolean bo=false;
		try {
			urI = new URL(url);
			  HttpURLConnection conn = (HttpURLConnection) urI.openConnection();
		        conn.setRequestMethod("POST");// 提交模式
		        conn.setConnectTimeout(10000);//连接超时 单位毫秒
		        conn.setReadTimeout(5000);//读取超时 单位毫秒
		        conn.setDoOutput(true);// 是否输入参数
		        conn.setInstanceFollowRedirects(false); 

		        StringBuffer params = new StringBuffer();
		        // 表单参数与get形式一样
		        for(String key : rawParams.keySet())
		           {  
		                //封装请求参数  
		        	params.append(key).append("=").append(rawParams.get(key)).append("&");
		           }  
		        byte[] bypes = params.toString().getBytes();
		        conn.getOutputStream().write(bypes);// 输入参数
		     // 取得sessionid. 
		       
		        InputStream inStream=conn.getInputStream();
		        
		        Map map = conn.getHeaderFields();
		        List<String> list = (List) map.get("Set-Cookie");
		        if(list!=null&&list.size()>1){
		        	 bo=true;
		        	 String cookieval= GsonUtil.getJsonValue(list);
				     StringUtil.saveInfo(con, "cookies", cookieval);
				     Log.d("SESSIONID&&&&&&&&", cookieval);
		        }
		       
		        
//		        String cookieval = conn.getHeaderField("Set-Cookie"); 
//		        if(cookieval != null) { 
//		       StringUtil.saveInfo(con, "cookies", cookieval);
//		        bo=true;
//		        Log.d("SESSIONID&&&&&&&&", cookieval);
//		        }
		        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		        byte[] buffer = new byte[1024];
		        int len = 0;
		        while( (len = inStream.read(buffer)) !=-1 ){
		            outStream.write(buffer, 0, len);
		        }
		        byte[] data = outStream.toByteArray();//网页的二进制数据
		        JsoUpUtils.getSpot(new String(data));
		        outStream.close();
		        inStream.close();
		        Log.d("********RESULT********", new String(data));
		        JsoUpUtils.getSpot(new String(data));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	       // System.out.println(new String(StreamTool.readInputStream(inStream), "gbk"));
	        return bo;
	}
	
}
