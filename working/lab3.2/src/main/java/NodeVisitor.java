import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public interface NodeVisitor {

	//###########################################################################//
	//########################Definicije zavrsnih znakova########################//
	//###########################################################################//

	public void accept(IDN idn);

	public void accept(BROJ broj);

	public void accept(ZNAK znak);

	public void accept(NIZ_ZNAKOVA niz_znakova);

	public void accept(KR_BREAK kr_break);

	public void accept(KR_CHAR kr_char);

	public void accept(KR_CONST kr_const);

	public void accept(KR_CONTINUE kr_continue);

	public void accept(KR_ELSE kr_else);

	public void accept(KR_FOR kr_for);

	public void accept(KR_IF kr_if);

	public void accept(KR_INT kr_int);

	public void accept(KR_RETURN kr_return);

	public void accept(KR_VOID kr_void);

	public void accept(KR_WHILE kr_while);

	public void accept(PLUS plus);

	public void accept(OP_INC op_inc);

	public void accept(MINUS minus);

	public void accept(OP_DEC op_dec);

	public void accept(OP_PUTA op_puta);

	public void accept(OP_DIJELI op_dijeli);

	public void accept(OP_MOD op_mod);

	public void accept(OP_PRIDRUZI op_pridruzi);

	public void accept(OP_LT op_lt);

	public void accept(OP_LTE op_lte);

	public void accept(OP_GT op_gt);

	public void accept(OP_GTE op_gte);

	public void accept(OP_EQ op_eq);

	public void accept(OP_NEQ op_neq);

	public void accept(OP_NEG op_neg);

	public void accept(OP_TILDA op_tilda);

	public void accept(OP_I op_i);

	public void accept(OP_ILI op_ili);

	public void accept(OP_BIN_I op_bin_i);

	public void accept(OP_BIN_ILI op_bin_ili);

	public void accept(OP_BIN_XILI op_bin_xili);

	public void accept(ZAREZ zarez);

	public void accept(TOCKAZAREZ tockazarez);

	public void accept(L_ZAGRADA l_zagrada);

	public void accept(D_ZAGRADA d_zagrada);

	public void accept(L_UGL_ZAGRADA l_ugl_zagrada);

	public void accept(D_UGL_ZAGRADA d_ugl_zagrada);

	public void accept(L_VIT_ZAGRADA l_vit_zagrada);

	public void accept(D_VIT_ZAGRADA d_vit_zagrada);

	//###########################################################################//
	//#######################Definicije nezavrsnih znakova#######################//
	//###########################################################################//

	public void accept(PrimarniIzraz pi);

	public void accept(PostfiksIzraz pi);

	public void accept(ListaArgumenata la);

	public void accept(UnarniIzraz ui);

	public void accept(UnarniOperator uo);

	public void accept(CastIzraz ci);

	public void accept(ImeTipa it);

	public void accept(SpecifikatorTipa st);

	public void accept(MultiplikativniIzraz mi);

	public void accept(AditivniIzraz ai);

	public void accept(OdnosniIzraz oi);

	public void accept(JednakosniIzraz ji);

	public void accept(BinIIzraz bii);

	public void accept(BinXiliIzraz bxi);

	public void accept(BinIliIzraz bii);

	public void accept(LogIIzraz lii);

	public void accept(LogIliIzraz lii);

	public void accept(IzrazPridruzivanja ip);

	public void accept(Izraz i);

	public void accept(SlozenaNaredba sn);

	public void accept(ListaNaredbi ln);

	public void accept(Naredba n);

	public void accept(IzrazNaredba in);

	public void accept(NaredbaGrananja ng);

	public void accept(NaredbaPetlje np);

	public void accept(NaredbaSkoka ns);

	public void accept(PrijevodnaJedinica pj);

	public void accept(VanjskaDeklaracija vd);

	public void accept(DefinicijaFunkcije df);

	public void accept(ListaParametara lp);

	public void accept(DeklaracijaParametra dp);

	public void accept(ListaDeklaracija ld);

	public void accept(Deklaracija d);

	public void accept(ListaInitDeklaratora lid);

	public void accept(InitDeklarator id);

	public void accept(IzravniDeklarator id);

	public void accept(Inicijalizator i);

	public void accept(ListaIzrazaPridruzivanja lip);
}

class SemAnalizatorVisitor implements NodeVisitor {

	private Map<Character, Character> legalEscapes = new HashMap<>();

	private Stack<Map<String, String>> tableLayers = new Stack<>();

	{
		legalEscapes.put('t', '\t');
		legalEscapes.put('n', '\n');
		legalEscapes.put('0', '\0');
		legalEscapes.put('\'', '\'');
		legalEscapes.put('\"', '\"');
		legalEscapes.put('\\', '\\');
	}

