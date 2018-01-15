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

class SemAnalizatorVisitor implements NodeVisitor {

	static private Map<Character, Character> legalEscapes = new HashMap<>();

	static private Map<Class<? extends ZavrsniZnak>, Tip> legalTypes = new HashMap<>();

	private Map<String, Funkcija> definedFunctions = new HashMap<>();

	private Stack<Map<String, Tip>> initDeklaratori = new Stack<>();

	private List<Funkcija> allDeclaredFunctions = new ArrayList<>();

	static {
		legalEscapes.put('t', '\t');
		legalEscapes.put('n', '\n');
		legalEscapes.put('0', '\0');
		legalEscapes.put('\'', '\'');
		legalEscapes.put('\"', '\"');
		legalEscapes.put('\\', '\\');

		legalTypes.put(KR_VOID.class, Tip.voidType);
		legalTypes.put(KR_INT.class, Tip.integer);
		legalTypes.put(KR_CHAR.class, Tip.character);
	}

	public Funkcija findMain() {
		Funkcija f;
		for (String s : definedFunctions.keySet()) {
			if (s.equals("main")) {
				f = definedFunctions.get(s);
				if (!(f.args.size() == 0) || !(f.primitiv.equals(int.class))) {
					throw new SemAnalysisException("funkcija");
				}
				return f;
			}
		}

		throw new SemAnalysisException("funkcija");
	}

	public boolean areAllDeclaredFunctionsDefined() {
		for (Funkcija f : allDeclaredFunctions) {
			if (initDeklaratori.peek().get(f.name) == null || !(initDeklaratori.peek().get(f.name) instanceof Funkcija)) {
				throw new SemAnalysisException("funkcija");
			}

			Funkcija f2 = (Funkcija) initDeklaratori.peek().get(f.name);

			if (!(f.primitiv.equals(f2.primitiv) && f.args.equals(f2.args) && f.name.equals(f2.name))) {
				throw new SemAnalysisException("funkcija");
			}
		}

		return true;
	}


	public void accept(IDN idn) {
	}


	public void accept(BROJ broj) {
		try {
			if (broj.getValue().startsWith("0x")) {
				broj.setIntValue(Integer.parseInt(broj.getValue().substring(2), 16));
				return;
			}

			broj.setIntValue(Integer.parseInt(broj.getValue()));
		} catch (NumberFormatException e) {
			throw new SemAnalysisException(generateMessage((NezavrsniZnak) broj.getParent()));
		}

	}


	public void accept(ZNAK znak) {
		char[] result;
		try {
			result = checkCharacters(znak.getValue(), true);
		} catch (IllegalStateException e) {
			throw new SemAnalysisException(generateMessage((NezavrsniZnak) znak.getParent()));
		}
		if (result.length != 1) {
			throw new IllegalStateException("There might be a bug in the `checkCharacters` method.");
		}

		znak.setCharValue(result[0]);
	}


