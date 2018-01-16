import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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


	public static void main(String[] args) throws IOException { //throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
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
//		Map<String, String> x = new HashMap<>();
//		x.merge("42", "42", (o, n) -> {
//			throw new IllegalStateException();
//		});
//		Map<String, String> x2 = new HashMap<>();

		//x.forEach(x2::put);
//		const int i = 34;

//		int i = 3;
//		Class<?> v = int.class;
//		System.out.println(v == int.class);
//		System.out.println(v.equals(int.class));
//
//		Stack<String> bljufljy = new Stack<>();
//		bljufljy.push("pimpke");
//		bljufljy.push("krastavac");
//		bljufljy.push("vnwj");
//
//		System.out.println(bljufljy.get(0));
//		System.out.println(bljufljy.get(bljufljy.size() - 1));
//		System.out.println(bljufljy.peek());

//		System.out.printf("%f", Math.pow(54, 54));
//		int[] nbjr = new int[42];
//		System.out.println(int[].class);
//		System.out.println(nbjr.getClass().isArray());
//		int mno = 42;
//		System.out.println(int.class);
//		System.out.println(Integer.parseInt("0x42".substring(2), 16));

		//######################prije uploada ovo odkomentirati!!!!!!!!!!!!!!!!!!!!!!!!!################################
		String s = realMain(System.in, System.out);//Files.newInputStream(Paths.get("../../../shit/input/1.in"))
		if (!s.isEmpty()) {
			System.out.println(s);
		}

//		for (int n = 1; n <= 1231; n++) {
//			String s = realMain(Files.newInputStream(Paths.get("../../../shit/input/" + n + ".in")), System.out);
//			List<String> ls = Files.readAllLines(Paths.get("../../../shit/ocekivani_output/" + n + ".out"));
//			if ((s.isEmpty() && ls.isEmpty()) || (!ls.isEmpty() && s.equals(ls.get(0)))) {
//				System.out.println("../../../shit/input/" + n + ".out OK");
//				continue;
//			}
//			System.out.println(">>>>in: ../../../shit/input/" + n + ".in");
//			System.out.println(s);
//			System.out.println("Should be:");
//			System.out.println(ls.isEmpty() ? "" : ls.get(0));
//			System.out.println("#-------------------------------------------------------#");
//			//break;
//		}

//		FileVisitor<Path> fv = new FileVisitor<Path>() {
//			@Override
//			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//				return FileVisitResult.CONTINUE;
//			}
//
//			@Override
//			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//				if (!file.toString().endsWith(".in")) {
//					return FileVisitResult.CONTINUE;
//				}
//				realMain(
//					  Files.newInputStream(file),
//					  Files.newOutputStream(Paths.get("tvoj_output/"
//						    + file.getFileName().toString().substring(0, file.getFileName().toString().indexOf(".")) + ".out")));
//				return FileVisitResult.CONTINUE;
//			}
//
//			@Override
//			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//				return FileVisitResult.CONTINUE;
//			}
//
//			@Override
//			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//				return FileVisitResult.CONTINUE;
//			}
//		};

		//Files.walkFileTree(Paths.get("input"), fv);
	}

	private static String realMain(InputStream in, OutputStream out) {
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


		//NodeVisitor nv = new TreePrintVisitor();
		//head.acceptVisitor(nv);

		SemAnalizatorVisitor bljub = new SemAnalizatorVisitor();
		try {
			head.acceptVisitor(bljub);
		} catch (SemAnalysisException e) {
			//System.out.println(e.getMessage());
			return e.getMessage();
		} catch (Exception e) {
			NodeVisitor nv = new TreePrintVisitor();
			head.acceptVisitor(nv);
			e.printStackTrace();
			System.exit(1);
		}

		try {
			bljub.findMain();
		} catch (SemAnalysisException e) {
			//System.out.println(e.getMessage());
			return e.getMessage();
		}

		try {
			bljub.areAllDeclaredFunctionsDefined();
		} catch (SemAnalysisException e) {
			//System.out.println(e.getMessage());
			return e.getMessage();
		}

		return "";
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