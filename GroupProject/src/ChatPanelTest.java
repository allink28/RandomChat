import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import student.GUITestCase;


// -------------------------------------------------------------------------
/**
 *  Test methods for the ChatPanel.
 *
 *  @author Allen Preville (a892186), John Eckstein (jeckste),
 *  @author  Jonas Weigert (jweigert)
 *  @version Apr 20, 2010
 */

public class ChatPanelTest
    extends GUITestCase
{

    // ----------------------------------------------------------
    /**
     * Exercises the ChatPanel.
     */
    public void testChatPanel()
    {
        Server.getInstance().start();

        ChatPanel panel = ChatPanel.getInstance();
        showInFrame(panel);

        JTextField textField = getComponent(JTextField.class, "enterText");
        JTextArea textArea = getComponent(JTextArea.class, "displayText");
        JButton users = getComponent(JButton.class, "users");
        click(users);
        JButton next = getComponent(JButton.class, "next");
        click(next);
        JButton send = getComponent(JButton.class, "send");
        enterText(textField, "Allen");
        click(send);
        click(send);
        enterText(textField, "I'm testing this!");
        click(send);

        String test = "Allen: I'm testing this!\n";
        assertTrue( textArea.getText().contains( test ));

        JButton clear = getComponent(JButton.class, "clear");
        click(clear);
        assertTrue(textArea.getText().equals( "" ));

        click(next);

        click(users);
        //click(getComponent(JButton.class, where.textIs("OK")));

    }

}
