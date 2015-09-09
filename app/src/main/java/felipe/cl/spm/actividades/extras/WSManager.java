package felipe.cl.spm.actividades.extras;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WSManager {

    private RequestQueue queue;
    private Context contexto;
    private final String url = "http://madgoatstd.com/SPM/ws.php";

    public WSManager(Context contexto){
        queue = Volley.newRequestQueue(contexto);
        this.contexto = contexto;
    }

    public void getDatos(String rut){
        final ProgressDialog dialog = new ProgressDialog(contexto);
        dialog.setTitle("Obteniendo datos");
        dialog.setMessage("Buscando información del cliente...");
        dialog.setCancelable(false);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url+"?accion=checkRut&rut="+rut,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        try {
                            JSONObject o = response.getJSONObject(0);
                            if(o.getString("RESPUESTA").equals("OK")){
                                terminaRequest(new Cliente(response.getJSONObject(0)));

                            }else
                                terminaRequest(null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        errorRequest(error);
                    }
                }
        );
        queue.add(jsonArrayRequest);
        dialog.show();
    }

    public void cambiarPlan(final String rut, final String idPlan){
        final ProgressDialog dialog = new ProgressDialog(contexto);
        dialog.setTitle("Enviando Datos");
        dialog.setMessage("Enviando datos al servidor...");
        dialog.setCancelable(false);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+"?accion=cambiarPlan",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject respuesta = new JSONObject(response);
                            terminaRequest(respuesta.getString("RESPUESTA").equals("OK"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        errorRequest(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("rut", rut);
                params.put("idPlan", idPlan);

                return params;
            }
        };
        queue.add(postRequest);
        dialog.show();
    }

    public void crearUsuario(final String rut, final String primerNombre, final String segundoNombre, final String apellidoPaterno, final String apellidoMaterno, final String telefonoFijo, final String telefonoMovil, final String direccion, final String planContratado, final String estadoActual){
        final ProgressDialog dialog = new ProgressDialog(contexto);
        dialog.setTitle("Enviando Datos");
        dialog.setMessage("Enviando datos al servidor...");
        dialog.setCancelable(false);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+"?accion=nuevoUsuario",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject respuesta = new JSONObject(response);
                            terminaRequest(respuesta.getString("RESPUESTA").equals("OK"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        errorRequest(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("rut", rut);
                params.put("primerNombre", primerNombre);
                params.put("segundoNombre", segundoNombre);
                params.put("apellidoPaterno", apellidoPaterno);
                params.put("apellidoMaterno", apellidoMaterno);
                params.put("telefonoFijo", telefonoFijo);
                params.put("telefonoMovil", telefonoMovil);
                params.put("direccion", direccion);
                params.put("planContratado", planContratado);
                params.put("estadoActual", estadoActual);

                return params;
            }
        };
        queue.add(postRequest);
        dialog.show();
    }

    public void terminaRequest(Cliente cliente) {}
    public void terminaRequest(boolean resultado) {}

    public void errorRequest(VolleyError error){Log.v("Debug",error.toString());}

}
