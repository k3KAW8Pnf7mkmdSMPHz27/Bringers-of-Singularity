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
    //public static final String[] TRANSITIONS = { EMPTY_PUNCT, ",COMMA ", ".PERIOD ", "?QMARK ", "!EXCL " };
    //public static final String[] TRANSITIONS = { EMPTY_PUNCT, ",COMMA ", ".PERIOD "};
    public static final String[] TRANSITIONS = { EMPTY_PUNCT, ".PERIOD "};
    public static final String[] POSTPROCESSES = { " ", ", ", ". ", "? ", "! " };
    public static final int TRANSITION_COUNT = TRANSITIONS.length;
    //public static final int STATES_COUNT = 2;
    private final static String END_OF_LINE = "¿EOL";


    Vector<String[]> outputs;
    NGramWrapper nGram;

    private double lowestValue = Double.MAX_VALUE;
    private double highestValue = Double.MIN_VALUE;

    private boolean optimalNotFound = true;

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
        Node startNode = new Node("START ", 1.0);
        root.children.add(startNode);
        startNode.parent = root;
        Long time = System.currentTimeMillis();
        //root = generateNodes(s, root);
        //It is a valid assumption that start of the line is START.

        /*
        startNode = generateNodes(Arrays.copyOfRange(s, 1, s.length), startNode, null);
        time = System.currentTimeMillis() - time;
        System.err.println("Generated nodes in "+time+" msec.");
        time = System.currentTimeMillis();
        generateOutputs(root, outputs);
        time = System.currentTimeMillis() - time;
        System.err.println("Generated output in "+time+" msec.");
        System.err.println("Highest value = " +highestValue);
        System.err.println("Lowest value = "+lowestValue);
        highestValue = Integer.MIN_VALUE;
        lowestValue = Integer.MAX_VALUE;

        */
        //time = System.currentTimeMillis();
        startNode = new Node("START ", 1.0);
        //Node backTrackNode = usePriorityQueue(Arrays.copyOfRange(s, 1, s.length), startNode);

        usePriorityQueue(Arrays.copyOfRange(s, 1, s.length), startNode);

        //time = System.currentTimeMillis() - time;
        //System.err.println("Generated priority queue in "+time+" msec.");
        /*
        StringBuilder test = new StringBuilder();
        Node node = findHighestValuedChild(root);
        System.out.println();
        backTrackFromChild(node, test);
        System.out.println(test.toString());
        System.out.println();
        */

    }
    private void usePriorityQueue(String[] s, Node startNode) {
        PriorityQueue<PriorityQueueElement> pq = new PriorityQueue<>();
        pq.add(new PriorityQueueElement(s, startNode));
        System.err.println("Printing priority queue");
        //Node result = null;
        while(optimalNotFound&&(!pq.isEmpty())) {
            PriorityQueueElement pqe = pq.poll();
            //result = generateNodes(pqe.s, pqe.self, pq);
            generateNodes(pqe.s, pqe.self, pq);
        }
        //return result;
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
            outputs.add(s.split(" "));
        } else {
            for (int i = 0; i < node.children.size(); i++) {
                generateOutputs(node.children.elementAt(i), outputs);
            }
        }
    }
    private Node findHighestValuedChild(Node root) {
        if(root.children.size()==0) {
            return root;
        }
        double value = Double.NEGATIVE_INFINITY;
        int index = -1;
        for(int i = 0; i < root.children.size(); i++) {
            Node node = findHighestValuedChild(root.children.get(i));
            if(node.cost>value) {
                value = node.cost;
                index = i;
            }
        }
        return root.children.get(index);
    }
    private void backTrackFromChild(Node n, StringBuilder sb) {
        if(n.parent!=null) {
            backTrackFromChild(n.parent, sb);
        }
        if(n.value.equals(EMPTY_PUNCT)) {
            //sb.append(' ');
        } else {
            sb.append(n.toString());
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
    private Node generateNodes(String[] s, Node parent, PriorityQueue<PriorityQueueElement> pq) {
        if((pq!=null)&&(s==null)) {
            optimalNotFound=false;
            StringBuilder sb = new StringBuilder();
            backTrackFromChild(parent, sb);
            //System.out.println(sb.toString()+"\t"+parent.cost);
            System.out.println(sb.toString());
            return parent;
        }


        if(s[0].equals(END_OF_LINE)) {
            return parent;
        }


        String unCapWord = deCapitalizeWord(s[0]);
        Node unCapNode = new Node(unCapWord + " ", parent.cost * getCost(parent, unCapWord));
        unCapNode.parent = parent;

        String capWord = capitalizeWord(s[0]);
        Node capNode = new Node(capWord + " ", parent.cost * getCost(parent, capWord));
        capNode.parent = parent;

        generateTransitions(unCapNode);
        generateTransitions(capNode);

        parent.children.add(capNode);
        parent.children.add(unCapNode);

        //Ska för all del vara > 0....
        if (s.length > 1) { //Borde vara s.length < NGramLength right .... ? nope...
            for (int i = 0; i < unCapNode.children.size(); i++) {
                if (pq == null) {
                    unCapNode.children.set(i, generateNodes(Arrays.copyOfRange(s, 1, s.length), unCapNode.children.get(i), null));
                    capNode.children.set(i, generateNodes(Arrays.copyOfRange(s, 1, s.length), capNode.children.get(i), null));
                } else {
                    pq.offer(new PriorityQueueElement(Arrays.copyOfRange(s, 1, s.length), unCapNode.children.get(i)));
                    pq.offer(new PriorityQueueElement(Arrays.copyOfRange(s, 1, s.length), capNode.children.get(i)));
                }
            }

        } else if(pq!=null) {
            for (int i = 0; i < unCapNode.children.size(); i++) {
                pq.offer(new PriorityQueueElement(null, unCapNode.children.get(i)));
                pq.offer(new PriorityQueueElement(null, capNode.children.get(i)));
            }
        } else {
            for (int i = 0; i < unCapNode.children.size(); i++) {
                double unCapNodeCost = unCapNode.children.get(i).cost;
                if(unCapNodeCost>highestValue) {
                    highestValue=unCapNodeCost;
                    StringBuilder sb = new StringBuilder();
                    backTrackFromChild(unCapNode.children.get(i), sb);
                    System.err.println(sb.toString()+"\t"+unCapNodeCost);
                }
                if(unCapNodeCost<lowestValue) {
                    lowestValue=unCapNodeCost;
                }
            }
            for (int i = 0; i < capNode.children.size(); i++) {
                double nodeCost = capNode.children.get(i).cost;
                if(nodeCost>highestValue) {
                    highestValue=nodeCost;
                    StringBuilder sb = new StringBuilder();
                    backTrackFromChild(capNode.children.get(i), sb);
                    System.err.println(sb.toString()+"\t"+nodeCost);
                }
                if(nodeCost<lowestValue) {
                    lowestValue=nodeCost;
                }
            }
        }

        /*
        if(s[0].equals(END_OF_LINE)&&(pq!=null)) { //Borde vara s[NGramLength - 1] (typ)
            optimalNotFound=false;
            StringBuilder test = new StringBuilder();
            backTrackFromChild(parent, test);
            test.append(END_OF_LINE);
            System.out.println(test+"\t"+parent.cost);

            return parent;
        }
         */


        return parent;
    }
    class PriorityQueueElement implements Comparable<PriorityQueueElement> {
        String[] s;
        Node self;
        public PriorityQueueElement(String[] s, Node self) {
            this.s = s;
            this.self = self;
        }

        @Override
        public int compareTo(PriorityQueueElement pqe) {
            return self.compareTo(pqe.self);
        }
    }
    private double getCost(Node parent, String word) {
        String[] ngram = (backTrack(parent, word, nGram.getNGramLength() - 2)
                .split(" "));
        // System.err.println(Arrays.toString(ngram.split(" ")));
        double cost = Double.NaN;
        if (ngram.length >= 0) { //Varför större än 1 istället för större än 0 ... ?
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
                //transNode = new Node(emission, parent.cost*0.5); //För att du vill ha en kostnad för att inte ha en punctuation ?
                transNode = new Node(emission, parent.cost);
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

        HyperStringFSA3 fsa = new HyperStringFSA3(words, ngw);
        for (String[] s : fsa.outputs) {
            System.out.println(Arrays.toString(s));
        }

    }

    private class Node implements Comparable<Node> {
        String value;
        double cost;
        Node parent;
        Vector<Node> children;

        public Node(String value, double cost) {
            children = new Vector<Node>();
            this.cost = cost;
            this.value = value;
        }

        @Override
        public int compareTo(Node n2) {
            if(cost<n2.cost) {
                return 1;
            } else if(cost>n2.cost) {
                return -1;
            } else {
                return 0;
            }
        }

        public String toString() {
            return value;
        }
    }

}