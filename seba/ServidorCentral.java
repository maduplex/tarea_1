import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class ServidorCentral{

     static int puertoClientes = 9000;
     static int puertoDistrito = 9001;
     static int puertoTitanes = 9002;

    public static void main(String[] args) throws IOException{
        ArrayList <String[]>   clientes = new ArrayList<>();
        ServidorCentralDistritoThread ser_dist = new ServidorCentralDistritoThread(puertoDistrito);
        Thread t1 = new Thread(ser_dist);
        t1.start();

        ServidorCentralThreadTitanico ser_tit = new ServidorCentralThreadTitanico(puertoTitanes);
        Thread t2 = new Thread(ser_tit);
        t2.start();

        DatagramSocket socket = new DatagramSocket(puertoClientes);
        System.out.println("[Servidor Central] Recibiendo Clientes en el puerto " + String.valueOf(puertoClientes));
        byte[] recData = new byte[2048];
        byte[] sendData;
        DatagramPacket rec_pack = new DatagramPacket(recData, recData.length);
        DatagramPacket send_pack;

        Scanner sc = new Scanner(System.in);
        int in;
        ArrayList<DistritoData> servers;
        ByteArrayOutputStream out;
        ObjectOutputStream outstr;

        while(true){
            socket.receive(rec_pack);
            String distrito = new String(rec_pack.getData(), 0, rec_pack.getLength());
            System.out.println("[Servidor Central] Dar autorizacion a " + rec_pack.getAddress().getHostAddress() + " por " + distrito + " ?\n" +
                    "1.- SI\n" +
                    "2.- NO");
            in  = Integer.valueOf(sc.nextLine());
            if (in == 1){
                servers = ser_dist.getServers();
                for(int i = 0; i< servers.size(); i++){
                    if(servers.get(i).name.matches(distrito)){
                        out = new ByteArrayOutputStream();
                        outstr = new ObjectOutputStream(out);
                        outstr.writeObject(servers.get(i));
                        outstr.flush();
                        sendData = out.toByteArray();
                        send_pack = new DatagramPacket(sendData, sendData.length, rec_pack.getAddress(), rec_pack.getPort());
                        socket.send(send_pack);
                        out.close();
                        outstr.close();
                        boolean needed = true;
                        int index = -1;
                        for(int j = 0; j < clientes.size(); j++){
                            if(clientes.get(j)[0].matches(rec_pack.getAddress().getHostAddress())){
                                index = j;
                                needed = false;
                            }
                        }
                        if(!needed){
                            clientes.remove(index);
                        }
                        clientes.add(new String[] {rec_pack.getAddress().getHostAddress(), distrito});
                    }
                }
            }
            else{
                sendData = "NO".getBytes();
                send_pack = new DatagramPacket(sendData, sendData.length, rec_pack.getAddress(), rec_pack.getPort());
                socket.send(send_pack);
            }
            System.out.println("[Servidor Central] Respuesta enviada a " + rec_pack.getAddress().getHostAddress());
        }

    }

}