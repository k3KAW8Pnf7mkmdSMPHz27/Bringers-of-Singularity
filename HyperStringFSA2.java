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
	public static final String[] TRANSITIONS = { " ", " ,COMMA ", " .PERIOD ",
			" ?QMARK ", " !EXCL " };
	public static final String[] POSTPROCESSES = { " ", ", ", ". ", "? ", "! " };
	public static final int TRANSITION_COUNT = 5;
	public static final int STATES_COUNT = 2;

	Vector<String[]> outputs;

	/**
	 * Constructor creating a FSA based on the specified String array consisting
	 * of words
	 * 
	 * @param s
	 *            Array of words
	 * 
	 */
	public HyperStringFSA2(String[] s) {
		outputs = new Vector<String[]>();
		constructFSA(s, outputs);
	}

	/**
	 * Construct the FSA with all possible outputs
	 * 
	 * @param s
	 *            Array of words
	 * 
	 * @param outputs
	 *            Vector holding all possible outputs
	 * 
	 */
	private void constructFSA(String[] s, Vector<String[]> outputs) {
		Node root = new Node("");
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
			String s = backTrack(node, "");
			//System.out.println(s);
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
	private String backTrack(Node node, String s) {
		if (node.parent == null) {
			return node.value + s.substring(0, s.length() - 1);
		} else {
			return backTrack(node.parent, node.value + s);
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
		Node unCapNode = new Node(deCapitalizeWord(s[0]));
		unCapNode.parent = parent;
		generateTransitions(unCapNode);

		Node capNode = new Node(capitalizeWord(s[0]));
		capNode.parent = parent;
		generateTransitions(capNode);
		parent.children.add(capNode);
		parent.children.add(unCapNode);
		if (s.length != 1) {
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
		}
		return parent;
	}

	/**
	 * Generate the possible punctuation transitions
	 * 
	 * @param node
	 */
	private void generateTransitions(Node node) {
		for (int i = 0; i < TRANSITION_COUNT; i++) {
			Node transNode = new Node(TRANSITIONS[i]);
			transNode.parent = node;
			node.children.add(transNode);
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
		HyperStringFSA2 fsa = new HyperStringFSA2(words);
		for (String[] s : fsa.outputs) {
			System.out.println(Arrays.toString(s));
		}
	}

	private class Node {
		String value;
		Node parent;
		Vector<Node> children;

		public Node(String value) {
			children = new Vector<Node>();
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}
}
