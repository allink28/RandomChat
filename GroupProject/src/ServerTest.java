
import student.TestCase;

/**
 * This class will test the server class.
 * @author Allen Preville (a892186), John Eckstein (jeckste),
 * @author  Jonas Weigert (jweigert)
 * @version Apr 20, 2010
 */
public class ServerTest extends TestCase
{

    /**
     * Setup the test class.
     */
    public void setUp() throws Exception
    {
        Chat.main(null);
    }

    /**
     * Test the getInstance method.
     */
    public void testGetInstance()
    {
        assertNotNull(Server.getInstance());
    }

}