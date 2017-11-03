import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by blues on 01-11-2017.
 */
public class ClienteMulticastThread implements Runnable {
    MulticastSocket sock;
    ArrayList<Titanes> titanes;
    boolean running = true;

    ClienteMulticastThread(MulticastSocket socket){
        this.sock = socket;
    }

    public void run() {
        byte[] msg = new byte[2048];
        DatagramPacket res = new DatagramPacket(msg, msg.length);

        while(running){
            try {
                this.sock.receive(res);
                String mess = new String(res.getData(), 0, res.getLength());
                String[] test = mess.split(":");
                if(test[0].matches("msg")){
                    System.out.println("[Cliente] Aparece nuevo Titan!" + test[1] + ".");
                }
                else{
                    byte[] data = res.getData();
                    TitanesList new_list;
                    ByteArrayInputStream input = new ByteArrayInputStream(data);
                    ObjectInputStream is = new ObjectInputStream(input);
                    new_list = (TitanesList) is.readObject();
                    this.titanes = new_list.getLista();
                    is.close();
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeDistrictData(MulticastSocket new_sock) throws InterruptedException {
        this.sock = new_sock;
    }

    public ArrayList<Titanes> getTitanes(){
        return this.titanes;
    }
}
