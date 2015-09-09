package felipe.cl.spm.actividades.extras;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import felipe.cl.spm.R;

public class CustomStateDialog extends Dialog {

    private ImageView iv;
    private TextView tv;

    public CustomStateDialog(Context context, int resourceIdOfImage, String message, int STATE) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                dialog.dismiss();
                return false;
            }
        });
        setCancelable(true);
        setOnCancelListener(null);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        if(STATE == Constantes.STATE_OK)
            layout.setBackgroundResource(R.drawable.dialog_bg_ok);
        else if(STATE == Constantes.STATE_NOK)
            layout.setBackgroundResource(R.drawable.dialog_bg_nok);
        else
            layout.setBackgroundResource(R.drawable.edittext_principal);


        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setPadding(100,100,100,100);
        LinearLayout layout1 = new LinearLayout(context);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        layout1.setOrientation(LinearLayout.VERTICAL);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams.setMargins(80,0,80,0);
        //params.setMargins(0,100,0,0);
        //textparams.setMargins(0, 0, 0, 100);

        ImageView iv = new ImageView(context);
        iv.setImageResource(resourceIdOfImage);
        iv.setLayoutParams(params);
        layout.addView(iv);

        TextView tv= new TextView(context);
        tv.setText(message);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(20);
        tv.setLayoutParams(textparams);
        layout.addView(tv);

        layout1.addView(layout, layoutparams);

        setContentView(layout1);
    }

    @Override
    public void show() {
        super.show();

    }
}
