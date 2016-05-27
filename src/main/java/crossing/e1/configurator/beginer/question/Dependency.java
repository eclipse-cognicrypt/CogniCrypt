package crossing.e1.configurator.beginer.question;

public class Dependency {
	
	String refClafer;
	String value;
	String operator;
	
	public String getRefClafer() {
		return refClafer;
	}
	public void setRefClafer(String refClafer) {
		this.refClafer = refClafer;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	@Override
	public String toString() {
		return "AnswerDependency [refClafer=" + refClafer + ", value=" + value
				+ ", operator=" + operator + "]";
	}
	
	
	
}
