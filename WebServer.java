

import java.net.ServerSocket;
import java.io.IOException;

public class WebServer {

	public WebServer() {
		
		try {
			while(true) {
				ServerSocket serverSocket = new ServerSocket(80);

				Socket webClientSocket = serverSocket.accept();

			}


		} catch(IOException e) {
			System.out.println(e);
		}		


	}

	public void startServer() {


	}

}