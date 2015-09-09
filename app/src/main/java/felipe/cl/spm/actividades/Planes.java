package felipe.cl.spm.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.Cliente;
import felipe.cl.spm.actividades.extras.Constantes;
import felipe.cl.spm.actividades.extras.CustomStateDialog;
import felipe.cl.spm.actividades.extras.TransparentProgressDialog;
import felipe.cl.spm.actividades.extras.WSManager;

public class Planes extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    TransparentProgressDialog d;
    Context mContext;

    public static Activity activity;
    int isNew = 0;
    ImageButton btn_back, btn_ok;
    CheckBox tv, ph, net;
    Spinner pTV, pTF, pNET;
    LinearLayout lTV, lTF, lNET;
    String rut;

    int tvSelected, tfSelected, netSelected;
    private WSManager wsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_planes);

        mContext = this;
        activity = this;
        rut = getIntent().getStringExtra("RUT");

        btn_back = (ImageButton) findViewById(R.id.ib_back);
        btn_ok = (ImageButton) findViewById(R.id.btn_ok);
        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        tv = (CheckBox) findViewById(R.id.isTv);
        ph = (CheckBox) findViewById(R.id.isTF);
        net = (CheckBox) findViewById(R.id.isNet);
        tv.setOnCheckedChangeListener(this);
        ph.setOnCheckedChangeListener(this);
        net.setOnCheckedChangeListener(this);

        pTV = (Spinner) findViewById(R.id.pTV);
        pTF = (Spinner) findViewById(R.id.pTF);
        pNET = (Spinner) findViewById(R.id.pNet);
        pTV.setOnItemSelectedListener(this);
        pTF.setOnItemSelectedListener(this);
        pNET.setOnItemSelectedListener(this);

        lTV = (LinearLayout) findViewById(R.id.lTV);
        lTF = (LinearLayout) findViewById(R.id.lTF);
        lNET = (LinearLayout) findViewById(R.id.lNET);


        wsManager = new WSManager(this) {
            @Override
            public void terminaRequest(boolean b) {
                if (b) {
                    Intent n = new Intent(mContext, FormValidacionVisual.class);
                    n.putExtra("RUT", rut);
                    startActivity(n);
                } else {
                    CustomStateDialog d = new CustomStateDialog(mContext, R.drawable.ic_state_nok, "Error al ingresar la información.\nPor favor reintente", Constantes.STATE_NOK);
                    d.show();
                }
            }
        };

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        }
        if (v.getId() == R.id.btn_ok) {
            String plan = getPlan();
            if(plan!= null)
                wsManager.cambiarPlan(rut, getPlan());
            else{
                Toast.makeText(mContext, "Debe seleccionar al menos un plan para continuar", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getPlan() {
        String plan = "20";
        if (ph.isChecked()) {
            if (tv.isChecked()) {
                if (net.isChecked()) {
                    // TODO LOS 3
                    if (netSelected == 0 && tvSelected == 0) {
                        plan = "1";
                    } else if (netSelected == 1 && tvSelected == 0) {
                        plan = "24";
                    } else if (netSelected == 1 && tvSelected == 1) {
                        plan = "25";
                    } else {
                        plan = "26";
                    }
                } else {
                    // TODO TELEFONO + TV
                    if (tfSelected == 0 && tvSelected == 0) {
                        plan = "21";
                    } else if (tfSelected == 1 && tvSelected == 0) {
                        plan = "8";
                    } else if (tfSelected == 1 && tvSelected == 1) {
                        plan = "22";
                    } else {
                        plan = "7";
                    }
                }
            } else {
                if (net.isChecked()) {
                    // TODO INTERNET + TELEFONO
                    if (netSelected == 0 && tfSelected == 0) {
                        plan = "6";
                    } else if (netSelected == 1 && tfSelected == 0) {
                        plan = "13";
                    } else if (netSelected == 1 && tfSelected == 1) {
                        plan = "16";
                    } else {
                        plan = "14";
                    }
                } else {
                    //TODO TELEFONO
                    if(tfSelected == 0){
                        plan = "5";
                    }else if(tfSelected == 1){
                        plan = "12";
                    }

                }
            }
        }else{
            if(tv.isChecked()){
                if(net.isChecked()){
                    //TODO TV + INTERNET
                    if (netSelected == 0 && tvSelected == 0) {
                        plan = "2";
                    } else if (netSelected == 1 && tvSelected == 0) {
                        plan = "23";
                    } else if (netSelected == 1 && tvSelected == 1) {
                        plan = "19";
                    } else {
                        plan = "18";
                    }
                }else{
                    //TODO TV
                    if(tvSelected == 0){
                        plan = "9";
                    }else if (tvSelected == 1){
                        plan = "10";
                    }
                }
            }else{
                if(net.isChecked()){
                    //TODO INTERNET
                    if(netSelected == 0){
                        plan = "3";
                    }else if(netSelected == 1){
                        plan = "15";
                    }
                }else{
                    plan = null;
                }
            }
        }
        return plan;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.d("PLAN", "ID: "+view.getId()+" SELECTED: "+ i);
        switch (adapterView.getId()) {
            case R.id.pTV:
                tvSelected = i;
                break;
            case R.id.pTF:
                tfSelected = i;
                break;
            case R.id.pNet:
                netSelected = i;
                break;
            default:
                break;
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.isTv:

                if (b) {
                    if (net.isChecked() && ph.isChecked()) {
                        pTF.setEnabled(false);
                        pTF.setSelection(0);
                    }
                    lTV.setVisibility(View.VISIBLE);
                } else {
                    pTF.setEnabled(true);
                    lTV.setVisibility(View.GONE);
                }
                break;
            case R.id.isTF:
                if (b) {
                    if (tv.isChecked() && net.isChecked()) {
                        pTF.setEnabled(false);
                        pTF.setSelection(0);
                    }
                    lTF.setVisibility(View.VISIBLE);
                } else {
                    lTF.setVisibility(View.GONE);
                }
                break;
            case R.id.isNet:
                if (b) {
                    if (tv.isChecked() && ph.isChecked()) {
                        pTF.setEnabled(false);
                        pTF.setSelection(0);
                    }
                    lNET.setVisibility(View.VISIBLE);
                } else {
                    pTF.setEnabled(true);
                    lNET.setVisibility(View.GONE);
                }
                break;
            default:
                break;
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
            startActivity(new Intent(Planes.this, FormProductos.class));
            d.dismiss();
        }
    }
}
