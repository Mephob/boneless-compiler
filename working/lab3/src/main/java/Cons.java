
/*
    Ovdje su konstovi za sve definicije, deklaracije, liste, izraze,
    naredbe, brojeve, tipove, sve i svasta, raznorazni stringovi koji
    se cesto koriste i tak.

    Takoder ima par fora metoda koje rade provjere oko implicitnog i eksplicitnog
    castanja, neke pretvorbe i takve stvari.

    !!!NAPOMENA!!! nek neka dobra dusa provjeri jel nesto tipa Cons.prvi.equals(Cons.drugi)
    vraca dobar rezultat (trebali bi se stringovi usporedivat) i ak nije nek javi asap.
 */
public enum Cons {
    //pocetak
    PRIJEVODNA_JEDINICA("<prijevodna_jedinica>"),

    //deklaracije
    DEFINICIJA_FUNKCIJE("<definicija_funkcije>"),
    DEKLARACIJA("<deklaracija>"),
    DEKLARACIJA_PARAMETRA("<deklaracija_parametra>"),
    INICIJALIZATOR("<inicijalizator>"),
    INIT_DEKLARATOR("<init_deklarator>"),
    IZRAVNI_DEKLARATOR("<izravni_deklarator>"),
    VANJSKA_DEKLARACIJA("<vanjska_deklaracija>"),

    IME_TIPA("<ime_tipa>"),
    SPECIFIKATOR_TIPA("<specifikator_tipa>"),

    //liste
    LISTA_ARGUMENATA("<lista_argumenata>"),
    LISTA_DEKLARACIJA("<lista_deklaracija>"),
    LISTA_INIT_DEKLARATORA("<lista_init_deklaratora>"),
    LISTA_IZRAZA_PRIDRUZIVANJA("<lista_izraza_pridruzivanja>"),
    LISTA_NAREDBI("<lista_naredbi>"),
    LISTA_PARAMETARA("<lista_parametara>"),

    //izrazi
    ADITIVNI_IZRAZ("<aditivni_izraz>"),
    BIN_I_IZRAZ("<bin_i_izraz>"),
    BIN_ILI_IZRAZ("<bin_ili_izraz>"),
    BIN_XILI_IZRAZ("<bin_xili_izraz>"),
    CAST_IZRAZ("<cast_izraz>"),
    IZRAZ("<izraz>"),
    IZRAZ_NAREDBA("<izraz_naredba>"),
    IZRAZ_PRIDRUZIVANJA("<izraz_pridruzivanja>"),
    JEDNAKOSNI_IZRAZ("<jednakosni_izraz>"),
    LOG_I_IZRAZ("<log_i_izraz>"),
    LOG_ILI_IZRAZ("<log_ili_izraz>"),
    MULTIPLIKATIVNI_IZRAZ("<multiplikativni_izraz>"),
    ODNOSNI_IZRAZ("<odnosni_izraz>"),
    POSTFIKS_IZRAZ("<postfiks_izraz>"),
    PRIMARNI_IZRAZ("<primarni_izraz>"),
    UNARNI_IZRAZ("<unarni_izraz>"),

    BINARNI_IZRAZ("BINARNI_IZRAZ"),
    LOGICKI_IZRAZ("LOGICKI_IZRAZ"),

    //naredbe
    NAREDBA("<naredba>"),
    NAREDBA_GRANANJA("<naredba_grananja>"),
    NAREDBA_PETLJE("<naredba_petlje>"),
    NAREDBA_SKOKA("<naredba_skoka>"),
    SLOZENA_NAREDBA("<slozena_naredba>"),

    //op unarni
    UNARNI_OPERATOR("<unarni_operator>"),

    //zagrade
    D_UGL_ZAGRADA("D_UGL_ZAGRADA"),
    D_VIT_ZAGRADA("D_VIT_ZAGRADA"),
    D_ZAGRADA("D_ZAGRADA"),
    L_UGL_ZAGRADA("L_UGL_ZAGRADA"),
    L_VIT_ZAGRADA("L_VIT_ZAGRADA"),
    L_ZAGRADA("L_ZAGRADA"),

    //misc
    BROJ("BROJ"),
    NIZ_ZNAKOVA("NIZ_ZNAKOVA"),
    NIZ_CONST_ZNAKOVA("NIZ_CONST_ZNAKOVA"),
    NIZ_BROJEVA("NIZ_BROJEVA"),
    NIZ_CONST_BROJEVA("NIZ_CONST_BROJEVA"),
    ZNAK("ZNAK"),
    IDN("IDN"),
    TOCKAZAREZ("TOCKAZAREZ"),
    ZAREZ("ZAREZ"),
    GRESKA("GRESKA"),

    //kljucne rijeci
    KR_BREAK("KR_BREAK"),
    KR_CHAR("KR_CHAR"),
    KR_CONST("KR_CONST"),
    KR_CONTINUE("KR_CONTINUE"),
    KR_ELSE("KR_ELSE"),
    KR_FOR("KR_FOR"),
    KR_IF("KR_IF"),
    KR_INT("KR_INT"),
    KR_RETURN("KR_RETURN"),
    KR_VOID("KR_VOID"),
    KR_WHILE("KR_WHILE"),

