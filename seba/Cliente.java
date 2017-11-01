import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Cliente{
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);

        InetAddress address_cent;
        int puerto_cent;
        String distrito;
        boolean authorized = false;
        DistritoData distrito_data = null;
        ArrayList<Titanes> server_titanes;
        ArrayList<Titanes> self_titanes = new ArrayList<>();

        System.out.println("[Cliente] Ingrese IP Servidor Central");
        address_cent = InetAddress.getByName(in.nextLine());
        System.out.println("[Cliente] Ingrese Puerto Servidor Central");
        puerto_cent = in.nextInt();

        System.out.println("[Cliente] Introducir nombre de Distrito que desea investigar");
        distrito = in.nextLine();

        try {
            distrito_data = askPerm(address_cent, puerto_cent, distrito);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MulticastSocket sock_multi = new MulticastSocket(9000);
        DatagramSocket sock_uni = new DatagramSocket(9001);
        sock_uni.setSoTimeout(10000);
        byte[] msg;
        ClienteMulticastThread multicastThread = null;
        DatagramPacket req = null;
        DatagramPacket res = null;
        if(distrito_data != null){
            authorized = true;


            multicastThread = new ClienteMulticastThread(sock_multi, distrito_data);
            Thread t1 = new Thread(multicastThread);
            t1.start();
        }

        int input;
        while(authorized){
            System.out.println("[Cliente] Consola");
            System.out.println("[Cliente] (1) Listar Titanes");
            System.out.println("[Cliente] (2) Cambiar Distrito");
            System.out.println("[Cliente] (3) Capturar Titan");
            System.out.println("[Cliente] (4) Asesinar Titan");
            System.out.println("[Cliente] (5) Listar Titanes Capturados");
            System.out.println("[Cliente] (6) Listar Titanes Asesinados");
            input = in.nextInt();
            if(input == 1){
                server_titanes = multicastThread.getTitanes();
                System.out.println("*********************");
                for(int i = 0; i < server_titanes.size(); i++){
                    Titanes aux = server_titanes.get(i);
                    System.out.println("ID: " + aux.iD);
                    System.out.println("Nombre: " + aux.nombre);
                    System.out.println("Tipo: " + aux.tipo);
                    System.out.println("*********************");
                }
            }
            else if (input == 2){
                System.out.println("[Cliente] Ingrese el nombre del distrito nuevo");
                String new_dist = in.nextLine();
                DistritoData temp = askPerm(address_cent, puerto_cent, new_dist);
                if(temp != null){
                    distrito_data = temp;
                    multicastThread.stopThread();
                    try {
                        multicastThread.changeDistrictData(distrito_data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("[Cliente] Permiso de cambio no concebido o distrito no existe");
                }
            }
            else if (input == 3){
                System.out.println("[Cliente] Ingrese el ID del Titan que desea Capturar");
                String iD = in.nextLine();
                msg = ("cap:" + iD).getBytes();
                Titanes aux = manageTitans(msg, sock_uni, req, res);
                if(aux != null){
                    aux.setObtencion("Capturado");
                    self_titanes.add(aux);
                    System.out.println("[Cliente] Captura exitosa!");
                }
                else{
                    System.out.println("[Cliente] Captura fallida, el titan ya fue capturado, no existe, o el metodo es incorrecto");
                }
            }
            else if (input == 4){
                System.out.println("[Cliente] Ingrese el ID del Titan que desea Asesinar");
                String iD = in.nextLine();
                msg = ("as:" + iD).getBytes();
                Titanes aux = manageTitans(msg, sock_uni, req, res);
                if(aux != null){
                    aux.setObtencion("Asesinado");
                    self_titanes.add(aux);
                    System.out.println("[Cliente] Asesinato exitoso!");
                }
                else{
                    System.out.println("[Cliente] Captura fallida, el titan ya fue asesinado, no existe, o el metodo es incorrecto");
                }
            }
            else if (input == 5){
                System.out.println("*********************");
                for(int i = 0; i < self_titanes.size(); i++){
                    Titanes aux = self_titanes.get(i);
                    if(aux.obtencion.matches("Capturado")){
                        System.out.println("ID: " + aux.iD);
                        System.out.println("Nombre: " + aux.nombre);
                        System.out.println("Tipo: " + aux.tipo);
                        System.out.println("*********************");
                    }
                }
            }
            else if (input == 6){
                System.out.println("*********************");
                for(int i = 0; i < self_titanes.size(); i++){
                    Titanes aux = self_titanes.get(i);
                    if(aux.obtencion.matches("Asesinado")){
                        System.out.println("ID: " + aux.iD);
                        System.out.println("Nombre: " + aux.nombre);
                        System.out.println("Tipo: " + aux.tipo);
                        System.out.println("*********************");
                    }
                }
            }
        }

    }

    private static Titanes manageTitans(byte[] messg, DatagramSocket sock, DatagramPacket req, DatagramPacket res) throws IOException, ClassNotFoundException {
        req = new DatagramPacket(messg, messg.length);
        sock.send(req);
        messg = new byte[2048];
        res = new DatagramPacket(messg, messg.length);
        sock.receive(res);
        String test = new String(res.getData(), 0, res.getLength());
        if(test.matches("NO")){
            return null;
        }
        else{
            byte[] data = res.getData();
            Titanes new_titan;
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(input);
            new_titan = (Titanes) is.readObject();
            is.close();
            input.close();
            return new_titan;
        }
    }

    private static DistritoData askPerm(InetAddress add, int port, String name) throws IOException, ClassNotFoundException {
        DatagramSocket sock = new DatagramSocket(9000);
        byte[] msg = name.getBytes();
        DatagramPacket out_pack = new DatagramPacket(msg, msg.length, add, port);
        sock.send(out_pack);
        msg = new byte[1024];
        DatagramPacket in = new DatagramPacket(msg, msg.length);
        sock.receive(in);
        String res = new String(in.getData(), 0 ,in.getLength());
        if(res.matches("NO")){
            System.out.println("[Cliente] El servidor central no ha otorgado permiso o no se encontro distrito");
            System.exit(420);
        }
        else{
            byte[] data = in.getData();
            DistritoData server;
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(input);
            server = (DistritoData) is.readObject();
            is.close();
            input.close();
            return server;
        }
        return null;
    }
}