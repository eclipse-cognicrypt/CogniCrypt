package de.cognicrypt.order.editor.statemachine;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.impl.EventImpl;

public class CrySLEvent extends EventImpl {

	
	public CrySLEvent() {
		super();
	}
}
