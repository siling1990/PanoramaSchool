package com.stone.panoramaschool.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片异步下载完成后回调
 * @author yanbin
 *
 */
public interface OnImageDownload {
	void onDownloadSucc(Bitmap bitmap,String c_url,ImageView imageView);
}
