package de.cognicrypt.staticanalyzer.kotlin.utilities;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jetbrains.kotlin.core.compiler.KotlinCompiler;
import org.jetbrains.kotlin.core.model.KotlinNature;

public class KotlinUtils {
	
	private static final String KOTLIN_FILE_EXTENSION = ".kt";

	public static void compileKotlinFiles(IProject ip) {
		IJavaProject javaProject = JavaCore.create(ip);
		
		if(Platform.getBundle("org.jetbrains.kotlin.core") != null) {
 			if(KotlinNature.hasKotlinNature(ip)) {
 				try {
 					if(ip.hasNature("org.eclipse.m2e.core.maven2Nature")) {
 						if(KotlinUtils.verifyKotlinDependency(javaProject)) {
 							
 							final Job builder = new Job("Custom Builder") {
								
								@Override
								protected IStatus run(IProgressMonitor monitor) {
									try {
										ip.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
									} catch (CoreException e) {
										de.cognicrypt.staticanalyzer.Activator.getDefault().logError(e);
									}
									return Status.OK_STATUS;
								}
							};
							builder.setPriority(Job.LONG);
							builder.schedule();
 							
 							while(builder.getState() != Job.NONE) {
								Thread.sleep(3);
							}
 			
 							KotlinUtils.waitForBuildAndRefreshJobs();
 							
							if(KotlinCompiler.compileKotlinFiles(javaProject).compiledCorrectly()) {
								de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Finished compiling kotlin files.");
							} else {
								de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Cannot compile some kotlin files due to errors. Static analysis skipped them.");
							}
 						}
 						else {
 							de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Cannot compile kotlin files without kotlin-stdlib dependency.");
 							de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Static analysis skipped all kotlin files.");
 						}
 					}
 					else {
 						if(KotlinCompiler.compileKotlinFiles(javaProject).compiledCorrectly()) {
 							de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Finished compiling kotlin files.");
 						} else {
 							de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Cannot compile some kotlin files due to errors. Static analysis skipped them.");
						}
 					}	
 				} catch (CoreException | OperationCanceledException | InterruptedException e) {
 					de.cognicrypt.staticanalyzer.Activator.getDefault().logError(e);
 				}
 			}
 		}
	}
	
	public static IResource findKotlinClassByName(final String className, final IProject currentProject)
			throws JavaModelException, ClassNotFoundException {
		if(Platform.getBundle("org.jetbrains.kotlin.core") != null) {
			// This part is required because Eclipse JDT doesn’t provide any mapping of kotlin light classes to its source code
			// As a result the above IPackageFragment.getCompilationUnits() doesn't return any kotlin .class files
			if(KotlinNature.hasKotlinNature(currentProject)) {

				// computing corresponding source filename, since in kotlin .class filename is changed
				// Eg. Demo.kt is compiled to DemoKt.class
				String[] temp = className.split("\\.");
				String classFileName = temp[temp.length-1];
				String srcFilename = "";

				if(classFileName.substring(classFileName.length()-2).equals("Kt")) {
					srcFilename = classFileName.substring(0, classFileName.length()-2) + KOTLIN_FILE_EXTENSION;
				}
				else if(classFileName.contains("$")) {
					srcFilename = Arrays.asList(classFileName.split("\\$")).get(0) + KOTLIN_FILE_EXTENSION;
				}
				// because in some projects the class names aren't renamed
				else {
					srcFilename = classFileName + KOTLIN_FILE_EXTENSION;
				}

				for (final IPackageFragment l : JavaCore.create(currentProject).getPackageFragments()) {
					// this check is needed because IJavaProject.getPackageFragments() returns dependencies as well
					if(l.getKind() == IPackageFragmentRoot.K_SOURCE) {
						// removing the <project_name> from path returned by IPackageFragment.getPath() because IProject.getFile() also appends it
						String[] originalPath = l.getPath().toString().split(File.separator);
						String[] modifiedPath = Arrays.copyOfRange(originalPath, 2, originalPath.length);
						String packageName = String.join(File.separator, modifiedPath);

						IFile sourceFile = currentProject.getFile(packageName + File.separator + srcFilename);
						if(sourceFile.exists()) {
							return (IResource) sourceFile;
						}
					}
				}
			}
		}
		
		throw new ClassNotFoundException("Class " + className + " not found.");
	}
	