	public void accept(IDN idn) {
	}


	public void accept(BROJ broj) {
		try {
			broj.setIntValue(Integer.parseInt(broj.getValue()));
		} catch (NumberFormatException e) {
			throw new SemAnalysisException(broj.getValue() + " couldn't fit into a integer", e);
		}
	}


	public void accept(ZNAK znak) {
		char[] result = checkCharacters(znak.getValue(), true);
		if (result.length != 1) {
			throw new IllegalStateException("There might be a bug in the `checkCharacters` method.");
		}

		znak.setCharValue(result[0]);
	}


	public void accept(NIZ_ZNAKOVA niz_znakova) {
		niz_znakova.setCharacters(checkCharacters(niz_znakova.getValue(), false));
	}


	public void accept(KR_BREAK kr_break) {

	}


	public void accept(KR_CHAR kr_char) {

	}


	public void accept(KR_CONST kr_const) {

	}


	public void accept(KR_CONTINUE kr_continue) {

	}


	public void accept(KR_ELSE kr_else) {

	}


	public void accept(KR_FOR kr_for) {

	}


	public void accept(KR_IF kr_if) {

	}


	public void accept(KR_INT kr_int) {

	}


	public void accept(KR_RETURN kr_return) {

	}


	public void accept(KR_VOID kr_void) {

	}


	public void accept(KR_WHILE kr_while) {

	}


	public void accept(PLUS plus) {

	}


	public void accept(OP_INC op_inc) {

	}


	public void accept(MINUS minus) {

	}


	public void accept(OP_DEC op_dec) {

	}


	public void accept(OP_PUTA op_puta) {

	}


	public void accept(OP_DIJELI op_dijeli) {

	}


	public void accept(OP_MOD op_mod) {

	}


	public void accept(OP_PRIDRUZI op_pridruzi) {

	}


	public void accept(OP_LT op_lt) {

	}


	public void accept(OP_LTE op_lte) {

	}


	public void accept(OP_GT op_gt) {

	}


	public void accept(OP_GTE op_gte) {

	}


	public void accept(OP_EQ op_eq) {

	}


	public void accept(OP_NEQ op_neq) {

	}


	public void accept(OP_NEG op_neg) {

	}


	public void accept(OP_TILDA op_tilda) {

	}


	public void accept(OP_I op_i) {

	}


	public void accept(OP_ILI op_ili) {

	}


	public void accept(OP_BIN_I op_bin_i) {

	}


	public void accept(OP_BIN_ILI op_bin_ili) {

	}


	public void accept(OP_BIN_XILI op_bin_xili) {

	}


	public void accept(ZAREZ zarez) {

	}


	public void accept(TOCKAZAREZ tockazarez) {

	}


	public void accept(L_ZAGRADA l_zagrada) {

	}


	public void accept(D_ZAGRADA d_zagrada) {

	}


	public void accept(L_UGL_ZAGRADA l_ugl_zagrada) {

	}


	public void accept(D_UGL_ZAGRADA d_ugl_zagrada) {

	}


	public void accept(L_VIT_ZAGRADA l_vit_zagrada) {

	}


	public void accept(D_VIT_ZAGRADA d_vit_zagrada) {

	}


	public void accept(PrimarniIzraz pi) {

	}


	public void accept(PostfiksIzraz pi) {

	}


	public void accept(ListaArgumenata la) {

	}


	public void accept(UnarniIzraz ui) {

	}


	public void accept(UnarniOperator uo) {

	}


	public void accept(CastIzraz ci) {

	}


	public void accept(ImeTipa it) {

	}


	public void accept(SpecifikatorTipa st) {

	}


	public void accept(MultiplikativniIzraz mi) {

	}


	public void accept(AditivniIzraz ai) {

	}


	public void accept(OdnosniIzraz oi) {

	}


	public void accept(JednakosniIzraz ji) {

	}


	public void accept(BinIIzraz bii) {

	}


	public void accept(BinXiliIzraz bxi) {

	}


	public void accept(BinIliIzraz bii) {

	}


	public void accept(LogIIzraz lii) {

	}


	public void accept(LogIliIzraz lii) {

	}


	public void accept(IzrazPridruzivanja ip) {

	}


	public void accept(Izraz i) {

	}


	public void accept(SlozenaNaredba sn) {

	}


	public void accept(ListaNaredbi ln) {

	}


	public void accept(Naredba n) {

	}


	public void accept(IzrazNaredba in) {

	}


	public void accept(NaredbaGrananja ng) {

	}


	public void accept(NaredbaPetlje np) {

	}


