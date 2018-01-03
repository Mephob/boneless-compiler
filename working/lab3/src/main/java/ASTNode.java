import java.util.ArrayList;
import java.util.List;

/*
    Jedan cvor sintaksnog stabla.
 */
public class ASTNode {
    public ASTNode parent;
    public List<ASTNode> children;
    public int depth;

    public String value;
    public String name;
    public List<String> names;        //koristi se u listama

    public String tip;
    public String ntip;           //koristi se kod deklaracija nizova
    public boolean l_izraz;
    public List<String> tipovi;

    public int broj_elem;
    public int appearance_line; //za ispis pogreske

    /*
    Konstruktor
     */
    public ASTNode() {
        children = new ArrayList<>();
        tipovi = new ArrayList<>();
        names = new ArrayList<>();
        return;
    }

    /*
        Vraca prvi entry il kajgod iz cijelog tokena
     */
    public String extractFirstFromToken() {
        String[] headSplitter = this.value.split(" ");

        if (headSplitter.length == 1) return headSplitter[0];   //nezavrsni znak
        if (headSplitter.length == 3) return headSplitter[0];   //splitan je token

        //neka greska u inputu
        Util.greska("extractFirstFromToken");
        return null;
    }
}
