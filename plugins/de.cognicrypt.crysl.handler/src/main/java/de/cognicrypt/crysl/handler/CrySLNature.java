package de.cognicrypt.crysl.handler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class CrySLNature implements IProjectNature {

	public static final String NATURE_ID = "de.cognicrypt.crysl.handler.cryslnature";

	private IProject project;

	@Override
	public void configure() throws CoreException {}

	@Override
	public void deconfigure() throws CoreException {}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
