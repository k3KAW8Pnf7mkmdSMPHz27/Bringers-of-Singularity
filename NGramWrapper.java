/*
OpenNLP can be found at: https://opennlp.apache.org/cgi-bin/download.cgi
 */

import java.io.*;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.IllegalArgumentException;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.StringList;

public class NGramWrapper {
    /**
     * The Stupid Backoff currently assumes that if it has never seen a word before it is equivalent to having seen it once (i.e. very unlikely).
     */
    public final static int STUPID_BACKOFF = 0;
    public final static double STUPID_BACKOFF_ALPHA = 0.4; //Following http://stackoverflow.com/questions/16383194/stupid-backoff-implementation-clarification
    /*
    From Stanley F. Chen and Joshua Goodman (1998), “An Empirical Study of Smoothing Techniques for Language Modeling"
     */
    public final static int MODIFIED_KNESER_NEY = 1;
    public final static int JELINEK_MERCER = 2;
    /**
     * Sets which smoothing technique should be used.
     * Note, this is not thread-safe.
     */
    public static int smoothing = STUPID_BACKOFF;

    /*
    Todo ?
     */
    private static boolean padStart = false; //If {<s><s><s>I} is a correct 3-gram
    private static boolean padEnd = false; //If {be.<e><e><e>} is a correct 3-gram

    long numberOfSentences = 0;
    long numberOfTokens = 0;

    private int nGramLength;

    //NGramModel ngram = new NGramModel();
    NGramModel ngram[];

    public static void main(String[] args) {
        File searchIn = new File("corpus.txt");
        int nGramLength=3;
        //NGramWrapper ngram = new NGramWrapper(nGramLength);
        for(int i = 0; i < args.length; i+=2) {
            if(args[i].equals("n-gram")) {
                nGramLength = Integer.parseInt(args[i+1]);
            } else if(args[i].equals("corpus")) {
                searchIn = new File(args[i+1]);
            } else {
                System.err.println(args[i]+ " is invalid.");
            }
        }
        NGramWrapper ngw = new NGramWrapper(nGramLength);
        ngw.readFile(searchIn);
    }

    /**
     * Assumes that the N-Gram is correct size.
     * @param s
     * @return
     */
    public double getCostOfNGram(String[] s) {
        return getCostOfNGram(s, this.smoothing);
    }
    public double getCostOfNGram(String[] s, int smoothing) {
        double value = 0;
        switch (smoothing) {
            case STUPID_BACKOFF: //From http://stackoverflow.com/questions/16383194/stupid-backoff-implementation-clarification
                if(s.length>1) {
                    value = counts(s);
                    String argument[] = new String[s.length-1];
                    System.arraycopy(s, 1, argument, 0, argument.length);
                    if(value>0) {
                        value /= counts(argument);
                    } else {
                        value = STUPID_BACKOFF_ALPHA*getCostOfNGram(argument);
                    }
                } else { //This is only "valid" because we will have a small corpus
                    double counts = counts(s);
                    if(counts==0) {
                        counts=0.1;
                    }
                    double total = ngram[0].numberOfGrams();
                    value = counts/total;
                }
                break;
            case JELINEK_MERCER: //From http://nlp.stanford.edu/~wcmac/papers/20050421-smoothing-tutorial.pdf

                break;
            default:
                throw new IllegalArgumentException();
        }

        return value;
    }
    public NGramWrapper(int nGramLength) {
        this.nGramLength = nGramLength;
        ngram = new NGramModel[nGramLength];
    }
    public boolean exists(String[] s) {
        return ngram[s.length-1].contains(new StringList(s));
    }
    public int counts(String[] s) {
        return ngram[s.length-1].getCount(new StringList(s));
    }
    public NGramModel getNgram() {
        return ngram[ngram.length-1];
    }
    private int getNumberOfNGrams(NGramModel ngm) {
        Iterator<StringList> iterator = ngm.iterator();
        int count = 0;
        while(iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
    public int getNGramLength() {
        return nGramLength;
    }
    public void readFile(File f) {
        System.err.println(f.getAbsolutePath());
        for(int i = 0; i < ngram.length; i++) {
            numberOfSentences = 0;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-16BE"));//new BufferedReader(new FileReader(f));
                String newLine = br.readLine();
                ngram[i] = new NGramModel();
                while (newLine != null) {
                    addNGrams(newLine, (i+1), ngram[i]);
                    numberOfSentences++;
                    newLine = br.readLine();
                }
                System.err.println("N-Gram size = "+(i+1));
                        /*
                        Fixa......................
                         */
                System.err.println("Total ngram length = " + getNumberOfNGrams(ngram[i]));//.numberOfGrams());
                System.err.println("Total lines = " + numberOfSentences);
                System.err.println("Total tokens = " + numberOfTokens);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void addNGrams(String string, int length, NGramModel ngm) {
        String input[] = string.split(" ");
        numberOfTokens += input.length;
        for(int i = 0; i < input.length-length+1; i++) {
            String[] ngram = new String[length];
            for(int j = 0; j < length; j++) {
                ngram[j] = input[i+j];
            }
            ngm.add(new StringList(ngram));
        }
    }
}