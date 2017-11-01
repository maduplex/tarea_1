import java.io.Serializable;

/**
 * Created by blues on 31-10-2017.
 */
public class Titanes implements Serializable {
    String nombre;
    String tipo;
    String iD;
    String obtencion = "";

    Titanes(String name, String type, String id){
        this.nombre = name;
        this.tipo = type;
        this.iD = id;
    }

    public void setObtencion(String metodo){
        this.obtencion = metodo;
    }
}
