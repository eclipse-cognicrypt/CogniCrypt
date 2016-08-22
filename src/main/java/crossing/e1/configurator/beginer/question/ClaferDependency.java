package crossing.e1.configurator.beginer.question;

public class ClaferDependency {

	private String algorithm;
	private String operand;
	private String value;
	private String operator;

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ClaferDependency)) {
			return false;
		} else {
			final ClaferDependency cmp = (ClaferDependency) obj;
			return cmp.getAlgorithm().equals(getAlgorithm()) && cmp.getOperand().equals(getOperand()) && cmp.getOperator().equals(getOperator()) && cmp.getValue()
				.equals(getValue());
		}
	}

	public String getAlgorithm() {
		return this.algorithm;
	}

	public String getOperand() {
		return this.operand;
	}

	public String getOperator() {
		return this.operator;
	}

	public String getValue() {
		return this.value;
	}

	public void setAlgorithm(final String algorithm) {
		this.algorithm = algorithm;
	}

	public void setOperand(final String operand) {
		this.operand = operand;
	}

	public void setOperator(final String operator) {
		this.operator = operator;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AnswerDependency [algorithm=" + this.algorithm + ", operand=" + this.operand + ", value=" + this.value + ", operator=" + this.operator + "]";
	}
}