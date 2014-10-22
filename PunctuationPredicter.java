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

	public static final String CORPUS_TEST_PATH = "sentences.txt";
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
		HyperStringFSA2 hypString = new HyperStringFSA2(words);

		// For each combination check it's frequency
		String prediction = "";
		int maxCount = 0;
		for (String[] s : hypString.getOutputs()) {
			//System.err.println(Arrays.toString(s));
			if (nGramWrapper.exists(s)) {
				int count = nGramWrapper.counts(s);
				System.err.println("Possible string: " + Arrays.toString(s));
				System.err.println("Counts = " + count);
				
				// If this combination occurs more often, use that as prediction
				if (count > maxCount) {
					prediction = "";
					maxCount = count;
					for (String w : s) {
						prediction += w + " ";
					}
				}
				
				prediction = HyperStringFSA2.postProcessing(prediction);
			}
		}

		return prediction;
	}

	// Test method to run input from command line
	private void handleInput(int nGramLength) {
		NGramWrapper ngw = new NGramWrapper(nGramLength);
		ngw.readFile(new File(CORPUS_TEST_PATH));
		System.err.println("Corpus:");
		System.err.println("Number of sentences: " + ngw.numberOfSentences);
		System.err.println("Number of tokens: " + ngw.numberOfTokens);
		System.err
				.println("Number of grams: " + ngw.getNgram().numberOfGrams());
		// ngw.serialize((OutputStream)(new FileOutputStream("test.txt")));
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
		PunctuationPredicter pI = new PunctuationPredicter(nGramLength, "");
		pI.handleInput(nGramLength);
	}
}