import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 */

/**
 * @author Jasmin Suljkic
 *
 */
public class CorpusPreprocess {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br;
		BufferedWriter bw;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader("corpus.txt"));
			bw = new BufferedWriter(new FileWriter("ppCorpus.txt"));
			String line;
			char[] lc;
			while ((line=br.readLine()) != null) {
			   // process the line.
				lc = line.toCharArray();
				for(char c : lc){
					if(c=='.'){
						sb.append(" .");
					}
					else if(c=='!'){
						sb.append(" !");
					}
					else if(c=='?'){
						sb.append(" ?");
					}
					else if(c==','){
						sb.append(" ,");
					}
					else if(c==' '){
						sb.append(' ');
						sb.append((char) 7);
						sb.append(' ');
					}
					else{
						sb.append(c);
					}
				}
				bw.write(sb.toString());
				sb = new StringBuffer();
				bw.newLine();
			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