	public void accept(NaredbaSkoka ns) {

	}


	public void accept(PrijevodnaJedinica pj) {

	}


	public void accept(VanjskaDeklaracija vd) {

	}


	public void accept(DefinicijaFunkcije df) {

	}


	public void accept(ListaParametara lp) {

	}


	public void accept(DeklaracijaParametra dp) {

	}


	public void accept(ListaDeklaracija ld) {

	}


	public void accept(Deklaracija d) {

	}


	public void accept(ListaInitDeklaratora lid) {

	}


	public void accept(InitDeklarator id) {

	}


	public void accept(IzravniDeklarator id) {

	}


	public void accept(Inicijalizator i) {

	}


	public void accept(ListaIzrazaPridruzivanja lip) {

	}


	private char[] checkCharacters(String value, boolean isChar) {
		StringBuilder bob = new StringBuilder();


		if (isChar && (value.length() > 2 || value.length() == 2 && value.charAt(0) != '\\')) {
			throw new SemAnalysisException(value + " is too long");
		}

		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) != '\\') {
				bob.append(value.charAt(i));
				continue;
			}

			try {
				i++;
				Character escaped = legalEscapes.get(value.charAt(i));
				if (escaped == null) {
					throw new SemAnalysisException(value + " contains Illegal characters");
				}

				bob.append(escaped);
			} catch (IndexOutOfBoundsException e) {
				throw new SemAnalysisException("Illegal use(tried to end literal) of `\\` in: " + value, e);
			}
		}

		char[] result = new char[bob.length() + (isChar ? 0 : 1)];
		result[result.length - 1] = '\0';
		System.arraycopy(bob.toString().toCharArray(), 0, result, 0, bob.length());

		return result;
	}
}

//###########################################################################//
//######################Only used for printing purposes######################//
//###########################################################################//
class TreePrintVisitor implements NodeVisitor {

	private int indent;


	public void accept(IDN idn) {
		printZav(idn);
	}


	public void accept(BROJ broj) {
		printZav(broj);
	}


	public void accept(ZNAK znak) {
		printZav(znak);
	}


	public void accept(NIZ_ZNAKOVA niz_znakova) {
		printZav(niz_znakova);
	}


	public void accept(KR_BREAK kr_break) {
		printZav(kr_break);
	}


	public void accept(KR_CHAR kr_char) {
		printZav(kr_char);
	}


	public void accept(KR_CONST kr_const) {
		printZav(kr_const);
	}


	public void accept(KR_CONTINUE kr_continue) {
		printZav(kr_continue);
	}


	public void accept(KR_ELSE kr_else) {
		printZav(kr_else);
	}


	public void accept(KR_FOR kr_for) {
		printZav(kr_for);
	}


	public void accept(KR_IF kr_if) {
		printZav(kr_if);
	}


	public void accept(KR_INT kr_int) {
		printZav(kr_int);
	}


	public void accept(KR_RETURN kr_return) {
		printZav(kr_return);
	}


	public void accept(KR_VOID kr_void) {
		printZav(kr_void);
	}


	public void accept(KR_WHILE kr_while) {
		printZav(kr_while);
	}


	public void accept(PLUS plus) {
		printZav(plus);
	}


	public void accept(OP_INC op_inc) {
		printZav(op_inc);
	}


	public void accept(MINUS minus) {
		printZav(minus);
	}


	public void accept(OP_DEC op_dec) {
		printZav(op_dec);
	}


	public void accept(OP_PUTA op_puta) {
		printZav(op_puta);
	}


	public void accept(OP_DIJELI op_dijeli) {
		printZav(op_dijeli);
	}


	public void accept(OP_MOD op_mod) {
		printZav(op_mod);
	}


	public void accept(OP_PRIDRUZI op_pridruzi) {
		printZav(op_pridruzi);
	}


	public void accept(OP_LT op_lt) {
		printZav(op_lt);
	}


	public void accept(OP_LTE op_lte) {
		printZav(op_lte);
	}


	public void accept(OP_GT op_gt) {
		printZav(op_gt);
	}


	public void accept(OP_GTE op_gte) {
		printZav(op_gte);
	}


	public void accept(OP_EQ op_eq) {
		printZav(op_eq);
	}


	public void accept(OP_NEQ op_neq) {
		printZav(op_neq);
	}


	public void accept(OP_NEG op_neg) {
		printZav(op_neg);
	}


	public void accept(OP_TILDA op_tilda) {
		printZav(op_tilda);
	}


	public void accept(OP_I op_i) {
		printZav(op_i);
	}


	public void accept(OP_ILI op_ili) {
		printZav(op_ili);
	}


