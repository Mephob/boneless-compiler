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
		List<String> nonterminalSymbols = new ArrayList<>();
		List<String> terminalSymbols = new ArrayList<>();
		List<String> syncSymbols = new ArrayList<>();
		List<String> grammarProductionsRaw = new ArrayList<>();

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
					grammarProductionsRaw.add(line);
			}
		}

		data = new SyntaxAnalyzerData(nonterminalSymbols, terminalSymbols, syncSymbols, grammarProductionsRaw);
	}

	public SyntaxAnalyzerData getData() {
		return data;
	}
}
