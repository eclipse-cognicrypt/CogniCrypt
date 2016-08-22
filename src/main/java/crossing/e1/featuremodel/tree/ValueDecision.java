package crossing.e1.featuremodel.tree;

public class ValueDecision extends SimpleDecision {

	private final String value;

	public ValueDecision(final String value, final DataStructure decision) {
		super(decision);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "{" + "\tvalue: " + this.value + "\n\tdecision: " + this.decision.toString() + "}";
	}
}
