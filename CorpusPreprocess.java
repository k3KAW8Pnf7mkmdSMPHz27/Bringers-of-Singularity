import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class loads the file corpus.txt, reads it line by line.
 * Each line is processed according to a set of rules, and the resuls
 * are written to ppCorpus.txt, line by line.
 * 
 * @author Jasmin Suljkic
 */
public class CorpusPreprocess {

	/**
	 * No arguments are taken in account. 
	 * Statically configured file corpus.txt is read and ppCorpus.txt is created and written to.
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
			
			//Read a line from file (as long as there are lines in the file)
			//Process the line
			//Write the result to output file.
			while ((line=br.readLine()) != null) {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
