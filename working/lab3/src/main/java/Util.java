import java.util.*;

/*
    Koristio samo za generiranje Cons enuma, nije nam vise potrebno
    al nek ostane za svaki slucaj u slucaju da sam nes fulao tu negdje
 */
public class Util {

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        Set<String> set = new HashSet<>();

        while (sc.hasNext()){
            String line = sc.nextLine();
            String[] array = line.split(" ");

            for (String s : array) {
                set.add(s.trim());
            }
        }

        List<String> list = new ArrayList<>();
        for (String s : set) {
            list.add(s);
        }

        Collections.sort(list);

        for (String s : list) {
            String temp = s.toUpperCase();
            System.out.println(temp + "(\"" + s + "\"),");
        }

        sc.close();
    }
}
