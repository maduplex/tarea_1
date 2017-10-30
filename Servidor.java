import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

public class Servidor {

		public static Socket[] tabSocket= new Socket[100];
		public static int i;
		
		public static void main(String args[]){ 
	        ServerSocket listenSocket;
	        i=1;
	        
	        Scanner sc = new Scanner(System.in);
		try {
			BufferedReader socIn = null;
			Socket clientSocket;
			listenSocket = new ServerSocket(2000); //port
			  
			while (true) {
				System.out.println("Servidor general listo");
				clientSocket = listenSocket.accept();
				socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String Name = socIn.readLine();
		        ServidorThread ct = new ServidorThread(clientSocket,i,Name);
				i++;
				System.out.println("[Servidor Central] Dar autorizacion a"+ clientSocket.getInetAddress()+" por Distrito "+ Name +"?");
				System.out.println("1-Si");
				System.out.println("2-No");
				String ans = sc.nextLine();
				//while(true) {
					if (ans =="1") {
						ct.run();
						break;
					}
					else if (ans=="2")
					{
						ct.run();
						//ct.exit=true;
						System.out.println("à enlever");
						break;
					}
					/**System.out.println("1-Si");
					System.out.println("2-No");
					ans=sc.nextLine();
				}**/
				
			}
			/**MulticastSocket socket = new MulticastSocket(2000); 
			 //Receive request from client
			  for(int i=0; i < str.length; i++){
			  DatagramPacket packet = new DatagramPacket(buffer, buffer.length, str[i], port);
			  socket.receive(packet);
			  addresStr[i] = packet.getAddress().toString();
			  InetAddress client = packet.getAddress();
			  int client_port = packet.getPort();
			  area.append("Received : '" + new String(buffer).trim() + "' from " + addresStr[i] + "\n");
			  // send information to the client
			  String message = "your request\n ";
			  buffer = message.getBytes() ;
			  packet = new DatagramPacket(buffer, buffer.length, client, client_port);
			  socket.send(packet);
			  }**/
			
	        } catch (Exception e) {
	            System.err.println("Error in ServerChat:" + e);
	        }
	      }
	  }



