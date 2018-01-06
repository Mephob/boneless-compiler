public abstract class ZavrsniZnak<V> extends Node {

	private V value;

	public ZavrsniZnak(String name, V value) {
		super(name);
		this.value = value;
	}


	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