	public void accept(NIZ_ZNAKOVA niz_znakova) {
		try {
			niz_znakova.setCharacters(checkCharacters(niz_znakova.getValue(), false));
		} catch (IllegalStateException e) {
			throw new SemAnalysisException(generateMessage((NezavrsniZnak) niz_znakova.getParent()));
		}
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

	//######################################################################################################################
	public void accept(PrimarniIzraz pi) {
		visitChildren(pi);

		Node prvi = pi.children.get(0);
		if (prvi instanceof IDN) {
			Tip buff = getIDeklarator(((IDN) prvi).getValue());

			if (buff == null) {
				throw new SemAnalysisException(generateMessage(pi));
			}

			pi.setTip(buff);
			pi.setlIzraz(!buff.isConstant);
			return;
		} else if (prvi instanceof BROJ) {
			pi.setTip(Tip.integer);
		} else if (prvi instanceof ZNAK) {
			pi.setTip(Tip.character);
		} else if (prvi instanceof NIZ_ZNAKOVA) {
			pi.setTip(Tip.constCharA);
		} else if (prvi instanceof L_ZAGRADA) {
			Izraz i = (Izraz) pi.children.get(1);
			pi.setTip(i.getTip());
			pi.setlIzraz(i.getlIzraz());
			return;
		} else {
			throw new IllegalStateException(generateMessage(pi));
		}

		pi.setlIzraz(false);
	}


	public void accept(PostfiksIzraz pi) {
		if (pi.children.size() == 1) {
			visitChildren(pi);
			PrimarniIzraz buff = (PrimarniIzraz) pi.children.get(0);
			pi.setTip(buff.getTip());
			pi.setlIzraz(buff.getlIzraz());
			return;
		} else if (pi.children.size() == 2) {
			visitChildren(pi);
			PostfiksIzraz buff = (PostfiksIzraz) pi.children.get(0);

			if (!buff.getlIzraz() && buff.getTip().isImplCastable(Tip.integer)) {
				throw new SemAnalysisException(generateMessage(pi));
			}
			pi.setTip(Tip.integer);
			pi.setlIzraz(false);
			return;
		}

		PostfiksIzraz prvi = (PostfiksIzraz) pi.children.get(0);
		prvi.acceptVisitor(this);

		Node drugi = pi.children.get(1);
		Node treci = pi.children.get(2);

		if (drugi instanceof L_UGL_ZAGRADA) {
			if (!prvi.getTip().primitiv.isArray()) {
				throw new SemAnalysisException(generateMessage(pi));
			}

			treci.acceptVisitor(this);

			if (!((Izraz) treci).getTip().isImplCastable(Tip.integer)) {
				throw new SemAnalysisException(generateMessage(pi));
			}

			Tip prviTip = prvi.getTip();

			//pi.setTip():
			if (prviTip.equals(Tip.intA)) {
				if (prviTip.isConstant) {
					pi.setTip(Tip.constInteger);
				} else {
					pi.setTip(Tip.integer);
				}
			} else if (prviTip.equals(Tip.charA)) {
				if (prviTip.isConstant) {
					pi.setTip(Tip.constCharacter);
				} else {
					pi.setTip(Tip.character);
				}
			} else {
				throw new IllegalStateException(generateMessage(pi));
			}

			pi.setlIzraz(!pi.getTip().isConstant);
			return;
		} else if (drugi instanceof L_ZAGRADA && treci instanceof D_ZAGRADA) {
			if (!isFunction(prvi.getTip(), null, Collections.emptyList())) {
				throw new SemAnalysisException(generateMessage(pi));
			}

		} else if (drugi instanceof L_ZAGRADA && treci instanceof ListaArgumenata) {
			treci.acceptVisitor(this);
			if (!isFunction(prvi.getTip(), null, ((ListaArgumenata) treci).getTips())) {
				throw new SemAnalysisException(generateMessage(pi));
			}
		} else {
			throw new IllegalStateException(generateMessage(pi));
		}


		pi.setTip(prvi.getTip());
		pi.setlIzraz(false);
	}


	public void accept(ListaArgumenata la) {
		visitChildren(la);

		if (la.children.size() == 1) {
			la.setTips(Collections.singletonList(((IzrazPridruzivanja) la.children.get(0)).getTip()));
		} else {
			List<Tip> tips = new ArrayList<>(((ListaArgumenata) la.children.get(0)).getTips());
			tips.add(((IzrazPridruzivanja) la.children.get(2)).getTip());
			la.setTips(tips);
		}
	}


	public void accept(UnarniIzraz ui) {
		//visitChildren(ui);

		if (ui.children.get(ui.children.size() - 1) instanceof PostfiksIzraz) {
			visitChildren(ui);
			ui.setTip(((PostfiksIzraz) ui.children.get(0)).getTip());
			ui.setlIzraz(((PostfiksIzraz) ui.children.get(0)).getlIzraz());
			return;
		} else if (ui.children.get(ui.children.size() - 1) instanceof UnarniIzraz) {
			visitChildren(ui);
			UnarniIzraz uib = (UnarniIzraz) ui.children.get(ui.children.size() - 1);
			uib.setlIzraz(true);
			uib.setTip(Tip.integer);

		} else if (ui.children.get(ui.children.size() - 1) instanceof CastIzraz) {
			ui.children.get(ui.children.size() - 1).acceptVisitor(this);
			((CastIzraz) ui.children.get(ui.children.size() - 1)).setTip(Tip.integer);
		} else throw new IllegalStateException(generateMessage(ui));

		ui.setlIzraz(false);
		ui.setTip(Tip.integer);
	}


	public void accept(UnarniOperator uo) {//done, iako ne treba ni visitad children
		visitChildren(uo);
	}


	public void accept(CastIzraz ci) {
		visitChildren(ci);

		if (ci.children.get(0) instanceof UnarniIzraz) {
			ci.setTip(((UnarniIzraz) ci.children.get(0)).getTip());
			ci.setlIzraz(((UnarniIzraz) ci.children.get(0)).getlIzraz());
		} else {
			CastIzraz cib = (CastIzraz) ci.children.get(3);
			ImeTipa it = (ImeTipa) ci.children.get(0);
			if (!cib.getTip().isImplCastable(it.getTip())) {
				throw new SemAnalysisException(generateMessage(ci));
			}
		}
	}


	public void accept(ImeTipa it) {
		visitChildren(it);

		if (it.children.get(0) instanceof KR_CONST) {
			if (((SpecifikatorTipa) it.children.get(1)).getTip().equals(Tip.voidType)) {
				throw new SemAnalysisException(generateMessage(it));
			}

			it.setTip(toConst(((SpecifikatorTipa) it.children.get(1)).getTip()));
		} else {
			it.setTip(((SpecifikatorTipa) it.children.get(0)).getTip());
		}
	}


	public void accept(SpecifikatorTipa st) {
		Tip buff = legalTypes.get(st.children.get(0).getClass());
		if (buff == null) {
			throw new IllegalStateException(st.children.get(0) + " is not a valid type");
		}

		st.setTip(buff);
	}


	public void accept(MultiplikativniIzraz mi) {
		//visitChildren(mi);
		CastIzraz x = (CastIzraz) mi.children.get(mi.children.size() - 1);
		if (mi.children.get(0) instanceof CastIzraz) {
			visitChildren(mi);
			mi.setTip(x.getTip());
			mi.setlIzraz(x.getlIzraz());
		} else {
			MultiplikativniIzraz y = (MultiplikativniIzraz) mi.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(mi));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(mi));
			mi.setTip(Tip.integer);
			mi.setlIzraz(false);
		}
	}


	public void accept(AditivniIzraz ai) {
		//visitChildren(ai);
		MultiplikativniIzraz x = (MultiplikativniIzraz) ai.children.get(ai.children.size() - 1);
		if (ai.children.get(0) instanceof MultiplikativniIzraz) {
			visitChildren(ai);
			ai.setTip(x.getTip());
			ai.setlIzraz(x.getlIzraz());
		} else {
			AditivniIzraz y = (AditivniIzraz) ai.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(ai));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(ai));
			ai.setTip(Tip.integer);
			ai.setlIzraz(false);
		}
	}


	public void accept(OdnosniIzraz oi) {
		//visitChildren(oi);
		AditivniIzraz x = (AditivniIzraz) oi.children.get(oi.children.size() - 1);
		if (oi.children.get(0) instanceof AditivniIzraz) {
			visitChildren(oi);
			oi.setTip(x.getTip());
			oi.setlIzraz(x.getlIzraz());
		} else {
			OdnosniIzraz y = (OdnosniIzraz) oi.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(oi));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(oi));
			oi.setTip(Tip.integer);
			oi.setlIzraz(false);
		}
	}


	public void accept(JednakosniIzraz ji) {
		//visitChildren(ji);
		OdnosniIzraz x = (OdnosniIzraz) ji.children.get(ji.children.size() - 1);
		if (ji.children.get(0) instanceof OdnosniIzraz) {
			visitChildren(ji);
			ji.setTip(x.getTip());
			ji.setlIzraz(x.getlIzraz());
		} else {
			JednakosniIzraz y = (JednakosniIzraz) ji.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(ji));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(ji));
			ji.setTip(Tip.integer);
			ji.setlIzraz(false);
		}
	}


	public void accept(BinIIzraz bii) {
		//visitChildren(bii);
		JednakosniIzraz x = (JednakosniIzraz) bii.children.get(bii.children.size() - 1);
		if (bii.children.get(0) instanceof JednakosniIzraz) {
			visitChildren(bii);
			bii.setTip(x.getTip());
			bii.setlIzraz(x.getlIzraz());
		} else {
			BinIIzraz y = (BinIIzraz) bii.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(bii));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(bii));
			bii.setTip(Tip.integer);
			bii.setlIzraz(false);
		}
	}


	public void accept(BinXiliIzraz bxi) {
		//visitChildren(bxi);
		BinIIzraz x = (BinIIzraz) bxi.children.get(bxi.children.size() - 1);
		if (bxi.children.get(0) instanceof BinIIzraz) {
			visitChildren(bxi);
			bxi.setTip(x.getTip());
			bxi.setlIzraz(x.getlIzraz());
		} else {
			BinXiliIzraz y = (BinXiliIzraz) bxi.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(bxi));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(bxi));
			bxi.setTip(Tip.integer);
			bxi.setlIzraz(false);
		}
	}


	public void accept(BinIliIzraz bii) {
		//visitChildren(bii);
		BinXiliIzraz x = (BinXiliIzraz) bii.children.get(bii.children.size() - 1);
		if (bii.children.get(0) instanceof BinXiliIzraz) {
			visitChildren(bii);
			bii.setTip(x.getTip());
			bii.setlIzraz(x.getlIzraz());
		} else {
			BinIliIzraz y = (BinIliIzraz) bii.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(bii));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(bii));
			bii.setTip(Tip.integer);
			bii.setlIzraz(false);
		}
	}


	public void accept(LogIIzraz lii) {
		//visitChildren(lii);
		BinIliIzraz x = (BinIliIzraz) lii.children.get(lii.children.size() - 1);
		if (lii.children.get(0) instanceof BinIliIzraz) {
			visitChildren(lii);
			lii.setTip(x.getTip());
			lii.setlIzraz(x.getlIzraz());
		} else {
			LogIIzraz y = (LogIIzraz) lii.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));
			lii.setTip(Tip.integer);
			lii.setlIzraz(false);
		}
	}


	public void accept(LogIliIzraz lii) {
		//visitChildren(lii);
		LogIIzraz x = (LogIIzraz) lii.children.get(lii.children.size() - 1);
		if (lii.children.get(0) instanceof LogIIzraz) {
			visitChildren(lii);
			lii.setTip(x.getTip());
			lii.setlIzraz(x.getlIzraz());
		} else {
			LogIliIzraz y = (LogIliIzraz) lii.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));
			lii.setTip(Tip.integer);
			lii.setlIzraz(false);
		}
	}


	public void accept(IzrazPridruzivanja ip) {
		//visitChildren(ip);
		if (ip.children.get(0) instanceof LogIliIzraz) {
			LogIliIzraz x = (LogIliIzraz) ip.children.get(ip.children.size() - 1);
			visitChildren(ip);
			ip.setTip(x.getTip());
			ip.setlIzraz(x.getlIzraz());
		} else {
			PostfiksIzraz x = (PostfiksIzraz) ip.children.get(0);
			IzrazPridruzivanja y = (IzrazPridruzivanja) ip.children.get(2);
			x.acceptVisitor(this);
			if (!x.getlIzraz()) throw new SemAnalysisException(generateMessage(ip));
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(x.getTip())) throw new SemAnalysisException(generateMessage(ip));
			ip.setTip(x.getTip());
			ip.setlIzraz(false);
		}
	}


	public void accept(Izraz i) {
		//visitChildren(i);
		IzrazPridruzivanja x = (IzrazPridruzivanja) i.children.get(i.children.size() - 1);
		if (i.children.get(0) instanceof IzrazPridruzivanja) {
			visitChildren(i);
			i.setTip(x.getTip());
			i.setlIzraz(x.getlIzraz());
		} else {
			visitChildren(i);
			i.setTip(x.getTip());
			i.setlIzraz(false);
		}
	}


	public void accept(SlozenaNaredba sn) {
		enterDeeper();
		if (sn.getParent() instanceof DefinicijaFunkcije) {
			DefinicijaFunkcije def = (DefinicijaFunkcije) sn.getParent();
			for (int i = 0; i < def.getParamNames().size(); i++) {
				initDeklaratori.peek().put(def.getParamNames().get(i), def.getFunc().args.get(i));
			}
		}
		visitChildren(sn);

		stepOut();
	}


	public void accept(ListaNaredbi ln) {//ok
		visitChildren(ln);
	}


	public void accept(Naredba n) {//ok
		visitChildren(n);
	}


	public void accept(IzrazNaredba in) {
		//visitChildren(in);
		if (!(in.children.get(0) instanceof Izraz)) {
			in.setTip(Tip.integer);
		} else {
			visitChildren(in);
			in.setTip(((Izraz) in.children.get(0)).getTip());
		}
	}


	public void accept(NaredbaGrananja ng) {
		//visitChildren(ng);
		ng.children.get(2).acceptVisitor(this);
		if (!((Izraz) ng.children.get(2)).getTip().isImplCastable(Tip.integer))
			throw new SemAnalysisException(generateMessage(ng));
		ng.children.get(4).acceptVisitor(this);
		if (ng.children.size() == 7) {
			ng.children.get(6).acceptVisitor(this);
		}
	}


	public void accept(NaredbaPetlje np) {
		//visitChildren(np);
		switch (np.children.size()) {
			case 5:
				np.children.get(2).acceptVisitor(this);
				if (!((Izraz) np.children.get(2)).getTip().isImplCastable(Tip.integer)) {
					throw new SemAnalysisException(generateMessage(np));
				}
				np.children.get(4).acceptVisitor(this);
				break;
			case 6:
				np.children.get(2).acceptVisitor(this);
				np.children.get(3).acceptVisitor(this);
				if (!((IzrazNaredba) np.children.get(3)).getTip().isImplCastable(Tip.integer)) {
					throw new SemAnalysisException(generateMessage(np));
				}
				np.children.get(5).acceptVisitor(this);
				break;
			case 7:
				np.children.get(2).acceptVisitor(this);
				np.children.get(3).acceptVisitor(this);
				if (!((IzrazNaredba) np.children.get(3)).getTip().isImplCastable(Tip.integer)) {
					throw new SemAnalysisException(generateMessage(np));
				}
				np.children.get(4).acceptVisitor(this);
				np.children.get(6).acceptVisitor(this);
				break;
			default:
				throw new SemAnalysisException(generateMessage(np));

		}
	}


	public void accept(NaredbaSkoka ns) {
		visitChildren(ns);

		if (ns.children.get(0) instanceof KR_RETURN) {
			Node n;
			for (n = ns.getParent(); n != null; n = n.getParent()) {
				if (n instanceof DefinicijaFunkcije) {
					DefinicijaFunkcije df = (DefinicijaFunkcije) n;
					//System.err.println(df.getFunc());

					if (ns.children.size() == 3) {
						if (!((Izraz) ns.children.get(1)).getTip().isImplCastable(df.getFunc().returnTip())) {
							throw new SemAnalysisException(generateMessage(ns));
						}
					} else {
						if (!df.getFunc().primitiv.equals(void.class)) {
							throw new SemAnalysisException(generateMessage(ns));
						}
					}
					break;
				}
			}

			if (n == null) {
				throw new SemAnalysisException(generateMessage(ns));
			}
		} else {
			Node n;
			for (n = ns.getParent(); n != null; n = n.getParent()) {
				if (n instanceof NaredbaPetlje) {
					break;
				}
			}

			if (n == null) {
				throw new SemAnalysisException(generateMessage(ns));
			}
		}
	}


	public void accept(PrijevodnaJedinica pj) {//ok
		if (pj.getDepth() == 0) {
			enterDeeper();
		}
		visitChildren(pj);
	}


	public void accept(VanjskaDeklaracija vd) {//ok
		visitChildren(vd);
	}


	public void accept(DefinicijaFunkcije df) {
		ImeTipa it = (ImeTipa) df.children.get(0);
		IDN idn = (IDN) df.children.get(1);
		Node uZagradi = df.children.get(3);

		it.acceptVisitor(this);
		if (it.getTip().isConstant) {
			//System.err.println("it.getTip().isConstant" + it.getTip().isConstant);
			throw new SemAnalysisException(generateMessage(df));
		}

		if (definedFunctions.containsKey(idn.getValue())) {
			//System.err.println("definedFunctions.containsKey(idn.getValue())" + definedFunctions.containsKey(idn.getValue()));
			throw new SemAnalysisException(generateMessage(df));
		}

		uZagradi.acceptVisitor(this);

		if (initDeklaratori.get(0).containsKey(idn.getValue())) {
			Tip buff = initDeklaratori.get(0).get(idn.getValue());

			if (uZagradi instanceof KR_VOID) {
				if (!isFunction(buff, it.getTip(), Collections.emptyList())) {
					//System.err.println("!isFunction(buff, it.getTip(), Collections.emptyList())");
					throw new SemAnalysisException(generateMessage(df));
				}
			} else if (uZagradi instanceof ListaParametara) {
				if (!isFunction(buff, it.getTip(), ((ListaParametara) uZagradi).getTips())) {
					//System.err.println("!isFunction(buff, it.getTip(), ((ListaParametara) uZagradi).getTips())");
					throw new SemAnalysisException(generateMessage(df));
				}
			} else {
				throw new IllegalStateException(generateMessage(df));
			}
		}
		Funkcija func = new Funkcija(
			  idn.getValue(),
			  it.getTip().primitiv,
			  uZagradi instanceof KR_VOID ?
				    Collections.emptyList() : ((ListaParametara) uZagradi).getTips());

		definedFunctions.put(idn.getValue(), func);

		df.setFunc(func);
		df.setParamNames(uZagradi instanceof KR_VOID ? Collections.emptyList() : ((ListaParametara) uZagradi).getIdns());
		initDeklaratori.peek().putIfAbsent(idn.getValue(), func);

		df.getChildren().get(5).acceptVisitor(this);
	}


	public void accept(ListaParametara lp) {
		visitChildren(lp);

		if (lp.children.size() == 1) {
			DeklaracijaParametra dp = (DeklaracijaParametra) lp.children.get(0);
			lp.setTips(Collections.singletonList(dp.getTip()));
			lp.setIdns(Collections.singletonList(dp.getIdn()));
		} else {
			ListaParametara lp2 = (ListaParametara) lp.children.get(0);
			DeklaracijaParametra dp = (DeklaracijaParametra) lp.children.get(2);
			if (lp2.getIdns().contains(dp.getIdn())) {
				throw new SemAnalysisException(generateMessage(lp));
			}

			List<Tip> types = new ArrayList<>();
			types.addAll(lp2.getTips());
			types.add(dp.getTip());

			List<String> idns = new ArrayList<>();
			idns.addAll(lp2.getIdns());
			idns.add(dp.getIdn());

			lp.setTips(types);
			lp.setIdns(idns);
		}
	}


	public void accept(DeklaracijaParametra dp) {
		visitChildren(dp);

		ImeTipa im = (ImeTipa) dp.children.get(0);

		if (im.getTip().equals(Tip.voidType)) {
			throw new SemAnalysisException(generateMessage(dp));
		}

		//dp.setTip():
		if (dp.children.size() == 2) {
			dp.setTip(im.getTip());
		} else {
			if (im.getTip().primitiv.equals(int.class)) {
				if (im.getTip().isConstant) {
					dp.setTip(Tip.constIntA);
				} else {
					dp.setTip(Tip.intA);
				}
			} else if (im.getTip().primitiv.equals(char.class)) {
				if (im.getTip().isConstant) {
					dp.setTip(Tip.constCharA);
				} else {
					dp.setTip(Tip.charA);
				}
			} else {
				throw new IllegalStateException(generateMessage(dp));
			}
		}

		dp.setIdn(((IDN) dp.children.get(1)).getValue());
	}


	public void accept(ListaDeklaracija ld) {
		visitChildren(ld);//ok
	}


	public void accept(Deklaracija d) {
		d.children.get(0).acceptVisitor(this);
		((ListaInitDeklaratora) d.children.get(1)).setNtip(((ImeTipa) d.children.get(0)).getTip());
		d.children.get(1).acceptVisitor(this);
	}


	public void accept(ListaInitDeklaratora lid) {
		if (lid.children.size() != 1) {
			((ListaInitDeklaratora) lid.children.get(0)).setNtip(lid.getNtip());
		}

		((InitDeklarator) lid.children.get(lid.children.size() - 1)).setNtip(lid.getNtip());
		visitChildren(lid);
	}


	public void accept(InitDeklarator id) {
		boolean isIzravni = id.children.size() == 1;
		IzravniDeklarator iz = (IzravniDeklarator) id.children.get(0);
		iz.setNtip(id.getNtip());
		visitChildren(id);

		if (isIzravni) {
			if (iz.getTip().isConstant) {
				throw new SemAnalysisException(generateMessage(id));
			}
		} else {
			Inicijalizator i = (Inicijalizator) id.children.get(2);
			if (iz.getTip().primitiv.equals(int.class) || iz.getTip().primitiv.equals(char.class)) {
				if (!i.getTip().isImplCastable(iz.getTip())) {
					throw new SemAnalysisException(generateMessage(id));
				}
			} else if (iz.getTip().primitiv.equals(int[].class) || iz.getTip().primitiv.equals(char[].class)) {
				if (i.getTips().size() > iz.getWhatInBracket()) {
					throw new SemAnalysisException(generateMessage(id));
				}

				i.getTips().forEach(tip -> {
					if (!tip.isImplCastable(iz.getTip().getPrimitivTip())) {
						throw new SemAnalysisException(generateMessage(id));
					}
				});
			} else {
				throw new SemAnalysisException(generateMessage(id));
			}
		}
	}


	public void accept(IzravniDeklarator id) {
		visitChildren(id);
		IDN idn = (IDN) id.children.get(0);

		if (id.children.size() == 1 || id.children.get(1) instanceof L_UGL_ZAGRADA) {
			if (id.getNtip().equals(Tip.voidType)) {
				throw new SemAnalysisException(generateMessage(id));
			}

			if (initDeklaratori.peek().containsKey(idn.getValue())) {
				throw new SemAnalysisException(generateMessage(id));
			}

			if (id.children.size() > 1) {
				if (((BROJ) id.children.get(2)).getIntValue() > 1024) {
					throw new SemAnalysisException(generateMessage(id));
				}

				//NETREBA br-elem <- BROJ.vrijedonst, jer se to metodom rijesava
			}

			initDeklaratori.peek().put(idn.getValue(), id.getNtip());
			if (id.children.size() > 1) {
				id.setTip(id.getNtip().getArrayTip());
			} else {
				id.setTip(id.getNtip());
			}
		} else if (id.children.get(1) instanceof L_ZAGRADA) {
			Funkcija func;
			Node uZagradi = id.children.get(2);
			//skoro Copy-paste os DefinicijaFunckije
			if (initDeklaratori.peek().containsKey(idn.getValue())) {
				Tip buff = initDeklaratori.peek().get(idn.getValue());

				if (uZagradi instanceof KR_VOID) {
					if (!isFunction(buff, id.getNtip(), Collections.emptyList())) {
						throw new SemAnalysisException(generateMessage(id));
					}
				} else if (uZagradi instanceof ListaParametara) {
					if (!isFunction(buff, id.getNtip(), ((ListaParametara) uZagradi).getTips())) {
						throw new SemAnalysisException(generateMessage(id));
					}
				} else {
					throw new IllegalStateException(generateMessage(id));
				}

				func = (Funkcija) buff;
			} else {
				func = new Funkcija(
					  idn.getValue(),
					  id.getNtip().primitiv,
					  uZagradi instanceof KR_VOID ?
						    Collections.emptyList() :
						    ((ListaParametara) uZagradi).getTips());
				initDeklaratori.peek().put(idn.getValue(), func);
				allDeclaredFunctions.add(func);
			}

			id.setTip(func);
		}
	}


	public void accept(Inicijalizator i) {
		visitChildren(i);

		if (i.children.size() == 1) {
			Node buff;
			NezavrsniZnak nz;
			for (buff = i.children.get(0); ; buff = nz.children.get(0)) {
				if (buff instanceof NezavrsniZnak) {
					nz = (NezavrsniZnak) buff;
					if (nz.children.size() > 1) {
						throw new SemAnalysisException(generateMessage(i));
					}
					continue;
				}

				break;
			}

			if (buff instanceof NIZ_ZNAKOVA) {
				List<Tip> tips = new ArrayList<>();
				for (int a = 0; a < ((NIZ_ZNAKOVA) buff).getCharacters().length; a++) {
					tips.add(Tip.character);
				}

				i.setTips(tips);
				i.setTip(Tip.charA);
			} else {
				i.setTip(((IzrazPridruzivanja) i.children.get(0)).getTip());
			}
		} else {
			ListaIzrazaPridruzivanja lip = (ListaIzrazaPridruzivanja) i.children.get(1);
			i.setTips(lip.getTips());
			i.setTip(lip.getTips().get(0));//todo mozda lose
		}
	}


	public void accept(ListaIzrazaPridruzivanja lip) {
		visitChildren(lip);

		if (lip.children.size() == 1) {
			lip.setTips(Collections.singletonList(((IzrazPridruzivanja) lip.children.get(0)).getTip()));
		} else {
			List<Tip> tips = new ArrayList<>(((ListaIzrazaPridruzivanja) lip.children.get(0)).getTips());
			tips.add(((IzrazPridruzivanja) lip.children.get(lip.children.size() - 1)).getTip());
			lip.setTips(tips);
		}
	}


	private char[] checkCharacters(String value, boolean isChar) {
		StringBuilder bob = new StringBuilder();
		value = value.substring(1, value.length() - 1);


		if (isChar && (value.length() > 2 || value.length() == 2 && value.charAt(0) != '\\')) {
			throw new IllegalStateException(value + " is too long");
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
					throw new IllegalStateException(value + " contains Illegal characters");
				}

				bob.append(escaped);
			} catch (IndexOutOfBoundsException e) {
				throw new IllegalStateException("Illegal use(tried to end literal) of `\\` in: " + value, e);
			}
		}

		char[] result = new char[bob.length() + (isChar ? 0 : 1)];
		result[result.length - 1] = '\0';
		System.arraycopy(bob.toString().toCharArray(), 0, result, 0, bob.length());

		return result;
	}

	private String generateMessage(NezavrsniZnak nz) {
		StringBuilder bob = new StringBuilder();

		bob.append(nz);
		bob.append(" ::=");
		nz.children.forEach(c -> {
			bob.append(' ');
			bob.append(c);
		});

		NodeVisitor nv = new TreePrintVisitor();
		nz.acceptVisitor(nv);

		return bob.toString();
	}

	private void enterDeeper() {
		initDeklaratori.push(new HashMap<>());
	}

	private void stepOut() {
		initDeklaratori.pop();
	}

	//only for void in params
	private boolean isFunction(Tip t, Tip expectedRet) {
		if (t instanceof Funkcija) {
			Funkcija buff = (Funkcija) t;
			if (buff.primitiv.equals(expectedRet.primitiv)) {
				return true;
			}
		}

		return false;
	}


	private boolean isFunction(Tip t, Tip expectedRet, List<Tip> args) {
		if (t instanceof Funkcija) {
			Funkcija buff = (Funkcija) t;

			if (expectedRet != null && !buff.primitiv.equals(expectedRet.primitiv)) {
				return false;
			}

			if (buff.args.size() != args.size()) {
				return false;
			}

			for (int i = 0; i < args.size(); i++) {
				if (!buff.args.get(i).equals(args.get(i))) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private Tip getIDeklarator(String value) {
		Tip tip = initDeklaratori.peek().get(value);
		int i = initDeklaratori.size() - 2;
		while (tip == null && i >= 0) {
			tip = initDeklaratori.get(i).get(value);
			i--;
		}

		return tip;
	}

	/**
	 * @param t some NONE ARRAY TYPE
	 * @return
	 */
	private Tip toConst(Tip t) {
		if (t.equals(Tip.integer)) {
			return Tip.constInteger;
		} else if (t.equals(Tip.character)) {
			return Tip.constCharacter;
		} else {
			throw new IllegalStateException("Doont call this with anything excpet int and char");
		}
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
		visitChildren(nz);
		indent--;
	}

	private void printZav(ZavrsniZnak zz) {
		printWithIndent(zz);
	}

	private void printWithIndent(Object o) {
		System.err.printf("%" + ((indent == 0) ? "" : Integer.toString(indent)) + "s%s%n", "", o);

	}
}
