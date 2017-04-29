import java.io.IOException;
import java.net.Socket;

public class Client {
	private String Username;
	private Socket socket;
	
	public Client(String Username,Socket socket){
		this.Username = Username;
		this.socket = socket;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public void DisconnectClient(){
		try {
			this.socket.close();
			this.socket = null;
			this.Username = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
