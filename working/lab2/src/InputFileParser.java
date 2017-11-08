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
		List<String> nonterminalSymbols = null;
		List<String> terminalSymbols = null;
		List<String> syncSymbols = null;
		
		//TODO: create a more practical data structure for this
		Map<String, List<List<String>>> grammarProductions = new HashMap<>(); 
		
		int lineNum = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			lineNum++;

			switch (lineNum) {
				case 1: // non-terminal symbols in the first line
					nonterminalSymbols = Arrays.asList(line.substring(3).split(" "));
					break;
				case 2: // terminal symbols in the second line
					terminalSymbols = Arrays.asList(line.substring(3).split(" "));
					break;
				case 3: // symbols for synchronization in the third line
					syncSymbols = Arrays.asList(line.substring(5).split(" "));
					break;
				default:
					//TODO: parsing grammar productions

			}
		}

		data = new SyntaxAnalyzerData(nonterminalSymbols, terminalSymbols, syncSymbols, grammarProductions);
	}


}
