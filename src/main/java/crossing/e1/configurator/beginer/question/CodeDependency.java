package crossing.e1.configurator.beginer.question;

public class CodeDependency {

	private String option;
	private Boolean value;

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CodeDependency)) {
			return false;
		} else {
			final CodeDependency comp = (CodeDependency) obj;
			return comp.getOption().equals(getOption()) && comp.getValue().equals(getValue());
		}
	}

	public String getOption() {
		return this.option;
	}

	public Boolean getValue() {
		return this.value;
	}

	public void setOption(final String option) {
		this.option = option;
	}

	public void setValue(final Boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Code Dependency [option=" + this.option + ", value=" + this.value + "]";
	}

}
