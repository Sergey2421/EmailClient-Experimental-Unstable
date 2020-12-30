/**
 * ***********************************
 * Filename: EmailClient.java 
 * Names: Haoxuan WANG,Yuan GAO
 * Student-IDs: 201219597, 201218960
 * Date: 21/Oct. 2016
 * ***********************************
 **/
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import java.util.ArrayList;

public class EmailClient extends Frame {

    /* The stuff for the GUI. */
    private Button btSend = new Button("Send");
    private Button btClear = new Button("Clear");
    private Button btQuit = new Button("Quit");
    private Label serverLabel = new Label("Local mailserver:");
    private TextField serverField = new TextField("smtp.mail.ru", 40);
    private Label fromLabel = new Label("From:");
    private TextField fromField = new TextField("", 40);
    private Label toLabel = new Label("To:");
    private TextField toField = new TextField("", 40);
    private Label messageLabel = new Label("Message:");
    private TextArea messageText = new TextArea(30, 80);
    /* [Add] Fields for mainText*/
    private static String contentType = MessageType.TXT.toString();  
    private static String ContentEncoding = EncodingType.ASCII_7.toString();

    public static void setContentType(String contentType) {
        EmailClient.contentType = contentType;
    }

    public static void setContentEncoding(String ContentEncoding) {
        EmailClient.ContentEncoding = ContentEncoding;
    }


    public EmailClient() {
        super("Email Client");

        /* Create panels for holding the fields. To make it look nice,
	   create an extra panel for holding all the child panels. */
        Panel serverPanel = new Panel(new BorderLayout());
        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        Panel messagePanel = new Panel(new BorderLayout());


        serverPanel.add(serverLabel, BorderLayout.WEST);
        serverPanel.add(serverField, BorderLayout.CENTER);
        fromPanel.add(fromLabel, BorderLayout.WEST);
        fromPanel.add(fromField, BorderLayout.CENTER);
        toPanel.add(toLabel, BorderLayout.WEST);
        toPanel.add(toField, BorderLayout.CENTER);
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(messageText, BorderLayout.CENTER);

        Panel fieldPanel = new Panel(new GridLayout(0, 1));
        fieldPanel.add(serverPanel);
        fieldPanel.add(fromPanel);
        fieldPanel.add(toPanel);


        /* Create a panel for the buttons and add listeners to the
	   buttons. */
        Panel buttonPanel = new Panel(new GridLayout(1, 0));
        btSend.addActionListener(new SendListener());
        btClear.addActionListener(new ClearListener());
        btQuit.addActionListener(new QuitListener());
        buttonPanel.add(btSend);
        buttonPanel.add(btClear);
        buttonPanel.add(btQuit);

        /* Add, pack, and show. */
        add(fieldPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);

        /* [Add]Verify sender address*/
        String host = "";
        try {
            host = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException ex) {
        }

        fromField.setText(host);

    }

    static public void main(String argv[]) {
        new EmailClient();
    }

    /* Handler for the Send-button. */
    class SendListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            System.out.println("Sending mail");

            /* Check that we have the local mailserver */
            if ((serverField.getText()).equals("")) {
                System.out.println("Need name of local mailserver!");
                return;
            }

            /* Check that we have the sender and recipient. */
            if ((fromField.getText()).equals("")) {
                System.out.println("Need sender!");
                return;
            }
            if ((toField.getText()).equals("")) {
                System.out.println("Need recipient!");
                return;
            }

            /* Create the message */
            EmailMessage mailMessage = null;
            try {
                mailMessage = new EmailMessage(fromField.getText(), toField.getText(), serverField.getText());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            /* Check that the message is valid, i.e., sender and
	       recipient addresses look ok. */
            if (!mailMessage.isValid()) {
                return;
            }
            try {
                System.out.println(mailMessage.getHeaders());
                SMTPConnect connection = new SMTPConnect(mailMessage);
                connection.send(mailMessage);
                connection.close();
            } catch (IOException error) {
                System.out.println("Sending failed: " + error);
                return;
            }
            System.out.println("Email sent succesfully!");
        }
    }

    /* Clear the fields on the GUI. */
    class ClearListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Clearing fields");
            fromField.setText("");
            toField.setText("");
            messageText.setText("");
            contentType = MessageType.TXT.toString(); //Set to default value
            ContentEncoding = EncodingType.ASCII_7.toString(); //Set to default value
        }
    }

    /* Quit. */
    class QuitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
    /* enum for regulate the possible encoding type */
enum EncodingType {
    BASE64("base64"), QP("quoted-printable"), ASCII_8("8BIT"), ASCII_7("7BIT"), BINARY("binary");
    private final String typeName;

    @Override
    public String toString() {
        return typeName;
    }

    private EncodingType(String typeName) {
        this.typeName = typeName;
    }
}
/* enum for regulate the possible message type */
enum MessageType {
    TXT("text/plain");
    private final String typeName;

    @Override
    public String toString() {
        return typeName;
    }

    private MessageType(String typeName) {
        this.typeName = typeName;
    }
}
