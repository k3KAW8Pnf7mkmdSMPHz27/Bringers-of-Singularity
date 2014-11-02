public class PreprocessUKWac {
    public static void main(String[] args) {
        String ukwacStringPath = "UKWAC-1.xml";
        ukWacToTxt.main(new String[]{ukwacStringPath});
        CorpusPreprocess.main(null);
    }
}