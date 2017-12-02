package lab2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;

public class InputFileParser {

	private SyntaxAnalyzerData data;

	public InputFileParser(Path file) throws IOException {
		parse(new Scanner(file));
	}

	public InputFileParser(InputStream is) {
		parse(new Scanner(is));
	}

	private void parse(Scanner sc) {
		List<NonterminalSymbol> nonterminalSymbols = new ArrayList<>();
		List<TerminalSymbol> terminalSymbols = new ArrayList<>();
		List<SynchronizingSymbol> syncSymbols = new ArrayList<>();
		List<GrammarProductionRule> grammarProductionRules = new ArrayList<>();

		int lineNum = 0;
		String currentStartSymbol = null;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			lineNum++;

			switch (lineNum) {
				case 1: // non-terminal symbols in the first line
					for (String value : line.substring(3).split(" ")) {
						nonterminalSymbols.add(new NonterminalSymbol(value));
					}
					break;
				case 2: // terminal symbols in the second line
					for (String value : line.substring(3).split(" ")) {
						terminalSymbols.add(new TerminalSymbol(value));
					}
					break;
				case 3: // symbols for synchronization in the third line
					for (String value : line.substring(5).split(" ")) {
						syncSymbols.add(new SynchronizingSymbol(value));
					}
					break;
				default:
					if (line.startsWith(" ")) {
						grammarProductionRules.add(createProduction(currentStartSymbol, line.trim()));
					} else {
						currentStartSymbol = line.trim();
					}

			}
		}

		data = new SyntaxAnalyzerData(nonterminalSymbols, terminalSymbols, syncSymbols, grammarProductionRules);
	}

	private GrammarProductionRule createProduction(String currentStartSymbol, String line) {
		List<Symbol> rule = new ArrayList<>();
		for (String value : line.split(" ")) {
			rule.add(value.startsWith("<") ? new NonterminalSymbol(value) : new TerminalSymbol(value));
		}
		return new GrammarProductionRule(currentStartSymbol, rule);
	}

	public SyntaxAnalyzerData getData() {
		return data;
	}
}
