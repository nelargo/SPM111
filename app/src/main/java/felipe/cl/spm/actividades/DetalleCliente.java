package felipe.cl.spm.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.Cliente;
import felipe.cl.spm.actividades.extras.Constantes;
import felipe.cl.spm.actividades.extras.CustomStateDialog;
import felipe.cl.spm.actividades.extras.CustomStateToast;
import felipe.cl.spm.actividades.extras.TransparentProgressDialog;
import felipe.cl.spm.actividades.extras.WSManager;

public class DetalleCliente extends Activity implements View.OnClickListener {
    ImageButton btn_ok, btn_nok;
    TextView eName, eRut, eState, ePlan;
    private WSManager wsManager;
    Context mContext;
    String rut;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_detalle_cliente);

        rut = getIntent().getStringExtra("RUT");
        mContext = this;
        activity = this;

        btn_ok = (ImageButton) findViewById(R.id.ib_ok);
        btn_nok = (ImageButton) findViewById(R.id.ib_nok);
        btn_ok.setOnClickListener(this);
        btn_nok.setOnClickListener(this);

        eName = (TextView) findViewById(R.id.enombre);
        eRut = (TextView) findViewById(R.id.erut);
        eState = (TextView) findViewById(R.id.eestado);
        ePlan = (TextView) findViewById(R.id.eplanes);

        wsManager = new WSManager(this) {
            @Override
            public void terminaRequest(Cliente cliente) {
                if(cliente !=  null){
                    eName.setText(cliente.primerNombre + " "+
                    cliente.segundoNombre + " " +
                    cliente.apellidoPaterno+ " " +
                    cliente.apellidoMaterno);
                    eRut.setText(rut);
                    eState.setText("ESTADO: "+cliente.estadoActual);
                    ePlan.setText(cliente.planContratado.getDescripcion());
                }else{
                    Toast.makeText(mContext, "Ha ocurrido un error, Por favor reintente", Toast.LENGTH_LONG).show();
                    ((Activity)mContext).finish();
                }
            }
        };
        //wsManager.getDatos(rut);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_ok) {
            Intent n = new Intent(this, Planes.class);
            n.putExtra("RUT", rut);
            startActivity(n);

        }
        if (v.getId() == R.id.ib_nok) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        wsManager.getDatos(rut);
    }
}
