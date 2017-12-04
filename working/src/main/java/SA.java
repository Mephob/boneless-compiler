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
			String token = currentInput.token;
			String action = table.findAction(stack.peek().value2, token);

			System.out.println(token + " " + action);
			System.out.println(stack);
			if (action == null) {
				System.out.println("Kurcina");
				break;
			} else if(action.startsWith("Reduciraj")) {
				String[] values = action.split(" ");
				for (int i = 0; i < Integer.parseInt(values[1]); i++) {
					stack.pop();
				}

				int index = stack.peek().value2;
				String putAction = table.findAction(index, values[2]);
				if (putAction == null && token.equals("~")
						&& stack.get(0).equals(new Pair(null, 0))
						) {
					System.out.println("Prihvaca");
					break;
				}
				System.out.println(putAction);
				stack.push(new Pair(values[2], Integer.parseInt(putAction.split(" ")[1])));
			} else if (action.startsWith("Pomakni")) {
				stack.push(new Pair(token, Integer.parseInt(action.split(" ")[1])));
				currentIndex++;
			}
		}
		System.out.println(stack);
	}

	public static class Input {
		public String token;
		public int lineNumber;
		public String actualText;

		public Input(String token, int lineNumber, String actualText) {
			this.token = token;
			this.lineNumber = lineNumber;
			this.actualText = actualText;
		}

		@Override
		public String toString() {
			return token + " " + lineNumber + " " + actualText;
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
