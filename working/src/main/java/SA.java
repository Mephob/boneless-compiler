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

			//System.err.println("Citam: " + token + " " + action);
			//System.err.println(stack);

			if (action == null) {
				recoveryMessage(stack.peek().value, currentInput.actualText, table, data.getTerminalSymbols());
				int shift = recoveryProcedure(currentIndex, inputValues, table, data.getSyncSymbols());
				currentIndex += shift;
				continue;
			} else if (action.startsWith("Prihvati") || stack.peek().name != null && stack.peek().name.startsWith("<S'>")) {

					String[] values = action.split(" ");
					List<Triplet> bois = new ArrayList<>();

				biggeBuoy = stack.pop();
				//System.err.println(biggeBuoy + " " + " ************ ");

							break;

			} else if(action.startsWith("Reduciraj")) {
				List<Triplet> bois = new ArrayList<>();
				String[] values = action.split(" ");

				for (int i = 0; i < Integer.parseInt(values[1]); i++) {
					bois.add(stack.pop());
				}

				if(values[2].equals("<S'>")){
					biggeBuoy = new Triplet(values[2], 0, bois);
					break;
				} else {
					int index = stack.peek().value;
					String putAction = table.findAction(index, values[2]);
					stack.push(new Triplet(values[2], Integer.parseInt(putAction.split(" ")[1]), bois));
				}



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
		//System.err.println(stack);
		printTree(biggeBuoy, 0);
	}

	/*
	Prima trenutno stanje, niz koji je izazvao pogresku, tablica i svi zavrsni znakovi.
	Ispisuje poruku o pogreski.
	 */
	private static void recoveryMessage(int currentState, String actualText, LRTable table, List<TerminalSymbol> terminals) {
		StringBuilder sb = new StringBuilder();
		sb.append("Pogreska se dogodila u retku: ").append(currentState).append("\n")
				.append("Procitan je niz: ").append(actualText).append("\n").append("\n")
				.append("Mozda ste mislili na nesto od ovoga:");

		//trazi sve zavrsne za koje postoji neka akcija, tj za koje ne bi doslo do pogreske
		for (TerminalSymbol terminal : terminals) {
			String kms = terminal.value;
			if (table.findAction(currentState, kms) != null) {
				sb.append(" ").append(terminal);
			}
		}
		//System.err.println(sb.toString());
	}

	private static int recoveryProcedure(int index, List<Input> imp, LRTable table, List<SynchronizingSymbol> synchros) {
		int counter = 0;
		int tempIndex = index;
		String znak;

		//faza 1 - trazimo prvi sljedeci sinkronizacijski
		do {
			znak = imp.get(tempIndex).token;
			if (synchros.contains(new SynchronizingSymbol(znak))) {
				break;
			}
			tempIndex++;
			counter++;
		} while (tempIndex < imp.size());

		//faza 2 - odbacujemo stack entryje dok ne naletimo na stanje u kojem postoji neka normalna akcija
		do {
			stack.pop();
			if (table.findAction(tempIndex, znak) != null) {
				break;
			}
		} while (stack.isEmpty() == false);

		//za koliko smo se pomakli u indeksu
		return counter;
	}

	/*
	Rekurzivno ispisuje stablo inorder, pravi se da je lista stablo marijone
	 */
	public static void printTree(Triplet boi, int depth) {
		//dosta rekurzije ako sam dosao do lista
		//System.err.println(depth);
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
			sb.append(" ");
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
