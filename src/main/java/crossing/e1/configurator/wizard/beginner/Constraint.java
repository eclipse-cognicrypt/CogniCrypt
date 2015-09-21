package crossing.e1.configurator.wizard.beginner;

public class Constraint {
	
	public static enum Operator{GREATER_THAN, EQUAL, LESS_THAN, GTE, LTE}
	private Operator operator;
	private String property;
	private Object value;
	
	public Constraint(String poperty, Operator operator, Object value){
		this.property = poperty;
		this.operator = operator;
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	

}
