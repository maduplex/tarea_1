import java.io.*;
import java.net.*;

	
public class ClienteThread extends Thread{


		
		private Socket clientSocket;
		public boolean exit;
		
		ClienteThread(Socket s) {
			this.clientSocket = s;
			exit = false;
		}

		public void run() {
			
	    	  try {
	    		  BufferedReader socIn = null;
	        		socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  
	       
	        		Juego j= new Juego(clientSocket);
	         	 	j.Juego();
	        	
	      		while (true) {
	      			//System.out.print(message);
	      			String message=socIn.readLine();
	      			if (message.equals("."))
	      			{
	      				break;
	      			}
	      			System.out.println(message);
	      			}
	    		

	    	} catch (Exception e) {
	        	System.err.println("Error in ClienteThread:" + e); 
	        	}
	       }
	  
	  }

