package de.cognicrypt.staticanalyzer.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.ui.IStartup;

import de.cognicrypt.staticanalyzer.Activator;

/**
 * At startup, this handler registers a listener that will be informed after a build, whenever resources were changed.
 *
 * @author Eric Bodden
 * @author Stefan Krueger
 */
public class StartupHandler implements IStartup {

	private static final AfterBuildListener BUILD_LISTENER = new AfterBuildListener();

	@Override
	public void earlyStartup() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(StartupHandler.BUILD_LISTENER, IResourceChangeEvent.POST_BUILD);
	}

	private static class AfterBuildListener implements IResourceChangeListener {

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			Activator.getDefault().logInfo("ResourcechangeListener has been triggered.");
			try {
				final List<IJavaElement> changedJavaElements = new ArrayList<>();

				event.getDelta().accept(delta -> {
					switch (delta.getKind()) {
						case IResourceDelta.ADDED:
						case IResourceDelta.CHANGED:
							final IResource res = delta.getResource();
							final IJavaElement javaElement = JavaCore.create(res);
							if (javaElement != null) {
								if (javaElement instanceof ICompilationUnit) {
									if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
										changedJavaElements.add(javaElement);
									}
									return false;

								}
							}
					}
					return true;
				});

				if (changedJavaElements.isEmpty()) {
					for (final IResourceDelta ev : event.getDelta().getAffectedChildren()) {
						ev.accept(delta -> {
							switch (delta.getKind()) {
								case IResourceDelta.ADDED:
								case IResourceDelta.CHANGED:
									final IResource res = delta.getResource();
									final IJavaElement javaElement = JavaCore.create(res);
									if (javaElement != null) {
										if (javaElement instanceof JavaProject) {
											if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
												changedJavaElements.add(javaElement);
											}
											return false;
										}
									}
							}
							return true;
						});
					}
				}
				if (changedJavaElements.isEmpty()) {
					Activator.getDefault().logInfo("No changed resource found. Abort.");
					return;
				}

				Activator.getDefault().logInfo("Analysis has been triggered.");

				final AnalysisKickOff ako = new AnalysisKickOff();

				if (ako.setUp(changedJavaElements.get(0))) {
					if (ako.run()) {
						Activator.getDefault().logInfo("Analysis has finished.");
					} else {
						Activator.getDefault().logInfo("Analysis has aborted.");
					}
				} else {
					Activator.getDefault().logInfo("Analysis has been canceled due to erroneous setup.");
				}

			} catch (final CoreException e) {
				Activator.getDefault().logError(e, "Internal error");
			}
		}

	}

}
