package crossing.e1.featuremodel.tree;

public class TreeModel {

	private TreeNode beginner;
	private TreeNode expert;

	public String toString() {
		return beginner.toString() + " \n\n\n " + expert.toString();
	}
}
