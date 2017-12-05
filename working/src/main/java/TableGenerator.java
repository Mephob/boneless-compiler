
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.*;


public class TableGenerator {

	public static LRTable generateTable(DKA dka, List<String> nonterminalSymbols, List<String> terminalSymbols) {
		List<DKA.DKAState> states = dka.getStates();
		int numberOfStates = states.size();

		//columns = terminal + 1(eof) + nontermianlkenser
		int columnCounter = 0;
		Map<String, Integer> columns = new HashMap<>();

		//fill the map with terminal symbols
		for (String terminalSymbol : terminalSymbols) {
			columns.put(terminalSymbol, columnCounter++);
		}

		//add the eof symbol to the table
		columns.put("~", columnCounter++);

		//fill the map with nonterminal symbols
		for (String nonterminalSymbol : nonterminalSymbols) {
			columns.put(nonterminalSymbol, columnCounter++);
		}



		//[retci][stupci]
		String[][] matrix = new String[numberOfStates][columnCounter];

		//populate the matrix
		//gledamo DKA
		//iteriramo sva stanja, i punimo matricu na mjesto koje odgovara onom za prijelaze
		//iteriramo prijelaze
		for (int i = 0; i < numberOfStates; i++) {
			System.err.println(dka.getStates().get(i).text);

			String[] lines = dka.getStates().get(i).text.split("\n");

			Set<String> reducedBy = horseShoe(lines);
			Set<String> movedFor = extremeIntegrating(lines);

			//reducedBy works properly

			for (int j = 0; j < terminalSymbols.size(); j++) {
				if (reducedBy.contains(terminalSymbols.get(j))) {//reduction is applied
					//find what reduction is applied
					//case 3b
					matrix[i][j] = nyahaha(lines, terminalSymbols.get(j));
				}
			}
			if (reducedBy.contains("~")) {//reduction is applied
				//find what reduction is applied
				//case 3b
				//System.out.println("im in");

				matrix[i][terminalSymbols.size()] = nyahaha(lines, "~");
			}

			int j = 0;
			for (Map.Entry<String, DKA.DKAState> entry : dka.getStates().get(i).crossings.entrySet()) {
				System.err.println(dka.getStates().get(i).id + "        " + entry.getKey() + "->" + entry.getValue().id + "      " +  j);

				//System.out.print("key = " + entry.getKey() + "\n");

				//boolean reducedContain = reducedBy.contains(entry.getKey());
				//System.out.println(reducedContain);

				//System.out.println(entry.getKey());

				if (terminalSymbols.contains(entry.getKey())) {
					//case 3a
					//enter new state
					int place = terminalSymbols.indexOf(entry.getKey());
					matrix[i][place] = "Pomakni " + (entry.getValue().id - 1);
				} else if (nonterminalSymbols.contains(entry.getKey())) { //reduction was applied
					//case 4a
					//reduction was applied
					matrix[i][columns.get(entry.getKey())] = "Stavi " + (Integer.toString(entry.getValue().id - 1));
				}

				j++;

			}
		}

		/*stanje i sa prijelazom j, ako postoji prijelaz iz i u neko novo stanje,
		 * na mjesto [i][mjesto nezavrsnog znaka] ide stavi ID
		 * [i][mapa index] -> crossing.id
		 */

		LRTable table = new LRTable(columns, matrix);
		return table;
	}

