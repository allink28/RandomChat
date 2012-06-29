import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This class is a singleton class.
 *
 * @author Allen Preville (a892186), John Eckstein (jeckste),
 * @author  Jonas Weigert (jweigert)
 * @version 2010.04.04
 */
class Server
    extends Thread
{

    /**
     * Map for the recieving socket.
     */
    public Map<String, ServerCThreads> clientRecieveSocket =
        new HashMap<String, ServerCThreads>();
    private Map<String, Socket>        clientSendSocket    =
        new HashMap<String, Socket>();
    /**
     * Contains the list of partners.
     */
    public Map<String, String>         partners            =
        new HashMap<String, String>();
    private HashMap<String, Integer>   active              =
        new HashMap<String, Integer>();
    /**
     * The list of available users.
     */
    public ArrayList<String>           available           =
        new ArrayList<String>();
    /**
     * The list of busy users.
     */
    public ArrayList<String>           busy                =
        new ArrayList<String>();
    private String[]                   ranked;
    private static Server instance = null;

    /**
     * Constructor for the Server class.
     */
    private Server()
    {
        super( "Server" );
        ranked = new String[3];
    }

    /**
     * Return the only instance of Server.
     *
     * @return server instance
     */
    public static Server getInstance()
    {
        if ( instance == null )
        {
            instance = new Server();
        }
        return instance;
    }

    /**
     * Will setup socket and start listening for connections.
     */
    public void run()
    {
        ServerSocket serverSocket = null;
        boolean listening = true;
        ServerCThreads temp = null;

        try
        {
            serverSocket = new ServerSocket( 27000 );
        }
        catch ( IOException e )
        {
            // Printing stack trace is not needed.
        }

        while ( listening )
        {
            Socket tempSocket = null;
            try
            {
                cleanLists();
                tempSocket = serverSocket.accept();
                temp = new ServerCThreads( tempSocket );
                temp.start();
            }
            catch ( IOException e )
            {
                // Printing stack trace is not needed.
            }

            if ( temp.isAlive() )
            {
                String clientIP = tempSocket.getInetAddress().getHostAddress();

                clientRecieveSocket.put( clientIP, temp );
                Socket clientSocket;
                try
                {
                    clientSocket = new Socket( clientIP, 27001 );
                    if ( clientSocket.isBound() )
                    {
                        clientSendSocket.put( clientIP, clientSocket );
                        active.put( clientIP, 0 );
                    }
                }
                catch ( UnknownHostException e )
                {
                    // Printing stack trace is not needed.
                }
                catch ( IOException e )
                {
                    // Printing stack trace is not needed.
                }
                if ( !available.contains( clientIP ) )
                {
                    addAvailable( clientIP );
                    if ( busy.contains( clientIP ) )
                    {
                        busy.remove( clientIP );
                    }
                }
            }
        }

    }


    /**
     * Send message to client.
     *
     * @param ip
     *            ip address of recipient of message
     * @param message
     *            message to be sent
     */
    public void sendMessage( String ip, String message )
    {
        if ( ip != null )
        {
            PrintWriter out;
            try
            {
                out =
                    new PrintWriter( clientSendSocket.get( ip )
                        .getOutputStream(), true );
                out.println( message );
            }
            catch ( IOException e )
            {
                // Print stack trace is not needed.
            }

        }
    }


    /**
     * Pairs you with a partner to chat.
     *
     * @param yourIP Your ip address.
     * @return boolean
     */
    public synchronized boolean findMeAPartner( String yourIP )
    {
        cleanLists();
        if ( !isAvailable( yourIP ) )
        {
            String partnerIP = partners.get( yourIP );
            sendMessage( yourIP, "$cclose$" );
            sendMessage( partnerIP, "$cclose$" );
            seperatePartners( yourIP );
        }
        String partnerIP = getRandomIP( yourIP );
        if ( partnerIP == null )
        {
            sendMessage( yourIP, "$ferror$" );
            return false;
        }
        partners.put( yourIP, partnerIP );
        partners.put( partnerIP, yourIP );
        makeUnavailable( yourIP );
        makeUnavailable( partnerIP );
        sendMessage( yourIP, "$pset=" + partnerIP + "$" );
        return true;
    }


    /**
     * Will connect with friend to chat.
     *
     * @param yourIP
     *            your IP address
     * @param partnerIP
     *            your friends IP address
     * @return boolean
     */
    public boolean findMyFriend( String yourIP, String partnerIP )
    {
        return false;
    }


    /**
     * Add ip addresses into the available ip ArrayList
     *
     * @param ip
     *            IP to add
     */
    private void addAvailable( String ip )
    {
        available.add( ip );
    }


    /**
     * Remove an ip address from the system.
     *
     * @param ip
     *            IP to remove
     */
    private void removeIP( String ip )
    {
        if ( clientRecieveSocket.containsKey( ip ) )
        {
            clientRecieveSocket.remove( ip );

        }
        if ( clientSendSocket.containsKey( ip ) )
        {
            clientSendSocket.remove( ip );
        }

        for ( int i = 0; i < ( available.size() - 1 ); i++ )
        {
            if ( available.get( i ).equals( ip ) )
            {
                available.remove( i );
            }
        }
        for ( int j = 0; j < ( busy.size() - 1 ); j++ )
        {
            if ( busy.get( j ).equals( ip ) )
            {
                busy.remove( j );
            }
        }

    }


    /**
     * Shuffle the available ip address in to a random order.
     */
    private void shuffle()
    {
        Collections.shuffle( available );
    }


    /**
     * Finds a random ip address in the available list.
     *
     * @param myIP
     *            your IP address
     * @return random IP address
     */
    private String getRandomIP( String myIP )
    {
        shuffle();
        if ( available.size() == 1 )
        {
            return null;
        }
        else
        {
            if ( available.get( 0 ).equals( myIP ) )
            {
                return available.get( 1 );
            }
            else
            {
                return available.get( 0 );
            }
        }
    }


    /**
     * Make an IP address unavailable.
     *
     * @param ip
     *            the IP address
     */
    private void makeUnavailable( String ip )
    {
        if ( available.contains( ip ) )
        {
            available.remove( ip );
            busy.add( ip );
        }
    }


    /**
     * Make an IP address available.
     *
     * @param ip
     */
    private void makeAvailable( String ip )
    {
        if ( busy.contains( ip ) )
        {
            busy.remove( ip );
            available.add( ip );
        }
    }


    /**
     * Checks if an IP address is available.
     *
     * @param ip
     *            the IP address to check
     * @return boolean
     */
    public boolean isAvailable( String ip )
    {
        if ( available.contains( ip ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * This will clean all Maps and Arrays of broken connections.
     */
    public synchronized void cleanLists()
    {
        ArrayList<String> removal = new ArrayList<String>();
        for ( Entry<String, ServerCThreads> s : clientRecieveSocket.entrySet() )
        {
            if ( s.getValue().getSocket().isClosed()
                || clientSendSocket.get( s.getKey() ).isClosed() )
            {
                removal.add( s.getKey() );
            }
        }
        for ( String ip : removal )
        {
            removeIP( ip );
            seperatePartners( ip );
            System.out.println( "Remove this ip:" + ip );
        }
    }


    /**
     * Remove ip address from the partners list.
     * @param myip Users ip.
     */
    public synchronized void seperatePartners( String myip )
    {
        if ( partners.containsKey( myip ) )
        {
            String partnersIP = partners.get( myip );
            partners.remove( partnersIP );
            partners.remove( myip );
            makeAvailable( partnersIP );
            makeAvailable( myip );
        }

    }


    /**
     * Get the stats for the server.
     *
     * @param ip
     *            the ip address of client
     * @return String containing stats
     */
    public String getStat( String ip )
    {
        int n = Server.getInstance().getActiveList().get( ip );
        // need to rewrite this
        String[] s = getMostActive();
        return "<html><body><br>Available: " + available.size()
            + "</br><br>Chatting: " + busy.size() + "</br><br>Your activity: "
            + n + "</br><br>Most active IP's: </br><ol><li>" + s[0]
            + "</li><li>" + s[1] + "</li><li>" + s[2]
            + "</li><ol></body></html>";

    }


    // ----------------------------------------------------------
    /**
     * Gets the active user map for statistics.
     *
     * @return map of user activity
     */
    public HashMap<String, Integer> getActiveList()
    {
        return active;
    }


    /**
     * Sorts the most active users.
     *
     * @return the array of sorted users.
     */
    private String[] getMostActive()
    {
        int first = 0;
        int second = 0;
        int third = 0;
        for ( Entry<String, Integer> s : active.entrySet() )
        {
            if ( s.getValue() > first )
            {

                if ( ranked[0] != null && ranked[0] != s.getKey() )
                {
                    if ( ranked[1] != null )
                    {
                        third = second;
                        ranked[2] = ranked[1];
                    }
                    ranked[1] = ranked[0];
                    second = first;
                }
                ranked[0] = s.getKey();
                first = s.getValue();
            }
            else if ( s.getValue() > second && ranked[1] != s.getKey() )
            {
                if ( ranked[1] != null )
                {
                    third = second;
                    ranked[2] = ranked[1];
                }
                ranked[1] = s.getKey();
                second = s.getValue();
            }
            else if ( s.getValue() > third && ranked[2] != s.getKey() )
            {
                third = s.getValue();
                ranked[2] = s.getKey();
            }
        }
        return ranked;
    }

}
