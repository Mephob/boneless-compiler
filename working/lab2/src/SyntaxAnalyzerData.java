package lab2;

import java.util.List;
import java.util.Map;

public class SyntaxAnalyzerData {

	private List<String> nonterminalSymbols;
	private List<String> terminalSymbols;
	private List<String> syncSymbols;
	
	//TODO: make better data structure for this shit
	private Map<String, List<List<String>>> grammarProductions;

	public SyntaxAnalyzerData(List<String> nonterminalSymbols, List<String> terminalSymbols, List<String> syncSymbols, Map<String, List<List<String>>> grammarProductions) {
		this.nonterminalSymbols = nonterminalSymbols;
		this.terminalSymbols = terminalSymbols;
		this.syncSymbols = syncSymbols;
		this.grammarProductions = grammarProductions;
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

	public Map<String, List<List<String>>> getGrammarProductions() {
		return grammarProductions;
	}
}
