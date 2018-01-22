package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class FeaturePropertiesContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			ArrayList<FeatureProperty> filteredProperties = (ArrayList<FeatureProperty>) inputFeature.getFeatureProperties().clone();
			filteredProperties.removeIf(x -> x.getPropertyName().isEmpty());

			return filteredProperties.toArray();
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ClaferModel) {
			ClaferModel inputModel = (ClaferModel) inputElement;
			ClaferModel filteredModel = inputModel.clone();

			filteredModel.getClaferModel().removeIf(x -> x.getFeatureName().isEmpty() || !x.hasProperties());
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
			return inputFeature.hasProperties();
		}
		return false;
	}

}
