import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by blues on 02-11-2017.
 */
public class TitanesList implements Serializable {
    private ArrayList<Titanes> lista;

    TitanesList(ArrayList<Titanes> new_list){
        this.lista = new_list;
    }

    public ArrayList<Titanes> getLista(){
        return this.lista;
    }

    public void updateLista(ArrayList<Titanes> new_lista){
        this.lista = new_lista;
    }
}
