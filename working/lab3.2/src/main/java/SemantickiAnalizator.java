import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class SemantickiAnalizator {

	public static void main(String[] args) {
		//int x = (int) 2.4e20;
		System.out.println(Integer.parseInt("5471657469788"));
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
