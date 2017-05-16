/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Stefan Krueger
 *
 */
package crossing.e1.configurator.codegeneration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.TreeSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.DeveloperProject;
import crossing.e1.configurator.utilities.Tuple;
import crossing.e1.configurator.utilities.Utils;
import crossing.e1.configurator.wizard.TaskSelectionPage;

/**
 * This class is responsible for generating code templates by performing an XSL transformation. Currently, Saxon is used as an XSLT- processor.
 *
 */
public class XSLBasedGenerator {

	private IFile currentFile;

	private int endingPositionForRunMethod = -1;
	private int endPosForImports = -1;
	private DeveloperProject project;
	private int startingPositionForRunMethod = -1;
	private int startPosForImports = -1;

	/***
	 * Generation of code templates using XSL template and Clafer instance.
	 *
	 * @param xmlInstanceFile
	 *        xml model that details the algorithm configuration chosen by the user.
	 * @param pathToFolderWithAdditionalResources
	 *        If additional files need to be generated into a developer's project, they are in this folder.
	 * @param xslFile
	 *        optional, can be used if not the default xsl stylesheet should be used.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if transformation successful/failed.
	 * @throws BadLocationException
	 *
	 */
	public boolean generateCodeTemplates(final File xmlInstanceFile, final String pathToFolderWithAdditionalResources, final File xslFile, final TaskSelectionPage taskSelectionPage) throws BadLocationException {
		try {
			// Check whether directories and templates/model exist
			final File claferOutputFiles = xmlInstanceFile != null && xmlInstanceFile.exists() ? xmlInstanceFile
				: Utils.getResourceFromWithin(Constants.pathToClaferInstanceFolder + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
			final File xslFiles = xslFile != null && xslFile.exists() ? xslFile : Utils.getResourceFromWithin(Constants.pathToXSLFile);
			if (!claferOutputFiles.exists() || !xslFiles.exists()) {
				Activator.getDefault().logError(Constants.FilesDoNotExistErrorMessage);
				return false;
			}
			// Perform actual transformation by calling XSLT processor.
			final String srcPath = this.project.getProjectPath() + Constants.innerFileSeparator + this.project.getSourcePath();
			final String temporaryOutputFile = srcPath + Constants.CodeGenerationCallFile;
			transform(claferOutputFiles, xslFiles, temporaryOutputFile);

			// Add additional resources like jar files
			if (!pathToFolderWithAdditionalResources.isEmpty()) {
				final File addResFolder = Utils.getResourceFromWithin(pathToFolderWithAdditionalResources);
				final File[] members = addResFolder.listFiles();
				if (members == null) {
					Activator.getDefault().logError("No directory for additional resources found.");
				}
				final IFolder libFolder = this.project.getFolder(Constants.pathsForLibrariesinDevProject);
				if (!libFolder.exists()) {
					libFolder.create(true, true, null);
				}
				for (int i = 0; i < members.length; i++) {
					final Path memberPath = members[i].toPath();
					Files.copy(memberPath,
						new File(this.project.getProjectPath() + Constants.outerFileSeparator + Constants.pathsForLibrariesinDevProject + Constants.outerFileSeparator + memberPath
							.getFileName()).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
					final String filePath = members[i].toString();
					final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
					if (".jar".equals(cutPath.substring(cutPath.indexOf(".")))) {
						if (!this.project.addJar(Constants.pathsForLibrariesinDevProject + Constants.outerFileSeparator + members[i].getName())) {
							return false;
						}
					}
				}
			}

			// If there is a java file opened in the editor, insert glue code
			// there, and remove temporary output file
			// Otherwise keep the output file
			// In any case, organize imports

			if (this.currentFile != null && this.currentFile.getProject().equals(taskSelectionPage.getSelectedProject())) {
				insertCallCodeIntoOpenFile(temporaryOutputFile);
			}

			else {
				this.project.refresh();
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IFile outputFile = this.project.getIFile(temporaryOutputFile);
				final IEditorPart editor = IDE.openEditor(page, outputFile);
				organizeImports(editor);
			}
			this.project.refresh();
		} catch (TransformerException | IOException | CoreException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
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
		final List<String> content = Files.readAllLines(Paths.get(filePath));
		final StringBuilder contentBuilder = new StringBuilder();
		for (final String el : content) {
			contentBuilder.append(el);
			contentBuilder.append(Constants.lineSeparator);
		}
		final String contentString = contentBuilder.toString();
		// Determine start and end position for relevant extract
		final ASTParser astp = ASTParser.newParser(AST.JLS8);
		astp.setSource(contentString.toCharArray());
		astp.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) astp.createAST(null);
		final ASTVisitor astVisitor = new ASTVisitor(true) {

			@Override
			public boolean visit(final ImportDeclaration node) {
				XSLBasedGenerator.this.startPosForImports = XSLBasedGenerator.this.startPosForImports < 0 ? node.getStartPosition() : XSLBasedGenerator.this.startPosForImports;
				final int endPos = node.getStartPosition() + node.getLength();
				XSLBasedGenerator.this.endPosForImports = XSLBasedGenerator.this.endPosForImports < endPos ? endPos : XSLBasedGenerator.this.endPosForImports;
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
			Activator.getDefault().logError(null, Constants.NoRunMethodFoundInTemporaryOutputFileErrorMessage);
			return null;
		}
		// Delete temporary output file as it is not needed anymore
		f.delete();
		// Get extract from content that we actually need
		return new String[] { contentString.substring(this.startPosForImports, this.endPosForImports), contentString.substring(this.startingPositionForRunMethod,
			this.endingPositionForRunMethod) };
	}

	/***
	 * Getter method for developer project the code is generated into
	 *
	 * @return developer project
	 */
	public DeveloperProject getDeveloperProject() {
		return this.project;
	}

	/**
	 * This method initializes the code template generator. If neither a java file is opened nor a project selected initialization fails.
	 *
	 * @return <Code>true</Code>/<Code>false</Code> if initialization successful/failed.
	 */
	public boolean initCodeGeneration(final IProject iproject) {
		this.project = new DeveloperProject(iproject);
		this.currentFile = Utils.getCurrentlyOpenFile();
		if (this.currentFile != null && Constants.JAVA.equals(this.currentFile.getFileExtension()) && this.currentFile.getProject().equals(iproject)) {
			// Get currently opened file to
			this.project = new DeveloperProject(this.currentFile.getProject());
		} else {
			// if no open file, get selected project
			final IProject iProject = iproject;
			if (iProject == null) {
				// if no project selected abort with error message
				Activator.getDefault().logError(null, Constants.NoFileandNoProjectOpened);
				return false;
			}
			Activator.getDefault().logInfo(Constants.NoFileOpenedErrorMessage);
			this.project = new DeveloperProject(iproject);
		}
		return true;
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
	private boolean insertCallCodeIntoOpenFile(final String temporaryOutputFile) throws BadLocationException, CoreException, IOException {
		final IEditorPart currentlyOpenPart = Utils.getCurrentlyOpenEditor();
		if (currentlyOpenPart == null || !(currentlyOpenPart instanceof AbstractTextEditor)) {
			Activator.getDefault().logError(null,
				"Could not open access the editor of the file. Therefore, an outputfile " + "containing calls to the generated classes in the Crypto package was generated.");
			return false;
		}
		final ITextEditor currentlyOpenEditor = (ITextEditor) currentlyOpenPart;
		final IDocument currentlyOpenDocument = currentlyOpenEditor.getDocumentProvider().getDocument(currentlyOpenEditor.getEditorInput());
		final ITextSelection cursorPosition = (ITextSelection) currentlyOpenPart.getSite().getSelectionProvider().getSelection();

		int cursorPos = cursorPosition.getOffset();
		final String docContent = currentlyOpenDocument.get();
		final TreeSet<Tuple<Integer, Integer>> methLims = new TreeSet<>();
		Tuple<Integer, Integer> classlims;
		classlims = new Tuple<>(0, 0);

		final ASTParser astp = ASTParser.newParser(AST.JLS8);
		astp.setSource(docContent.toCharArray());
		astp.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) astp.createAST(null);
		final ASTVisitor astVisitor = new ASTVisitor(true) {

			@Override
			public boolean visit(final MethodDeclaration node) {
				methLims.add(new Tuple<>(node.getStartPosition(), node.getStartPosition() + node.getLength()));
				return super.visit(node);
			}

			@Override
			public boolean visit(final TypeDeclaration node) {
				classlims.x = node.getStartPosition();
				classlims.y = node.getStartPosition() + node.getLength();
				return super.visit(node);
			}
		};
		cu.accept(astVisitor);

		// Check and correct cursor position
		// 1. case: cursor is outside the class -> set cursor position to end of
		// the class
		// 2. case: it is inside the class but also inside a method -> set
		// cursor position two right after the method
		if (classlims.x < cursorPos || cursorPos < classlims.y) {
			cursorPos = classlims.y - 2;
		} else {
			for (final Tuple<Integer, Integer> meth : methLims) {
				if (meth.x.intValue() > cursorPos) {
					break;
				}
				if (meth.x.intValue() <= cursorPos && meth.y.intValue() >= cursorPos) {
					cursorPos = meth.y.intValue() + 2;
					break;
				}
			}
		}
		final int imports = docContent.startsWith("package") ? docContent.indexOf(Constants.lineSeparator) : 0;
		final String[] callsForGenClasses = getCallsForGenClasses(temporaryOutputFile);
		currentlyOpenDocument.replace(cursorPos, 0, callsForGenClasses[1]);
		currentlyOpenDocument.replace(imports, 0, callsForGenClasses[0] + Constants.lineSeparator);
		this.project.refresh();
		organizeImports(currentlyOpenEditor);
		return true;
	}

	/**
	 * This method organizes imports for all generated files and the file, in which the call code for the generated classes is inserted.
	 *
	 * @param editor
	 *        of the currently open file.
	 * @throws CoreException
	 */
	private void organizeImports(final IEditorPart editor) throws CoreException {
		final OrganizeImportsAction organizeImportsActionForAllFilesTouchedDuringGeneration = new OrganizeImportsAction(editor.getSite());
		final ICompilationUnit[] compilationUnitsInCryptoPackage = this.project.getPackagesOfProject(Constants.PackageName).getCompilationUnits();
		for (int i = 0; i < compilationUnitsInCryptoPackage.length; i++) {
			organizeImportsActionForAllFilesTouchedDuringGeneration.run(compilationUnitsInCryptoPackage[i]);
		}
		organizeImportsActionForAllFilesTouchedDuringGeneration.run(JavaCore.createCompilationUnitFrom(Utils.getCurrentlyOpenFile(editor)));
		editor.doSave(null);
	}

	protected void setPosForClassDecl(final int start, final int end) {
		// classlims = new Tuple<Integer, Integer>(start, end);
	}

	private void setPosForRunMethod(final int start, final int end) {

		this.startingPositionForRunMethod = start;
		this.endingPositionForRunMethod = end;
	}

	/**
	 * Performs the XSL-Transformation
	 *
	 * @param sourceFile
	 *        xmlFile that contains the Clafer Instance
	 * @param xsltFile
	 *        XSL Template
	 * @param resultDir
	 *        Path to temporary output file
	 * @throws TransformerException
	 *         see {@link javax.xml.transform.Transformer#transform(javax.xml.transform.Source, javax.xml.transform.Result) transform()}
	 */
	private void transform(final File sourceFile, final File xsltFile, final String resultDir) throws TransformerException {
		// TODO: currently, only one xml file is used
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		final TransformerFactory tFactory = TransformerFactory.newInstance();
		final Transformer transformer = tFactory.newTransformer(new StreamSource(xsltFile));
		transformer.transform(new StreamSource(sourceFile), new StreamResult(new File(resultDir)));
	}
}
