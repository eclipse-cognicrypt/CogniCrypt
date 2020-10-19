/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class ClaferModelLabelProvider implements ILabelProvider {

	@Override
	public void addListener(final ILabelProviderListener arg0) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object arg0, final String arg1) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener arg0) {}

	@Override
	public Image getImage(final Object arg0) {
		return null;
	}

	@Override
	public String getText(final Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			final ClaferFeature inputFeature = (ClaferFeature) inputElement;
			return inputFeature.toString(false);
		} else if (inputElement instanceof ClaferProperty) {
			final ClaferProperty inputProperty = (ClaferProperty) inputElement;
			return inputProperty.toString();
		}
		return null;
	}

}
