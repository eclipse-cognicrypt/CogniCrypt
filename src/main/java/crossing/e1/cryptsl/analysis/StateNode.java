package crossing.e1.cryptsl.analysis;

public class StateNode  {

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
	
	public void setAccepting(Boolean _accepting) {
		accepting = _accepting;
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

	public boolean isErrorState() {
		return !accepting;
	}

	public boolean isInitialState() {
		return init;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StateNode)) {
			return false;
		}
		StateNode cmp = (StateNode) obj;
		
		return cmp.getName().equals(this.name) && cmp.getAccepting().equals(this.accepting) && cmp.getInit().equals(this.init);
	}

	
}
