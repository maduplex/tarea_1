import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Created by blues on 01-11-2017.
 */
public class ClienteMulticastThread implements Runnable {
    DistritoData distrito;
    MulticastSocket sock;
    ArrayList<Titanes> titanes;
    boolean running = true;

    ClienteMulticastThread(MulticastSocket socket, DistritoData data){
        this.sock = socket;
        this.distrito = data;
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
                    ArrayList<Titanes> new_list;
                    ByteArrayInputStream input = new ByteArrayInputStream(data);
                    ObjectInputStream is = new ObjectInputStream(input);
                    new_list = (ArrayList<Titanes>) is.readObject();
                    this.titanes = new_list;
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

    public void changeDistrictData(DistritoData new_data) throws InterruptedException {
        this.distrito = new_data;
        wait(2000);
        this.running = true;
    }

    public ArrayList<Titanes> getTitanes(){
        return this.titanes;
    }

    public void stopThread(){
        this.running = false;
    }
}
