package com.stone.panoramaschool.util;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogUtil {
	public static AlertDialog.Builder builder;
	public static void showAlertDialogConfirm(Context context,String msg){
			builder=new AlertDialog.Builder(context);
		builder
		.setTitle("提示")
		.setMessage(msg)
		.setPositiveButton("确认",null).setNegativeButton("取消", null)
		.show();
		
	}
	public static void showAlertDialog(Context context,String msg){
		builder=new AlertDialog.Builder(context);
		builder
		.setTitle("提示")
		.setMessage(msg)
		.setPositiveButton("确认",null)
		.show();
		
	}

}
