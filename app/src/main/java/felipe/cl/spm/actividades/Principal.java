package felipe.cl.spm.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.Cliente;
import felipe.cl.spm.actividades.extras.Constantes;
import felipe.cl.spm.actividades.extras.CustomStateDialog;
import felipe.cl.spm.actividades.extras.Plan;
import felipe.cl.spm.actividades.extras.TransparentProgressDialog;
import felipe.cl.spm.actividades.extras.WSManager;

public class Principal extends Activity implements View.OnClickListener {
    Context mContext;
    ImageButton btn_search;
    EditText edit_rut;
    TransparentProgressDialog d;
    WSManager wsManager;
    String rut;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_rut);

        btn_search = (ImageButton) findViewById(R.id.search);
        btn_search.setOnClickListener(this);
        edit_rut = (EditText)findViewById(R.id.edit_rut);

        mContext = this;
        activity = this;

    }

    private String transformar(String q) throws Exception{
            String tmp, M, m, d, v;
            v = q.substring(q.length() - 1);
            tmp = q.substring(0, q.length() - 1);

            d = tmp.substring(q.length() - 4);
            tmp = tmp.substring(0, q.length() - 4);

            m = tmp.substring(q.length() - 7);
            tmp = tmp.substring(0, q.length() - 7);

            M = tmp;

            return M + "." + m + "." + d + "-" + v;
            }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search) {
            try {
                if (edit_rut.getText().toString().length() <= 10) {
                    rut = transformar(edit_rut.getText().toString());

                    wsManager = new WSManager(this) {
                        @Override
                        public void terminaRequest(Cliente cliente) {
                            if (cliente == null) {
                                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(mContext);
                                b.setCancelable(false);
                                b.setMessage("¿Desea ingresarlo como NUEVO CLIENTE?");
                                b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent n = new Intent(Principal.this, Form_Cliente.class);
                                        n.putExtra("RUT", rut);
                                        startActivity(n);
                                    }
                                });
                                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                b.show();
                                return;
                            }
                            if (cliente.estadoActual.equals("OK")) {
                                    Intent n = new Intent(Principal.this, DetalleCliente.class);
                                    n.putExtra("RUT", rut);
                                    startActivity(n);
                            } else if (cliente.estadoActual.equals("MORA")){
                                CustomStateDialog d = new CustomStateDialog(mContext, R.drawable.ic_state_nok, "Cliente Moroso", Constantes.STATE_NOK);

                                d.show();
                            }

                        }
                    };
                    wsManager.getDatos(rut);
                }
                }catch(Exception e){
                    Toast.makeText(this, "Rut Incorrecto", Toast.LENGTH_LONG).show();
                }

        }
    }

    private class TimeoutOperation extends AsyncTask<String, Void, Void> {
        Context context;
        int caso;
        int duration;
        Dialog dialog;

        public TimeoutOperation(Context context, Dialog d, int caso, int duration) {
            this.context = context;
            this.duration = duration;
            this.dialog = d;
            this.caso = caso;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if(caso == 0){
                startActivity(new Intent(Principal.this, Form_Cliente.class));
            }else if(caso == 1){
                startActivity(new Intent(Principal.this, DetalleCliente.class));
            }else if(caso == 2){
                CustomStateDialog di = new CustomStateDialog(Principal.this, R.drawable.ic_state_nok, "Cliente no puede contratar servicios", Constantes.STATE_NOK);
                di.show();
                TimeoutOperation t = new TimeoutOperation(context,di,-1, Constantes.TIME_LONG);
                t.execute();
            }
        }
    }

}
