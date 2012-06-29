import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


// -------------------------------------------------------------------------
/**
 *  This class contains the primary GUI panel that the program uses.
 *
 * @author Allen Preville (a892186), John Eckstein (jeckste),
 * @author  Jonas Weigert (jweigert)
 * @version Apr 20, 2010
 */

public class ChatPanel extends JPanel
{
    private JPanel controls;
    private JLabel header;
    private JButton clear;
    private JButton next;
    private JButton users;
    private JButton send;
    private JButton test;
    private JTextField enterText;
    private JTextArea displayText;
    private Client client;
    private String name;
    private static ChatPanel instance = null;
    private String pname = null;
    /**
     * Creates the primary panel for the program.
     * @throws IOException
     * @throws UnknownHostException
     */
    private ChatPanel() throws UnknownHostException
    {
        client = new Client();
        client.start();
        setPreferredSize(new Dimension(580, 490));
        setBackground(Color.WHITE);
        Listener listener = new Listener();
        ButtonListener buttonListner = new ButtonListener();

        controls = new JPanel();
        controls.setBounds(0, 0, 550, 36);
        controls.setName( "controls" );
        Color color = new Color(0x3399FF);
        controls.setBackground(Color.WHITE);

        // HEADER
        // FONT FOR THE HEADER
        String fontFile = "fonts/header.ttf";
        Font font = null;
        try {
            InputStream is = ChatPanel.class.getResourceAsStream(fontFile);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        }
        catch (Exception ex)
        {
            font = new Font("sans-serif", Font.PLAIN, 35);
        }

        header = new JLabel();
        header.setFont(font);
        header.setForeground(color);
        header.setName( "header" );
        header.setPreferredSize(new Dimension(238, 30));
        header.setText( "Kutana" );

        ImageIcon infoImage = new ImageIcon("./images/info.png");
        users = new JButton("Stats", infoImage);
        users.addActionListener( buttonListner);
        users.setName( "users" );

        // clear button
        ImageIcon clearImage = new ImageIcon("./images/clear.png");
        clear = new JButton("  Clear", clearImage);
        clear.setName( "clear" );
        clear.addActionListener( buttonListner );

        ImageIcon nextImage = new ImageIcon("./images/next.png");
        next = new JButton("  Next", nextImage);
        next.setName( "next" );
        next.addActionListener( buttonListner );

        controls.add( header );
        controls.add( users );
        controls.add( clear );
        controls.add( next );
        add ( controls, BorderLayout.PAGE_START );

        displayText = new JTextArea(25, 50);
        displayText.setLineWrap(true);
        displayText.setName( "displayText" );
        displayText.setEditable( false );
        displayText.setText( "What is your name?" );
        add( displayText, BorderLayout.CENTER);

        JScrollPane scrollingResult = new JScrollPane(displayText);
        scrollingResult.setAutoscrolls(true);
        add(scrollingResult);

        // TEXTFIELD
        enterText = new JTextField(37);
        Font newTextFieldFont = new Font(enterText.getFont().getName(),
            enterText.getFont().getStyle(), 17);
        enterText.setFont(newTextFieldFont);
        enterText.setName( "enterText" );
        enterText.addActionListener( listener );
        add( enterText, BorderLayout.PAGE_END);

        // SEND BUTTON
        ImageIcon sendImage = new ImageIcon("./images/send.png");
        send = new JButton(sendImage);
        send.setName( "send" );
        send.addActionListener( listener );
        add( send, BorderLayout.PAGE_END);

        test = new JButton("Test");
        test.setName( "test" );
    }
    /**
     * Creates a single instance of the chatPanel.
     * INTER-PROGRAM COMMUNICATION
     * @return the single instance
     */
    public static ChatPanel getInstance()
    {
        if ( instance == null )
        {
            try
            {
                instance = new ChatPanel();
            }
            catch ( UnknownHostException e )
            {
                System.exit(1);
            }

        }
        return instance;
    }
    /**
     * Add text to the chat panel.
     * INTER-PROGRAM COMMUNICATION
     * @param message String to add to panel
     */
    public void addToPanel(String message)
    {
        if ( getPname() != null )
        {
            displayText.append( getPname() + ": " + message + "\n" );
        }
        else
        {
            displayText.append( message + "\n" );
        }
    }

    /**
     * Inner listener class.
     */
    private class Listener implements ActionListener
    {

        /**
         * Activates when enter is pressed in the text field,
         *  or the submit button is clicked.
         *  INTER-PROGRAM COMMUNICATION
         *  @param arg0 What triggers the event.
         */
        public void actionPerformed( ActionEvent arg0 )
        {

            String text = enterText.getText();
            if ( text != null && text.length() > 0)
            {
                if (name == null)
                {
                    name = text;
                    displayText.setText("");
                    client.sendToServer( "$sname$" + text );
                    enterText.setText("");
                }
                else
                {
                    displayText.append(name + ": " + text + "\n" );
                    client.sendToServer( "$smessage$" + text );
                    displayText.setCaretPosition(displayText
                        .getDocument().getLength());
                    enterText.setText("");
                }
            }
        }

    }
    /**
     * Inner Button Listener class.
     * INTER-PROGRAM COMMUNICATION
     * @author Allen Preville (a892186), John Eckstein (jeckste), Jonas Weigert
     *
     */
    private class ButtonListener implements ActionListener
    {

        /**
         * Activates when enter is pressed in the text field,
         *  or the submit button is clicked.
         *  @param arg0 What triggers the event.
         */
        public void actionPerformed( ActionEvent arg0 )
        {
            if ( arg0.getSource() == clear)
            {
                displayText.setText("");
            }
            else if (name != null && arg0.getSource() == users)
            {
                client.sendToServer( "$getstat$" );
            }
            else if (name != null && arg0.getSource() == next)
            {
                displayText.setText("");
                setPname(null);
                client.sendToServer( "$pfind$" );
            }

        }

    }

    // ----------------------------------------------------------
    /**
     * Sets the partners name.
     * INTER-PROGRAM COMMUNICATION
     * @param pn the Name of partner
     */
    public void setPname(String pn)
    {
        pname = pn;
    }
    /**
     * Gets the partners name.
     * INTER-PROGRAM COMMUNICATION
     * @return name of partner
     */
    public String getPname()
    {
        return pname;
    }
    /**
     * Gets the clients name.
     * @return The clients name which they entered at the start.
     */
    public String getName()
    {
        return name;
    }
    /**
     * Displays the current stats from the server.
     * INTER-PROGRAM COMMUNICATION
     * @param stats The string if statistics.
     */
    public void statDisplay(String stats)
    {
        JOptionPane.showMessageDialog(displayText, stats);
    }
}
