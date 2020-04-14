package de.cognicrypt.staticanalyzer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;

public interface IListener {
	void listen1(IProject ip);
	IResource listen2(String name, IProject ip) throws JavaModelException, ClassNotFoundException;
}
