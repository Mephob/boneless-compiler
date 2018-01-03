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

    welcome to magic(manic?) string fiesta

 */
public class SemantickiAnalizator {
    private TablicaZnakova tabZnak;
    private Stack<Integer> stack;       //drzi duljinu nizova
    private HashMap<String, String> definirane_funkcije; //valjda String string?
    private HashMap<String, String> spomenute_funkcije; //radi provjere na kraju
                        // jesu li sve spomenute funkcije definirane
    private ASTNode root;


    public static void main(String args[]) {

        SemantickiAnalizator sm = new SemantickiAnalizator();
        sm.begin(sm.root);
        sm.finalChecks();

    }

    /*
    Konstruktor.
     */
    private SemantickiAnalizator() {
        this.buildTree();
        tabZnak = new TablicaZnakova();
        stack = new Stack<>();
        definirane_funkcije = new HashMap<>();
        spomenute_funkcije = new HashMap<>();
    }

    private void buildTree() {
        Scanner sc = new Scanner(System.in);

        ASTNode tempRoot = null;
        ASTNode root = new ASTNode();

        while (sc.hasNext()) {
            String line = sc.nextLine();

            int depth = calculateDepth(line);
            String value = line.trim();

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

    private void begin(ASTNode root) {
        if (root.value.equals("PRIJEVODNA_JEDINICA")) {
            prijevodnaJedinica(root);
        } else {
            System.err.println("Ili CST ne pocinje NZ <prijevodna_jedinica> ili lose citas input.");
            exit(1);
        }
    }

    private void terminal(ASTNode node) {
        //value je neki token
        String[] stuff = node.value.split(" ");
        node.tip = stuff[0].trim();
        node.appearance_line = Integer.parseInt(stuff[1].trim());
        node.name = stuff[2].trim();
    }

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

    /* ========== DEFINICIJE I DEKLARACIJE ========== */

    private void vanjskaDeklaracija(ASTNode node) {
        String value = node.value;
        ASTNode dijete = node.children.get(0);

        if (value.equals("DEFINICIJA_FUNKCIJE")) {
            definicijaFunkcije(dijete);
        } else if (value.equals("DEKLARACIJA")) {
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
        int size = node.children.size();

        if (size == 5 && node.children.get(2).value.equals("KR_VOID")) { //prva produkcija
            ASTNode prvi = node.children.get(0);
            ASTNode drugi = node.children.get(1);

            imeTipa(prvi);
            //todo boze pomozi
        }
    }

    private void deklaracija(ASTNode node) {
        ASTNode prvo = node.children.get(0);
        ASTNode drugo = node.children.get(1);

        imeTipa(prvo);
        if (prvo.tip == null) {
            Util.greska("deklaracija");
        }
        drugo.ntip = prvo.tip;
        listaInitDeklaratora(drugo);
    }

    /*
    Ovo nema sanse da radi... nek netko pogleda ko boga vas molim
    Ocekujem greske?1
 */
    private void initDeklarator(ASTNode node) {
        int size = node.children.size();

        if (size == 1) {            //prva produkcija
            ASTNode prvo = node.children.get(0);
            prvo.ntip = node.ntip;
            izravniDeklarator(prvo);

            String tip_prvog = prvo.tip;

            if (tip_prvog.equals("KR_CONST_CHAR") ||      //iz uvjeta za konstantne tipove
                    tip_prvog.equals("KR_CONST_INT") ||   //i nizove konstanti
                    tip_prvog.equals("NIZ_CONST_ZNAKOVA") ||
                    tip_prvog.equals("NIZ_CONST_BROJEVA")) {
                System.out.println("<init_deklarator> ::= <izravni_deklarator>");
                exit(1);
            }
        } else if (size == 3) {     //druga produkcija
            ASTNode izrDekl = node.children.get(0);
            ASTNode inic = node.children.get(2);

            izrDekl.ntip = node.ntip;
            izravniDeklarator(izrDekl);
            inicijalizator(inic);

            if (isTypeT(izrDekl.tip) || isConstQualified(toConsType(izrDekl.tip))) {   //je li T ili const(T)
                inic.tip = izrDekl.tip;
            } else if (isNiz(izrDekl.tip) || isNiz(toConsType(izrDekl.tip))) {
                if (inic.tipovi.size() > 0 && inic.broj_elem <= izrDekl.broj_elem) {
                    for (String tip : inic.tipovi) {
                        if (isTypeT(tip) == false) {
                            //todo
                            System.err.println("Greska kod " + node.value);
                            exit(1);
                        }
                    }
                }
            } else {
                //todo
                System.err.println("Greska kod " + node.value);
                exit(1);
            }
        } else {
            Util.greska("initDeklarator");
        }
    }

    /*
    <izravni_deklarator> ::= IDN
	            | IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA
	            | IDN L_ZAGRADA KR_VOID D_ZAGRADA
	            | IDN L_ZAGRADA <lista_parametara> D_ZAGRADA

	    //todo ovo ima djelokruge, to prokljuviti kasnije ali danas...
     */
    private void izravniDeklarator(ASTNode node) {
        int size = node.children.size();
        if (size == 1) {
            if (node.ntip.equals("KR_VOID")) {
                //todo ispis greske
            }
            node.tip = node.ntip;
        }
    }

    private void imeTipa(ASTNode node) {
        ASTNode prvo = node.children.get(0);

        if (prvo.value.equals("SPECIFIKATOR_TIPA")) {
            specifikatorTipa(prvo);
            node.tip = prvo.tip;
        } else if (prvo.value.equals("KR_CONST")) {
            ASTNode drugo = node.children.get(1);
            specifikatorTipa(drugo);
            if (drugo.tip.equals("KR_VOID")) {   //sintaksno ispravno al besmisleno
                terminal(prvo);

                System.out.format("%s ::= KR_CONST(%d,%s) <specifikator_tipa>",
                        node.value, prvo.appearance_line, prvo.name);
                exit(1);
            }

            node.tip = toConsType(drugo.tip);
        }
    }

    private void inicijalizator (ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);
        if (size == 1 && prvi.value.equals("<izraz_pridruzivanja")) {   //prva produkcija
            izrazPridruzivanja(prvi);
            if (prvi.value.equals("NIZ_ZNAKOVA") || prvi.value.equals("NIZ_CONST_ZNAKOVA")) {
                if (stack.size() == 0) {
                    node.broj_elem = 0;
                } else {
                    node.broj_elem = stack.pop() + 1;
                }

                List<String> tipovi = node.tipovi;
                for (int i = 0, b = node.broj_elem; i < b; i++) {
                    tipovi.add("KR_CHAR");
                }
            } else {
                node.tip = prvi.tip;
            }
        } else if (size == 3) {
            ASTNode lista = node.children.get(1);
            listaIzrazaPridruzivanja(lista);     //druga produkcija, saljemo listu izraza
            node.broj_elem = lista.broj_elem;

            for (String s : lista.tipovi) {
                node.tipovi.add(s);
            }
        } else {
            Util.greska("inicijalizator");
        }
    }

    /* ========== LISTE OVOGA I ONOGA ========== */

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

    private void listaDeklaracija(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<deklaracija>")) {
            deklaracija(prvi);
        } else if (size == 2 && prvi.value.equals("<lista_deklaracija")) {
            listaDeklaracija(prvi);
            deklaracija(node.children.get(1));
        } else {
            Util.greska("listaDeklaracija");
        }
    }

    private void listaArgumenata(ASTNode node) {
        ASTNode prvi = node.children.get(0);

        if (node.children.size() == 1 && prvi.value.equals("<izraz_pridruzivanja>")) {
            izrazPridruzivanja(prvi);
            node.tipovi.add(prvi.tip);
        } else if (node.children.size() == 3 && prvi.value.equals("<lista_argumenata>")) {
            listaArgumenata(prvi);
            izrazPridruzivanja(node.children.get(2));

            for (String s : prvi.tipovi) {
                node.tipovi.add(s);
            }
            node.tipovi.add(node.children.get(2).tip);
        } else {
            Util.greska("listaArgumenata");
        }
    }

    private void listaIzrazaPridruzivanja (ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<izraz_pridruzivanja>")) {
            izrazPridruzivanja(prvi);
            node.broj_elem = 1;
            node.tipovi.add(prvi.tip);
        } else if (size == 3) {
            ASTNode treci = node.children.get(2);

            listaIzrazaPridruzivanja(prvi);
            izrazPridruzivanja(treci);

            for (String s : prvi.tipovi) {
                node.tipovi.add(s);
            }
            node.tipovi.add(treci.tip);

            node.broj_elem = prvi.broj_elem + 1;
        } else {
            Util.greska("listaIzrazaPridruzivanja");
        }
    }

