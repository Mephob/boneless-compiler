import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SA {

	static Stack<Triplet> stack = new Stack<>();

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

		stack.push(new Triplet(null, 0, null));
		inputValues.add(new Input("~", -1, ""));
		int currentIndex = 0;
		//dodaj ovog decka
		Triplet biggeBuoy = new Triplet(null, 0, null);//roditelj svih deckica

		while (currentIndex < inputValues.size()) {
			Input currentInput = inputValues.get(currentIndex);
			String token = currentInput.token;
			String action = table.findAction(stack.peek().value, token);

			System.err.println("Citam: " + token + " " + action);
			System.err.println(stack);
			if (action == null) {
				System.out.println("Kurcina");
				break;
			} else if(action.startsWith("Reduciraj")) {
				List<Triplet> bois = new ArrayList<>();
				String[] values = action.split(" ");

				for (int i = 0; i < Integer.parseInt(values[1]); i++) {
					bois.add(stack.pop());
				}

				int index = stack.peek().value;
				String putAction = table.findAction(index, values[2]);
				if (putAction == null && token.equals("~")
						&& stack.get(0).equals(new Triplet(null, 0, new ArrayList<>()))
						) {
					System.err.println("Prihvaca");
					String name = values[2];
					biggeBuoy = new Triplet(name, 0, bois);

					break;
				}
				System.err.println(putAction);
				stack.push(new Triplet(values[2], Integer.parseInt(putAction.split(" ")[1]), bois));

				//bila je epsilon redukcija
				if (Integer.parseInt(values[1]) == 0) {
					bois.add(new Triplet("$", 0, new ArrayList<>()));
					stack.peek().children = bois;
				}

			} else if (action.startsWith("Pomakni")) {
				stack.push(new Triplet(currentInput.toString(), Integer.parseInt(action.split(" ")[1]), new ArrayList<>()));
				currentIndex++;
			}
		}
		System.err.println(stack);
		printTree(biggeBuoy, 0);
	}

	/*
	Rekurzivno ispisuje stablo inorder, pravi se da je lista stablo marijone
	 */
	public static void printTree(Triplet boi, int depth) {
		//dosta rekurzije ako sam dosao do lista
		if (boi.children.isEmpty()) {
			String rez = buildString(boi, depth);
			System.out.println(rez);
			return;
		}

		//jos uvijek nije list
		System.out.println(buildString(boi, depth));

		List<Triplet> temp = new ArrayList<>(boi.children);
		Collections.reverse(temp);
		for (Triplet child : temp) {
			printTree(child, depth + 1);
		}
	}

	private static String buildString(Triplet boi, int depth) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < depth; i++) {
			//todo: prebaci crticu u space invaders
			sb.append("-");
		}
		sb.append(boi.name);

		return sb.toString();
	}

	public static class Triplet {
		public String name;
		public Integer value;
		public List<Triplet> children;

		public Triplet(String name, Integer value, List<Triplet> children) {
			this.name = name;
			this.value = value;
			this.children = children;
		}

		@Override
		public String toString()  {
			return "(" + name + "," + value + ")";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Triplet triplet = (Triplet) o;
			return Objects.equals(name, triplet.name) &&
					Objects.equals(value, triplet.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, value);
		}
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

	private static String concatArrayOfStrings(String[] strings) {
		StringJoiner sj = new StringJoiner(" ");
		for (String string : strings) {
			sj.add(string);
		}
		return sj.toString();
	}
}
