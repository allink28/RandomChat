import java.io.IOException;
/**
 *  Starts the server.
 *  INTER-PROGRAM COMMUNICATION
 *
 *  @author Allen Preville (a892186), John Eckstein (jeckste),
 *  @author  Jonas Weigert (jweigert)
 *  @version May 4, 2010
 */
public class ServerMain
{
    // ----------------------------------------------------------
    /**
     * Sets up the primary frame and panel.
     *
     * @param args unused command-line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        Server.getInstance().start();
    }
}
