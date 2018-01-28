import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionBuilder {
	//ovo NE MIJENJAT U PUBLIC, tako da tjera koristiti {@link addInstruction}
	private List<String> rows = new ArrayList<>();
	Map<String, Integer> labels = new HashMap<>();

	public void addInstruction(String s) {
		rows.add(s);
	}

	public List<String> getRows() {
		return rows;
	}

	public void replaceinstruction(int index, String instruction) {
		rows.set(index, instruction);
	}

	public int size() {
		return rows.size();
	}
}
