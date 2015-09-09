package felipe.cl.spm.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.TransparentProgressDialog;

public class Servicios extends Activity implements View.OnClickListener {
    TransparentProgressDialog d;
    int isNew = 0;
    ImageButton btn_back;
    Button btn_tv,btn_internet, btn_telefono, btn_tv_internet, btn_tv_internet_telefono, btn_internet_telefono, btn_telefono_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_servicios);
        isNew = getIntent().getExtras().getInt("CASO", 0);

        btn_back = (ImageButton) findViewById(R.id.ib_back);
        btn_tv = (Button) findViewById(R.id.b_tv);
        btn_tv_internet = (Button) findViewById(R.id.b_tv_internet);
        btn_tv_internet_telefono = (Button) findViewById(R.id.b_all);
        btn_internet = (Button) findViewById(R.id.b_internet);
        btn_internet_telefono = (Button) findViewById(R.id.b_internet_telefono);
        btn_telefono = (Button) findViewById(R.id.b_telefono);
        btn_telefono_tv = (Button) findViewById(R.id.b_telefono_tv);        
        btn_back.setOnClickListener(this);
        btn_tv.setOnClickListener(this);
        btn_internet.setOnClickListener(this);
        btn_telefono.setOnClickListener(this);
        btn_tv_internet_telefono.setOnClickListener(this);
        btn_tv_internet.setOnClickListener(this);
        btn_internet_telefono.setOnClickListener(this);
        btn_telefono_tv.setOnClickListener(this);

        if(isNew == 1){
            btn_tv_internet_telefono.setEnabled(false);
        }
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
        else{
            d = new TransparentProgressDialog(this, R.drawable.progress_img, "CARGANDO");
            d.show();
            TimeoutOperation t = new TimeoutOperation();
            t.execute();
        }
    }

    private class TimeoutOperation extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            startActivity(new Intent(Servicios.this, FormProductos.class));
            d.dismiss();
        }
    }
}
