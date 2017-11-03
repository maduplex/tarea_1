import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by blues on 30-10-2017.
 */
public class ServidorCentralDistritoThread implements Runnable {
    boolean running = true;
    int port;
    ArrayList<DistritoData> servers = new ArrayList<>();

    public ServidorCentralDistritoThread(){
        this.port = 9001;
    }

    public ServidorCentralDistritoThread(int puerto){
        this.port = puerto;
    }

    public void run(){
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buffer;

        try{
            socket = new DatagramSocket(this.port);
            buffer = new byte[2048];
            packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("[Servidor Central] Recibiendo registro de Distritos en puerto " + String.valueOf(this.port));
        } catch (SocketException e) {
            e.printStackTrace();
        }

        byte[] ack;
        DatagramPacket ackg;
        while(this.running){
            try{
                socket.receive(packet);
                addServer(packet);
                ack = "OK".getBytes();
                ackg = new DatagramPacket(ack, ack.length, packet.getAddress(), packet.getPort());
                socket.send(ackg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public ArrayList<DistritoData> getServers(){
        return this.servers;
    }

    private void addServer(DatagramPacket input){
        byte[] data = input.getData();
        DistritoData server;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            ObjectInputStream is = new ObjectInputStream(in);
            server = (DistritoData) is.readObject();
            this.servers.add(server);
            System.out.println("[Servidor Central] Distrito " + server.name + " agregado.");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
