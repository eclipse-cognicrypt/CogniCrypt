package crossing.e1.featuremodel.tree;

public class TreeModel {

	private TreeNode beginner;
	private TreeNode expert;

	@Override
	public String toString() {
		return this.beginner.toString() + " \n\n\n " + this.expert.toString();
	}
}
