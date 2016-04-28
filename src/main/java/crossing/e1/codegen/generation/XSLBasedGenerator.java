/**
 * Copyright 2015 Technische Universität Darmstadt
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
package crossing.e1.codegen.generation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
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

import crossing.e1.codegen.DeveloperProject;
import crossing.e1.codegen.Utils;
import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;

/**
 * This class is responsible for generating code templates by performing an XSL transformation. Currently, Saxon is used
 * as an XSLT- processor.
 *
 */
public class XSLBasedGenerator {

	private DeveloperProject project;

	private boolean fileOpened = false;
	private IFile currentFile;

	private int startPosForImports = -1;

	private int endPosForImports = -1;

	private int startingPositionForRunMethod = -1;

	private int endingPositionForRunMethod = -1;
	
//	public boolean generateCodeTemplates() {
//	
//		final File claferOutputFiles;
//		final File xslFiles;
//		try {
//			claferOutputFiles = Utils.resolveResourcePathToFile(Constants.pathToClaferInstanceFolder + Constants.fileSeparator + Constants.pathToClaferInstanceFile);
//			xslFiles = Utils.resolveResourcePathToFile(Constants.pathToXSLFile);
//			
//		} catch (URISyntaxException | IOException e) {
//			Activator.getDefault().logError(Constants.FilesDoNotExistErrorMessage);
//		}
//		
//		return generateCodeTemplates();
//	}
		
