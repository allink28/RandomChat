import student.TestCase;


// -------------------------------------------------------------------------
/**
 *  Test method for the Server main method.
 *
 *  @author Allen Preville (a892186), John Eckstein (jeckste),
 *  @author  Jonas Weigert (jweigert)
 *  @version May 4, 2010
 */

public class ServerMainTest
    extends TestCase
{
    /**
     * Test method for {@link ServerMain#main(java.lang.String[])}.
     */
    public void testMain()
    {
        assertNotNull(Server.getInstance());
    }

}
