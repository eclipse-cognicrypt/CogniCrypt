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
 * This class is responsible for generating code templates by performing an XSL transformation. 
 * Currently, Saxon is used as an XSLT- processor.
 * 
 */
public class XSLBasedGenerator {
	
	private DeveloperProject project;
	private boolean fileOpened = false;
	private IFile currentFile;
	
	/**
	 * This method initializes the code template generator. If neither a java file is opened nor a project selected initialization fails.
	 * @return <Code>true</Code>/<Code>false</Code> if initialization successful/failed. 
	 */
	public boolean init() {
		currentFile = Utils.getCurrentlyOpenFile();
		fileOpened = currentFile != null;
		if (currentFile != null && "java".equals(currentFile.getFileExtension())) {
			//Get currently opened file to 
			project = new DeveloperProject(currentFile.getProject());
		} else {
			//if no open file, get selected project
			IProject iproject = Utils.getIProjectFromSelection();
			if (iproject == null) {
				//if no project selected abort with error message
				Activator.getDefault().logError(null, Constants.NoFileandNoProjectOpened);
				return false;
			}
			Activator.getDefault().logInfo(Constants.NoFileOpenedErrorMessage);
		    project = new DeveloperProject(iproject);
		}
		return true;
	}
	
	/**
	 * Generation of code templates using XSL template and Clafer instance.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if transformation successful/failed.
	 */
	public boolean generateCodeTemplates() {
		//TODO: Android
		//TODO: extract init to Configurator
		if (!init()) {
			return false;
		}
		try {
			//Check whether directories and templates/model exist
			File claferOutputFiles = Utils.resolveResourcePathToFile(Constants.folderOfClaferInstance);
			File xslFiles = Utils.resolveResourcePathToFile(Constants.folderOfXSLTemplates);
			if (!Files.exists(claferOutputFiles.toPath()) || !Files.exists(xslFiles.toPath())) {
				Activator.getDefault().logInfo(Constants.FilesDoNotExistErrorMessage);
				return false;
			}
			
			//Perform actual transformation by calling XSLT processor.
			final String srcPath = project.getProjectPath() + Constants.fileSeparator + project.getSourcePath();
			final String temporaryOutputFile = srcPath + Constants.CodeGenerationCallFile;
			transform(claferOutputFiles, xslFiles,  temporaryOutputFile);
			
			//If there is a java file opened in the editor, insert call code there, and remove temporary output file
			//else keep the output file
			//In any case, organize imports 
			if (fileOpened) {
				insertCallCodeIntoOpenFile(temporaryOutputFile);
			} else {
				project.refresh();
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IFile outputFile = project.getIFile(temporaryOutputFile);
				IEditorPart editor = IDE.openEditor(page, outputFile);
				
				organizeImports(editor);
			}
			project.refresh();
		} catch (TransformerException | IOException | URISyntaxException |  CoreException | BadLocationException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 * If a file was open when the code generation was started, this method inserts the glue code that calls the generated classes directly into the opened file and removes the temporary output file.
	 * If no file was open this method is skipped and the temporary output file is not removed. 
	 * @param temporaryOutputFile Path to temporary output file.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if insertion successful/failed.
	 * @throws BadLocationException See {@link org.eclipse.jface.text.IDocument#replace(int, int, String) replace()}
	 * @throws IOException See {@link crossing.e1.codegen.generation.XSLBasedGenerator#getCallsForGenClasses(String) getCallsForGenClasses()}
	 * @throws CoreException See {@link DeveloperProject.crossing.opencce.cryptogen.CryptoProject#refresh() refresh()}
	 */
	private boolean insertCallCodeIntoOpenFile(final String temporaryOutputFile) throws  BadLocationException, CoreException, IOException {
		IEditorPart currentlyOpenPart = Utils.getCurrentlyOpenEditor();
		if (currentlyOpenPart == null || !(currentlyOpenPart instanceof AbstractTextEditor)) {
			Activator.getDefault().logError(null, "Could not open access the editor of the file. Therefore, an outputfile "
					+ "containing calls to the generated classes in the Crypto package was generated.");
			return false;
		}
		ITextEditor currentlyOpenEditor = (ITextEditor) currentlyOpenPart;
		IDocument currentlyOpenDocument = currentlyOpenEditor.getDocumentProvider().getDocument(currentlyOpenEditor.getEditorInput());
		ITextSelection cursorPosition = (ITextSelection) currentlyOpenPart.getSite().getSelectionProvider().getSelection();
		final String docContent = currentlyOpenDocument.get();
		int imports = docContent.startsWith("package") ? docContent.indexOf(Constants.lineSeparator) : 0;
		final String[] callsForGenClasses = getCallsForGenClasses(temporaryOutputFile);
		currentlyOpenDocument.replace(cursorPosition.getOffset(), 0, callsForGenClasses[1]);
		currentlyOpenDocument.replace(imports, 0, callsForGenClasses[0] + Constants.lineSeparator);
		
		project.refresh();
		organizeImports(currentlyOpenEditor);
		return true;
	}

	/**
	 * This method organizes imports for all generated files and the file, in which the call code for the generated classes is inserted.
	 * @param editor of the currently open file.
	 * @throws CoreException 
	 */
	private void organizeImports(IEditorPart editor) throws CoreException {
		OrganizeImportsAction organizeImportsActionForAllFilesTouchedDuringGeneration = new OrganizeImportsAction(editor.getSite());
		
		ICompilationUnit[] compilationUnitsInCryptoPackage = project.getPackagesOfProject(Constants.Packagname).getCompilationUnits();
		for (int i = 0; i < compilationUnitsInCryptoPackage.length; i++) {
			organizeImportsActionForAllFilesTouchedDuringGeneration.run(compilationUnitsInCryptoPackage[i]);
		}
		organizeImportsActionForAllFilesTouchedDuringGeneration.run(JavaCore.createCompilationUnitFrom(Utils.getCurrentlyOpenFile(editor)));
		editor.doSave(null);
	}

	/**
	 * This method extracts the glue code with the calls to the generated classes from the temporary output file.
	 * @param filePath Path to temporary output file.
	 * @return Glue code from temporary output file which calls the generated files.
	 * @throws IOException See {@link java.nio.file.Files#readAllLines(Path) readAllLines()}
	 */
	private String[] getCallsForGenClasses(String filePath) throws IOException {
		//Checks whether file exists
		File f = new File(filePath);
		if (!f.exists()) {
			Activator.getDefault().logError(null, Constants.NoTemporaryOutputFile);
			return null;
		}
		
		//Retrieve complete content from file
		List<String> content = Files.readAllLines(Paths.get(filePath));
		StringBuilder contentBuilder = new StringBuilder();
		for (String el : content) {
			contentBuilder.append(el); 
			contentBuilder.append(Constants.lineSeparator);
		}
		final String contentString = contentBuilder.toString();
		
		//Determine start and end position for relevant extract
		final ASTParser astp = ASTParser.newParser(AST.JLS8);
		astp.setSource(contentString.toCharArray());
		astp.setKind(ASTParser.K_COMPILATION_UNIT);	 
		final CompilationUnit cu = (CompilationUnit) astp.createAST(null);	
		ASTVisitor astVisitor = new ASTVisitor(true) {

			@Override
			public boolean visit(ImportDeclaration node) {
				startPosForImports = startPosForImports < 0 ? node.getStartPosition() : startPosForImports;
				final int endPos = node.getStartPosition() + node.getLength();
				endPosForImports = endPosForImports < endPos ? endPos : endPosForImports;
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				if (Constants.NameOfTemporaryMethod.equals(node.getName().toString())) {
					setPosForRunMethod(node.getStartPosition(), node.getStartPosition() + node.getLength());
				}
				return super.visit(node);
			}
		};
		cu.accept(astVisitor);
		
		if (startingPositionForRunMethod < 0 || endingPositionForRunMethod < 0) {
			Activator.getDefault().logError(null, Constants.NoRunMethodFoundInTemporaryOutputFileErrorMessage);
			return null;
		}
		
		//Delete temporary output file as it is not needed anymore
		f.delete();
		
		//Get extract from content that we actually need
		return new String[] {contentString.substring(startPosForImports, endPosForImports), contentString.substring(startingPositionForRunMethod, endingPositionForRunMethod)};
	}
	
	private int startPosForImports = -1;
	private int endPosForImports = -1;
	
	private int startingPositionForRunMethod = -1;
	private int endingPositionForRunMethod = -1;
	
	protected void setPosForRunMethod(int start, int end) {
		startingPositionForRunMethod = start;
		endingPositionForRunMethod = end;
	}
	
	/**
	 * Performs the XSL-Transformation
	 * @param sourceFile xmlFile that contains the Clafer Instance
	 * @param xsltFile XSL Template
	 * @param resultDir Path to temporary output file
	 * @throws TransformerException see {@link javax.xml.transform.Transformer#transform(javax.xml.transform.Source, javax.xml.transform.Result) transform()}
	 */
	private static void transform(File sourceFile, File xsltFile, String resultDir) throws TransformerException  {
		// TODO: currently, only one xml file is used
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(xsltFile));
        transformer.transform(new StreamSource(sourceFile), new StreamResult(new File(resultDir)));
    }
}
