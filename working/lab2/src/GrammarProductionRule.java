package lab2;

import java.util.List;

public class GrammarProductionRule {
	private String startSymbol;
	private List<Symbol> rule;

	public GrammarProductionRule(String startSymbol, List<Symbol> rule) {
		this.startSymbol = startSymbol;
		this.rule = rule;
	}

	public String getStartSymbol() {
		return startSymbol;
	}

	public List<Symbol> getRule() {
		return rule;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(startSymbol);
		sb.append(" ->");
		for (Symbol symbol : rule) {
			sb.append(" ").append(symbol.getValue());
		}
		return sb.toString();
	}
}
