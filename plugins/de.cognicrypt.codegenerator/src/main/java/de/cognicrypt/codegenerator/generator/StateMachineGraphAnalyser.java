/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import java.util.ArrayList;
import java.util.List;

import crypto.rules.StateMachineGraph;
import crypto.rules.TransitionEdge;
import de.cognicrypt.utils.CrySLUtils;

/**
 * 
 * This analyser uses depth-first search to find all paths through a given state machine.
 * 
 * This paths are returned as a list.
 * 
 * INFO It is used an adaption of the classical depth-first search algorithm. In contrast to the original algorithm we use consider the already visited transitions and not the
 * visited notes.
 *
 */
public class StateMachineGraphAnalyser {

	protected StateMachineGraph stateMachine;
	protected ArrayList<String> usedTransitions = new ArrayList<String>();
	protected ArrayList<List<TransitionEdge>> allTransitions;

	public StateMachineGraphAnalyser(StateMachineGraph stateMachine) {
		this.stateMachine = stateMachine;
	}

	// FIXME loop handling
	// Current solution: Take every loop once.
	// This solution does not distinguish between the two operates
	// + and * of the crysl language
	public ArrayList<List<TransitionEdge>> getTransitions() {
		allTransitions = new ArrayList<List<TransitionEdge>>();

		//Collection<StateNode> nodes = stateMachine.getNodes();
		List<TransitionEdge> edges = stateMachine.getEdges();
		//Collection<StateNode> acceptingNodes = stateMachine.getAcceptingStates();

		List<TransitionEdge> initialTransitions = stateMachine.getInitialTransitions();
		for (TransitionEdge initialTransition : initialTransitions) {

			List<TransitionEdge> transitions = new ArrayList<TransitionEdge>();

			visitNode(edges, initialTransition, transitions);
		}

		return allTransitions;
	}

	private void visitNode(List<TransitionEdge> edges, TransitionEdge currentTransition, List<TransitionEdge> transitions) {
		List<TransitionEdge> transitionsToAdjacentNodes = new ArrayList<TransitionEdge>();
		List<TransitionEdge> transitionsWithNextTransition = new ArrayList<TransitionEdge>();

		transitionsWithNextTransition.addAll(transitions);
		transitionsWithNextTransition.add(currentTransition);

		usedTransitions.add(currentTransition.toString());

		//usedTransitions.add(currentNode.getName());

		// get all adjacent nodes from the current node
		transitionsToAdjacentNodes.addAll(CrySLUtils.getOutgoingEdges(stateMachine.getAllTransitions(), currentTransition.getRight(), currentTransition.getRight()));
		//		for (TransitionEdge edge : edges) {
		//			
		//			if (edge.getLeft().getName().equals(currentTransition.getRight().getName()))
		//				transitionsToAdjacentNodes.add(edge);
		//		}

		for (TransitionEdge transition : transitionsToAdjacentNodes) {
			//StateNode adjacentNode = transition.getRight();

			if (!usedTransitions.contains(transition.toString())) {//adjacentNode.getName())) {
				visitNode(edges, transition, transitionsWithNextTransition);
			}
		}

		/*
		 * The current node is a leaf. Check if it is an accepting state
		 */
		if (currentTransition.getRight().getAccepting()) {
			allTransitions.add(transitionsWithNextTransition);
		}
	}

}
