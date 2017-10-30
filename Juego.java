
	import java.io.BufferedReader;

	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.PrintStream;
	import java.net.Socket;
import java.util.Scanner;

public class Juego {



		private Socket jSocket;
		String text;
		boolean exit;
		
		Juego(Socket socket){

				jSocket = socket;
				exit = false;
				
				}
		
		
		public void Juego(){
			
			String line=null;
			Scanner sc =new Scanner(System.in);
			while(true)
			{
				try
				{
					BufferedReader socIn = null;
		    		socIn = new BufferedReader(new InputStreamReader(jSocket.getInputStream()));  
		    		System.out.println("ecrire message");
		    		PrintStream socOut = new PrintStream(jSocket.getOutputStream());;

		    		text= sc.nextLine();
		    				
		    			
		    				
		    				socOut.println(text);
		    				text = socIn.readLine();
		    		System.out.println(text);
				}
	    		catch (Exception ep) 
	    		{
		            System.err.println("Error in ClientChat:" + ep);
		        }
			}
		
		
			
			 
	}
}
