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
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// ����ģʽ
		// ���� DOM storage API ����
		mWebView.getSettings().setDomStorageEnabled(false);
		// ���� database storage API ����
		mWebView.getSettings().setDatabaseEnabled(false);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		String cacheDirPath = this.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
		// String cacheDirPath =
		// getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
		Log.i(TAG, "cacheDirPath=" + cacheDirPath);
		// // �������ݿ⻺��·��
		// mWebView.getSettings().setDatabasePath(cacheDirPath);
		// ���� Application Caches ����
		mWebView.getSettings().setAppCacheEnabled(false);
		// ���� Application Caches ����Ŀ¼
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
				showCustomProgrssDialog("���ڼ���..."); // ��ʾ���ؽ���
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				String title = view.getTitle();
				Log.e(TAG, "onPageFinished WebView title=" + title);

				hideCustomProgressDialog();// ���ؼ��ؽ���
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				hideCustomProgressDialog(); // ���ؼ��ؽ���
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

				new AlertDialog.Builder(WebActivity.this).setMessage(message).setTitle("��ʾ")
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						result.confirm();

					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

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
				// "�ϴ��ļ�/ͼƬ",Toast.LENGTH_SHORT).show();
//				mUploadFile = uploadFile;
//				startActivityForResult(Intent.createChooser(createCameraIntent(), "Image Browser"),
//						Constants.REQUEST_UPLOAD_FILE_CODE);
			}
			
		});
		mWebView.loadUrl(url);
	}


	/**
	 * ���WebView����
	 */
	public void clearWebViewCache() {

		// ����Webview�������ݿ�
		try {
			this.deleteDatabase("webview.db");
			this.deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// WebView �����ļ�
		File appCacheDir = new File(this.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
		Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

		File webviewCacheDir = new File(this.getCacheDir().getAbsolutePath() + "/webviewCache");
		Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

		// ɾ��webview ����Ŀ¼
		if (webviewCacheDir.exists()) {
			deleteFile(webviewCacheDir);
		}
		// ɾ��appCacheDir ���� ����Ŀ¼
		if (appCacheDir.exists()) {
			deleteFile(appCacheDir);
		}
	}

	/**
	 * �ݹ�ɾ�� �ļ�/�ļ���
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
