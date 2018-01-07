import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NezavrsniZnak extends Node {

	protected List<Node> children;

	protected NezavrsniZnak(String name, Node parent) {
		this(name, parent, Collections.<Node>emptyList());
	}

	protected NezavrsniZnak(String name, Node parent,List<Node> children) {
		super(name, parent);
		this.children = new ArrayList<Node>(children);
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public List<Node> getChildren() {
		return children;
	}
}
