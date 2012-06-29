import javax.swing.JFrame;

//-------------------------------------------------------------------------
/**
 * The main driver for the Chat project.
 *
 *@author Allen Preville (a892186), John Eckstein (jeckste),
 * @author  Jonas Weigert (jweigert)
 * @version Apr 20, 2010
 */
public class Chat
{
    // ----------------------------------------------------------
    /**
     * Sets up the primary frame and panel.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Chat");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(ChatPanel.getInstance());
        frame.pack();
        frame.setVisible(true);
    }
}
