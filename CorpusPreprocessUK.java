import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 * This class loads the file corpus.txt, reads the file line by line up to the nr of lines provided in args[0].
 * Each line is processed according to a set of rules, and the resuls are written to ppCorpus.txt, line by line.
 * After next it reads the following lines args[0]+1 to args[0]+args[1] and preprocesses them for testing,
 * saving the results in testSentences.txt
 *
 *
 * @author Jasmin Suljkic
 */
public class CorpusPreprocessUK {

    /**
     * No arguments are taken in account.
     * Statically configured file corpus.txt is read and ppCorpus.txt as well as testSentences.txt is created and written to.
     * @param args -> X Y, (X: lines to for learning, Y: lines for testing)
     */
    public static void main(String[] args) {
        //args[0] -> Amount of lines to preprocess for learning
        //args[1] -> Amount of lines to preprocess for testing
        String corpusStringPath = "corpus.txt";
        String testSentenceStringPath = "testSentences";
        String testSentencesCorrectionStringPath = "testSentencesCorrection";

        // TODO Auto-generated method stub
        BufferedReader br;
        BufferedWriter bufferedWriterCorpus;
        BufferedWriter bufferedWriterTest;
        BufferedWriter bufferedWriterTestCorrection;

        StringBuffer sb = new StringBuffer();
        StringBuffer sbt = new StringBuffer();

        int nrLines=0;
        int toLearn = Integer.MAX_VALUE>>3;
        int toTest = Integer.MAX_VALUE>>3;
        if(args.length>=2) {
            toLearn = Integer.parseInt(args[0]);
            toTest = Integer.parseInt(args[1]);
        }

        try {
            //br = new BufferedReader(new FileReader("corpus.txt"));
            //br = new BufferedReader(new InputStreamReader(new FileInputStream("corpus.txt"), "UTF-8"));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(corpusStringPath)));
            //bufferedWriterCorpus = new BufferedWriter(new FileWriter("ppCorpus.txt"));
            bufferedWriterCorpus = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ppCorpus.txt"), "UTF-16BE"));
            //bufferedWriterTest = new BufferedWriter(new FileWriter("testSentences.txt"));
            //bufferedWriterTestCorrection = new BufferedWriter(new FileWriter("testScentencesCorrect.txt"));
            String line;
            char[] lc;

            //Go trough the corpus and count the amount of lines present
            while ((line=br.readLine()) != null) {
                nrLines++;
            }
            br.close();

            if((toLearn+toTest)>nrLines){
                System.err.println("Request invalid: Number of lines requested > nr of lines available in corpus.\nThere are "+nrLines+" number of lines.");
                System.err.println("toLearn = "+toLearn);
                System.err.println("toTest = "+toTest);
                return;
            }

            br=new BufferedReader(new FileReader("corpus.txt"));

            //Read a line from file (as long as there are lines in the file)
            //Process the line
            //Write the result to output file.
            int current = 0;
            boolean testing = false;
            OutputStreamWriter writeToTest[] = new OutputStreamWriter[1];
            OutputStreamWriter writeToTestCorrection[] = new OutputStreamWriter[1];
            for(int i = 0; i < writeToTest.length; i++) {
                writeToTest[i] = new OutputStreamWriter(new FileOutputStream(testSentenceStringPath+i+".txt"), "UTF-16BE");
                writeToTestCorrection[i] = new OutputStreamWriter(new FileOutputStream(testSentencesCorrectionStringPath+i+".txt"), "UTF-16BE");
                //writeToTest[i] = new OutputStreamWriter(new FileOutputStream("testSentences"+i+".txt"));
                //writeToTestCorrection[i] = new OutputStreamWriter(new FileOutputStream("testSentencesCorrection"+i+".txt"));
            }

            int corpusSentences = 0;
            int trainingSentences = 0;
            int index = 0;
            int length = 0;
            String buffer[] = new String[10];
            while ((line=br.readLine()) != null) {
                length += line.split(" ").length;
                if(length<=10) {
                    buffer[index]=line.toLowerCase().replaceAll("( )*[.!?]+( )*", " .PERIOD ").replaceAll("( )+", " ");
                    index++;
                } else if(length>=3) {
                    if(toLearn>corpusSentences) {
                        bufferedWriterCorpus.write("START ");
                        for(int i = 0; i < index; i++) {
                            //System.err.println(buffer[i]);
                            bufferedWriterCorpus.write(buffer[i]);
                        }
                        bufferedWriterCorpus.append(" ¿EOL");
                        bufferedWriterCorpus.newLine();
                        corpusSentences++;
                    } else if(trainingSentences<toTest) {
                        writeToTestCorrection[0].write("START ");
                        writeToTest[0].write("START ");
                        for(int i = 0; i < index; i++) {
                            writeToTestCorrection[0].write(buffer[i]);
                            writeToTest[0].write(buffer[i].replaceAll("( )*.PERIOD( )*", " "));
                        }
                        writeToTestCorrection[0].write(" ¿EOL");
                        writeToTestCorrection[0].write('\n');
                        writeToTest[0].write(" ¿EOL");
                        writeToTest[0].write('\n');
                        trainingSentences++;
                    } else {
                        break;
                    }

                    index = 0;
                    length = 0;
                } else {
                    index = 0;
                    length = 0;
                }
            }
            br.close();
            bufferedWriterCorpus.close();
            //bufferedWriterTest.close();
            System.err.println("Using encoding: "+writeToTest[0].getEncoding());
            System.err.println(corpusSentences+" sentences in corpus.");
            System.err.println(trainingSentences+" sentences in training.");
            for(int k = 0; k < writeToTest.length; k++) {
                writeToTest[k].close();
                writeToTestCorrection[k].close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
