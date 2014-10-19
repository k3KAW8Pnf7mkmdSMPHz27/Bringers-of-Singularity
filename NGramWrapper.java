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

public class NGramWrapper {
    /*
    Todo ?
     */
    private static boolean padStart = false; //If {<s><s><s>I} is a correct 3-gram
    private static boolean padEnd = false; //If {be.<e><e><e>} is a correct 3-gram


    private static int nGramLength;
    long numberOfSentences = 0;
    long numberOfTokens = 0;

    private NGramModel ngram = new NGramModel();

    public static void main(String[] args) {
        File searchIn = new File("corpus.txt");
        nGramLength=3;
        NGramWrapper ngram = new NGramWrapper();
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
        System.err.println("Total ngram length = "+ngram.getNgram().numberOfGrams());
        System.err.println("Total sentences = "+ngram.numberOfSentences);
        System.err.println("Total tokens = "+ngram.numberOfTokens);
    }


    public NGramWrapper() {

    }


    public NGramModel getNgram() {
        return ngram;
    }

    private void readFile(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String newLine = br.readLine();
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