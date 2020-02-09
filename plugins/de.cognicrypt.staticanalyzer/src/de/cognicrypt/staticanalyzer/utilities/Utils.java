package de.cognicrypt.staticanalyzer.utilities;

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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jetbrains.kotlin.core.model.KotlinNature;

import de.cognicrypt.staticanalyzer.Activator;

public class Utils {

	public static IResource findClassByName(final String className, final IProject currentProject) throws ClassNotFoundException {
 		try {
 			for (final IPackageFragment l : JavaCore.create(currentProject).getPackageFragments()) {
 				for (final ICompilationUnit cu : l.getCompilationUnits()) {
 					final IJavaElement cuResource = JavaCore.create(cu.getCorrespondingResource());
 					String name = cuResource.getParent().getElementName() + "." + cuResource.getElementName();

 					if (name.startsWith(".")) {
 						name = name.substring(1);
 					}
 					if (name.startsWith(className)) {
 						return cu.getCorrespondingResource();
 					}
 				}
 			}

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
 						srcFilename = classFileName.substring(0, classFileName.length()-2) + ".kt";
 					}
 					// because in some projects the class names aren't renamed
 					else
 						srcFilename = classFileName + ".kt";

 					for (final IPackageFragment l : JavaCore.create(currentProject).getPackageFragments()) {
 						// this check is needed because IJavaProject.getPackageFragments() returns dependencies as well
 						if(l.getKind() == IPackageFragmentRoot.K_SOURCE) {
 							// removing the <project_name> from path returned by IPackageFragment.getPath() because IProject.getFile() also appends it
 							String[] originalPath = l.getPath().toString().split(File.separator);
 							String[] modifiedPath = Arrays.copyOfRange(originalPath, 2, originalPath.length);
 							String packageName = String.join(File.separator, modifiedPath);

 							IFile sourceFile = currentProject.getFile(packageName + File.separator + srcFilename);
 							if(sourceFile.exists())
 								return (IResource) sourceFile;
 						}
 					}
 				}
 			}
 		}
 		catch (final JavaModelException e) {
 			throw new ClassNotFoundException("Class " + className + " not found.", e);
 		}
 		throw new ClassNotFoundException("Class " + className + " not found.");
 	}
	
	public static boolean verifyKotlinDependency(IJavaProject javaProject) {
 		IProject p = javaProject.getProject();
 		IFile pomFile = p.getFile("pom.xml");
 		boolean isPresent = false;
 		try {
 			MavenProject project = loadProject(pomFile.getLocation().toFile());
 			List<Dependency> dependencies = project.getDependencies();
 			
 			for (Dependency dependency : dependencies) {
 				System.out.println(dependency);
 				if(dependency.getArtifactId().equals("kotlin-stdlib"))
 					isPresent = true;
 			}
 			if(!isPresent) {
 				boolean accepted = requestUsersPermission();
 				if(accepted) {
 					addKotlinDependency(project, dependencies);
 					isPresent = true;
 				}
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return isPresent;
 	}
	
	public static void waitForBuildAndRefreshJobs() {
		ArrayList<Job> jobs = new ArrayList<Job>();
		Job[] jobsArray;
		while(true)
		{
			jobs.clear();
			jobsArray = Job.getJobManager().find(ResourcesPlugin.FAMILY_AUTO_BUILD);
			jobs.addAll(Arrays.asList(jobsArray));
			jobsArray = Job.getJobManager().find(ResourcesPlugin.FAMILY_AUTO_REFRESH);
			jobs.addAll(Arrays.asList(jobsArray));
			jobsArray = Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_BUILD);
			jobs.addAll(Arrays.asList(jobsArray));
			jobsArray = Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_REFRESH);
			jobs.addAll(Arrays.asList(jobsArray));
			if(jobs.size() == 0) {
				System.out.println("No pending jobs found.");
				return;
			}
			System.out.println("Waiting for " + jobs.size() + " Jobs to finish...");
			
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
 			Activator.getDefault().logInfo("Required kotlin dependency added to pom.");
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	}

 	private static MavenProject loadProject(File pomFile) throws Exception
 	{
 		MavenProject ret = null;
 		MavenXpp3Reader mavenReader = new MavenXpp3Reader();

 		if (pomFile != null && pomFile.exists())
 		{
 			FileReader reader = null;
 			try
 			{
 				reader = new FileReader(pomFile);
 				Model model = mavenReader.read(reader);
 				model.setPomFile(pomFile);
 				ret = new MavenProject(model);
 			}
 			finally
 			{
 				reader.close();
 			}
 		}
 		return ret;
 	}
 	
 	private static boolean requestUsersPermission() {
 		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
 		MessageBox dialog = new MessageBox(window.getShell(),
 				SWT.APPLICATION_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO);
 		dialog.setMessage("Analysis requires maven kotlin-stdlib dependency. Would you like to add it?");
 		dialog.setText("CAUTION");
 		int opt = dialog.open();
 		if (opt == SWT.YES)
 			return true;
 		else
 			return false;
 	}
}
