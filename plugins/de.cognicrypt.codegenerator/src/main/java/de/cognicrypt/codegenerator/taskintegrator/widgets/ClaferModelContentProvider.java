package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class ClaferModelContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof String) {
			String stringArg0 = (String) arg0;
			if (stringArg0.equals("abstract A")) {
				return new String[] { "A1", "A2" };
			}
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ArrayContentProvider.getInstance().getElements(inputElement);
	}

	@Override
	public Object getParent(Object arg0) {
		// return null if the parent cannot be computed
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		if (arg0 instanceof String) {
			String stringArg0 = (String) arg0;
			if (stringArg0.equals("abstract A")) {
				return true;
			}
		}
		return false;
	}

}
