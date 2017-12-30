import java.io.Serializable;
import java.util.List;

public class SyntaxAnalyzerData implements Serializable {

	public static final long serialVersionUID = 42L;

	private List<NonterminalSymbol> nonterminalSymbols;
	private List<TerminalSymbol> terminalSymbols;
	private List<SynchronizingSymbol> syncSymbols;
	private List<ProductionRule> grammarProductions;

	public SyntaxAnalyzerData(List<NonterminalSymbol> nonterminalSymbols, List<TerminalSymbol> terminalSymbols, List<SynchronizingSymbol> syncSymbols, List<ProductionRule> grammarProductions) {
		this.nonterminalSymbols = nonterminalSymbols;
		this.terminalSymbols = terminalSymbols;
		this.syncSymbols = syncSymbols;
		this.grammarProductions = grammarProductions;
	}

	public List<NonterminalSymbol> getNonterminalSymbols() {
		return nonterminalSymbols;
	}

	public List<TerminalSymbol> getTerminalSymbols() {
		return terminalSymbols;
	}

	public List<SynchronizingSymbol> getSyncSymbols() {
		return syncSymbols;
	}

	public List<ProductionRule> getGrammarProductions() {
		return grammarProductions;
	}
}
