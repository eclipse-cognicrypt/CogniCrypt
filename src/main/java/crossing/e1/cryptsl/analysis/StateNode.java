package crossing.e1.cryptsl.analysis;

import typestate.finiteautomata.State;

public class StateNode implements State {

	private final String name;

	private Boolean init = false;
	private Boolean accepting = false;

	public StateNode(String _name) {
		name = _name;
	}

	public StateNode(String _name, Boolean _init) {
		this(_name);
		init = _init;
	}

	public StateNode(String _name, Boolean _init, Boolean _accepting) {
		this(_name, _init);
		accepting = _accepting;
	}

	public String getName() {
		return name;
	}

	public Boolean getInit() {
		return init;
	}

	public Boolean getAccepting() {
		return accepting;
	}
	
	public String toString() {
		StringBuilder nodeSB = new StringBuilder();
		nodeSB.append("Name: ");
		nodeSB.append(name);
		nodeSB.append("( init: ");
		nodeSB.append(init);
		nodeSB.append("; accept: ");
		nodeSB.append(accepting);
		nodeSB.append(")");
		return nodeSB.toString();
	}

	@Override
	public boolean isErrorState() {
		return !accepting;
	}

	@Override
	public boolean isInitialState() {
		return init;
	}

}
