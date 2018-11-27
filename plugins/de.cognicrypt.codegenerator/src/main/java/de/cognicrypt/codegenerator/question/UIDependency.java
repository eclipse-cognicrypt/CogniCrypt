package de.cognicrypt.codegenerator.question;

/**
 * 
 * This class provides an additional data container for answers that allows to give additional information
 * to the UI in order to provide a UI that fits a specific questions. To keep it as generic as possible,
 * we use Strings to encode additional values.
 * 
 * For example, it can be used to for checkbox groups where some values should be handled exclusively.
 * 
 * @author Michael Reif
 */
public class UIDependency {
	
	private String option;
	private String value;
	
	public String getOption() {
		return option;
	}
	
	public void setOption(String option) {
		this.option = option;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((option == null) ? 0 : option.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UIDependency other = (UIDependency) obj;
		if (option == null) {
			if (other.option != null)
				return false;
		} else if (!option.equals(other.option))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "UIDependency [option=" + option + ", value=" + value + "]";
	}
}
