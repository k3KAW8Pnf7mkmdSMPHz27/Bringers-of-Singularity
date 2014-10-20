import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Read {

  List<Sentence> sentences = new ArrayList<Sentence>();
  Sentence       sentence;

  public List<Sentence> Read(String filename) {


    try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
      for(String line; (line = br.readLine()) != null; ) {
        if (line.startsWith("<text")){   
          continue;
        } else if (line.equals("<s>")) {
          sentence = new Sentence();
          continue;
        } else if (line.equals("</s>")) {
          String wordString = sentence.getLastWord().getWord();
          int classifier = -1;
          if (wordString.equals(".")) {
            classifier = Sentence.PERIOD;
          } else if (wordString.equals("?")) {
            classifier = Sentence.QUESTION_MARK;
          } else if (wordString.equals("!")) {
            classifier = Sentence.EXCLAMATION_MARK;
          }
          sentence.setClassifier(classifier);
          sentences.add(sentence);
          continue;

        } else {
          final String[] l = line.split("\\s");
          if (l.length == 3) {
            final Word word = new Word(l[0],l[1],l[2]);
            sentence.addWord(word);
          }
          
        }
      }

    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return sentences;
  }
  
  public void Write(List<Sentence> sentences) {
    
  }
}
