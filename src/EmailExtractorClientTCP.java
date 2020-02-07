import java.io.*;
import java.net.*;
import java.sql.Array;
import java.sql.SQLOutput;
import java.util.HashSet;
import java.util.Set;

public class EmailExtractorClientTCP
{
    public static void main(String[] args) throws IOException
    {

        String hostName = "localhost"; // Default host, localhost
        int portNumber = 5555; // Default port to use
        if (args.length > 0)
        {
            hostName = args[0];
            if (args.length > 1)
            {
                portNumber = Integer.parseInt(args[1]);
                if (args.length > 2)
                {
                    System.err.println("Usage: java EmailExtractorClientTCP [<host name>] [<port number>]");
                    System.exit(1);
                }
            }
        }


        System.out.println("Hi, I am Email Extractor client!");

        try
                (
                        // create TCP socket for the given hostName, remote port PortNumber
                        Socket clientSocket = new Socket(hostName, portNumber);

                        // Stream writer to the socket
                        PrintWriter out =
                                new PrintWriter(clientSocket.getOutputStream(), true);
                        // Stream reader from the socket
                        BufferedReader in =
                                new BufferedReader(
                                        new InputStreamReader(clientSocket.getInputStream()));
                        // Keyboard input reader
                        BufferedReader stdIn =
                                new BufferedReader(
                                        new InputStreamReader(System.in));
                        // Reads the object input stream in order to recieve the email Set
                        ObjectInputStream objIn =
                                new ObjectInputStream(clientSocket.getInputStream())

                )
        {
            String userInput;
            // Loop until null input string
            System.out.print("I (Client) [" + InetAddress.getLocalHost()  + ":" + clientSocket.getLocalPort() + "] > ");
            while ((userInput = stdIn.readLine()) != null && !userInput.isEmpty())
            {
                // write keyboard input to the socket
                out.println(userInput);

                // receives the code from the input
                String codeString = in.readLine();
                System.out.println("Server [" + hostName + ":" + portNumber + "] > " + codeString);
                int code = Integer.parseInt(codeString.substring(0,1));
                //switches between different cases depending on the recieved code from the server
                switch (code) {
                    case 0:
                        //recieves object from InputStream
                        Set<String> emailAddresses = (Set<String>) objIn.readObject();

                        //prints emails line by line
                        System.out.println("Extracted Email Addresses: ");
                        for (String emails : emailAddresses){
                            System.out.println(emails);
                        }
                        break;

                    case 1:
                        System.out.println("No emails found!");
                        break;

                    case 2:
                        System.out.println("Can't find webpage!");
                        break;

                    case 3:
                        System.out.println("Invalid URL! Did you use http/https?");
                        break;

                }
                System.out.print("I (Client) [" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "] > ");
            }
        } catch (UnknownHostException e)
        {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
