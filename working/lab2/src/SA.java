package lab2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SA {

	static Stack<Pair> stack = new Stack<>();

	public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException {
		Path filePath = Paths.get(GSA.class.getClassLoader().getResource(args[0]).toURI());
		Scanner sc = new Scanner(filePath);
		List<Input> inputValues = new ArrayList<>();
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] values = line.split(" ");
			inputValues.add(new Input(values[0], Integer.parseInt(values[1]), concatArrayOfStrings(Arrays.copyOfRange(values, 2, values.length))));
		}

		LRTable table = Util.readSerFile("lrtable.ser");
		SyntaxAnalyzerData data = Util.readSerFile("sadata.ser");

		executeLR(inputValues, table, data);

	}

	private static void executeLR(List<Input> inputValues, LRTable table, SyntaxAnalyzerData data) {

		stack.push(new Pair(null, 0));
		inputValues.add(new Input("~", -1, ""));
		int currentIndex = 0;
		while (currentIndex < inputValues.size()) {
			Input currentInput = inputValues.get(currentIndex);
			String unifChar = currentInput.unformniZnak;
			String action = table.findAction(stack.peek().value2, unifChar);
			System.out.println(action);

			if (action == null) {
//				System.err.println("Greška: " + currentInput);
			} else if(action.startsWith("Reduciraj")) {
				String[] values = action.split(" ");
				for (int i = 0; i < Integer.parseInt(values[1]); i++) {
					stack.pop();
				}
				int index = stack.peek().value2;
				String putAction = table.findAction(index, values[2]);
				if (unifChar.equals("~")
						&& stack.get(0).equals(new Pair(null, 0))
						&& values[2].equals(data.getNonterminalSymbols().get(0))
						&& putAction == null
						) {
					System.out.println("Prihvaća");
					break;
				}
				stack.push(new Pair(values[2], Integer.parseInt(putAction.split(" ")[1])));
			} else if (action.startsWith("Pomakni")) {
				stack.push(new Pair(unifChar, Integer.parseInt(action.split(" ")[1])));
				currentIndex++;
			}
		}
		System.out.println(stack);
	}

	public static class Input {
		public String unformniZnak;
		public int lineNumber;
		public String token;

		public Input(String unformniZnak, int lineNumber, String token) {
			this.unformniZnak = unformniZnak;
			this.lineNumber = lineNumber;
			this.token = token;
		}

		@Override
		public String toString() {
			return unformniZnak + " " + lineNumber + " " + token;
		}
	}

	public static class Pair {
		public String value1;
		public Integer value2;

		public Pair(String value1, Integer value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public String toString() {
			return "("+value1+","+value2+")";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Pair pair = (Pair) o;
			return Objects.equals(value1, pair.value1) &&
					Objects.equals(value2, pair.value2);
		}

		@Override
		public int hashCode() {

			return Objects.hash(value1, value2);
		}
	}

	private static String concatArrayOfStrings(String[] strings) {
		StringJoiner sj = new StringJoiner(" ");
		for (String string : strings) {
			sj.add(string);
		}
		return sj.toString();
	}
}
