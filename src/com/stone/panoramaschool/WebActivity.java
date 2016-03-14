package com.stone.panoramaschool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.stone.panoramaschool.window.MyProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebActivity extends Activity {

	private static final String TAG = WebActivity.class.getSimpleName();
	private static final String APP_CACAHE_DIRNAME = "/webcache";
	private WebView mWebView;
	private MyProgressDialog m_customProgrssDialog;
	private String url;
	private Intent intent;
	private ValueCallback<Uri> mUploadFile;
	private boolean isOpenwindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		
		mWebView = (WebView) findViewById(R.id.webView);

		intent = getIntent();
		url = intent.getStringExtra("url");
		isOpenwindow = intent.getBooleanExtra("isOpenwindow", true);
		initWebView();
	}

	public void back(View view) {
		this.finish();
	}
	public void refresh(View view) {
		initWebView();
	}

	private void initWebView() {

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.getSettings().setSaveFormData(false);
		mWebView.getSettings().setAllowFileAccess(true);
		// mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 缓存模式
		// 开启 DOM storage API 功能
		mWebView.getSettings().setDomStorageEnabled(false);
		// 开启 database storage API 功能
		mWebView.getSettings().setDatabaseEnabled(false);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		String cacheDirPath = this.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
		// String cacheDirPath =
		// getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
		Log.i(TAG, "cacheDirPath=" + cacheDirPath);
		// // 设置数据库缓存路径
		// mWebView.getSettings().setDatabasePath(cacheDirPath);
		// 开启 Application Caches 功能
		mWebView.getSettings().setAppCacheEnabled(false);
		// 设置 Application Caches 缓存目录
		mWebView.getSettings().setAppCachePath(cacheDirPath);

		clearWebViewCache();

		registerForContextMenu(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onLoadResource(WebView view, String url) {
				Log.i(TAG, "onLoadResource url=" + url);
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.e(TAG, "onPageStarted");
				showCustomProgrssDialog("正在加载..."); // 显示加载界面
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				String title = view.getTitle();
				Log.e(TAG, "onPageFinished WebView title=" + title);

				hideCustomProgressDialog();// 隐藏加载界面
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				hideCustomProgressDialog(); // 隐藏加载界面
				Toast.makeText(WebActivity.this, "", Toast.LENGTH_LONG).show();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//
				if (isOpenwindow) {
					intent = new Intent(WebActivity.this, WebActivity.class);
					intent.putExtra("url", url);
					startActivity(intent);
					return true;
				} else {
					return super.shouldOverrideUrlLoading(view, url);
				}

			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				Log.e(TAG, "onJsAlert " + message);
				Toast.makeText(WebActivity.this, message, Toast.LENGTH_SHORT).show();
				result.confirm();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
				Log.e(TAG, "onJsConfirm " + message);

				new AlertDialog.Builder(WebActivity.this).setMessage(message).setTitle("提示")
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						result.confirm();

					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						result.cancel();

					}
				}).show();

				return true;
				// return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public void onCloseWindow(WebView window) {
				super.onCloseWindow(window);
				WebActivity.this.finish();
			}
			@Override
			public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
				// TODO Auto-generated method stub
				return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
			}
			
			@Override
			public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
					JsPromptResult result) {
				Log.e(TAG, "onJsPrompt " + url);
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}

			// Andorid 4.1+
			public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
				openFileChooser(uploadFile);
			}

			// Andorid 3.0 +
			public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
				openFileChooser(uploadFile);
			}

			// Android 3.0
			public void openFileChooser(ValueCallback<Uri> uploadFile) {
				// Toast.makeText(WebviewActivity.this,
				// "上传文件/图片",Toast.LENGTH_SHORT).show();
//				mUploadFile = uploadFile;
//				startActivityForResult(Intent.createChooser(createCameraIntent(), "Image Browser"),
//						Constants.REQUEST_UPLOAD_FILE_CODE);
			}
			
		});
		mWebView.loadUrl(url);
	}


	/**
	 * 清除WebView缓存
	 */
	public void clearWebViewCache() {

		// 清理Webview缓存数据库
		try {
			this.deleteDatabase("webview.db");
			this.deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// WebView 缓存文件
		File appCacheDir = new File(this.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
		Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

		File webviewCacheDir = new File(this.getCacheDir().getAbsolutePath() + "/webviewCache");
		Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

		// 删除webview 缓存目录
		if (webviewCacheDir.exists()) {
			deleteFile(webviewCacheDir);
		}
		// 删除appCacheDir 缓存 缓存目录
		if (appCacheDir.exists()) {
			deleteFile(appCacheDir);
		}
	}

	/**
	 * 递归删除 文件/文件夹
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {

		Log.i(TAG, "delete file path=" + file.getAbsolutePath());

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
			Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
		}
	}

	void showCustomProgrssDialog(String msg) {
		if (null == m_customProgrssDialog)
			m_customProgrssDialog = MyProgressDialog.createProgrssDialog(this);
		if (null != m_customProgrssDialog) {
			m_customProgrssDialog.setMessage(msg);
			m_customProgrssDialog.show();
			m_customProgrssDialog.setCancelable(true);
		}
	}

	void hideCustomProgressDialog() {
		if (null != m_customProgrssDialog) {
			m_customProgrssDialog.dismiss();
			m_customProgrssDialog = null;
		}
	}

	@Override
	protected void onResume() {
		initWebView();
		super.onResume();
	}
}
