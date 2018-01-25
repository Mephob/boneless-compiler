import java.util.*;

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

	default void visitChildren(NezavrsniZnak nz) {
		nz.children.forEach(c -> c.acceptVisitor(this));
	}
}