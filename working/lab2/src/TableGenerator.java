import java.util.*;

public class TableGenerator {


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
						//System.out.println(first.mainString());
					}
				}
			}
		}

		//TODO add eps crossings
		for (ENKA.ENKAState e : result.states) {
			String buff;
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

		//TODO {a, b, c} and stuff
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
						if (e.right[i] == null && e.right[i+1].equals(enkaS.left)) {
							beginings[thingiesList.indexOf(enkaS.left)][thingiesList.indexOf(enkaS.right[i+2])] = true;
							break;
						}
					}
				}
			}
		}

		findBeginings(beginings, thingiesList);

		/*
		System.out.printf("%5s,","");
		for (int i = 0; i < beginings.length; i++) {
			System.out.printf("%5s,",thingiesList.get(i));
		}
		System.out.println();
		for (int i = 0; i < beginings.length; i++) {
			System.out.printf("%5s,",thingiesList.get(i));
			for (int j = 0; j < beginings.length; j++) {
				System.out.printf("%5d,",beginings[i][j] ? 1: 0);
			}
			System.out.println();
		}*/

		first.propagateFinishers(Arrays.asList("~"));
		first.calcluateChildEpsFinishers(beginings, thingiesList);


		//TODO to DKA

		//TODO to consumer<String> 2D table

		ENKA.ENKAState buff = result.new ENKAState("<S'>", lines.get(0).split(" "));

		//podrazumijeva se da je prvo stanje prvo stanji i prijelaz u tekstualnoj datoteci
		buff.eCrossings = Arrays.asList(first);

		return result;
	}


	private static void findBeginings(boolean[][] field, List<String> elements) {
		boolean[] done = new boolean[field.length];
		boolean finish = false;

		while (!finish) {
			mainFor: for (int i = 0; i < field.length; i++) {
				if (!(elements.get(i).startsWith("<") && elements.get(i).endsWith(">"))) {
					field[i][i] = true;
				} else {
					List<String> buff = new ArrayList<>();
					for (int j = 0; j < field[i].length; j++) {
						if (!field[i][j]) {
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
				//System.out.println(b);
				//System.out.println(elements.get());
				finish &= b;
			}

			//System.out.println(finish);
		}
	}




	private static class DKA {

		List<DKAState> states;

		private class DKAState {
			private String name;
			private Map<Character, DKAState> crossings;
		}
	}



	private static class ENKA {

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
			private String left;
			private String[] right;
			private Map<String, ENKAState> crossings = new HashMap<>();
			private List<ENKAState> eCrossings = new ArrayList<>(4);
			private List<String> finishers = new ArrayList<>(4);
			private boolean epsCalculated = false;

			public ENKAState(String left, String[] right) {
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
				if (epsCalculated) {
					return;
				}
				epsCalculated = true;

				propagateFinishers(finishers);

				List<String> buffFinishers = new ArrayList<>();
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
						if (finishers.contains("~") && e.left.equals(thingiesList.get(index))) {
							if (e.right.length == 1) {
								buffFinishers.add("~");
							}
						}
					}
				}

				for (ENKAState e : eCrossings) {
					e.finishers = buffFinishers;
					e.calcluateChildEpsFinishers(beginings, thingiesList);
				}
			}

			private void propagateFinishers(List<String> finishers) {
				this.finishers = finishers;

				for (String s : crossings.keySet()) {
					crossings.get(s).propagateFinishers(finishers);
				}
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
