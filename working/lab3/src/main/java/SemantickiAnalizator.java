import java.util.*;
import static java.lang.System.exit;
import static java.lang.System.lineSeparator;

/*
    Tijelo semantickog analizatora. Generira stablo iz inputa.
    Prekida s radom kad god naleti na semanticku pogresku uz ispis na std.out.

    Metode su pretezito gradene na temelju produkcija u BNF gramatici zadanoj na:
        https://raw.githubusercontent.com/fer-ppj/ppj-labosi/master/res/lab3/bnf.txt
    takoder u direktoriju resources/bnf.txt

    bnf_radni.txt sadrzi sve neobradene produkcije

    sve sta sam dolje tu pisao nekako podrazumijeva da nece bit null pointer exceptiona...
    ako ih bude boze pomozi
    ehehehehehehehe ipak ih ima hehehehehehehehehehehehe

    welcome to magic(manic?) string fiesta
izravni
 */
public class SemantickiAnalizator {
    private TablicaZnakova tablicaZnakova;
    private Stack<Integer> arrLenStack;                     //drzi duljinu nizova
    private TablicaZnakova tablicaDefFunkcija;
    private TablicaZnakova deklariraneFunkcije;
    private ASTNode root;


    public static void main(String args[]) {

        SemantickiAnalizator sm = new SemantickiAnalizator();
        sm.begin(sm.root);
        sm.finalChecks();

    }

    //GOTOVO
    private SemantickiAnalizator() {
        this.buildTree();
        tablicaZnakova = new TablicaZnakova();
        arrLenStack = new Stack<>();
        tablicaDefFunkcija = new TablicaZnakova();
        deklariraneFunkcije = new TablicaZnakova();
    }

    //GOTOVO
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

    //GOTOVO
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

    //GOTOVO
    private void begin(ASTNode root) {
        if (root.value.equals("<prijevodna_jedinica>")) {
            prijevodnaJedinica(root);
        } else {
            System.err.println("Ili CST ne pocinje NZ <prijevodna_jedinica> ili lose citas input.");
            exit(1);
        }
    }

    //GOTOVO
    private void terminal(ASTNode node) {
        //value je neki token
        String[] stuff = node.value.split(" ");
        node.tip = stuff[0].trim();
        node.line = Integer.parseInt(stuff[1].trim());
        node.name = stuff[2].trim();
    }

    //GOTOVO
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

    //GOTOVO
    private void vanjskaDeklaracija(ASTNode node) {
        String value = node.value;
        ASTNode dijete = node.children.get(0);

        if (dijete.value.equals("<definicija_funkcije>")) {
            definicijaFunkcije(dijete);
        } else if (dijete.value.equals("<deklaracija>")) {
            deklaracija(dijete);
        } else {
            Util.greska("vanjskaDeklaracija");
        }
    }

    //GOTOVO
    private void definicijaFunkcije(ASTNode node) {
        int size = node.children.size();

        ASTNode prvi = node.children.get(0);
        ASTNode drugi = node.children.get(1);
        ASTNode treci = node.children.get(3);
        imeTipa(prvi);
        terminal(drugi);
        boolean greska = false;
        tablicaZnakova.openScope();

        if (tablicaDefFunkcija.hasElem(drugi.name)) {
            greska = true;
        } else if (prvi.tip.equals(toConsType(prvi.tip))) {
            greska = true;
        }

        if (size == 6 && !treci.value.equals("<lista_parametara>")) { //prva produkcija

            ASTNode peti = node.children.get(4);

            if (deklariraneFunkcije.hasElem(drugi.name)) {
                List<ASTNode> sveDeklarirane = deklariraneFunkcije.getNodeList(drugi.name);
                //todo ovo ne znam jel dobro...
                //provjerava se globalni djelokrug, tak da ako postoji to ce bit valjda prvi u listi
                ASTNode prvaDeklarirana = sveDeklarirane.get(0);
                if (!prvaDeklarirana.tip.equals("KR_VOID") || !sameTypeLists(prvaDeklarirana.tipovi, prvi.tipovi)) {
                    greska = true;
                }
            }
            if (greska) {
                ASTNode op1 = drugi;
                ASTNode op2 = node.children.get(2); terminal(op2);
                ASTNode op3 = node.children.get(3); terminal(op3);
                ASTNode op4 = node.children.get(4); terminal(op4);

                System.out.printf("<definicija_funkcije> ::= <ime_tipa> IDN(%d,%s) " +
                                "L_ZAGRADA(%d,%s) KR_VOID(%d,%s) D_ZAGRADA(%d,%s) <slozena_naredba>",
                        op1.line, op1.name, op2.line, op2.name, op3.line, op3.name, op4.line, op4.name);
                exit(1);
            }
            tablicaDefFunkcija.addElem(drugi.name, drugi);  //mozda referenca na neki drugi cvor potrebna? nisam siguran
            deklariraneFunkcije.addElem(drugi.name, drugi);

            slozenaNaredba(node.children.get(4), null);
        } else if (size == 6 && treci.value.equals("<lista_parametara>")) {
            ASTNode listaPar = treci;
            ASTNode sloNar = node.children.get(5);

            if (greska) {
                getOut(node);
            }

            listaParametara(listaPar);
            if (deklariraneFunkcije.hasElem(drugi.name)) {
                List<ASTNode> sveDeklarirane = deklariraneFunkcije.getNodeList(drugi.name);
                ASTNode prvaDeklarirana = sveDeklarirane.get(0);
                if (!prvaDeklarirana.tip.equals(prvi.tip) || !sameTypeLists(prvaDeklarirana.tipovi, listaPar.tipovi)) {
                    greska = true;
                }
                else drugi.tip = wrapTip(prvi.tip, listaPar.tipovi);
            }
            if (greska) {
                getOut(node);
            }

            tablicaDefFunkcija.addElem(drugi.name, node);  //mozda referenca na neki drugi cvor potrebna? nisam siguran
            deklariraneFunkcije.addElem(drugi.name, drugi);

            List<String> parametri = new ArrayList<>();
            listCopyPasta(listaPar.tipovi, parametri);
            listCopyPasta(listaPar.names, parametri);
            node.tip = wrapTip(null, parametri);
            slozenaNaredba(sloNar, parametri);
        } else Util.greska("definicijaFunkcije");

        tablicaZnakova.closeScope();

        if (greska) {
            //todo output
            System.out.printf("GRESKA BOKTE MAZO");
            exit(1);
        }
    }

