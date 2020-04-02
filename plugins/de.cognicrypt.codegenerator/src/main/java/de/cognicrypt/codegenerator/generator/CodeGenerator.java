/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap.SimpleEntry;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.ComparableEntry;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.UIUtils;

public abstract class CodeGenerator {

	protected final DeveloperProject project;
	protected final IFile targetFile;
	private int endingPositionForRunMethod = -1;
	private int endPosForImports = -1;
	private int startingPositionForRunMethod = -1;
	private int startPosForImports = -1;
	private String temporaryOutputFile;

	protected CodeGenerator(final IResource target) {
		this.project = new DeveloperProject(target.getProject());
		if (target instanceof IFile) {
			this.targetFile = (IFile) target;
		} else {
			this.targetFile = null;
		}
	}

	/**
	 * Generation of code templates.
	 *
	 * @param chosenConfig
	 *        Solution chosen by the user.
	 * @param pathToFolderWithAdditionalResources
	 *        If additional files need to be generated into a developer's project, they are in this folder.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if transformation successful/failed.
	 *
	 */
	public abstract boolean generateCodeTemplates(Configuration chosenConfig, final String pathToFolderWithAdditionalResources);

	/**
	 * Getter method for developer project the code is generated into.
	 *
	 * @return developer project
	 */
	public DeveloperProject getDeveloperProject() {
		return this.project;
	}

	/**
	 * If a file was open when the code generation was started, this method inserts the glue code that calls the generated classes directly into the opened file and removes the
	 * temporary output file. If no file was open this method is skipped and the temporary output file is not removed.
	 *
	 * @param temporaryOutputFile
	 *        Path to temporary output file.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if insertion successful/failed.
	 * @throws BadLocationException
	 *         See {@link org.eclipse.jface.text.IDocument#replace(int, int, String) replace()}
	 * @throws IOException
	 *         See {@link crossing.e1.configurator.codegeneration.XSLBasedGenerator#getCallsForGenClasses(String) getCallsForGenClasses()}
	 * @throws CoreException
	 *         See {@link DeveloperProject.crossing.opencce.cryptogen.CryptoProject#refresh() refresh()}
	 */
	protected boolean insertCallCodeIntoFile(final String temporaryOutputFile, final boolean openFileFlag, final boolean authorFlag, final boolean tempFlag) throws BadLocationException, CoreException, IOException {
			if (this.targetFile != null) {	
				if(this.targetFile.getRawLocation().toOSString().equals(Paths.get(temporaryOutputFile).toString())) {
					return true;
				}
				else {
					IDE.openEditor(UIUtils.getCurrentlyOpenPage(), targetFile);
				}
			}

		if ((openFileFlag && authorFlag) || !openFileFlag) {
			final StringBuilder sb = new StringBuilder(temporaryOutputFile);
			sb.delete(temporaryOutputFile.length() - 9, temporaryOutputFile.length() - 5);
			final IFile output = tempFlag == true ? this.project.getIFile(sb.toString()) : this.project.getIFile(temporaryOutputFile);
			IDE.openEditor(UIUtils.getCurrentlyOpenPage(), output);
		}

		final IEditorPart currentlyOpenPart = UIUtils.getCurrentlyOpenEditor();
		if (currentlyOpenPart == null || !(currentlyOpenPart instanceof AbstractTextEditor)) {
			Activator.getDefault().logError(null,
				"Could not open access the editor of the file. Therefore, an outputfile containing calls to the generated classes in the Crypto package was generated.");
			return false;
		}

		final ITextEditor currentlyOpenEditor = (ITextEditor) currentlyOpenPart;
		final IDocument currentlyOpenDocument = currentlyOpenEditor.getDocumentProvider().getDocument(currentlyOpenEditor.getEditorInput());
		final String docContent = currentlyOpenDocument.get();
		final TreeSet<SimpleEntry<Integer, Integer>> methLims = new TreeSet<>();
		final SimpleEntry<Integer, SimpleEntry<Integer, Integer>> classlims = new SimpleEntry<>(0, null);

		final ASTParser astp = ASTParser.newParser(AST.JLS11);
		astp.setSource(docContent.toCharArray());
		astp.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) astp.createAST(null);
		final ASTVisitor astVisitor = new ASTVisitor(true) {

			@Override
			public boolean visit(final MethodDeclaration node) {
				methLims.add(new ComparableEntry<>(node.getStartPosition(), node.getStartPosition() + node.getLength()));
				return super.visit(node);
			}

			@Override
			public boolean visit(final TypeDeclaration node) {
				classlims.setValue(new ComparableEntry<>(node.getStartPosition(), node.getStartPosition() + node.getLength()));
				return super.visit(node);
			}
		};
		cu.accept(astVisitor);

