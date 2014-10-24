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
		//args[0] -> Amount of lines to preprocess for learning
		//args[1] -> Amount of lines to preprocess for testing
		
		// TODO Auto-generated method stub
		BufferedReader br;
		BufferedWriter bw;
		BufferedWriter bwT;
		StringBuffer sb = new StringBuffer();
		int nrLines=0;
		int toLearn=Integer.parseInt(args[0]);
		int toTest=Integer.parseInt(args[1]);
		
		try {
			br = new BufferedReader(new FileReader("corpus.txt"));
			bw = new BufferedWriter(new FileWriter("ppCorpus.txt"));
			bwT = new BufferedWriter(new FileWriter("testSentences.txt"));
			String line;
			char[] lc;
			
			//Go trough the corpus and count the amount of lines present
			while ((line=br.readLine()) != null) {
				nrLines++;
			}
			br.close();
			
			if((toLearn+toTest)>nrLines){
				System.err.println("Request invalid: Number of lines requested > nr of lines available in corpus.");
				return;
			}
			
			br=new BufferedReader(new FileReader("corpus.txt"));
			
			//Read a line from file (as long as there are lines in the file)
			//Process the line
			//Write the result to output file.
			int current =0;
			boolean testing =false;
			while ((line=br.readLine()) != null) {
				if(current==toLearn+1){
					testing=true;
				}
				if(current==(toLearn+toTest)){
					break;
				}
				lc = line.toCharArray();
				for(char c : lc){
					if(c=='.'){
						if(testing){
							sb.append(" ");
						}
						else{
							sb.append(" .PERIOD");
						}
					}
					else if(c=='!'){
						if(testing){
							sb.append(" ");
						}
						else{
							sb.append(" !EXCL");
						}					}
					else if(c=='?'){
						if(testing){
							sb.append(" ");
						}
						else{
							sb.append(" ?QMARK");
						}
					}
					else if(c==','){
						if(testing){
							sb.append(" ");
						}
						else{
							sb.append(" ,COMMA");
						}
					}
//					else if(c==' '){
//						sb.append(' ');
//					}
					else{
						sb.append(c);
					}
				}
				if(testing){
					bwT.write(sb.toString());
					sb = new StringBuffer();
					bwT.newLine();
				}
				else{
					bw.write(sb.toString());
					sb = new StringBuffer();
					bw.newLine();
				}
				
				current++;
			}
			br.close();
			bw.close();
			bwT.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
