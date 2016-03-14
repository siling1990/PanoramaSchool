package com.stone.panoramaschool.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

/**
 * @ClassName: HttpUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Stone
 * @date 2015-7-8 下午2:42:25
 * 
 */
public class HttpUtil {
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	private static DefaultHttpClient client;
	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(File file, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"fup\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: image/pjpeg; charset=" + CHARSET
						+ LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */

				int res = conn.getResponseCode();
				System.out.println("res=========" + res);
				if (res == 200) {
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
				} else {
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 提交表单
	 * */
	public static String doPostForm(Map<String, String> parmas, String url,
			boolean isLogin, Context context) {

		// 封装数据
		if (!isLogin) {
			parmas.put(Constants.TOKEN,
					StringUtil.getInfo(context, Constants.TOKEN, ""));
			parmas.put("id_user",
					StringUtil.getInfo(context, Constants.USERID, ""));
		}

		client = new DefaultHttpClient();// http客户端
		 // 请求超时
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
        // 读取超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT    );

		Log.d("********HTTPURL********", url);
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		if (parmas != null) {
			Set<String> keys = parmas.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				pairs.add(new BasicNameValuePair(key, parmas.get(key)));
			}
		}
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"GBK");
			httpPost.setEntity(p_entity);
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			return convertStreamToString(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * 提交包含文件的表单
	 * */
	public static String doPostFileForm(Map<String, String> parmas, String url,
			boolean isLogin, Context context, File file) {
		final ContentType TEXT_PLAIN = ContentType.create("text/plain",  
	            Charset.forName("GBK"));
		client = new DefaultHttpClient();
		// 请求超时
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
		        // 读取超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT    );
		if (!isLogin) {
			parmas.put(Constants.TOKEN,
					StringUtil.getInfo(context, Constants.TOKEN, ""));
			parmas.put("id_user",
					StringUtil.getInfo(context, Constants.USERID, ""));
		}
		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			HttpPost httppost = new HttpPost(url);// 创建 HTTP POST 请求
			
			if(file!=null){
				builder.addBinaryBody("file", file, ContentType.create("image/jpeg"), file.getName());
			}
			if (parmas != null) {
				Set<String> keys = parmas.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					builder.addPart(key, new StringBody(parmas.get(key),TEXT_PLAIN));
				}
			}
			HttpEntity reqEntity = builder.build();
			httppost.setEntity(reqEntity);
			HttpResponse response = client.execute(httppost);
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				InputStream content = resEntity.getContent();
				return convertStreamToString(content);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	/**
	 * 转换返回结果
	 * */
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new InputStreamReader(is,"GBK"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.d("***********HTTPTRSULT**********", sb.toString());
		return sb.toString();
	}
}
