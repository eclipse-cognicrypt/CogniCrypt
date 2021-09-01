package de.cognicrypt.order.editor.statemachine;

import java.util.List;

import de.darmstadt.tu.crossing.crySL.Event;

public interface StateTransition<State>{
	State from();
	State to();
	Event getLabel();
}
