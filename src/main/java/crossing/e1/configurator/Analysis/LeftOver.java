package crossing.e1.configurator.Analysis;

import crypto.rules.StateNode;

public class LeftOver {
	
	public StateNode prev;
	public int levelItShouldBeResolvedAt = 0;
	public String operator;

	public LeftOver(StateNode previous, int level, String op) {
		prev = previous;
		levelItShouldBeResolvedAt = level;
		operator = op;
	}
	
}
