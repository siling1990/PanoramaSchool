package com.stone.panoramaschool;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.panoramagl.PLView;
import com.panoramagl.loaders.PLILoader;
import com.panoramagl.loaders.PLJSONLoader;
import com.panoramagl.transitions.PLTransitionBlend;
import com.stone.panoramaschool.R;
import com.stone.panoramaschool.entity.Spot;
import com.stone.panoramaschool.util.GsonUtil;
import com.stone.panoramaschool.util.StringUtil;

import android.util.Log;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class PanoramaGLActivity extends PLView {
	/** init methods */

	private ImageLoader imageLoader = ImageLoader.getInstance();//
	private DisplayImageOptions options; // 显示图像设置
	private TextView textSpotName;
	private Spot spot;
	private Button btBack, btRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.activity_spot);

		String url = StringUtil.getInfo(PanoramaGLActivity.this, "PanoramaGL",
				"http://pic20.nipic.com/20120505/4320883_105925710000_2.jpg");
		Log.d("PLURL", url);
		// if (mDownloader == null) {
		// mDownloader = new ImageDownloader();
		// }
		// if(url != null){
		// imageName = Util.getInstance().getImageName(url);
		// }
		// bitMap=mDownloader.getBitmapFromFileA(PanoramaGLActivity.this,
		// imageName, Config.IMGSTORE);
		// //Load panorama
		// PLSpherical2Panorama panorama = new PLSpherical2Panorama();
		// //panorama.getCamera().lookAt(30.0f, 90.0f);
		// panorama.getCamera().lookAt(0.0f, 0.0f);
		// panorama.getCamera().setPitchRange(0.0f, 0.0f);
		// panorama.getCamera().setYawRange(-180, 180);
		// if(bitMap==null){
		// panorama.setImage(new PLImage(PLUtils.getBitmap(this,
		// R.raw.buzhidao), false));
		// }else{
		// panorama.setImage(new PLImage(bitMap));
		// }
		//
		// this.setPanorama(panorama);
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
		loadPanoramaFromJSON();
	}

	/**
	 * This event is fired when root content view is created
	 * 
	 * @param contentView
	 *            current root content view
	 * @return root content view that Activity will use
	 */
	@Override
	protected View onContentViewCreated(View contentView) {
		String jsonString = StringUtil.getInfo(PanoramaGLActivity.this,
				"SpotInfo", "{}");
		try {
			spot = GsonUtil.getObject(jsonString, Spot.class);
		} catch (Exception e) {
			spot = null;
		}

		// Load layout
		ViewGroup mainView = (ViewGroup) this.getLayoutInflater().inflate(
				R.layout.activity_panorana_gl, null);
		RelativeLayout relativeLayoutM = (RelativeLayout) mainView
				.findViewById(R.id.relativeLayoutM);
		// Add 360 view
		relativeLayoutM.addView(contentView, 0);
		//

		textSpotName = (TextView) mainView.findViewById(R.id.textSpotName);
		btBack = (Button) mainView.findViewById(R.id.btBack);
		btRefresh = (Button) mainView.findViewById(R.id.btRefresh);

		btBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PanoramaGLActivity.this.finish();

			}
		});
		btRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loadPanoramaFromJSON();
			}
		});
		if (spot != null) {
			textSpotName.setText(spot.getSpotName());
		}
		// Zoom controls
		ZoomControls zoomControls = (ZoomControls) mainView
				.findViewById(R.id.zoomControls);
		zoomControls.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCamera().zoomIn(true);
			}
		});
		zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getCamera().zoomOut(true);
			}
		});
		// Return root content view
		return super.onContentViewCreated(mainView);
	}

	private void loadPanoramaFromJSON() {
		try {
			PLILoader loader = null;
			//loader = new PLJSONLoader("res://raw/json_spherical");file:///
			loader = new PLJSONLoader("file:///sdcard/stone/"+"test.data");
			if (loader != null)
				this.load(loader, true, new PLTransitionBlend(2.0f));
		} catch (Throwable e) {
			Toast.makeText(this.getApplicationContext(), "Error: " + e,
					Toast.LENGTH_SHORT).show();
		}
	}

}