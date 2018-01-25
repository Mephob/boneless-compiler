public abstract class Node {

	private String name;

	private Node parent;

	private int depth;

	protected Node(String name, Node parent) {
		this.name = name;
		setParent(parent);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
		depth = parent == null ? 0 : parent.getDepth() + 1;
	}

	public int getDepth() {
		return depth;
	}

	public abstract void acceptVisitor(NodeVisitor nv);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Node)) return false;

		Node node = (Node) o;

		return name != null ? name.equals(node.name) : node.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
