package com.stone.panoramaschool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.stone.panoramaschool.entity.Spot;
import com.stone.panoramaschool.entity.University;
import com.stone.panoramaschool.util.AlertDialogUtil;
import com.stone.panoramaschool.util.Constants;
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * com.stone.panoramaschool
 * 
 * @author stone
 * 
 *         2014年11月1日/下午9:14:17
 */
public class UnInfoActivity extends Activity {

	private TextView textUnName, txtIN;
	private Button btBack, btRefresh;
	private ImageView imageLogo;
	private PullListView listViewSpot;
	private ArrayList<Spot> spotList;
	private University university;
	private ProgressBar moreProgressBar;
	private SpotAdapterA spotAdapter;
	private RelativeLayout relativeLayout3;
	private Button btMore, btLess;

	private Map<String, String> map;
	private Gson gson;
	private String msgs;
	private MyProgressDialog m_customProgrssDialog;
	private GetPTask getPTask;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();//
	private DisplayImageOptions options; // 显示图像设置
	// 结果处理
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case com.stone.panoramaschool.util.Constants.LOAD_MORE_SUCCESS:
				moreProgressBar.setVisibility(View.GONE);
				spotAdapter.notifyDataSetChanged();
				listViewSpot.setSelectionfoot();
				break;

			case com.stone.panoramaschool.util.Constants.LOAD_NEW_INFO:
				spotAdapter.notifyDataSetChanged();
				listViewSpot.onRefreshComplete();
				break;
			default:
				break;
			}
		}

	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_un_info_g);

		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_img) // 设置图片下载期间显示的图�?
				.showImageForEmptyUri(R.drawable.default_img) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_img) // 设置图片加载或解码过程中发生错误显示的图�?
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存�?
				.cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
			//	.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图�?
				.build(); // 创建配置过得DisplayImageOption对象
		
		listViewSpot = (PullListView) findViewById(R.id.listViewSpot);
		textUnName = (TextView) findViewById(R.id.textUnName);
		// txtIN = (TextView) findViewById(R.id.txtIN);
		// imageLogo = (ImageView) findViewById(R.id.imageLogo);
		btBack = (Button) findViewById(R.id.btBack);
		btRefresh = (Button) findViewById(R.id.btRefresh);
		relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);

		btBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UnInfoActivity.this.finish();

			}
		});
		btRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				load();
			}
		});
		LayoutInflater inflater = LayoutInflater.from(this);
		View unVuewHead = inflater.inflate(R.layout.unifo_head, null);
		txtIN = (TextView) unVuewHead.findViewById(R.id.txtIN);
		imageLogo = (ImageView) unVuewHead.findViewById(R.id.imageLogo);
		btMore = (Button) unVuewHead.findViewById(R.id.btMore);
		btLess = (Button) unVuewHead.findViewById(R.id.btLess);

		btMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtil.isEmpty(university.getUnInstruction())) {
					btMore.setVisibility(View.GONE);
					btLess.setVisibility(View.VISIBLE);
					txtIN.setText(university.getUnInstruction());
				}
			}
		});
		btLess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtil.isEmpty(university.getUnInstruction())) {
					btMore.setVisibility(View.VISIBLE);
					btLess.setVisibility(View.GONE);
					if (university.getUnInstruction().length() > Constants.SUMMARYNUMMAXLINE) {
						txtIN.setText(university.getUnInstruction().substring(0, Constants.SUMMARYNUMMAXLINE + 1)+"...");
					} else {
						txtIN.setText(university.getUnInstruction());
					}
				}

			}
		});

		// 获取学校信息
		String jsonString = StringUtil.getInfo(UnInfoActivity.this, "unInfo", "{}");
		Log.d("********学校信息******", jsonString);
		try {
			university = GsonUtil.getObject(jsonString, University.class);
		} catch (Exception e) {
			university = null;
		}

		if (university != null) {
			if (!StringUtil.isEmpty(university.getUnPic())) {
				if (!university.getUnPic().startsWith("http")) {
					university.setUnPic("http://www.college360.cn/" + university.getUnPic());
				}
			} else {
				university.setUnPic("");
			}
			if (university.getUnName().length() < 24) {
				textUnName.setText(university.getUnName());
			} else {
				textUnName.setText(university.getUnName().substring(0, 24)+"...");
			}

			if (!StringUtil.isEmpty(university.getUnInstruction())) {
				btMore.setVisibility(View.VISIBLE);
				btLess.setVisibility(View.GONE);
				if (university.getUnInstruction().length() > Constants.SUMMARYNUMMAXLINE) {
					txtIN.setText(university.getUnInstruction().substring(0, Constants.SUMMARYNUMMAXLINE + 1)+"...");
				} else {
					txtIN.setText(university.getUnInstruction());
				}
			}

			// 设置默认图片
			imageLogo.setImageResource(R.drawable.default_img);
			imageLogo.setTag(university.getUnPic());
			// //异步下载学校图片
			imageLoader.displayImage(university.getUnPic(),imageLogo, options);
			
		}

		// 加载数据
		spotList = new ArrayList<Spot>();

		spotAdapter = new SpotAdapterA(UnInfoActivity.this, spotList);

		listViewSpot.setAdapter(spotAdapter);

		// 添加listview底部获取更多按钮（可自定义）
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) view.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);

		listViewSpot.addFooterView(footerView);
		listViewSpot.addHeaderView(unVuewHead);

		// 刷新
		listViewSpot.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessage(com.stone.panoramaschool.util.Constants.LOAD_NEW_INFO);
			}
		});
		// 更多
		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				moreProgressBar.setVisibility(View.VISIBLE);
				// TODO
				myHandler.sendEmptyMessage(com.stone.panoramaschool.util.Constants.LOAD_MORE_SUCCESS);
			}
		});
		load();
	}

	class SpotAdapterA extends BaseAdapter {
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

		public SpotAdapterA(Context context, ArrayList<Spot> spotList) {
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
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_spot, null);
				viewHolder = new ViewHolder();
				viewHolder.layoutSpotComment = (LinearLayout) convertView.findViewById(R.id.layoutSpotComment);
				viewHolder.textSpotName = (TextView) convertView.findViewById(R.id.textSpotName);
				viewHolder.btNext = (Button) convertView.findViewById(R.id.btNext);
				viewHolder.imageSpot = (CircleImageView) convertView.findViewById(R.id.imageSpot);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			spot = spotList.get(position);
			final int po = position;
			resources = context.getResources();
			viewHolder.imageSpot.setTag(spot.getSpotImage());

			viewHolder.layoutSpotComment.removeAllViewsInLayout();
			int spotScore = 1;
			try {
				spotScore = Integer.parseInt(spot.getScore());
			} catch (Exception e) {

			}

			viewHolder.layoutSpotComment.removeAllViews();
			int score = spotScore / 2;

			if (score > (score % 5)) {
				for (int i = 0; i < 5; i++) {
					ImageView image = new ImageView(context);
					image.setBackground(resources.getDrawable(R.drawable.heart_full));

					viewHolder.layoutSpotComment.addView(image);

				}
			} else {
				for (int i = 0; i < score; i++) {
					ImageView image = new ImageView(context);
					image.setBackground(resources.getDrawable(R.drawable.heart_full));
					viewHolder.layoutSpotComment.addView(image);
				}
				for (int i = 0; i < (5 - score); i++) {
					ImageView image = new ImageView(context);
					image.setBackground(resources.getDrawable(R.drawable.heart_line));
					viewHolder.layoutSpotComment.addView(image);

				}
			}

			viewHolder.textSpotName.setText(spot.getSpotName());
			viewHolder.btNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, SpotActivity.class);
					StringUtil.saveInfo(context, "SpotInfo", GsonUtil.getJsonValue(spotList.get(po)));
					context.startActivity(intent);
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, SpotActivity.class);
					StringUtil.saveInfo(context, "SpotInfo", GsonUtil.getJsonValue(spotList.get(po)));
					context.startActivity(intent);
				}
			});

			viewHolder.imageSpot.setImageResource(R.drawable.default_img);
			// 异步下载图片
			imageLoader.displayImage(spot.getSpotImage(),viewHolder.imageSpot, options);
			return convertView;
		}

	}

	private void load() {
		if (getPTask != null && getPTask.getStatus() == AsyncTask.Status.RUNNING) {
			getPTask.cancel(true); // 如果Task还在运行，则先取消它
		}

		getPTask = new GetPTask(this);
		getPTask.execute();
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
			String result = HttpUtil.doPostForm(map, Constants.GETSPLISTBYUN + "&unid=" + university.getUnId(), false,
					UnInfoActivity.this);
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
				spotList = gson.fromJson(result, new TypeToken<List<Spot>>() {
				}.getType());
				Log.d("********patientList*******", spotList.size() + "");
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
				spotAdapter = new SpotAdapterA(UnInfoActivity.this, spotList);
				listViewSpot.setAdapter(spotAdapter);
			} else {
				if (!StringUtil.isEmpty(msgs)) {
					AlertDialogUtil.showAlertDialog(UnInfoActivity.this, msgs);
				}
			}
			hideCustomProgressDialog();
		}

	}

	void showCustomProgrssDialog(String msg) {

		if (null == m_customProgrssDialog)

			m_customProgrssDialog = MyProgressDialog

					.createProgrssDialog(UnInfoActivity.this);

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
