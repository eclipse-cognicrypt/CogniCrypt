package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class ClaferModelContentProvider implements ITreeContentProvider {
	
	private Predicate<? super ClaferFeature> featureFilter;
	private Predicate<? super FeatureProperty> propertyFilter;
	
	/**
	 * create a {@link ClaferModelContentProvider} that yields all of the content's elements
	 */
	public ClaferModelContentProvider() {
		this(null, null);
	}
	
	/**
	 * create a {@link ClaferModelContentProvider} with filters attached
	 * 
	 * @param featureFilter
	 *        display {@link ClaferFeature}s that this predicate applies to (returns true for)
	 * @param propertyFilter
	 *        display {@link FeatureProperty}s that this predicate applies to (returns true for)
	 */
	public ClaferModelContentProvider(Predicate<? super ClaferFeature> featureFilter, Predicate<? super FeatureProperty> propertyFilter) {
		this.featureFilter = featureFilter;
		this.propertyFilter = propertyFilter;
	}

	@Override
	public Object[] getChildren(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			ArrayList<FeatureProperty> filteredProperties = (ArrayList<FeatureProperty>) inputFeature.getFeatureProperties().clone();
			
			if (propertyFilter != null) {
				filteredProperties.removeIf(propertyFilter.negate());
			}

			return filteredProperties.toArray();
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ClaferModel) {
			ClaferModel inputModel = (ClaferModel) inputElement;
			ClaferModel filteredModel = inputModel.clone();

			if (featureFilter != null) {
				filteredModel.getClaferModel().removeIf(featureFilter.negate());
			}
			return filteredModel.getClaferModel().toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object arg0) {
		// return null if the parent cannot be computed
		return null;
	}

	@Override
	public boolean hasChildren(Object inputElement) {
		if (inputElement instanceof ClaferModel) {
			ClaferModel inputModel = (ClaferModel) inputElement;
			return !inputModel.getClaferModel().isEmpty();
		} else if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			if (propertyFilter != null) {
				return inputFeature.hasPropertiesSatisfying(propertyFilter);
			} else {
				return inputFeature.hasProperties();
			}
		}
		return false;
	}

}
