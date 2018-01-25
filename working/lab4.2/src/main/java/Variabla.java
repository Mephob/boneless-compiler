public class Variabla {

	String name;

	Tip type;

	public Variabla(String name, Tip type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Variabla)) return false;

		Variabla variabla = (Variabla) o;

		return name != null ? name.equals(variabla.name) : variabla.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
