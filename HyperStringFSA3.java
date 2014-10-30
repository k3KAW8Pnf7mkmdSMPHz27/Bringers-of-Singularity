import java.io.File;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Hyper String Finite State Automata Object holding all possible combinations
 * of first letter capitalization and punctuation symbols .,?
 * 
 * @author joakimlilja
 *
 */
public class HyperStringFSA3 {
	public static final String EMPTY_PUNCT = "" + ((char) 007) + "EMPTY ";
	public static final String[] TRANSITIONS = { EMPTY_PUNCT, ",COMMA ", ".PERIOD ", "?QMARK ", "!EXCL " };
    //public static final String[] TRANSITIONS = { EMPTY_PUNCT, ",COMMA ", ".PERIOD "};
	public static final String[] POSTPROCESSES = { " ", ", ", ". ", "? ", "! " };
	public static final int TRANSITION_COUNT = TRANSITIONS.length;
	public static final int STATES_COUNT = 2;

	Vector<String[]> outputs;
	NGramWrapper nGram;

	/**
	 * Constructor creating a FSA based on the specified String array consisting
	 * of words
	 * 
	 * @param s
	 *            Array of words
	 * 
	 */
	public HyperStringFSA3(String[] s, NGramWrapper nGram) {
		outputs = new Vector<String[]>();
		this.nGram = nGram;
		constructFSA(s, outputs);
	}

	/**
	 * Construct the FSA with all possible outputs with each emission having a
	 * cost
	 * 
	 * @param s
	 *            Array of words
	 * 
	 * @param outputs
	 *            Vector holding all possible outputs
	 * 
	 */
	private void constructFSA(String[] s, Vector<String[]> outputs) {
		Node root = new Node("", 1.0);
        Long time = System.currentTimeMillis();
        PriorityQueue<Node> pq = new PriorityQueue<>();
		root = generateNodes(s, root);
        time = System.currentTimeMillis() - time;
        System.err.println("Generated nodes in "+time);
        time = System.currentTimeMillis();
		generateOutputs(root, outputs);
        time = System.currentTimeMillis() - time;
        System.err.println("Generated output in "+time);
	}

	/**
	 * Generate the outputs using a tree structure
	 * 
	 * @param node
	 * @param outputs
	 */
	private void generateOutputs(Node node, Vector<String[]> outputs) {
		if (node.children.size() == 0) {
			String s = backTrack(node, "", Integer.MAX_VALUE) + node.cost;
			// System.out.println(s);
			outputs.add(s.split(" "));

		} else {
			for (int i = 0; i < node.children.size(); i++) {
				generateOutputs(node.children.elementAt(i), outputs);
			}
		}
	}

	/**
	 * Backtrack from end node to generate the output of that path
	 * 
	 * @param node
	 * @param s
	 * @return output
	 */
	private String backTrack(Node node, String s, int n) {
		String value = node.value.equals(EMPTY_PUNCT) ? "" : node.value;
		if (node.parent == null || n == 0) {
			return value + s;

		} else {
			return backTrack(node.parent, value + s, n - 1);
		}

	}

	/**
	 * Generate children
	 * 
	 * @param s
	 * @param parent
	 * @return
	 */
	private Node generateNodes(String[] s, Node parent) {
		String unCapWord = deCapitalizeWord(s[0]);
		Node unCapNode = new Node(unCapWord + " ", parent.cost
				* getCost(parent, unCapWord));
		unCapNode.parent = parent;
		generateTransitions(unCapNode);

		String capWord = capitalizeWord(s[0]);
		Node capNode = new Node(capWord + " ", parent.cost
				* getCost(parent, capWord));
		capNode.parent = parent;
		generateTransitions(capNode);

		parent.children.add(capNode);
		parent.children.add(unCapNode);

		if (s.length > 1) {
			for (int i = 0; i < unCapNode.children.size(); i++) {
                /*
                Varför använder du set ?
                 */
				unCapNode.children.set(i, generateNodes(Arrays.copyOfRange(s, 1, s.length), unCapNode.children.get(i)));
				capNode.children.set(i, generateNodes(Arrays.copyOfRange(s, 1, s.length), capNode.children.get(i)));
			}
		}

		return parent;
	}

    class PriorityQueueElement{
        Node unCapNode;
        Node capNode;
        String capitalizedString;
        String uncapitalizedString;
        int i;
        String[] s;
        Node self;
        public PriorityQueueElement(int i, Node self, String[] s) {
            this.i = i;
            this.s = s;
            this.self = self;
        }
    }

	private double getCost(Node parent, String word) {
		String[] ngram = (backTrack(parent, word, nGram.getNGramLength() - 2)
				.split(" "));
		// System.err.println(Arrays.toString(ngram.split(" ")));
		double cost = 1.0;
		if (ngram.length > 1) { //Varför större än 1 istället för större än 0 ... ?
			cost = nGram.getCostOfNGram(ngram);
		}
		// System.err.println("Cost: " + cost);

		return cost;
	}

	/**
	 * Generate the possible punctuation transitions
	 * 
	 * @param node
	 */
	private void generateTransitions(Node parent) {
		for (int i = 0; i < TRANSITION_COUNT; i++) {
			String emission = TRANSITIONS[i];
			Node transNode = null;
			if (emission.equals(EMPTY_PUNCT)) {
				transNode = new Node(emission, parent.cost*0.5); //För att du vill ha en kostnad för att inte ha en punctuation ?
			} else {
				transNode = new Node(emission, parent.cost
						* getCost(parent, emission));
			}
			transNode.parent = parent;
			parent.children.add(transNode);
		}

	}

	private String capitalizeWord(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	private String deCapitalizeWord(String input) {
		return input.substring(0, 1).toLowerCase() + input.substring(1);
	}

	public String toString() {
		return outputs.toString();
	}

	public Vector<String[]> getOutputs() {
		return outputs;
	}

	public static String postProcessing(String input) {
		for (int i = 0; i < TRANSITION_COUNT; i++) {
			input = input.replaceAll(TRANSITIONS[i], POSTPROCESSES[i]);
		}
		return input;
	}

	public static void main(String... args) {
		String[] words = { "mars", "scientists" };
		NGramWrapper ngw = new NGramWrapper(3);
		ngw.readFile(new File("sentences.txt"));

		HyperStringFSA2 fsa = new HyperStringFSA2(words, ngw);
		for (String[] s : fsa.outputs) {
			System.out.println(Arrays.toString(s));
		}

	}

	private class Node {
		String value;
		double cost;
		Node parent;
		Vector<Node> children;

		public Node(String value, double cost) {
			children = new Vector<Node>();
			this.cost = cost;
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}

}