    private void getOut(ASTNode node) {
        ASTNode drugi = node.children.get(1); terminal(drugi);
        ASTNode op2 = node.children.get(2); terminal(op2);
        ASTNode op4 = node.children.get(4); terminal(op4);

        System.out.printf("<definicija_funkcije> ::= <ime_tipa> IDN(%d,%s) " +
                        "L_ZAGRADA(%d,%s) <lista_parametara> D_ZAGRADA(%d,%s) <slozena_naredba>",
                drugi.line, drugi.name, op2.line, op2.name, op4.line, op4.name);
        exit(1);
    }

    //GOTOVO
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

    //GOTOVO
    private void deklaracijaParametra(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);
        if (prvi.value.equals("<ime_tipa>") == false) Util.greska("deklaracijaParametra");

        imeTipa(prvi);
        ASTNode drugi = node.children.get(1);
        terminal(drugi);
        if (prvi.tip.equals("KR_VOID")) {
            if (size == 2) {
                System.out.printf("%s ::= %s IDN(%d,%s)",
                        node.value, prvi.value, drugi.line, drugi.name);
            } else {
                ASTNode treci = node.children.get(2);
                terminal(treci);
                ASTNode cet = node.children.get(3);
                terminal(cet);

                System.out.printf("%s ::= %s IDN(%d,%s) L_UGL_ZAGRADA(%d,%s) D_UGL_ZAGRADA(%d,%s)",
                        node.value, prvi.value, drugi.line, drugi.name,
                        treci.line, treci.name, cet.line, cet.name);
            }
            exit(1);
        }
        node.name = drugi.name; //po IDN se zove
        if (size == 2) {
            node.tip = prvi.tip;
        } else if (size == 4) {
            node.tip = toArrayType(prvi.tip);
        } else {
            Util.greska("deklaracijaParametra");
        }
    }

    //GOTOVO sad bi moglo bit oke
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
            boolean greska = false;

            izrDekl.ntip = node.ntip;
            izravniDeklarator(izrDekl);
            inicijalizator(inic);

            //za ovaj dio nisam siguran tbfh a valjda je ok
            if (isTypeT(izrDekl.tip) || isConst(izrDekl.tip)) {   //je li T ili const(T)
                if (!isCastableToT(inic.tip)) {
                    greska = true;
                }
            } else if (isNiz(izrDekl.tip) || isNiz(toConsType(izrDekl.tip))) {
                if (inic.broj_elem > izrDekl.broj_elem) {
                    greska = true;
                }
                for (String s : inic.tipovi) {
                    if (!isTypeT(s)) {
                        greska = true;
                    }
                }
            }
            if (greska) {
                ASTNode eh = node.children.get(1);
                terminal(eh);
                System.out.printf("%s ::= %s OP_PRIDRUZI(%d,%s) %s",
                        node.value, izrDekl.value, eh.line, eh.name, inic.value);
                exit(1);
            }
        } else {
            Util.greska("initDeklarator");
        }
    }

    private void bigGreska(ASTNode node) {
        ASTNode op1 = node.children.get(0); terminal(op1);
        ASTNode op2 = node.children.get(1); terminal(op2);
        ASTNode op3 = node.children.get(2); terminal(op3);
        ASTNode op4 = node.children.get(3); terminal(op4);

        System.out.printf("izravni_deklarator> ::= IDN(%d,%s) L_UGL_ZAGRADA(%d,%s) BROJ(%d,%s) D_UGL_ZAGRADA(%d,%s)",
                op1.line, op1.name, op2.line, op2.name, op3.line, op3.name, op4.line, op4.name);
        exit(1);
    }

    /*
    <izravni_deklarator> ::= IDN
	        | IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA
	        | IDN L_ZAGRADA KR_VOID D_ZAGRADA
	        | IDN L_ZAGRADA <lista_parametara> D_ZAGRADA
     */

    private void izravniDeklarator(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);
        terminal(prvi);

        if (size == 1) {
            if (node.ntip.equals("KR_VOID") || tablicaZnakova.hasElemLocally(prvi.name)) {
                System.out.printf("<izravni_deklarator> ::= IDN(%d,%s)",
                        prvi.line, prvi.name);
                exit(1);
            }
            node.tip = node.ntip;
            node.l_izraz = true;
            if (toConsType(node.tip).equals(node.tip)) node.l_izraz = false;
            tablicaZnakova.addElem(prvi.name, prvi);
        } else if (node.children.size() == 4) {
            ASTNode drugi = node.children.get(1);
            ASTNode treci = node.children.get(2);
            ASTNode cetvrti = node.children.get(3);
            boolean greska = false;

            terminal(drugi);
            terminal(treci);
            terminal(cetvrti);

            if (drugi.tip.equals("L_UGL_ZAGRADA")) {
                if (node.ntip.equals("KR_VOID") || tablicaZnakova.hasElemLocally(prvi.name)) {
                    bigGreska(node);
                }
                //provjeri je li zavrsni i ako je onda je li BROJ
                if (treci.value.startsWith("<") && treci.value.endsWith(">")) {
                    bigGreska(node);
                } else {
                    terminal(treci);

                    if (!treci.tip.equals("BROJ")) {
                        bigGreska(node);
                    }
                }

                int intBuff = Integer.parseInt(treci.name);
                if (intBuff <= 0 || intBuff > 1024) {
                    bigGreska(node);
                }

                node.tip = toArrayType(node.ntip);
                node.broj_elem = intBuff;

                //TODO<ne znam da li ovo ispod tu treba, neki kurac da arrays nisu konst ali oni unutra jesu<
                //node.l_izraz = true;
                //if (toConsType(node.tip).equals(node.tip)) node.l_izraz = false;
                //tablicaZnakova.addElem(prvi.name, prvi);
                //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            } else if (drugi.tip.equals("L_ZAGRADA")) {
                if (treci.value.startsWith("<") && treci.value.endsWith(">")) {
                    if (!treci.value.equals("<lista_parametara>")) {    //predzadnja produkcija
                        //TODO<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        System.out.printf("<izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA",
                                prvi.line, prvi.name);
                        exit(1);
                    }
                    listaParametara(treci);

                    List<String> paramList = new ArrayList<>();
                    listCopyPasta(treci.tipovi, paramList);
                    String condition = wrapTip(node.ntip, paramList);

                    if (deklariraneFunkcije.hasElem(prvi.name)) {
                        ASTNode zadnja = deklariraneFunkcije.getIDN(prvi.name);
                        if (!zadnja.tip.equals(condition)) {
                            System.out.println("Greska");
                            exit(1);
                        }
                    }
                    deklariraneFunkcije.addElem(prvi.name, node);
                    tablicaZnakova.addElem(prvi.name, node);
                    node.tip = condition;

                } else {
                    terminal(treci);

                    if (!treci.tip.equals("KR_VOID")) {             //zadnja produkcija
                        //TODO<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        System.out.printf("<izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA",
                                prvi.line, prvi.name);
                        exit(1);
                    }

                    List<String> voidList = new ArrayList<>();
                    voidList.add("KR_VOID");
                    String condition = wrapTip(node.ntip, voidList);

                    if (deklariraneFunkcije.hasElem(prvi.name)) {
                        ASTNode zadnja = deklariraneFunkcije.getIDN(prvi.name);
                        if (!zadnja.tip.equals(condition)) {
                            System.out.println("Greska");
                            exit(1);
                        }
                    }
                    deklariraneFunkcije.addElem(prvi.name, node);
                    tablicaZnakova.addElem(prvi.name, node);
                    node.tip = condition;
                }
            } else {
                Util.greska("imeTipa");
            }
        }
    }

    //GOTOVO
    private void imeTipa(ASTNode node) {
        ASTNode prvo = node.children.get(0);

        if (prvo.value.equals("<specifikator_tipa>")) {
            specifikatorTipa(prvo);
            node.tip = prvo.tip;
        } else if (prvo.extractFirstFromToken().equals("KR_CONST")) {       //KR_CONST prvi u produkciji
            ASTNode drugo = node.children.get(1);
            specifikatorTipa(drugo);
            if (drugo.tip.equals("KR_VOID")) {   //sintaksno ispravno al besmisleno
                terminal(prvo);

                System.out.format("%s ::= KR_CONST(%d,%s) <specifikator_tipa>",
                        node.value, prvo.line, prvo.name);
                exit(1);
            }

            node.tip = toConsType(drugo.tip);
        } else {
            Util.greska("imeTipa");
        }
    }

    //GOTOVO
    private void inicijalizator (ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);
        if (size == 1 && prvi.value.equals("<izraz_pridruzivanja>")) {   //prva produkcija
            izrazPridruzivanja(prvi);
            if (prvi.tip.equals("NIZ_ZNAKOVA") || prvi.tip.equals("NIZ_CONST_ZNAKOVA")) {
                if (arrLenStack.size() == 0) {
                    node.broj_elem = 0;
                } else {
                    node.broj_elem = arrLenStack.pop() + 1;
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
            listCopyPasta(lista.tipovi, node.tipovi);
        } else {
            Util.greska("inicijalizator");
        }
    }

    /* ========== LISTE OVOGA I ONOGA ========== */

    //GOTOVO
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

    //GOTOVO
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

    //GOTOVO
    private void listaArgumenata(ASTNode node) {
        ASTNode prvi = node.children.get(0);

        if (node.children.size() == 1 && prvi.value.equals("<izraz_pridruzivanja>")) {
            izrazPridruzivanja(prvi);
            node.tipovi.add(prvi.tip);
        } else if (node.children.size() == 3 && prvi.value.equals("<lista_argumenata>")) {
            listaArgumenata(prvi);
            izrazPridruzivanja(node.children.get(2));

            listCopyPasta(prvi.tipovi, node.tipovi);
            node.tipovi.add(node.children.get(2).tip);
        } else {
            Util.greska("listaArgumenata");
        }
    }

    //GOTOVO
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
            listCopyPasta(prvi.tipovi, node.tipovi);
            node.tipovi.add(treci.tip);

            node.broj_elem = prvi.broj_elem + 1;
        } else {
            Util.greska("listaIzrazaPridruzivanja");
        }
    }

    //GOTOVO
    private void listaParametara(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<deklaracija_parametra>")) {
            deklaracijaParametra(prvi);
            node.tipovi.add(prvi.tip);
            node.names.add(prvi.name);
        } else if (size == 3 && prvi.value.equals("<lista_parametara>")) {
            ASTNode treci = node.children.get(2);
            listaParametara(prvi);
            deklaracijaParametra(treci);

            listCopyPasta(prvi.tipovi, node.tipovi);
            node.tipovi.add(treci.tip);
            listCopyPasta(prvi.tipovi, node.tipovi);
            node.names.add(treci.name);

            String trazenoIme = treci.name;
            List<String> vecPoznataImena = prvi.names;

            for (String s : vecPoznataImena) {
                if (s.equals(trazenoIme)) {         //vec je deklarirano
                    ASTNode srednji = node.children.get(1);
                    terminal(srednji);
                    System.out.printf("%s ::= %s ZAREZ(%d,%s) %s",
                            node.value, prvi.value, srednji.line,
                            srednji.name, treci.value);
                    exit(1);
                }
            }
        } else {
            Util.greska("listaParametara");
        }
    }

    //GOTOVO
    private void listaNaredbi(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<naredba>")) {
            naredba(prvi);
        } else if (size == 2 && prvi.value.equals("<lista_naredbi>")) {
            listaNaredbi(prvi);
            naredba(prvi);
        } else {
            Util.greska("listaNaredbi");
        }
    }

    /* ========== IZRAZI OVI ONI ========== */

    //GOTOVO trebalo bi ok bit, nadam se da ce radit...
    private void primarniIzraz(ASTNode node) {
        int size = node.children.size();
        if (size == 3) {
            ASTNode izraz = node.children.get(1);

            izraz(izraz);
            node.tip = izraz.tip;
            node.l_izraz = izraz.l_izraz;
            return;
        } else if (size != 1) Util.greska("primarniIzraz 1");

        ASTNode singleton = node.children.get(0);
        String[] tokenSplitter = singleton.value.split(" ");            //npr IDN 27 printf
        String value = tokenSplitter[0].trim();
        int line = Integer.parseInt(tokenSplitter[1].trim());
        String name = tokenSplitter[2].trim();
        boolean greska = false;

        singleton.tip = value;
        singleton.line = line;
        singleton.name = name;

        if (value.equals("NIZ_ZNAKOVA")) {                  //npr NIZ_ZNAKOVA 27 "printf\n"
            node.tip = toArrayType(toConsType("KR_CHAR"));
            node.l_izraz = false;
            if (!charrayIsOkay(name)) {
                greska = true;
            } else {
                arrLenStack.push(arrayLength(name));
            }
        } else if (value.equals("ZNAK")) {
            node.tip = "KR_CHAR";
            node.l_izraz = false;
            if (!charIsOkay(name)) {
                greska = true;
            }
        } else if (value.equals("BROJ")) {
            node.tip = "KR_INT";
            node.l_izraz = false;
            if (!brojIsOkay(name)) {
                greska = true;
            }
        } else if (value.equals("IDN")) {
            if (!tablicaZnakova.hasElem(name) && !tablicaDefFunkcija.hasElem(name)) {   //ne postoji nikakav IDN u scopeu
                System.out.printf("<primarni_izraz> ::= IDN(%d,%s)", line, name);
                exit(1);
            }
            if (tablicaZnakova.hasElem(name)) {             //postoji neki var
                ASTNode spawn = tablicaZnakova.getIDN(name);
                if (spawn.tipovi.size() == 0) {
                    node.tip = spawn.tip;
                    node.l_izraz = spawn.l_izraz;
                } else {
                    listCopyPasta(spawn.tipovi, node.tipovi);
                    node.l_izraz = false;
                }
            }
            if (tablicaDefFunkcija.hasElem(name)) {         //postoji neka funkcija
                node.tip = tablicaDefFunkcija.getIDN(name).tip;
                node.l_izraz = false;
            }
        } else Util.greska("primarniIzraz 2");

        if (greska) {
            System.out.printf("%s ::= %s(%d,%s)",
                    node.value, value, line, name);
            exit(1);
        }
    }

    //GOTOVO
    private void izraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1 && prvi.value.equals("<izraz_pridruzivanja>")) {
            izrazPridruzivanja(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;
        } else if (size == 3 && prvi.value.equals("<izraz>")) {
            ASTNode treci = node.children.get(2);
            izraz(prvi);
            izrazPridruzivanja(treci);
            node.tip = treci.tip;
            node.l_izraz = false;
        } else {
            Util.greska("izraz");
        }
    }

    //GOTOVO uz todo napomene, ovo provjeriti pls
    private void postfiksIzraz(ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size < 1 || size > 4) Util.greska("postfiksIzraz 1");

        if (size == 1 && prvi.value.equals("<primarni_izraz>")) {
            primarniIzraz(prvi);
            node.tip = prvi.tip;
            node.l_izraz = prvi.l_izraz;

        } else if (size == 2 && prvi.value.equals("<postfiks_izraz>")) {
            postfiksIzraz(prvi);
            if (!prvi.l_izraz || !isImplicitlyCastable(prvi.tip, "KR_INT")) {
                ASTNode drugi = node.children.get(1);
                terminal(drugi);

                System.out.printf("%s ::= %s %s(%d, %s)",
                        node.value, prvi.value, drugi.tip, drugi.line, drugi.name);
                exit(1);
            } else {
                node.tip = "KR_INT";
                node.l_izraz = false;
            }
        } else if (size == 3 && prvi.value.equals("<postfiks_izraz>")) {
            postfiksIzraz(prvi);

            //cudno nes, ugl molim peruna da su ovi svi uvjeti dovoljni, tu se gleda jel povratna
            //funkcije void, ak ne onda je greska
            if (prvi.tipovi.size() == 0 || !prvi.tipovi.get(0).equals("KR_VOID")) {
                ASTNode drugi = node.children.get(1);
                terminal(drugi);
                ASTNode treci = node.children.get(2);
                terminal(treci);

                System.out.printf("%s ::= %s %s(%d,%s) %s(%d, %s)",
                        node.value, prvi.value, drugi.tip, drugi.line, drugi.name,
                        treci.tip, treci.line, treci.name);
                exit(1);
            }
            node.tip = "KR_VOID";
            node.l_izraz = false;

        } else if (node.children.get(2).value.equals("<izraz>")) {
            String tipX = findingXemo(prvi.tip);        //trazimo kojeg tipa je niz
            ASTNode treci = node.children.get(2);
            postfiksIzraz(prvi);
            izraz(treci);

            if (tipX.equals("GRESKA findingXemo") || isImplicitlyCastable(treci.tip, "KR_INT")) {    //greska
                ASTNode op1 = node.children.get(1);
                ASTNode op2 = node.children.get(2);
                terminal(op1);
                terminal(op2);
                System.out.printf("%s ::= %s %s(%d,%s) %s %s(%d,%s)",
                        node.value, prvi.value, op1.tip, op1.line, op1.name,
                        treci.value, op2.tip, op2.line, op2.name);
                exit(1);
            }
            node.tip = tipX;
            node.l_izraz = tipX.equals(toConsType(tipX));

        } else if (node.children.get(2).value.equals("<lista_argumenata>")) {
            ASTNode treci = node.children.get(2);
            postfiksIzraz(prvi);
            listaArgumenata(treci);
            boolean greska = false;

            String[] tipovi = prvi.tipovi.toArray(new String[0]);
            String[] argumentiFunkcije = treci.tipovi.toArray(new String[0]);
            //todo mozda dodaj if tipovi.length != 2 ako ovo ne radi
            if (tipovi.length == 0 || tipovi.length != argumentiFunkcije.length) {
                greska = true;
            }

            for (int i = 0, len = tipovi.length; i < len; i++) {
                if (greska) break;
                if (!isImplicitlyCastable(argumentiFunkcije[i], tipovi[i])) {
                    greska = true;
                    break;
                }
            }
            if (greska) {
                ASTNode op1 = node.children.get(1);
                ASTNode op2 = node.children.get(2);
                terminal(op1);
                terminal(op2);
                System.out.printf("%s ::= %s %s(%d,%s) %s %s(%d,%s)",
                        node.value, prvi.value, op1.tip, op1.line, op1.name,
                        treci.value, op2.tip, op2.line, op2.name);
                exit(1);
            }
            node.tip = tipovi[0];       //todo ako ne radi probaj tipovi[1]
            node.l_izraz = false;
        } else {
            Util.greska("postfiksIzraz 2");
        }
    }

    //GOTOVO
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

            if (!prvi.l_izraz) {
                ASTNode drugi = node.children.get(1);
                terminal(drugi);

                System.out.printf("%s ::= %s OP_PRIDRUZI(%d,%s) %s",
                        node.value, prvi.value, drugi.line, drugi.name, treci.value);
            }
            node.tip = prvi.tip;
            izrazPridruzivanja(treci);

            if (isImplicitlyCastable(treci.tip, node.tip)) {
                ASTNode drugi = node.children.get(1);
                terminal(drugi);

                System.out.printf("%s ::= %s OP_PRIDRUZI(%d,%s) %s",
                        node.value, prvi.value, drugi.line, drugi.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("izrazPridruzivanja");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            logIIzraz(treci);
            if (isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_ILI(%d,%s) %s",
                        node.value, prvi.value, srednji.line, srednji.name, treci.value);
                exit(1);
            }

        } else {
            Util.greska("log ili izraz (LogIliIzraz)");
        }
    }

    //GOTOVO
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
                if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                    greskaCast = true;
                }

                ASTNode treci = node.children.get(2);
                binIliIzraz(treci);
                if(!isImplicitlyCastable(treci.tip, "KR_INT")) {
                    greskaCast = true;
                }

                if (greskaCast) {
                    ASTNode srednji = node.children.get(1);
                    terminal(srednji);

                    System.out.printf("%s ::= %s OP_I(%d,%s) %s",
                            node.value, prvi.value, srednji.line, srednji.name, treci.value);
                    exit(1);
                }
            } else {
                Util.greska("logicki i izraz (logIIzraz)");
            }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            binXiliIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_BIN_ILI(%d,%s) %s",
                        node.value, prvi.value, srednji.line, srednji.name, treci.value);
                exit(1);
            }

        } else {
            Util.greska("bin ili izraz (binIliIzraz");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            binIIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_BIN_XILI(%d,%s) %s",
                        node.value, prvi.value, srednji.line, srednji.name, treci.value);
                exit(1);
            }

        } else {
            Util.greska("bin xili izraz (binXiliIzraz");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            jednakosniIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s OP_BIN_I(%d,%s) %s",
                        node.value, prvi.value, srednji.line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("bin i izraz (binIIzraz)");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            odnosniIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("jednakosniIzraz");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            aditivniIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("odnosniIzraz");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            multiplikativniIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("aditivniIzraz");
        }
    }

    //GOTOVO
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
            if (!isImplicitlyCastable(prvi.tip, "KR_INT")) {
                greskaCast = true;
            }

            ASTNode treci = node.children.get(2);
            castIzraz(treci);
            if (!isImplicitlyCastable(treci.tip, "KR_INT")) {
                greskaCast = true;
            }

            if (greskaCast) {
                ASTNode srednji = node.children.get(1);
                terminal(srednji);

                System.out.printf("%s ::= %s %s(%d,%s) %s",
                        node.value, prvi.value, srednji.tip, srednji.line, srednji.name, treci.value);
                exit(1);
            }
        } else {
            Util.greska("aditivniIzraz");
        }
    }

    //GOTOVO
    private void specifikatorTipa(ASTNode node) {
        String value = node.children.get(0).extractFirstFromToken();

        switch (value) {
            case "KR_VOID":
                node.tip = "KR_VOID";
                break;
            case "KR_INT":
                node.tip = "KR_INT";
                break;
            case "KR_CHAR":
                node.tip = "KR_CHAR";
                break;
            default:
                Util.greska("specifikatorTipa");
                break;
        }
    }

    //GOTOVO
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
                        node.value, lijeva.tip, lijeva.line, lijeva.name,
                        drugi.value, desna.tip, desna.line, desna.name, zadnji.value);
                exit(1);
            }
        } else {
            Util.greska("castIzraz");
        }
    }

    //GOTOVO
    private void unarniIzraz(ASTNode node) {
        ASTNode prvi = node.children.get(0);
        ASTNode drugi = null;
        if (node.children.size() == 2) {
            drugi = node.children.get(1);
        }

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
            if (!drugi.l_izraz) {
                greska = true;
            }
            if (isImplicitlyCastable(drugi.tip, "KR_INT")) {
                greska = true;
            }
            if (greska) {
                //prvi je operator
                terminal(prvi);
                System.out.printf("%s ::= %s(%d,%s) %s",
                        node.value, prvi.tip, prvi.line, prvi.name, drugi.value);
                exit(1);
            }
        } else {
            Util.greska("unarniIzraz");
        }
    }

    //GOTOVO
    private void izrazNaredba (ASTNode node) {
        int size = node.children.size();
        ASTNode prvi = node.children.get(0);

        if (size == 1) {
            node.tip = "KR_INT";
        } else if (size == 2 && prvi.value.equals("<izraz>")) {
            izraz(prvi);
            node.tip = prvi.tip;
        } else {
            Util.greska("izrazNaredba");
        }
    }

    /* ========== NAREDBE NAREDNICI VODNICI PORUCNICI ========== */

    //GOTOVO
    private void naredba(ASTNode node) {
        if (node.children.size() != 1) Util.greska("naredba");

        ASTNode jedinac = node.children.get(0);

        if (jedinac.value.equals("<slozena_naredba>")) slozenaNaredba(jedinac, null);
        else if (jedinac.value.equals("<izraz_naredba>")) izrazNaredba(jedinac);
        else if (jedinac.value.equals("<naredba_grananja>")) naredbaGrananja(jedinac);
        else if (jedinac.value.equals("<naredba_petlje>")) naredbaPetlje(jedinac);
        else if (jedinac.value.equals("<naredba_skoka>")) naredbaSkoka(jedinac);
        else Util.greska("naredba");
    }

    //GOTOVO
    private void naredbaGrananja(ASTNode node) {
        int size = node.children.size();
        boolean greska = false;

        if (size == 5) {
            izraz(node.children.get(2));
            if (!isImplicitlyCastable(node.children.get(2).tip, "KR_INT")) {
                ASTNode jen = node.children.get(0);
                ASTNode dva = node.children.get(1);
                ASTNode tri = node.children.get(2);
                ASTNode cet = node.children.get(3);
                ASTNode pet = node.children.get(4);
                terminal(jen); terminal(dva); terminal(cet);

                System.out.printf("%s ::= KR_IF(%d,%s) L_ZAGRADA(%d,%s) %s D_ZAGRADA(%d, %s) %s",
                        node.value, jen.line, jen.name, dva.line, dva.name,
                        tri.value, cet.line, cet.name, pet.value);
                exit(1);
            }
            naredba(node.children.get(4));
        } else if (size == 7) {
            izraz(node.children.get(2));

            if (!isImplicitlyCastable(node.children.get(2).tip, "KR_INT")) {
                ASTNode jen = node.children.get(0);
                ASTNode dva = node.children.get(1);
                ASTNode cet = node.children.get(3);
                ASTNode pet = node.children.get(5);
                terminal(jen); terminal(dva); terminal(cet); terminal(pet);

                System.out.printf("<naredba_grananja> ::= KR_IF(%d,%s) L_ZAGRADA(%d,%s) <izraz> " +
                                "D_ZAGRADA(%d,%s) <naredba> KR_ELSE(%d,%s) <naredba>",
                        jen.line, jen.name, dva.line, dva.name,
                        cet.line, cet.name, pet.line, pet.name);
                exit(1);
            }
            naredba(node.children.get(4));
            naredba(node.children.get(6));
        } else {
            Util.greska("naredbaGrananja");
        }
    }

    //GOTOVO
    private void naredbaPetlje(ASTNode node) {
        int size = node.children.size();

        if (size == 5) {
            izraz(node.children.get(2));
            if (!isImplicitlyCastable(node.children.get(2).tip, "KR_INT")) {
                ASTNode jen = node.children.get(0); terminal(jen);
                ASTNode dva = node.children.get(1); terminal(dva);
                ASTNode tri = node.children.get(3); terminal(tri);

                System.out.printf("<naredba_petlje> ::= KR_WHILE(%d,%s) " +
                        "L_ZAGRADA(%d,%s) <izraz> D_ZAGRADA(%d,%s) <naredba>",
                        jen.line, jen.name, dva.line, dva.name, tri.line, tri.name);
                exit(1);
            }
            naredba(node.children.get(4));
        } else if (size == 6) {
            izrazNaredba(node.children.get(2));
            izrazNaredba(node.children.get(3));

            if (!isImplicitlyCastable(node.children.get(3).tip, "KR_INT")) {
                ASTNode jen = node.children.get(0); terminal(jen);
                ASTNode dva = node.children.get(1); terminal(dva);
                ASTNode cet = node.children.get(3); terminal(cet);

                System.out.printf("<naredba_petlje> ::= KR_FOR(%d,%s) L_ZAGRADA(%d,%s) " +
                        "<izraz_naredba> <izraz_naredba> D_ZAGRADA(%d,%s) <naredba>",
                        jen.line, jen.name, dva.line, dva.name, cet.line, cet.name);
                exit(1);
            }
            naredba(node.children.get(5));
        } else if (size == 7) {
            izrazNaredba(node.children.get(2));
            izrazNaredba(node.children.get(3));

            if (!isImplicitlyCastable(node.children.get(3).tip, "KR_INT")) {
                ASTNode jen = node.children.get(0);
                terminal(jen);
                ASTNode dva = node.children.get(1);
                terminal(dva);
                ASTNode ses = node.children.get(5);
                terminal(ses);

                System.out.printf("<naredba_petlje> ::= KR_FOR(%d,%s) L_ZAGRADA(%d,%s) " +
                        "<izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA(%d,%s) <naredba>",
                        jen.line, jen.name, dva.line, dva.name, ses.line, ses.name);
                exit(1);
            }
            izraz(node.children.get(4));
            naredba(node.children.get(6));
        } else {
            Util.greska("naredbaPetlje");
        }
    }

    //GOTOVO
    private void naredbaSkoka(ASTNode node) {
        ASTNode prvi = node.children.get(0);
        ASTNode drugi = node.children.get(1);
        terminal(prvi);
        if (!drugi.value.equals("<izraz>")) {
            terminal(drugi);
        }

        if(prvi.tip.equals("KR_CONTINUE") || prvi.tip.equals("KR_BREAK")) {
            ASTNode buff = node.parent;
            while (!buff.value.equals("<naredba_petlje>")) {
                buff = buff.parent;

                if (buff.depth == 0) {
                    System.out.printf("<naredba_skoka> ::= %s(%d,%s) TOCKAZAREZ(%d, %s)",
                            prvi.tip, prvi.line, prvi.name, drugi.line, drugi.name);
                    exit(1);
                }
            }

            if (!drugi.tip.equals("TOCKAZAREZ")) {
                System.out.printf("<naredba_skoka> ::= %s(%d,%s) TOCKAZAREZ(%d, %s)",
                        prvi.tip, prvi.line, prvi.name, drugi.line, drugi.name);
                exit(1);
            }
        } else if (prvi.tip.equals("KR_RETURN") && node.children.size() == 2) {
            ASTNode buff = node.parent;
            while (!buff.value.equals("<definicija funkcije>")) {
                buff = buff.parent;

                if (buff.depth == 0) {
                    System.out.printf("<naredba_skoka> ::= KR_RETURN(%d, %s) TOCKAZAREZ(%d, %s)",
                            prvi.line, prvi.name, drugi.line, drugi.name);
                    exit(1);
                }
            }

            if (!drugi.tip.equals("TOCKAZAREZ")) {
                System.out.printf("<naredba_skoka> ::= KR_RETURN(%d, %s) TOCKAZAREZ(%d, %s)",
                        prvi.line, prvi.name, drugi.line, drugi.name);
                exit(1);
            }
        } else if (prvi.tip.equals("KR_RETURN") && node.children.size() == 3) {
            ASTNode treci = node.children.get(2);
            terminal(treci);

            ASTNode buff = node.parent;
            while (!buff.value.equals("<definicija_funkcije>")) {
                buff = buff.parent;

                if (buff.depth == 0) {
                    System.out.printf("<naredba_skoka> ::= KR_RETURN(%d, %s) <izraz> TOCKAZAREZ(%d, %s)",
                            prvi.line, prvi.name, treci.line, treci.name);
                    exit(1);
                }
            }
            izraz(drugi);

            String tip = buff.tip.split(";")[0];

            if (!treci.tip.equals("TOCKAZAREZ") || !isImplicitlyCastable(drugi.tip, buff.tip)) {
                System.out.printf("<naredba_skoka> ::= KR_RETURN(%d, %s) <izraz> TOCKAZAREZ(%d, %s)",
                        prvi.line, prvi.name, treci.line, treci.name);
                exit(1);
            }
        } else {
            Util.greska("naredbaSkoka");
        }
    }

    //GOTOVO
    private void slozenaNaredba(ASTNode node, List<String> parametri) {
        int size = node.children.size();
        ASTNode lista = node.children.get(1);

        tablicaZnakova.openScope();
        if (parametri != null && parametri.size() > 0) {
            for (String s : parametri) {
                tablicaZnakova.addElem(s, node.parent.children.get(3));     //jer su deklarirani tamo gore valjda...
            }
        }
        if (size == 3 && lista.value.equals("<lista_naredbi>")) {
            listaNaredbi(lista);
        } else if (size == 4 && lista.value.equals("<lista_deklaracija>")) {
            listaDeklaracija(lista);
            listaNaredbi(node.children.get(2));
        }
        tablicaZnakova.closeScope();
    }

    /* ========== MISCELLANEOUS ========== */

        /*
    Provjere na kraju semanticke analize:
        Postoji li main metoda?
        Jesu li sve deklarirane metode negdje i definirane?
     */

    private void finalChecks() {
        if(!postojiMain() || !postojiVoidInt()) {
            System.out.println("main");
            exit(1);
        }

        if (!sveDeklariraneSuDefinirane()) {
            System.out.println("funkcija");
            exit(1);
        }
    }

    private boolean postojiMain() {
        Set<String> set = tablicaDefFunkcija.getKeys();

        for (String s : set) {
            if (s.equals("main")) return true;
        }
        return false;
    }

    private boolean postojiVoidInt() {
        return true;
    }

    private boolean sveDeklariraneSuDefinirane() {
        Set<String> deklarirane = deklariraneFunkcije.getKeys();
        for (String s : deklarirane) {
            if (!tablicaDefFunkcija.hasElem(s)) return false;
        }
        return true;
    }

    private static void listCopyPasta(List<String> src, List<String> dest) {
        for (String s : src) {
            dest.add(s);
        }
    }

    private static String toConsType (String type) {
        if (type.equals("KR_CONST_INT") || type.equals("KR_CONST_CHAR")) return type;
        if (type.equals("KR_INT")) return "KR_CONST_INT";
        if (type.equals("KR_CHAR")) return "KR_CONST_CHAR";

        //nesto ne valja jel
        return "GRESKA toConsType";
    }

    private static String toArrayType(String type) {
        if (type.equals("KR_INT")) return "NIZ_BROJEVA";
        if (type.equals("KR_CONST_INT")) return "NIZ_CONST_BROJEVA";
        if (type.equals("KR_CHAR")) return "NIZ_ZNAKOVA";
        if (type.equals("KR_CONST_CHAR")) return "NIZ_CONST_ZNAKOVA";
        return "GRESKA toArrayType";
    }

    private static String findingXemo(String niz) {
        if (niz.equals("NIZ_BROJEVA")) return "KR_INT";
        if (niz.equals("NIZ_CONST_BROJEVA")) return "KR_CONST_INT";
        if (niz.equals("NIZ_ZNAKOVA")) return "KR_CHAR";
        if (niz.equals("NIZ_CONST_ZNAKOVA")) return "KR_CONST_CHAR";
        return "GRESKA findingXemo";
    }

    private static String wrapTip(String povratna, List<String> list) {
        StringBuilder bob = new StringBuilder();
        if (povratna != null) bob.append(povratna);

        for (String s : list) {
            bob.append(";").append(s);
        }

        if (povratna == null) bob.deleteCharAt(0);
        return bob.toString();
    }

    private static boolean isTypeT(String type) {
        String[] tees = new String[] { "KR_INT", "KR_CONST_INT", "KR_CHAR", "KR_CONST_CHAR" };
        for (String s : tees) {
            if (type.equals(s)) return true;
        }
        return false;
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

    private static boolean isCastableToT(String str) {
        String[] tees = new String[] { "KR_INT", "KR_CONST_INT", "KR_CHAR", "KR_CONST_CHAR" };
        boolean isCastable = false;

        for (String t : tees) {
            if (isImplicitlyCastable(str, t)) {
                isCastable = true;
            }
        }
        return isCastable;
    }

    private static boolean brojIsOkay(String str) {
        int broj = Integer.parseInt(str);
        return ((-2147483648 <= broj) && (broj <= 2147483647));
    }

    private static boolean sameTypeLists(List<String> prva, List<String> druga) {
        if (prva.size() != druga.size()) return false;

        for (int i = 0, len = prva.size(); i < len; i++) {
            if (!prva.get(i).equals(druga.get(i))) return false;
        }
        return true;
    }

    //todo
    private static boolean charIsOkay(String str) {
        return false;
    }

    //todo
    private static boolean charrayIsOkay(String str) {
        char[] charray = str.toCharArray();
        char[] specimen = new char[] {'n', 't'}; //jos fale neki

        return false;
    }

    //todo
    private static int arrayLength(String str) {
        return 0;
    }
}