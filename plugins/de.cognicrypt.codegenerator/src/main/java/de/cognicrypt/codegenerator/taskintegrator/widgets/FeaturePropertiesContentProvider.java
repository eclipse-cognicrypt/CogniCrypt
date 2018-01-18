package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;

public class FeaturePropertiesContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			return inputFeature.getFeatureProperties().toArray();
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ClaferModel) {
			ClaferModel inputModel = (ClaferModel) inputElement;
			ClaferModel withPropertiesOnly = inputModel.clone();

			withPropertiesOnly.getClaferModel().removeIf(x -> !x.hasProperties());
			return withPropertiesOnly.getClaferModel().toArray();
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