	/**
	 * Generation of code templates using XSL template and Clafer instance.
	 *
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if transformation successful/failed.
	 */
	public boolean generateCodeTemplates(File claferOutput, File xslFile) {
		// TODO: Android
		// TODO: extract init to Configurator
		if (!initCodeGeneration()) {
			return false;
		}
		try {
			// Check whether directories and templates/model exist
			final File claferOutputFiles = claferOutput.exists() ? claferOutput : Utils.resolveResourcePathToFile(Constants.pathToClaferInstanceFolder + Constants.fileSeparator + Constants.pathToClaferInstanceFile);
			final File xslFiles = xslFile.exists() ? xslFile : Utils.resolveResourcePathToFile(Constants.pathToXSLFile);
			if (!claferOutputFiles.exists() || !xslFiles.exists()) {
				Activator.getDefault().logError(Constants.FilesDoNotExistErrorMessage);
				return false;
			}

			// Perform actual transformation by calling XSLT processor.
			final String srcPath = this.project.getProjectPath() + Constants.fileSeparator + this.project.getSourcePath();
			final String temporaryOutputFile = srcPath + Constants.CodeGenerationCallFile;
			transform(claferOutputFiles, xslFiles, temporaryOutputFile);

			// If there is a java file opened in the editor, insert call code there, and remove temporary output file
			// else keep the output file
			// In any case, organize imports
			if (this.fileOpened) {
				insertCallCodeIntoOpenFile(temporaryOutputFile);
			} else {
				this.project.refresh();
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IFile outputFile = this.project.getIFile(temporaryOutputFile);
				final IEditorPart editor = IDE.openEditor(page, outputFile);

				organizeImports(editor);
			}
			this.project.refresh();
		} catch (TransformerException | IOException | URISyntaxException | CoreException | BadLocationException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 * This method initializes the code template generator. If neither a java file is opened nor a project selected
	 * initialization fails.
	 *
	 * @return <Code>true</Code>/<Code>false</Code> if initialization successful/failed.
	 */
	public boolean initCodeGeneration() {
		this.currentFile = Utils.getCurrentlyOpenFile();
		this.fileOpened = this.currentFile != null;
		if (this.currentFile != null && Constants.JAVA.equals(this.currentFile.getFileExtension())) {
			// Get currently opened file to
			this.project = new DeveloperProject(this.currentFile.getProject());
		} else {
			// if no open file, get selected project
			final IProject iproject = Utils.getIProjectFromSelection();
			if (iproject == null) {
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
	 * This method extracts the glue code with the calls to the generated classes from the temporary output file.
	 *
	 * @param filePath
	 *            Path to temporary output file.
	 * @return Glue code from temporary output file which calls the generated files.
	 * @throws IOException
	 *             See {@link java.nio.file.Files#readAllLines(Path) readAllLines()}
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
				XSLBasedGenerator.this.startPosForImports = XSLBasedGenerator.this.startPosForImports < 0 ? node.getStartPosition()
						: XSLBasedGenerator.this.startPosForImports;
				final int endPos = node.getStartPosition() + node.getLength();
				XSLBasedGenerator.this.endPosForImports = XSLBasedGenerator.this.endPosForImports < endPos ? endPos
						: XSLBasedGenerator.this.endPosForImports;
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
		return new String[] { contentString.substring(this.startPosForImports, this.endPosForImports), contentString
				.substring(this.startingPositionForRunMethod, this.endingPositionForRunMethod) };
	}

	/**
	 * If a file was open when the code generation was started, this method inserts the glue code that calls the
	 * generated classes directly into the opened file and removes the temporary output file. If no file was open this
	 * method is skipped and the temporary output file is not removed.
	 *
	 * @param temporaryOutputFile
	 *            Path to temporary output file.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if insertion successful/failed.
	 * @throws BadLocationException
	 *             See {@link org.eclipse.jface.text.IDocument#replace(int, int, String) replace()}
	 * @throws IOException
	 *             See {@link crossing.e1.codegen.generation.XSLBasedGenerator#getCallsForGenClasses(String)
	 *             getCallsForGenClasses()}
	 * @throws CoreException
	 *             See {@link DeveloperProject.crossing.opencce.cryptogen.CryptoProject#refresh() refresh()}
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
		final String docContent = currentlyOpenDocument.get();
		final int imports = docContent.startsWith("package") ? docContent.indexOf(Constants.lineSeparator) : 0;
		final String[] callsForGenClasses = getCallsForGenClasses(temporaryOutputFile);
		currentlyOpenDocument.replace(cursorPosition.getOffset(), 0, callsForGenClasses[1]);
		currentlyOpenDocument.replace(imports, 0, callsForGenClasses[0] + Constants.lineSeparator);

		this.project.refresh();
		organizeImports(currentlyOpenEditor);
		return true;
	}

	/**
	 * This method organizes imports for all generated files and the file, in which the call code for the generated
	 * classes is inserted.
	 *
	 * @param editor
	 *            of the currently open file.
	 * @throws CoreException
	 */
	private void organizeImports(final IEditorPart editor) throws CoreException {
		final OrganizeImportsAction organizeImportsActionForAllFilesTouchedDuringGeneration = new OrganizeImportsAction(editor.getSite());

		final ICompilationUnit[] compilationUnitsInCryptoPackage = this.project.getPackagesOfProject(Constants.PackageName)
				.getCompilationUnits();
		for (int i = 0; i < compilationUnitsInCryptoPackage.length; i++) {
			organizeImportsActionForAllFilesTouchedDuringGeneration.run(compilationUnitsInCryptoPackage[i]);
		}
		organizeImportsActionForAllFilesTouchedDuringGeneration.run(JavaCore.createCompilationUnitFrom(Utils.getCurrentlyOpenFile(editor)));
		editor.doSave(null);
	}

	protected void setPosForRunMethod(final int start, final int end) {
		this.startingPositionForRunMethod = start;
		this.endingPositionForRunMethod = end;
	}

	/**
	 * Performs the XSL-Transformation
	 *
	 * @param sourceFile
	 *            xmlFile that contains the Clafer Instance
	 * @param xsltFile
	 *            XSL Template
	 * @param resultDir
	 *            Path to temporary output file
	 * @throws TransformerException
	 *             see
	 *             {@link javax.xml.transform.Transformer#transform(javax.xml.transform.Source, javax.xml.transform.Result)
	 *             transform()}
	 */
	private static void transform(final File sourceFile, final File xsltFile, final String resultDir) throws TransformerException {
		// TODO: currently, only one xml file is used
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		final TransformerFactory tFactory = TransformerFactory.newInstance();
		final Transformer transformer = tFactory.newTransformer(new StreamSource(xsltFile));
		transformer.transform(new StreamSource(sourceFile), new StreamResult(new File(resultDir)));
	}
}
