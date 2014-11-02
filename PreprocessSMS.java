import java.io.IOException;
import java.io.File;

public class PreprocessSMS {
    public static void main(String[] args) {
        smsCorpusToText.main(null);
        CorpusPreprocess.main(new String[]{"10000", "100", "0"});

        try {
            asketTest.removeMultiplePunctuations(new File("testSentences0.txt"));
            asketTest.removeMultiplePunctuations(new File("ppCorpus.txt"));
            asketTest.removeMultiplePunctuations(new File("testSentencesCorrection0.txt"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}