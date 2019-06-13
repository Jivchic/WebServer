import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.APPEND;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Message {

    public synchronized static boolean parsePostText(String value, String ... parameters) {
	String[] array = new String[parameters.length];
	for(int i = 0; i < parameters.length - 1;i++) {
		int a = value.indexOf(parameters[i]);
		int b = value.indexOf(parameters[i + 1]);
		array[i] = (value.substring(a, b - 1)).trim();

		if(i == parameters.length - 2) {
			array[i + 1] = (value.substring(b, value.length())).trim();
		}
	
	}

	String message = "\n";
	for(int i = 0; i < array.length - 1;i++) {
		int a = value.indexOf(parameters[i]);
		int b = value.indexOf(parameters[i + 1]);
		array[i] = (value.substring(a + parameters[i].length() + 1, b - 1)).trim();
//		System.out.println("-------------------------------");
//		System.out.println("/" + array[i] + "/");
		message = message + array[i] + " ";
	}
	byte data[] = message.getBytes();
	Path p = Paths.get("./database/employees.iserver");
	try{
		OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, APPEND));
		out.write(data, 0, data.length);
		out.close();
	} catch (IOException ioe) {
		System.out.println(ioe);
		return false;
	}

	try {
	 	Thread.sleep(3000);
	} catch(InterruptedException ie) {
		System.out.println(ie);

	}

	return true;

    }
}