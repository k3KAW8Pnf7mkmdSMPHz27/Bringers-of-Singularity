import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.io.File;
import java.lang.StringBuilder;
import java.util.Arrays;

public class asketTest {
    public static void main(String[] args) {
        int nGramLength = 2;
        for(int i = 0; i < args.length; i += 2) {
            if(args[i].equals("n-gram")) {
                nGramLength = Integer.parseInt(args[i+1]);
            }
        }

        handleInput(nGramLength);
    }

    private static String[] tempFSA(String[] input) {
        char[] transitions = {' ', ',', '.', '?'};
        int numberOfTransitions = transitions.length;
        String[] output = new String[input.length*input.length*numberOfTransitions];
        int counter = 0;
        int internalCounters[] = new int[input.length];
        Arrays.fill(internalCounters, (transitions.length-1));
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
                String[] extraOut = tempFSA(FSAInput);
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
    private void addNGrams(String string, int length) {
        String input[] = string.split(" ");
        numberOfTokens += input.length;
        for(int i = 0; i < input.length-length+1; i++) {
            String[] ngram = new String[length];
            for(int j = 0; j < length; j++) {
                ngram[j] = input[i+j];
            }
            this.ngram.add(new StringList(ngram));
        }
    }
}