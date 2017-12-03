package lab2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GSA {

	/**
	 *
	 * @param args lab2/kanon_gramatika.san ili lab2/minusLang.san ili lab2/simplePpjLang.san
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		Path filePath = Paths.get(GSA.class.getClassLoader().getResource(args[0]).toURI());
		InputFileParser parser = new InputFileParser(filePath);
		SyntaxAnalyzerData data = parser.getData();

		LRTable table = TableGenerator.generateTable(TableGenerator.generateDKA(data.getGrammarProductionsRaw()), data.getNonterminalSymbols(), data.getTerminalSymbols());

		try (FileOutputStream fileOut = new FileOutputStream("analizator/lrtable.ser");
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
		){
			out.writeObject(table);
		}

		try (FileOutputStream fileOut = new FileOutputStream("analizator/sadata.ser");
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
		){
			out.writeObject(data);
		}

		for (int i = 0; i < table.neo.length; i++) {
			for (int j = 0; j < table.neo[i].length; j++) {
				System.out.printf("%25s" , table.neo[i][j]);
			}
			System.out.println();
		}
		//LRTable table = TableGenerator.generateTable(TableGenerator.generateDKA(/*todo send lines for generating automata from parsed data*/));
		//serialize the chair(hohohohhahahah what an laugh we are having haha)
	}
}
