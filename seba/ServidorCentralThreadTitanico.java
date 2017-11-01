import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by blues on 30-10-2017.
 */
public class ServidorCentralThreadTitanico implements Runnable {
    int port;
    String id = "1";

    public ServidorCentralThreadTitanico(){
        this.port = 9002;
    }

    public ServidorCentralThreadTitanico(int puerto){
        this.port = puerto;
    }

    public void run() {
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buffer;

        try{
            socket = new DatagramSocket(this.port);
            buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("[Servidor Central] Respondiendo IDs de Titantes en puerto " + String.valueOf(this.port));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] sendData;
        while(true){
            try{
                socket.receive(packet);
                sendData = this.id.getBytes();
                DatagramPacket send_packet = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                socket.send(send_packet);
                this.id = Integer.toString(Integer.parseInt(this.id) + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
