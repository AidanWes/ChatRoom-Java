package BasicServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class for the client to connect through
 *
 * @author Aidan Wesloskie
 */

public class Client
{

    private static String serverIP;
    private static int PORT = 9090;
    public static void main(String[] args) throws IOException
    {
        //Reads user input
        Scanner keyboard = new Scanner(new InputStreamReader(System.in));
        System.out.println("Input the IP Address of the server: ");
        //Sets the IP for the client
        serverIP = keyboard.nextLine();

        //Connects the user to the server
        Socket socket = new Socket(serverIP, PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        //Runs thread to keep client messages up to date
        new Thread(new ServerConnection(socket)).start();

        while (true)
        {
            System.out.print("");
            String command = keyboard.nextLine();

            if (command.equals("quit")) break;
            out.println(command);
        }

        socket.close();
        System.exit(0);
    }
}

/**
 * Thread that keeps messages up to date so users don't
 * have to put in a message to see the next one
 */

class ServerConnection implements Runnable
{

    private Socket server;
    private Scanner in;

    /**
     * Constructor for the server connection Thread
     * @param client the user that is connected
     * @throws IOException
     */
    public ServerConnection(Socket client) throws IOException
    {
        server = client;
        in = new Scanner(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                String serverResponse = in.nextLine();

                if (serverResponse == null) break;

                System.out.println(serverResponse);
            }
        } finally
        {
            in.close();
        }
    }
}

