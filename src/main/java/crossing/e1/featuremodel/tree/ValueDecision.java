package crossing.e1.featuremodel.tree;

public class ValueDecision extends SimpleDecision {

	private final String value;

	public ValueDecision(String value, DataStructure decision) {
		super(decision);
		this.value = value;
	}

	public String toString() {
		return "{" + "\tvalue: " + value + "\n\tdecision: " + decision.toString() + "}";
	}

	public String getValue() {
		return value;
	}
}