    KR_CONST_INT("KR_CONST_INT"),
    KR_CONST_CHAR("KR_CONST_CHAR"),
    KR_CONST_NIZ("KR_CONST_NIZ"),

    //operatori
    MINUS("MINUS"),
    OP_BIN_I("OP_BIN_I"),
    OP_BIN_ILI("OP_BIN_ILI"),
    OP_BIN_XILI("OP_BIN_XILI"),
    OP_DEC("OP_DEC"),
    OP_DIJELI("OP_DIJELI"),
    OP_EQ("OP_EQ"),
    OP_GT("OP_GT"),
    OP_GTE("OP_GTE"),
    OP_I("OP_I"),
    OP_ILI("OP_ILI"),
    OP_INC("OP_INC"),
    OP_LT("OP_LT"),
    OP_LTE("OP_LTE"),
    OP_MOD("OP_MOD"),
    OP_NEG("OP_NEG"),
    OP_NEQ("OP_NEQ"),
    OP_PRIDRUZI("OP_PRIDRUZI"),
    OP_PUTA("OP_PUTA"),
    OP_TILDA("OP_TILDA"),
    PLUS("PLUS");

    private final String text;

    private Cons(final String text) {
            this.text = text;
    }

    public String toString() {
        return text;
    }

    /*
        Provjerava koji tip niza je glavni za zadani neki tip
     */
    public static Cons toNizType(Cons tip) {
        if (tip == KR_INT) return NIZ_BROJEVA;
        if (tip == KR_CHAR) return NIZ_ZNAKOVA;
        if (tip == KR_CONST_INT) return NIZ_CONST_BROJEVA;
        if (tip == KR_CONST_CHAR) return NIZ_CONST_ZNAKOVA;

        //u slucaju da nije niti jedan od ponudena 4 tipa vrati gresku
        //pa nek se pozivatelj bakce s tim...
        return GRESKA;
    }

    /*
        Pretvara non-const tipove u const tipove.
     */
    public static Cons toConsType(Cons tip) {
        //tip je vec const
        if (tip == KR_CONST_INT || tip == KR_CONST_CHAR) return tip;
        if (tip == KR_INT) return KR_CONST_INT;
        if (tip == KR_CHAR) return KR_CONST_CHAR;

        //u slucaju da nije niti jedan od ponudena 4 tipa vrati gresku
        return GRESKA;
    }

    /*
        Pretvara const tipove u non-const tipove
     */
    public static Cons toNonConsType(Cons tip) {
        //jesu li vec non-const
        if (tip == KR_INT || tip == KR_CHAR) return tip;
        if (tip == KR_CONST_CHAR) return KR_CHAR;
        if (tip == KR_CONST_INT) return KR_INT;

        //u slucaju da nije niti jedan od ponudena 4 tipa vrati gresku
        return GRESKA;
    }

    /*
        Provjerava spada li tip u T skupinu (INT, const INT, CHAR, const CHAR).
     */
    public static boolean isTypeT(Cons type) {
        return (type == KR_INT || type == KR_CONST_INT ||
                type == KR_CHAR || type == KR_CONST_CHAR);
    }

    /*
        Self explanatory.
     */
    public static boolean notTypeT(Cons type) {
        return !isTypeT(type);
    }

    /*
        Provjerava jesu li dva tipa medusobno kompatibilna tijekom implicitnog castanja.
     */
    public static boolean isImplicitlyCastable(Cons from, Cons to) {
        //ako su isti
        if (from == to) {
            return true;
        }
        //provjeri za INT
        if ((from == KR_INT && to == KR_CONST_INT) ||
                (from == KR_CONST_INT && to == KR_INT)) {
            return true;
        }
        //provjera za CHAR
        if ((from == KR_CHAR && to == KR_CONST_CHAR) ||
                (from == KR_CONST_CHAR && to == KR_CHAR)) {
            return true;
        }
        //provjera za nizove
        if ((from == NIZ_BROJEVA && to == NIZ_CONST_BROJEVA) ||
                (from == NIZ_CONST_BROJEVA && to == NIZ_BROJEVA) ||
                (from == NIZ_ZNAKOVA && to == NIZ_CONST_ZNAKOVA) ||
                (from == NIZ_CONST_ZNAKOVA && to == NIZ_ZNAKOVA)) {
            return true;
        }
        //provjera za CHAR u INT i obratno, izgleda da i to treba
        if ((from == KR_CHAR && to == KR_INT) ||
                (from == KR_INT && to == KR_CHAR)) {
            return true;
        }
        return false;
    }

    /*
        Provjerava jesu li dva tipa medusobno kompatibilna kod eksplicitnog castana.
     */
    public static boolean isExplicitlyCastable(Cons from, Cons to) {
        if (notTypeT(from) || notTypeT(to)) {
            return false;
        }
        return true;
    }
}
