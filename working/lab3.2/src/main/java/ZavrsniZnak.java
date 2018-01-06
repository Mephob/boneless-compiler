import java.util.List;

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
}

//###########################################################################//
//########################Definicije zavrsnih znakova########################//
//###########################################################################//

class IDN extends ZavrsniZnak {

	public IDN(String name, Node parent, int lineNumber, String value) {
		super(name, parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class BROJ extends ZavrsniZnak {

	public BROJ(String name, Node parent, int lineNumber, String value) {
		super(name, parent, lineNumber, value);

		//baca exception ako je prevelik/mali za int(tak i treba)
		//to radimo tu a ne u super tako da se name i lineNumber inicializiraju
		//setValue(Integer.parseInt(value));
		//TODO prebacit u vizitor
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class ZNAK extends ZavrsniZnak {

	public ZNAK(String name, Node parent, int lineNumber, String value) {
		super(name, parent, lineNumber, null);
/*
		if (value.startsWith("\\") && value.length() == 2) {
			switch (value.charAt(1)) {
				case 't':setValue('\t');
				case 'n':setValue('\n');
				case '0':setValue('\0');
				case '\'':setValue('\'');
				case '\"':setValue('\"');
				case '\\':setValue('\\');
				default:
					//todo trhow exception
			}
		} else if (value.length() == 1) {
			setValue(value.charAt(0));
		} else {
			//TODO throw exception???
		}*///TODO prebacit u visitor
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class NIZ_ZNAKOVA extends ZavrsniZnak {

	public NIZ_ZNAKOVA(String name, Node parent, int lineNumber, String value) {
		super(name, parent, lineNumber, value);
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
class KR_BREAK extends ZavrsniZnak {

	public KR_BREAK(Node parent, int lineNumber) {
		super("break", parent, lineNumber, "break");
	}

	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}

//###########################################################################//
// KR_CHAR

//###########################################################################//
// KR_CONST

//###########################################################################//
// KR_CONTINUE

//###########################################################################//
// KR_ELSE

//###########################################################################//
// KR_FOR

//###########################################################################//
// KR_IF

//###########################################################################//
// KR_INT

//###########################################################################//
// KR_RETURN

//###########################################################################//
// KR_VOID

//###########################################################################//
// KR_WHILE

//###########################################################################//
// PLUS

//###########################################################################//
// OP_INC

//###########################################################################//
// MINUS

//###########################################################################//
// OP_DEC

//###########################################################################//
// OP_PUTA

//###########################################################################//
// OP_DIJELI

//###########################################################################//
// OP_MOD

//###########################################################################//
// OP_PRIDRUZI

//###########################################################################//
// OP_LT

//###########################################################################//
// OP_LTE

//###########################################################################//
// OP_GT

//###########################################################################//
// OP_GTE

//###########################################################################//
// OP_EQ

//###########################################################################//
// OP_NEQ

//###########################################################################//
// OP_NEG

//###########################################################################//
// OP_TILDA

//###########################################################################//
// OP_I

//###########################################################################//
// OP_ILI

//###########################################################################//
// OP_BIN_I

//###########################################################################//
// OP_BIN_ILI

//###########################################################################//
// OP_BIN_XILI

//###########################################################################//
// ZAREZ

//###########################################################################//
// TOCKAZAREZ

//###########################################################################//
// L_ZAGRADA

//###########################################################################//
// D_ZAGRADA

//###########################################################################//
// L_UGL_ZAGRADA

//###########################################################################//
// D_UGL_ZAGRADA

//###########################################################################//
// L_VIT_ZAGRADA

//###########################################################################//
// D_VIT_ZAGRADA
