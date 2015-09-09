package felipe.cl.spm.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import felipe.cl.spm.R;

public class FormProductos extends Activity implements View.OnClickListener {
    ImageButton btn_back, btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_form_productos);

        btn_back = (ImageButton) findViewById(R.id.ib_back);
        btn_save = (ImageButton) findViewById(R.id.ib_save);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            /*TransparentProgressDialog d = new TransparentProgressDialog(this, R.drawable.progress_img, "BUSCANDO");
            d.show();*/
            finish();

            /*CustomStateToast d = new CustomStateToast(this, R.drawable.progress_img, "REGISTRO INGRESADO EXISTOSAMENTE");
            d.show();*/
        }
        if (v.getId() == R.id.ib_save) {
            startActivity(new Intent(this, FormContrato.class));
        }
    }
}
