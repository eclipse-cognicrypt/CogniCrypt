package crossing.e1.featuremodel.tree;

public class NCriteria extends TreeNode {

	private final TreeNode[] children;
	private final String criteria;

	public NCriteria(final String criteria, final TreeNode[] children) {
		this.criteria = criteria;
		this.children = children;
	}

	public TreeNode[] getChildren() {
		return this.children;
	}

	public String getCriteria() {
		return this.criteria;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public String toString() {

		String childNodes = "[";
		for (final TreeNode tn : this.children) {
			childNodes += "{\n\t" + tn.toString() + "},";
		}

		return "{" + "\tcriteria: " + this.criteria + "\n\tcildren: " + childNodes + "]}";
	}
}
