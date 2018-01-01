import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    public ASTNode parent;
    public List<ASTNode> children;
    public String value;
    public String name;
    public String names;

    public int depth;
    public Cons tip;
    public Cons ntip;
    public Cons log_izraz;
    public int broj_elem;
    public int appearance_line;

    public ASTNode() {
        children = new ArrayList<>();

        return;
    }

    //ispisuje stablo u inorderu
    public void printInorder() {
        //todo
    }
}
