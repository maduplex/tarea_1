import java.io.*;
import java.net.*;
	
public class ServidorThread extends Thread{
	
		
		private Socket clientSocket;
		String Name;
		
		ServidorThread(Socket s,int i,String name) {
			this.clientSocket = s;
			Servidor.tabSocket[i]= s;
			Name=name;
		}

	 	/**
	  	* receives a request from client then sends an echo to the client
	  	* @param clientSocket the client socket
	  	**/
		public void run() {
	    	  try {
	    		BufferedReader socIn = null;
	    		socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
	    		PrintStream socOut;

	    		while (true) {
	    		String line = socIn.readLine();
	    		
	    		//client's disconnection (putting it out of the list)
	    		if (line.equals(".")){
	    			for(int j=1;j<Servidor.i; j++)
	    			{
	    				 if(clientSocket == Servidor.tabSocket[j]){
	    					 Servidor.tabSocket[j]=null;
	    				 }
	       		  	}
	    			break;
	    		}
	    		
				  for(int j=1;j<Servidor.i; j++)
				  {
					  socOut = new PrintStream(Servidor.tabSocket[j].getOutputStream());
					  socOut.println(Name+": "+line);
				  }
			  
			}
	    	} catch (Exception e) {
	        	System.err.println("Error in ServidorThread:" + e); 
	        }
	       }
	  
	  }

	 