		// Check and correct cursor position
		// 1. case: cursor is outside the class -> set cursor position to end of the class
		// 2. case: it is inside the class but also inside a method -> set cursor position two right after the method
		int cursorPos = ((ITextSelection) currentlyOpenPart.getSite().getSelectionProvider().getSelection()).getOffset();
		if (classlims.getValue().getKey() < cursorPos || cursorPos < classlims.getValue().getValue()) {
			cursorPos = classlims.getValue().getValue() - 2;
		} else {
			for (final SimpleEntry<Integer, Integer> meth : methLims) {
				if (meth.getKey().intValue() > cursorPos) {
					break;
				}
				if (meth.getKey().intValue() <= cursorPos && meth.getValue().intValue() >= cursorPos) {
					cursorPos = meth.getValue().intValue() + 2;
					break;
				}
			}
		}

		final int imports = docContent.startsWith("package") ? docContent.indexOf("\n") : 0;
		final String[] callsForGenClasses = getCallsForGenClasses(temporaryOutputFile);
		currentlyOpenDocument.replace(cursorPos, 0, callsForGenClasses[1]);
		currentlyOpenDocument.replace(imports, 0, callsForGenClasses[0] + Constants.lineSeparator);
		currentlyOpenEditor.doSave(null);
		cleanUpProject(currentlyOpenEditor);
		return true;
	}

	/**
	 * This method extracts the glue code with the calls to the generated classes from the temporary output file.
	 *
	 * @param filePath
	 *        Path to temporary output file.
	 * @return Glue code from temporary output file which calls the generated files.
	 * @throws IOException
	 *         See {@link java.nio.file.Files#readAllLines(Path) readAllLines()}
	 */
	private String[] getCallsForGenClasses(final String filePath) throws IOException {
		// Checks whether file exists
		final File f = new File(filePath);
		if (!(f.exists() && Files.isWritable(f.toPath()))) {
			Activator.getDefault().logError(null, Constants.NoTemporaryOutputFile);
			return null;
		}
		// Retrieve complete content from file
		final String fileContent = String.join(Constants.lineSeparator, Files.readAllLines(Paths.get(filePath)));
		// Determine start and end position for relevant extract
		final ASTParser astp = ASTParser.newParser(AST.JLS11);
		astp.setSource(fileContent.toCharArray());
		astp.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) astp.createAST(null);
		final ASTVisitor astVisitor = new ASTVisitor(true) {

			@Override
			public boolean visit(final ImportDeclaration node) {
				CodeGenerator.this.startPosForImports = CodeGenerator.this.startPosForImports < 0 ? node.getStartPosition() : CodeGenerator.this.startPosForImports;
				final int endPos = node.getStartPosition() + node.getLength();
				CodeGenerator.this.endPosForImports = CodeGenerator.this.endPosForImports < endPos ? endPos : CodeGenerator.this.endPosForImports;
				return super.visit(node);
			}

			@Override
			public boolean visit(final MethodDeclaration node) {
				if (Constants.NameOfTemporaryMethod.equals(node.getName().toString())) {
					setPosForRunMethod(node.getStartPosition(), node.getStartPosition() + node.getLength());
				}
				return super.visit(node);
			}
		};
		cu.accept(astVisitor);
		if (this.startingPositionForRunMethod < 0 || this.endingPositionForRunMethod < 0) {
			Activator.getDefault().logError(Constants.NoRunMethodFoundInTemporaryOutputFileErrorMessage);
			return null;
		}
		// Delete temporary output file as it is not needed anymore
		f.delete();
		// Get extract from content that we actually need
		return new String[] { fileContent.substring(this.startPosForImports, this.endPosForImports), fileContent.substring(this.startingPositionForRunMethod,
			this.endingPositionForRunMethod) };
	}

	private void setPosForRunMethod(final int start, final int end) {
		this.startingPositionForRunMethod = start;
		this.endingPositionForRunMethod = end;
	}

	/**
	 * Removes crypto package from developer project.
	 *
	 * @throws CoreException
	 *         {@link DeveloperProject#getPackagesOfProject(String)} and {@link IPackageFragment#getCompilationUnit()}
	 */
	protected void removeCryptoPackageIfEmpty() throws CoreException {
		final IPackageFragment cryptoPackage = this.project.getPackagesOfProject(Constants.PackageNameAsName);
		if (cryptoPackage.getCompilationUnits().length == 0) {
			this.project.removePackage(Constants.PackageNameAsName);
		}
	}

	/**
	 * This method allows to add the corresponding jar file.
	 *
	 * @param source
	 *        Folder with files
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if files were added successfully (or not).
	 */
	protected boolean addAdditionalFiles(final String source) {
		if (source.isEmpty()) {
			return true;
		}
		try {
			File pathToAddFiles = CodeGenUtils.getResourceFromWithin(source);
			if (pathToAddFiles == null || !pathToAddFiles.exists()) {
				return true;
			}

			final File[] members = pathToAddFiles.listFiles();
			if (members == null) {
				Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY);
			}
			for (int i = 0; i < members.length; i++) {
				final File addFile = members[i];
				if (!addAddtionalFile(addFile)) {
					return false;
				}
			}
		} catch (IOException | CoreException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param fileToBeAdded
	 *        file that ought to be added to the dev project
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if file was added successfully (or not).
	 * @throws IOException
	 * @throws CoreException
	 */
	protected boolean addAddtionalFile(final File fileToBeAdded) throws IOException, CoreException {
		final IFolder libFolder = this.project.getFolder(Constants.pathsForLibrariesInDevProject);
		if (!libFolder.exists()) {
			libFolder.create(true, true, null);
		}

		final Path memberPath = fileToBeAdded.toPath();
		Files
			.copy(
				memberPath, new File(this.project
					.getProjectPath() + Constants.outerFileSeparator + Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + memberPath.getFileName()).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		final String filePath = fileToBeAdded.toString();
		final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
		if (Constants.JAR.equals(cutPath.substring(cutPath.indexOf(".")))) {
			if (!this.project.addJar(Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + fileToBeAdded.getName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method organizes imports for all generated files and the file, in which the call code for the generated classes is inserted.
	 *
	 * @param editor
	 *        Editor with the currently open file
	 * @throws CoreException
	 *         {@link DeveloperProject#refresh() refresh()} and {@link DeveloperProject#getPackagesOfProject(String) getPackagesOfProject()}
	 */
	protected void cleanUpProject(IEditorPart editor) throws CoreException {
		this.project.refresh();
		final ICompilationUnit[] generatedCUnits = this.project.getPackagesOfProject(Constants.PackageNameAsName).getCompilationUnits();
		boolean anyFileOpen = false;

		if(editor == null && generatedCUnits[0].getResource().getType() == IResource.FILE) {
			    IFile genClass = (IFile) generatedCUnits[0].getResource();
				IDE.openEditor(UIUtils.getCurrentlyOpenPage(), genClass);
				editor = UIUtils.getCurrentlyOpenPage().getActiveEditor();
				anyFileOpen = true;
		}

		final OrganizeImportsAction organizeImportsActionForAllFilesTouchedDuringGeneration = new OrganizeImportsAction(editor.getSite());
		final FormatAllAction faa = new FormatAllAction(editor.getSite());
		faa.runOnMultiple(generatedCUnits);
		organizeImportsActionForAllFilesTouchedDuringGeneration.runOnMultiple(generatedCUnits);

		if (anyFileOpen) {
			UIUtils.closeEditor(editor);
		}
		
		final ICompilationUnit openClass = JavaCore.createCompilationUnitFrom(UIUtils.getCurrentlyOpenFile(editor));
		organizeImportsActionForAllFilesTouchedDuringGeneration.run(openClass);
		faa.runOnMultiple(new ICompilationUnit[] { openClass });
		editor.doSave(null);
	}

}
