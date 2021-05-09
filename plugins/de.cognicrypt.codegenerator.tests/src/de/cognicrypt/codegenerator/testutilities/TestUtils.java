/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0
 * which is available at http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.testutilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.Utils;

/**
 * @author André Sonntag
 */
public class TestUtils {
	private static Logger log = Logger.getLogger(TestUtils.class.getName());

	/**
	 * This method creates an empty Java project in the current workspace
	 * 
	 * @param projectName The name of the Java project
	 * @return The newly created Java project
	 */
	public static IJavaProject createJavaProject(final String projectName) {
		IJavaProject javaProject = null;
		try {
			final IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			deleteProject(workSpaceRoot.getProject(projectName));

			final IProject project = workSpaceRoot.getProject(projectName);
			project.create(null);
			project.open(null);

			final IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] {JavaCore.NATURE_ID});
			project.setDescription(description, null);

			javaProject = JavaCore.create(project);

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
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
		}
		
		return javaProject;
	}

	/**
	 * This method creates a package with a Java class into a Java project
	 * 
	 * @param project The Java project 
	 * @param packageName The package name
	 * @param className The name of the new Java class
	 * @return The resource with the generated Java class in it
	 */
	public static IResource generateJavaClassInJavaProject(final IJavaProject project, final String packageName, final String className) {
		IResource unitResource = null;
		try {
			final IPackageFragment pack = project.getPackageFragmentRoot(project.getProject().getFolder("src")).createPackageFragment(packageName, false, null);
			final String source = "public class " + className + " {\n\n}\n";
			final StringBuffer buffer = new StringBuffer();
			buffer.append("package " + pack.getElementName() + ";\r\n\r\n");
			buffer.append(source);
			ICompilationUnit unit = pack.createCompilationUnit(className + ".java", buffer.toString(), false, null);
			unitResource = unit.getUnderlyingResource();
		} catch (JavaModelException e) {
			Activator.getDefault().logError(e);
		}
		
		return unitResource;
	}

	/**
	 * This method deletes a Java project from the workspace or hard drive
	 * 
	 * @param project Java project that will be deleted
	 */
	public static void deleteProject(final IProject project) {
		try {
			project.delete(true, true, null);
		}
		
		catch (CoreException e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * This method looks for the right task by name
	 * 
	 * @param name The name of the task
	 * @return The task that is found by name
	 * @throws NoSuchElementException
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
	 * This method creates a HashMap which contains the questions and the associated default answers for the given task
	 *
	 * @param t The task
	 * @return A HashMap with questions and their default answers
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
	 * This method creates a HashMap which contains the questions and the associated given answers for the selected task
	 *
	 * @param task The task
	 * @param answers The list of the given answers
	 * @return An ArrayList<String> containing answers for the selected task
	 */
	public static HashMap<Question, Answer> setConstraintsForTask(final Task task, ArrayList<String> answers) {
		final List<Page> pageList = (new QuestionsJSONReader()).getPages(task.getQuestionsJSONFile());
		final HashMap<Question, Answer> contraintsForTask = new LinkedHashMap<>();

		if (pageList.isEmpty()) {
			return contraintsForTask;
		}

		for (Page page : pageList) {
			for (int i = 0; i < page.getContent().size(); i++) {
				Question tmpQuestion = page.getContent().get(i);
				for (Answer answer : tmpQuestion.getAnswers()) {
					if (answer.getValue().equals(answers.get(i))) {
						contraintsForTask.put(tmpQuestion, answer);
						break;
					}
				}
			}
		}

		return contraintsForTask;
	}

	/**
	 * This method creates the necessary XSL configuration for code generation
	 *
	 * @param developerProject The project
	 * @param t The task for what the configuration is created for
	 * @return The configuration for a given task
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
	 * This method creates the necessary CrySL configuration for code generation
	 * 
	 * @param template The name of the template
	 * @param targetFile The target file
	 * @param codeGenerator The code generator that is set up
	 * @param developerProject The project
	 * @return The configuration for a given task
	 */
	public static CrySLConfiguration createCrySLConfiguration(String template, IResource targetFile, CodeGenerator codeGenerator, DeveloperProject developerProject) {
		CrySLConfiguration chosenConfig = null;
		try {
			File templateFile = CodeGenUtils.getResourceFromWithin(Constants.codeTemplateFolder + template).listFiles()[0];
			String projectRelDir =
					Constants.outerFileSeparator + codeGenerator.getDeveloperProject().getSourcePath() + Constants.outerFileSeparator + Constants.PackageName + Constants.outerFileSeparator;
			String pathToTemplateFile = projectRelDir + templateFile.getName();
			String resFileOSPath = targetFile.getProject().getLocation().toOSString() + pathToTemplateFile;

			Files.createDirectories(Paths.get(targetFile.getProject().getLocation().toOSString() + projectRelDir));
			Files.copy(templateFile.toPath(), Paths.get(resFileOSPath), StandardCopyOption.REPLACE_EXISTING);
			developerProject.refresh();

			GeneratorClass genClass = ((CrySLBasedCodeGenerator) codeGenerator).setUpTemplateClass(pathToTemplateFile);
			chosenConfig = new CrySLConfiguration("", genClass);
			return chosenConfig;
		} catch(CoreException | IOException e) {
			Activator.getDefault().logError(e);
		}
		
		return chosenConfig;
	}

	/**
	 * This method opens a certain Java file in the current workspace
	 *
	 * @param project The project
	 * @param packageName The name of the package
	 * @param cu The compilation unit
	 */
	public static void openJavaFileInWorkspace(final DeveloperProject project, final String packageName, final ICompilationUnit cu) {
		try {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IFile openFile = project.getIFile(getFilePathInProject(project, packageName, cu));
			IDE.openEditor(page, openFile);
		} catch(CoreException e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * This method finds and returns a CompilationUnit from a project
	 *
	 * @param project The project
	 * @param packageName The name of the package
	 * @param cuName The name of the compilation unit
	 * @return The compilation unit
	 */
	public static ICompilationUnit getICompilationUnit(final DeveloperProject project, final String packageName, final String cuName) {
		ICompilationUnit unit = null;
		try {
			IPackageFragment packageFragment = project.getPackagesOfProject(packageName);
			for (int i = 0; i < packageFragment.getCompilationUnits().length; i++) {
				if (packageFragment.getCompilationUnits()[i].getElementName().equals(cuName)) {

					return packageFragment.getCompilationUnits()[i];
				}
			}
		} catch(CoreException e) {
			Activator.getDefault().logError(e);
		}
		
		return unit;
	}

	/**
	 * This method prints the source code of a given package
	 *
	 * @param project The project
	 * @param packageName The name of the package
	 */
	public static void printSourceCode(final DeveloperProject project, final String packageName) {
		IPackageFragment packageFragment;
		try {
			packageFragment = project.getPackagesOfProject(packageName);
			for (int i = 0; i < packageFragment.getCompilationUnits().length; i++) {
				log.info("\n" + packageFragment.getCompilationUnits()[i].getSource());
			}
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e, Constants.ERROR_CANNOT_PRINT_SRC_CODE);
		}
	}

	/**
	 * This method reads a Java file and stores its contents into a byte array
	 *
	 * @param project The project
	 * @param packageName The name of the package
	 * @param unit The compilation unit
	 * @return The converted file contents into a byte array
	 */
	public static byte[] fileToByteArray(final DeveloperProject project, final String packageName, final ICompilationUnit cu) {
		byte[] fBytes = null;
		try {
			File f = new File(getFilePathInProject(project, packageName, cu));
			fBytes = Files.readAllBytes(Paths.get(f.getPath()));
		}
		catch (CoreException | IOException e) {
			Activator.getDefault().logError(e, Constants.ERROR_CANNOT_FILE_TO_BYTEARRAY);
		}
		return fBytes;
	}

	/**
	 * This method returns the path to file in a certain developer project
	 *
	 * @param project The project
	 * @param packageName The name of the package
	 * @param cu The compilation unit
	 * @return The path to file in a developer project
	 * @throws CoreException
	 */
	private static String getFilePathInProject(final DeveloperProject project, final String packageName, final ICompilationUnit cu) throws CoreException {
		return project.getProjectPath() + Constants.innerFileSeparator + project.getSourcePath() + Constants.innerFileSeparator + packageName + Constants.innerFileSeparator
				+ cu.getElementName();
	}

	/**
	 * This method counts methods in ICompilationUnits
	 * 
	 * @param unit The unit
	 * @return The number of methods in a given unit
	 */
	public static int countMethods(ICompilationUnit unit) {
		int methodCount = -1;
		try {
			IMethod[] methods = unit.getAllTypes()[0].getMethods();
			methodCount = methods.length;
		} catch(JavaModelException e) {
			Activator.getDefault().logError(e);
		}
		
		return methodCount;
	}

	/**
	 * This method counts methods in ICompilationUnits
	 * 
	 * @param unit The unit
	 * @param method The method
	 * @return The number of statements in a given method
	 */
	public static int countStatements(ICompilationUnit unit, String method) {
		int statementCount = -1;
		try {
			for (IMethod meth : unit.getAllTypes()[0].getMethods()) {
				if (method.equals(meth.getElementName())) {
					statementCount = meth.getSource().split(";").length - 1;
				}
			}
		} catch(JavaModelException e) {
			Activator.getDefault().logError(e);
		}

		return statementCount;
	}

	/**
	 * This method generates a package in a Java project
	 * 
	 * @param generatedProject The Java project
	 * @param packageName The name of the package
	 * @return The generated package fragment in the Java project
	 * @throws JavaModelException
	 */
	public static IPackageFragment generatePackageInJavaProject(IJavaProject generatedProject, String packageName) throws JavaModelException {
		return generatedProject.getPackageFragmentRoot(generatedProject.getProject().getFolder("src")).createPackageFragment(packageName, false, null);
	}

	/**
	 * This method checks if a package exists in a Java project
	 * 
	 * @param generatedProject The Java project
	 * @param packageName The name of the package
	 * @return The truth of whether a package exists in a Java project
	 */
	public static boolean packageExists(IJavaProject generatedProject, String packageName) {
		final IPackageFragment expectedPackage = generatedProject.getPackageFragmentRoot(generatedProject.getProject().getFolder("src")).getPackageFragment(packageName);
		return expectedPackage != null;
	}

}
