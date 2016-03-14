package com.stone.panoramaschool.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * String 实用类
 * 
 * @author stone
 * */
public class StringUtil {

	/**
	 * 存储用户名密码
	 * */
	public static void saveLoginInfo(Context context, String username,
			String password) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		// 设置参数
		if(!isEmpty(username)){
			editor.putString("username", username);
		}else{
			editor.remove("username");
		}
		if(!isEmpty(password)){
			editor.putString("password", password);
		}else{
			editor.remove("password");
		}
		// 提交
		editor.commit();
	}

	/**
	 * 存储信息
	 * */
	public static void saveInfo(Context context, String name,String value) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		// 设置参数
		
		if(!isEmpty(value)){
			editor.putString(name, value);
		}else{
			editor.remove(name);
		}
		// 提交
		editor.commit();
	}
	/**
	 * 存储信息
	 * @return 
	 * */
	public static String getInfo(Context context,String name,String def) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sharedPre.getString(name,"");
		
	}
	/**
	 * 删除用户名密码
	 * */
	public static void removeLoginInfo(Context context) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		// 设置参数
		editor.remove("username");
		editor.remove("password");
		// 提交
		editor.commit();
	}

	/**
	 * 标记是否登录
	 * */
	public static void signLogin(Context context, Boolean flag) {

		SharedPreferences sharedPre = context.getSharedPreferences("loginFlag",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		// 设置参数
		editor.putBoolean("flag", flag);
		// 提交
		editor.commit();
	}

	/**
	 * 判断字符串是否为空（null "" ）
	 * */
	public static Boolean isEmpty(String string) {

		if (null != string && !string.equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * 若字符串为空则用相应字符串替换
	 * */

	public static String emptyInsteadWIth(String string, String instead) {

		if (null != string && !string.equals("")) {
			return string;
		}
		return instead;
	}

	/**
	 * 拆分字符串"/Date(1401120000000+0800)/"
	 * */
	public static String[] split(String str) {
		String[] str1=new String[2];
		int index1;
		int index2;
		int index3;
		String r2;
		try {
			index1=str.indexOf("(");
			index2=str.indexOf(")");
			r2=str.substring(index1+1, index2);
			index3=r2.indexOf("+");
			str1[0]=r2.substring(0, index3);
			str1[1]=r2.substring(index3+1);
		} catch (Exception e) {
			str1=null;
		}
		return str1;
	}
	
	public static void saveOAuth(Context context,List<Cookie>cookies) {
		SharedPreferences preferences = context.getSharedPreferences("base64",
				context.MODE_PRIVATE);
		// 创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			// 将对象写入字节流
			oos.writeObject(cookies);
			// 将字节流编码成base64的字符窜
			String oAuth_Base64 = new String(Base64.encodeBase64(baos
					.toByteArray()));
			Editor editor = preferences.edit();
			editor.putString("cookies", oAuth_Base64);

			editor.commit();
		} catch (IOException e) {
			// TODO Auto-generated
		}
		Log.i("ok", "存储成功");
	}
	public static List<Cookie> readOAuth(Context context) {
		List<Cookie> cookies = null;
		SharedPreferences preferences = context.getSharedPreferences("base64",
				context.MODE_PRIVATE);
		String productBase64 = preferences.getString("cookies", "");
				
		//读取字节
		byte[] base64 = Base64.decodeBase64(productBase64.getBytes());
		
		//封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			//再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				//读取对象
				cookies = (List<Cookie>) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cookies;
	}
}
