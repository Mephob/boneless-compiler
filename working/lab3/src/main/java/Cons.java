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
    ZNAK("ZNAK"),
    IDN("IDN"),
    TOCKAZAREZ("TOCKAZAREZ"),
    ZAREZ("ZAREZ"),

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
}
