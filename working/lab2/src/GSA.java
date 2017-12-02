package lab2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GSA {
	public static void main(String[] args) throws URISyntaxException, IOException {
		Path filePath = Paths.get(GSA.class.getClassLoader().getResource(args[0]).toURI());
		InputFileParser parser = new InputFileParser(filePath);
		SyntaxAnalyzerData data = parser.getData();
	}
}
