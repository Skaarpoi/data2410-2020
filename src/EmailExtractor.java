import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailExtractor {
    String pattern = "\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b";
    URL url;
    StringBuilder contents;
    Set<String> emailAddresses = new HashSet<>();

    EmailExtractor(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("Wrong url format! did you include http/https?");
            System.exit(1);
        }
    }

    public boolean readContents(){

        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
            contents = new StringBuilder();
            String input = " ";
            while((input = read.readLine()) != null) {
                contents.append(input);
            }
            return true;
        }catch (IOException ex) {
            return false;
        }

    }

    public void extractEmail(){
        Pattern pat = Pattern.compile(pattern);
        Matcher match = pat.matcher(contents);
        while(match.find()){
            emailAddresses.add((match.group()));
        }
    }

    public boolean printAddresses(){
        if (emailAddresses.size() > 0){
            System.out.println("Extracted Email Addresses: ");
            for (String emails : emailAddresses){
                System.out.println(emails);
            }
            return true;
        }else {
            System.out.println("No emails were extracted!");
            return false;
        }
    }
}
