

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Util {

	@SuppressWarnings("unchecked")
	public static <T> T readSerFile(String filename) throws IOException, ClassNotFoundException {
		T data;
		try (FileInputStream fileIn = new FileInputStream("analizator/" + filename);
			 ObjectInputStream in = new ObjectInputStream(fileIn)
		) {
			data = (T) in.readObject();
			in.close();
			fileIn.close();
		}
		return data;
	}
}
