import java.io.IOException;

public class Start {

	public static void main(String[] args) {
		try {
			new Server(12345);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
