package de.cognicrypt.order.editor.statemachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.darmstadt.tu.crossing.crySL.CrySLFactory;
import de.darmstadt.tu.crossing.crySL.Expression;
import de.darmstadt.tu.crossing.crySL.Order;
import de.darmstadt.tu.crossing.crySL.SimpleOrder;
import de.darmstadt.tu.crossing.crySL.impl.ExpressionImpl;

public final class StateMachineGraph{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<StateNode> nodes;
	private final List<TransitionEdge> edges;

	private final HashMap<String,Expression> nodemaps;
	public StateMachineGraph() {
		nodes = new HashSet<StateNode>();
		edges = new ArrayList<TransitionEdge>();
		nodemaps = new HashMap<String,Expression>();
	}

	public Boolean addEdge(TransitionEdge edge) {
		final StateNode right = edge.getRight();
		final StateNode left = edge.getLeft();
		if(edge.getLeft().getName().equals("init")) {
			//remove initial node (Order) and put an expression instead of it 
			Expression newElement = CrySLFactory.eINSTANCE.createExpression();
			nodemaps.put("init", newElement);
			
		}
		if (!(nodes.parallelStream().anyMatch(e -> e.equals(left)) || nodes.parallelStream().anyMatch(e -> e.equals(right)))) {
			return false;
		}
		if (edges.contains(edge)) {
			return false;
		}
		edges.add(edge);
		return true;
	}

	public void wrapUpCreation() {
		getAcceptingStates().parallelStream().forEach(e -> {
			e.setHopsToAccepting(0);
			updateHops(e);
		});
	}

	private void updateHops(StateNode node) {
		int newPath = node.getHopsToAccepting() + 1;
		getAllTransitions().parallelStream().forEach(e -> {
			StateNode theNewRight = e.getLeft();
			if (e.getRight().equals(node) && theNewRight.getHopsToAccepting() > newPath) {
				theNewRight.setHopsToAccepting(newPath);
				updateHops(theNewRight);
			}
		});
	}

	public Boolean addNode(StateNode node, Expression expression) {
		for (StateNode innerNode : nodes) {
			if (innerNode.getName().equals(node.getName())) {
				return false;
			}
		}
		nodes.add(node);
		nodemaps.put(node.getName(), expression);
		return true;
	}
	
	public String toString() {
		StringBuilder graphSB = new StringBuilder();
		for (StateNode node : nodes) {
			graphSB.append(node.toString());
			graphSB.append(System.lineSeparator());
		}

		for (TransitionEdge te : edges) {
			graphSB.append(te.toString());
			graphSB.append(System.lineSeparator());
		}

		return graphSB.toString();
	}

	public Set<StateNode> getNodes() {
		return nodes;
	}
	
	public HashMap<String,Expression> getNodeMap() {
		return nodemaps;
	}

	public List<TransitionEdge> getEdges() {
		return edges;
	}

	public TransitionEdge getInitialTransition() {
		return edges.get(0);
	}

	public Collection<StateNode> getAcceptingStates() {
		Collection<StateNode> accNodes = new ArrayList<StateNode>();
		for (StateNode node : nodes) {
			if (node.getAccepting()) {
				accNodes.add(node);
			}
		}

		return accNodes;
	}

	public Collection<TransitionEdge> getAllTransitions() {
		return getEdges();
	}

}

