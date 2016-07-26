package crossing.e1.featuremodel.tree;

public class NCriteria extends TreeNode{
	
	private TreeNode[] children;
	private String criteria;
	
	public NCriteria(String criteria, TreeNode[] children){
		this.criteria = criteria;
		this.children = children;
	}
	
	public String toString(){
		
		String childNodes = "[";
		for(TreeNode tn: children){
			childNodes += "{\n\t" + tn.toString() +"},";
		}
		
		return "{" 
				+ "\tcriteria: " + criteria +
				"\n\tcildren: " + childNodes +"]}";
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	public TreeNode[] getChildren(){
		return children;
	}
	
	
	public String getCriteria(){
		return criteria;
	}
}
