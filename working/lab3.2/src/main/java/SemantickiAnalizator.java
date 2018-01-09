import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SemantickiAnalizator {

	private static Map<String, Class<? extends NezavrsniZnak>> nezToClass = new HashMap<>();

	static {
		nezToClass.put("<primarni_izraz>", PrimarniIzraz.class);
		nezToClass.put("<postfiks_izraz>", PostfiksIzraz.class);
		nezToClass.put("<lista_argumenata>", ListaArgumenata.class);
		nezToClass.put("<unarni_izraz>", UnarniIzraz.class);
		nezToClass.put("<unarni_operator>", UnarniOperator.class);
		nezToClass.put("<cast_izraz>", CastIzraz.class);
		nezToClass.put("<ime_tipa>", ImeTipa.class);
		nezToClass.put("<specifikator_tipa>", SpecifikatorTipa.class);
		nezToClass.put("<multiplikativni_izraz>", MultiplikativniIzraz.class);
		nezToClass.put("<aditivni_izraz>", AditivniIzraz.class);
		nezToClass.put("<odnosni_izraz>", OdnosniIzraz.class);
		nezToClass.put("<jednakosni_izraz>", JednakosniIzraz.class);
		nezToClass.put("<bin_i_izraz>", BinIIzraz.class);
		nezToClass.put("<bin_xili_izraz>", BinXiliIzraz.class);
		nezToClass.put("<bin_ili_izraz>", BinIliIzraz.class);
		nezToClass.put("<log_i_izraz>", LogIIzraz.class);
		nezToClass.put("<log_ili_izraz>", LogIliIzraz.class);
		nezToClass.put("<izraz_pridruzivanja>", IzrazPridruzivanja.class);
		nezToClass.put("<izraz>", Izraz.class);
		nezToClass.put("<slozena_naredba>", SlozenaNaredba.class);
		nezToClass.put("<lista_naredbi>", ListaNaredbi.class);
		nezToClass.put("<naredba>", Naredba.class);
		nezToClass.put("<izraz_naredba>", IzrazNaredba.class);
		nezToClass.put("<naredba_grananja>", NaredbaGrananja.class);
		nezToClass.put("<naredba_petlje>", NaredbaPetlje.class);
		nezToClass.put("<naredba_skoka>", NaredbaSkoka.class);
		nezToClass.put("<prijevodna_jedinica>", PrijevodnaJedinica.class);
		nezToClass.put("<vanjska_deklaracija>", VanjskaDeklaracija.class);
		nezToClass.put("<definicija_funkcije>", DefinicijaFunkcije.class);
		nezToClass.put("<lista_parametara>", ListaParametara.class);
		nezToClass.put("<deklaracija_parametra>", DeklaracijaParametra.class);
		nezToClass.put("<lista_deklaracija>", ListaDeklaracija.class);
		nezToClass.put("<deklaracija>", Deklaracija.class);
		nezToClass.put("<lista_init_deklaratora>", ListaInitDeklaratora.class);
		nezToClass.put("<init_deklarator>", InitDeklarator.class);
		nezToClass.put("<izravni_deklarator>", IzravniDeklarator.class);
		nezToClass.put("<inicijalizator>", Inicijalizator.class);
		nezToClass.put("<lista_izraza_pridruzivanja>", ListaIzrazaPridruzivanja.class);
	}


	public static void main(String[] args) { //throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
		//int x = (int) 2.4e20;
//		Class<? extends	 Node> classN =  BROJ.class;
//		Node n = new BROJ(null, 42, "1024");
//		System.out.println(NezavrsniZnak.class.isInstance(n.getClass()));
//		System.out.println(n instanceof ZavrsniZnak);
//		System.out.println();
//		//System.out.println(classN.getDeclaredConstructor(Node.class, int.class, String.class).newInstance(null, 34, "24"));
//		System.out.println("#######################");
//
//		Class<? extends test> znj = testBaby.class;
//		znj = testBabyBaby.class;
//
//		Class<? extends test> znjBaby = testBabyBaby.class;
//		Class<? super testBabyBaby> znjBaby2 = test.class;
//
//		List<test> listTest = new ArrayList<test>();
//		listTest.add(new testBaby());
//		testListParam(listTest);
//
//		List<testBaby> listTestBaby = new ArrayList<testBaby>();
//		listTest.add(new testBabyBaby());
//		testListParam(listTestBaby);
//
//		List<? extends test> listQ = new ArrayList<test>();
//		listQ.add(new testBabyBaby());
//
//
//		System.out.println();
		//System.out.println(n.getClass().equals());
//		List<NezavrsniZnak> vbjv = new ArrayList<>();
//		System.out.println(nezToClass.size());
//		System.out.println(nezToClass.get("<primarni_izraz>"));
//
//		try {
//			vbjv.add(nezToClass.get("<primarni_izraz>").getDeclaredConstructor(Node.class).newInstance((Node) null));
//			vbjv.get(0).addChild(nezToClass.get("<lista_izraza_pridruzivanja>").getDeclaredConstructor(Node.class).newInstance((Node) null));
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}

		realMain(System.in, System.out);
	}

	private static void realMain(InputStream in, OutputStream out) {
		Scanner sc = new Scanner(in);
		Stack<NezavrsniZnak> daddy = new Stack<>();

		while (sc.hasNext()) {
			String line = sc.nextLine();
			String node = line.trim();
			int indent = line.indexOf(node);

			while (!daddy.isEmpty() && indent <= daddy.peek().getDepth()) {
				daddy.pop();
			}

			NezavrsniZnak currentDaddy = daddy.isEmpty() ? null : daddy.peek();

			try {
				if (node.startsWith("<") && node.endsWith(">")) {
					NezavrsniZnak buff = nezToClass.get(node).getDeclaredConstructor(Node.class).newInstance((Node) currentDaddy);

					if (currentDaddy != null) {
						currentDaddy.addChild(buff);
					}
					daddy.push(buff);
				} else {
					String[] blocks = node.split(" ", 3);
//					if (blocks.length != 3) {
//						throw new SemAnalysisException(String.format("Expected size of zavsni for: %s, is: %d, got: %d", node,  3, blocks.length));
//					}

					if (currentDaddy != null) {
						currentDaddy.addChild((Node)
							  Class.forName(blocks[0])
								    .getDeclaredConstructor(Node.class, int.class, String.class)
								    .newInstance(daddy.peek(), Integer.parseInt(blocks[1]), blocks[2]));
					}
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new SemAnalysisException(e);
			} catch (NoSuchMethodException e) {
				throw new SemAnalysisException(String.format("No constructor found for:%s", node), e);
			} catch (ClassNotFoundException e) {
				throw new SemAnalysisException(String.format("No class found for:%s", node), e);
			} catch (NumberFormatException e) {
				throw new SemAnalysisException(String.format("Can not parse lineNumber for:%s", node), e);
			}
		}

		NezavrsniZnak head = daddy.pop();
		while(!daddy.isEmpty()) {
			head = daddy.pop();
		}


		NodeVisitor nv = new TreePrintVisitor();
		head.acceptVisitor(nv);

		//TODO provijeritipostoji li main(void -> int) print main
		//TODO provijeriti da svaka funkcij koja je deklarirana mora biti i definirana print funkcija
	}

	private static void testListParam(List<? extends test> listTest) {
		return;
	}
}


abstract class test {

}

class testBaby extends test {
	testBaby(int p) {

	}

}

class testBabyBaby extends testBaby {

	testBabyBaby() {
		super(42);
	}
}