import java.util.*;

public class SemantickiAnalizator {
    private TablicaZnakova tabZnak;
    private Stack<Integer> stack;
    private HashMap<String, String> definirane_funkcije; //valjda String string?
    private HashMap<String, String> spomenute_funkcije; //radi provjere na kraju
    // jesu li sve spomenute funkcije definirane

    private ASTNode root;

    //todo: gradi stablo iz inputa
    //todo: hoda po stablu i kiti ga

    public static void main(String args[]) {

        SemantickiAnalizator sm = new SemantickiAnalizator();

    }

    //konstruktor
    public SemantickiAnalizator() {
        this.smonkeTree();
        tabZnak = new TablicaZnakova();
        stack = new Stack<>();
        definirane_funkcije = new HashMap<>();
        spomenute_funkcije = new HashMap<>();
    }

    //generira stablo kroz std.in
    public void smonkeTree() {
        Scanner sc = new Scanner(System.in);
        List<String> lista = new ArrayList<>();

        ASTNode tempRoot = null;
        ASTNode root = new ASTNode();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            lista.add(line);

            int depth = calculateDepth(line);
            String value = line.trim();

            //tek smo poceli hehe
            if (tempRoot == null) {
                root.value = value;
                root.depth = 0;
                tempRoot = root;
            } else {
                while (tempRoot.depth >= depth) {
                    tempRoot = tempRoot.parent;
                }
                ASTNode node = new ASTNode();
                node.value = value;
                node.depth = depth;

                node.parent = tempRoot;
                tempRoot.children.add(node);
                tempRoot = node;
            }
        }
        this.root = root;
        root.printInorder();

        sc.close();
    }

    private static int calculateDepth(String line) {
        char[] charray = line.toCharArray();
        int ctr = 0;

        for (char c : charray) {
            if (c == ' ') {
                ctr++;
            } else {
                break;
            }
        }

        return ctr;
    }

    ;
}

