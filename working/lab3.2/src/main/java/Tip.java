public class Tip {
	Class<?> primitiv;

	Boolean isConstant;

	public static final Tip integer = new Tip(int.class, false);
	public static final Tip constInteger = new Tip(int.class, true);
	public static final Tip character = new Tip(char.class, false);
	public static final Tip constCharacter = new Tip(char.class, true);
	public static final Tip intA = new Tip(int[].class, false);
	public static final Tip constIntA = new Tip(int[].class, true);
	public static final Tip charA = new Tip(char[].class, false);
	public static final Tip constCharA = new Tip(char[].class, true);
	public static final Tip voidType = new Tip(void.class, false);

	public Tip(Class<?> primitiv, Boolean isConstant) {
		this.primitiv = primitiv;
		this.isConstant = isConstant;
	}

	public Tip getArrayTip() {
		if (primitiv.isArray()) {
			return this;
		}

		if (primitiv.equals(int.class)) {
			return isConstant ? constIntA : intA;
		}

		if (primitiv.equals(char.class)) {
			return isConstant ? constCharA : charA;
		}

		throw new IllegalStateException();
	}

	public Tip getPrimitivTip() {
		if (!primitiv.isArray()) {
			return this;
		}

		if (primitiv.equals(int[].class)) {
			return isConstant ? constInteger : integer;
		}

		if (primitiv.equals(char[].class)) {
			return isConstant ? constCharacter : character;
		}

		throw new IllegalStateException();
	}

	public boolean isExplCastable(Tip t) {
//		if (this instanceof Funkcija || t instanceof Funkcija) {
//			return false;
//		}

		if (!primitiv.isArray() && !t.primitiv.isArray()) {
			return true;
		}

		return false;
	}

	public boolean isImplCastable(Tip t) {
//		if (this instanceof Funkcija || t instanceof Funkcija) {
//			return false;
//		}

		if (!primitiv.isArray() && !t.primitiv.isArray()) {
			if (primitiv.equals(t.primitiv)) {
				return true;
			}

			if (primitiv.equals(char.class) && t.primitiv.equals(int.class)) {
				return true;
			}
		} else if (primitiv.isArray() && t.primitiv.isArray()) {
			if (isConstant && !t.isConstant) { //todo retardiran tekst u pripremi
				return false;
			} else if (primitiv.equals(t.primitiv)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tip)) return false;

		Tip tip1 = (Tip) o;

		if (primitiv != null ? !primitiv.equals(tip1.primitiv) : tip1.primitiv != null) return false;
		return isConstant != null ? isConstant.equals(tip1.isConstant) : tip1.isConstant == null;
	}

	@Override
	public int hashCode() {
		int result = primitiv != null ? primitiv.hashCode() : 0;
		result = 31 * result + (isConstant != null ? isConstant.hashCode() : 0);
		return result;
	}
}
