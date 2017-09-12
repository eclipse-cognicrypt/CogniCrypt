/**
 * 
 */
package crossing.e1.taskintegrator.models;

import crossing.e1.configurator.Constants.FeatureConstraintRelationship;

/**
 * @author rajiv
 *
 */
public class FeatureConstraint {
	private String constraintName;
	private String constraintValue;
	private FeatureConstraintRelationship constraintRelationship;
	/**
	 * @param constraintName
	 * @param constraintValue
	 * @param constraintRelationship
	 */
	public FeatureConstraint(String constraintName, String constraintValue, FeatureConstraintRelationship constraintRelationship) {
		super();
		this.setConstraintName(constraintName);
		this.setConstraintValue(constraintValue);
		this.setConstraintRelationship(constraintRelationship);
	}
	/**
	 * @return the constraintName
	 */
	public String getConstraintName() {
		return constraintName;
	}
	/**
	 * @param constraintName the constraintName to set
	 */
	private void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}
	/**
	 * @return the constraintValue
	 */
	public String getConstraintValue() {
		return constraintValue;
	}
	/**
	 * @param constraintValue the constraintValue to set
	 */
	private void setConstraintValue(String constraintValue) {
		this.constraintValue = constraintValue;
	}
	/**
	 * @return the constraintRelationship
	 */
	public FeatureConstraintRelationship getConstraintRelationship() {
		return constraintRelationship;
	}
	/**
	 * @param constraintRelationship the constraintRelationship to set
	 */
	private void setConstraintRelationship(FeatureConstraintRelationship constraintRelationship) {
		this.constraintRelationship = constraintRelationship;
	}
}
