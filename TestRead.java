import java.util.List;

public class TestRead {

  public static void main(String args[]) {
    //final StaXParser read = new StaXParser();
    final Read read = new Read();
    //final List<Sentence> readConfig = read.readConfig("config.xml");
    final List<Sentence> readConfig = read.Read("testread");
    for (final Sentence item : readConfig) {
      System.out.println(item);
      System.out.println(item.getClassifier());
    }
  }
}