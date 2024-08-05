import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/** Server-side class where the server runs
 * This class is based on the equivalent server class from the Knock Knock exercise with the necessary adjustments
 * made
 * When it starts, the program takes the port as input from the first argument which has to be given before
 * the server runs. The same port has to be used from the Client Class in order to be connected correctly.
 * After the port is given, it enters an endless loop where it
 * listens to see if a client has tried to connect. When a connection is made, a thread from the class
 * EmailServerMultiThread starts, while it continues then the loop to listen another client. After a thread
 * starts, then it continues until the client decides to end the program from their choices.
 *
 */
public class EmailServer {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean listening=true;
        ServerSocket serverSocket = null;
        try {
            serverSocket= new ServerSocket(Integer.parseInt(args[0])); //port name is given in 1st argument
        } catch (NumberFormatException e) {
            System.err.println("Wrong port input");
            System.exit(1);
        } catch (IOException e) {
            //System.err.println("Problem occurred when tried to listen port " + port);
            System.exit(1);
        }
        while (listening) {
            new EmailServerMultiThread(serverSocket.accept()).start();
        }
        serverSocket.close();
        in.close();
    }
}
