import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *  Client object used for each user.
 *  This entire class uses INTER-PROGRAM COMMUNICATION.
 *  @author Allen Preville (a892186), John Eckstein (jeckste),
 *  @author  Jonas Weigert (jweigert)
 *  @version May 4, 2010
 */
public class Client extends Thread
{

    String inputLine;
    String outputLine;
    BufferedReader in = null;
    PrintWriter out = new PrintWriter(System.out , true ); //Initialized just so
    Socket outSocket = null;    //'out' would not be null for tests.
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    /**
     * Constructor to setup socket connection to server.
     * @throws UnknownHostException
     * @throws IOException
     */
    public Client()
    {

        try
        {
            serverSocket = new ServerSocket(27001);
            outSocket = new Socket("172.31.180.193", 27000);
        }
        catch ( UnknownHostException e )
        {
            //
        }
        catch ( IOException e )
        {
            //
        }
    }

    /**
     * Run method for thread.  Will listen for information
     * from the server.
     */
    public void run()
    {
        //Setup
        try
        {
            clientSocket = serverSocket.accept();

            in = new BufferedReader(
                new InputStreamReader(
                    clientSocket.getInputStream()));
            out = new PrintWriter(outSocket.getOutputStream(), true);
            //Listen for input from server.
            while ((inputLine = in.readLine()) != null) {
                outputLine = inputLine;
                translate( outputLine );
            }
        }
        catch ( IOException e )
        {
            System.err.println("Could not listen on port: 27001.");
            System.exit(-1);
        }
    }
    /**
     * Will translate all output from server to client into
     * an action or display the text.
     * @param message text from server
     */
    private void translate(String message)
    {
        if ( message.charAt( 0 ) == '$' )
        {
            int count = 0;
            int commandSize = 0;
            for ( int i = 0; i <= message.length(); i++)
            {
                if ( message.charAt( i ) == '$')
                {
                    count++;
                    if ( count == 2 )
                    {
                        commandSize = i;
                        break;
                    }
                }
            }
            String command = message.substring( 1, commandSize );
            String smessage = message.substring( commandSize + 1 );

            if ( command.contains("pname"))
            {
                ChatPanel.getInstance().setPname( smessage );
                sendToServer("$pnamer$" +
                    ChatPanel.getInstance().getName());
            }
            else if ( command.contains("pnamer"))
            {
                ChatPanel.getInstance().setPname( smessage );
            }
            else if ( command.contains("pmessage"))
            {
                ChatPanel.getInstance().addToPanel( smessage );
            }
            else if ( command.contains( "pnotfound" ) )
            {
                ChatPanel.getInstance().addToPanel("Could not find a partner!");
            }
            else if ( command.contains( "ferror" ))
            {
                ChatPanel.getInstance().addToPanel( "Search error" );
            }
            else if ( command.contains( "uavailable" ))
            {
                ChatPanel.getInstance().addToPanel( "You are available" );
            }
            else if ( command.contains( "pfound" ) )
            {
                ChatPanel.getInstance().addToPanel( "You have a partner!" );
            }
            else if ( command.contains( "pneed" ) )
            {
                ChatPanel.getInstance().setPname(null);
                ChatPanel.getInstance().addToPanel( "You need a partner" );
            }
            else if ( command.contains("stat") )
            {
                ChatPanel.getInstance().statDisplay(smessage);
            }

        }
        else
        {
            ChatPanel.getInstance().addToPanel( message );
        }
    }

    // ----------------------------------------------------------
    /**
     * Sends message to server.
     * @param message Message to be sent to server.
     */
    public void sendToServer(String message)
    {
        out.println( message );
    }
}
