public class Word {

  private String word;
  private String tag;
  private String stemmedWord;

  public Word(String word, String tag, String stemmedWord) {
    this.word = word;
    this.tag = tag;
    this.stemmedWord = stemmedWord;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getStemmedWord() {
    return stemmedWord;
  }

  public void setStemmedWord(String stemmedWord) {
    this.stemmedWord = stemmedWord;
  }
}
