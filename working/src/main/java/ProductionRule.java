import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class ProductionRule {
	private String leftSide;
	private List<Symbol> rightSide;

	public ProductionRule(String leftSide, List<Symbol> rightSide) {
		this.leftSide = leftSide;
		this.rightSide = new ArrayList<>(rightSide);
	}

	public String getLeftSide() {
		return leftSide;
	}

	public List<Symbol> getRightSide() {
		return rightSide;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(" ");
		for (Symbol symbol : rightSide) {
			sj.add(symbol.getValue());
		}
		return leftSide + " -> " + sj.toString();
 	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductionRule that = (ProductionRule) o;
		return Objects.equals(leftSide, that.leftSide) &&
				Objects.equals(rightSide, that.rightSide);
	}

	@Override
	public int hashCode() {

		return Objects.hash(leftSide, rightSide);
	}
}
