import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Created by blues on 31-10-2017.
 */
public class ServidorDistritoMulticastThread implements Runnable {
    ArrayList<Titanes> syncList = null;
    MulticastSocket sock = null;

    ServidorDistritoMulticastThread(MulticastSocket socket, ArrayList<Titanes> lista){
        this.sock = socket;
        this.syncList = lista;
    }

    public void run() {
        DatagramPacket out;
        ObjectOutputStream out_cls;
        ByteArrayOutputStream out_byt;
        byte[] sendData;
        while(true){
            try {
                wait(2000);
                out_byt = new ByteArrayOutputStream();
                out_cls = new ObjectOutputStream(out_byt);
                out_cls.writeObject(this.syncList);
                out_cls.flush();
                sendData = out_byt.toByteArray();
                out = new DatagramPacket(sendData, sendData.length);
                sock.send(out);
                out_byt.close();
                out_cls.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void updateList(ArrayList<Titanes> aux){
        this.syncList = aux;
    }
}
