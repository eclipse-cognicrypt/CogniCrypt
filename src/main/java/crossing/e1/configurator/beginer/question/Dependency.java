package crossing.e1.configurator.beginer.question;

public class Dependency {

	String algorithm;
	String operand;
	String value;
	String operator;

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

}
