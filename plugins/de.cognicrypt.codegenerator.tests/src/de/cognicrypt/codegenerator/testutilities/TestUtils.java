/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.testutilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import org.clafer.instance.InstanceClafer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.generator.GeneratorClass;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.codegenerator.wizard.CrySLConfiguration;
import de.cognicrypt.codegenerator.wizard.XSLConfiguration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.Utils;

/**
 * @author André Sonntag
 */
public class TestUtils {
	private static Logger log = Logger.getLogger(TestUtils.class.getName());

	/**
	 * This method creates a empty JavaProject in the current workspace
	 * 
	 * @param projectName for the JavaProject
	 * @return new created JavaProject
	 * @throws CoreException
	 */
	public static IJavaProject createJavaProject(final String projectName) throws CoreException {

		final IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		deleteProject(workSpaceRoot.getProject(projectName));

		final IProject project = workSpaceRoot.getProject(projectName);
		project.create(null);
		project.open(null);

		final IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {JavaCore.NATURE_ID});
		project.setDescription(description, null);

		final IJavaProject javaProject = JavaCore.create(project);

		final IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		final List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		final IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		final LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (final LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}
		// add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

		final IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);

		final IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		final IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		final IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(packageRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);

		return javaProject;
	}

	/**
	 * This method creates a package with a java class into a JavaProject <<<<<<< HEAD
	 * 
	 * @param project JavaProject in which the new Java class will be generated
	 * @param packageName package in which the new Java class will be generated
	 * @param className name of the new Java class
	 * @throws JavaModelException
	 */
	public static IResource generateJavaClassInJavaProject(final IJavaProject project, final String packageName, final String className) throws JavaModelException {

		final IPackageFragment pack = project.getPackageFragmentRoot(project.getProject().getFolder("src")).createPackageFragment(packageName, false, null);
		final String source = "public class " + className + " {\n\n}\n";
		final StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\r\n\r\n");
		buffer.append(source);
		ICompilationUnit unit = pack.createCompilationUnit(className + ".java", buffer.toString(), false, null);
		return unit.getUnderlyingResource();
	}

	/**
	 * This method deletes a JavaProject from the Workspace/hard drive
	 * 
	 * @param project Java project that will be deleted
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void deleteProject(final IProject project) throws CoreException {
		project.delete(true, true, null);
	}

	/**
	 * This method looks for the right task by name
	 * 
	 * @param name name of the task what we looking for.
	 * @return Task object
	 */
	public static Task getTask(final String name) throws NoSuchElementException {
		for (final Task t : TaskJSONReader.getTasks()) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		throw new NoSuchElementException(name);
	}

	/**
	 * This method creates a HashMap. This HashMap contains the Questions and the associated default Answers for certain Task.
	 *
	 * @param t Task
	 * @return A HashMap with Questions and default Answers
	 */
	public static HashMap<Question, Answer> setDefaultConstraintsForTask(final Task t) {

		final List<Page> pageList = (new QuestionsJSONReader()).getPages(t.getQuestionsJSONFile());
		final HashMap<Question, Answer> contraintsForTask = new HashMap<>();

		if (pageList.isEmpty()) {
			return contraintsForTask;
		}

		int nextID = 0;
		int tempID = 0;
		while (nextID != -1) {
			for (final Question question : pageList.get(nextID).getContent()) {
				contraintsForTask.put(question, question.getDefaultAnswer());
				tempID = question.getDefaultAnswer().getNextID() != -2 ? question.getDefaultAnswer().getNextID() : -100;
			}
			nextID = tempID == -100 ? pageList.get(nextID).getNextID() : tempID;
		}

		return contraintsForTask;
	}

	/**
	 * This method creates the necessary Configuration for a CodeGenerator.
	 *
	 * @param developerProject
	 * @param t task for what we create the Configuration
	 * @return Configuration for a certain Task
	 */
	public static Configuration createXSLConfigurationForCodeGeneration(final DeveloperProject developerProject, final Task t) {

		final InstanceGenerator instGen = new InstanceGenerator(Utils.getResourceFromWithin(t.getModelFile(), de.cognicrypt.codegenerator.Activator.PLUGIN_ID).getAbsolutePath(),
				"c0_" + t.getName(), t.getTaskDescription());

		final HashMap<Question, Answer> constraints = TestUtils.setDefaultConstraintsForTask(t);
		final List<InstanceClafer> instList = instGen.generateInstances(constraints);
		final InstanceClafer inst = instList.get(0);
		final Configuration ret = new XSLConfiguration(inst, constraints, developerProject.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
		return ret;
	}

	/**
	 * This method creates the necessary Configuration for a CodeGenerator.
	 * 
	 * @param codeGenerator
	 * @param t task for what we create the Configuration
	 * @return Configuration for a certain Task
	 */
	public static CrySLConfiguration createCrySLConfiguration(String template, IResource targetFile, CodeGenerator codeGenerator, DeveloperProject developerProject)
			throws CoreException, IOException {
		File templateFile = CodeGenUtils.getResourceFromWithin(Constants.codeTemplateFolder + template).listFiles()[0];
		String projectRelDir =
				Constants.outerFileSeparator + codeGenerator.getDeveloperProject().getSourcePath() + Constants.outerFileSeparator + Constants.PackageName + Constants.outerFileSeparator;
		String pathToTemplateFile = projectRelDir + templateFile.getName();
		String resFileOSPath = targetFile.getProject().getLocation().toOSString() + pathToTemplateFile;

		Files.createDirectories(Paths.get(targetFile.getProject().getLocation().toOSString() + projectRelDir));
		Files.copy(templateFile.toPath(), Paths.get(resFileOSPath), StandardCopyOption.REPLACE_EXISTING);
		developerProject.refresh();

		GeneratorClass genClass = ((CrySLBasedCodeGenerator) codeGenerator).setUpTemplateClass(pathToTemplateFile);
		CrySLConfiguration chosenConfig = new CrySLConfiguration("", genClass);
		return chosenConfig;
	}

	/**
	 * This Method open a certain Java file in the current workspace
	 *
	 * @param project
	 * @param packageName
	 * @param unit
	 * @throws CoreException
	 */
	public static void openJavaFileInWorkspace(final DeveloperProject project, final String packageName, final ICompilationUnit cu) throws CoreException {

		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final IFile openFile = project.getIFile(getFilePathInProject(project, packageName, cu));
		IDE.openEditor(page, openFile);
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
	public static ICompilationUnit getICompilationUnit(final DeveloperProject project, final String packageName, final String cuName) throws CoreException, NoSuchElementException {
		final IPackageFragment packageFragment = project.getPackagesOfProject(packageName);
		for (int i = 0; i < packageFragment.getCompilationUnits().length; i++) {
			if (packageFragment.getCompilationUnits()[i].getElementName().equals(cuName)) {
				return packageFragment.getCompilationUnits()[i];
			}
		}
		return null;
	}

	public static void printSourceCode(final DeveloperProject project, final String packageName) throws CoreException, NoSuchElementException {
		final IPackageFragment packageFragment = project.getPackagesOfProject(packageName);
		for (int i = 0; i < packageFragment.getCompilationUnits().length; i++) {
			log.info("\n" + packageFragment.getCompilationUnits()[i].getSource());
		}

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
	public static byte[] fileToByteArray(final DeveloperProject project, final String packageName, final ICompilationUnit cu) throws IOException, CoreException {

		final File f = new File(getFilePathInProject(project, packageName, cu));
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
	private static String getFilePathInProject(final DeveloperProject project, final String packageName, final ICompilationUnit cu) throws CoreException {
		final String srcPath = project.getProjectPath() + Constants.innerFileSeparator + project.getSourcePath();
		final String cuPath = srcPath + Constants.innerFileSeparator + packageName + Constants.innerFileSeparator + cu.getElementName();
		return cuPath;
	}

	/**
	 * This method counts methods in ICompilationUnits
	 * 
	 * @param unit
	 * @return
	 * @throws JavaModelException
	 */
	public static int countMethods(ICompilationUnit unit) throws JavaModelException {
		return unit.getAllTypes()[0].getMethods().length;
	}

	public static int countStatements(ICompilationUnit unit, String method) throws JavaModelException {
		for (IMethod meth : unit.getAllTypes()[0].getMethods()) {
			if (method.equals(meth.getElementName())) {
				return meth.getSource().split(";").length - 1;
			}
		}

		return -1;
	}

}
