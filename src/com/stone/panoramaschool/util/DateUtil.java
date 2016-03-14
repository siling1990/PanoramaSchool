package com.stone.panoramaschool.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
/**
 * 日期实用类
 * @author stone
 * */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
	/**
	 * 把Date 转化成特定格式的String（yyMMddhhmmss）
	 * */
	public static String dateToString(Date date,String pattern){
		
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		if(date==null){
			date=new Date();
		}
		
		return sdf.format(date);
	}
	/**
	 * 把String 转换成Date
	 * @throws ParseException
	 * */
	public static Date stringToDate(String string) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat();
		return sdf.parse(string);
	}
	/**
	 * 时间差（年）
	 * */
	public static int yearsBetween(Date dateFormer,Date dateLatter){
		if(dateFormer==null){
			dateFormer=new Date();
		}
		Calendar cal=Calendar.getInstance();
		
		cal.setTime(dateLatter);
		int lYear=cal.get(Calendar.YEAR);
		
		cal.setTime(dateFormer);
		int fYear=cal.get(Calendar.YEAR);
		
		return lYear>fYear?lYear-fYear:fYear-lYear;
	}
	/**
	 * 时间减（年）
	 * */
public static Date yearsAfterBefore(Date date,int year){
		
		Calendar cal=Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.YEAR, year);
		
		
		return cal.getTime();
	}
/**
 * 时间加减（时）
 * */
public static Date hoursAfterBefore(Date date,int hour){
	
	Calendar cal=Calendar.getInstance();
	
	cal.setTime(date);
	cal.add(Calendar.HOUR, hour);
	
	
	return cal.getTime();
}
/**
 * 毫秒变日期
 * */
public  static Date millsToDate(String[] str){
	
	
	 long l;
	 String str1;
	 try{
		 l=Long.parseLong(str[0]); 
		 str1=str[1];
	 }catch(Exception e){
		 l=new Date().getTime();
		 str1="0800";
	 }
	
	 Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+"+str1));            
	 calendar.setTimeInMillis(l);  
	 Date d=calendar.getTime();
	return hoursAfterBefore(d, 8);
}
}
