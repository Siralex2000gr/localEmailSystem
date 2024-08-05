import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/** Client-side Class where the client connects with the server
 * This class is based on the equivalent client class from the Knock Knock exercise with the necessary adjustments
 * made.
 * When the program starts, it asks the client to give the host of the server and then its port
 * Then, the Client connects (through the intermediate Class) with the server and the two-way communication happens.
 * The one way is the input the client gives to the server from the variable input (DataInputStream).
 * The other is the output that the server delivers to the client from the variable output (DataOutputStream).
 * Each time information needs to be passed from one side to the other, it is made possible
 * with the function readUTF to pass information from server to client, and writeUTF(data) to
 * pass information from client to server.
 * Each information (input and output) is stored in a String variable named appropriately (fromServer is the info the
 * server delivers and fromClient is the info the Client transfers to Server)
 * This communication is happening in the main loop which ends when the client decides to exit the
 * program from the choices given to them. After each input from the client, which is given from the keyboard,
 * with the help of the function writeUTF(data), the information passes on the server to the EmailServerMultiThread
 * class, which then processes the input and gives the appropriate output to fromServer through the function readUTF.
 * When the loop stops, the final message is shown and the Streams and socket finally close.
 */
public class EmailClient {

    public static void main(String[] args) throws IOException {

        DataInputStream input= null;
        DataOutputStream output = null;
        Socket emailSocket=null;
        BufferedReader keyboardIn = new BufferedReader(new InputStreamReader(System.in));
        try {//host name is given in 1st argument and port of server in 2nd argument
            emailSocket = new Socket(args[0], Integer.parseInt(args[1]));
            //emailSocket = new Socket("127.0.0.1", 4444);
            input = new DataInputStream(emailSocket.getInputStream());
            output = new DataOutputStream(emailSocket.getOutputStream());
        } catch (NumberFormatException | UnknownHostException e) {
            System.err.println("Unknown host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO problem occurred input client");
            e.printStackTrace();
            System.exit(1);
        }
        String fromServer=input.readUTF();
        String fromUser;
        while (!(fromServer.endsWith("GoodBye!\n"))) {
            System.out.println(fromServer);
            fromUser = keyboardIn.readLine();
            output.writeUTF(fromUser);
            fromServer= input.readUTF();
        }
        System.out.println(fromServer);
        keyboardIn.close();
        emailSocket.close();
        input.close();
        output.close();
    }
}