import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NezavrsniZnak extends Node {

	protected List<Node> children;

	protected NezavrsniZnak(String name) {
		this(name, Collections.<Node>emptyList());
	}

	protected NezavrsniZnak(String name,List<Node> children) {
		super(name);
		this.children = new ArrayList<Node>(children);
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public List<Node> getChildren() {
		return children
	}
}
