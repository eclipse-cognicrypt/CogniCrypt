package crossing.e1.cryptsl.analysis;

import typestate.interfaces.Transition;

public class TransitionEdge implements Transition {

	private StateNode left = null;
	private StateNode right = null;
	private String label = "";

	public TransitionEdge(String _label, StateNode _left, StateNode _right) {
		left = _left;
		right = _right;
		label = _label;
	}

	public StateNode getLeft() {
		return left;
	}

	public StateNode getRight() {
		return right;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		StringBuilder edgeSB = new StringBuilder();
		edgeSB.append("Left: ");
		edgeSB.append(this.left.getName());
		edgeSB.append(" ====");
		edgeSB.append(label);
		edgeSB.append("====> Right:");
		edgeSB.append(this.right.getName());
		return edgeSB.toString();
	}

	public StateNode from() {
		return left;
	}

	public StateNode to() {
		return right;
	}

	public String label() {
		return label;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TransitionEdge)) {
			return false;
		}
		TransitionEdge cmp = (TransitionEdge) obj;
		
		return cmp.getLeft().equals(this.left) && cmp.getRight().equals(this.right) && cmp.getLabel().equals(this.label);
	}
	
	
	
}
