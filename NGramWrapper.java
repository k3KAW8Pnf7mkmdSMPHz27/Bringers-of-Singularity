/*
OpenNLP can be found at: https://opennlp.apache.org/cgi-bin/download.cgi
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.io.File;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.StringList;
import java.util.Iterator;

public class NGramWrapper {
    /*
    Todo ?
     */
    private static boolean padStart = false; //If {<s><s><s>I} is a correct 3-gram
    private static boolean padEnd = false; //If {be.<e><e><e>} is a correct 3-gram


    private static int nGramLength;
    long numberOfSentences = 0;
    long numberOfTokens = 0;

    NGramModel ngram = new NGramModel();

    public static void main(String[] args) {
        File searchIn = new File("corpus.txt");
        nGramLength=3;
        NGramWrapper ngram = new NGramWrapper(nGramLength);
        for(int i = 0; i < args.length; i+=2) {
            if(args[i].equals("n-gram")) {
                nGramLength = Integer.parseInt(args[i+1]);
            } else if(args[i].equals("corpus")) {
                searchIn = new File(args[i+1]);
            } else {
                System.err.println(args[i]+ " is invalid.");
            }
        }

        ngram.readFile(searchIn);
        System.err.println("Total ngram length = "+getNumberOfNGrams(ngram.getNgram()));
        System.err.println("Total lines = "+ngram.numberOfSentences);
        System.err.println("Total tokens = "+ngram.numberOfTokens);
    }

    public static long getNumberOfNGrams(NGramModel ngm) {
        Iterator<StringList> it = ngm.iterator();
        long count = 0;
        while(it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }



    public NGramWrapper(int nGramLength) {
        NGramWrapper.nGramLength = nGramLength;
    }

    public boolean exists(String[] s) {
        return ngram.contains(new StringList(s));
    }

    public int counts(String[] s) {
        return ngram.getCount(new StringList(s));
    }

    public NGramModel getNgram() {
        return ngram;
    }

    public void readFile(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String newLine = br.readLine();
            /*
            The assumption that one line is a sentence is erreneouos in ukWAC.
             */
            while(newLine!=null) {
                addNGrams(newLine, nGramLength);
                numberOfSentences++;
                newLine=br.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
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