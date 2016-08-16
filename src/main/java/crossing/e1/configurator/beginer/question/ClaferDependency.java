package crossing.e1.configurator.beginer.question;

public class ClaferDependency {

	private String algorithm;
	private String operand;
	private String value;
	private String operator;

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
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

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public String toString() {
		return "AnswerDependency [algorithm=" + algorithm + ", operand=" + operand + ", value=" + value + ", operator=" + operator + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClaferDependency)) {
			return false;
		} else {
			ClaferDependency cmp = (ClaferDependency) obj;
			return cmp.getAlgorithm().equals(this.getAlgorithm()) && cmp.getOperand().equals(this.getOperand()) && cmp.getOperator().equals(this.getOperator()) && cmp.getValue()
				.equals(this.getValue());
		}
	}
}