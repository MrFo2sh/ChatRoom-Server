import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
	private static ServerSocket serverSocket = null;
	 
	public static List<Client> ConnectedClients = null;
	private boolean working = true;
	
	public Server(int port) throws IOException{
		Server.serverSocket = new ServerSocket(port);
		Server.ConnectedClients = new CopyOnWriteArrayList<Client>();
		SetupServer();
		BeginRecive();
	}

	private void SetupServer() {
		System.out.println("Server On!");
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(working){
					try {
						Socket clientSocket = Server.serverSocket.accept();
						HandleClient(clientSocket);
					} catch (IOException e) {
						
						e.printStackTrace();
					}	
				}
			}
		}).start();
	}
	private void HandleClient(Socket clientSocket) {
		try {
			DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			String Username = in.readLine();
			System.out.println(Username);
			synchronized(Server.ConnectedClients){
				Server.ConnectedClients.add(new Client(Username,clientSocket));
				ServerBroadCast("User: "+Username+" has been connected.");
			}
			System.out.println(Username+"Client has been added");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void IsWorking(boolean b){
		this.working=b;
	}

	private void BeginRecive() {
		new Thread(new Runnable() {
			public void run() {
				while(working){
					synchronized(Server.ConnectedClients){
						for(Client c : Server.ConnectedClients){
							try {
								DataInputStream in = new DataInputStream(new BufferedInputStream(c.getSocket().getInputStream()));
								if(in.available()>0){
									String MSG = in.readLine();
									System.out.println(MSG);
									if(MSG.equals("!#%&")){
										Server.ConnectedClients.remove(c);
										ServerBroadCast("User: "+c.getUsername()+" has been Disconnected.");
										c.DisconnectClient();
										c=null;
									}else
									BroadCastMessage(c , MSG);
									MSG = null;
									in=null;
								}else{
									try {
										Thread.sleep(1);
									} catch (InterruptedException e1) {
									}
									continue;
								}
							} catch (IOException e) {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e1) {
								}
								continue;
							}
						}
					}
				}
			}
		}).start();
	}
	
	private void BroadCastMessage(Client sender, String MSG){
		new Thread(new Runnable() {
			public void run() {
				for(Client c : Server.ConnectedClients){
					if(!sender.getSocket().getRemoteSocketAddress().equals(c.getSocket().getRemoteSocketAddress())){
						try {
							PrintStream out = new PrintStream( c.getSocket().getOutputStream());
							out.println(sender.getUsername());
							out.flush();
							out.println(MSG);
							out.flush();
							out = null;
						} catch (IOException e) {
							if(!c.getSocket().isConnected()){
								Server.ConnectedClients.remove(c);
								c.DisconnectClient();
							}
							else{
								continue;
							}
						}
					}else{
						continue;
					}
				}
			}
		}).start();
	}
	private void ServerBroadCast(String MSG){
		new Thread(new Runnable() {
			public void run() {
				for(Client c : Server.ConnectedClients){
					try {
						PrintStream out = new PrintStream( c.getSocket().getOutputStream());
						out.println("ChatRoom");
						out.flush();
						out.println(MSG);
						out.flush();
						out = null;
					} catch (IOException e) {
						if(!c.getSocket().isConnected()){
							Server.ConnectedClients.remove(c);
							c.DisconnectClient();
						}
						else{
							continue;
						}
					}
				}
			}
		}).start();
	}
}
