package crossing.e1.configurator.beginer.question;

public class CodeDependency {

	private String option;
	private Boolean value;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Code Dependency [option=" + option + ", value=" + value + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CodeDependency)) {
			return false;
		} else {
			CodeDependency comp = (CodeDependency) obj;
			return comp.getOption().equals(this.getOption()) && comp.getValue().equals(this.getValue());
		}
	}

}
