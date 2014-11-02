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
public class CorpusPreprocess {

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
        if(args.length==2) {
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
                return;
            }

            Random r = new Random(0);
            int[] uniqueLinesToLearn = new int[toLearn];
            int[] uniqueLinesToTest = new int[toTest];
            for(int j = 0; j < uniqueLinesToLearn.length; j++) {
                int lineNumber = r.nextInt(toLearn+toTest);
                boolean isNotAcceptable = true;
                while(isNotAcceptable) {
                    isNotAcceptable=false;
                    for (int i = j - 1; i >= 0; i--) {
                        if(uniqueLinesToLearn[i]==lineNumber) {
                            isNotAcceptable = true;
                            lineNumber = r.nextInt(toLearn+toTest);
                            break;
                        }
                    }
                }
                uniqueLinesToLearn[j]=lineNumber;
            }
            for(int j = 0; j < uniqueLinesToTest.length; j++) {
                int lineNumber = r.nextInt(uniqueLinesToTest.length+1);
                boolean isNotAcceptable = true;
                while(isNotAcceptable) {
                    isNotAcceptable=false;
                    for(int k = uniqueLinesToLearn.length-1; k>=0; k--) {
                        if(uniqueLinesToLearn[k]==lineNumber) {
                            isNotAcceptable=true;
                            lineNumber = r.nextInt(toLearn+toTest);
                            break;
                        }
                    }
                    if(!isNotAcceptable) {
                        for (int i = j - 1; i >= 0; i--) {
                            if (uniqueLinesToTest[i] == lineNumber) {
                                isNotAcceptable = true;
                                lineNumber = r.nextInt(toLearn+toTest);
                                break;
                            }
                        }
                    }
                }
                uniqueLinesToTest[j]=lineNumber;
            }
            Arrays.sort(uniqueLinesToLearn);
            Arrays.sort(uniqueLinesToTest);
            System.out.println(uniqueLinesToLearn.length);
            System.out.println(uniqueLinesToTest.length);

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
            while ((line=br.readLine()) != null) {
                /*
                if(current==toLearn+1){
                    testing=true;
                }
                if(current==(toLearn+toTest)){
                    break;
                }*/
                boolean skip = true;
                if(Arrays.binarySearch(uniqueLinesToLearn, current)>=0) {
                    testing = false;
                } else if(Arrays.binarySearch(uniqueLinesToTest, current)>=0) {
                    testing = true;
                } else {
                    skip=false;
                }
                /*
                Handling input on one line.
                 */
                if(skip) {
                    int category = line.trim().split("( )+").length; //Very inefficient =)
                    if(category>=writeToTest.length) {
                        category=writeToTest.length-1;
                    }
                    lc = line.toCharArray();
                    if(testing) {
                        sbt.append(" START ");
                        writeToTest[category].write(" START ");
                        writeToTestCorrection[category].write(" START ");
                    }
                    sb.append(" START ");
                    for(char c : lc){
                        if(c=='.'){
                            if(testing){
                                sbt.append(" ");
                                writeToTest[category].write(" ");
                                writeToTestCorrection[category].write(" .PERIOD ");
                            }
                            sb.append(" .PERIOD ");
                        }
                        else if(c=='!'){
                            if(testing){
                                sbt.append(" ");
                                writeToTest[category].write(" ");
                                writeToTestCorrection[category].write(" .PERIOD ");
                            }
                            //sb.append(" !EXCL ");
                            sb.append(" .PERIOD ");
                        }
                        else if(c=='?'){
                            if(testing){
                                sbt.append(" ");
                                writeToTest[category].write(" ");
                                writeToTestCorrection[category].write(" .PERIOD ");
                            }
                            //sb.append(" ?QMARK ");
                            sb.append(" .PERIOD ");
                        }
                        else if(c==','){
                            if(testing){
                                sbt.append(" ");
                                writeToTest[category].write(" ");
                                writeToTestCorrection[category].write(" .PERIOD ");
                            }
                            //sb.append(" ,COMMA ");
                            sb.append(" .PERIOD ");
                        }
                        else{
                            if(testing){
                                sbt.append(c);
                                writeToTest[category].write(c);
                                writeToTestCorrection[category].write(c);

                            }
                            sb.append(c);
                        }
                    }
                    if(testing) {
                        sbt.append(" ¿EOL ");
                        writeToTest[category].write(" ¿EOL ");
                        writeToTestCorrection[category].write(" ¿EOL ");
                        writeToTest[category].write('\n');
                        writeToTestCorrection[category].write('\n');
                    }
                    sb.append(" ¿EOL");
                    if(testing){
                        //bufferedWriterTest.write(sbt.toString());
                        sbt = new StringBuffer();
                        //bufferedWriterTest.newLine();

                        //bufferedWriterTestCorrection.write(sb.toString());
                        sb = new StringBuffer();
                        //bufferedWriterTestCorrection.newLine();
                    }
                    else{
                        bufferedWriterCorpus.write(sb.toString());
                        sb = new StringBuffer();
                        bufferedWriterCorpus.newLine();
                    }
                }
                current++;

            }
            br.close();
            bufferedWriterCorpus.close();
            //bufferedWriterTest.close();
            System.err.println("Using encoding: "+writeToTest[0].getEncoding());
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
