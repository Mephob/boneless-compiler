import java.util.*;

class Assembler implements NodeVisitor {

	static private Map<Character, Character> legalEscapes = new HashMap<>();

	static private Map<Class<? extends ZavrsniZnak>, Tip> legalTypes = new HashMap<>();

	private Map<String, Funkcija> definedFunctions = new HashMap<>();

	//private Stack<Map<String, Object>> values = new Stack<>();

	private Stack<Map<String, Tip>> initDeklaratori = new Stack<>();

	private List<Funkcija> allDeclaredFunctions = new ArrayList<>();

	InstructionBuilder instructions = new InstructionBuilder();

	private int br = 0;

	private Stack<Integer> looId = new Stack<>();

	int onStack = 0;

	private Stack<Map<String, String>> addresses = new Stack<>();

	//NO! private int stackPointer = 0x400000;

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
					throw new SemAnalysisException("main");
				}
				return f;
			}
		}

		throw new SemAnalysisException("main");
	}

	public boolean areAllDeclaredFunctionsDefined() {
		for (Funkcija f : allDeclaredFunctions) {
			if (definedFunctions.get(f.name) == null) {
				throw new SemAnalysisException("funkcija");
			}

			Funkcija f2 = (Funkcija) definedFunctions.get(f.name);

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
			pi.setlIzraz(!buff.isConstant && !(buff instanceof Funkcija) && !buff.primitiv.isArray());

			String adress = "";
			out: for (int i = initDeklaratori.size() - 1; i > 0; i--) {
				for (String s : initDeklaratori.get(i).keySet()) {
					if (s.equals(((IDN) prvi).getValue())) {
						adress = addresses.get(i).get(s);
						break out;
					}
				}
			}

			if (!adress.isEmpty()) {
				String[] a = adress.split(" ");
				if (a.length == 1) {
					instructions.addInstruction(String.format(" MOVE %s, R0", adress));
				} else {
					instructions.addInstruction(" MOVE SP, R0");
					if (a[1].equals("+")) {
						instructions.addInstruction(" ADD R0, " + a[2] + ", R0");
					} else {
						instructions.addInstruction(" SUB R0, " + a[2] + ", R0");
					}
				}
				addPUSH("R0");
			}

			pi.isAdress = true;
			return;
		} else if (prvi instanceof BROJ) {
			pi.setTip(Tip.integer);
			instructions.addInstruction(String.format(" MOVE 0%s, R0", Integer.toHexString(((BROJ) prvi).getIntValue())));
			addPUSH("R0");
		} else if (prvi instanceof ZNAK) {
			pi.setTip(Tip.character);
			instructions.addInstruction(" MOVE " + Integer.toString(((ZNAK) prvi).getCharValue()) + ", R0" );
			addPUSH("R0");
		} else if (prvi instanceof NIZ_ZNAKOVA) {
			pi.setTip(Tip.constCharA);
			char[] chars = ((NIZ_ZNAKOVA) prvi).getCharacters();
			for (int n = 0; n < chars.length; n++) {
				instructions.addInstruction(" MOVE " + Integer.toString(chars[n]) + ", R0");
				addPUSH("R0");
			}
		} else if (prvi instanceof L_ZAGRADA) {
			Izraz i = (Izraz) pi.children.get(1);
			pi.setTip(i.getTip());
			pi.setlIzraz(i.getlIzraz());
			//izraz bu pushal svoje
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
			pi.isAdress = buff.isAdress;
			return;
		} else if (pi.children.size() == 2) {
			visitChildren(pi);
			PostfiksIzraz buff = (PostfiksIzraz) pi.children.get(0);

			if (!buff.getlIzraz() && buff.getTip().isImplCastable(Tip.integer)) {
				throw new SemAnalysisException(generateMessage(pi));
			}
			pi.setTip(Tip.integer);
			pi.setlIzraz(false);

			//new
			addPOP("R1");
			instructions.addInstruction(" LOAD R0, (R1)");

			addPUSH("R0");
			if (pi.children.get(1) instanceof OP_INC) {
				instructions.addInstruction(" ADD R0, 1, R0");
			} else {
				instructions.addInstruction(" SUB R0, 1, R0");
			}
			instructions.addInstruction(" STORE R0, (R1)");

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
			if (prviTip.primitiv.equals(int[].class)) {
				if (prviTip.isConstant) {
					pi.setTip(Tip.constInteger);
				} else {
					pi.setTip(Tip.integer);
				}
			} else if (prviTip.primitiv.equals(char[].class)) {
				if (prviTip.isConstant) {
					pi.setTip(Tip.constCharacter);
				} else {
					pi.setTip(Tip.character);
				}
			} else {
				throw new IllegalStateException(generateMessage(pi));
			}

			pi.setlIzraz(!pi.getTip().isConstant);

			addPOP("R1");
			instructions.addInstruction(" SHL R1, 2, R1");
			addPOP("R0");

			instructions.addInstruction(" ADD R0, R1, R0");
			addPUSH("R0");

			pi.isAdress = true;
			return;
		} else if (drugi instanceof L_ZAGRADA && treci instanceof D_ZAGRADA) {
			if (!isFunction(prvi.getTip(), null, Collections.emptyList())) {
				throw new SemAnalysisException(generateMessage(pi));
			}

			//##################poziv funkcije
			addCALL(((Funkcija) prvi.getTip()).name, false);
		} else if (drugi instanceof L_ZAGRADA && treci instanceof ListaArgumenata) {

			int buff = onStack;

			treci.acceptVisitor(this);
			if (!isFunction(prvi.getTip(), null, ((ListaArgumenata) treci).getTips())) {
				throw new SemAnalysisException(generateMessage(pi));
			}

			ListaArgumenata la = (ListaArgumenata) treci;
			//###########################poziv funkcije
			//push je napravljen u listaArgumenata
			addCALL(((Funkcija) prvi.getTip()).name, false);

			buff = onStack - buff;
			moveSP(buff, true);
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
			//ako adresa promijenit u vrijednost ali samo ak ti caca nije unarni_izraz
			if (!(ui.getParent() instanceof UnarniIzraz) && ((PostfiksIzraz) ui.children.get(0)).isAdress) {
				addPOP("R0");
				instructions.addInstruction(" LOAD R1, (R0)");
				addPUSH("R1");
			}
			return;
		} else if (ui.children.get(ui.children.size() - 1) instanceof UnarniIzraz) {
			visitChildren(ui);
			UnarniIzraz uib = (UnarniIzraz) ui.children.get(ui.children.size() - 1);

			if (!uib.getlIzraz() || !uib.getTip().isImplCastable(Tip.integer) ) {
				throw new SemAnalysisException(generateMessage(ui));
			}

			//new
			addPOP("R1");
			instructions.addInstruction(" LOAD R0, (R1)");
			if (ui.children.get(0) instanceof OP_INC) {
				instructions.addInstruction(" ADD R0, 1, R0");
			} else {
				instructions.addInstruction(" SUB R0, 1, R0");
			}
			addPUSH("R0");
			instructions.addInstruction(" STORE R0, (R1)");
		} else if (ui.children.get(ui.children.size() - 1) instanceof CastIzraz) {
			ui.children.get(ui.children.size() - 1).acceptVisitor(this);
			((CastIzraz) ui.children.get(ui.children.size() - 1)).setTip(Tip.integer);

			//#############################3
			UnarniOperator uo = (UnarniOperator) ui.children.get(0);

			addPOP("R0");
			if (uo.children.get(0) instanceof OP_NEG) {
				//stack overflow
				//(((x>>1) | (x&1)) + ~0U) >> 31
				instructions.addInstruction(" MOVE R0, R1");
				instructions.addInstruction(" SHR R1, 1, R1");
				instructions.addInstruction(" AND R0, 1, R0");
				instructions.addInstruction(" OR R0, R1, R0");
				instructions.addInstruction(" SUB R0, 1, R0");
				instructions.addInstruction(" SHR R0, %D 31, R0");
			} else if (uo.children.get(0) instanceof MINUS) {
				instructions.addInstruction(" MOVE 0, R1");
				instructions.addInstruction(" SUB R1, R0, R0");
			} else if (uo.children.get(0) instanceof OP_TILDA) {
				instructions.addInstruction(" XOR R0, -1, R0");
			}

			addPUSH("R0");
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
			ImeTipa it = (ImeTipa) ci.children.get(1);
			if (!cib.getTip().isExplCastable(it.getTip())) {
				throw new SemAnalysisException(generateMessage(ci));
			}

			ci.setTip(it.getTip());
			ci.setlIzraz(false);
		}
	}


	public void accept(ImeTipa it) {
		visitChildren(it);

		if (it.children.get(0) instanceof KR_CONST) {
			if (((SpecifikatorTipa) it.children.get(1)).getTip().equals(Tip.voidType)) {
				throw new SemAnalysisException(generateMessage(it));
			}

			try {
				it.setTip(toConst(((SpecifikatorTipa) it.children.get(1)).getTip()));
			} catch (IllegalStateException e) {
				throw new SemAnalysisException(generateMessage(it));
			}
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

			//###################################################################
			if (mi.children.get(1) instanceof OP_PUTA) {
				addCALL("MUL", true);
			} else if (mi.children.get(1) instanceof OP_DIJELI) {
				addCALL("DIV", true);
			} else if (mi.children.get(1) instanceof OP_MOD) {
				addCALL("MOD", true);
			}

			//clearStackOf(8, true);
			moveSP(8, true);
			addPUSH("R6");
			//povratna vrijednost je na stogu a argumenti su maknuti
			//tako da se u mapi dinamiski generira kaj treba zapravo pisati
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

			addPOP("R0");
			addPOP("R1");
			if (ai.children.get(1) instanceof PLUS) {
				instructions.addInstruction(" ADD R1, R0, R0");
			} else {
				instructions.addInstruction(" SUB R1, R0, R0");
			}
			addPUSH("R0");
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
			int broj = br;
			br++;

			OdnosniIzraz y = (OdnosniIzraz) oi.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(oi));
			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(oi));
			oi.setTip(Tip.integer);
			oi.setlIzraz(false);

			//new
			addPOP("R1");
			addPOP("R0");
			instructions.addInstruction(" CMP R0, R1");
			if (oi.children.get(1) instanceof OP_LT) {
				instructions.addInstruction(" JP_SLT OI" + broj);
			} else if (oi.children.get(1) instanceof OP_GT) {
				instructions.addInstruction(" JP_SGT OI" + broj);
			} else if (oi.children.get(1) instanceof OP_LTE) {
				instructions.addInstruction(" JP_SLE OI" + broj);
			} else {
				instructions.addInstruction(" JP_SGE OI" + broj);
			}

			instructions.addInstruction(" MOVE 0, R0");
			addPUSH("R0");
			instructions.addInstruction(" JP OIEND" + broj);
			instructions.addInstruction("OI" + broj + " MOVE -1, R0");
			instructions.addInstruction(" PUSH R0"); //tu ne addPush jer ce mislit da je 2 put pushano ali samo je jednom
			//addPUSH("R0");
			instructions.addInstruction("OIEND" + broj);
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

			int broj = br;
			br++;
			//new
			addPOP("R1");
			addPOP("R0");
			instructions.addInstruction(" CMP R0, R1");
			if (ji.children.get(1) instanceof OP_EQ) {
				instructions.addInstruction(" JP_SLT JI" + broj);
			} else if (ji.children.get(1) instanceof OP_NEG) {
				instructions.addInstruction(" JP_SGT JI" + broj);
			}

			instructions.addInstruction(" MOVE 0, R0");
			addPUSH("R0");
			instructions.addInstruction(" JP JIEND" + broj);
			instructions.addInstruction("JI" + broj + " MOVE -1, R0");
			instructions.addInstruction(" PUSH R0"); //tu ne addPush jer ce mislit da je 2 put pushano ali samo je jednom
			//addPUSH("R0");
			instructions.addInstruction("JIEND" + broj);
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
			//<<<<__>>>> OVO DODAO
			//valjda ide u ovaj blok a ne dolje kod tode?
			addPOP("R0");
			addPOP("R1");
			instructions.addInstruction(" AND R1, R0, R0");
			addPUSH("R0");
			//<<<<__>>>> KRAJ DODANOG
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
			//<<<<__>>>> OVO DODAO
			//valjda ide u ovaj blok a ne dolje kod tode?
			addPOP("R0");
			addPOP("R1");
			instructions.addInstruction(" XOR R1, R0, R0");
			addPUSH("R0");
			//<<<<__>>>> KRAJ DODANOG
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
			//<<<<__>>>> OVO DODAO
			//valjda ide u ovaj blok a ne dolje kod tode?
			addPOP("R0");
			addPOP("R1");
			instructions.addInstruction(" OR R1, R0, R0");
			addPUSH("R0");
			//<<<<__>>>> KRAJ DODANOG
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
			int broj = br;
			br++;

			LogIIzraz y = (LogIIzraz) lii.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));

			addPOP("R0");
			instructions.addInstruction(" MOVE 0, R2");
			instructions.addInstruction(" CMP R0, R2");
			instructions.addInstruction(" JP_EQ LOGIFALSE" + broj);

			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));
			lii.setTip(Tip.integer);
			lii.setlIzraz(false);

			addPOP("R1");
			instructions.addInstruction(" CMP R1, R2");
			instructions.addInstruction(" JP_EQ LOGIFALSE" + broj);
			instructions.addInstruction(" MOVE -1, R3");
			instructions.addInstruction(" JP LOGIEND" + broj);

			instructions.addInstruction("LOGIFALSE" + broj + " MOVE 0, R3");

			instructions.addInstruction("LOGIEND" + broj);
			addPUSH("R3");

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
			int broj = br;
			br++;

			LogIliIzraz y = (LogIliIzraz) lii.children.get(0);
			y.acceptVisitor(this);
			if (!y.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));

			addPOP("R0");
			instructions.addInstruction(" MOVE 0, R2");
			instructions.addInstruction(" CMP R0, R2");
			instructions.addInstruction(" JP_NEQ LOGILITRUE" + broj);

			x.acceptVisitor(this);
			if (!x.getTip().isImplCastable(Tip.integer))
				throw new SemAnalysisException(generateMessage(lii));
			lii.setTip(Tip.integer);
			lii.setlIzraz(false);

			addPOP("R1");
			instructions.addInstruction(" CMP R1, R2");
			instructions.addInstruction(" JP_NEQ LOGILITRUE" + broj);
			instructions.addInstruction(" MOVE 0, R3");
			instructions.addInstruction(" JP LOGILIEND" + broj);

			instructions.addInstruction("LOGILITRUE" + broj + " MOVE -1, R3");

			instructions.addInstruction("LOGILIEND" + broj);
			addPUSH("R3");
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

			addPOP("R1");
			addPOP("R0");
			instructions.addInstruction(" STORE R1, (R0)");
			addPUSH("R1");//pusha vrijednost tog na stog za ulancavanje izraza pridruzivanja
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
			i.children.get(0).acceptVisitor(this);

			addPOP("R0");

			i.children.get(1).acceptVisitor(this);
			i.setTip(x.getTip());
			i.setlIzraz(false);
		}
	}


	public void accept(SlozenaNaredba sn) {
		enterDeeper();
		if (sn.getParent() instanceof DefinicijaFunkcije) {
			DefinicijaFunkcije def = (DefinicijaFunkcije) sn.getParent();
			for (int i = 0, j = def.getParamNames().size() - 1; i < def.getParamNames().size(); i++, j--) {
				initDeklaratori.peek().put(def.getParamNames().get(i), def.getFunc().args.get(i));
				//################################################################################
				// push na stog, ici kroz razine, naci variablu, i uzet sa te adrese
				//mislim da ne
				addresses.peek().put(def.getParamNames().get(i), "SP + " + Integer.toHexString((def.getParamNames().size() - 1 - i)*4));
			}
		}

		sn.onStack = onStack;
		visitChildren(sn);
		//#######################################
		//new
		if (!(sn.getParent() instanceof DefinicijaFunkcije) && sn.children.get(1) instanceof ListaDeklaracija) {
			moveSP(((ListaDeklaracija)sn.children.get(1)).size, true);
		}

		stepOut();
	}


	public void accept(ListaNaredbi ln) {//ok
		visitChildren(ln);
	}


	public void accept(Naredba n) {//ok
		visitChildren(n);
		if (n.children.get(0) instanceof IzrazNaredba) {
			moveSP(4, true);
		}
	}


	public void accept(IzrazNaredba in) {
		//visitChildren(in);
		if (!(in.children.get(0) instanceof Izraz)) {
			in.setTip(Tip.integer);
		} else {
			visitChildren(in);
			in.setTip(((Izraz) in.children.get(0)).getTip());
			//addPOP("R0"); //ne tu nego u naredba
		}
	}


	public void accept(NaredbaGrananja ng) {
		//visitChildren(ng);
		int broj = br;
		br++;

		ng.children.get(2).acceptVisitor(this);

		addPOP("R0");
		instructions.addInstruction(" CMP R0, 0");
		instructions.addInstruction(" JP_EQ ifend" + broj);

		if (!((Izraz) ng.children.get(2)).getTip().isImplCastable(Tip.integer))
			throw new SemAnalysisException(generateMessage(ng));

		instructions.addInstruction("infend" + broj);

		ng.children.get(4).acceptVisitor(this);
		if (ng.children.size() == 7) {
			ng.children.get(6).acceptVisitor(this);
		}
	}


	public void accept(NaredbaPetlje np) {
		//visitChildren(np);
		int broj = br;
		br++;
		instructions.addInstruction(";;petljabr:" + br);
		looId.push(broj);
		switch (np.children.size()) {
			case 5:
				np.children.get(2).acceptVisitor(this);
				if (!((Izraz) np.children.get(2)).getTip().isImplCastable(Tip.integer)) {
					throw new SemAnalysisException(generateMessage(np));
				}
				//novo
				instructions.addInstruction("petlja" + broj);
				addPOP("R0");
				instructions.addInstruction(" CMP R0, 0");
				instructions.addInstruction(" JP_EQ petljafin" + broj);
				//bvbjfbjrfv
				int buff = onStack;
				np.children.get(4).acceptVisitor(this);

				instructions.addInstruction("petljainkr" + broj +" JP petlja" +  broj);
				instructions.addInstruction("petljafin" + broj);
				buff = onStack - buff;
				moveSP(buff, true);
				break;
			case 6:
				np.children.get(2).acceptVisitor(this);
				//novo
				moveSP(4, true);
				instructions.addInstruction("petlja" + broj);
				np.children.get(3).acceptVisitor(this);
				addPOP("R0");

				instructions.addInstruction(" CMP R0, 0");
				instructions.addInstruction(" JP_EQ petljafin" + broj);

				if (!((IzrazNaredba) np.children.get(3)).getTip().isImplCastable(Tip.integer)) {
					throw new SemAnalysisException(generateMessage(np));
				}

				//int buff6 = onStack;
				np.children.get(5).acceptVisitor(this);
				//buff6 = onStack - buff6;
				//moveSP(buff6, true);
				instructions.addInstruction("petljainkr" + broj +" JP petlja" + broj);
				instructions.addInstruction("petljafin" + broj);

				break;
			case 7:
				np.children.get(2).acceptVisitor(this);
				//novo
				moveSP(4, true);
				instructions.addInstruction("petlja" + broj);
				np.children.get(3).acceptVisitor(this);
				addPOP("R0");

				instructions.addInstruction(" CMP R0, 0");
				instructions.addInstruction(" JP_EQ petljafin" + broj);

				if (!((IzrazNaredba) np.children.get(3)).getTip().isImplCastable(Tip.integer)) {
					throw new SemAnalysisException(generateMessage(np));
				}
				//zamijenjeno tu
				np.children.get(6).acceptVisitor(this);
				instructions.addInstruction("petljainkr" + broj);

				int buff7 = onStack;
				np.children.get(4).acceptVisitor(this);
				buff7 = onStack - buff7;
				moveSP(buff7, true);
				instructions.addInstruction(" JP petlja" + broj);
				instructions.addInstruction("petljafin" + broj);

				//<<<<<<<<<<<<<<<<<<<<<<<
				break;
			default:
				throw new SemAnalysisException(generateMessage(np));

		}

		looId.pop();
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

			DefinicijaFunkcije df = (DefinicijaFunkcije) n;
			addPOP("R6");//vrati to prek R6
			moveSP(onStack - ((SlozenaNaredba) df.children.get(df.children.size() - 1)).onStack, true);
			addRET();
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

			if (ns.children.get(0) instanceof KR_CONTINUE) {
				instructions.addInstruction(" JP petljainkr" + looId.peek());
			} else {
				instructions.addInstruction(" JP petljafin" + looId.peek());
			}
		}
	}


	public void accept(PrijevodnaJedinica pj) {//ok
		if (pj.getDepth() == 0) {
			instructions.addInstruction(" MOVE 40000, R7");
			instructions.addInstruction(" CALL main");
			//instructions.addInstruction(" POP R6");
			instructions.addInstruction(" HALT");
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
		//################################################################################

		//new
		instructions.addInstruction(idn.getValue());//generira labelu
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
		//######################################################################
		//ne????? milsim da ne
		// dodaj u generator koda
		//ako stog.size == 1 generiraj globalnu(labela + vrijednost)
		//ako lokalna push na stog

	}


	public void accept(ListaDeklaracija ld) {
		visitChildren(ld);//ok
		if (ld.children.get(0) instanceof ListaDeklaracija) {
			ld.size += ((ListaDeklaracija) ld.children.get(0)).size;
		}

		ld.size += ((ListaInitDeklaratora)((Deklaracija) ld.children.get(ld.children.size() - 1)).children.get(1)).size;
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

		if (lid.children.get(0) instanceof ListaInitDeklaratora) {
			lid.size += ((ListaInitDeklaratora) lid.children.get(0)).size;
		}

		lid.size += ((IzravniDeklarator)((InitDeklarator) lid.children.get(lid.children.size() - 1)).children.get(0)).size;
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
				if (i.getTips() == null || i.getTips().size() > iz.getWhatInBracket()) {
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
		instructions.addInstruction(";;;;;;;;;deklaracija " + idn);

		if (id.children.size() == 1 || id.children.get(1) instanceof L_UGL_ZAGRADA) {
			if (id.getNtip().equals(Tip.voidType)) {
				throw new SemAnalysisException(generateMessage(id));
			}

			if (initDeklaratori.peek().containsKey(idn.getValue())) {
				throw new SemAnalysisException(generateMessage(id));
			}

			if (id.children.size() > 1) {
				if (((BROJ) id.children.get(2)).getIntValue() > 1024 || ((BROJ) id.children.get(2)).getIntValue() <= 0) {
					throw new SemAnalysisException(generateMessage(id));
				}
				//id.size = ((BROJ) id.children.get(2)).getIntValue()*4;

				//NETREBA br-elem <- BROJ.vrijedonst, jer se to metodom rijesava
				initDeklaratori.peek().put(idn.getValue(), id.getNtip().getArrayTip());
			} else {
				initDeklaratori.peek().put(idn.getValue(), id.getNtip());
				//id.size = 4;
			}

			//initDeklaratori.peek().put(idn.getValue(), id.getNtip());
			if (id.children.size() > 1) {
				id.setTip(id.getNtip().getArrayTip());
			} else {
				id.setTip(id.getNtip());
			}

			if (initDeklaratori.size() == 1) {//globalna variable, labele
				if (id.children.size() > 1) {
					id.size = id.getWhatInBracket();
					initDeklaratori.peek().get(idn.getValue()).size = id.getWhatInBracket()*4;
					//initDeklaratori.peek().get(idn.getValue()).adress = idn.getValue();
					addresses.peek().put(idn.getValue(), idn.getValue());
					instructions.addInstruction(idn.getValue());//genrirra labelu
					for (int i = 0; i < id.getWhatInBracket(); i++) {
						instructions.addInstruction("DW %D 0");
					}
				} else {
					id.size = 4;
					initDeklaratori.peek().get(idn.getValue()).size = 4;
					//initDeklaratori.peek().get(idn.getValue()).adress = idn.getValue();
					addresses.peek().put(idn.getValue(), idn.getValue());

					instructions.addInstruction(idn.getValue() + " DW %D 0");//generira labelu
				}
			} else {//lokalne variable
				if (id.children.size() > 1) {
					id.size = id.getWhatInBracket();
					moveSP(-id.getWhatInBracket()*4, true);
					//initDeklaratori.peek().get(idn.getValue()).adress = "SP + 0";
					addresses.peek().put(idn.getValue(), "SP + 0");
					initDeklaratori.peek().get(idn.getValue()).size = id.getWhatInBracket();
				} else {
					id.size = 4;
					instructions.addInstruction(" MOVE 0, R0");
					addPUSH("R0");
					//initDeklaratori.peek().get(idn.getValue()).adress = "SP + 0";
					addresses.peek().put(idn.getValue(), "SP + 0");
					initDeklaratori.peek().get(idn.getValue()).size = 4;
				}
			}
		} else if (id.children.get(1) instanceof L_ZAGRADA) {
			Funkcija func;
			Node uZagradi = id.children.get(2);
			//skoro Copy-paste os DefinicijaFunckije
			if (initDeklaratori.peek().containsKey(idn.getValue())) {
				Tip buff = initDeklaratori.peek().get(idn.getValue());

				if (uZagradi instanceof KR_VOID) {
					if (!isFunctionIdentical(buff, id.getNtip(), Collections.emptyList())) {
						throw new SemAnalysisException(generateMessage(id));
					}
				} else if (uZagradi instanceof ListaParametara) {
					if (!isFunctionIdentical(buff, id.getNtip(), ((ListaParametara) uZagradi).getTips())) {
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
			boolean canBeNiz = true;
			for (buff = i.children.get(0); ; buff = nz.children.get(0)) {
				if (buff instanceof NezavrsniZnak) {
					nz = (NezavrsniZnak) buff;
					if (nz.children.size() > 1) {
						//throw new SemAnalysisException(generateMessage(i));
						canBeNiz = false;
						break;
					}
					continue;
				}

				break;
			}

			if (buff instanceof NIZ_ZNAKOVA && canBeNiz) {
				List<Tip> tips = new ArrayList<>();
				for (int a = 0; a < ((NIZ_ZNAKOVA) buff).getCharacters().length; a++) {
					tips.add(Tip.character);
				}

				i.setTips(tips);
				i.setTip(Tip.charA);

				//new done
//				char[] chars = ((NIZ_ZNAKOVA) buff).getCharacters();
//				for (int n = 0; n < chars.length; n++) {
//					instructions.addInstruction(" MOVE " + Integer.toString(chars[n]) + ", R0");
//					instructions.addInstruction(" STORE R0, (SP + " + n*4 +")");
//				}
				for (int n = ((NIZ_ZNAKOVA) buff).getCharacters().length - 1; n >= 0; n--) {
					addPOP("R1");
					instructions.addInstruction(" STORE R1, (SP + 0" + Integer.toHexString(n*2*4) + ")");
				}
			} else {
				i.setTip(((IzrazPridruzivanja) i.children.get(0)).getTip());

				//new done
				addPOP("R0");
				instructions.addInstruction(" STORE R0, (SP)");
			}
		} else {
			ListaIzrazaPridruzivanja lip = (ListaIzrazaPridruzivanja) i.children.get(1);
			i.setTips(lip.getTips());
			i.setTip(lip.getTips().get(0));
			//new done
			for (int n = lip.getTips().size() - 1; n >= 0; n++) {
				addPOP("R1");
				instructions.addInstruction(" STORE R1, (SP + 0" + Integer.toHexString(n*2*4) + ")");
			}
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
			if ((isChar && value.charAt(i)=='\'') || (!isChar && value.charAt(i)=='"')) {
				throw new IllegalStateException();
			}

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

		//NodeVisitor nv = new TreePrintVisitor();
		//nz.acceptVisitor(nv);

		return bob.toString();
	}

	private void enterDeeper() {
		initDeklaratori.push(new HashMap<>());
		addresses.push(new HashMap<>());
		//values.push(new HashMap<>());
	}

	private void stepOut() {
		initDeklaratori.pop();
		addresses.pop();
		//values.pop();
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

	private boolean isFunctionIdentical(Tip t, Tip expectedRet, List<Tip> args) {
		if (t instanceof Funkcija) {
			Funkcija buff = (Funkcija) t;

			if (expectedRet != null && !buff.primitiv.equals(expectedRet.primitiv)) {
				return false;
			}

			if (buff.args.size() != args.size()) {
				return false;
			}

			for (int i = 0; i < args.size(); i++) {
				if (!args.get(i).equals(buff.args.get(i))) {
					return false;
				}
			}

			return true;
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
				if (!args.get(i).isImplCastable(buff.args.get(i))) {
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
			throw new IllegalStateException("Dont call this with anything excpet int and char");
		}
	}


	private void addPUSH(String registar) {
		instructions.addInstruction(" PUSH " + registar);
		moveSP(-4, false);
	}

	private void addPOP(String registar) {
		instructions.addInstruction(" POP " + registar);
		moveSP(4, false);
	}

	private void addCALL(String labela, boolean returnDibs) {
		if (returnDibs) {
			moveSP(-4, true);
		}

		instructions.addInstruction(" CALL " + labela);
		moveSP(-4, false);//<<<<<<<<<<<<<<<<<<<<<
	}

	private void addRET() {
		instructions.addInstruction(" RET");
		moveSP(4, false);
	}

//	/**
//	 * PAZI. uvijek mice n. Ako keepTop onaj inicijalini pop se ne broji u n.
//	 *
//	 * @param n	DO NOT call with 0
//	 * @param keepTop
//	 */
//	private void clearStackOf(int n, boolean keepTop) {
//		if (keepTop) {
//			instructions.addInstruction(" POP R0");
//		}
//
//		moveSP(n, true);
//
//		if (keepTop) {
//			instructions.addInstruction(" PUSH R0");
//		}
//	}

	//DO NOT CALL YOURSELF internstuff!!!!!!!
	private void moveSP(int x, boolean generateCode) {
		//List<String> removed = new ArrayList<>();

		for (int i = initDeklaratori.size() - 1; i > 0; i--) {
			for (String s : initDeklaratori.get(i).keySet()) {
				//Tip buff = initDeklaratori.get(i).get(s);
				String buff = addresses.get(i).get(s);

				if (buff == null) {
					continue;
				}

				String[] adressComponents = buff.split("[ ]+");
				int number = Integer.parseInt(adressComponents[2], 16);
				if (adressComponents[1].equals("-")) {
					number = -number;
				}
				number -= x;

				adressComponents[2] = Integer.toString(number, 16);

				if (number < 0) {
					addresses.get(i).put(s, "SP - 0" + Integer.toHexString(Math.abs(number)));
				} else {
					addresses.get(i).put(s, "SP + 0" + Integer.toHexString(number));
				}
			}
		}

		onStack -= x;
		//removed.forEach(s -> initDeklaratori.peek().remove(s));

		if (generateCode) {
			int n = Math.abs(x);
			if (x >= 0) {
				instructions.addInstruction(" ADD SP, 0" + Integer.toHexString(n) + ", SP");
			} else {
				instructions.addInstruction(" SUB SP, 0" + Integer.toHexString(n) + ", SP");
			}
		}
	}

}
