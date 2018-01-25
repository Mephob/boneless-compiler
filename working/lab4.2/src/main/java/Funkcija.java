import java.util.List;

public class Funkcija extends Tip {

	String name;

	List<Tip> args;

	public Funkcija(String name, Class<?> ret, List<Tip> args) {
		super(ret, false);
		this.name = name;
		this.args = args;
	}

	public boolean isEquivelentTo(Funkcija f) {
		return f.name.equals(name) && f.primitiv.equals(primitiv) && f.args.equals(args);
	}

	public Tip returnTip() {
		if (primitiv.equals(int.class)) {
			return Tip.integer;
		} else if (primitiv.equals(char.class)) {
			return Tip.character;
		} else if (primitiv.equals(void.class)) {
			return Tip.voidType;
		} else if (primitiv.equals(int[].class)) {
			return Tip.intA;
		} else if (primitiv.equals(char[].class)) {
			return Tip.charA;
		}

		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return "Funkcija{" +
			  "name='" + name + '\'' +
			  ", args=" + args +
			  ", primitiv=" + primitiv +
			  '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Funkcija)) return false;
		if (!super.equals(o)) return false;

		Funkcija funkcija = (Funkcija) o;

		return name != null ? name.equals(funkcija.name) : funkcija.name == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
