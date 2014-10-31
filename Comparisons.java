import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Comparisons {

    public static void main(String[] args) {
        if(args.length!=2) {
            System.err.println("Correct usage is:\njava Comparisons <generated_file> <original_file>");
            System.exit(1);
        }
        File generated = new File(args[0]);
        File original = new File(args[1]);

        System.err.println("Data for generated file:");
        System.err.println("Question marks: "+countQuestionMark(generated));
        System.err.println("Exclamation marks: "+countExclamationMark(generated));
        System.err.println("Periods: "+countPeriod(generated));
        System.err.println("Commas: "+countComma(generated));
        System.err.println();
        System.err.println("Data for original file:");
        System.err.println("Question marks: "+countQuestionMark(original));
        System.err.println("Exclamation marks: "+countExclamationMark(original));
        System.err.println("Periods: "+countPeriod(original));
        System.err.println("Commas: "+countComma(original));
        System.err.println();
        System.err.println("Additional data:");

        char punctuations[] = {'.',',','?','!'};

        double[] FScore = getFScore(punctuations, generated, original);
        for(int i = 0; i < punctuations.length; i++) {
            System.err.println("F-score for "+punctuations[i]+" = "+FScore[i]);
        }
        System.err.println("Total f-score is "+FScore[FScore.length-1]);
    }

    /**
     Not implemented yet.
     */
    private static double getPerplexity(File generated, File original) {
        return Double.NaN;
    }
    /**
     Might be implemented.
     */
    private static double[] getFScore(char[] punctuation, File generated, File original) {
        double FScore[] = new double[punctuation.length+1];
        Arrays.fill(FScore, -1);
        try {
            //BufferedReader brGenerated = new BufferedReader(new FileReader(generated));
            //BufferedReader brOriginal = new BufferedReader(new FileReader(original));
            InputStreamReader brGenerated = new InputStreamReader(new FileInputStream(generated), "UTF-16BE");
            InputStreamReader brOriginal = new InputStreamReader(new FileInputStream(original), "UTF-16BE");
            /*
            The intention is to do a word by word comparison, if this is not possible, the amount of words in the generated compared to the original does not match which is _REALLY_ bad.
             */
            int originalChar = brOriginal.read();
            int generatedChar = brGenerated.read();
            final int space = ' ';
            int truePositivePrecision = 0;
            int trueAndFalsePrecision = 0;
            int positiveRecall = 0;
            int charTruePositivePrecision[] = new int[punctuation.length];
            int charTrueAndFalsePrecision[] = new int[punctuation.length];
            int charPositiveRecall[] = new int[punctuation.length];
            while(originalChar>=0) {
                //System.err.print(originalChar);
                //System.err.print(generatedChar+" ");
                //Check if first char is the punctuation looked for
                //If there is a generated punctuation there is either a false positive or a true positive
                /*
                Precision ...
                 */
                //if(generatedChar==punctuation) {
                int containsGenerated = contains(punctuation, generatedChar);
                int containsOriginal = contains(punctuation, originalChar);
                //if(contains(punctuation, generatedChar)) {
                //System.err.println(containsGenerated);

                //If a punctuation is detected in the generated document
                if(containsGenerated>=0) {
                    trueAndFalsePrecision++;
                    charTrueAndFalsePrecision[containsGenerated]++;
                    //System.err.println(originalChar+" "+generatedChar);
                }
                if(containsOriginal>=0) {
                    positiveRecall++;
                    charPositiveRecall[containsOriginal]++;
                }
                if((originalChar==generatedChar)&&containsGenerated>=0) {
                    truePositivePrecision++;
                    charTruePositivePrecision[containsGenerated]++;
                }

                //System.err.println(generatedChar+" "+originalChar);

                if(containsOriginal>=0&&containsGenerated<0) {
                    //System.err.println("A missed punctuation found");
                    originalChar = findNextChar(brOriginal);
                } else if(containsGenerated>=0&&containsOriginal<0) {
                    //System.err.println("An extra punctuation found");
                    generatedChar = findNextChar(brGenerated);
                } else if(originalChar!=generatedChar) {
                    //System.err.println("Different chars");
                    generatedChar = findNextChar(brGenerated);
                    originalChar = findNextChar(brOriginal);
                } else if(containsOriginal<0&&containsGenerated<0) {
                    //System.err.println("No punctuations found");
                    originalChar = findNextChar(brOriginal);
                    generatedChar = findNextChar(brGenerated);
                } else if(originalChar==generatedChar) {
                    //System.err.println("They are both the same punctuation.");
                    originalChar = findNextChar(brOriginal);
                    generatedChar = findNextChar(brGenerated);
                }
                else {
                    System.err.println("Else is run");
                    originalChar=brOriginal.read();
                    generatedChar=brGenerated.read();
                }

                /*
                //If the generated punctuation is the correct one
                if(originalChar==generatedChar&&containsGenerated>=0) {
                    truePositivePrecision++;
                    charTruePositivePrecision[containsGenerated]++;
                    skipThisWord(brOriginal);
                    skipThisWord(brGenerated);
                }
                //If there is a generated punctuation but it is the wrong one.
                else if((containsGenerated>=0)&&(containsOriginal>=0)&&originalChar!=generatedChar) {
                    skipThisWord(brOriginal);
                    skipThisWord(brGenerated);
                }
                //If there is a generated punctuation but there shouldn't be one
                else if((containsGenerated>=0)&&containsOriginal<0) {
                    skipThisWord(brGenerated); //Skip the punctuation
                    skipThisWord(brGenerated); //Skip the word that is looked up in original
                    skipThisWord(brOriginal);
                }
                //If there should be a puntuation but there isn't
                else if(containsOriginal>=0&&containsGenerated<0) {
                    skipThisWord(brOriginal);
                    skipThisWord(brOriginal);
                    skipThisWord(brGenerated);
                }
                */

                /*
                if(containsGenerated>=0) {
                    positivePrecision++;
                    charPositivePrecision[containsGenerated]++;
                    if (containsOriginal>=0) {

                    }
                    if (originalChar == punctuation[containsGenerated]) {
                        truePositivePrecision++;
                        charTruePositivePrecision[containsGenerated]++;

                        //Positive recall needs to be increased since a real punctuation was encountered.
                        positiveRecall++;
                        charPositiveRecall[containsGenerated]++;
                        skipThisWord(brOriginal);
                        skipThisWord(brGenerated);
                    } else {
                        skipThisWord(brGenerated);
                    }
                    //} else if(originalChar==punctuation) {
                    //} else if(contains(punctuation, originalChar)) {
                } else if(containsOriginal>=0) { //If a punctuation were not detected in the generated document but there should be one.
                    positiveRecall++;
                    charPositiveRecall[containsOriginal]++;
                    skipThisWord(brOriginal);
                } else {
                    skipThisWord(brOriginal);
                    skipThisWord(brGenerated);
                }
                */


            }
            for(int i = 0; i < punctuation.length; i++) {
                double precision = (double)charTruePositivePrecision[i]/(double)charTrueAndFalsePrecision[i];
                double recall = (double)charTruePositivePrecision[i]/(double)charPositiveRecall[i];
                FScore[i] = (2*precision*recall)/(precision+recall);
                if(punctuation[i]=='.') {
                    System.err.println("Score for punctuation " + punctuation[i]);
                    System.err.println("Precision = " + precision);
                    System.err.println("Recall = " + recall);
                    System.err.println("TruePositivePrecision = " + charTruePositivePrecision[i]);
                    System.err.println("TrueAndFalsePositivePrecision = "+charTrueAndFalsePrecision[i]);
                    System.err.println("PositiveRecall = "+charPositiveRecall[i]);
                }

            }
            double precision = (double)truePositivePrecision/(double)trueAndFalsePrecision;
            double recall = (double)truePositivePrecision/(double)positiveRecall;
            FScore[FScore.length-1] = (2*precision*recall)/(precision + recall);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return FScore;
    }
    private static int contains(char[] chars, int c) {
        for(int i = 0; i < chars.length; i++) {
            if(chars[i]==c) {
                return i;
            }
        }
        return -1;
    }
    /**
     * The underlying assumptions is that a word can only be separated by a space(' ') or a newline('\n').
     * @param br
     * @throws IOException
     */
    private static void skipThisWord(InputStreamReader br) throws IOException {
        int character = br.read();
        while((character>=0)&&(character!=' ')&&(character!='\n')) {
            character=br.read();
        }
        if(character==' ') {
            skipThisWord(br);
        }
    }
    private static int findNextChar(InputStreamReader br) throws IOException {
        int character = br.read();
        while((character>=0)&&(character!=' ')&&(character!='\n')) {
            character=br.read();
        }
        character=br.read();
        while(character==' '||character=='\n') {
            character=br.read();
        }
        return character;
    }
    private static long countExclamationMark(File f) {
        return countPunctuation('!', f);
    }
    private static long countQuestionMark(File f) {
        return countPunctuation('?', f);
    }
    private static long countComma(File f) {
        return countPunctuation(',', f);
    }
    private static long countPeriod(File f) {
        return countPunctuation('.', f);
    }
    /**
     Note that if the file is on one line, this is a fairly bad idea.
     br.read() should be investigate in that case.
     */
    private static long countPunctuation(char punctuation, File f) {
        long hits = -1;
        try {
            //BufferedReader br = new BufferedReader(new FileReader(f));
            InputStreamReader br = new InputStreamReader(new FileInputStream(f), "UTF-16BE");
            //String temp = br.readLine();
            hits++;
        /*
        This is only valid for ASCII...
         */
            int intPunctuation = punctuation;
            int read = br.read();
            while (read >= 0) {
                if (intPunctuation == read) {
                    hits++;
                }
                read = br.read();
            }
        /*while(temp!=null) {
            for(int i = 0; i < temp.length(); i++) {
                if(temp.charAt(i)==punctuation) {
                    hits++;
                }
            }
            temp=br.readLine();
        }
        */
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return hits;
    }
}