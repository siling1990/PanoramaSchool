package com.stone.panoramaschool.adapter;

import java.util.ArrayList;
import com.stone.panoramaschool.PanoramaGLActivity;
import com.stone.panoramaschool.R;
import com.stone.panoramaschool.entity.Spot;
import com.stone.panoramaschool.util.StringUtil;
import com.stone.panoramaschool.window.CircleImageView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 *com.stone.panoramaschool.adapter
 *
 * @author stone
 *
 * 2014年11月1日/下午9:14:25
 */
public class SpotAdapter extends BaseAdapter {
	public class ViewHolder {
		public TextView textSpotName;
		public Button btNext;
		public LinearLayout layoutSpotComment;
		public CircleImageView imageSpot;
	}

	private Context context;
	private Spot spot;
	private ArrayList<Spot> spotList;
	private ViewHolder viewHolder;
	private Resources resources;
	private Bitmap bitmap;
	private int po=0;
	private ListView listView;
	private Handler myHandler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				Log.d("BITMAPRECEIVE", ""+msg.arg1);
				Bitmap bm=(Bitmap)msg.obj;
				Log.d("BITMAPINFO", bm.toString());
				updateView(msg.arg1,bm);
			}
			
		};
	};

	public SpotAdapter(Context context, ArrayList<Spot> spotList) {
		this.context = context;
		this.spotList = spotList;
	}

	@Override
	public int getCount() {
		return spotList.size();
	}

	@Override
	public Object getItem(int position) {
		return spotList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null || position < spotList.size()) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_spot, null);
			viewHolder = new ViewHolder();
			viewHolder.layoutSpotComment = (LinearLayout) convertView
					.findViewById(R.id.layoutSpotComment);
			viewHolder.textSpotName = (TextView) convertView
					.findViewById(R.id.textSpotName);
			viewHolder.btNext = (Button) convertView.findViewById(R.id.btNext);
			viewHolder.imageSpot=(CircleImageView)convertView.findViewById(R.id.imageSpot);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		spot = spotList.get(position);
		resources = context.getResources();
		viewHolder.layoutSpotComment.removeAllViewsInLayout();
//		if(ViewHolder.imageSpot.getBackground()==null){
//			ViewHolder.imageSpot.setBackground(resources.getDrawable(R.drawable.default_img));
//		}
		
		
		po=position+1;
			
//			new Thread(new Runnable() {
//				final int ind =po;
//				@Override
//				public void run() {
//					bitmap = HttpUtils.getHttpBitmap(spot.getSpotImage());
//					if(bitmap!=null){
//						Log.d("BITMAP", ""+bitmap.getByteCount());
//					}else{
//						Log.d("BITMAP", "NULL");
//					}
//					
//					Message msg=Message.obtain();
//					msg.what=1;
//					msg.obj=bitmap;
//					msg.arg1=ind;
//					Log.d("POSITION2", ""+ind);
//					myHandler.sendMessage(msg);
//				}
//			}).start();
		
		int spotScore=1;
		try{
			spotScore=Integer.parseInt(spot.getScore());
		}catch(Exception e){
			
		}
		if (spotScore > (spotScore % 5)) {
			for (int i = 0; i < 5; i++) {
				ImageView image = new ImageView(context);
				image.setBackground(resources
						.getDrawable(R.drawable.heart_full));

				viewHolder.layoutSpotComment.addView(image);

			}
		} else {
			for (int i = 0; i < spotScore; i++) {
				ImageView image = new ImageView(context);
				image.setBackground(resources
						.getDrawable(R.drawable.heart_full));
				viewHolder.layoutSpotComment.addView(image);
			}
			for (int i = 0; i < (5 - spotScore); i++) {
				ImageView image = new ImageView(context);
				image.setBackground(resources
						.getDrawable(R.drawable.heart_line));
				viewHolder.layoutSpotComment.addView(image);

			}
		}

		viewHolder.textSpotName.setText(spot.getSpotName());
		final int pos=position;
		viewHolder.btNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,
						PanoramaGLActivity.class);
			//	StringUtil.saveInfo(context, "spotId", "" + spotList.get(pos).getSpotId());
				StringUtil.saveInfo(context, "spotId", "" + pos);
				context.startActivity(intent);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,
						PanoramaGLActivity.class);
			//	StringUtil.saveInfo(context, "spotId", "" + spotList.get(pos).getSpotId());
				StringUtil.saveInfo(context, "spotId", "" + pos);
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}
	private void updateView(int index,Bitmap bm){
		int visiblePosition = getListView().getFirstVisiblePosition(); 
		Log.d("VIEWPOSITION1", ""+visiblePosition);
		Log.d("VIEWPOSITION2", ""+index);
		View view = listView.getChildAt(index- visiblePosition);
		viewHolder.imageSpot=(CircleImageView)view.findViewById(R.id.imageSpot);
		viewHolder.imageSpot.setImageBitmap(bm);
		//ViewHolder.imageSpot.setBackground(resources.getDrawable(R.drawable.default_bg));
	}
}