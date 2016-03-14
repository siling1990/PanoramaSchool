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
import com.stone.panoramaschool.util.OnImageDownload;
import com.stone.panoramaschool.CityListActivity.GetPTask;
import com.stone.panoramaschool.adapter.ProvinceAdapter;
import com.stone.panoramaschool.entity.Provience;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
 *         2014年10月27日/上午12:45:14
 */
public class UniversityListActivity extends Activity {

	private static final int LOAD_MORE_SUCCESS = 1;
	private static final int LOAD_NEW_INFO = 2;
	private PullListView listViewUn;
	private ArrayList<University> unList;
	private ArrayList<University> unSearchList;
	private UniversityAdapterA unAdapter;
	private Button btBack, btRefresh;
	private ProgressBar moreProgressBar;
	private EditText editSearch;

	private Map<String, String> map;
	private Gson gson;
	private String msgs;
	private MyProgressDialog m_customProgrssDialog;
	private GetPTask getPTask;
	private int cityId;

	private ImageLoader imageLoader = ImageLoader.getInstance();//
	private DisplayImageOptions options; // 显示图像设置
	// 结果处理
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_MORE_SUCCESS:
				moreProgressBar.setVisibility(View.GONE);
				unAdapter.notifyDataSetChanged();
				listViewUn.setSelectionfoot();
				break;

			case LOAD_NEW_INFO:
				unAdapter.notifyDataSetChanged();
				listViewUn.onRefreshComplete();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 

		// 载入布局
		setContentView(R.layout.activity_university_list);

		// cityId=StringUtil.getInfo(UniversityListActivity.this, "cityId",
		// "85");
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
		Intent intent = getIntent();
		cityId = intent.getIntExtra("provienceId", 85);
		listViewUn = (PullListView) findViewById(R.id.listViewUn);
		btBack = (Button) findViewById(R.id.btBack);
		btRefresh = (Button) findViewById(R.id.btRefresh);
		editSearch= (EditText) findViewById(R.id.editSearch);

		btBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UniversityListActivity.this.finish();

			}
		});
		
		editSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				unSearchList.clear();
				for(int i=0;i<unList.size();i++){
					if(unList.get(i).getUnName().contains(s)){
						unSearchList.add(unList.get(i));
					}
				}
				unAdapter = new UniversityAdapterA(UniversityListActivity.this,
						unSearchList);
				listViewUn.setAdapter(unAdapter);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		// 加载数据
		unList = new ArrayList<University>();
		unSearchList= new ArrayList<University>();

		// 添加listview底部获取更多按钮（可自定义）
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) view
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
		listViewUn.addFooterView(footerView);

		listViewUn.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

			}
		});

		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				moreProgressBar.setVisibility(View.VISIBLE);

				myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
			}
		});
		btRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				load();
			}
		});
		load();
	}

	class UniversityAdapterA extends BaseAdapter {
		public class ViewHolder {
			public TextView textUnName;
			public Button btNext;
			public LinearLayout layoutUnComment;
			public CircleImageView imageUn;
		}

		private Context context;
		private University university;
		private ArrayList<University> unList;
		private ViewHolder viewHolder;
		private Resources resources;

		public UniversityAdapterA(Context context, ArrayList<University> unList) {
			this.context = context;
			this.unList = unList;
		}

		@Override
		public int getCount() {
			return unList.size();
		}

		@Override
		public Object getItem(int position) {
			return unList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null || position < unList.size()) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list_item_un, null);
				viewHolder = new ViewHolder();
				viewHolder.layoutUnComment = (LinearLayout) convertView
						.findViewById(R.id.layoutUnComment);
				viewHolder.textUnName = (TextView) convertView
						.findViewById(R.id.textUnName);
				viewHolder.btNext = (Button) convertView
						.findViewById(R.id.btNext);
				viewHolder.imageUn = (CircleImageView) convertView
						.findViewById(R.id.imageUn);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			university = unList.get(position);
			if(!StringUtil.isEmpty(university.getUnPic())){
				if(!university.getUnPic().startsWith("http")){
					university.setUnPic("http://www.college360.cn/"+university.getUnPic());
				}
			}else{
				university.setUnPic("");
			}
			resources = context.getResources();
			viewHolder.imageUn.setTag(university.getUnPic());
			
			viewHolder.layoutUnComment.removeAllViews();
			int score =university.getUnScore()/2;
			if (score >( university.getUnScore()%5)) {
				for (int i = 0; i < 5; i++) {
					ImageView image = new ImageView(context);
					image.setBackground(resources
							.getDrawable(R.drawable.heart_full));

					viewHolder.layoutUnComment.addView(image);

				}
			} else {
				for (int i = 0; i < score; i++) {
					ImageView image = new ImageView(context);
					image.setBackground(resources
							.getDrawable(R.drawable.heart_full));
					viewHolder.layoutUnComment.addView(image);
				}
				for (int i = 0; i < (5 - score); i++) {
					ImageView image = new ImageView(context);
					image.setBackground(resources
							.getDrawable(R.drawable.heart_line));
					viewHolder.layoutUnComment.addView(image);

				}
			}

			viewHolder.textUnName.setText(university.getUnName());
			final int pos = position;
			viewHolder.btNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, UnInfoActivity.class);
					StringUtil.saveInfo(context, "unInfo",
							GsonUtil.getJsonValue(unList.get(pos)));
					context.startActivity(intent);
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, UnInfoActivity.class);
					StringUtil.saveInfo(context, "unInfo",
							GsonUtil.getJsonValue(unList.get(pos)));
					context.startActivity(intent);
				}
			});

			

			viewHolder.imageUn.setImageResource(R.drawable.default_img);
			imageLoader.displayImage(university.getUnPic(),viewHolder.imageUn, options);

			return convertView;
		}

	}

	private void load() {
		if (getPTask != null
				&& getPTask.getStatus() == AsyncTask.Status.RUNNING) {
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
			String result = HttpUtil.doPostForm(map, Constants.GETUNLISTBYP
					+ "&provinceid=" + cityId, false,
					UniversityListActivity.this);
			if (StringUtil.isEmpty(result)) {
				msgs = "连接服务器超时，请确认网络畅通";
				return Constants.FAILURE;
			}
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
				unList = gson.fromJson(result,
						new TypeToken<List<University>>() {
						}.getType());
				Log.d("********patientList*******", unList.size() + "");
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
				unAdapter = new UniversityAdapterA(UniversityListActivity.this,
						unList);
				listViewUn.setAdapter(unAdapter);
			} else {
				if (!StringUtil.isEmpty(msgs)) {
					AlertDialogUtil.showAlertDialog(
							UniversityListActivity.this, msgs);
				}
			}
			hideCustomProgressDialog();
		}

	}

	void showCustomProgrssDialog(String msg) {

		if (null == m_customProgrssDialog)

			m_customProgrssDialog = MyProgressDialog

			.createProgrssDialog(UniversityListActivity.this);

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
