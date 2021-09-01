package de.cognicrypt.order.editor.statemachine;

import java.util.List;

import de.darmstadt.tu.crossing.crySL.Event;

public class TransitionEdge implements StateTransition<StateNode>, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private StateNode left = null;
	private StateNode right = null;
	private Event method = null;

	// see the implementation in CryptoAnalysis TransitionEdge
	// may be it is required to keep it as in CryptoAnalysis.
	public TransitionEdge(Event _method, StateNode _left, StateNode _right) {
		left = _left;
		right = _right;
		method = _method;
	}

	public StateNode getLeft() {
		return left;
	}

	public StateNode getRight() {
		return right;
	}

	public Event getLabel() {
		return method;
	}

	public String toString() {
		StringBuilder edgeSB = new StringBuilder();
		edgeSB.append("Left: ");
		edgeSB.append(this.left.getName());
		edgeSB.append(" ====");
		edgeSB.append(method);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransitionEdge other = (TransitionEdge) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
}
