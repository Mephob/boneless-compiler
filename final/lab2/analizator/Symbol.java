import java.io.Serializable;
import java.util.Objects;

public abstract class Symbol implements Serializable {

	public static final long serialVersionUID = 42L;

	protected String value;

	public Symbol(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(value, symbol.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
