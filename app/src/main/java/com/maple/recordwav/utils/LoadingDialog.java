package com.maple.recordwav.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;


import com.maple.recordwav.R;


/**
 * loading dialog
 *
 * @author maple
 * @time 16/4/18 上午10:35
 */
public class LoadingDialog extends Dialog {

    private final TextView tvMsg;

    public LoadingDialog(Context context) {
        this(context, false);
    }

    public LoadingDialog(Context context, boolean isShowAsFloatWindow) {
        super(context, R.style.CustomDialog);

        if (!isShowAsFloatWindow) {
//            Assert.assertTrue("context must be Activity in Dialog.", context instanceof Activity);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }

        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        this.setCancelable(true);
        this.setContentView(R.layout.dialog_common_progress);
        tvMsg = (TextView) this.findViewById(R.id.tvMsg);
        tvMsg.setTextColor(Color.WHITE);
        tvMsg.setText("loading");

    }

    public void show(String msg) {
        show(msg, true);
    }

    public void show(String msg, boolean cancelable) {
        this.setCancelable(cancelable);
        tvMsg.setText(msg);
        this.show();
    }
}
