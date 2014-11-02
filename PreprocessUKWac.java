public class PreprocessUKWac {
    public static void main(String[] args) {
        String ukwacStringPath = "UKWAC-1.xml";
        ukWacToTxt.main(new String[]{ukwacStringPath});
        CorpusPreprocessUK.main(new String[]{"50000", "1000"});
    }
}