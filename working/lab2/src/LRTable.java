package lab2;

import java.io.Serializable;
import java.util.Map;

public class LRTable implements Serializable {

	public static final long serialVersionUID = 42L;

	public Map<String, Integer> columns;
    public String[][] neo;

    public LRTable(Map<String, Integer> columns, String[][] neo) {
        this.columns = columns;
        this.neo = neo;
    }

    public String findAction(int rowIndex, String columnName) {
        if (neo[rowIndex][columns.get(columnName)] != null) {
            return neo[rowIndex][columns.get(columnName)];
        } else {
            return null;
        }
    }
}
