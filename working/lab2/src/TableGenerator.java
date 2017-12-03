
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
			String[] lines = dka.getStates().get(i).text.split("\n");
			Set<String> reducedBy = horseShoe(lines);
			Set<String> movedFor = extremeIntegrating(lines);

			int j = 0;
			for (Map.Entry<String, DKA.DKAState> entry : dka.getStates().get(i).crossings.entrySet()) {
				if (reducedBy.contains(entry.getKey())) {//reduction is applied
					//find what reduction is applied
					//case 3b

					matrix[i][j] = nyahaha(lines, entry.getValue().id, entry.getKey());
				} else if (terminalSymbols.contains(entry.getKey())) {
					//case 3a
					//enter new state
					matrix[i][j] = new String("Pomakni " + entry.getValue().id);
				} else if (nonterminalSymbols.contains(entry.getKey())) { //reduction was applied
					//case 4a
					//reduction was applied
					matrix[i][columns.get(entry.getKey())] = new String("Stavi " + Integer.toString(entry.getValue().id));
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

	private static String nyahaha(String[] lines, int id, String crossing) {
		String matrixEntry = "Reduciraj ";
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("<S'>")) {
				//skip
			} else {

				String right = lines[i].split("\\*")[1];
				char[] rightChar = right.toCharArray();
				if (rightChar[0] == ',') {//we found a potential reduction
					String[] reducedByString = right.split("\\{")[1].split(",");
					int lastIndex = reducedByString.length - 1;
					int lastLength = reducedByString[lastIndex].length();
					reducedByString[lastIndex].substring(0, lastLength - 2);

					List<String> reducers = Arrays.asList(reducedByString);
					if (reducers.contains(crossing)) {//this is the one
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
				char[] rightChar = right.toCharArray();
				if (rightChar[2] == '{') {//has to be reduced
					String[] reducedByString = right.split("\\{")[1].split(",");
					int lastIndex = reducedByString.length - 1;
					int lastLength = reducedByString[lastIndex].length();
					reducedByString[lastIndex].substring(0, lastLength - 2);

					for (int j = 0; j < reducedByString.length; j++) {
						reducedBy.add(reducedByString[j]);
					}
				}
			}
		}

		return reducedBy;
	}

	public TableGenerator() {

	}


	public static void main(String[] args) {
		System.out.println(generateENKA(Arrays.asList(
				"<S>",
				" <A>",
				"<A>",
				" <B> <A>",
				" $",
				"<B>",
				" a <B>",
				" b")));

	}


	public static DKA generateDKA(List<String> lines) {
		ENKA enka = generateENKA(lines);
		System.out.println(enka);
		Map<Integer, Set<Integer>> enkToDkaMap = new HashMap<>();

		ENKA.ENKAState first = null;
		for (int i = 0, n = enka.states.size(); i < n; i++) {
			if (enka.states.get(i).left.equals("<S'>")) {
				first = enka.states.get(i);
				break;
			}
		}

		DKA result = new DKA();
		List<ENKA.ENKAState> todo = new ArrayList<>();
		todo.add(first);
		Set<ENKA.ENKAState> done = new HashSet<>();

		//add all the states
		while (!todo.isEmpty()) {
			Set<ENKA.ENKAState> epsSurrounding = new HashSet<>();
			epsSurrounding.add(todo.get(0));

			Set<ENKA.ENKAState> diff = new HashSet<>();
			while (!diff.equals(epsSurrounding)) {
				diff = new HashSet<>(epsSurrounding);
				for (ENKA.ENKAState e : diff) {
					epsSurrounding.addAll(e.eCrossings);
				}
			}

			done.addAll(epsSurrounding);
			epsSurrounding.forEach(s -> {
				s.crossings.forEach((key, c) -> {
					if (!done.contains(c) && !todo.contains(c)) {
						todo.add(c);
					}
				});
			});
			todo.remove(0);

			StringBuilder bob = new StringBuilder();
			for (ENKA.ENKAState e : epsSurrounding) {
				bob.append(e.mainString());
				bob.append(", { ");
				for (String s : e.finishers) {
					bob.append(s + ",");
				}
				bob.deleteCharAt(bob.length() - 1);
				bob.append(" }\n");
			}
			DKA.DKAState dkaBuff = result.new DKAState(bob.toString());
			//map nka states to dka states id
			epsSurrounding.forEach(e -> {
				if (enkToDkaMap.get(e.id) == null) {
					enkToDkaMap.put(e.id, new HashSet<>(Arrays.asList(dkaBuff.id)));

				} else {
					enkToDkaMap.get(e.id).add(dkaBuff.id);
				}
			});
		}

		//connect all the states
		enka.states.forEach(s -> {
			Set<Integer> dkaEqList = enkToDkaMap.get(s.id);
			//System.out.println(s.toString() + dkaEqList);
			dkaEqList.forEach(dkaEq -> {
				s.crossings.forEach((key, state) -> {
					//System.out.println("worikn on:" + dkaEq + ">>>" + key + "--->>>>" + enkToDkaMap.get(state.id));
					enkToDkaMap.get(state.id).forEach(a -> {
						result.getForId(dkaEq).crossings.put(key, result.getForId(a));
					});

				});
			});
		});

		return result;
	}


	private static ENKA generateENKA(List<String> lines) {
		ENKA result = new ENKA();
		ENKA.ENKAState first = null;
		Set<String> thingies = new HashSet<>();

		String ref = null;
		boolean firstDone = false;
		for (int i = 0, n = lines.size(); i < n; i++) {
			if (!lines.get(i).startsWith(" ")) {
				ref = lines.get(i);
				thingies.add(ref);
			} else if (lines.get(i).substring(1).equals("$")) {
				result.new ENKAState(ref, new String[]{null});
				thingies.add(ref);
			} else {
				String[] elements = lines.get(i).substring(1).split(" ");
				thingies.addAll(Arrays.asList(elements));
				ENKA.ENKAState previous = null;

				for (int j = 0; j <= elements.length; j++) {
					String[] right = new String[elements.length + 1];

					for (int l = 0, inserted = 0; l <= elements.length; l++) {
						if (l == j) {
							right[l] = null;
							inserted++;
							continue;
						}

						right[l] = elements[l - inserted];
					}

					ENKA.ENKAState current = result.new ENKAState(ref, right);
					if (previous != null) {
						previous.crossings.put(elements[j - 1], current);
					}

					previous = current;

					if (!firstDone) {
						first = current;
						firstDone = true;
					}
				}
			}
		}

		//add eps crossings
		for (ENKA.ENKAState e : result.states) {
			int i = e.indexOfDot();

			if (i >= e.right.length - 1) {
				continue;
			}

			for (ENKA.ENKAState f : result.states) {
				if (f.indexOfDot() == 0 && f.left.equals(e.right[i + 1])) {
					e.eCrossings.add(f);
				}
			}
		}

		//{a, b, c} and stuff
		boolean[][] beginings = new boolean[thingies.size()][thingies.size()];
		List<String> thingiesList = new ArrayList<>(thingies);
		for (ENKA.ENKAState enkaS : result.states) {
			if (enkaS.right[0] != null) {
				beginings[thingiesList.indexOf(enkaS.left)][thingiesList.indexOf(enkaS.right[0])] = true;
			} else if (enkaS.right.length == 1) {
				for (ENKA.ENKAState e : result.states) {
					for (int i = 0; i < e.right.length - 2; i++) {
						if (e.right.length == 1) {
							continue;
						}
						if (e.right[i] == null && e.right[i + 1].equals(enkaS.left)) {
							beginings[thingiesList.indexOf(enkaS.left)][thingiesList.indexOf(enkaS.right[i + 2])] = true;
							break;
						}
					}
				}
			}
		}

		findBeginings(beginings, thingiesList);


		ENKA.ENKAState buff = result.new ENKAState("<S'>", new String[]{first.left});

		//podrazumijeva se da je prvo stanje prvo stanji i prijelaz u tekstualnoj datoteci
		//buff.eCrossings = Arrays.asList(first);
		ENKA.ENKAState firstForAutistLambda = first;
		//System.out.println(first.left);
		result.states.forEach(s -> {
			if (s.left.equals(firstForAutistLambda.left) && s.indexOfDot() == 0) {
				buff.eCrossings.add(s);
			}
		});

		buff.propagateFinishers(beginings, thingiesList, Collections.singleton("~"));
		//first.calcluateChildEpsFinishers(beginings, thingiesList);




		return result;
	}


	private static void findBeginings(boolean[][] field, List<String> elements) {
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
	}

	public static class DKA {

		public class DKAState {

			private int id;
			private String text;
			private Map<String, DKAState> crossings = new HashMap<>();

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
			private int epsCalculated = 0;

			public ENKAState(String left, String[] right) {
				id = ++n;
				states.add(this);
				this.left = left;
				this.right = right;
			}


			private int indexOfDot() {
				int i = 0;
				for (; i < right.length; i++) {
					if (right[i] == null) {
						break;
					}
				}

				return i;
			}

			private void calcluateChildEpsFinishers(boolean[][] beginings, List<String> thingiesList) {
				//if (epsCalculated != eCrossings.size()) {
				//	return;
				//}
				//epsCalculated = true;

				//propagateFinishers(beginings, thingiesList, finishers);

				Set<String> buffFinishers = new HashSet<>();
				if (indexOfDot() + 2 >= right.length) {
					buffFinishers = finishers;
				} else {
					int index = thingiesList.indexOf(right[indexOfDot() + 2]);

					for (int i = 0; i < beginings.length; i++) {
						String buff = thingiesList.get(i);
						if (beginings[index][i] && !(buff.startsWith("<") && buff.endsWith(">"))) {
							buffFinishers.add(buff);
						}
					}

					for (ENKAState e : states) {
						if (e.left.equals(thingiesList.get(index))) {
							if (e.right.length == 1) {
								buffFinishers.add("~");
							}
						}
					}
				}

				for (ENKAState e : eCrossings) {
					//e.finishers = buffFinishers;
					e.propagateFinishers(beginings, thingiesList, buffFinishers);
				}
			}

			private void propagateFinishers(boolean[][] beginings, List<String> thingiesList, Set<String> finishers) {
				Set<String> buff = new HashSet<>(this.finishers);
				this.finishers.addAll(finishers);
				//epsCalculated = true;

				if (buff.equals(this.finishers)) {
					return;
				}

				for (String s : crossings.keySet()) {
					crossings.get(s).propagateFinishers(beginings, thingiesList, finishers);
				}

				calcluateChildEpsFinishers(beginings, thingiesList);
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

					bob.append("(" + crossings.get(s).mainString() + ")");
				}

				bob.append('\n');
				bob.append("  ");
				bob.append('-');
				bob.append('$');
				bob.append("->");
				for (ENKAState s : eCrossings) {
					bob.append("(" + s.mainString() + ")");
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
		}
	}
}
