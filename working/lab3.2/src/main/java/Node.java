public abstract class Node {

	private String name;

	protected Node(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract void acceptVisitor(NodeVisitor nv);
}
