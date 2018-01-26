package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class ClaferModelLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener arg0) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
	}

	@Override
	public Image getImage(Object arg0) {
		return null;
	}

	@Override
	public String getText(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			return inputFeature.getFeatureName();
		} else if (inputElement instanceof FeatureProperty) {
			FeatureProperty inputProperty = (FeatureProperty) inputElement;
			return inputProperty.getPropertyName();
		}
		return null;
	}

}
