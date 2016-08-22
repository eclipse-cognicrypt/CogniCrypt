package crossing.e1.featuremodel.tree;

public class SimpleDecision extends TreeNode {

	protected DataStructure decision;

	public SimpleDecision(final DataStructure decision) {
		super();
		this.decision = decision;
	}

	public DataStructure getDecision() {
		return this.decision;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public String toString() {
		return "{" + this.decision.toString() + "}";
	}
}
