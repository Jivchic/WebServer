import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

public class WebClient extends Thread {

    private Socket socketWebClient;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedOutputStream dataOut;
    private String fileRequested;
    public static final File WEB_ROOT = new File(".");

    public WebClient(Socket socketWebClient) {
	this.socketWebClient = socketWebClient;
    }

    public void run() {
	try{
	    InputStreamReader inputStreamReader = new InputStreamReader(socketWebClient.getInputStream());
	    in = new BufferedReader(inputStreamReader);
	    OutputStream outputStream = socketWebClient.getOutputStream();
	    out = new PrintWriter(outputStream, true);
	    dataOut = new BufferedOutputStream(socketWebClient.getOutputStream());
	    answerToWebClient();
	} catch(IOException ioe) {
		System.out.println(ioe);

	} finally {
		try {
			in.close();
			out.close();
			dataOut.close();
			socketWebClient.close();
		} catch (IOException ioe) {
			System.out.println("Error closing stream : " + ioe);
		} 

	}
    }

    private synchronized void answerToWebClient() {

	try {

	String inputLine = in.readLine();

	if(inputLine == null) {
		return;

	}

	StringTokenizer parse = new StringTokenizer(inputLine);


	String method = parse.nextToken().toUpperCase();
	fileRequested = parse.nextToken().toLowerCase();


	if(method.equals("GET") || method.equals("HEAD")) {
		readAll();
		doGet(fileRequested, method);
	    return;
	}


	if(method.equals("POST")) {
		doPost(fileRequested, inputLine);
	    return;
       	}


	if(!method.equals("GET") && !method.equals("HEAD")) {
		File file = new File(WEB_ROOT, "not_supported.html");
		int fileLength = (int) file.length();
		String contentMimeType = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		out.println("HTTP/1.1 501 Not Implemented");
		out.println("Server: Java HTTP Server from iServer : 1.0");
		out.println("Content-type: " + contentMimeType);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	}


	} catch (FileNotFoundException fnfe) {
		try {
			fileNotFound(out, dataOut, fileRequested);
		} catch (IOException ioe) {
			System.err.println("Error with file not found exception : " + ioe.getMessage());
		}

	} catch(IOException ioe) {
		System.out.println(ioe);
	}
    }

    private String getContentType(String fileRequested) {
	if (fileRequested.endsWith(".html") || fileRequested.endsWith(".htm")) {
		return "text/html";
	} else if(fileRequested.endsWith(".css")){
		return "text/css";
	} else if(fileRequested.endsWith(".js")){
		return "text/javascript";
	} else if(fileRequested.endsWith(".jpg")){
		return "image/jpeg";
	} else if(fileRequested.endsWith(".png")){
		return "image/png";
	} else if(fileRequested.endsWith(".gif")){
		return "image/gif";
	} else if(fileRequested.endsWith(".ico")){
		return "image/webp";
	} else {
		return "text/plain";
	}
    }

    private byte[] readFileData(File file, int fileLength) throws FileNotFoundException, IOException{
	FileInputStream fileInputStream = null;
	byte[] fileData = new byte[fileLength];
	try {
		fileInputStream = new FileInputStream(file);
		fileInputStream.read(fileData);

	} finally {
		try {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		} catch(IOException ioe) {
		    System.out.println(ioe);
		}

	}
	return fileData;
    }

    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
	File file = new File(WEB_ROOT, "404.html");
	int fileLength = (int) file.length();
	String content = "text/html";
	byte[] fileData = readFileData(file, fileLength);
	out.println("HTTP/1.1 404 File Not Found");
	out.println("Server: Java HTTP Server from iServer : 1.0");
	out.println("Content-type: " + content);
	out.println("Content-length: " + fileLength);
	out.println();
	out.flush();
	dataOut.write(fileData, 0, fileLength);
	dataOut.flush();
    }



    private void doGet(String fileRequested, String method) throws FileNotFoundException, IOException {

	    if(getEndOfFile(fileRequested)) {
		fileRequested += "";
	    } else {
		fileRequested += "/";

	    }

System.out.println(fileRequested);


	    if(fileRequested.equals("/show_employees") || fileRequested.equals("/show_employees/")) {
		SendToClient.showEmployees(out);
		return;
	    }



	    if(fileRequested.endsWith("/")) {
		fileRequested += "index.html";
	    }

	    File file = new File(WEB_ROOT, fileRequested);
	    int fileLength = (int)file.length();
	    String content = getContentType(fileRequested);

	    if(method.equals("GET")) {
		byte[] fileData = readFileData(file, fileLength);
		out.println("HTTP/1.1 200 OK");
		out.println("Server: Java HTTP Server from iServer : 1.0");
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	    }
    }

    private synchronized void readAll() throws IOException{
	while(in.ready()) {
		String inputLine = in.readLine();
	}

    }

    private int getArraySizeContentLength() throws IOException {
	int arraySize = 0;
	while(in.ready()) {
		String inputLine = in.readLine();
		if(inputLine.equals("")) {
			break;
		}
		StringTokenizer parse = new StringTokenizer(inputLine);
		String method = parse.nextToken().toUpperCase();
		String value = parse.nextToken().toLowerCase();

		if(method.equals("CONTENT-LENGTH:")) {
			arraySize = Integer.parseInt(value);
		}
	}
	return arraySize;
    }


    private void doPost(String request, String input) {
	try {
		int size = getArraySizeContentLength();

		String dataPost = readPostElements(size);


//System.out.println(dataPost);
//System.out.println(request);

		if(request.equals("/adddatatodatabase") || request.equals("/developer/adddatatodatabase")) {
			SendToClient.addDatatoDatabase(out, dataPost);
		}




	} catch(IOException ioe) {
	    System.out.println(ioe);
	}
    }


    private String readPostElements(int size) throws IOException {
	String dataPost = "";
	for(int i = 0; i < size; i++) {
	       	byte byteOfSymbol = (byte)in.read();
		char symbol = (char)byteOfSymbol;
        	if(symbol == '+') {
			dataPost = dataPost + " ";
			continue;
		}
        	if(symbol != '%') {
			dataPost = dataPost + symbol;
		}
		if(symbol == '%') {
			char a = (char)in.read();
			if(a == 'D') {
				char b = (char)in.read();
				byte x = (byte)in.read();
				char c = (char)in.read();
				char d = (char)in.read();
				String hexString = "" + a + b + c + d;
				dataPost = dataPost + hexToString(hexString);
				i = i + 5;
			} else {
				char b = (char)in.read();
				String hexString = "" + a + b;
				dataPost = dataPost + hexToString(hexString);
				i = i + 2;
			}
		}
	}
//	dataPost = dataPost + "&";
	return dataPost;


    }
    private String hexToString(String hexString) {
	try {
		byte[] bytes = javax.xml.bind.DatatypeConverter.parseHexBinary(hexString);
		String result = new String(bytes, "UTF8");
		return result;
	} catch(java.io.UnsupportedEncodingException e) {
                System.out.println(e);
	}
	return "";
    }



    private boolean getEndOfFile(String fileRequested) {

	if(
		fileRequested.endsWith("/")
		||
		fileRequested.endsWith(".css")
		||
		fileRequested.endsWith(".css")
		||
		fileRequested.endsWith(".js")
		||
		fileRequested.endsWith(".html")
		||
		fileRequested.endsWith(".htm")
		||
		fileRequested.endsWith(".jpg")
		||
		fileRequested.endsWith(".jpeg")
		||
		fileRequested.endsWith(".png")
		||
		fileRequested.endsWith(".gif")
	) {
		return true;

	} else {
		return false;
	}

    }
}