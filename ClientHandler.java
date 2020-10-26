package BasicServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

/**
 * Hands the clients sending and receiving messages in the chat room
 *
 * @author Aidan Wesloskie
 */

public class ClientHandler implements Runnable
{

    private Socket client;
    private Scanner in;
    private PrintWriter out;
    private Map<String, ClientHandler> clients;
    private String username;

    /**
     * Implicit constructor for the ClientHandler class
     * @param clientSocket user that is connected to the server
     * @param clients List of all users connected to the server and their username
     * @throws IOException if a user leaves it catches the error
     */
    public ClientHandler(Socket clientSocket, Map<String, ClientHandler> clients) throws IOException
    {
        this.client = clientSocket;
        this.clients = clients;
        in = new Scanner(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);
    }

    @Override
    public void run()
    {
        try
        {

            //Prompts the client for a username
           out.println("Welcome lad, enter thy name!");

           //Sets the username for the client and adds it to the map
           username = in.nextLine();
           clients.put(username, this);

           //Announces that a user has joined the server
           shout(username + " has joined the chat room!");

           //Shows all users online
           displayUsers();
           String previous = "";
            while (true)
            {
                String command = in.nextLine();

                //Blocks spam and really long messages
                if(command.toLowerCase().equals(previous.toLowerCase()) || command.length() > 250){
                    out.println("ERROR: Invalid input");
                }
                //Blocks empty or null strings
                else if (command.equals("") || command.equals(" ")){
                    out.println("ERROR: No input");
                    previous = command;
                }
                else {
                    //Block of code for USER COMMANDS

                    // If the user wants to private message
                    if (command.startsWith("/msg")) { message(command); }
                    //List the users
                    else if (command.startsWith("/list")) { displayUsers(); }
                    //Shows a list of commands
                    else if (command.startsWith("/help")) { help(); }
                    //Shows a list of emojis
                    else if (command.startsWith("/copypasta")) { copypasta(); }
                    //Renames the user
                    else if (command.startsWith("/rename")) { rename(command.substring(command.indexOf(" ") + 1, command.length())); }
                    //messages all users
                    else { shout(username, command); }
                    }
                    previous = command;
            }
        } finally {
            //When a user leaves it is announced and removes them from the list
            shout(username + " has left!");
            clients.remove(username, this);
            out.close();
            in.close();
        }
    }

    /**
     * Shows all users in the chat
     */
    private void displayUsers()
    {
        Collection<String> users = clients.keySet();
        out.println("Connected users: " + users);
    }

    /**
     * Allows one client to private message another client
     * @param receiver Client who the message is being sent to
     * @param message What the receiver is being sent
     */
    private void whisper(String receiver, String message)
    {
        ClientHandler sent = clients.get(receiver);
        sent.out.println("\"" + this.username + " says" + message + "\"");
    }

    /**
     * Sends a message to all users in the chat room
     * @param username Name of the user talking
     * @param message Message the user wants to say
     */
    private void shout(String username, String message)
    {
        Collection<ClientHandler> sockets = clients.values();

        for(ClientHandler aClient : sockets)
        {
            aClient.out.println(username + ": "  + message);
        }
    }

    /**
     * Another constructor that announces actions done on the server
     * i.e A user connecting or disconnecting
     * @param message What the announcement is
     */
    private void shout(String message)
    {

        Collection<ClientHandler> sockets = clients.values();

        for(ClientHandler aClient : sockets)
        {
            aClient.out.println(message);
        }
    }

    /**
     * Lists copy pasta emojis
     */
    private void copypasta(){
        String pasta = "( ͡° ͜ʖ ͡°)\n¯\\_(ツ)_/¯\n(ง ͠° ͟ل͜ ͡°)ง\nಠ_ಠ\n⚆ _ ⚆\n( ͡° ͜ʖ ͡ -)\n( ͡°╭͜ʖ╮͡° )\n(ง ͡° ͜ʖ ͡°)=/̵͇̿̿/'̿'̿̿̿ ̿ ̿̿\nᕦ( ͡°╭͜ʖ╮͡° )ᕤ\n";
        out.println(pasta);
    }

    /**
     * Lists help commands
     */
    private void help(){
        String help = "Here is a list of all the current commands in the room: \n\n " +
                "• /help: list of all commands \n " +
                "• /list: shows all current connected users \n " +
                "• private message: lets you message another client on the server\n" +
                "  Example:  /msg RECEIVER \"message to be sent\" " +
                "• /copypasta for emojis";
        out.println(help);
    }

    /**
     * Used to privately message any user
     * @param input what is being said
     */
    private void message(String input){
        //String after /msg is pulled
        String received = input.substring(input.indexOf(" ") + 1, input.length());
        //Finds the username
        String receiver = received.substring(0, input.indexOf(" ") + 1);

        if (clients.containsKey(receiver))
        {
            String message = received.substring(received.indexOf(" "), received.length());
            whisper(receiver, message);
        }
    }

    /**
     * Renames the user
     */
    private void rename(String newName){
        shout(username + " has changed their name to " + newName);
        clients.remove(username, this);
        clients.put(newName, this);
        this.username = newName;
    }
}
