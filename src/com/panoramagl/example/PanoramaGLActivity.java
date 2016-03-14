package com.panoramagl.example;

import javax.microedition.khronos.opengles.GL10;

import com.panoramagl.PLImage;
import com.panoramagl.PLSpherical2Panorama;
import com.panoramagl.PLView;
import com.panoramagl.utils.PLUtils;
import com.stone.panoramaschool.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ZoomControls;

public class PanoramaGLActivity extends PLView
{
	/**init methods*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//Load panorama
//		PLSpherical2Panorama panorama = new PLSpherical2Panorama();
//		//panorama.getCamera().lookAt(30.0f, 90.0f);
//		panorama.getCamera().lookAt(0.0f, 0.0f);
//		panorama.getCamera().setPitchRange(0.0f, 0.0f);
//		panorama.getCamera().setYawRange(-180, 180);
//        panorama.setImage(new PLImage(PLUtils.getBitmap(this, R.raw.buzhidao), false));
//        this.setPanorama(panorama);
	}
	
	/**
     * This event is fired when root content view is created
     * @param contentView current root content view
     * @return root content view that Activity will use
     */
//	@Override
//	protected View onContentViewCreated(View contentView)
//	{
//		//Load layout
//		ViewGroup mainView = (ViewGroup)this.getLayoutInflater().inflate(R.layout.activity_panorana_gl, null);
//		//Add 360 view
//    	mainView.addView(contentView, 0);
//    	//Return root content view
//		return super.onContentViewCreated(mainView);
//	}
	@Override
	protected void onGLContextCreated(GL10 gl)
	{
		super.onGLContextCreated(gl);
		
		//Add layout
    	View mainView = this.getLayoutInflater().inflate(R.layout.activity_panorana_gl, null);
        this.addContentView(mainView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        
        //Zoom controls
        ZoomControls zoomControls = (ZoomControls)mainView.findViewById(R.id.zoomControls);
        zoomControls.setOnZoomInClickListener(new OnClickListener()
        {	
			@Override
			public void onClick(View view)
			{
				getCamera().zoomIn(true);
			}
		});
        zoomControls.setOnZoomOutClickListener(new OnClickListener()
        {	
			@Override
			public void onClick(View view)
			{
				getCamera().zoomOut(true);
			}
		});
	}
}