package felipe.cl.spm.actividades.extras;

public class Plan {

    int id;
    String nombre;
    String descripcion;
    float precio;

    public Plan(int ID, String NOMBRE, String DESCRIPCION, float PRECIO){
        id = ID;
        nombre = NOMBRE;
        descripcion = DESCRIPCION;
        precio = PRECIO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }
}
