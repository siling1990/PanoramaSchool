package com.stone.panoramaschool.window;


import com.stone.panoramaschool.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProgressDialog extends Dialog {

	private static MyProgressDialog m_progrssDialog;

	private MyProgressDialog(Context context, int theme) {

		super(context, theme);

	}

	public static MyProgressDialog createProgrssDialog(Context context) {

		m_progrssDialog = new MyProgressDialog(context,
				R.style.SF_pressDialogCustom);
		m_progrssDialog.setContentView(R.layout.custome_progress_dialog);
		m_progrssDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		m_progrssDialog.setCancelable(true);
		return m_progrssDialog;
	}

	public static MyProgressDialog createProgrssDialog(Context context,
			String msg) {

		m_progrssDialog = new MyProgressDialog(context,
				R.style.SF_pressDialogCustom);
		m_progrssDialog.setContentView(R.layout.custome_progress_dialog);
		m_progrssDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		m_progrssDialog.setMessage(msg);
		return m_progrssDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (null == m_progrssDialog)

			return;

		ImageView loadingImageView = (ImageView) m_progrssDialog

		.findViewById(R.id.sf_iv_progress_dialog_loading);

		AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView

		.getBackground();

		animationDrawable.start();

	}

	public MyProgressDialog setMessage(String msg) {

		TextView loadingTextView = (TextView) m_progrssDialog

		.findViewById(R.id.sf_tv_progress_dialog_loading);

		if (!TextUtils.isEmpty(msg))

			loadingTextView.setText(msg);

		else

			loadingTextView.setText(R.string.progress_dialog_image_loading);

		return m_progrssDialog;

	}

}