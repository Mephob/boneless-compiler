import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GSA {

	/**
	 * @param args kanon_gramatika.san ili minusLang.san ili simplePpjLang.san
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		Path filePath = Paths.get(GSA.class.getClassLoader().getResource(args[0]).toURI());
		InputFileParser parser = new InputFileParser(filePath);
		SyntaxAnalyzerData data = parser.getData();
		System.out.println(data.getNonterminalSymbols());
		System.out.println(data.getTerminalSymbols());
		System.out.println(data.getSyncSymbols());
		System.out.println(data.getGrammarProductions());

		LRTable table = null;
		//TODO: Waiting for the new ENKA input requierments
//		TableGenerator.generateTable(TableGenerator.generateDKA(data.getGrammarProductions(), data.getNonterminalSymbols().get(0)), data.getNonterminalSymbols(), data.getTerminalSymbols());

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
