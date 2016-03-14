package com.stone.panoramaschool.util;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;

public class FileUtil {
	/**
	 * 将下载好的图片存放到文件中
	 * @param path 图片路径
	 * @param mActivity
	 * @param imageName 图片名字
	 * @param bitmap 图片
	 * @return
	 */
	public static boolean setStringToFile(String path,Activity mActivity,String jsonName,String content){
		File file = null;
		String real_path = "";
		try {
			if(Util.getInstance().hasSDCard()){
				real_path = Util.getInstance().getExtPath() + (path != null && path.startsWith("/") ? path : "/" + path);
			}else{
				real_path = Util.getInstance().getPackagePath(mActivity) + (path != null && path.startsWith("/") ? path : "/" + path);
			}
			file = new File(real_path, jsonName);
			if(!file.exists()){
				File file2 = new File(real_path + "/");
				file2.mkdirs();
			}
			file.createNewFile();
			FileOutputStream fos = null;
			if(Util.getInstance().hasSDCard()){
				fos = new FileOutputStream(file);
			}else{
				fos = mActivity.openFileOutput(jsonName, Context.MODE_PRIVATE);
			}
			
			if (jsonName != null && jsonName.contains(".data")){
				fos.write(content.getBytes());
			}
			else{
				return false;
			}
			fos.flush();
			if(fos != null){
				fos.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
