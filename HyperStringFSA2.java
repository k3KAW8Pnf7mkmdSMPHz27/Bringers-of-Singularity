import java.io.File;
import java.util.Arrays;
import java.util.Vector;

/**
 * Hyper String Finite State Automata Object holding all possible combinations
 * of first letter capitalization and punctuation symbols .,?
 * 
 * @author joakimlilja
 *
 */
public class HyperStringFSA2 {
	public static final String[] TRANSITIONS = { ",COMMA ", ".PERIOD ",
			"\\?QMARK ", "!EXCL " };
	public static final String[] POSTPROCESSES = { ", ", ". ", "? ", "! " };
	public static final int TRANSITION_COUNT = 4;
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
	public HyperStringFSA2(String[] s, NGramWrapper nGram) {
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
		Node root = new Node("", 0d);
		root = generateNodes(s, root);
		generateOutputs(root, outputs);
	}

	/**
	 * Generate the outputs using a tree structure
	 * 
	 * @param node
	 * @param outputs
	 */
	private void generateOutputs(Node node, Vector<String[]> outputs) {
		if (node.children.size() == 0) {
			String s = backTrack(node, "", 100) + node.cost;
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
		if (node.parent == null || n == 0) {
			return node.value + s;
		} else {
			return backTrack(node.parent, node.value + s, n - 1);
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
				+ getCost(parent, unCapWord));
		unCapNode.parent = parent;
		generateTransitions(unCapNode);

		String capWord = capitalizeWord(s[0]);
		Node capNode = new Node(capWord + " ", parent.cost
				+ getCost(parent, capWord));
		capNode.parent = parent;
		generateTransitions(capNode);
		parent.children.add(capNode);
		parent.children.add(unCapNode);

		if (s.length > 1) {
			for (int i = 0; i < unCapNode.children.size(); i++) {
				unCapNode.children.set(
						i,
						generateNodes(Arrays.copyOfRange(s, 1, s.length),
								unCapNode.children.get(i)));
				capNode.children.set(
						i,
						generateNodes(Arrays.copyOfRange(s, 1, s.length),
								capNode.children.get(i)));
			}
			// Add empty emission with zero count (WHAT COST FOR EMPTY
			// EMISSION??)
			String[] nextWord = Arrays.copyOfRange(s, 1, 2);
			unCapNode = generateNodes(nextWord, unCapNode);
			capNode = generateNodes(nextWord, capNode);
		}
		if (s.length == 1) {
			Node emptyEndNode1 = new Node("", parent.cost);
			emptyEndNode1.parent = unCapNode;
			unCapNode.children.add(emptyEndNode1);

			Node emptyEndNode2 = new Node("", parent.cost);
			emptyEndNode2.parent = capNode;
			capNode.children.add(emptyEndNode2);
		}
		return parent;
	}

	private double getCost(Node parent, String word) {
		String ngram = backTrack(parent, word, nGram.getNGramLength() - 2);
		System.err.println("Generating cost for ngram: "
				+ Arrays.toString(ngram.split(" ")) + "\nngram length "
				+ nGram.getNGramLength());
		double cost = nGram.getCostOfNGram(ngram.split(" "));
		System.err.println("Cost = " + cost);

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
			Node transNode = new Node(emission, parent.cost
					+ getCost(parent, emission));
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
		for (String[] s : fsa.getOutputs()) {
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