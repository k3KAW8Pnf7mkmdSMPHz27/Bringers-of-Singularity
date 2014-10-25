import java.io.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.Iterator;
import opennlp.tools.util.StringList;
import opennlp.tools.ngram.NGramModel;



public class asketTest {
    public static void main(String[] args) {
        int nGramLength = 2;
        String trainOn = "corpus.txt";

        NGramWrapper ngw = new NGramWrapper(nGramLength);
        //ngw.readFile(new File("corpus.txt"));

        String[][] test = HFSA(args);
        for(int i = 0; i < test.length; i++) {
            for(int j = 0; j < test[i].length; j++) {
                System.err.print(test[i][j] + " ");
            }
            System.err.println();
        }
        /*
        int nGramLength = 2;
        for(int i = 0; i < args.length; i += 2) {
            if(args[i].equals("n-gram")) {
                nGramLength = Integer.parseInt(args[i+1]);
            }
        }

        handleInput(nGramLength);
        */
    }
    /**
     * The underlying assumption is that fakeHyperFSA()[0] is space and that space is the most common in-between in the corpus.
     * matrix[][][0] = the score
     * matrix[][][1] = used for backtracking purposes and terminates on -1.
     */
    private static String[] dynProg(final String[] punctuation, final String[] words, final int NGramLength) {
        double[][][] matrix = new double[words.length][punctuation.length][2];

        /*
        Init
         */
        String[][] temp = fakeHyperFSA(words, 0, NGramLength);
        for(int i = 0; i < matrix[0].length; i++) {
            double NGramScore = fakeNGramValue(temp[i]);
            matrix[0][i][0] = NGramScore;
            matrix[0][i][1] = -1; //-1 will be used to stop the backtrack.
        }

        /*
        Calculating matrix ...
         */
        for(int i = 1; i < matrix.length-NGramLength; i++) {
            String[][] NGrams = fakeHyperFSA(words, i, NGramLength);
            for(int j = 0; j < matrix[i].length; j++) {


                for(int k = 0; k < NGramLength; k++) {
                    double NGramScore = fakeNGramValue(NGrams[k]);


                }
            }
        }

        /*
        Decode step
         */
        return null;
    }
    private static String[][] fakeHyperFSA(String[] s, int from, int NGramLength) {
        return null;
    }
    private static double fakeNGramValue(String[] s) {
        return 0;
    }
    /**
    Continuously step through the input
     */
    private static void oneWordStep(String words[], int NGramLength, NGramWrapper ngw) {
        /*
        Init must be all possible versions of the first n-gram size.
         */
        int version = -1;
        char[] transitions = {' ', ',', '.', '?'};
        int possibleCombinations = NGramLength*transitions.length*transitions.length+1;
        double maxValue = Double.MIN_VALUE;
        for(int i = 0; i < possibleCombinations; i++) {

        }
    }
    private static String[][] createPermuatedStrings(final String words[], final int numberOfPermuations) {
        String[][] permuations = new String[numberOfPermuations][words.length];
        final char[] transitions = {' ', ',', '.', '?'};
        String[] tempArg = new String[words.length/2];
        System.arraycopy(words, 0, tempArg, 0, tempArg.length);
        String[] tempReturn = tempFSA(tempArg);
        String[][] moreTemporary = new String[numberOfPermuations][words.length];
        for(int i = 0; i < tempReturn.length; i++) {
            System.arraycopy(tempReturn[i].split("[ ,.?!]+"), 0, moreTemporary[i], 0, tempReturn[i].split("[ ,.?!]+").length);
        }
        if(tempReturn[0].split("[ ,.?!]+").length!=words.length) { //A word is missing ...

        }

        if(permuations[permuations.length-1]!=null) {
            System.err.println("Last entry of permutations is != null");
            System.exit(1);
        }
        permuations[permuations.length-1]=words;
        return permuations;
    }
    /**
    If a . or ! or ? is detected, make the word afterward the start of a new sentence.
     */
    private static void oneWordJump() {

    }
    /**
     * A case insensitive HFSA ...
     * @param input
     * @return
     */
    private static String[][] HFSA(String[] input) {
        int NGramLength = input.length;
        String[] transitions = {" ", ",COMMA", ".PERIOD", "?QMARK", "!EXCL"};
        //String[] transitions = {" ", ".PERIOD"};
        int numberOfTransitionsInString = input.length/2;
        int internalCounters[] = new int[numberOfTransitionsInString];
        Arrays.fill(internalCounters, transitions.length-1);
        int numberOfReturnValues = (int)Math.pow(transitions.length, numberOfTransitionsInString);
        String[][] returnValue = new String[numberOfReturnValues][NGramLength];
        System.err.println("NumberOfTransitions = "+numberOfTransitionsInString);
        System.err.println("Transitions length = "+transitions.length);

        int counter = 0;
        while (internalCounters[0]>=0) {
            int positionInInnerArray = 0;
            int positionInOuterArray = 0;
            String[] string = new String[input.length];

            for(int i = 0; i < numberOfTransitionsInString; i++) {
                string[positionInInnerArray] = input[positionInOuterArray];
                positionInOuterArray++;
                positionInInnerArray++;
                if(!transitions[internalCounters[i]].equals(" ")) {
                    string[positionInInnerArray] = transitions[internalCounters[i]];
                    positionInInnerArray++;
                } else {
                    string[positionInInnerArray] = input[positionInOuterArray];
                    positionInInnerArray++;
                    positionInOuterArray++;
                }
            }
            for(int i = positionInInnerArray; i < NGramLength; i++) {
                string[i] = input[positionInOuterArray];
                positionInOuterArray++;
            }

            internalCounters[internalCounters.length-1]--;

            for(int i = internalCounters.length-1; i > 0; i--) {
                if(internalCounters[i]<0) {
                    internalCounters[i] = transitions.length-1;
                    internalCounters[i-1]--;
                }
            }

            returnValue[counter] = string;
            counter++;
        }

        return returnValue;
    }
    /**
     * Note, this method DOES NOT WORK. It SHOULD generate ArrayIndexOutOfBoundsException.
     * @param input
     * @return
     */
    private static String[] tempFSA(String[] input) {
        char[] transitions = {' ', ',', '.', '?'};
        int numberOfTransitions = transitions.length;
        String[] output = new String[input.length*input.length*numberOfTransitions];
        int counter = 0;
        int internalCounters[] = new int[input.length];
        Arrays.fill(internalCounters, (transitions.length - 1));
        while (internalCounters[0]>=0) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < input.length; i++) {
                sb.append(input[i]);
                sb.append(transitions[internalCounters[i]]);
            }
            //System.err.println(internalCounters[internalCounters.length-1]);
            internalCounters[internalCounters.length-1]--;

