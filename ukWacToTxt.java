import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This class opens a file whose absolute path is specified in args[0].
 * One line at a time it extracts the text from the file and saves it to corpus.txt in the current working directory.
 * @author Jasmin Suljkic
 * @author Jonatan Asketorp
 */
public class ukWacToTxt {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            BufferedWriter bw = new BufferedWriter(new FileWriter("corpus.txt"));
            String temp;
            StringBuffer sb = new StringBuffer();
            
            while((temp=br.readLine())!=null) {
            	if(temp.startsWith("<s>")){
            		sb = new StringBuffer();
            	}
            	else if(temp.startsWith("</s>")){
            		bw.write(sb.toString().toLowerCase().trim());
                    bw.newLine();
            	}
            	else{
                    String[] text = temp.split("\\t");
                    	sb.append(text[0]);//ignoring the rest which is tags...
                        sb.append(" ");
            	}
            }
            br.close();
            bw.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}