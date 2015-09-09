package felipe.cl.spm.actividades.extras;

import org.json.JSONException;
import org.json.JSONObject;

public class Cliente {

    public int id;
    public String rut;
    public String primerNombre;
    public String segundoNombre;
    public String apellidoPaterno;
    public String apellidoMaterno;
    public String telefonoFijo;
    public String telefonoMovil;
    public String direccion;
    public Plan planContratado;
    public String estadoActual;

    public Cliente(JSONObject datos){
        try {
            id = datos.getInt("ID");
            rut = datos.getString("RUT");
            primerNombre = datos.getString("PRIMER_NOMBRE");
            segundoNombre = datos.getString("SEGUNDO_NOMBRE");
            apellidoPaterno= datos.getString("APELLIDO_PATERNO");
            apellidoMaterno = datos.getString("APELLIDO_MATERNO");
            telefonoFijo = datos.getString("TELEFONO_FIJO");
            telefonoMovil = datos.getString("TELEFONO_MOVIL");
            direccion = datos.getString("DIRECCION");
            JSONObject plan = datos.getJSONObject("PLAN_CONTRATADO");
            if(plan != null)
                planContratado = new Plan(plan.getInt("ID"),plan.getString("NOMBRE"),plan.getString("DESCRIPCION"),plan.getInt("PRECIO"));
            else planContratado = null;
            estadoActual = datos.getString("ESTADO_ACTUAL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
