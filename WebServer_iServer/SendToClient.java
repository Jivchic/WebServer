import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class SendToClient {


    public static void addDatatoDatabase(PrintWriter out, String dataPost) {
	out.println("HTTP/1.1 200 OK");
	out.println("Server: Java HTTP Server from iServer : 1.0");
	out.println("Content-Type: text/html\r\n");
	out.println("Your message was received.");
	out.println("<BR>");
	out.println("Processing ...");
	out.println("<BR>");
	boolean flag = Message.parsePostText(dataPost, "firstName", "lastName", "position", "phone_number", "email", "submitcontact");
	if(flag) {
		out.println("Your message was saved.");
	} else {
		out.println("Your message wasn't saved.");
	}
	out.println();
	out.flush();
    }

    public static void showEmployees(PrintWriter out) {

	out.println("HTTP/1.1 200 OK");
	out.println("Server: Java HTTP Server from iServer : 1.0");
	out.println("Content-Type: text/html\r\n");
	out.println("Show Employees.");
	out.println("<BR>");
	out.println("Processing ...");
	out.println("<BR>");


	FileReader inputStream = null;
	BufferedReader bufferedReader = null;
	try {
		inputStream = new FileReader("./database/employees.iserver");
		bufferedReader = new BufferedReader(inputStream);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
			out.println(line);
			out.println("<BR>");
		}

	} catch(IOException ioe) {
		System.out.println(ioe);
	} finally {

		try {
			bufferedReader.close();
			inputStream.close();
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}

	out.println();
	out.flush();
    }



}