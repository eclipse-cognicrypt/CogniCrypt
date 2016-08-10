package crossing.e1.featuremodel.tree;

public class SimpleDecision extends TreeNode {

	DataStructure decision;

	public SimpleDecision(DataStructure decision) {
		super();
		this.decision = decision;
	}

	public String toString() {
		return "{" + decision.toString() + "}";
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public DataStructure getDecision() {
		return decision;
	}
}
