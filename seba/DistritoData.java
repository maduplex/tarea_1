import java.io.Serializable;

/**
 * Created by blues on 30-10-2017.
 */
public class DistritoData implements Serializable {
    String name;
    String[] multiAddress;
    String[] uniAddress;


    public DistritoData(String init_name, String init_multiAddress, String init_multiPort, String init_uniAddress, String init_uniPort){
        this.name = init_name;
        this.multiAddress = new String[] {init_multiAddress, init_multiPort};
        this.uniAddress = new String[] {init_uniAddress, init_uniPort};
    }

    public DistritoData(){
        this.name = null;
        this.multiAddress = null;
        this.uniAddress = null;
    }
}
