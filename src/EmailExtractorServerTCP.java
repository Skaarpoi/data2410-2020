import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EmailExtractorServerTCP {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter default port");
        int port = Integer.parseInt(scanner.next());

        int portNumber = port; // Default port to use
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

        try (
                // Create server socket with the given port number
                ServerSocket serverSocket =
                        new ServerSocket(portNumber);
        )

        {
            String recievedText;

            while (true){
                //
                ClientService clientserver = new ClientService(serverSocket.accept());
                clientserver.start();
            }
        }catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }
    static class ClientService extends Thread{
        Socket connectSocket;
        InetAddress clientAddr;
        int serverPort, clientPort;

        public ClientService(Socket connectSocket){
            this.connectSocket = connectSocket;
            clientAddr = connectSocket.getInetAddress();
            clientPort = connectSocket.getPort();
            serverPort = connectSocket.getLocalPort();
        }

        public void run()
        {
          EmailExtractor extract;

            try (
                    // Stream writer to the connection socket
                    PrintWriter out =
                            new PrintWriter(connectSocket.getOutputStream(), true);
                    // Stream reader from the connection socket
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connectSocket.getInputStream()));

                    ObjectOutputStream objOutput =
                            new ObjectOutputStream(connectSocket.getOutputStream());
                    )
            {



            String receivedText;

            // read from the connection socket
            while ((receivedText = in.readLine()) != null) {
                System.out.println("Client [" + clientAddr.getHostAddress() + ":" + clientPort + "] > " + receivedText);

                if (isValid(receivedText)){
                    extract = new EmailExtractor(receivedText);
                }else {
                    out.println("3: MALFORMED URL");
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + serverPort + "] > Not a valid URL! Did you use http/https?");
                    continue;
                }
                if(!extract.readContents()){
                    out.println("2: WEBSITE NOT FOUND");
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + serverPort + "] > Can't find webpage!");
                    continue;
                }
                extract.extractEmail();
                if (!extract.printAddresses()){
                    out.println("1: NO EMAILS FOUND");
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + serverPort + "] > No emails found!");
                    continue;
                }
                out.println("0: EMAILS FOUND");
                objOutput.writeObject(extract.emailAddresses);
                System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + serverPort + "] > " + extract.emailAddresses);
            }

            System.out.println("I am done, Bye!");

        } catch (IOException e){
                System.out.println("Exception occured when trying to communicate with the client" + clientAddr.getHostAddress());
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
}


