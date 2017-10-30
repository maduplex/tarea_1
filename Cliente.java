import java.io.*;
import java.net.*;
import java.util.Scanner;



public class Cliente {

 
  /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {

        Socket socket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;
        Scanner sc = new Scanner(System.in);
    
        System.out.println("[Cliente] Ingresar IP Servidor Central");
        String IP = sc.nextLine();
        System.out.println("[Cliente] Ingresar Puerto Servidor Central");
        String puerto = sc.nextLine();


        
        try {
      	    // creation socket ==> connection
        
        socket = new Socket(IP,new Integer(puerto).intValue());
	    socIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));    
	    socOut= new PrintStream(socket.getOutputStream());
	    
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + IP);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "+ "the connection to:"+ IP);
            System.exit(1);
        }
                             
        System.out.println("[Cliente] Introducir Nombre de Distrito a Investigar, Ej: Trost, Shiganshina");
        String distrito = sc.nextLine();
        String line;
        try{

		socOut.println(distrito);
			
			
        ClienteThread ct = new ClienteThread (socket); 
        ct.run();
        
        while (true) 
        {

        	line=stdIn.readLine();
        	if (line.equals("."))
        	{ 
        		ct.exit =true;
        		break;	
        	}
        	//socOut.println(line);
        	//System.out.println(socIn.readLine());
        }
   	 	
        }catch (Exception e) {
            System.err.println("Error in ClientChat:" + e);
        }
        
      //socOut.close();
      //socIn.close();
      stdIn.close();
      //socket.close();
    
   }
}