            for(int i = 1; i < internalCounters.length; i++) {
                if(internalCounters[i]<0) {
                    internalCounters[i] = numberOfTransitions-1;
                    internalCounters[i-1]--;
                }
            }
            //System.err.println(sb.toString());
            output[counter] = sb.toString();
            counter++;
        }

        return output;
    }
    private static void handleInput(int nGramLength) {
        NGramWrapper ngw = new NGramWrapper(nGramLength);
        ngw.readFile(new File("/Users/JAsketorp/Documents/DD2380/smsCorpusAsText.txt"));
        System.err.println("Corpus:");
        System.err.println("Number of sentences: "+ngw.numberOfSentences);
        System.err.println("Number of tokens: "+ngw.numberOfTokens);
        System.err.println("Number of grams: "+ngw.getNgram().numberOfGrams());
        //ngw.serialize((OutputStream)(new FileOutputStream("test.txt")));
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = br.readLine();

            while(input != null) {
                String[][] fromInput = createNGramsFromText(nGramLength, input.split(" "));

                //String temp[] = input.split(" ");
                //String FSAInput[] = {temp[0], temp[1]};
                /*
                HyperStringFSA versions = new HyperStringFSA(FSAInput);
                System.err.println("-----------------------HYPERSTRING---------------------------");
                for(String s: versions.outputs) {
                    System.err.println(s);
                }
                */
                String[] extraOut = tempFSA(input.split(" "));
                boolean found = true;
                for(String s: extraOut) {
                    if(ngw.exists(s.split(" "))) {
                        System.err.println("Possible string: " + s);
                        System.err.println("Counts = " + ngw.counts(s.split(" ")));
                        found = false;
                    }
                }
                if(found) {
                    System.err.println("Was not found in corpus");
                }
                input = br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private static String[][] createNGramsFromText(int nGramLength, String input[]) {
        String output[][] = new String[input.length-nGramLength+1][nGramLength];
        for(int i = 0; i < output.length; i++) {
            //StringBuilder sb = new StringBuilder();
            for(int j = 0; j < nGramLength; j++) {
                output[i][j] = input[i+j];
            }
        }
        return output;
    }
    private static void workAroundToSaveNGramModel(OutputStream out, NGramModel ngm) {
        PrintWriter pw = new PrintWriter(out);

        Iterator<StringList> iterator = ngm.iterator();
        while (iterator.hasNext()) {
            StringList sl = iterator.next();
            int count = ngm.getCount(sl);
            pw.print(sl.toString());
            pw.print(' ');
            pw.print(count);
            pw.println();
        }
        pw.flush();
    }
}