package de.cognicrypt.staticanalyzer.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IStartup;

import de.cognicrypt.staticanalyzer.Activator;


/**
 * At startup, this handler registers a listener that will be informed after a build, whenever resources were changed.
 */
public class StartupHandler implements IStartup {

	private static final AfterBuildListener BUILD_LISTENER = new AfterBuildListener();

	public void earlyStartup() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(BUILD_LISTENER, IResourceChangeEvent.POST_BUILD);
	}
	
	private static class AfterBuildListener implements IResourceChangeListener {

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				final Set<IJavaElement> changedJavaElements = new HashSet<IJavaElement>();
				event.getDelta().accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						switch(delta.getKind()) {
						case IResourceDelta.ADDED:
						case IResourceDelta.CHANGED:
							IResource res = delta.getResource();
							IJavaElement javaElement = JavaCore.create(res);
							if(javaElement!=null) {
//								if(res instanceof IProject) {
//TODO allow for filtering of Android projects in plugin config
//									if(!AnalysisDispatcher.isAndroidProject((IProject) res)) {
//										don't care about non-Android projects
//										return false;
//									}
//								}
								if(javaElement instanceof ICompilationUnit) {
									//only care if file contents changed
									if((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
										changedJavaElements.add(javaElement);
									}
									return false;
								}
							}
						}					
						return true;
					}
				});
				if(changedJavaElements.isEmpty()) return;
				
//				IJavaElement[] changeArray = changedJavaElements.toArray(new IJavaElement[0]);
//				AnalysisDispatcher.searchAndAnalyze(changeArray);
				
				AnalysisKickOff ako = new AnalysisKickOff();
				
				ako.setUp();
				ako.run();
				ako.cleanUp();
			} catch (CoreException e) {
				Activator.getDefault().logError(e, "Internal error");
			}
		}

	}


}