	public static boolean verifyKotlinDependency(IJavaProject javaProject) {
 		IProject project = javaProject.getProject();
 		IFile pomFile = project.getFile("pom.xml");
 		boolean isPresent = false;
 		try {
 			MavenProject mavenProject = loadProject(pomFile.getLocation().toFile());
 			List<Dependency> dependencies = mavenProject.getDependencies();
 			
 			for (Dependency dependency : dependencies) {
 				if("kotlin-stdlib".equals(dependency.getArtifactId())) {
 					isPresent = true;
 				}
 			}
 			if(!isPresent) {
 				if(requestUsersPermission() == SWT.YES) {
 					addKotlinDependency(mavenProject, dependencies);
 					isPresent = true;
 				}
 			}
 		} catch (Exception e) {
 			de.cognicrypt.staticanalyzer.Activator.getDefault().logError(e);
 		}
 		return isPresent;
 	}
	
	public static void waitForBuildAndRefreshJobs() {
		ArrayList<Job> jobs = new ArrayList<Job>();
		while(true)
		{
			jobs.clear();
			jobs.addAll(Arrays.asList(Job.getJobManager().find(ResourcesPlugin.FAMILY_AUTO_BUILD)));
			jobs.addAll(Arrays.asList(Job.getJobManager().find(ResourcesPlugin.FAMILY_AUTO_REFRESH)));
			jobs.addAll(Arrays.asList(Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_BUILD)));
			jobs.addAll(Arrays.asList(Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_REFRESH)));
			if(jobs.isEmpty()) {
				return;
			}
			
			boolean buildSuccess;
			do {
				buildSuccess = true;
				for (Job job : jobs) {
					if(job.getState() != Job.NONE)
						buildSuccess = false;
				}
			}
			while(!buildSuccess);
		}
	}

	private static void addKotlinDependency(MavenProject project, List<Dependency> dependencies) {
		Dependency kotlinDependency = new Dependency();
		kotlinDependency.setGroupId("org.jetbrains.kotlin");
		kotlinDependency.setArtifactId("kotlin-stdlib");
		kotlinDependency.setVersion("1.3.61");
		kotlinDependency.setType("jar");
		dependencies.add(kotlinDependency);
		project.setDependencies(dependencies);
		storeProject(project);
	}
	
	private static void storeProject(MavenProject project) {
 		MavenXpp3Writer mavenWriter = new MavenXpp3Writer();
 		Model model = project.getModel();
 		try {
 			FileWriter writer = new FileWriter(model.getPomFile());
 			mavenWriter.write(writer, model);
 			de.cognicrypt.staticanalyzer.Activator.getDefault().logInfo("Required kotlin dependency added to pom.");
 		} catch (IOException e) {
 			de.cognicrypt.staticanalyzer.Activator.getDefault().logError(e);
 		}
 	}

 	private static MavenProject loadProject(File pomFile) throws IOException, XmlPullParserException
 	{
 		MavenProject ret = null;
 		MavenXpp3Reader mavenReader = new MavenXpp3Reader();

 		if (pomFile != null && pomFile.exists())
 		{
 			try	(FileReader reader = new FileReader(pomFile)) {
 				Model model = mavenReader.read(reader);
 				model.setPomFile(pomFile);
 				ret = new MavenProject(model);
 			}
 		}
 		return ret;
 	}
 	
 	private static int requestUsersPermission() {
 		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
 		MessageBox dialog = new MessageBox(window.getShell(),
 				SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO);
 		return selectedDialogOption(dialog);
 	}

	private static int selectedDialogOption(MessageBox dialog) {
		dialog.setMessage("Analysis requires maven kotlin-stdlib dependency. Would you like to add it?");
 		dialog.setText("CAUTION");
 		return dialog.open();
	}
 	
 	
}