	private static String nyahaha(String[] lines, String terminalSymbol) {

		String matrixEntry = "Reduciraj ";
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("<S'>")) {
				//skip
			} else {

				String right = lines[i].split("\\*")[1];
				char[] rightChar = right.toCharArray();
				if (rightChar[0] == ',') {//we found a potential reduction
					String[] reducedByString = right.split("\\{")[1].trim().split(",");
					int lastIndex = reducedByString.length - 1;
					int lastLength = reducedByString[lastIndex].length();
					reducedByString[lastIndex] = reducedByString[lastIndex].substring(0, lastLength - 2);

					List<String> reducers = Arrays.asList(reducedByString);
					if (reducers.contains(terminalSymbol)) {//this is the one

						//parse the line again, lines[i]
						String rightSide = lines[i].split("->")[1];
						rightSide = rightSide.split("\\*")[0].trim();

						if (rightSide.isEmpty()) {//eps production
							matrixEntry += Integer.toString(0);
							matrixEntry += " ";
							matrixEntry += lines[i].split("->")[0].trim();
						} else {//non eps production
							String[] tokens = rightSide.split(" ");
							matrixEntry += Integer.toString(tokens.length);
							matrixEntry += " ";
							matrixEntry += lines[i].split("->")[0].trim();
						}


					} else {
						continue;
					}

				}

			}
		}

		/*
		 * Reduciraj 3 <A>
		 */

		return matrixEntry;
	}

	private static Set<String> extremeIntegrating(String[] lines) {
		Set<String> movedFor = new TreeSet<>();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("<S'>")) {
				//skip
			} else {

				String right = lines[i].split("\\*")[1];
				char[] rightChar = right.toCharArray();
				if (rightChar[0] == ',') {
					//skip
					continue;
				} else {
					right = right.substring(1);
					String movedForCandidate = right.split(" ")[0];
					movedFor.add(movedForCandidate);
				}

			}
		}

		return movedFor;
	}

	private static Set<String> horseShoe(String[] lines) {
		Set<String> reducedBy = new TreeSet<>();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("<S'>")) {
				//skip
			} else {

				String right = lines[i].split("\\*")[1];

				right = right.trim();
				//right side parsed correctly
				char[] rightChar = right.toCharArray();
				if (rightChar[2] == '{') {//has to be reduced
					String[] reducedByString = right.split("\\{")[1].trim().split(",");

					//reducedByString works properly
					if(reducedByString.length > 0){
						int lastIndex = reducedByString.length - 1;
						int lastLength = reducedByString[lastIndex].length();
						//System.out.println(reducedByString[lastIndex]);
						reducedByString[lastIndex] = reducedByString[lastIndex].substring(0, lastLength - 2);
					}

					for (int j = 0; j < reducedByString.length; j++) {
						//System.out.println(reducedByString[j]);
						reducedBy.add(reducedByString[j]);
						//this works
					}
				}
			}
		}

		return reducedBy;
	}

	public TableGenerator() {

	}

	//###################################################################################################################################
	public static void main(String[] args) throws IOException {
		//minusLang
		//kanon_gramatika
		System.out.println(generateDKA(new InputFileParser(Paths.get("main/resources/simplePpjLang.san")).getData()));

	}


	public static DKA generateDKA(SyntaxAnalyzerData data) {
		ENKA enka = generateENKA(data);
		//System.err.println(enka.n);
		Map<Integer, Set<Integer>> enkToDkaMap = new HashMap<>();

		ENKA.ENKAState first = null;
		for (int i = 0, n = enka.states.size(); i < n; i++) {
			if (enka.states.get(i).left.equals("<S'>")) {
				first = enka.states.get(i);
				break;
			}
		}

		DKA result = new DKA();

		Map<ENKA.ENKAState, Set<ENKA.ENKAState>> epsSurroundings = new HashMap<>();
		for (ENKA.ENKAState e : enka.states) {
			Set<ENKA.ENKAState> buff = new HashSet<>();
			findEpsSurrounding(buff, e);
			epsSurroundings.put(e, buff);
		}

		String firstStateString = generateEnkaToDkaText(epsSurroundings.get(first));

		DKA.DKAState firstDka = result.new DKAState(firstStateString);
		firstDka.enkaStates = epsSurroundings.get(first);

		firstDka.findStates(epsSurroundings);

		return result;
	}

	private static void findEpsSurrounding(Set<ENKA.ENKAState> set, ENKA.ENKAState e) {
		set.add(e);
		if (set.addAll(e.eCrossings)) {
			for (ENKA.ENKAState eBuff : e.eCrossings) {
				findEpsSurrounding(set, eBuff);
			}
		}
	}


	private static ENKA generateENKA(SyntaxAnalyzerData data) {
		ENKA result = new ENKA();

		//{a, b, c} and stuff
		List<String> thingiesList = new ArrayList<>();
		data.getNonterminalSymbols().forEach(s -> thingiesList.add(s.getValue()));
		data.getTerminalSymbols().forEach(t -> thingiesList.add(t.getValue()));

		boolean[][] beginings = new boolean[thingiesList.size()][thingiesList.size()];
		for (ProductionRule p : data.getGrammarProductions()) {
			if (p.getRightSide().size() == 1 && p.getRightSide().get(0).getValue().equals("$")) {

				Symbol buff = new NonterminalSymbol(p.getLeftSide());
				//for (int i = 0; i < beginings.length; i++) {
					for (ProductionRule subP : data.getGrammarProductions()) {
						List<Symbol> rightSide = subP.getRightSide();

						int index = rightSide.indexOf(buff);
						if (index == -1) {
							continue;
						}

						int k = 0;
						for (; k < index; k++) {
							if (!hasEpsProd(rightSide.get(k).getValue(), data)) {
								break;
							}
						}

						if (k == index) { //svi znakovi ipred imaju epsilon
							beginings[thingiesList.indexOf(subP.getLeftSide())]
								  [thingiesList.indexOf(rightSide.get(index+1).getValue())] = true;
						}
					}
				//}
				continue;
			}

			beginings[thingiesList.indexOf(p.getLeftSide())][thingiesList.indexOf(p.getRightSide().get(0).getValue())] = true;
		}

		Map<String, Set<String>> mapOfBeginings = findBeginings(beginings, thingiesList);

		ENKA.ENKAState buff = result.new ENKAState("<S'>", new String[]{null, data.getNonterminalSymbols().get(0).getValue()});
		buff.finishers.add("~");
		//ENKA.ENKAState after = result.new ENKAState("<S'>", new String[]{data.getNonterminalSymbols().get(0).getValue(), "*"});
		//buff.crossings.put(data.getNonterminalSymbols().get(0).getValue(), after);

//----------------------------------------------------------------------------------------------------------------------
		buff.findYourCrossings(data, mapOfBeginings);
		//System.err.println(result.n);
		return result;
	}

	private static String generateEnkaToDkaText(Set<ENKA.ENKAState> states) {
		StringBuilder bob = new StringBuilder();

		List<ENKA.ENKAState> sort = new ArrayList<>(states);
		sort.sort(Comparator.comparingInt(o -> o.id));

		for (ENKA.ENKAState e3 : states) {
			bob.append(e3.mainString());
			bob.append(", { ");
			for (String s : e3.finishers) {
				bob.append(s + ",");
			}
			bob.deleteCharAt(bob.length() - 1);
			bob.append(" }\n");
		}

		return bob.toString();
	}


	private static Map<String, Set<String>> findBeginings(boolean[][] field, List<String> elements) {
		boolean[] done = new boolean[field.length];
		boolean finish = false;

		while (!finish) {
			mainFor:
			for (int i = 0; i < field.length; i++) {
				if (!(elements.get(i).startsWith("<") && elements.get(i).endsWith(">"))) {
					field[i][i] = true;
				} else {
					for (int j = 0; j < field[i].length; j++) {
						if (!field[i][j]) {
							continue;
						}
						if (i == j) {
							continue;
						}

						if (!done[j]) {
							continue mainFor;
						}

						for (int k = 0; k < field[i].length; k++) {
							if (field[j][k]) {
								field[i][k] = true;
							}
						}
					}
				}

				done[i] = true;
			}

			finish = true;
			for (boolean b : done) {
				finish &= b;
			}
		}

		Map<String, Set<String>> result = new HashMap<>();
		for (int i = 0; i < field.length; i++ ) {
			HashSet<String> beginers = new HashSet<>();
			for (int j = 0; j < field.length; j++) {
				if (field[i][j]
					  && !elements.get(j).startsWith("<") && !elements.get(j).startsWith(">")) {
					beginers.add(elements.get(j));
				}
			}

			result.put(elements.get(i), beginers);
		}

		return result;
	}


	private static boolean hasEpsProd(String symbol, SyntaxAnalyzerData data) {
		for (ProductionRule p : data.getGrammarProductions()) {
			if (p.getLeftSide().equals(symbol)
				  && p.getRightSide().size() == 1
				  && p.getRightSide().get(0).value.equals("$")) {
				return true;
			}
		}

		return false;
	}

	public static class DKA {


		private DKAState get(String text) {
			for (DKAState d : states) {
				if (d.text.equals(text)) {
					return d;
				}
			}

			return null;
		}

		public class DKAState {

			private int id;
			private String text;
			private Map<String, DKAState> crossings = new HashMap<>();
			private Set<ENKA.ENKAState> enkaStates;

			private void findStates (Map<ENKA.ENKAState, Set<ENKA.ENKAState>> epsSurroundings) {
				Map<String, Set<ENKA.ENKAState>> allCrossingsForE = new HashMap<>();

				for (ENKA.ENKAState e : enkaStates) {
					for (String s : e.crossings.keySet()) {
						if (allCrossingsForE.get(s) == null) {
							allCrossingsForE.put(s, new HashSet<>(Collections.singleton(e.crossings.get(s))));
						} else {
							allCrossingsForE.get(s).add(e.crossings.get(s));
						}
					}
				}

				for (String s : allCrossingsForE.keySet()) {
					Set<ENKA.ENKAState> includeEps = new HashSet<>();
					allCrossingsForE.get(s).forEach(x -> includeEps.addAll(epsSurroundings.get(x)));

					String dkaText = generateEnkaToDkaText(includeEps); //epsSurroundings.get(allCrossingsForE));

					DKAState buff = DKA.this.get(dkaText);

					if (buff == null) {
						buff = DKA.this.new DKAState(dkaText);
						buff.enkaStates = includeEps;
						buff.findStates(epsSurroundings);
					}

					crossings.put(s, buff);
				}
			}

			private DKAState(String text) {
				id = ++n;
				this.text = text;
				states.add(this);
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;

				DKAState dkaState = (DKAState) o;

				return id == dkaState.id;
			}

			@Override
			public int hashCode() {
				return id;
			}

			@Override
			public String toString() {
				StringBuilder bob = new StringBuilder();
				bob.append(id);
				bob.append('\n');
				bob.append(text);

				for (String s : crossings.keySet()) {
					bob.append('\n');
					bob.append("  ");

					bob.append('-');
					bob.append(s);
					bob.append("->");

					bob.append("(" + crossings.get(s).id + ")");
				}

				return bob.toString();
			}
		}


		private int n = 0;
		List<DKAState> states = new ArrayList<>();


		public List<DKAState> getStates() {
			return states;
		}

		private DKAState getForId(int id) {
			return states.get(id - 1);
		}

		@Override
		public String toString() {
			StringBuilder bob = new StringBuilder();

			for (DKAState dnkaS : states) {
				bob.append(dnkaS.toString());
				bob.append('\n');
				bob.append("--------------------------------");
				bob.append('\n');
			}

			return bob.toString();
		}

	}


	private static class ENKA {

		private int n;
		List<ENKAState> states = new ArrayList<>();

		private ENKAState get(String left, String[] right, Set<String> finishers) {
			for (ENKAState e : states) {
				if (e.left.equals(left)
					  && Arrays.equals(e.right, right)
					  && e.finishers.equals(finishers)) {
					return e;
				}
			}

			return null;
		}

		@Override
		public String toString() {
			StringBuilder bob = new StringBuilder();

			for (ENKAState enkaS : states) {
				bob.append(enkaS.toString());
				bob.append('\n');
				bob.append("--------------------------------");
				bob.append('\n');
			}

			return bob.toString();
		}

		private class ENKAState {
			private int id;
			private String left;
			private String[] right;
			private Map<String, ENKAState> crossings = new HashMap<>();
			private List<ENKAState> eCrossings = new ArrayList<>(4);
			private Set<String> finishers = new HashSet<>();

			public ENKAState(String left, String[] right) {
				id = ++n;
				states.add(this);
				this.left = left;
				this.right = right;
			}


			private int indexOfDot() {
				for (int i = 0; i < right.length; i++) {
					if (right[i] == null) {
						return i;
					}
				}

				throw new RuntimeException("There aint no dot in:" + toString());
			}


			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;

				ENKAState enkaState = (ENKAState) o;

				return id == enkaState.id;
			}

			@Override
			public int hashCode() {
				return id;
			}

			@Override
			public String toString() {
				StringBuilder bob = new StringBuilder(mainString());


				for (String s : crossings.keySet()) {
					bob.append('\n');
					bob.append("  ");

					bob.append('-');
					bob.append(s);
					bob.append("->");

					bob.append("(" + crossings.get(s).mainString());
					bob.append(", { ");
					for (String splj : finishers) {
						bob.append(splj + ",");
					}
					bob.deleteCharAt(bob.length() - 1);
					bob.append(" }");
					bob.append(')');
				}

				bob.append('\n');
				bob.append("  ");
				bob.append('-');
				bob.append('$');
				bob.append("->");
				for (ENKAState s : eCrossings) {
					bob.append("(" + s.mainString());
					bob.append(", { ");
					for (String splj : s.finishers) {
						bob.append(splj + ",");
					}
					bob.deleteCharAt(bob.length() - 1);
					bob.append(" }");
					bob.append(")");
					bob.append(", ");
				}
				if (bob.charAt(bob.length() - 1) != '>') {
					bob.delete(bob.length() - 2, bob.length());
				}

				bob.append('\n');
				bob.append("  {");
				for (String s : finishers) {
					bob.append(s);
					bob.append(", ");
				}
				if (bob.charAt(bob.length() - 1) != '{') {
					bob.delete(bob.length() - 2, bob.length());
				}

				bob.append("}\n");

				return bob.toString();
			}

			private String mainString() {
				StringBuilder bob = new StringBuilder(left);

				bob.append(" -> ");

				for (String s : right) {
					bob.append(s == null ? "*" : s);
					bob.append(' ');
				}
				bob.deleteCharAt(bob.length() - 1);

				return bob.toString();
			}

			private void findYourCrossings(SyntaxAnalyzerData data, Map<String, Set<String>> mapOfBeginings) {
				if (indexOfDot() >= right.length-1) {
					return;
				}

				//moving * one place back and generetingIfAbsent state
				String[] followerRight = Arrays.copyOf(right, right.length);
				followerRight[indexOfDot()] = followerRight[indexOfDot()+1];
				followerRight[indexOfDot()+1] = null;
				ENKAState follower = get(left, followerRight, finishers);

				if (follower == null) {
					follower = new ENKAState(left, followerRight);
					follower.finishers = finishers;
					System.err.println(toString() + "created:" + follower.toString());
					follower.findYourCrossings(data, mapOfBeginings);
				}

				crossings.put(follower.right[indexOfDot()], follower);


				//generetingIfAbsent states that this state will eps cross to
				Set<String> childFinishers;
				if (indexOfDot()+2 >= right.length) {
					childFinishers = finishers;
				} else {
					int index = indexOfDot()+2;
					String symbol = right[index];
					childFinishers = new HashSet<>(mapOfBeginings.get(symbol));

					while (hasEpsProd(symbol, data)) {
						if (++index >= right.length) {
							childFinishers.addAll(finishers);
							break;
						}

						symbol = right[index];
						childFinishers.addAll(mapOfBeginings.get(symbol));
					}
				}

				String postDot = right[indexOfDot()+1];
				for (ProductionRule p : data.getGrammarProductions()) {
					if (p.getLeftSide().equals(postDot)) {
						List<String> rightSide = new ArrayList<>();
						rightSide.add(null);
						if (!(p.getRightSide().size() == 1 && p.getRightSide().get(0).value.equals("$"))) {
							p.getRightSide().forEach(s -> rightSide.add(s.value));//<<<bilo samo ovo bez ovo if-a
						}

						String[] rightSideArray = rightSide.toArray(new String[rightSide.size()]);

						ENKAState child = get(p.getLeftSide(), rightSideArray, childFinishers);

						if (child == null) {
							child = new ENKAState(p.getLeftSide(), rightSideArray);
							child.finishers = childFinishers;
							System.err.println(toString() + "created:" + child.toString());
							child.findYourCrossings(data, mapOfBeginings);
						}

						eCrossings.add(child);
					}
				}
			}
		}
	}
}