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
    public String names;        //koristi se u listama

    public Cons tip;
    public Cons ntip;           //koristi se kod deklaracija nizova
    public Cons l_izraz;

    public int broj_elem;
    public int appearance_line; //za ispis pogreske

    /*
    Konstruktor
     */
    public ASTNode() {
        children = new ArrayList<>();
        return;
    }

    /*
        Vraca kljucnu rijec il kajgod iz cijelog tokena
     */
    public String extractCons() {
        String[] headSplitter = this.value.split(" ");

        if (headSplitter.length == 1) return headSplitter[0];   //nezavrsni znak
        if (headSplitter.length == 3) return headSplitter[0];   //splitan je token

        //neka greska u inputu
        Util.greska("extractCons");
        return null;
    }

    //ispisuje stablo u inorderu radi kontrole
    //ovo mozda u neki drugi razred nabiti
    //nije trenutno pretjerano potrebno
    public void printInorder() {
        //todo
    }
}
