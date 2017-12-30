import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GSA {

	/**
	 * @param args kanon_gramatika.san ili minusLang.san ili simplePpjLang.san
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		//Path filePath = Paths.get(GSA.class.getClassLoader().getResource(args[0]).toURI());
		InputFileParser parser = new InputFileParser(System.in);
		SyntaxAnalyzerData data = parser.getData();
		System.out.println(data.getNonterminalSymbols());
		System.out.println(data.getTerminalSymbols());
		System.out.println(data.getSyncSymbols());
		System.out.println(data.getGrammarProductions());

		//TODO: Waiting for the new ENKA input requierments
		List<String> nezav = new ArrayList();
		List<String> zav = new ArrayList<>();
		List<NonterminalSymbol> nonte = data.getNonterminalSymbols();
		List<TerminalSymbol> ter = data.getTerminalSymbols();

		for (NonterminalSymbol t : nonte) {
			nezav.add(t.value);
		}

		for (TerminalSymbol t : ter) {
			zav.add(t.value);
		}

		LRTable table = TableGenerator.generateTable(TableGenerator.generateDKA(data), nezav, zav);

		try (FileOutputStream fileOut = new FileOutputStream("analizator/lrtable.ser");
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
		) {
			out.writeObject(table);
		}

		try (FileOutputStream fileOut = new FileOutputStream("analizator/sadata.ser");
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
		) {
			out.writeObject(data);
		}

		System.out.println();

		for (int i = 0; i < table.neo.length; i++) {
			for (int j = 0; j < table.neo[i].length; j++) {
				System.out.printf("%25s", table.neo[i][j]);
			}
			System.out.println();
		}
	}
}
