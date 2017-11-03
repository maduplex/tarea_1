import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by blues on 31-10-2017.
 */
public class ServidorDistritoMulticastThread implements Runnable {
    TitanesList syncList = new TitanesList(new ArrayList<Titanes>());
    MulticastSocket sock = null;
    String[] out_Data;

    ServidorDistritoMulticastThread(MulticastSocket socket, ArrayList<Titanes> lista, String[] data){
        this.sock = socket;
        this.syncList.updateLista(lista);
        this.out_Data = data;
    }

    public void run() {
        DatagramPacket out;
        ObjectOutputStream out_cls;
        ByteArrayOutputStream out_byt;
        byte[] sendData;
        while(true){
            try {
                out_byt = new ByteArrayOutputStream();
                out_cls = new ObjectOutputStream(out_byt);
                out_cls.writeObject(this.syncList);
                out_cls.flush();
                sendData = out_byt.toByteArray();
                out = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(this.out_Data[0]), Integer.valueOf(this.out_Data[1]));
                this.sock.send(out);
                out_byt.close();
                out_cls.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void updateList(ArrayList<Titanes> aux){
        this.syncList.updateLista(aux);
    }
}
