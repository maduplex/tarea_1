import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Created by blues on 31-10-2017.
 */
public class ServidorDistritoCLIThread implements Runnable {
    Titanes titan = null;
    String name;
    InetAddress address;
    boolean newTitan = false;

    ServidorDistritoCLIThread(String nombre, InetAddress add){
        this.name = nombre;
        this.address = add;
    }

    Scanner in = new Scanner(System.in);
    public void run() {
        int input;
        String nombre;
        String tipo;
        String iD;
        while(true){
            System.out.println("[Distrito " + this.name + "] ¿Desea agregar Titan?\n" +
                    "1.- SI\n" +
                    "2.- NO (Se le volvera a preguntar)");
            input = Integer.valueOf(in.nextLine());
            if (input == 1){
                System.out.println("[Distrito " + this.name + "] Introducir Nombre");
                nombre = in.nextLine();
                System.out.println("[Distrito " + this.name + "] Introducir Tipo\n" +
                        "1.- Normal\n" +
                        "2.- Excentrico\n" +
                        "3.- Cambiante");
                input = Integer.valueOf(in.nextLine());
                if(input == 1){
                    tipo = "Normal";
                } else if (input == 2){
                    tipo = "Excentrico";
                } else{
                    tipo = "Cambiante";
                }
                iD = getID();
                this.titan = new Titanes(nombre, tipo, iD);
                this.newTitan = true;
                System.out.println("[Distrito " + this.name + "] Se ha publicado el titan: " + this.titan.nombre + "\n" +
                        "***************************\n" +
                        "ID: " + this.titan.iD +"\n" +
                        "Nombre: " + this.titan.nombre +"\n" +
                        "Tipo: " + this.titan.tipo +"\n" +
                        "***************************"
                );
            }
        }
    }

    private String getID(){
        String iD = "";
        try {
            DatagramSocket sock = new DatagramSocket(9002);
            byte[] req = "PLS".getBytes();
            DatagramPacket out = new DatagramPacket(req, req.length, this.address, 9002);
            sock.send(out);
            byte[] res = new byte[1024];
            DatagramPacket in =  new DatagramPacket(res, res.length);
            sock.receive(in);
            iD = new String(in.getData(), 0, in.getLength());
            sock.close();
            return iD;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iD;
    }

    public Titanes getTitan(){
        return this.titan;
    }

    public boolean askNew(){
        return this.newTitan;
    }

    public void setNull(){
        this.titan = null;
        this.newTitan = false;
    }
}
