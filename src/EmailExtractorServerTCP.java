import java.net.*;
import java.io.*;

public class EmailExtractorServerTCP {
    public static void main(String[] args) throws IOException {

        int portNumber = 5555; // Default port to use
        EmailExtractor extract;

        if (args.length > 0) {
            if (args.length == 1)
                portNumber = Integer.parseInt(args[0]);
            else {
                System.err.println("Usage: java EmailExtractionServerTCP [<port number>]");
                System.exit(1);
            }
        }

        System.out.println("Hi, I am an Email Extraction TCP server");

        // try() with resource makes sure that all the resources are automatically
        // closed whether there is any exception or not!!!
        try (
                // Create server socket with the given port number
                ServerSocket serverSocket =
                        new ServerSocket(portNumber);
                // create connection socket, server begins listening
                // for incoming TCP requests
                Socket connectSocket = serverSocket.accept();

                // Stream writer to the connection socket
                PrintWriter out =
                        new PrintWriter(connectSocket.getOutputStream(), true);
                // Stream reader from the connection socket
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connectSocket.getInputStream()));

                ObjectOutputStream objOutput = new ObjectOutputStream(connectSocket.getOutputStream())
        ) {
            InetAddress clientAddr = connectSocket.getInetAddress();
            int clientPort = connectSocket.getPort();
            String receivedText;
            // read from the connection socket
            while ((receivedText = in.readLine()) != null) {
                System.out.println("Client [" + clientAddr.getHostAddress() + ":" + clientPort + "] > " + receivedText);

                if (isValid(receivedText)){
                    extract = new EmailExtractor(receivedText);
                }else {
                    out.println("3: MALFORMED URL");
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + portNumber + "] > Not a valid URL! Did you use http/https?");
                    continue;
                }
                if(!extract.readContents()){
                    out.println("2: WEBSITE NOT FOUND");
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + portNumber + "] > Can't find webpage!");
                    continue;
                }
                extract.extractEmail();
                if (!extract.printAddresses()){
                    out.println("1: NO EMAILS FOUND");
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + portNumber + "] > No emails found!");
                    continue;
                }
                out.println("0: EMAILS FOUND");
                objOutput.writeObject(extract.emailAddresses);
                System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + portNumber + "] > " + extract.emailAddresses);
            }

            System.out.println("I am done, Bye!");
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }



    public static boolean isValid(String url){

        try{
            new URL(url).toURI();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
