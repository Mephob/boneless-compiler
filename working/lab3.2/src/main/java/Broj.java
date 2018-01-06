public class Broj extends Node implements ZavrsniZnak<Integer> {

	private int value;

	public Broj(int value) {

		this.value = value;
	}

	public void acceptVisitor(NodeVisitor nv) {

	}


	public Integer getValue() {
		return value;
	}
}
