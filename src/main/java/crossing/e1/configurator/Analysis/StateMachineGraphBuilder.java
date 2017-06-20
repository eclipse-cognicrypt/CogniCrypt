package crossing.e1.configurator.Analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashMultimap;

import crypto.rules.CryptSLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.Order;
import de.darmstadt.tu.crossing.cryptSL.SimpleOrder;

public class StateMachineGraphBuilder {

	private final String classname;
	private final Expression head; 
	private final StateMachineGraph result = new StateMachineGraph();
	private int nodeNameCounter = 0;
	
	public StateMachineGraphBuilder(Expression order, String className) {
		head = order;
		classname = className;
		result.addNode(new StateNode("pre_init", true));
	}
	
	public StateMachineGraph buildSMG() {
		process(head, 0, new ArrayList<LeftOver>(), result.getNodes().get(0));
		return result;
	}

	private void process(Expression curLevel, int level, List<LeftOver> leftOvers, StateNode prevNode) {
		Expression left = curLevel.getLeft();
		Expression right = curLevel.getRight();
		String leftElOp = left.getElementop();
		String rightElOp = right.getElementop();
		
		//case 1 = left & right = non-leaf
		//case 2 = left = non-leaf & right = leaf
		//case 3 = left = leaf & right = non-leaf
		//case 4 = left = leaf & right = leaf
		int caseID = 0;

		if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			process(left, level + 1, leftOvers, prevNode);
			
			process(right, level + 1, leftOvers, prevNode);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			process(left, level + 1, leftOvers, prevNode);
			
			prevNode = addRegularEdge(right, prevNode, null);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			if (leftElOp != null) {
				leftPrev = prevNode;
			}
			prevNode = addRegularEdge(left, prevNode, null);
			
			if (leftElOp != null && ("+".equals(leftElOp) ||  "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode);
			}
			
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.add(new LeftOver(prevNode, level,  rightElOp));
			}
			
			process(right, level + 1, leftOvers, prevNode);
			
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode);
			}
			
			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode);
			}
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			if (leftElOp != null) {
				leftPrev = prevNode;
			}
				
			prevNode = addRegularEdge(left, prevNode, null);
			
			if (leftElOp != null && ("+".equals(leftElOp) ||  "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode);
			}
			
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.add(new LeftOver(prevNode, level,  rightElOp));
			}
			
			prevNode = addRegularEdge(right, prevNode, null);
			
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode);
			}
			
			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode);
			}
		}
		if(curLevel.equals(head)) {
			setAcceptingState();
		}

	}

	private void setAcceptingState() {
		List<StateNode> candidates = new ArrayList<StateNode>();
		for (TransitionEdge edge : result.getAllTransitions()) {
			candidates.remove(edge.getLeft());
			candidates.add(edge.getRight());
		}
		
		for (StateNode candidate : candidates) {
			candidate.setAccepting(true);
		}
	}

	private StateNode addRegularEdge(Expression leaf, StateNode prevNode, StateNode nextNode) {
		List<CryptSLMethod> label = new ArrayList<CryptSLMethod>(); //leaf.getOrderEv().get(0));
		if (nextNode == null) {
			nextNode = getNewNode();
		}
		result.addEdge(new TransitionEdge(label, prevNode, nextNode));
		return nextNode;
	}

	private void setLeftOvers(String leftElOp, int level, HashMap<Integer, String> leftOvers) {
//		if (leftElOp)
	}
	
	private StateNode getNewNode() {
		return new StateNode(String.valueOf(nodeNameCounter ++), false, false);
	}

}
