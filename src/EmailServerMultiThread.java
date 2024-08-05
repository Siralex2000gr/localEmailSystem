import java.io.*;
import java.net.Socket;

/** Intermediate Class between Client and Server where every client connected with the Server can
 *  have access to the server database. This can happen because each client has their own thread.
 *  The thread begins when the class EmailServer "listens" to the port of a client and makes
 *  the connection immediately.
 *  When in the threading part (inside the overridden run function), similarly to the Class EmailClient
 *  we have our input and output streams for the connection between the server and the client and then
 *  the main loop where the exchange of the data from the server and the client happens, but now we have
 *  a direct communication to the server and the client, as well. Before we enter the main loop, we give the "default"
 *  input for the server to process in order to give as output the introductory message. The communication with
 *  the client happens the same way it is done in the EmailClient class. The input data from the client comes with the
 *  function readUTF, and the output data is sent to the client with the function writeUTF(data). The difference is how
 *  the output is made, where here is processed directly from the communication with the database of the server. This is
 *  done with the function processingInput, where the input from the client is sent, and then from its part,
 *  the server (through the class Protocol) gives each time the appropriate output. Again, similarly with the
 *  EmailClient class, the loop ends when the client chooses the appropriate option. After that, the client
 *  disconnects, so the Streams close as well.
 *
 */
public class EmailServerMultiThread extends Thread{

    private Socket socket;

    public EmailServerMultiThread(Socket socket) {
        super("EmailServerMultiThread");
        this.socket = socket;
    }

    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String input="0", output;
            Protocol emailpr = new Protocol();
            output = emailpr.processingInput(input);
            out.writeUTF(output);
            while (!output.endsWith("GoodBye!\n")) {
                input = in.readUTF();
                synchronized (this) { //in order for the actions not to be interrupted and the database to be messed up
                    output = emailpr.processingInput(input);
                    out.writeUTF(output);
                }
            }
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("I/O problem occurred\n");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
