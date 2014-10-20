import java.util.ArrayList;

public class Sentence {

  public static final int PERIOD           = 0;
  public static final int QUESTION_MARK    = 1;
  public static final int EXCLAMATION_MARK = 2;
  private ArrayList<Word> words;
  private int             classifier;

  public Sentence() {
    words = new ArrayList<Word>();
  }

  public ArrayList<Word> getWords() {
    return words;
  }

  public Word getWord(int index) {
    return words.get(index);
  }

  public void setWords(ArrayList<Word> words) {
    this.words = words;
  }

  public void addWord(Word word) {
    words.add(word);
  }

  public int getClassifier() {
    return classifier;
  }

  public void setClassifier(int classifier) {
    this.classifier = classifier;
  }
  //unsafe
  public Word getLastWord() {
    return words.get(words.size()-1);
  }
  
  public void setClassifier(Word word) {
    String wordString = word.getWord();
    if (wordString.equals(".")) {
      this.classifier = PERIOD;
    } else if (wordString.equals("?")) {
      this.classifier = QUESTION_MARK;
    } else if (wordString.equals("!")) {
      this.classifier = EXCLAMATION_MARK;
    }
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    for (final Word word : words) {
      sb.append(word.getWord() + " ");
    }
    return sb.toString().trim();
  }
}
