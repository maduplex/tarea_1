import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServidorDistrito{
    public static void main(String[] args) throws IOException{
        boolean registrado = false;
        ArrayList<Titanes> titanes = new ArrayList<>();
        Scanner in = new Scanner(System.in);

        System.out.println("[Distrito] Nombre del Servidor:");
        String name = in.nextLine();
        System.out.println("[Distrito " + name + "] IP Multicast:");
        String ip_multi = in.nextLine();
        System.out.println("[Distrito " + name + "] Puerto Multicast:");
        String port_multi = in.nextLine();
        System.out.println("[Distrito " + name + "] IP Peticiones:");
        String ip_uni = in.nextLine();
        System.out.println("[Distrito " + name + "] Puerto Peticiones:");
        String port_uni = in.nextLine();
        System.out.println("[Distrito " + name + "] IP Servidor Central:");
        String ip_cent = in.nextLine();

        DistritoData selfData = new DistritoData(name, ip_multi, port_multi, ip_uni, port_uni);

        registrado = sendData(selfData, ip_cent, "9001");

        MulticastSocket socket = new MulticastSocket(Integer.valueOf(port_multi));
        socket.joinGroup(InetAddress.getByName(ip_multi));

        ServidorDistritoCLIThread cliThread = new ServidorDistritoCLIThread(name, InetAddress.getByName(ip_cent));
        Thread t1 = new Thread(cliThread);

        ServidorDistritoMulticastThread multicastThread = new ServidorDistritoMulticastThread(socket, titanes);
        Thread t2 = new Thread(multicastThread);

        ServidorDistritoAuxThread unicastThread = new ServidorDistritoAuxThread(titanes, Integer.valueOf(port_uni));
        Thread t3 = new Thread(unicastThread);

        t1.start();
        t2.start();
        t3.start();

        while(registrado){
            if(cliThread.askNew() && !unicastThread.askBusy()){
                if(unicastThread.askChange()){
                    titanes = unicastThread.getList();
                }
                titanes.add(cliThread.getTitan());
                signalChange(socket, cliThread.getTitan());
                cliThread.setNull();
                unicastThread.updateList(titanes);
                multicastThread.updateList(titanes);
            }
            else if(cliThread.askNew() && unicastThread.askBusy()){
                while(unicastThread.askBusy()){
                }
                if(unicastThread.askChange()){
                    titanes = unicastThread.getList();
                }
                titanes.add(cliThread.getTitan());
                signalChange(socket, cliThread.getTitan());
                cliThread.setNull();
                unicastThread.updateList(titanes);
                multicastThread.updateList(titanes);
            }
            else if(unicastThread.askChange()){
                titanes = unicastThread.getList();
                multicastThread.updateList(titanes);
            }
        }

    }

    private static void signalChange(MulticastSocket sock, Titanes titan) throws IOException {
        DatagramPacket out;
        byte[] out_msg;

        String out_string = "msg:" + titan.nombre + ", tipo " + titan.tipo + ", ID " + titan.iD;
        out_msg = out_string.getBytes();
        out = new DatagramPacket(out_msg, out_msg.length);
        sock.send(out);
    }

    private static boolean sendData(DistritoData data, String ip, String port){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outsrc = null;
        try {
            InetAddress address = InetAddress.getByName(ip);
            int puerto = Integer.valueOf(port);
            outsrc = new ObjectOutputStream(out);
            outsrc.writeObject(data);
            outsrc.flush();
            byte[] buff = out.toByteArray();
            DatagramPacket outpck = new DatagramPacket(buff, buff.length, address, puerto);
            DatagramSocket socket = new DatagramSocket(489);
            socket.send(outpck);
            byte[] ack = new byte[1024];
            DatagramPacket ackg = new DatagramPacket(ack, ack.length);
            socket.setSoTimeout(5000);
            socket.receive(ackg);
            String ackn = new String(ackg.getData(), 0, ackg.getLength());
            if(ackn.matches("OK")){
                return true;
            }
            socket.close();
            out.close();
            outsrc.close();
            System.out.println("[Distrito " + data.name + "] Datos inscritos en Servidor Central");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }
}