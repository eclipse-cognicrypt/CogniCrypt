/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.controllers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;

public class ClaferModelLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener arg0) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {}

	@Override
	public Image getImage(Object arg0) {
		return null;
	}

	@Override
	public String getText(Object inputElement) {
		if (inputElement instanceof ClaferFeature) {
			ClaferFeature inputFeature = (ClaferFeature) inputElement;
			return inputFeature.toString(false);
		} else if (inputElement instanceof ClaferProperty) {
			ClaferProperty inputProperty = (ClaferProperty) inputElement;
			return inputProperty.toString();
		}
		return null;
	}

}
