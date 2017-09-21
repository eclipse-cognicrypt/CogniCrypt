/**
 * 
 */
package crossing.e1.taskintegrator.models;

import java.util.ArrayList;

import crossing.e1.configurator.Constants.FeatureType;

/**
 * @author rajiv
 *
 */
public class ClaferFeature {
	private FeatureType featureType;
	private String featureName;
	private FeatureProperty featureInheritsFromForAbstract; // single key value pair of property. Clafer allows the inheritances to be of this type.
	private FeatureConstraint featureInheritsFromForConcrete; // when defining a concrete feature.
	private ArrayList<FeatureProperty> featureProperties; // <String name, String value>. The names in this collection cannot be repeated.
	private ArrayList<String> featureConstraints; // each constraint will be generated as a "valid" string in the constraint generator pop up.
	/**
	 * @param featureType
	 * @param featureName
	 * @param featureInheritsFromForAbstract
	 * @param featureInheritsFromForConcrete
	 * 
	 * 
	 */
	public ClaferFeature(FeatureType featureType, String featureName, FeatureProperty featureInheritsFromForAbstract, FeatureConstraint featureInheritsFromForConcrete) {
		super();
		this.setFeatureType(featureType);
		this.setFeatureName(featureName);
		this.setFeatureInheritsFromForAbstract(featureInheritsFromForAbstract);
		this.setFeatureInheritsFromForConcrete(featureInheritsFromForConcrete);
		this.featureProperties = new ArrayList<FeatureProperty>();
		//this.setProperties(properties);
		this.featureConstraints=new ArrayList<String>();
		//this.setFeatureConstraints(featureConstraints);
	}
	/**
	 * @return the featureType
	 */
	public FeatureType getFeatureType() {
		return featureType;
	}
	/**
	 * @param featureType the featureType to set
	 */
	private void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}
	/**
	 * @return the featureName
	 */
	public String getFeatureName() {
		return featureName;
	}
	/**
	 * @param featureName the featureName to set
	 */
	private void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	/**
	 * @return the featureInheritsFromForAbstract
	 */
	public FeatureProperty getFeatureInheritsFromForAbstract() {
		return featureInheritsFromForAbstract;
	}
	/**
	 * @param featureInheritsFromForAbstract the featureInheritsFromForAbstract to set
	 */
	private void setFeatureInheritsFromForAbstract(FeatureProperty featureInheritsFromForAbstract) {
		this.featureInheritsFromForAbstract = featureInheritsFromForAbstract;
	}
	/**
	 * @return the featureInheritsFromForConcrete
	 */
	public FeatureConstraint getFeatureInheritsFromForConcrete() {
		return featureInheritsFromForConcrete;
	}
	/**
	 * @param featureInheritsFromForConcrete the featureInheritsFromForConcrete to set
	 */
	private void setFeatureInheritsFromForConcrete(FeatureConstraint featureInheritsFromForConcrete) {
		this.featureInheritsFromForConcrete = featureInheritsFromForConcrete;
	}
	/**
	 * @return the properties
	 */
	public ArrayList<FeatureProperty> getfeatureProperties() {
		return featureProperties;
	}
	/**
	 * @param properties the properties to set
	 */
	/*private void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
	/**
	 * @return the featureConstraints
	 */
	public ArrayList<String> getFeatureConstraints() {
		return featureConstraints;
	}
	/**
	 * @param featureConstraints the featureConstraints to set
	 */
	/*private void setFeatureConstraints(ArrayList<String> featureConstraints) {
		this.featureConstraints = featureConstraints;
	}*/
}
