import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by blues on 31-10-2017.
 */
public class ServidorDistritoAuxThread implements Runnable{
    ArrayList<Titanes> list;
    boolean change = false;
    boolean busy = false;
    int puerto;

    ServidorDistritoAuxThread(ArrayList<Titanes> aux, int port){
        this.list = aux;
        this.puerto = port;
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(this.puerto);
            byte[] in_msg = new byte[1024];
            DatagramPacket in = new DatagramPacket(in_msg, in_msg.length);
            Titanes aux;
            DatagramPacket out;
            byte[] out_msg;
            ObjectOutputStream outstr;
            ByteArrayOutputStream outbyt;
            while(true){
                socket.receive(in);
                this.busy = true;
                aux = manageTitan(in);
                if(aux != null){
                    this.change = true;
                    outbyt = new ByteArrayOutputStream();
                    outstr = new ObjectOutputStream(outbyt);
                    outstr.writeObject(aux);
                    outstr.flush();
                    out_msg = outbyt.toByteArray();
                    out = new DatagramPacket(out_msg, out_msg.length, in.getAddress(), in.getPort());
                    socket.send(out);
                }
                this.busy = false;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Titanes manageTitan(DatagramPacket packet){
        String req = new String(packet.getData(), 0, packet.getLength());
        String[] req_spl = req.split(":");
        String method = req_spl[0];
        String iD = req_spl[1];
        int index = -1;
        for(int i = 0; i < this.list.size(); i++){
            if(this.list.get(i).iD.matches(iD)){
                if(method.matches("cap") && (this.list.get(i).tipo.matches("Normal") || this.list.get(i).tipo.matches("Cambiante"))){
                    index = i;
                }
                else if(method.matches("as") && (this.list.get(i).tipo.matches("Normal") || this.list.get(i).tipo.matches("Excentrico"))){
                    index = i;
                }
            }
        }
        Titanes return_tit;
        if(index != -1){
            return_tit = this.list.get(index);
            this.list.remove(index);
            return return_tit;
        }
        return null;
    }

    public ArrayList<Titanes> getList(){
        this.change = false;
        return this.list;
    }

    public void updateList(ArrayList<Titanes> aux){
        this.list = aux;
    }

    public boolean askChange(){
        return this.change;
    }

    public boolean askBusy(){
        return this.busy;
    }

}
