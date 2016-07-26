package crossing.e1.featuremodel.tree;

public class TreeModel {

	TreeNode beginner;
	TreeNode expert;
	
	public String toString(){
		return beginner.toString() + " \n\n\n " + expert.toString();
	}
}
