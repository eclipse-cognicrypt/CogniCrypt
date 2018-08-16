package de.cognicrypt.codegenerator.testutilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

/**
 * @author André Sonntag
 */
public class TestUtils {

	/**
	 * This method creates a empty JavaProject in the current workspace
	 * 
	 * @param projectName
	 *            for the JavaProject
	 * @return new created JavaProject
	 * @throws CoreException
	 */
	public static IJavaProject createJavaProject(String projectName) throws CoreException {

		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		deleteProject(workSpaceRoot.getProject(projectName));

		IProject project = workSpaceRoot.getProject(projectName);
		project.create(null);
		project.open(null);

		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);

		IJavaProject javaProject = JavaCore.create(project);

		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}
		// add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);

		IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(packageRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);

		return javaProject;
	}

	/**
	 * This method creates a package with a java class into a JavaProject
	 * 
	 * @param project
	 *            JavaProject in which the new Java class will be generated
	 * @param packageName
	 *            package in which the new Java class will be generated
	 * @param className
	 *            name of the new Java class
	 * @throws JavaModelException
	 */
	public static void generateJavaClassInJavaProject(IJavaProject project, String packageName, String className)
			throws JavaModelException {

		IPackageFragment pack = project.getPackageFragmentRoot(project.getProject().getFolder("src"))
				.createPackageFragment(packageName, false, null);
		String source = "public class " + className + " {\n\n}\n";
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\r\n\r\n");
		buffer.append(source);
		ICompilationUnit cu = pack.createCompilationUnit(className + ".java", buffer.toString(), false, null);
	}

	/**
	 * This method deletes a JavaProject from the Workspace/hard drive
	 * 
	 * @param project
	 *            JavaProject which will be deleted
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void deleteProject(IProject project) throws CoreException {
		project.delete(true, true, null);
	}

	/**
	 * This method looks for the right task by name
	 * 
	 * @param name
	 *            name of the task what we looking for.
	 * @return Task object
	 */
	public static Task getTask(String name) throws NoSuchElementException {
		for (Task t : TaskJSONReader.getTasks()) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * This method creates a HashMap. This HashMap contains the Questions and the
	 * associated default Answers for certain Task.
	 * 
	 * @param t
	 *            Task
	 * @return A HashMap with Questions and default Answers
	 */
	public static HashMap<Question, Answer> setDefaultConstraintsForTask(Task t) {

		List<Page> pageList = (new QuestionsJSONReader()).getPages(t.getQuestionsJSONFile());
		HashMap<Question, Answer> contraintsForTask = new HashMap<>();

		for (final Page page : pageList) {
			for (final Question question : page.getContent()) {
				contraintsForTask.put(question, question.getDefaultAnswer());
			}
		}
		return contraintsForTask;
	}

	/**
	 * This method creates the necessary Configuration for a CodeGenerator.
	 * 
	 * @param developerProject
	 * @param t
	 *            task for what we create the Configuration
	 * @return Configuration for a certain Task
	 */
	public static Configuration createConfigurationForCodeGeneration(DeveloperProject developerProject, Task t) {
		
		//InstanceGenerator instGen = new InstanceGenerator(Utils.getResourceFromWithin(t.getModelFile()).getAbsolutePath(), "c0_" + t.getName(),t.getTaskDescription());
		InstanceGenerator instGen = new InstanceGenerator(Utils.getResourceFromWithin(t.getModelFile(),de.cognicrypt.codegenerator.Activator.PLUGIN_ID).getAbsolutePath(), "c0_" + t.getName(),t.getTaskDescription());
		
		//InstanceGenerator instGen = new InstanceGenerator(Utils.getResourceFromWithin(t.getModelFile()).getAbsolutePath(), "c0_" + t.getName(),t.getTaskDescription());
		HashMap<Question, Answer> constraints = TestUtils.setDefaultConstraintsForTask(t);
		List<InstanceClafer> instList = instGen.generateInstances(constraints);
		InstanceClafer inst = instList.get(0);
		Configuration ret = new Configuration(inst, constraints,
				developerProject.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
		return ret;
	}

	/**
	 * This Method open a certain Java file in the current workspace
	 * 
	 * @param project
	 * @param packageName
	 * @param unit
	 * @throws CoreException
	 */
	public static void openJavaFileInWorkspace(DeveloperProject project, String packageName, ICompilationUnit cu)
			throws CoreException {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IFile openFile = project.getIFile(getFilePathInProject(project, packageName, cu));
		IEditorPart editor = IDE.openEditor(page, openFile);
	}

	/**
	 * This method finds and returns a CompilationUnit from a project
	 * 
	 * @param project
	 * @param packageName
	 * @param unit
	 * @return IComplitationUnit
	 * @throws CoreException
	 */
	public static ICompilationUnit getICompilationUnit(DeveloperProject project, String packageName, String cuName)
			throws CoreException, NoSuchElementException {
		IPackageFragment packageFragment = project.getPackagesOfProject(packageName);
		for (int i = 0; i < packageFragment.getCompilationUnits().length; i++) {
			if (packageFragment.getCompilationUnits()[i].getElementName().equals(cuName)) {
				return packageFragment.getCompilationUnits()[i];
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * This method passed a Java file into a byte array
	 * 
	 * @param project
	 * @param packageName
	 * @param unit
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static byte[] fileToByteArray(DeveloperProject project, String packageName, ICompilationUnit cu)
			throws IOException, CoreException {

		File f = new File(getFilePathInProject(project, packageName, cu));
		if (!(f.exists() && Files.isReadable(f.toPath()))) {
			throw new IOException();
		}

		return Files.readAllBytes(Paths.get(f.getPath()));
	}

	/**
	 * This method returns the Path to File in a certain DeveloperProject
	 * 
	 * @param project
	 * @param packageName
	 * @param cu
	 * @return
	 * @throws CoreException
	 */
	private static String getFilePathInProject(DeveloperProject project, String packageName, ICompilationUnit cu)
			throws CoreException {
		final String srcPath = project.getProjectPath() + Constants.innerFileSeparator + project.getSourcePath();
		String cuPath = srcPath + Constants.innerFileSeparator + packageName + Constants.innerFileSeparator
				+ cu.getElementName();
		return cuPath;
	}
}
