package de.cognicrypt.staticanalyzer.kotlin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;

import de.cognicrypt.staticanalyzer.IListener;
import de.cognicrypt.staticanalyzer.kotlin.utilities.KotlinUtils;

public class KotlinListener implements IListener {

	@Override
	public void listen1(IProject ip) {
		KotlinUtils.compileKotlinFiles(ip);
	}

	@Override
	public IResource listen2(String name, IProject ip) throws JavaModelException, ClassNotFoundException {
		return KotlinUtils.findKotlinClassByName(name, ip);
	}
}
