import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NezavrsniZnak extends Node {

	protected List<Node> children;

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

	@Override
	public String toString() {
		return getName();
	}
}

//###########################################################################//
//#######################Definicije nezavrsnih znakova#######################//
//###########################################################################//

class PrimarniIzraz extends NezavrsniZnak {

	protected PrimarniIzraz(Node parent) {
		super("<primarni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class PostfiksIzraz extends NezavrsniZnak {

	protected PostfiksIzraz(Node parent) {
		super("<postfiks_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ListaArgumenata extends NezavrsniZnak {

	protected ListaArgumenata(Node parent) {
		super("<lista_argumenata>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class UnarniIzraz extends NezavrsniZnak {

	protected UnarniIzraz(Node parent) {
		super("<unarni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
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

	protected CastIzraz(Node parent) {
		super("<cast_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
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

	protected MultiplikativniIzraz(Node parent) {
		super("<multiplikativni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class AditivniIzraz extends NezavrsniZnak {

	protected AditivniIzraz(Node parent) {
		super("<aditivni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OdnosniIzraz extends NezavrsniZnak {

	protected OdnosniIzraz(Node parent) {
		super("<odnosni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class JednakosniIzraz extends NezavrsniZnak {

	protected JednakosniIzraz(Node parent) {
		super("<jednakosni_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class BinIIzraz extends NezavrsniZnak {

	protected BinIIzraz(Node parent) {
		super("<bin_i_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class BinXiliIzraz extends NezavrsniZnak {

	protected BinXiliIzraz(Node parent) {
		super("<bin_xili_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class BinIliIzraz extends NezavrsniZnak {

	protected BinIliIzraz(Node parent) {
		super("<bin_ili_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class LogIIzraz extends NezavrsniZnak {

	protected LogIIzraz(Node parent) {
		super("<log_i_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class LogIliIzraz extends NezavrsniZnak {

	protected LogIliIzraz(Node parent) {
		super("<log_ili_izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class IzrazPridruzivanja extends NezavrsniZnak {

	protected IzrazPridruzivanja(Node parent) {
		super("<izraz_pridruzivanja>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class Izraz extends NezavrsniZnak {

	protected Izraz(Node parent) {
		super("<izraz>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}


//###########################################################################//
class SlozenaNaredba extends NezavrsniZnak {

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

	protected DefinicijaFunkcije(Node parent) {
		super("<definicija_funkcije>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ListaParametara extends NezavrsniZnak {

	protected ListaParametara(Node parent) {
		super("<lista_parametara>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class DeklaracijaParametra extends NezavrsniZnak {

	protected DeklaracijaParametra(Node parent) {
		super("<deklaracija_parametra>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ListaDeklaracija extends NezavrsniZnak {

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

	protected ListaInitDeklaratora(Node parent) {
		super("<lista_init_deklaratora>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class InitDeklarator extends NezavrsniZnak {

	protected InitDeklarator(Node parent) {
		super("<init_deklarator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class IzravniDeklarator extends NezavrsniZnak {

	protected IzravniDeklarator(Node parent) {
		super("<izravni_deklarator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class Inicijalizator extends NezavrsniZnak {

	protected Inicijalizator(Node parent) {
		super("<inicijalizator>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ListaIzrazaPridruzivanja extends NezavrsniZnak {

	protected ListaIzrazaPridruzivanja(Node parent) {
		super("<lista_izraza_pridruzivanja>", parent);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}