import java.util.List;

public class AditivniIzraz extends NezavrsniZnak {


	@Override
	public void acceptVisitor(NodeVisitor nv) {
		nv.accept(this);
	}
}
