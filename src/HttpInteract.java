/**
 * ***********************************
 * Filename: HTTPInteract.java 
 * Names: Haoxuan WANG,Yuan GAO 
 * Student-IDs:201219597, 201218960 
 * Date: 21/Oct/2016 . 
 * ***********************************
 */
import java.awt.TextField;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Class for downloading one object from http server.
 *
 */
public class HttpInteract {

    private String host;
    private String path;
    private String requestMessage;

    private static final int HTTP_PORT = 80;
    private static final String CRLF = "\r\n";
    private static final int BUF_SIZE = 4096;
    private static final int MAX_OBJECT_SIZE = 102400;

    private TextField tf;

    /*
     * [Alter] Our send() method now support redirection, notice that it do not work if the intended address
     * want to jump to an address with HTTPS protocol 
     */
    public String send() throws IOException {

        /* buffer to read object in 4kB chunks */
        char[] buf = new char[BUF_SIZE];

        /* Maximum size of object is 100kB, which should be enough for most objects. 
         * Change constant if you need more. */
        char[] body = new char[MAX_OBJECT_SIZE];

        String statusLine = "";	// status line
        int status;		// status code
        String headers = "";	// headers
        int bodyLength = -1;	// lenghth of body

        String[] tmp;

        /* The socket to the server */
        Socket connection;

        /* Streams for reading from and writing to socket */
        BufferedReader fromServer;
        DataOutputStream toServer;

        System.out.println("Connecting server: " + host + CRLF);

        /* Connect to http server on port 80.
        * Assign input and output streams to connection. */
        connection = new Socket(host, HTTP_PORT);

        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());

        System.out.println("Send request:\n" + requestMessage);

        /* Send requestMessage to http server */
        toServer.writeBytes(requestMessage);
        /* Read the status line from response message */
        try {
            statusLine = fromServer.readLine();
        } catch (IOException ioe) {
            return ("Error: a failure happend when accessing the address");
        }
        System.out.println("Status Line:\n" + statusLine + CRLF);

        /* Extract status code from status line. If status code is not 200,
	 * close connection and return an error message. 
	 * Do NOT throw an exception */
        String[] temp;
        try {
            temp = statusLine.split(" ");;
        } catch (NullPointerException ne) {
            return ("Error: a failure happend when accessing the address");
        }
        /*[Add] record 301 or 302 code */
        boolean needRedirection = false;
        if ((status = Integer.parseInt(temp[1])) != 200) {
            if (status == 301 || status == 302) {  //record two redirection code
                needRedirection = true; //change flag
            } else {
                return ("Error: a failure happend when accruing the intended file");
            }
        }

        /* If object is larger than MAX_OBJECT_SIZE, close the connection and 
		 * return meaningful message. */
        if (bodyLength > MAX_OBJECT_SIZE) {
            connection.close();
            return ("Sorry! The size of this object has a size of " + bodyLength + " bits, which is too large to get.");
        }

        /* Read the body in chunks of BUF_SIZE using buf[] and copy the chunk
		 * into body[]. Stop when either we have
		 * read Content-Length bytes or when the connection is
		 * closed (when there is no Content-Length in the response). 
		 * Use one of the read() methods of BufferedReader here, NOT readLine().
		 * Also make sure not to read more than MAX_OBJECT_SIZE characters.
         */
        int bytesRead = 0;
        int remainBytes = bodyLength;
        if (bodyLength == -1) {
            return ("No body length info found");
        }
        while (bytesRead < Math.min(bodyLength, MAX_OBJECT_SIZE)) {
            int length = Math.min(remainBytes, BUF_SIZE);
            try {
                fromServer.read(buf, 0, length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.arraycopy(buf, 0, body, bytesRead, length);
            Arrays.fill(buf, '\0');
            bytesRead += BUF_SIZE;
            remainBytes -= BUF_SIZE;
        }

        /* At this points body[] should hold to body of the downloaded object and 
		 * bytesRead should hold the number of bytes read from the BufferedReader
         */
 /* Close connection and return object as String. */
        System.out.println("Done reading file. Closing connection.");
        connection.close();
        return (new String(body, 0, bytesRead));
    }
}
