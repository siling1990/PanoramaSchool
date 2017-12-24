package com.stone.panoramaschool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.stone.panoramaschool.entity.Comment;
import com.stone.panoramaschool.entity.Spot;
import com.stone.panoramaschool.util.AlertDialogUtil;
import com.stone.panoramaschool.util.Constants;
import com.stone.panoramaschool.util.FileUtil;
import com.stone.panoramaschool.util.GsonUtil;
import com.stone.panoramaschool.util.HttpUtil;
import com.stone.panoramaschool.util.StringUtil;
import com.stone.panoramaschool.window.CircleImageView;
import com.stone.panoramaschool.window.MyProgressDialog;
import com.stone.panoramaschool.window.PullListView;
import com.stone.panoramaschool.window.PullListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SpotActivity extends Activity {

	private TextView textSpotName;
	private EditText editComm;
	private ImageView imageLogo;
	private PullListView listViewComment;
	private Spot spot;
	private Button btBack, btRefresh, btSubmit;
	private ArrayList<Comment> commentList;
	private CommentAdapter commentAdapter;
	private ProgressBar moreProgressBar;

	private TextView summary, mapTxt;
	private Button btMore, btLess;

	private Map<String, String> map;
	private Gson gson;
	private String msgs;
	private MyProgressDialog m_customProgrssDialog;
	private GetPTask getPTask;
	private CommTask commTask;// 结果处理

	private ImageLoader imageLoader = ImageLoader.getInstance();//
	private DisplayImageOptions options; // 显示图像设置

	private ImageView imgAd, imgDel;
	private RelativeLayout ad;
	private Intent intent;
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.LOAD_MORE_SUCCESS:
				moreProgressBar.setVisibility(View.GONE);
				commentAdapter.notifyDataSetChanged();
				listViewComment.setSelectionfoot();
				break;

			case Constants.LOAD_NEW_INFO:
				commentAdapter.notifyDataSetChanged();
				listViewComment.onRefreshComplete();
				break;
			case Constants.LOAD_PL_SUCCESS:
				commentAdapter.notifyDataSetChanged();
				listViewComment.onRefreshComplete();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_spot_g);

		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_img) // 设置图片下载期间显示的图�?
				.showImageForEmptyUri(R.drawable.default_img) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_img) // 设置图片加载或解码过程中发生错误显示的图�?
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存�?
				.cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象

		listViewComment = (PullListView) findViewById(R.id.listViewComment);
		textSpotName = (TextView) findViewById(R.id.textSpotName);
		editComm = (EditText) findViewById(R.id.editComm);
		btBack = (Button) findViewById(R.id.btBack);
		btRefresh = (Button) findViewById(R.id.btRefresh);
		btSubmit = (Button) findViewById(R.id.btSubmit);

		imgAd = (ImageView) findViewById(R.id.imgAd);
		imgDel = (ImageView) findViewById(R.id.imgDel);
		ad = (RelativeLayout) findViewById(R.id.ad);

		imgAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(SpotActivity.this, WebActivity.class);
				// intent.putExtra("hTitle","广告");
				intent.putExtra("isOpenwindow", false);
				intent.putExtra("url", spot.getQq().split("&")[1]);
				startActivity(intent);

			}
		});

		imgDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.setVisibility(View.GONE);
			}
		});

		btBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SpotActivity.this.finish();

			}
		});
		btRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				load();
			}
		});

		btSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitComm(editComm.getText().toString());
			}
		});

		LayoutInflater inflater = LayoutInflater.from(this);
		View unVuewHead = inflater.inflate(R.layout.spot_head, null);
		summary = (TextView) unVuewHead.findViewById(R.id.summary);
		mapTxt = (TextView) unVuewHead.findViewById(R.id.mapTxt);
		imageLogo = (ImageView) unVuewHead.findViewById(R.id.imageLogo);
		btMore = (Button) unVuewHead.findViewById(R.id.btMore);
		btLess = (Button) unVuewHead.findViewById(R.id.btLess);

		btMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtil.isEmpty(spot.getSpotInstruction())) {
					btMore.setVisibility(View.GONE);
					btLess.setVisibility(View.VISIBLE);
					summary.setText(spot.getSpotInstruction());
				}
			}
		});
		btLess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtil.isEmpty(spot.getSpotInstruction())) {
					btMore.setVisibility(View.VISIBLE);
					btLess.setVisibility(View.GONE);
					if (spot.getSpotInstruction().length() > Constants.SUMMARYNUMMAXLINE) {
						summary.setText(
								spot.getSpotInstruction().substring(0, Constants.SUMMARYNUMMAXLINE + 1) + "...");
					} else {
						summary.setText(spot.getSpotInstruction());
					}
				}

			}
		});

		mapTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(SpotActivity.this, OverlayDemo.class);
				intent.putExtra("lon", spot.getLon());
				intent.putExtra("lat", spot.getLat());
				intent.putExtra("name", spot.getSpotName());
				startActivity(intent);
			}
		});
		// 获取景点信息
		String jsonString = StringUtil.getInfo(SpotActivity.this, "SpotInfo", "{}");
		try {
			spot = GsonUtil.getObject(jsonString, Spot.class);
		} catch (Exception e) {
			spot = null;
		}
		if (spot != null) {
			if (StringUtil.isEmpty(spot.getQq())) {
				spot.setQq(
						"http://www.college360.cn/source/plugin/dz55625_haodian/upimg/20160308/20160308211337284.jpg&http://www.weibo.com/college360/home?wvr=5");
			}
			// 景点名
			if (spot.getSpotName().length() < 20) {
				textSpotName.setText(spot.getSpotName());
			} else {
				textSpotName.setText(spot.getSpotName().substring(0, 20) + "...");
			}
			if (!StringUtil.isEmpty(spot.getSpotInstruction())) {
				btMore.setVisibility(View.VISIBLE);
				btLess.setVisibility(View.GONE);
				if (spot.getSpotInstruction().length() > Constants.SUMMARYNUMMAXLINE) {
					summary.setText(spot.getSpotInstruction().substring(0, Constants.SUMMARYNUMMAXLINE + 1) + "...");
				} else {
					summary.setText(spot.getSpotInstruction());
				}
			}

			// 设置默认图片
			imageLogo.setImageResource(R.drawable.default_img);
			imageLoader.displayImage(spot.getQq().split("&")[0], imgAd, options);
			imageLoader.displayImage(spot.getSpotImage(), imageLogo, options);

			// 点击图片看全景
			imageLogo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					intent = new Intent(SpotActivity.this, PanoramaGLActivity.class);
					Log.d("*****全景图片地址****", spot.getSpotPanorama());
					
					if (!StringUtil.isEmpty(spot.getSpotPanorama())&&(spot.getSpotPanorama().contains(".png")||spot.getSpotPanorama().contains(".jpg"))) {
						saveData(spot);
						StringUtil.saveInfo(SpotActivity.this, "PanoramaGL", spot.getSpotPanorama());
						startActivity(intent);	
					}else{
						Toast toast = Toast.makeText(SpotActivity.this, "暂无全景图片！", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						
					}
					
				}
			});
		} else {
			finish();
		}

		commentList = new ArrayList<Comment>();

		listViewComment.setAdapter(commentAdapter);

		inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) view.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
		listViewComment.addFooterView(footerView);

		listViewComment.addHeaderView(unVuewHead);

		// 刷新
		listViewComment.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessage(Constants.LOAD_NEW_INFO);
			}
		});
		// 更多
		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				moreProgressBar.setVisibility(View.VISIBLE);
				// TODO
				myHandler.sendEmptyMessage(Constants.LOAD_MORE_SUCCESS);
			}
		});
		load();
	}

	// Adapter
	class CommentAdapter extends BaseAdapter {
		public class ViewHolder {
			public TextView textUserName, textUpdateTime, textZan, textContent;
			public CircleImageView imageUser;
		}

		private Context context;
		private Comment comment;
		private ArrayList<Comment> commentList;
		private ViewHolder viewHolder;

		public CommentAdapter(Context context, ArrayList<Comment> commentList) {
			this.context = context;
			this.commentList = commentList;
		}

		@Override
		public int getCount() {
			return commentList.size();
		}

		@Override
		public Object getItem(int position) {
			return commentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null || position < commentList.size()) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, null);
				viewHolder = new ViewHolder();
				// 加载组件
				viewHolder.imageUser = (CircleImageView) convertView.findViewById(R.id.imageUser);
				viewHolder.textContent = (TextView) convertView.findViewById(R.id.textContent);
				viewHolder.textUpdateTime = (TextView) convertView.findViewById(R.id.textUpdateTime);
				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.textZan = (TextView) convertView.findViewById(R.id.textZan);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			comment = commentList.get(position);
			if (comment.getUserName() == null || comment.getUserName().equals("null")) {
				viewHolder.textUserName.setText("匿名用户");
			} else {
				viewHolder.textUserName.setText(comment.getUserName());
			}
			if (!StringUtil.isEmpty(comment.getContent())) {
				viewHolder.textContent.setText(comment.getContent());
			}
			
			long timeMils=(new Date()).getTime()-comment.getDateLine()*1000;
			int ss = 1000;  
            int mi = ss * 60;  
            int hh = mi * 60;  
            int dd = hh * 24;  
 
            long day = timeMils / dd;  
            long hour = (timeMils - day * dd) / hh;  
            long minute = (timeMils - day * dd - hour * hh) / mi;  
			String text="";
			if(minute>0) {
				text=minute+"分钟前";
			}else {
				text="刚刚";
			}
			if(hour>0) {
				text=hour+"小时前";
			}
			if(day>0) {
				text=day+"天前";
			}
            if(day>365) {
            	text="一年前";
            }
			viewHolder.textUpdateTime.setText(text);
			
			viewHolder.imageUser.setTag(comment.getUserAvatar());

			// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
			viewHolder.imageUser.setImageResource(R.drawable.default_img);
			imageLoader.displayImage(comment.getUserAvatar(), viewHolder.imageUser, options);
			return convertView;
		}

	}

	private void saveData(Spot spot) {
		String imageUrl=spot.getSpotPanorama();
		String jumpStr=spot.getXgqj();
		List<JSONObject> jsonList=new ArrayList<JSONObject>();
		if(!StringUtil.isEmpty(jumpStr)) {
			String[] strArray=jumpStr.split(";");
			String strJson;
			String[] strJsonArray;
			JSONObject json;
			for(int i=0;i<strArray.length;i++) {
				strJson=strArray[i];
				strJsonArray=strJson.split(",");
				json=new JSONObject();
				try {
					json.put("id", strJsonArray[0]);
					json.put("x", strJsonArray[1]);
					json.put("y", strJsonArray[2]);
					json.put("hotspotUrl", strJsonArray[3]);
					json.put("text", strJsonArray[4]);
					
					HotSpotTask hotSpotTask = new HotSpotTask(this,json.getInt("id"));
					hotSpotTask.execute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					json=null;
				}
				if(json!=null) {
					jsonList.add(json);
				}
			}
		}
		String hotspots="";
		for(int i=0;i<jsonList.size();i++) {
			String hotspot="";
			try {
				 TextView tv = new TextView(this);  
				 tv.setText(jsonList.get(i).getString("text"));
				 tv.setBackgroundColor(Color.WHITE);
				 tv.setTextColor(Color.BLACK);
				 tv.setDrawingCacheEnabled(true);  
				 tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
				 tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());  
				 Bitmap bitmap = Bitmap.createBitmap(tv.getDrawingCache());  

				 File PHOTO_DIR = new File(Environment.getExternalStorageDirectory()+"/stone");//设置保存路径

				 File avaterFile = new File(PHOTO_DIR, "avater"+jsonList.get(i).getString("id")+".jpg");//设置文件名称

                 if(avaterFile.exists()){
                     avaterFile.delete();
                 }
                 try {
                     avaterFile.createNewFile();
                     FileOutputStream fos = new FileOutputStream(avaterFile);
                     bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                     fos.flush();
                     fos.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 tv.destroyDrawingCache();
				 
			hotspot="{\n" + 
					"			\"id\": "+jsonList.get(i).getString("id")+",\n" + 
					"			\"atv\": "+jsonList.get(i).getString("x")+",\n" + 
					"			\"ath\": "+jsonList.get(i).getString("y")+",\n" + 
					"			\"width\": 0.05,\n" + 
					"			\"height\": 0.05,\n" + 
					"			\"image\":\""+jsonList.get(i).getString("hotspotUrl")+"\",\n" +
					"			\"textImage\":\"file:///sdcard/stone/avater"+jsonList.get(i).getString("id")+".jpg\",\n" +
					"			\"alpha\": 0.8,\n" + 
					"			\"overAlpha\": 1.0,\n" + 
					"			\"onClick\": \"lookAt("+jsonList.get(i).getString("y")+", "+jsonList.get(i).getString("x")+", true); load('file:///sdcard/stone/test"+jsonList.get(i).getString("id")+".data', true, BLEND(2.0, 1.0))\"\n" + 
					"		}";
			}catch(Exception e) {
				
			}
			String dot=",";
			if(i==jsonList.size()-1) {
				dot="";
			}
			if(!StringUtil.isEmpty(hotspot)) {
				hotspots=hotspots+hotspot+dot;	
			}
			
		}
		writeToSD(imageUrl,hotspots,"test.data");

	}

	private void load() {
		if (getPTask != null && getPTask.getStatus() == AsyncTask.Status.RUNNING) {
			getPTask.cancel(true); // 如果Task还在运行，则先取消它
		}

		getPTask = new GetPTask(this);
		getPTask.execute();
	}
	private void writeToSD(String imageUrl,String hotspots,String fileName) {
		String result = "";
		try {
			InputStream in = getResources().openRawResource(
					R.raw.json_spherical);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = result.replaceAll("IMAGEPATH", imageUrl);
		result=result.replace("HOTSPOTS", hotspots);

		Log.d("JSONPL", result);

		FileUtil.setStringToFile("/stone", SpotActivity.this, fileName,
				result);
	}
	private void submitComm(String suContent) {
		if (!StringUtil.isEmpty(suContent)) {
			if (commTask != null && commTask.getStatus() == AsyncTask.Status.RUNNING) {
				commTask.cancel(true); // 如果Task还在运行，则先取消它
			}

			commTask = new CommTask(this, suContent);
			commTask.execute();
		}

	}
	public class HotSpotTask extends AsyncTask<Integer, String, Integer> {
		private Context mainFrame = null;
		private int spotId;

		public HotSpotTask(Context mainFrame,int spotId) {
			this.mainFrame = mainFrame;
			this.spotId=spotId;
		}

		@Override
		protected void onCancelled() {
			hideCustomProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			map = new HashMap<String, String>();
			// map.put("provinceid", cityId);
			gson = new Gson();
			String result = HttpUtil.doPostForm(map, Constants.GETSPOTBYID+ "&spotid=" + this.spotId, false, SpotActivity.this);
			if (StringUtil.isEmpty(result)) {
				msgs = "连接服务器超时，请确认网络畅通";
				return Constants.FAILURE;
			}
			// result.s
			Log.d("********patientList*******", result);
			JSONObject myJsonObject = null;
			try {
				myJsonObject = new JSONObject(result);
				String r = myJsonObject.getString("r");
				if (r.equals("no")) {
					msgs = myJsonObject.getString("msg");
					return Constants.FAILURE;
				}
			} catch (Exception e) {

			}
			List<Spot> spots=new ArrayList<Spot>();
			try {
				spots = gson.fromJson(result,
						new TypeToken<List<Spot>>() {
						}.getType());
				writeToSD(spots.get(0).getSpotPanorama(),"","test"+spots.get(0).getSpotId()+".data");
				
				return Constants.SUCCESS;
			} catch (Exception e) {
				msgs = e.getMessage();
			}
			return Constants.FAILURE;
		}

		@Override
		protected void onPreExecute() {
			showCustomProgrssDialog("正在加载...");
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO
			if (result == Constants.SUCCESS) {
				
				
			} else {
				if (!StringUtil.isEmpty(msgs)) {
					AlertDialogUtil.showAlertDialog(SpotActivity.this, msgs);
				}
			}
			hideCustomProgressDialog();
		}

	}
	
	public class GetPTask extends AsyncTask<Integer, String, Integer> {
		private Context mainFrame = null;

		public GetPTask(Context mainFrame) {
			this.mainFrame = mainFrame;
		}

		@Override
		protected void onCancelled() {
			hideCustomProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			map = new HashMap<String, String>();
			// map.put("provinceid", cityId);
			gson = new Gson();
			String result = HttpUtil.doPostForm(map, Constants.GETCOMLISTBYSP + "&spotid=" + spot.getSpotId(), false,
					SpotActivity.this);
			if (StringUtil.isEmpty(result)) {
				msgs = "连接服务器超时，请确认网络畅通";
				return Constants.FAILURE;
			}
			// result.s
			Log.d("********patientList*******", result);
			JSONObject myJsonObject = null;
			try {
				myJsonObject = new JSONObject(result);
				String r = myJsonObject.getString("r");
				if (r.equals("no")) {
					msgs = myJsonObject.getString("msg");
					return Constants.FAILURE;
				}
			} catch (Exception e) {

			}

			try {
				commentList = gson.fromJson(result, new TypeToken<List<Comment>>() {
				}.getType());
				Log.d("********patientList*******", commentList.size() + "");
				return Constants.SUCCESS;
			} catch (Exception e) {
				msgs = e.getMessage();
			}
			return Constants.FAILURE;
		}

		@Override
		protected void onPreExecute() {
			showCustomProgrssDialog("正在加载...");
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO
			if (result == Constants.SUCCESS) {
				commentAdapter = new CommentAdapter(SpotActivity.this, commentList);
				listViewComment.setAdapter(commentAdapter);
			} else {
				if (!StringUtil.isEmpty(msgs)) {
					AlertDialogUtil.showAlertDialog(SpotActivity.this, msgs);
				}
			}
			hideCustomProgressDialog();
		}

	}

	// 评论
	public class CommTask extends AsyncTask<Integer, String, Integer> {
		private Context mainFrame = null;
		private String sucontent;

		public CommTask(Context mainFrame, String sucontent) {
			this.mainFrame = mainFrame;
			this.sucontent = sucontent;
		}

		@Override
		protected void onCancelled() {
			hideCustomProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			map = new HashMap<String, String>();
			// map.put("provinceid", cityId);
			gson = new Gson();
			String str = "";
			try {
				str = URLEncoder.encode(sucontent, "GBK");
			} catch (Exception localException) {
				Log.d("toURLEncoded error:", str);
			}
			String result = HttpUtil.doPostForm(map,
					Constants.ADDCOMMENT + "&userid=220&spotid=" + spot.getSpotId() + "&comment=" + str, false,
					SpotActivity.this);
			if (StringUtil.isEmpty(result)) {
				msgs = "连接服务器超时，请确认网络畅通";
				return Constants.FAILURE;
			}
			// result.s
			Log.d("********patientList*******", result);
			JSONObject myJsonObject = null;
			try {
				myJsonObject = new JSONObject(result);
				String r = myJsonObject.getString("r");
				if (r.equals("no")) {
					msgs = myJsonObject.getString("msg");
					return Constants.FAILURE;
				}
				if (r.equals("1")) {
					msgs = myJsonObject.getString("msg");
					return Constants.SUCCESS;
				}
			} catch (Exception e) {

			}

			return Constants.FAILURE;
		}

		@Override
		protected void onPreExecute() {
			showCustomProgrssDialog("正在加载...");
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == Constants.SUCCESS) {
				editComm.setText("");
				commentAdapter = new CommentAdapter(SpotActivity.this, commentList);
				listViewComment.setAdapter(commentAdapter);
				load();
			} else {
				if (!StringUtil.isEmpty(msgs)) {
					AlertDialogUtil.showAlertDialog(SpotActivity.this, msgs);
				}
			}
			hideCustomProgressDialog();
		}

	}

	void showCustomProgrssDialog(String msg) {

		if (null == m_customProgrssDialog)

			m_customProgrssDialog = MyProgressDialog

					.createProgrssDialog(SpotActivity.this);

		if (null != m_customProgrssDialog) {

			m_customProgrssDialog.setMessage(msg);

			m_customProgrssDialog.show();

			m_customProgrssDialog.setCancelable(false);

		}

	}

	void hideCustomProgressDialog() {

		if (null != m_customProgrssDialog) {

			m_customProgrssDialog.dismiss();

			m_customProgrssDialog = null;

		}

	}
}
