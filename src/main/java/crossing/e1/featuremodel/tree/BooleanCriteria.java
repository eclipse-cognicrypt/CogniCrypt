package crossing.e1.featuremodel.tree;

/**
 * Represents a boolean criteria in a decision tree. This node has only to children and the name of the criteria.
 * 
 * @author Michael Reif
 *
 */
public class BooleanCriteria extends TreeNode {

	private TreeNode trueBranch;
	private TreeNode falseBranch;
	private String criteria;

	public BooleanCriteria(String criteria, TreeNode trueBranch, TreeNode falseBranch) {
		this.criteria = criteria;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	/*
	 * Only Used for debugging puposes. (non-Javadoc)
	 * @see crossing.e1.featuremodel.tree.TreeNode#toString()
	 */
	public String toString() {
		return "{\n\t" + "criteria: " + criteria + "\n\ttrueBranch: " + trueBranch.toString() + "\n\tfalseBranch: " + falseBranch.toString() + "}";
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	public TreeNode getTrueBranch() {
		return trueBranch;
	}

	public TreeNode getFalseBranch() {
		return falseBranch;
	}

	public TreeNode getBranch(boolean bool) {
		return (bool) ? trueBranch : falseBranch;
	}

	public String getCriteria() {
		return criteria;
	}

}
