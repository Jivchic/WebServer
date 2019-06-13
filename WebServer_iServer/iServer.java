import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class iServer {

    private ServerSocket serverSocket;
    private boolean flagSocket;
    private final Object object = new Object();

    public iServer(int port) {
	try {
	    serverSocket = new ServerSocket(port);

	} catch(IOException ioe) {
		System.out.println(ioe);

	}
	flagSocket = true;
    }

    public void runServer() {
	while(flagSocket) {
	    try {
		synchronized(object) {
		   Socket clientSocket = serverSocket.accept();
		   WebClient webClient = new WebClient(clientSocket);
		   webClient.start();
		}
	    } catch(IOException ioe) {
		System.out.println(ioe);
	    }
	}
    }
}