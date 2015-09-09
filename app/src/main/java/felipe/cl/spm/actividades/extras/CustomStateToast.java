package felipe.cl.spm.actividades.extras;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import felipe.cl.spm.R;

public class CustomStateToast extends Toast{


    public CustomStateToast(Context context, int resourceIdOfImage, String message) {
        super(context);


        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundResource(R.drawable.edittext_principal);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,100,0,0);
        textparams.setMargins(0,0,0,100);

        ImageView iv = new ImageView(context);
        iv.setImageResource(resourceIdOfImage);
        iv.setLayoutParams(params);
        layout.addView(iv);

        TextView tv= new TextView(context);
        tv.setText(message);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(24);
        tv.setLayoutParams(textparams);
        layout.addView(tv);

        setGravity(Gravity.CENTER_VERTICAL,0,0);
        setView(layout);
        setDuration(Toast.LENGTH_LONG);
    }


}
