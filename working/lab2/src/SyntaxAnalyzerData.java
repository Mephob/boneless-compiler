
import java.util.List;
import java.util.Map;

public class SyntaxAnalyzerData {

	private List<String> nonterminalSymbols;
	private List<String> terminalSymbols;
	private List<String> syncSymbols;
	private List<String> grammarProductionsRaw;

	public SyntaxAnalyzerData(List<String> nonterminalSymbols, List<String> terminalSymbols, List<String> syncSymbols, List<String> grammarProductionsRaw) {
		this.nonterminalSymbols = nonterminalSymbols;
		this.terminalSymbols = terminalSymbols;
		this.syncSymbols = syncSymbols;
		this.grammarProductionsRaw = grammarProductionsRaw;
	}

	public List<String> getNonterminalSymbols() {
		return nonterminalSymbols;
	}

	public List<String> getTerminalSymbols() {
		return terminalSymbols;
	}

	public List<String> getSyncSymbols() {
		return syncSymbols;
	}

	public List<String> getGrammarProductionsRaw() {
		return grammarProductionsRaw;
	}
}
