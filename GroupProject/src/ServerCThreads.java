import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *  Individual thread for each client which handles recieved commands and
 *  messages.
 *  This entire class relies on INTER-PROGRAM COMMUNICATION.
 *
 *  @author Allen Preville (a892186), John Eckstein (jeckste),
 * @author  Jonas Weigert (jweigert)
 *  @version May 4, 2010
 */
public class ServerCThreads
    extends Thread
{

    private Socket socket  = null;
    String         message = null;
    private String partner = null;
    String         myIP    = null;
    String         myName  = null;


    /**
     * Creates the socket connection to listen to the client.
     * @param socket Socket for client.
     */
    public ServerCThreads( Socket socket )
    {
        super( "ServerCThreads" );
        this.socket = socket;
        myIP = socket.getInetAddress().getHostAddress();
    }

    /**
     * Run method for the thread.
     */
    public void run()
    {
        try
        {
            System.out.println( "run started" );
            BufferedReader in =
                new BufferedReader( new InputStreamReader(
                    socket.getInputStream() ) );

            String inputLine;

            while ( ( inputLine = in.readLine() ) != null )
            {
                String command = getCommand( inputLine );
                String mess = getMessage( inputLine );

                if ( command != null )
                {
                    if ( command.contains( "pfind" ) )
                    {
                        if ( searchAttemptConnect( null ) )
                        {
                            Server.getInstance().sendMessage( myIP, "$pfound$");
                            Server.getInstance().sendMessage(
                                Server.getInstance().partners.get( myIP ),
                                "$pname$" + getUsername() );
                        }
                        else
                        {
                            System.out.println( "Cannot find partner" );
                            Server.getInstance().sendMessage(
                                myIP, "$pnotfound$" );
                        }
                    }

                    else if ( command.contains( "fpip" ) )
                    {
                        Server.getInstance()
                            .sendMessage(
                                myIP,
                                "$pip$"
                                    + Server.getInstance().partners.get( myIP));
                    }

                    else if ( command.contains( "smessage" ) )
                    {

                        if ( Server.getInstance().partners.get( myIP ) != null )
                        {
                            int n =
                                Server.getInstance().getActiveList().get( myIP);
                            Server.getInstance()
                                .getActiveList()
                                .put( myIP, ++n );
                            System.out.println( Server.getInstance()
                                .getActiveList()
                                .get( myIP ) );
                            Server.getInstance().sendMessage(
                                Server.getInstance().partners.get( myIP ),
                                "$pmessage$" + mess );
                        }
                        else
                        {
                            System.out.println( "Active: "
                                + Server.getInstance().getActiveList().get(
                                    myIP ) );
                            Server.getInstance().sendMessage( myIP, "$pneed$" );
                        }
                    }
                    else if ( command.contains( "sname" ) )
                    {
                        setUsername( mess );
                    }
                    else if ( command.contains( "pname" ) )
                    {
                        Server.getInstance().sendMessage(
                            Server.getInstance().partners.get( myIP ),
                            "$pname$" + mess );
                    }
                    else if ( command.contains( "pnamer" ) )
                    {
                        Server.getInstance().sendMessage(
                            Server.getInstance().partners.get( myIP ),
                            "$pnamer$" + mess );
                    }
                    else if ( command.contains( "getstat" ) )
                    {
                        Server.getInstance().sendMessage(
                            myIP,
                            "$stat$" + Server.getInstance().getStat( myIP ) );
                    }
                }

            }
            in.close();
            socket.close();

        }
        catch ( IOException e )
        {
            Server.getInstance().cleanLists();
        }
    }

    /**
     * Get the ip address of the user you are chatting with.
     *
     * @return partner ip address
     */
    public String getPartnerIP()
    {
        return partner;
    }


    /**
     * Find a random partner or search for a friend to chat.
     *
     * @param ip
     *            IP address of friend
     * @return boolean
     * @throws IOException
     */
    private synchronized boolean searchAttemptConnect( String ip )
        throws IOException
    {
        // Find random connection.
        if ( ip == null )
        {
            return Server.getInstance().findMeAPartner( myIP );
        }
        // Connect to friend.
        else
        {
            return Server.getInstance().findMyFriend( myIP, ip );
        }
    }

    /**
     * Getter method for the socket.
     * @return Returns the socket.
     */
    public Socket getSocket()
    {
        return socket;
    }

    /**
     * Gets the command from a sent message.
     * @param mess The message sent.
     */
    private String getCommand( String mess )
    {
        if ( mess.charAt( 0 ) == '$' )
        {
            int count = 0;
            int commandSize = 0;
            for ( int i = 0; i <= mess.length(); i++ )
            {
                if ( mess.charAt( i ) == '$' )
                {
                    count++;
                    if ( count == 2 )
                    {
                        commandSize = i;
                        break;
                    }
                }
            }
            String command = mess.substring( 1, commandSize );
            return command;
        }
        else
        {
            return null;
        }
    }

    /**
     * Seperates the message and command portion of what a user has sent.
     * @param mess The message sent.
     */
    private String getMessage( String mess )
    {
        if ( mess.charAt( 0 ) == '$' )
        {
            int count = 0;
            int commandSize = 0;
            for ( int i = 0; i <= mess.length(); i++ )
            {
                if ( mess.charAt( i ) == '$' )
                {
                    count++;
                    if ( count == 2 )
                    {
                        commandSize = i;
                        break;
                    }
                }
            }
            return mess.substring( commandSize + 1 );
        }
        else
        {
            return null;
        }
    }


    /**
     * Sets your username.
     * @param name your name
     */
    private void setUsername( String name )
    {
        myName = name;
    }


    /**
     * Gets your username.
     * @return your username
     */
    public String getUsername()
    {
        return myName;
    }
}
