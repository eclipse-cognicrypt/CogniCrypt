package de.cognicrypt.codegenerator.taskintegrator.models;

import java.util.ArrayList;
import java.util.Iterator;

import de.cognicrypt.codegenerator.Constants;

public class ClaferModel implements Iterable<ClaferFeature> {

	private ArrayList<ClaferFeature> claferModel;

	public ClaferModel() {
		claferModel = new ArrayList<>();
	}
	
	public ClaferModel(ArrayList<ClaferFeature> claferModel) {
		this.claferModel = claferModel;
	}
	
	public ArrayList<ClaferFeature> getClaferModel() {
		return claferModel;
	}
	
	public void add(ClaferFeature claferFeature) {
		claferModel.add(claferFeature);
	}
	
	public void remove(ClaferFeature claferFeature) {
		claferModel.remove(claferFeature);
	}

	@Override
	public Iterator iterator() {
		return claferModel.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// use the iterator to serialize all children
		for (ClaferFeature cfrFeature : this) {
			sb.append(cfrFeature);
		}
		
		return sb.toString();
	}

	/**
	 * Implement missing inheritance or property types in this model with respect to a given feature
	 * 
	 * @param refFeature
	 *        reference feature to sift for unimplemented features
	 * @return a model containing only those features that have been added
	 */
	public ClaferModel implementMissingFeatures(ClaferFeature refFeature) {
		ClaferModel addedFeatures = new ClaferModel();
		
		// find missing inherited feature
		if (!refFeature.getFeatureInheritance().isEmpty()) {
			boolean parentFound = false;
			for (ClaferFeature cfrFeature : claferModel) {
				if (cfrFeature.getFeatureName().equals(refFeature.getFeatureInheritance())) {
					parentFound = true;
					break;
				}
			}

			// remember missing inherited feature
			if (!parentFound) {
				ClaferFeature parentFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, refFeature.getFeatureInheritance(), "");
				addedFeatures.add(parentFeature);
			}
		}

		// find missing property types
		for (FeatureProperty fp : refFeature.getfeatureProperties()) {
			boolean propertyTypeFound = false;
			for (ClaferFeature cfrFeature : claferModel) {
				if (cfrFeature.getFeatureName().equals(fp.getPropertyType())) {
					propertyTypeFound = true;
					break;
				}
			}

			// remember missing property types			
			if (!fp.getPropertyType().isEmpty() && !propertyTypeFound) {
				ClaferFeature propertyTypeFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, fp.getPropertyType(), "");
				addedFeatures.add(propertyTypeFeature);
			}
		}
		
		// add all missing features
		for (ClaferFeature cfrFeature : addedFeatures) {
			claferModel.add(cfrFeature);
		}
		
		return addedFeatures;
	}
}
