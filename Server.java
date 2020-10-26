package BasicServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server for the chat room clients to connect to via IP and Port Number
 *
 * @author Aidan Wesloskie
 */

public class Server
{

    //Creates a place a store users and their names along with the amount of threads a server can hold
    private static final int PORT = 9090;
    private static Map<String, ClientHandler> clients = new HashMap<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(40);

    public static void main(String[] args) throws IOException
    {

        //Creates the server socket to start the server
        ServerSocket listener = new ServerSocket(PORT);

        while (true)
        {
            System.out.println("[SERVER] Waiting for connection . . . ");
            //takes in the user client sockets
            Socket client = listener.accept();
            //Announces that the user has connected
            System.out.println("[SERVER] Connected to client " + client.getInetAddress());
            ClientHandler clientThread = new ClientHandler(client, clients);

            pool.execute(clientThread);
        }
    }
}
