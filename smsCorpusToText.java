import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This class opens a file by a statically set filename in the current working directory.
 * One line at a time it extracts the text from the file and saves it to corpus.txt
 * @author Jasmin Suljkic
 * @author Jonatan Asketorp
 */
public class smsCorpusToText {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("smsCorpus_en_2014.09.06_all.xml"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("corpus.txt"));
            String temp;
            while((temp=br.readLine())!=null) {
                String[] text = temp.split("(<text>)|(</text>)");
                if(text.length>1) {
                    bw.write(text[1]);
                    bw.newLine();
                }
            }
            br.close();
            bw.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}