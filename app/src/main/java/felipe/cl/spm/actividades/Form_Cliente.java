package felipe.cl.spm.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.Constantes;
import felipe.cl.spm.actividades.extras.CustomStateDialog;
import felipe.cl.spm.actividades.extras.WSManager;

public class Form_Cliente extends Activity implements View.OnClickListener {
    ImageButton btn_back, btn_save;
    EditText nombres, paterno, materno, calle, numero, comuna, region, fijo, movil;
    WSManager wsManager;
    Context mContext;
    String rut;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_form_cliente);

        mContext = this;
        activity = this;
        rut = getIntent().getStringExtra("RUT");

        btn_back = (ImageButton) findViewById(R.id.ib_back);
        btn_save = (ImageButton) findViewById(R.id.ib_save);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        nombres = (EditText) findViewById(R.id.nombres);
        paterno = (EditText) findViewById(R.id.paterno);
        materno = (EditText) findViewById(R.id.materno);
        calle = (EditText) findViewById(R.id.calle);
        numero = (EditText) findViewById(R.id.numero);
        comuna = (EditText) findViewById(R.id.comuna);
        region  = (EditText) findViewById(R.id.region);
        fijo = (EditText) findViewById(R.id.fijo);
        movil  = (EditText) findViewById(R.id.movil);

        wsManager = new WSManager(this) {
            @Override
            public void terminaRequest(boolean b) {
                if(b){
                    CustomStateDialog d = new CustomStateDialog(mContext, R.drawable.ic_state_ok, "Cliente Ingresado con Éxito", Constantes.STATE_OK);
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Intent n = new Intent(mContext, Planes.class);
                            n.putExtra("RUT", rut);
                            startActivity(n);
                        }
                    });
                    d.show();
                }
                else {
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
        if (v.getId() == R.id.ib_save) {
            if(nombres.getText().length() > 0 && paterno.getText().length()>0 && materno.getText().length()> 0 && calle.getText().length()>0 &&
                    numero.getText().length()>0 && comuna.getText().length()>0 && region.getText().length()>0 && fijo.getText().length()>0 &&
                    movil.getText().length()>0) {
                String[] names = nombres.getText().toString().split(" ");
                String n1, n2;
                n1 = names[0];
                if(names.length == 1){
                    n2 = "";
                }else
                    n2 = names[1];

                String dir = calle.getText().toString() +" "+ numero.getText().toString() + ", "+ comuna.getText().toString() + ", " +region.getText().toString();
                wsManager.crearUsuario(rut,n1,n2,paterno.getText().toString(),
                        materno.getText().toString(),
                        fijo.getText().toString(),
                        movil.getText().toString(),
                        dir, "8", "PENDIENTE");
            }

        }
    }
}
