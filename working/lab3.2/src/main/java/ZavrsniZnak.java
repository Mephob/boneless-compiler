public abstract class ZavrsniZnak extends Node {


	private int lineNumber;

	private String value;

	ZavrsniZnak(String name, Node parent, int lineNumber, String value) {
		super(name, parent);
		this.lineNumber = lineNumber;
		this.value = value;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getName() + String.format("(%d,%s)", lineNumber, value);
	}
}

//###########################################################################//
//########################Definicije zavrsnih znakova########################//
//###########################################################################//

class IDN extends ZavrsniZnak {

	public IDN(Node parent, int lineNumber, String value) {
		super("IDN", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class BROJ extends ZavrsniZnak {

	private int intValue;

	public BROJ(Node parent, int lineNumber, String value) {
		super("BROJ", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public int getIntValue() {
		return intValue;
	}

	void setIntValue(int value) {
		intValue = value;
	}
}

//###########################################################################//
class ZNAK extends ZavrsniZnak {

	private char charValue;

	public ZNAK(Node parent, int lineNumber, String value) {
		super("ZNAK", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public char getCharValue() {
		return charValue;
	}

	public void setCharValue(char charValue) {
		this.charValue = charValue;
	}
}

//###########################################################################//
class NIZ_ZNAKOVA extends ZavrsniZnak {

	private char[] characters;

	public NIZ_ZNAKOVA(Node parent, int lineNumber, String value) {
		super("NIZ_ZNAKOVA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}

	public char[] getCharacters() {
		return characters;
	}

	public void setCharacters(char[] characters) {
		this.characters = characters;
	}
}

//###########################################################################//
class KR_BREAK extends ZavrsniZnak {

	public KR_BREAK(Node parent, int lineNumber, String value) {
		super("KR_BREAK", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_CHAR extends ZavrsniZnak {

	public KR_CHAR(Node parent, int lineNumber, String value) {
		super("KR_CHAR", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_CONST extends ZavrsniZnak {

	public KR_CONST(Node parent, int lineNumber, String value) {
		super("KR_CONST", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_CONTINUE extends ZavrsniZnak {

	public KR_CONTINUE(Node parent, int lineNumber, String value) {
		super("KR_CONTINUE", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_ELSE extends ZavrsniZnak {

	public KR_ELSE(Node parent, int lineNumber, String value) {
		super("KR_ELSE", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_FOR extends ZavrsniZnak {

	public KR_FOR(Node parent, int lineNumber, String value) {
		super("KR_FOR", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_IF extends ZavrsniZnak {

	public KR_IF(Node parent, int lineNumber, String value) {
		super("KR_IF", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_INT extends ZavrsniZnak {

	public KR_INT(Node parent, int lineNumber, String value) {
		super("KR_INT", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_RETURN extends ZavrsniZnak {

	public KR_RETURN(Node parent, int lineNumber, String value) {
		super("KR_RETURN", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_VOID extends ZavrsniZnak {

	public KR_VOID(Node parent, int lineNumber, String value) {
		super("KR_VOID", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_WHILE extends ZavrsniZnak {

	public KR_WHILE(Node parent, int lineNumber, String value) {
		super("KR_WHILE", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class PLUS extends ZavrsniZnak {

	public PLUS(Node parent, int lineNumber, String value) {
		super("PLUS", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_INC extends ZavrsniZnak {

	public OP_INC(Node parent, int lineNumber, String value) {
		super("OP_INC", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class MINUS extends ZavrsniZnak {

	public MINUS(Node parent, int lineNumber, String value) {
		super("MINUS", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_DEC extends ZavrsniZnak {

	public OP_DEC(Node parent, int lineNumber, String value) {
		super("OP_DEC", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_PUTA extends ZavrsniZnak {

	public OP_PUTA(Node parent, int lineNumber, String value) {
		super("OP_PUTA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_DIJELI extends ZavrsniZnak {

	public OP_DIJELI(Node parent, int lineNumber, String value) {
		super("OP_DIJELI", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_MOD extends ZavrsniZnak {

	public OP_MOD(Node parent, int lineNumber, String value) {
		super("OP_MOD", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_PRIDRUZI extends ZavrsniZnak {

	public OP_PRIDRUZI(Node parent, int lineNumber, String value) {
		super("OP_PRIDRUZI", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_LT extends ZavrsniZnak {

	public OP_LT(Node parent, int lineNumber, String value) {
		super("OP_LT", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_LTE extends ZavrsniZnak {

	public OP_LTE(Node parent, int lineNumber, String value) {
		super("OP_LTE", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_GT extends ZavrsniZnak {

	public OP_GT(Node parent, int lineNumber, String value) {
		super("OP_GT", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_GTE extends ZavrsniZnak {

	public OP_GTE(Node parent, int lineNumber, String value) {
		super("OP_GTE", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_EQ extends ZavrsniZnak {

	public OP_EQ(Node parent, int lineNumber, String value) {
		super("OP_EQ", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_NEQ extends ZavrsniZnak {

	public OP_NEQ(Node parent, int lineNumber, String value) {
		super("OP_NEQ", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_NEG extends ZavrsniZnak {

	public OP_NEG(Node parent, int lineNumber, String value) {
		super("OP_NEG", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_TILDA extends ZavrsniZnak {

	public OP_TILDA(Node parent, int lineNumber, String value) {
		super("OP_TILDA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_I extends ZavrsniZnak {

	public OP_I(Node parent, int lineNumber, String value) {
		super("OP_I", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_ILI extends ZavrsniZnak {

	public OP_ILI(Node parent, int lineNumber, String value) {
		super("OP_ILI", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_BIN_I extends ZavrsniZnak {

	public OP_BIN_I(Node parent, int lineNumber, String value) {
		super("OP_BIN_I", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_BIN_ILI extends ZavrsniZnak {

	public OP_BIN_ILI(Node parent, int lineNumber, String value) {
		super("OP_BIN_ILI", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class OP_BIN_XILI extends ZavrsniZnak {

	public OP_BIN_XILI(Node parent, int lineNumber, String value) {
		super("OP_BIN_XILI", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ZAREZ extends ZavrsniZnak {

	public ZAREZ(Node parent, int lineNumber, String value) {
		super("ZAREZ", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class TOCKAZAREZ extends ZavrsniZnak {

	public TOCKAZAREZ(Node parent, int lineNumber, String value) {
		super("TOCKAZAREZ", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class L_ZAGRADA extends ZavrsniZnak {

	public L_ZAGRADA(Node parent, int lineNumber, String value) {
		super("L_ZAGRADA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class D_ZAGRADA extends ZavrsniZnak {

	public D_ZAGRADA(Node parent, int lineNumber, String value) {
		super("D_ZAGRADA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class L_UGL_ZAGRADA extends ZavrsniZnak {

	public L_UGL_ZAGRADA(Node parent, int lineNumber, String value) {
		super("L_UGL_ZAGRADA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class D_UGL_ZAGRADA extends ZavrsniZnak {

	public D_UGL_ZAGRADA(Node parent, int lineNumber, String value) {
		super("D_UGL_ZAGRADA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class L_VIT_ZAGRADA extends ZavrsniZnak {

	public L_VIT_ZAGRADA(Node parent, int lineNumber, String value) {
		super("L_VIT_ZAGRADA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class D_VIT_ZAGRADA extends ZavrsniZnak {

	public D_VIT_ZAGRADA(Node parent, int lineNumber, String value) {
		super("D_VIT_ZAGRADA", parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}
