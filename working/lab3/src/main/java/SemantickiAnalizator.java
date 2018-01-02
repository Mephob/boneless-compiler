import java.util.*;

import static java.lang.System.exit;

/*
    Tijelo semantickog analizatora. Generira stablo iz inputa.
    Prekida s radom kad god naleti na semanticku pogresku uz ispis na std.out.

    Metode su pretezito gradene na temelju produkcija u BNF gramatici zadanoj na:
        https://raw.githubusercontent.com/fer-ppj/ppj-labosi/master/res/lab3/bnf.txt
    takoder u direktoriju resources/bnf.txt

    bnf_radni.txt sadrzi sve neobradene produkcije

    sve sta sam dolje tu pisao nekako podrazumijeva da nece bit null pointer exceptiona...
    ako ih bude boze pomozi

 */
public class SemantickiAnalizator {
    private TablicaZnakova tabZnak;
    private Stack<Integer> stack;
    private HashMap<String, String> definirane_funkcije; //valjda String string?
    private HashMap<String, String> spomenute_funkcije; //radi provjere na kraju
    // jesu li sve spomenute funkcije definirane

    private ASTNode root;

    //todo: hoda po stablu i kiti ga

    public static void main(String args[]) {

        SemantickiAnalizator sm = new SemantickiAnalizator();
        sm.begin(sm.root);
        sm.finalChecks();

    }

    /*
    Konstruktor.
     */
    private SemantickiAnalizator() {
        this.smonkeTree();
        tabZnak = new TablicaZnakova();
        stack = new Stack<>();
        definirane_funkcije = new HashMap<>();
        spomenute_funkcije = new HashMap<>();
    }

    /*
    Generira stablo iz std.in.
     */
    private void smonkeTree() {
        Scanner sc = new Scanner(System.in);

        ASTNode tempRoot = null;
        ASTNode root = new ASTNode();

        while (sc.hasNext()) {
            String line = sc.nextLine();

            int depth = calculateDepth(line);
            String value = line.trim();

            //todo mozda jos dodat neke korisne brije
            //tipa da odmah based on value puni jel se
            //radi o tipu podatka il
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
        //root.printInorder();

        sc.close();
    }

    /*
    Racuna dubinu cvora.
     */
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

    /*
    Zapocinje semanticku analizu.
     */
    private void begin(ASTNode root) {
        if (root.value.equals(Cons.PRIJEVODNA_JEDINICA.toString())) {
            prijevodnaJedinica(root);
        } else {
            System.err.println("Ili CST ne pocinje NZ <prijevodna_jedinica> ili lose citas input.");
            exit(1);
        }
    }

    //gotovo
    private void prijevodnaJedinica(ASTNode node) {
        int size = node.children.size();

        if (size == 1) {
            vanjskaDeklaracija(node.children.get(0));
        } else if (size == 2){
            prijevodnaJedinica(node.children.get(0));
            vanjskaDeklaracija(node.children.get(1));
        } else {
            Util.greska("prijevodnaJedinica");
        }
    }

    //gotovo
    private void vanjskaDeklaracija(ASTNode node) {
        String value = node.value;
        ASTNode dijete = node.children.get(0);

        if (value.equals(Cons.DEFINICIJA_FUNKCIJE.toString())) {
            definicijaFunkcije(dijete);
        } else if (value.equals(Cons.DEKLARACIJA.toString())) {
            deklaracija(dijete);
        } else {
            Util.greska("vanjskaDeklaracija");
        }
    }

    /*
    Rjesava definiciju funkcije. Ovo ce bit grdo za implementirati...
    <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>
	            | <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>
     */
    private void definicijaFunkcije(ASTNode node) {
        //todo
    }

    /*
    Rjesava deklaraciju zadanu:
    <deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ
     */
    private void deklaracija(ASTNode node) {
        ASTNode prvo = node.children.get(0);
        ASTNode drugo = node.children.get(1);

        imeTipa(prvo);
        drugo.ntip = prvo.tip;
        listaInitDeklaratora(drugo);
    }

    //skoro gotovo
    private void imeTipa(ASTNode node) {
        ASTNode prvo = node.children.get(0);

        if (prvo.value.equals(Cons.SPECIFIKATOR_TIPA.toString())) {
            specifikatorTipa(prvo);
            node.tip = prvo.tip;
        } else if (prvo.value.equals(Cons.KR_CONST.toString())) {
            ASTNode drugo = node.children.get(1);
            specifikatorTipa(drugo);
            if (drugo.tip.equals(Cons.KR_VOID)) {   //sintaksno ispravno al besmisleno
                //todo generate error message
                System.out.println("Dodaj output greske u imeTipa.");
            }
            node.tip = Cons.toConsType(drugo.tip);
        }
    }

    //gotovo
    private void listaInitDeklaratora(ASTNode node) {
        int size = node.children.size();
        if (size == 1) {                            //prva produkcija
            node.children.get(0).ntip = node.ntip;  //pise uz nasljedno svojstvo
            initDeklarator(node.children.get(0));
        } else if (size == 3) {                     //druga produkcija
            ASTNode prvo = node.children.get(0);
            ASTNode trece = node.children.get(2);

            prvo.ntip = node.ntip;          //nasljedna svojstva
            listaInitDeklaratora(prvo);

            trece.ntip = node.ntip;
            initDeklarator(trece);
        } else {
            Util.greska("listaInitDeklaratora");
        }
    }

    /*
    <init_deklarator> ::= <izravni_deklarator>
	            | <izravni_deklarator> OP_PRIDRUZI <inicijalizator>
     */
    private void initDeklarator(ASTNode node) {
        int size = node.children.size();

        if (size == 1) {            //prva produkcija
            ASTNode prvo = node.children.get(0);
            prvo.ntip = node.ntip;
            izravniDeklarator(prvo);

            String tip_prvog = prvo.tip.toString();

            if (tip_prvog.equals(Cons.KR_CONST_CHAR.toString()) ||      //iz uvjeta za konstantne tipove
                    tip_prvog.equals(Cons.KR_CONST_INT.toString()) ||   //i nizove konstanti
                    tip_prvog.equals(Cons.NIZ_CONST_ZNAKOVA.toString()) ||
                    tip_prvog.equals(Cons.NIZ_CONST_BROJEVA.toString())) {
                System.out.println("<init_deklarator> ::= <izravni_deklarator>");
                exit(1);
            }
        } else if (size == 3) {     //druga produkcija
            ASTNode izrDekl = node.children.get(0);
            ASTNode inic = node.children.get(2);

            izrDekl.ntip = node.ntip;
            izravniDeklarator(izrDekl);
            inicijalizator(inic);

            //todo provjere

        } else {
            Util.greska("initDeklarator");
        }
    }

    //gotovo
    private void specifikatorTipa(ASTNode node) {
        String value = node.extractCons();

        if (value.equals(Cons.KR_VOID.toString())) node.tip = Cons.KR_VOID;
        if (value.equals(Cons.KR_INT.toString())) node.tip = Cons.KR_INT;
        if (value.equals(Cons.KR_CHAR.toString())) {
            node.tip = Cons.KR_CHAR;
        } else {
            Util.greska("specifikatorTipa");
        }
    }

    /*
    Provjere na kraju semanticke analize:
        Postoji li main metoda?
        Jesu li sve deklarirane metode negdje i definirane?
     */
    public static void finalChecks() {
        //todo
        //ako ne postoji main i funkcija(void) -> int
            //ispisi "main" i prekini
        //ako ne postoji definicija za svaku deklariranu metodu
            //ispisi "funkcija" i prekini
    }
}

