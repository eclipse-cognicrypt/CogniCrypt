/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.reader;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.utils.Utils;
import de.darmstadt.tu.crossing.crySL.Expression;
import de.darmstadt.tu.crossing.crySL.Order;
import de.darmstadt.tu.crossing.crySL.SimpleOrder;

public class StateMachineGraphBuilder {

	private final Expression head;
	private final StateMachineGraph result = new StateMachineGraph();
	private int nodeNameCounter = 0;

	public StateMachineGraphBuilder(final Expression order) {
		this.head = order;
		this.result.addNode(new StateNode("-1", true, true));
	}

	private StateNode addRegularEdge(final Expression leaf, final StateNode prevNode, final StateNode nextNode) {
		return addRegularEdge(leaf, prevNode, nextNode, false);
	}

	private StateNode addRegularEdge(final Expression leaf, final StateNode prevNode, final StateNode nextNode, final Boolean isStillAccepting) {
		final List<CrySLMethod> label = CrySLReaderUtils.resolveAggregateToMethodeNames(leaf.getOrderEv().get(0));
		return addRegularEdge(label, prevNode, nextNode, isStillAccepting);
	}

	private StateNode addRegularEdge(final List<CrySLMethod> label, final StateNode prevNode, StateNode nextNode, final Boolean isStillAccepting) {
		if (nextNode == null) {
			nextNode = getNewNode();
			this.result.addNode(nextNode);
		}
		if (!isStillAccepting) {
			prevNode.setAccepting(false);
		}
		this.result.addEdge(new TransitionEdge(label, prevNode, nextNode));
		return nextNode;
	}

	public StateMachineGraph buildSMG() {
		StateNode initialNode = null;
		for (final StateNode n : this.result.getNodes()) {
			initialNode = n;
		}
		if (this.head != null) {
			processHead(this.head, 0, HashMultimap.create(), initialNode);
		} else {
			this.result.addEdge(new TransitionEdge(new ArrayList<CrySLMethod>(), initialNode, initialNode));
		}
		return this.result;
	}

	private StateNode getNewNode() {
		return new StateNode(String.valueOf(this.nodeNameCounter++), false, true);
	}

