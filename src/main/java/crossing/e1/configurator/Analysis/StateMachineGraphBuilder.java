package crossing.e1.configurator.Analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.cryptSL.Expression;
import de.darmstadt.tu.crossing.cryptSL.Order;
import de.darmstadt.tu.crossing.cryptSL.SimpleOrder;

public class StateMachineGraphBuilder {

	private final Expression head; 
	private final StateMachineGraph result = new StateMachineGraph();
	private int nodeNameCounter = 0;
	
	public StateMachineGraphBuilder(Expression order) {
		head = order;
		result.addNode(new StateNode("-1", true));
	}
	
	public StateMachineGraph buildSMG() {
		processHead(head, 0, HashMultimap.create(), result.getNodes().get(0));
		return result;
	}
	
	private void processHead(Expression curLevel, int level, Multimap<Integer, Map.Entry<String, StateNode>> leftOvers, StateNode prevNode) {
		Expression left = curLevel.getLeft();
		Expression right = curLevel.getRight();
		String leftElOp = (left != null) ? left.getElementop() : "";
		String rightElOp = (right != null) ? right.getElementop() : "";
//		String orderOp = curLevel.getOrderop();
		
		if (left == null && right == null) {
			addRegularEdge(curLevel, prevNode, null);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);
			StateNode rightPrev = prevNode;
			prevNode = process(right, level + 1, leftOvers, prevNode);
			
			if ("*".equals(rightElOp) || "?".equals(rightElOp)) {
				setAcceptingState(rightPrev);
			} 
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				TransitionEdge outgoingEdge = getOutgoingEdge(rightPrev, prevNode);
				addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
			}
			
			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			prevNode = process(left, level + 1, leftOvers, prevNode);
			prevNode = addRegularEdge(right, prevNode, null);
			StateNode returnToNode = null;
			if ((returnToNode = isQM(level, leftOvers)) != null) {
				addRegularEdge(right, returnToNode, prevNode, true);
			}	
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			process(curLevel, level, leftOvers, prevNode);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			process(curLevel, level, leftOvers, prevNode);
		}
	}

	private TransitionEdge getOutgoingEdge(StateNode rightPrev, StateNode notTo) {
		for (TransitionEdge comp : result.getAllTransitions()) {
			if (comp.getLeft().equals(rightPrev) && !(comp.getRight().equals(rightPrev) || comp.getRight().equals(notTo))) {
				return comp;
			}
		}
		return null;
	}

	private StateNode process(Expression curLevel, int level, Multimap<Integer, Map.Entry<String, StateNode>> leftOvers, StateNode prevNode) {
		Expression left = curLevel.getLeft();
		Expression right = curLevel.getRight();
		String leftElOp = (left != null) ? left.getElementop() : "";
		String rightElOp = (right != null) ? right.getElementop() : "";
		String orderOp = curLevel.getOrderop();
		//case 1 = left & right = non-leaf
		//case 2 = left = non-leaf & right = leaf
		//case 3 = left = leaf & right = non-leaf
		//case 4 = left = leaf & right = leaf

		if (left == null && right == null) {
			addRegularEdge(curLevel, prevNode, null);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			prevNode = process(left, level + 1, leftOvers, prevNode);			
			prevNode = process(right, level + 1, leftOvers, prevNode);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			prevNode = process(left, level + 1, leftOvers, prevNode);
			
			prevNode = addRegularEdge(right, prevNode, null);
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
				prevNode = addRegularEdge(left, prevNode, null);
			
			if (leftElOp != null && ("+".equals(leftElOp) ||  "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}
			
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<String,StateNode>(rightElOp, prevNode));
			}
			StateNode rightPrev = prevNode;
			StateNode returnToNode = null;
			if ("|".equals(orderOp)) {
				leftOvers.put(level + 1, new HashMap.SimpleEntry<String,StateNode>(orderOp, prevNode));
				prevNode = process(right, level + 1, leftOvers, leftPrev);
			} else if ((returnToNode = isOr(level, leftOvers)) != null) {
				prevNode = process(right, level + 1, leftOvers, returnToNode);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}
			
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				TransitionEdge outgoingEdge = getOutgoingEdge(rightPrev, prevNode);
				addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
			}
			
			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
			
			
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			StateNode returnToNode = isOr(level, leftOvers);
	
			prevNode = addRegularEdge(left, prevNode, null);
			
			if (leftElOp != null && ("+".equals(leftElOp) ||  "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}
			
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<String,StateNode>(rightElOp, prevNode));
			}
			if (returnToNode != null || "|".equals(orderOp)) {
				if ("|".equals(orderOp)) {
					addRegularEdge(right, leftPrev, prevNode);
				}
				if ((returnToNode = isOr(level, leftOvers)) != null) {
					prevNode = addRegularEdge(right, prevNode, returnToNode);
				} 
			} else {
				prevNode = addRegularEdge(right, prevNode, null);
			}
			
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode, true);
			}
			
			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		}
		leftOvers.removeAll(level);
		return prevNode;
	}

	private StateNode isOr(int level, Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		return isGeneric("|", level, leftOvers);
	}
	
	private StateNode isQM(int level, Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		return isGeneric("?", level, leftOvers);
	}
	
	private StateNode isGeneric(String el, int level, Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		for (Entry<String, StateNode> entry : leftOvers.get(level)) {
			if (el.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	private void setAcceptingState(StateNode prevNode) {
		prevNode.setAccepting(true);
	}
	
	private StateNode addRegularEdge(Expression leaf, StateNode prevNode, StateNode nextNode) {
		return addRegularEdge(leaf, prevNode, nextNode, false);
	}
	
	private StateNode addRegularEdge(Expression leaf, StateNode prevNode, StateNode nextNode, Boolean isStillAccepting) {
		List<CryptSLMethod> label = CryptSLReaderUtils.resolveAggregateToMethodeNames(leaf.getOrderEv().get(0));
		return addRegularEdge(label, prevNode, nextNode, isStillAccepting);
	}
	
	private StateNode addRegularEdge(List<CryptSLMethod> label, StateNode prevNode, StateNode nextNode, Boolean isStillAccepting) {
		if (nextNode == null) {
			nextNode = getNewNode();
			result.addNode(nextNode);
		}
		if (!isStillAccepting) {
			prevNode.setAccepting(false);
		}
		result.addEdge(new TransitionEdge(label, prevNode, nextNode));
		return nextNode;
	}

	
	private StateNode getNewNode() {
		return new StateNode(String.valueOf(nodeNameCounter ++), false, true);
	}

}
