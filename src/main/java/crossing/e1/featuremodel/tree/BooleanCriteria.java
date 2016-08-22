package crossing.e1.featuremodel.tree;

/**
 * Represents a boolean criteria in a decision tree. This node has only to children and the name of the criteria.
 * 
 * @author Michael Reif
 *
 */
public class BooleanCriteria extends TreeNode {

	private final TreeNode trueBranch;
	private final TreeNode falseBranch;
	private final String criteria;

	public BooleanCriteria(final String criteria, final TreeNode trueBranch, final TreeNode falseBranch) {
		this.criteria = criteria;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	public TreeNode getBranch(final boolean bool) {
		return (bool) ? this.trueBranch : this.falseBranch;
	}

	public String getCriteria() {
		return this.criteria;
	}

	public TreeNode getFalseBranch() {
		return this.falseBranch;
	}

	public TreeNode getTrueBranch() {
		return this.trueBranch;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	/*
	 * Only Used for debugging puposes. (non-Javadoc)
	 * @see crossing.e1.featuremodel.tree.TreeNode#toString()
	 */
	@Override
	public String toString() {
		return "{\n\t" + "criteria: " + this.criteria + "\n\ttrueBranch: " + this.trueBranch.toString() + "\n\tfalseBranch: " + this.falseBranch.toString() + "}";
	}

}
