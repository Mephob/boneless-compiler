import java.util.*;

    /*
    Malo me zeza jedino ovdje kaj se dogodi ako roknemo vise identifikatora istog naziva u vise scopeova
    To je valjda sredeno tako kaj koristim mapu listi...

    Osim tog valjda bi trebalo bit solidno.
     */


public class TablicaZnakova {
    //fuck that idemo stogom aylmao
    private Stack<String> stek;
    private HashMap<String, List<ASTNode>> listMap;
    private String scopeLimiter;

    public TablicaZnakova() {
        stek = new Stack<>();
        scopeLimiter = "Nani?";
        listMap = new HashMap<>();
    }

    public void openScope() {
        stek.push(scopeLimiter);
    }

    public void closeScope() {
        while (!stek.isEmpty()) {
            String key = stek.pop();
            if (key.equals(scopeLimiter)) {
                break;
            }
            //nije jos kraj trenutnog scopea
            //skini za oodgovarajuci kljuc zadnji element liste
            List<ASTNode> list = listMap.get(key);
            if (list.isEmpty()) {
                break;
            }
            list.remove(list.size() - 1);
        }
    }

    /*
    Vrati 1 ako je uspjelo, 0 ako vec postoji
    Initnode pokazuje na cvor gdje je IDN deklariran
     */
    public void addElem(String s, ASTNode initNode) {
        stek.push(s);
        List<ASTNode> lista = listMap.get(s);
        if (lista == null) {
            lista = new ArrayList<>();
        }
        lista.add(initNode);
        listMap.put(s, lista);
    }

    /*
    Checks whether at least one IDN is present.
     */
    public boolean hasElem(String s) {
        Stack<String> tempboi = new Stack<>();
        boolean isPresent = false;

        while (!stek.isEmpty()) {
            String temp = stek.pop();
            tempboi.push(temp);

            if (temp.equals(s)) {
                isPresent = true;
                break;
            }
        }
        while (!tempboi.isEmpty()) {
            stek.push(tempboi.pop());
        }
        return isPresent;
    }

    public boolean hasElemLocally(String s) {
        Stack<String> tempboi = new Stack<>();
        boolean isPresent = false;

        while (!stek.isEmpty()) {
            String temp = stek.pop();
            tempboi.push(temp);

            if (temp.equals(s)) {
                isPresent = true;
                break;
            } else if (temp.equals(scopeLimiter)) {
                break;
            }
        }
        while (!tempboi.isEmpty()) {
            stek.push(tempboi.pop());
        }
        return isPresent;
    }

    /*
    returns null if item does not exist
    else returns IDN declaration closest in scope
     */
    public ASTNode getIDN(String s) {
        String nearest = findNearestDeclaration(s);

        List<ASTNode> temp = listMap.get(nearest);
        return temp.get(temp.size() - 1);
    }

    private String findNearestDeclaration(String s) {
        Stack<String> tempboi = new Stack<>();
        String closest = new String();

        while (!stek.isEmpty()) {
            String temp = stek.pop();
            tempboi.push(temp);

            if (temp.equals(s)) {
                closest = temp;
                break;
            }
        }
        while (!tempboi.isEmpty()) {
            stek.push(tempboi.pop());
        }
        return closest;
    }

    public List<ASTNode> getNodeList(String s) {
        return listMap.get(s);
    }

    public Set<String> getKeys() {
        return listMap.keySet();
    }
}
