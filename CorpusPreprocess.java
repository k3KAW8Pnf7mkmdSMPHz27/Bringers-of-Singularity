import java.io.*;

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
            br = new BufferedReader(new FileReader("corpus.txt"));
            bufferedWriterCorpus = new BufferedWriter(new FileWriter("ppCorpus.txt"));
            bufferedWriterTest = new BufferedWriter(new FileWriter("testSentences.txt"));
            bufferedWriterTestCorrection = new BufferedWriter(new FileWriter("testScentencesCorrect.txt"));
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

            br=new BufferedReader(new FileReader("corpus.txt"));

            //Read a line from file (as long as there are lines in the file)
            //Process the line
            //Write the result to output file.
            int current = 0;
            boolean testing = false;
            PrintWriter writeToTest[] = new PrintWriter[20];
            PrintWriter writeToTestCorrection[] = new PrintWriter[20];
            for(int i = 0; i < writeToTest.length; i++) {
                writeToTest[i] = new PrintWriter("testSentences"+i+".txt");
                writeToTestCorrection[i] = new PrintWriter("testSentencesCorrection"+i+".txt");
            }
            while ((line=br.readLine()) != null) {
                if(current==toLearn+1){
                    testing=true;
                }
                if(current==(toLearn+toTest)){
                    break;
                }
                /*
                Handling input on one line.
                 */
                int category = line.trim().split("( )+").length; //Very inefficient =)
                if(category>=writeToTest.length) {
                    category=writeToTest.length-1;
                }
                lc = line.toCharArray();
                if(testing) {
                    sbt.append(" START ");
                    writeToTest[category].print(" START ");
                    writeToTestCorrection[category].print(" START ");
                }
                sb.append(" START ");
                for(char c : lc){
                    if(c=='.'){
                        if(testing){
                            sbt.append(" ");
                            writeToTest[category].print(" ");
                            writeToTestCorrection[category].print(" .PERIOD ");
                        }
                        sb.append(" .PERIOD ");
                    }
                    else if(c=='!'){
                        if(testing){
                            sbt.append(" ");
                            writeToTest[category].print(" ");
                            writeToTestCorrection[category].print(" .PERIOD ");
                        }
                        //sb.append(" !EXCL ");
                        sb.append(" .PERIOD ");
                    }
                    else if(c=='?'){
                        if(testing){
                            sbt.append(" ");
                            writeToTest[category].print(" ");
                            writeToTestCorrection[category].print(" .PERIOD ");
                        }
                        //sb.append(" ?QMARK ");
                        sb.append(" .PERIOD ");
                    }
                    else if(c==','){
                        if(testing){
                            sbt.append(" ");
                            writeToTest[category].print(" ");
                            writeToTestCorrection[category].print(" .PERIOD ");
                        }
                        //sb.append(" ,COMMA ");
                        sb.append(" .PERIOD ");
                    }
                    else{
                        if(testing){
                            sbt.append(c);
                            writeToTest[category].print(c);
                            writeToTestCorrection[category].print(c);

                        }
                        sb.append(c);
                    }
                }
                if(testing) {
                    sbt.append(" ¿EOL ");
                    writeToTest[category].print(" ¿EOL ");
                    writeToTestCorrection[category].print(" ¿EOL ");
                    writeToTest[category].println();
                    writeToTestCorrection[category].println();
                }
                sb.append(" ¿EOL");
                if(testing){
                    bufferedWriterTest.write(sbt.toString());
                    sbt = new StringBuffer();
                    bufferedWriterTest.newLine();

                    bufferedWriterTestCorrection.write(sb.toString());
                    sb = new StringBuffer();
                    bufferedWriterTestCorrection.newLine();
                }
                else{
                    bufferedWriterCorpus.write(sb.toString());
                    sb = new StringBuffer();
                    bufferedWriterCorpus.newLine();
                }

                current++;
            }
            br.close();
            bufferedWriterCorpus.close();
            bufferedWriterTest.close();
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
