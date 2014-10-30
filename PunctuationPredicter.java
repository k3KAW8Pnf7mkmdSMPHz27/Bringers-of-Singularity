import java.io.*;
import java.util.Arrays;

/**
 * N-gram Punctuation Prediction. Requires a corpus with the punctuation symbols
 * tokenized as , --> ,COMMA . --> .PERIOD ? --> ?QMARK
 * 
 * @author joakimlilja
 *
 */
public class PunctuationPredicter {

	public static final String CORPUS_TEST_PATH = "ppSentenses.txt";
	public static NGramWrapper nGramWrapper;

	/**
	 * Contructor
	 * 
	 * @param nGramLength
	 *            - length of n-gram
	 * @param corpusPath
	 *            - path to corpus
	 */
	public PunctuationPredicter(int nGramLength, String corpusPath) {
		nGramWrapper = new NGramWrapper(nGramLength);
		if (!corpusPath.equals("")) {
			nGramWrapper.readFile(new File(corpusPath));
		} else {
			nGramWrapper.readFile(new File(CORPUS_TEST_PATH));
		}
	}

	/**
	 * Predicts the most likely sentence with punctuation symbols inserted given
	 * the input
	 * 
	 * @param input
	 *            - string from where the prediction is to be made
	 * @return the predicted sentence
	 */
	public String predictPunctuation(String input) {
		System.err
				.println("-----------------------PREDICTION---------------------------");

		// Split into words
		String[] words = input.split(" ");

		// Generate all possible punctuation combinations
		HyperStringFSA3 hypString = new HyperStringFSA3(words, nGramWrapper);

		// For each combination get it's count (last index)
		String prediction = "";
		double maxCount = 0;
		for (String[] s : hypString.getOutputs()) {
			//System.err.println(Arrays.toString(s));
			double count = Double.parseDouble(s[s.length - 1]);
			if (count > maxCount) {
				prediction = "";
				maxCount = count;
				for (String w : s) {
					prediction += w + " ";
				}
			}
		}

		return prediction;
	}

	// Test method to run input from command line
	private void handleInput(int nGramLength) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String input = br.readLine();

			while (input != null) {
				System.out.println(predictPunctuation(input));
				input = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Test
	public static void main(String[] args) {
		int nGramLength = 3;
		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equals("n-gram")) {
				nGramLength = Integer.parseInt(args[i + 1]);
			}
		}
		PunctuationPredicter pI = new PunctuationPredicter(nGramLength, "ppCorpus.txt");
        String evaluate = "testSentences.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(evaluate));
            int counter = 6;
            while(counter>0) { //Risky?
                long time = System.currentTimeMillis();
                String fix = br.readLine().trim().replaceAll("( )+", " ");
                System.err.println("---------------------");
                System.err.println(fix);
                System.out.println(pI.predictPunctuation(fix));
                time = System.currentTimeMillis()-time;
                time = time/1000;
                System.err.println("Spent "+time+" s calculating sentence.");
                counter--;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
		//System.out.println("Ready for prediction");
		//pI.handleInput(nGramLength);
	}
}