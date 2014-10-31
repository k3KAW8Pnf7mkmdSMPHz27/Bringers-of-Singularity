import java.io.*;
import java.util.Arrays;
import java.util.Vector;

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
        /*
        String prediction = "";
        double maxCount = 0;
        for (String[] s : hypString.getOutputs()) {
            //System.err.println(Arrays.toString(s));
            double count = Double.parseDouble(s[s.length - 1]);
            //System.err.println(count);
            if (count > maxCount) {
                prediction = "";
                maxCount = count;
                for (String w : s) {
                    prediction += w + " ";
                }
            }
        }
        */

        return hypString.getOptimalString();
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

    private double getCostOfString(String word) {
        String ngram[] = word.split(" ");
        double value = 1.0D;
        for(int i = ngram.length-1; i > nGramWrapper.getNGramLength()-1; i--) {
            String[] argument = new String[nGramWrapper.getNGramLength()];
            System.arraycopy(ngram, i-nGramWrapper.getNGramLength(), argument, 0, nGramWrapper.getNGramLength());
            value *= nGramWrapper.getCostOfNGram(argument);
        }
        return value;

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
        for(int i = 0; i < 3; i++) {
            String evaluate = "testSentences"+i+".txt";
            String answers = "testSentencesAnswers"+i+".txt";
            try {
                //BufferedReader br = new BufferedReader(new FileReader(evaluate));
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(evaluate), "UTF-16BE"));
                //PrintWriter pw = new PrintWriter(answers);
                OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(answers), "UTF-16BE");
                int counter = Integer.MAX_VALUE;
                //int counter = 3;
                while ((counter > 0) && br.ready()) { //Risky?
                    //while(false) {
                    long time = System.currentTimeMillis();
                    String fix = br.readLine();
                    //System.err.println("---------------------");
                    //System.err.println(fix);
                    fix = fix.trim().replaceAll("( )+", " ");
                    //if(fix.split(" ").length<9) {
                    if (true) {
                        System.err.println("-----------------------------------------------------");
                        System.err.println(fix);
                        //System.out.println(pI.predictPunctuation(fix));
                        String answer = pI.predictPunctuation(fix);
                        pw.write(answer);
                        pw.write('\n');
                        //System.err.println(answer);
                        time = System.currentTimeMillis() - time;
                        time = time / 1000;
                        System.err.println("Spent " + time + " s calculating sentence.");
                    }
                    counter--;
                }
                br.close();
                pw.close();
            /*
            br = new BufferedReader(new FileReader("testdata.txt"));
            while(br.ready()) {
                String input = br.readLine();
                System.err.println(input+"\t"+pI.getCostOfString(input));
            }
            */
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Ready for prediction");
        //pI.handleInput(nGramLength);
    }
}