package com.stone.panoramaschool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stone.panoramaschool.UniversityListActivity.UniversityAdapterA;
import com.stone.panoramaschool.adapter.ProvinceAdapter;
import com.stone.panoramaschool.entity.Provience;
import com.stone.panoramaschool.util.AlertDialogUtil;
import com.stone.panoramaschool.util.Constants;
import com.stone.panoramaschool.util.HttpUtil;
import com.stone.panoramaschool.util.StringUtil;
import com.stone.panoramaschool.window.MyProgressDialog;
import com.stone.panoramaschool.window.PullListView;
import com.stone.panoramaschool.window.PullListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * com.stone.panoramaschool
 * 
 * @author stone
 * 
 *         2014年10月26日/下午1:52:14
 */
@SuppressLint("HandlerLeak")
public class CityListActivity extends Activity {

	private static final int LOAD_MORE_SUCCESS = 1;
	private static final int LOAD_NEW_INFO = 2;
	private PullListView listCity;
	private ArrayList<Provience> cityList;
	private ArrayList<Provience> citySearchList;
	private ProvinceAdapter cityAdapter;
	private ProgressBar moreProgressBar;
	private Boolean isExit = false;
	private Button btRefresh;
	private ConnectivityManager manager;
	private Map<String, String> map;
	private Gson gson;
	private String msgs;
	private MyProgressDialog m_customProgrssDialog;
	private GetPTask getPTask;
	private EditText editSearch;
	// 结果处理
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_MORE_SUCCESS:
				moreProgressBar.setVisibility(View.GONE);
				// cityAdapter.notifyDataSetChanged();
				// listCity.setSelectionfoot();
				load();
				break;

			case LOAD_NEW_INFO:
				cityAdapter.notifyDataSetChanged();
				listCity.onRefreshComplete();
				break;
			case 4:
				isExit = false;
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
		setContentView(R.layout.activity_city_list);

		listCity = (PullListView) findViewById(R.id.listCity);
		btRefresh = (Button) findViewById(R.id.btRefresh);
		editSearch = (EditText) findViewById(R.id.editSearch);
		// 加载数据
		cityList = new ArrayList<Provience>();
		citySearchList = new ArrayList<Provience>();

		// 添加listview底部获取更多按钮（可自定义）
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) view
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
		listCity.addFooterView(footerView);

		if (!checkNetworkState()) {

		}

		listCity.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				myHandler
						.sendEmptyMessage(com.stone.panoramaschool.util.Constants.LOAD_NEW_INFO);
			}
		});

		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				moreProgressBar.setVisibility(View.VISIBLE);
				myHandler
						.sendEmptyMessage(com.stone.panoramaschool.util.Constants.LOAD_MORE_SUCCESS);

			}
		});
		//
		editSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				citySearchList.clear();
				for (int i = 0; i < cityList.size(); i++) {
					if (cityList.get(i).getProvinceName().contains(s)) {
						citySearchList.add(cityList.get(i));
					}
				}
				cityAdapter = new ProvinceAdapter(CityListActivity.this,
						citySearchList);
				listCity.setAdapter(cityAdapter);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

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

	private void load() {
		if (getPTask != null
				&& getPTask.getStatus() == AsyncTask.Status.RUNNING) {
			getPTask.cancel(true); // 如果Task还在运行，则先取消它
		}

		getPTask = new GetPTask(this);
		getPTask.execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 按下的如果是BACK，同时没有重复
			exit();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出方法
	 * */
	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出",
					Toast.LENGTH_SHORT).show();
			myHandler.sendEmptyMessageDelayed(4, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	private boolean checkNetworkState() {
		boolean flag = false;
		// 得到网络连接信息
		manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		if (!flag) {
			setNetwork();
		}
		/*
		 * else { isNetworkAvailable(); }
		 */

		return flag;
	}

	/**
	 * 网络未连接时，调用设置方法
	 */
	private void setNetwork() {
		// Toast.makeText(this, "wifi is closed!", Toast.LENGTH_SHORT).show();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("网络提示信息");
		builder.setMessage("网络不可用，如果继续，请先设置网络！");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				/**
				 * 判断手机系统的版本！如果API大于10 就是3.0+ 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
				 */
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CityListActivity.this.finish();
			}
		});
		builder.create();
		builder.setCancelable(false);
		builder.show();
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
			gson = new Gson();
			String result = HttpUtil.doPostForm(map, Constants.GETPLIST, false,
					CityListActivity.this);
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
				cityList = gson.fromJson(result,
						new TypeToken<List<Provience>>() {
						}.getType());
				Log.d("********patientList*******", cityList.size() + "");
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
				cityAdapter = new ProvinceAdapter(CityListActivity.this,
						cityList);
				listCity.setAdapter(cityAdapter);
			} else {
				if (!StringUtil.isEmpty(msgs)) {
					AlertDialogUtil
							.showAlertDialog(CityListActivity.this, msgs);
				}
			}
			hideCustomProgressDialog();
//			final AlertDialog  myDialog = new AlertDialog.Builder(CityListActivity.this).create(); 
//            myDialog.show();  
//            myDialog.getWindow().setContentView(R.layout.alert_dialog);
//            myDialog.getWindow()  
//            .findViewById(R.id.button_back_mydialog)  
//            .setOnClickListener(new View.OnClickListener() {  
//            @Override  
//            public void onClick(View v) {  
//                myDialog.dismiss();  
//            }  
//        });  
		}

	}

	void showCustomProgrssDialog(String msg) {

		if (null == m_customProgrssDialog)

			m_customProgrssDialog = MyProgressDialog

			.createProgrssDialog(CityListActivity.this);

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