	private StateNode isGeneric(final String el, final int level, final Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		for (final Entry<String, StateNode> entry : leftOvers.get(level)) {
			if (el.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	private StateNode isOr(final int level, final Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		return isGeneric("|", level, leftOvers);
	}

	private StateNode isQM(final int level, final Multimap<Integer, Entry<String, StateNode>> leftOvers) {
		return isGeneric("?", level, leftOvers);
	}

	private StateNode process(final Expression curLevel, final int level, final Multimap<Integer, Map.Entry<String, StateNode>> leftOvers, StateNode prevNode) {
		final Expression left = curLevel.getLeft();
		final Expression right = curLevel.getRight();
		final String leftElOp = (left != null) ? left.getElementop() : "";
		final String rightElOp = (right != null) ? right.getElementop() : "";
		final String orderOp = curLevel.getOrderop();
		// case 1 = left & right = non-leaf
		// case 2 = left = non-leaf & right = leaf
		// case 3 = left = leaf & right = non-leaf
		// case 4 = left = leaf & right = leaf

		if (left == null && right == null) {
			addRegularEdge(curLevel, prevNode, null);
		} else if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			final StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);

			final StateNode rightPrev = prevNode;
			StateNode returnToNode = null;
			if ("|".equals(orderOp)) {
				leftOvers.put(level + 1, new HashMap.SimpleEntry<>(orderOp, prevNode));
				prevNode = process(right, level + 1, leftOvers, leftPrev);
			} else if ((returnToNode = isOr(level, leftOvers)) != null) {
				prevNode = process(right, level + 1, leftOvers, returnToNode);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final List<TransitionEdge> outgoingEdges = new ArrayList<TransitionEdge>();
				if ("|".equals(orderOp)) {
					final List<TransitionEdge> tmpOutgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), leftPrev, null);
					for (final TransitionEdge outgoingEdge : tmpOutgoingEdges) {
						if (isReachable(outgoingEdge.to(), prevNode, new ArrayList<StateNode>())) {
							outgoingEdges.addAll(Utils.getOutgoingEdges(result.getAllTransitions(), outgoingEdge.to(), prevNode));
						}
					}
					for (final TransitionEdge outgoingEdge : outgoingEdges) {
						if (isReachable(prevNode, outgoingEdge.from(), new ArrayList<StateNode>())) {
							addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.from(), true);
						}
					}

				} else {
					outgoingEdges.addAll(Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, prevNode));
					for (final TransitionEdge outgoingEdge : outgoingEdges) {
						addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
					}
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}

		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			Optional<Entry<String, StateNode>> optionalOrLevel = leftOvers.get(level).stream().filter(e -> "|".equals(e.getKey())).findFirst();
			if (optionalOrLevel.isPresent()) {
				Entry<String, StateNode> orLevel = optionalOrLevel.get();
				StateNode p = orLevel.getValue();
				List<TransitionEdge> orEdges = Utils.getOutgoingEdges(result.getAllTransitions(), prevNode, null);
				if (!orEdges.isEmpty()) {
					Optional<TransitionEdge> edge = orEdges.stream().filter(e -> e.getRight().equals(p)).findFirst();
					if (edge.isPresent() && edge.get().getLabel().equals(CrySLReaderUtils.resolveAggregateToMethodeNames(getLeftMostChild(left).getOrderEv().get(0)))) {
						leftOvers.put(level + 1, orLevel);
					}
				}
			}
			prevNode = process(left, level + 1, leftOvers, prevNode);

			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<>(rightElOp, prevNode));
				prevNode = addRegularEdge(right, prevNode, null, true);
			} else {
				prevNode = addRegularEdge(right, prevNode, null);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode, true);
			}

			Collection<Entry<String, StateNode>> skippers = leftOvers.get(level);
			for (Entry<String, StateNode> skipper : skippers) {
				if ("*".equals(skipper.getKey()) || "?".equals(skipper.getKey())) {
					final StateNode endNode = prevNode;
					Optional<TransitionEdge> edge = result.getAllTransitions().parallelStream().filter(e -> e.to().equals(endNode)).findFirst();
					if (edge.isPresent()) {
						for (final TransitionEdge outgoingEdge : Utils.getOutgoingEdges(result.getAllTransitions(), skipper.getValue(), null)) {
							addRegularEdge(edge.get().getLabel(), outgoingEdge.to(), endNode, true);
						}
					}
				}
			}

		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = prevNode;
			prevNode = addRegularEdge(left, prevNode, null);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<>(rightElOp, prevNode));
			}
			final StateNode rightPrev = prevNode;
			if ("|".equals(orderOp)) {
				leftOvers.put(level + 1, new HashMap.SimpleEntry<>(orderOp, prevNode));
				prevNode = process(right, level + 1, leftOvers, leftPrev);
				
				if ((rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) &&
						leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
					final StateNode finPrevNode = prevNode;
					addRegularEdge(result.getAllTransitions().parallelStream().filter(e -> leftPrev.equals(e.from()) && rightPrev.equals(e.to())).findFirst().get().getLabel(), prevNode, rightPrev, true);
					addRegularEdge(result.getAllTransitions().parallelStream().filter(e -> leftPrev.equals(e.from()) && finPrevNode.equals(e.to())).findFirst().get().getLabel(), rightPrev, prevNode, true);
					
					List<StateNode> collect = result.getAllTransitions().parallelStream().filter(e -> e.from().equals(leftPrev) && !leftPrev.equals(e.to()) && !rightPrev.equals(e.to()) && !finPrevNode.equals(e.to())).map(e -> e.to()).collect(Collectors.toList());
					collect.stream().forEach(e -> {
						TransitionEdge edge = fetchEdge(leftPrev, e);
						if (fetchEdge(e, rightPrev) != null) {
							addRegularEdge(edge.getLabel(), finPrevNode, e, true);
						} else if (fetchEdge(e, finPrevNode) != null) {
							addRegularEdge(edge.getLabel(), rightPrev, e, true);
						}
						
					});
				}
				
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final List<TransitionEdge> outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, prevNode);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				prevNode = addRegularEdge(right, leftPrev, prevNode, true);
			}

		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;

			boolean sameName = false;
			List<TransitionEdge> orEdges = Utils.getOutgoingEdges(result.getAllTransitions(), prevNode, null);
			Optional<Entry<String, StateNode>> alternative = leftOvers.get(level).stream().filter(e -> "|".equals(e.getKey())).findFirst();
			if (alternative.isPresent()) {
				Entry<String, StateNode> orLevel = alternative.get();
				StateNode p = orLevel.getValue();
				if (!orEdges.isEmpty()) {
					Optional<TransitionEdge> edge = orEdges.stream().filter(e -> e.getRight().equals(p)).findFirst();
					if (edge.isPresent() && edge.get().getLabel().equals(CrySLReaderUtils.resolveAggregateToMethodeNames(getLeftMostChild(left).getOrderEv().get(0)))) {
						sameName = true;
						prevNode = p;
						leftOvers.remove(level, orLevel);
					}
				}
			}
			if (!sameName) {
				prevNode = addRegularEdge(left, prevNode, null);
			}

			StateNode returnToNode = isOr(level, leftOvers);
			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				leftOvers.put(level - 1, new HashMap.SimpleEntry<>(rightElOp, prevNode));
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

			if (sameName) {
				setAcceptingState(alternative.get().getValue());
			}

		}
		leftOvers.removeAll(level);
		return prevNode;
	}

	private boolean isReachable(final StateNode startNode, final StateNode endNode, final List<StateNode> skippable) {
		for (final TransitionEdge edge : Utils.getOutgoingEdges(result.getAllTransitions(), startNode, startNode)) {
			if (edge.to().equals(endNode)) {
				return true;
			} else if (!skippable.contains(edge.to())) {
				skippable.add(edge.to());
				return isReachable(edge.to(), endNode, skippable);
			}
		}
		return false;
	}

	private void processHead(final Expression curLevel, final int level, final Multimap<Integer, Map.Entry<String, StateNode>> leftOvers, StateNode prevNode) {
		final Expression left = curLevel.getLeft();
		final Expression right = curLevel.getRight();
		final String leftElOp = (left != null) ? left.getElementop() : "";
		final String rightElOp = (right != null) ? right.getElementop() : "";
		final String orderOp = curLevel.getOrderop();

		if (left == null && right == null) {
			final String elOp = curLevel.getElementop();
			if ("*".equals(elOp) || "?".equals(elOp)) {
				prevNode = addRegularEdge(curLevel, prevNode, null, true);
			} else {
				addRegularEdge(curLevel, prevNode, null);
			}
			if ("*".equals(elOp) || "+".equals(elOp)) {
				addRegularEdge(curLevel, prevNode, prevNode, true);
			}
		} else if ((left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			final StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);
			final StateNode rightPrev = prevNode;
			if ("|".equals(orderOp)) {
				prevNode = process(right, level + 1, leftOvers, leftPrev);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}
			for (Entry<String, StateNode> a : leftOvers.get(level).stream().filter(e -> "?".equals(e.getKey())).collect(Collectors.toList())) {
				if ("*".equals(rightElOp) || "?".equals(rightElOp)) {
					setAcceptingState(a.getValue());
					for (TransitionEdge l : Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, null)) {
						addRegularEdge(l.getLabel(), a.getValue(), l.getRight(), true);
					}
				}
			}

			if ("*".equals(rightElOp) || "?".equals(rightElOp)) {
				setAcceptingState(rightPrev);
			}
			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final String orderop = right.getOrderop();
				List<TransitionEdge> outgoingEdges = null;
				if (orderop != null && "|".equals(orderop)) {
					outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, null);
				} else {
					outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, prevNode);
				}
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		} else if ((left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			final StateNode leftPrev = prevNode;
			prevNode = process(left, level + 1, leftOvers, prevNode);
			final StateNode rightPrev = prevNode;
			if ("|".equals(orderOp)) {
				prevNode = addRegularEdge(right, leftPrev, prevNode);
			} else {
				prevNode = addRegularEdge(right, prevNode, null);
			}
			for (Entry<String, StateNode> a : leftOvers.get(level).stream().filter(e -> "*".equals(e.getKey())).collect(Collectors.toList())) {
				addRegularEdge(right, a.getValue(), prevNode, true);
			}
			boolean isOptional = "*".equals(rightElOp) || "?".equals(rightElOp);
			if (isOptional) {
				setAcceptingState(rightPrev);
				if ("?".equals(left.getRight().getElementop()) || "*".equals(left.getRight().getElementop())) {
					final List<TransitionEdge> outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), leftPrev, null);
					for (final TransitionEdge outgoingEdge : outgoingEdges) {
						setAcceptingState(outgoingEdge.to());
					}
				}
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final List<TransitionEdge> outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, null);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}
			if (leftOvers.containsKey(level)) {
				for (Entry<String, StateNode> entry : leftOvers.get(level).stream().filter(e -> "*".equals(e.getKey()) || "?".equals(e.getKey())).collect(Collectors.toList())) {
					addRegularEdge(right, entry.getValue(), prevNode, isOptional);
				}
			}
			StateNode returnToNode = null;
			if ((returnToNode = isQM(level, leftOvers)) != null) {
				addRegularEdge(right, returnToNode, prevNode, true);
			}
		} else if (!(left instanceof Order || left instanceof SimpleOrder) && (right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			prevNode = addRegularEdge(left, prevNode, null);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode);
			}

			final StateNode rightPrev = prevNode;
			StateNode returnToNode = null;
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				setAcceptingState(rightPrev);
			}
			if ("|".equals(orderOp)) {
				setAcceptingState(prevNode);
				SimpleEntry<String, StateNode> entry = new HashMap.SimpleEntry<>(orderOp, prevNode);
				leftOvers.put(level + 1, entry);
				prevNode = process(right, level + 1, leftOvers, leftPrev);

			} else if ((returnToNode = isOr(level, leftOvers)) != null) {
				prevNode = process(right, level + 1, leftOvers, returnToNode);
			} else {
				prevNode = process(right, level + 1, leftOvers, prevNode);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				final List<TransitionEdge> outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, null);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					addRegularEdge(outgoingEdge.getLabel(), prevNode, outgoingEdge.to(), true);
				}
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				setAcceptingState(leftPrev);
				final List<TransitionEdge> outgoingEdges = Utils.getOutgoingEdges(result.getAllTransitions(), rightPrev, null);
				for (final TransitionEdge outgoingEdge : outgoingEdges) {
					setAcceptingState(outgoingEdge.to());
					addRegularEdge(outgoingEdge.getLabel(), leftPrev, outgoingEdge.to(), true);
				}
			}
			if (rightElOp != null && ("?".equals(rightElOp) || "*".equals(rightElOp))) {
				setAcceptingState(rightPrev);
				if (leftOvers.containsKey(level)) {
					leftOvers.get(level).stream().filter(e -> "*".equals(e.getKey()) || "?".equals(e.getKey())).forEach(e -> setAcceptingState(e.getValue()));
				}
			}

		} else if (!(left instanceof Order || left instanceof SimpleOrder) && !(right instanceof Order || right instanceof SimpleOrder)) {
			StateNode leftPrev = null;
			leftPrev = prevNode;
			StateNode returnToNode = isOr(level, leftOvers);

			final boolean leftOptional = "?".equals(leftElOp) || "*".equals(leftElOp);
			prevNode = addRegularEdge(left, prevNode, null, leftOptional);

			if (leftElOp != null && ("+".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(left, prevNode, prevNode, true);
			}

			final boolean rightoptional = "?".equals(rightElOp) || "*".equals(rightElOp);
			if (returnToNode != null || "|".equals(orderOp)) {
				if ("|".equals(orderOp)) {
					addRegularEdge(right, leftPrev, prevNode, rightoptional);
				}
				if ((returnToNode = isOr(level, leftOvers)) != null) {
					prevNode = addRegularEdge(right, prevNode, returnToNode, rightoptional);
				}
			} else {
				prevNode = addRegularEdge(right, prevNode, null, rightoptional);
			}

			if (rightElOp != null && ("+".equals(rightElOp) || "*".equals(rightElOp))) {
				addRegularEdge(right, prevNode, prevNode, true);
			}

			if (leftElOp != null && ("?".equals(leftElOp) || "*".equals(leftElOp))) {
				addRegularEdge(right, leftPrev, prevNode, true);
			}
		}
	}

	private void setAcceptingState(final StateNode prevNode) {
		prevNode.setAccepting(true);
	}

	private Expression getLeftMostChild(Expression ex) {
		if (ex.getOrderEv().size() > 0) {
			return ex;
		}
		if (ex.getLeft() != null) {
			return getLeftMostChild(ex.getLeft());
		}
		return null;
	}
	
	private TransitionEdge fetchEdge(StateNode start, StateNode goal) {
		Optional<TransitionEdge> edgeOpt = result.getAllTransitions().parallelStream().filter(e -> e.from().equals(start) && e.to().equals(goal)).findFirst();
		if (edgeOpt.isPresent()) {
			return edgeOpt.get();
		} else {
			return null;
		}
	}

}
