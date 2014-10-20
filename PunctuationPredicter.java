import java.io.*;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.NEW;

import opennlp.tools.util.StringList;
import opennlp.tools.ngram.NGramModel;



/**
 * N-gram Punctuation Prediction. Requires a corpus with the punctuation symbols 
 * tokenized as , --> ,COMMA  . --> .PERIOD  ? --> ?QMARK
 * @author joakimlilja
 *
 */
public class PunctuationPredicter {
	
	public static final String CORPUS_TEST_PATH = "sentences.txt";
	public static NGramWrapper nGramWrapper;
	
	/**
	 * Contructor
	 * @param nGramLength - length of n-gram
	 * @param corpusPath - path to corpus
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
     * Predicts the most likely sentence with punctuation symbols inserted
     * given the input
     * @param input - string from where the prediction is to be made
     * @return the predicted sentence
     */
    public String predictPunctuation(String[] input) {
    	System.err.println("-----------------------PREDICTION---------------------------");
    	HyperStringFSA2 hypString = new HyperStringFSA2(input);
    	boolean isFound = false;
    	String prediction = "";
    	for(String[] s: hypString.getOutputs()) {
    		//System.err.println(Arrays.toString(s));
            if(nGramWrapper.exists(s)) {
                System.err.println("Possible string: " + Arrays.toString(s));
                System.err.println("Counts = " + nGramWrapper.counts(s));
                for (String w : s) {
                	prediction += w + " ";
                }
                prediction = HyperStringFSA2.postProcessing(prediction);
                isFound = true;
            }
        }

        if(!isFound) {
            System.err.println("Was not found in corpus");
        }
        
    	return prediction;
    }

    
    // Test method to run inputfrom command line
	private void handleInput(int nGramLength) {
        NGramWrapper ngw = new NGramWrapper(nGramLength);
        ngw.readFile(new File(CORPUS_TEST_PATH));
        System.err.println("Corpus:");
        System.err.println("Number of sentences: "+ngw.numberOfSentences);
        System.err.println("Number of tokens: "+ngw.numberOfTokens);
        System.err.println("Number of grams: "+ngw.getNgram().numberOfGrams());
        //ngw.serialize((OutputStream)(new FileOutputStream("test.txt")));
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = br.readLine();

            while(input != null) {
            	System.out.println(predictPunctuation(input.split(" ")));
                input = br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
	// Test
    public static void main(String[] args) {
        int nGramLength = 3;
        for(int i = 0; i < args.length; i += 2) {
            if(args[i].equals("n-gram")) {
                nGramLength = Integer.parseInt(args[i+1]);
            }
        }
        PunctuationPredicter pI = new PunctuationPredicter(nGramLength, "");
        pI.handleInput(nGramLength);
    }
}