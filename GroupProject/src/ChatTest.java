import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import student.GUITestCase;


// -------------------------------------------------------------------------
/**
 *  Test class for the main method of the Chat program.
 *
 *  @author Allen Preville (a892186), John Eckstein (jeckste),
 *  @author  Jonas Weigert (jweigert)
 *  @version May 3, 2010
 */

public class ChatTest
    extends GUITestCase
{
        // ----------------------------------------------------------
    /**
     *Test that ShapeMaker displays a panel.
     */
    public void testChat()
    {
        Chat.main( null );

        ChatPanel panel = getComponent(ChatPanel.class);
        assertNotNull(panel);

        JTextField textField = getComponent(JTextField.class, "enterText");
        assertNotNull(textField);
        JTextArea textArea = getComponent(JTextArea.class, "displayText");
        assertNotNull(textArea);
        JButton users = getComponent(JButton.class, "users");
        assertNotNull(users);
        JButton send = getComponent(JButton.class, "send");
        assertNotNull(send);
        JButton clear = getComponent(JButton.class, "clear");
        assertNotNull(clear);
        JButton next = getComponent(JButton.class, "next");
        assertNotNull(next);
    }
}

