import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
		HyperStringFSA2 hypString = new HyperStringFSA2(words, nGramWrapper);

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
		int nGramLength = 4;
		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equals("n-gram")) {
				nGramLength = Integer.parseInt(args[i + 1]);
			}
		}
		PunctuationPredicter pI = new PunctuationPredicter(nGramLength, "ppCorpus.txt");
		System.out.println("Ready for prediction");
		pI.handleInput(nGramLength);
	}
}