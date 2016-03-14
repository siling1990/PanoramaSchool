package com.stone.panoramaschool.adapter;

import java.util.ArrayList;

import com.stone.panoramaschool.R;
import com.stone.panoramaschool.UniversityListActivity;
import com.stone.panoramaschool.entity.City;
import com.stone.panoramaschool.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 *com.stone.panoramaschool.adapter
 *
 * @author stone
 *
 * 2014年10月26日/上午12:36:28
 */
@SuppressLint("NewApi")
public class CityAdapter extends BaseAdapter{
	
	public class Zujian{
		public TextView textCityName;
		public Button btNext;
		public LinearLayout layoutCityComment;
	}

	private Context context;
	private City city;
	private ArrayList<City> cityList;
	private Zujian zujian;
	private Resources resources;
	
	public CityAdapter(Context context,ArrayList<City> cityList){
		this.context=context;
		this.cityList=cityList;
	}
	@Override
	public int getCount() {
		return cityList.size();
	}

	@Override
	public Object getItem(int position) {
		return cityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null || position < cityList.size()) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_city, null);
			zujian=new Zujian();
			zujian.layoutCityComment = (LinearLayout) convertView.findViewById(R.id.layoutCityComment);
			zujian.textCityName = (TextView) convertView
					.findViewById(R.id.textCityName);
			zujian.btNext = (Button) convertView
					.findViewById(R.id.btNext);
			
			convertView.setTag(zujian);
		}else{
			zujian=(Zujian)convertView.getTag();  
		}
		
		city=cityList.get(position);
		resources=context.getResources();
		zujian.layoutCityComment.removeAllViewsInLayout();
		if(city.getCityScore()>5){
			for(int i=0;i<5;i++){
				ImageView image=new ImageView(context);
				image.setBackground(resources.getDrawable(R.drawable.heart_full));
				
				zujian.layoutCityComment.addView(image);
				
			}
		}else{
			for(int i=0;i<city.getCityScore();i++){
				ImageView image=new ImageView(context);
				image.setBackground(resources.getDrawable(R.drawable.heart_full));
				zujian.layoutCityComment.addView(image);
			}
			for(int i=0;i<(5-city.getCityScore());i++){
				ImageView image=new ImageView(context);
				image.setBackground(resources.getDrawable(R.drawable.heart_line));
				zujian.layoutCityComment.addView(image);
				
			}
		}
		
		
		zujian.textCityName.setText(city.getCityName());
		zujian.btNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent =new Intent(context,UniversityListActivity.class);
				StringUtil.saveInfo(context, "cityId", ""+city.getCityId());
				context.startActivity(intent);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent =new Intent(context,UniversityListActivity.class);
				StringUtil.saveInfo(context, "cityId", ""+city.getCityId());
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	

}
