import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NezavrsniZnak extends Node {

	protected List<Node> children;

	private Tip tip;

	protected NezavrsniZnak(String name, Node parent) {
		this(name, parent, Collections.emptyList());
	}

	protected NezavrsniZnak(String name, Node parent,List<Node> children) {
		super(name, parent);
		this.children = new ArrayList<>(children);
	}

	public void addChild(Node child) {
		children.add(child);
		child.setParent(this);
	}

	public List<Node> getChildren() {
		return children;
	}

	public Tip getTip() {
		return tip;
	}

	public void setTip(Tip tip) {
		this.tip = tip;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NezavrsniZnak)) return false;

		NezavrsniZnak that = (NezavrsniZnak) o;
		if (getName() != null) {
			if (!getName().equals(that.getName())) return false;
		} else {
			if (that.getName() != null) return false;
		}

		return children != null ? children.equals(that.children) : that.children == null;
	}

	@Override
	public int hashCode() {
		return children != null ? children.hashCode() : 0;
	}
}

interface Declaration {
}

//###########################################################################//
//#######################Definicije nezavrsnih znakova#######################//
//###########################################################################//

class PrimarniIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	boolean isAdress = false;

	protected PrimarniIzraz(Node parent) {
		super("<primarni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class PostfiksIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	boolean isAdress = false;

	protected PostfiksIzraz(Node parent) {
		super("<postfiks_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class ListaArgumenata extends NezavrsniZnak {

	private List<Tip> tips;

	protected ListaArgumenata(Node parent) {
		super("<lista_argumenata>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public List<Tip> getTips() {
		return tips;
	}

	public void setTips(List<Tip> tips) {
		this.tips = tips;
	}
}

//###########################################################################//
class UnarniIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	String adress;

	protected UnarniIzraz(Node parent) {
		super("<unarni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class UnarniOperator extends NezavrsniZnak {

	protected UnarniOperator(Node parent) {
		super("<unarni_operator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class CastIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected CastIzraz(Node parent) {
		super("<cast_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class ImeTipa extends NezavrsniZnak {

	protected ImeTipa(Node parent) {
		super("<ime_tipa>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}


//	public Boolean getlIzraz() {
//		return lIzraz;
//	}
//
//	public void setlIzraz(Boolean constant) {
//		lIzraz = constant;
//	}
}

//###########################################################################//
class SpecifikatorTipa extends NezavrsniZnak {

	protected SpecifikatorTipa(Node parent) {
		super("<specifikator_tipa>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class MultiplikativniIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected MultiplikativniIzraz(Node parent) {
		super("<multiplikativni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class AditivniIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected AditivniIzraz(Node parent) {
		super("<aditivni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class OdnosniIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected OdnosniIzraz(Node parent) {
		super("<odnosni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class JednakosniIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected JednakosniIzraz(Node parent) {
		super("<jednakosni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class BinIIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected BinIIzraz(Node parent) {
		super("<bin_i_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class BinXiliIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected BinXiliIzraz(Node parent) {
		super("<bin_xili_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class BinIliIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected BinIliIzraz(Node parent) {
		super("<bin_ili_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class LogIIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	int id;

	protected LogIIzraz(Node parent) {
		super("<log_i_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class LogIliIzraz extends NezavrsniZnak {

	private Boolean lIzraz;

	int id;

	protected LogIliIzraz(Node parent) {
		super("<log_ili_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class IzrazPridruzivanja extends NezavrsniZnak {

	private Boolean lIzraz;

	protected IzrazPridruzivanja(Node parent) {
		super("<izraz_pridruzivanja>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}

//###########################################################################//
class Izraz extends NezavrsniZnak {

	private Boolean lIzraz;

	protected Izraz(Node parent) {
		super("<izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Boolean getlIzraz() {
		return lIzraz;
	}

	public void setlIzraz(Boolean lIzraz) {
		this.lIzraz = lIzraz;
	}
}


//###########################################################################//
class SlozenaNaredba extends NezavrsniZnak {

	int onStack;

	protected SlozenaNaredba(Node parent) {
		super("<slozena_naredba>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ListaNaredbi extends NezavrsniZnak {

	protected ListaNaredbi(Node parent) {
		super("<lista_naredbi>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class Naredba extends NezavrsniZnak {

	protected Naredba(Node parent) {
		super("<naredba>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class IzrazNaredba extends NezavrsniZnak {

	protected IzrazNaredba(Node parent) {
		super("<izraz_naredba>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class NaredbaGrananja extends NezavrsniZnak {

	protected NaredbaGrananja(Node parent) {
		super("<naredba_grananja>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class NaredbaPetlje extends NezavrsniZnak {

	protected NaredbaPetlje(Node parent) {
		super("<naredba_petlje>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class NaredbaSkoka extends NezavrsniZnak {

	protected NaredbaSkoka(Node parent) {
		super("<naredba_skoka>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class PrijevodnaJedinica extends NezavrsniZnak {

	protected PrijevodnaJedinica(Node parent) {
		super("<prijevodna_jedinica>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class VanjskaDeklaracija extends NezavrsniZnak {

	protected VanjskaDeklaracija(Node parent) {
		super("<vanjska_deklaracija>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class DefinicijaFunkcije extends NezavrsniZnak {
	private Funkcija func;

	private List<String> paramNames;


	protected DefinicijaFunkcije(Node parent) {
		super("<definicija_funkcije>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Funkcija getFunc() {
		return func;
	}

	public void setFunc(Funkcija func) {
		this.func = func;
	}

	public List<String> getParamNames() {
		return paramNames;
	}

	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;
	}
}

//###########################################################################//
class ListaParametara extends NezavrsniZnak {

	private List<Tip> tips;

	private List<String> idns;

	protected ListaParametara(Node parent) {
		super("<lista_parametara>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public List<Tip> getTips() {
		return tips;
	}

	public void setTips(List<Tip> tips) {
		this.tips = tips;
	}

	public List<String> getIdns() {
		return idns;
	}

	public void setIdns(List<String> idns) {
		this.idns = idns;
	}
}

//###########################################################################//
class DeklaracijaParametra extends NezavrsniZnak {

	private String idn;

	protected DeklaracijaParametra(Node parent) {
		super("<deklaracija_parametra>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public String getIdn() {
		return idn;
	}

	public void setIdn(String idn) {
		this.idn = idn;
	}
}

//###########################################################################//
class ListaDeklaracija extends NezavrsniZnak {

	int size = 0;

	protected ListaDeklaracija(Node parent) {
		super("<lista_deklaracija>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class Deklaracija extends NezavrsniZnak {

	protected Deklaracija(Node parent) {
		super("<deklaracija>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ListaInitDeklaratora extends NezavrsniZnak {

	private Tip ntip;

	int size;

	protected ListaInitDeklaratora(Node parent) {
		super("<lista_init_deklaratora>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Tip getNtip() {
		return ntip;
	}

	public void setNtip(Tip ntip) {
		this.ntip = ntip;
	}
}

//###########################################################################//
class InitDeklarator extends NezavrsniZnak {

	private Tip ntip;

	protected InitDeklarator(Node parent) {
		super("<init_deklarator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public Tip getNtip() {
		return ntip;
	}

	public void setNtip(Tip ntip) {
		this.ntip = ntip;
	}
}

//###########################################################################//
class IzravniDeklarator extends NezavrsniZnak {

	private Tip ntip;

	int size = 0;

	protected IzravniDeklarator(Node parent) {
		super("<izravni_deklarator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public int getWhatInBracket() {
		if (children.size() == 1) {
			throw new IllegalStateException();
		}

		return ((BROJ)children.get(2)).getIntValue();
	}

	public Tip getNtip() {
		return ntip;
	}

	public void setNtip(Tip ntip) {
		this.ntip = ntip;
	}
}

//###########################################################################//
class Inicijalizator extends NezavrsniZnak {

	private List<Tip> tips;

	protected Inicijalizator(Node parent) {
		super("<inicijalizator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public List<Tip> getTips() {
		return tips;
	}

	public void setTips(List<Tip> tips) {
		this.tips = tips;
	}
}

//###########################################################################//
class ListaIzrazaPridruzivanja extends NezavrsniZnak {

	private List<Tip> tips;

	protected ListaIzrazaPridruzivanja(Node parent) {
		super("<lista_izraza_pridruzivanja>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public int getBrElem() {
		return tips.size();
	}

	public List<Tip> getTips() {
		return tips;
	}

	public void setTips(List<Tip> tips) {
		this.tips = tips;
	}
}