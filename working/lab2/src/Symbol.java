package lab2;

public abstract class Symbol {
	protected String value;

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