	public void accept(OP_BIN_I op_bin_i) {
		printZav(op_bin_i);
	}


	public void accept(OP_BIN_ILI op_bin_ili) {
		printZav(op_bin_ili);
	}


	public void accept(OP_BIN_XILI op_bin_xili) {
		printZav(op_bin_xili);
	}


	public void accept(ZAREZ zarez) {
		printZav(zarez);
	}


	public void accept(TOCKAZAREZ tockazarez) {
		printZav(tockazarez);
	}


	public void accept(L_ZAGRADA l_zagrada) {
		printZav(l_zagrada);
	}


	public void accept(D_ZAGRADA d_zagrada) {
		printZav(d_zagrada);
	}


	public void accept(L_UGL_ZAGRADA l_ugl_zagrada) {
		printZav(l_ugl_zagrada);
	}


	public void accept(D_UGL_ZAGRADA d_ugl_zagrada) {
		printZav(d_ugl_zagrada);
	}


	public void accept(L_VIT_ZAGRADA l_vit_zagrada) {
		printZav(l_vit_zagrada);
	}


	public void accept(D_VIT_ZAGRADA d_vit_zagrada) {
		printZav(d_vit_zagrada);
	}


	public void accept(PrimarniIzraz pi) {
		printNez(pi);
	}


	public void accept(PostfiksIzraz pi) {
		printNez(pi);
	}


	public void accept(ListaArgumenata la) {
		printNez(la);
	}


	public void accept(UnarniIzraz ui) {
		printNez(ui);
	}


	public void accept(UnarniOperator uo) {
		printNez(uo);
	}


	public void accept(CastIzraz ci) {
		printNez(ci);
	}


	public void accept(ImeTipa it) {
		printNez(it);
	}


	public void accept(SpecifikatorTipa st) {
		printNez(st);
	}


	public void accept(MultiplikativniIzraz mi) {
		printNez(mi);
	}


	public void accept(AditivniIzraz ai) {
		printNez(ai);
	}


	public void accept(OdnosniIzraz oi) {
		printNez(oi);
	}


	public void accept(JednakosniIzraz ji) {
		printNez(ji);
	}


	public void accept(BinIIzraz bii) {
		printNez(bii);
	}


	public void accept(BinXiliIzraz bxi) {
		printNez(bxi);
	}


	public void accept(BinIliIzraz bii) {
		printNez(bii);
	}


	public void accept(LogIIzraz lii) {
		printNez(lii);
	}


	public void accept(LogIliIzraz lii) {
		printNez(lii);
	}


	public void accept(IzrazPridruzivanja ip) {
		printNez(ip);
	}


	public void accept(Izraz i) {
		printNez(i);
	}


	public void accept(SlozenaNaredba sn) {
		printNez(sn);
	}


	public void accept(ListaNaredbi ln) {
		printNez(ln);
	}


	public void accept(Naredba n) {
		printNez(n);
	}


	public void accept(IzrazNaredba in) {
		printNez(in);
	}


	public void accept(NaredbaGrananja ng) {
		printNez(ng);
	}


	public void accept(NaredbaPetlje np) {
		printNez(np);
	}


	public void accept(NaredbaSkoka ns) {
		printNez(ns);
	}


	public void accept(PrijevodnaJedinica pj) {
		printNez(pj);
	}


	public void accept(VanjskaDeklaracija vd) {
		printNez(vd);
	}


	public void accept(DefinicijaFunkcije df) {
		printNez(df);
	}


	public void accept(ListaParametara lp) {
		printNez(lp);
	}


	public void accept(DeklaracijaParametra dp) {
		printNez(dp);
	}


	public void accept(ListaDeklaracija ld) {
		printNez(ld);
	}


	public void accept(Deklaracija d) {
		printNez(d);
	}


	public void accept(ListaInitDeklaratora lid) {
		printNez(lid);
	}


	public void accept(InitDeklarator id) {
		printNez(id);
	}


	public void accept(IzravniDeklarator id) {
		printNez(id);
	}


	public void accept(Inicijalizator i) {
		printNez(i);
	}


	public void accept(ListaIzrazaPridruzivanja lip) {
		printNez(lip);
	}

	private void printNez(NezavrsniZnak nz) {
		printWithIndent(nz);
		indent++;
		for (Node n : nz.getChildren()) {
			n.acceptVisitor(this);
		}
		indent--;
	}

	private void printZav(ZavrsniZnak zz) {
		printWithIndent(zz);
	}

	private void printWithIndent(Object o) {
		System.out.printf("%" + ((indent == 0) ? "" : Integer.toString(indent)) + "s%s%n", "", o);

	}
}
