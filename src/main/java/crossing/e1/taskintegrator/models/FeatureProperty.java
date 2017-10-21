/**
 * 
 */
package crossing.e1.taskintegrator.models;


/**
 * @author rajiv
 *
 */
public class FeatureProperty{
	private String propertyName;
	private String propertyType;
	/**
	 * @param propertyName
	 * @param propertyType
	 */
	public FeatureProperty(String propertyName, String propertyType) {
		super();
		this.setPropertyName(propertyName);
		this.setPropertyType(propertyType);
	}
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	/**
	 * @return the propertyType
	 */
	public String getPropertyType() {
		return propertyType;
	}
	/**
	 * @param propertyType the propertyType to set
	 */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
}