    /* ========== IZRAZI OVI ONI ========== */

    private void izrazPridruzivanja (ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<log_ili_izraz>")) {
            logIliIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<postfiks_izraz")) {
            ASTNode treci = node.children.get(2);
            node.l_izraz = false;

            postfiksIzraz(prvi);

            if (prvi.l_izraz == false) {
                ASTNode drugi = node.children.get(1);
                terminal(drugi);

                System.out.printf("%s ::= %s OP_PRIDRUZI(%d,%s) %s",
                        node.value, drugi.appearance_line, drugi.name, treci.value);
            }
            node.tip = prvi.tip;
            izrazPridruzivanja(treci);

            if (isImplicitlyCastable(treci.tip, node.tip)) {
                ASTNode drugi = node.children.get(1);
                terminal(drugi);

                System.out.printf("%s ::= %s OP_PRIDRUZI(%d,%s) %s",
                        node.value, drugi.appearance_line, drugi.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("izrazPridruzivanja");
        }
    }

    private void logIliIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<log_i_izraz>")) {
            logIIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<log_ili_izraz>")) {

            boolean greskaCast = false;

            node.tip = "KR_INT";
            node.l_izraz = false;

            logIliIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children
            logIIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_ILI(%d,%s) %s",
                        node.value, prvi.value, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }

        } else {
            Util.greska("log ili izraz (LogIliIzraz)");
        }
    }

    private void logIIzraz(ASTNode node) {
            int size = node.children.size();
            ASTNode prvi = node.children.get(0);

            if (size == 1 && prvi.value.equals("<bin_ili_izraz>")) {
                binIliIzraz(prvi);
                node.tip = prvi.tip;
                node.l_izraz = prvi.l_izraz;
            } else if (size == 3 && prvi.value.equals("<log_i_izraz>")) {
                node.tip = "KR_INT";
                node.l_izraz = false;
                boolean greskaCast = false;

                logIIzraz(prvi);
                if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                    greskaCast = true;
                }

                ASTNode treci = node.children.get(2);
                binIliIzraz(treci);
                if(isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                    greskaCast = true;
                }

                if (greskaCast) {
                    ASTNode srednji = node.children.get(1);
                    terminal(srednji);

                    System.out.printf("%s ::= %s OP_I(%d,%s) %s",
                            node.value, prvi.value, srednji.appearance_line, srednji.name, treci.value);
                    exit(1);
                }
            } else {
                Util.greska("logicki i izraz (logIIzraz)");
            }
    }

    private void binIliIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<bin_xili_izraz>")) {
            binXiliIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;

        } else if (size == 3 && prvi.value.equals("<bin_ili_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            binIliIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            binXiliIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_BIN_ILI(%d,%s) %s",
                        node.value, prvi.value, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }

        } else {
            Util.greska("bin ili izraz (binIliIzraz");
        }
    }

    private void binXiliIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<bin_i_izraz>")) {
            binIIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<bin_xili_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            binXiliIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            binIIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_BIN_XILI(%d,%s) %s",
                        node.value, prvi.value, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }

        } else {
            Util.greska("bin xili izraz (binXiliIzraz");
        }
    }

    private void binIIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<jednakosni_izraz>")) {
            jednakosniIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<bin_i_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            binIIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            jednakosniIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_BIN_I(%d,%s) %s",
                        node.value, prvi.value, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("bin i izraz (binIIzraz)");
        }
    }

    private void jednakosniIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<odnosni_izraz>")) {
            odnosniIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<jednakosni_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            jednakosniIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            odnosniIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("jednakosniIzraz");
        }
    }

    private void odnosniIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<aditivni_izraz>")) {
            aditivniIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<odnosni_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            odnosniIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            aditivniIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("odnosniIzraz");
        }
    }

    private void aditivniIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<multiplikativni_izraz>")) {
            multiplikativniIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<aditivni_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            aditivniIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            multiplikativniIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("aditivniIzraz");
        }
    }

    private void multiplikativniIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<cast_izraz>")) {
            castIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<multiplikativni_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greskaCast = false;

            multiplikativniIzraz(prvi);
            if (isImplicitlyCastable(prvi.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            castIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT") == false) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.appearance_line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("aditivniIzraz");
        }
    }

    private void specifikatorTipa(ASTNode node) {
        String value = node.extractFirstFromToken();

        if (value.equals("KR_VOID")) node.tip = "KR_VOID";
        if (value.equals("KR_INT")) node.tip = "KR_INT";
        if (value.equals("KR_CHAR")) {
            node.tip = "KR_CHAR";
        } else {
            Util.greska("specifikatorTipa");
        }
    }

    private void castIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<unarni_izraz>")) {
            unarniIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 4) {
            ASTNode drugi = node.children.get(1);
            ASTNode zadnji = node.children.get(3);
            boolean greska = false;

            imeTipa(drugi);
            node.tip = drugi.tip;
            node.l_izraz = false;

            castIzraz(zadnji);
            if (isExplicitlyCastable(zadnji.tip, drugi.tip)) {
                greska = true;
            }

            if (greska) {
                ASTNode lijeva = node.children.get(0);
                terminal(lijeva);
                ASTNode desna = node.children.get(2);
                terminal(desna);

                System.out.printf("%s ::= %s(%d,%s) %s %s(%d,%s) %s",
                        node.value, lijeva.tip, lijeva.appearance_line, lijeva.name,
                        drugi.value, desna.tip, desna.appearance_line, desna.name, zadnji.value);
                exit(1);
            }
        } else {
            Util.greska("castIzraz");
        }
    }

    private void unarniIzraz(ASTNode node) {
        ASTNode prvi = node.children.get(0);
        ASTNode drugi = node.children.get(1);

        if (prvi.value.equals("<postfiks_izraz>")) {
            postfiksIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;

        } else if (prvi.value.equals("<unarni_operator>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;

            //za unarni operator ne treba nikakva provjera
            castIzraz(drugi);
            if (isImplicitlyCastable(drugi.tip, "KR_INT")) {
                System.out.printf("%s ::= %s %s",
                        node.value, prvi.value, drugi.value);
                exit(1);
            }
        } else if (drugi.value.equals("<unarni_izraz>")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            boolean greska = false;

            unarniIzraz(drugi);
            if (drugi.l_izraz != true) {
                greska = true;
            }
            if (isImplicitlyCastable(drugi.tip, "KR_INT")) {
                greska = true;
            }
            if (greska) {
                //prvi je operator
                terminal(prvi);
                System.out.printf("%s ::= %s(%d,%s) %s",
                        node.value, prvi.tip, prvi.appearance_line, prvi.name, drugi.value);
                exit(1);
            }
        } else {
            Util.greska("unarniIzraz");
        }
    }

    /*
    <postfiks_izraz> ::= <primarni_izraz>
	        | <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
	        | <postfiks_izraz> L_ZAGRADA D_ZAGRADA
	        | <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
	        | <postfiks_izraz> OP_INC
	        | <postfiks_izraz> OP_DEC
     */
    private void postfiksIzraz(ASTNode node) {

    }
    /*
    Provjere na kraju semanticke analize:
        Postoji li main metoda?
        Jesu li sve deklarirane metode negdje i definirane?
     */
    private static void finalChecks() {
        //todo
        //ako ne postoji main i funkcija(void) -> int
            //ispisi "main" i prekini
        //ako ne postoji definicija za svaku deklariranu metodu
            //ispisi "funkcija" i prekini
    }

    private static boolean isTypeT(String type) {
        String[] tees = new String[] { "KR_INT", "KR_CONST_INT", "KR_CHAR", "KR_CONST_CHAR" };
        for (String s : tees) {
            if (type.equals(s)) return true;
        }
        return false;
    }

    private static String toConsType (String type) {
        if (type.equals("KR_CONST_INT") || type.equals("KR_CONST_CHAR")) return type;
        if (type.equals("KR_INT")) return "KR_CONST_INT";
        if (type.equals("KR_CHAR")) return "KR_CONST_CHAR";

        //nesto ne valja jel
        return "GRESKA toConsType";
    }

    private static boolean isNiz (String type) {
        String[] tees = new String[] { "NIZ_BROJEVA", "NIZ_ZNAKOVA", "NIZ_CONST_BROJEVA", "NIZ_CONST_ZNAKOVA"};
        for (String s : tees) {
            if (type.equals(s)) return true;
        }
        return false;
    }

    private static boolean isConst(String type) {
        return (type.equals("KR_CONST_CHAR") || type.equals("KR_CONST_INT"));
    }

    private static boolean isConstQualified(String type) {
        return (type.equals("KR_INT") || type.equals("KR_CHAR"));
    }

    private static boolean isImplicitlyCastable (String from, String to) {
        if (from.equals(to)) return true;

        if (from.equals("KR_INT") && to.equals("KR_CONST_INT")
            || from.equals("KR_CONST_INT") && to.equals("KR_INT")) return true;

        if (from.equals("KR_CHAR") && to.equals("KR_CONST_CHAR")
            || from.equals("KR_CONST_CHAR") && to.equals("KR_CHAR")) return true;

        if ((from.equals("KR_CHAR") || from.equals("KR_CONST_CHAR"))
            && (to.equals("KR_INT") || to.equals("KR_CONST_INT"))) return true;

        //niz nije const kvalificiran!
        if (from.equals("NIZ_BROJEVA") && to.equals("NIZ_CONST_BROJEVA")) return true;
        if (from.equals("NIZ_ZNAKOVA") && to.equals("NIZ_CONST_ZNAKOVA")) return true;

        return false;
        //nikad vise ovakvo nesto pisat molim te boze
    }

    private static boolean isExplicitlyCastable(String from, String to) {
        if (isTypeT(from) && isTypeT(to)) return true;
        return false;
    }
}