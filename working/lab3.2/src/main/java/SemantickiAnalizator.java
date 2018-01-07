import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class SemantickiAnalizator {

	//parsiranje: ako <shit> onda mapa -> java reflectionApi, ako SHIT onda java reflection direct

	public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		//int x = (int) 2.4e20;
		ZavrsniZnak n = new BROJ(null, 42, "1024");
		System.out.println(n);
		System.out.println(n.getClass().getDeclaredConstructor(Node.class, int.class, String.class).newInstance(null, 34, "24"));
		//System.out.println(n.getClass().equals());

		//realMain(System.in, System.out);
	}

	private static void realMain(InputStream in, OutputStream out) {
		Scanner sc = new Scanner(in);

		while (sc.hasNext()) {
			String line = sc.nextLine();
			//TODO dodaj tam gdje treba
		}



		//TODO obici stablo

		//TODO provijeritipostoji li main(void -> int) print main
		//TODO provijeriti da svaka funkcij koja je deklarirana mora biti i definirana print funkcija
	}
}
