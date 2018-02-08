package de.cognicrypt.codegenerator.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap.SimpleEntry;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.utilities.ComparableEntry;
import de.cognicrypt.codegenerator.utilities.FileHelper;
import de.cognicrypt.codegenerator.utilities.Utils;

/**
 * This class is responsible for generating code templates by performing an XSL transformation. Currently, Saxon is used as an XSLT- processor.
 *
 */
public class XSLBasedGenerator {

	private int endingPositionForRunMethod = -1;
	private int endPosForImports = -1;
	private final DeveloperProject project;
	private int startingPositionForRunMethod = -1;
	private int startPosForImports = -1;
	private String provider;

	/**
	 * Constructor to initialize the code template generator.
	 * 
	 * @param targetProject
	 *        Project code is generated into.
	 * @param provider
	 *        Provider used for selected algorithm.
	 */
	public XSLBasedGenerator(final IProject targetProject, final String provider) {
		this.project = new DeveloperProject(targetProject);
		this.provider = provider;
	}

	/**
	 * Generation of code templates using XSL template and Clafer instance.
	 *
	 * @param xmlInstanceFile
	 *        xml model that details the algorithm configuration chosen by the user.
	 * @param pathToFolderWithAdditionalResources
	 *        If additional files need to be generated into a developer's project, they are in this folder.
	 * @param pathToXSLFile
	 *        path to the XSL file is read from the Tasks.json file instead of a constant.
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if transformation successful/failed.
	 * @throws BadLocationException
	 *
	 */
	public boolean generateCodeTemplates(final File xmlInstanceFile, final String pathToFolderWithAdditionalResources, final String providerName, final String pathToXSLFile) {
		try {
			// Check whether directories and templates/model exist
			final File claferOutputFiles = xmlInstanceFile != null && xmlInstanceFile.exists() ? xmlInstanceFile
				: Utils.getResourceFromWithin(Constants.pathToClaferInstanceFolder + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile);
			final File xslFiles = Utils.getResourceFromWithin(pathToXSLFile);
			if (!claferOutputFiles.exists() || !xslFiles.exists()) {
				Activator.getDefault().logError(Constants.FilesDoNotExistErrorMessage);
				return false;
			}

			final String srcPath = this.project.getProjectPath() + Constants.innerFileSeparator + this.project.getSourcePath();
			String temporaryOutputFile = srcPath + Constants.CodeGenerationCallFile;

			// If Output.java exists create OutputTemp.java
			Path path = Paths.get(temporaryOutputFile);
			boolean tempFlag;
			if (Files.exists(path)) {
				StringBuilder sb = new StringBuilder(temporaryOutputFile);
				sb.insert(temporaryOutputFile.length() - 5, Constants.TempSuffix);
				temporaryOutputFile = sb.toString();
				Activator.getDefault().logInfo(Constants.CreateOutputTemp);
				tempFlag = true;
			} else {
				Activator.getDefault().logInfo(Constants.CreateOutput);
				tempFlag = false;
			}

			// Perform actual transformation by calling XSLT processor.
			transform(claferOutputFiles, xslFiles, temporaryOutputFile);

			// Trim Output.java
			FileHelper.trimFile(temporaryOutputFile);

			// Add additional resources like jar files
			if (!addAdditionalJarFiles(pathToFolderWithAdditionalResources)) {
				return false;
			}
			if (!addAdditionalJarFiles(providerName)) {
				return false;
			}

			final IFile currentlyOpenFile = Utils.getCurrentlyOpenFile();
			if (currentlyOpenFile != null && project.equals(currentlyOpenFile.getProject())) {
				Activator.getDefault().logInfo(Constants.OpenFile + currentlyOpenFile.getName());

				if (FileHelper.checkFileForString(currentlyOpenFile.getRawLocation().toOSString(), Constants.AuthorTag)) {
					Activator.getDefault().logInfo(Constants.ContainsAuthorTag + currentlyOpenFile.getName());
					insertCallCodeIntoFile(temporaryOutputFile, true, true, tempFlag);
					removeCryptoPackageIfEmpty();
				} else {
					Activator.getDefault().logInfo(Constants.ContainsNotAuthorTag + currentlyOpenFile.getName());
					insertCallCodeIntoFile(temporaryOutputFile, true, false, tempFlag);
					removeCryptoPackageIfEmpty();
				}
			} else {
				if (tempFlag) {
					Activator.getDefault().logInfo(Constants.CloseFile);
					insertCallCodeIntoFile(temporaryOutputFile, false, false, tempFlag);
					removeCryptoPackageIfEmpty();
				}
				this.project.refresh();
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IFile outputFile = this.project.getIFile(temporaryOutputFile);
				final IEditorPart editor = IDE.openEditor(page, outputFile);
				cleanUpProject(editor);

			}
			this.project.refresh();
		} catch (TransformerException | IOException | CoreException | BadLocationException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 * Removes crypto package from developer project.
	 * 
	 * @throws CoreException
	 *         {@link DeveloperProject#getPackagesOfProject(String)} and {@link IPackageFragment#getCompilationUnit()}
	 */
	private void removeCryptoPackageIfEmpty() throws CoreException {
		final IPackageFragment cryptoPackage = this.project.getPackagesOfProject(Constants.PackageName);
		if (cryptoPackage.getCompilationUnits().length == 0) {
			this.project.removePackage(Constants.PackageName);
		}
	}

	/**
	 * This method allows to add the corresponding jar file.
	 *
	 * @param source
	 *        Location where
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if transformation successful/failed.
	 */
	private boolean addAdditionalJarFiles(String source) {
		try {

			if (!source.isEmpty() && !source.equals(Constants.JCA)) {
				final String sourceFolder = "src/";
				if (!source.startsWith(sourceFolder)) {
					source = Constants.providerPath;
				}
				final File[] members = Utils.getResourceFromWithin(source).listFiles();
				if (members == null) {
					Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY);
				}
				final IFolder libFolder = this.project.getFolder(Constants.pathsForLibrariesInDevProject);
				if (!libFolder.exists()) {
					libFolder.create(true, true, null);
				}
				boolean jarIsAdded = false;
				for (int i = 0; i < members.length && !jarIsAdded; i++) {

					if (members[i].getName().equalsIgnoreCase(source + Constants.JAR) || source.startsWith(sourceFolder)) {
						final Path memberPath = members[i].toPath();
						Files.copy(memberPath, new File(this.project
							.getProjectPath() + Constants.outerFileSeparator + Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + memberPath.getFileName())
								.toPath(),
							StandardCopyOption.REPLACE_EXISTING);
						final String filePath = members[i].toString();
						final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
						if (Constants.JAR.equals(cutPath.substring(cutPath.indexOf(".")))) {
							if (!this.project.addJar(Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + members[i].getName())) {
								return false;
							}
						}
						jarIsAdded = true;
					}
				}
			}
		} catch (IOException | CoreException e) {
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
		final String fileContent = String.join(Constants.lineSeparator, Files.readAllLines(Paths.get(filePath)));
		// Determine start and end position for relevant extract
		final ASTParser astp = ASTParser.newParser(AST.JLS8);
		astp.setSource(fileContent.toCharArray());
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
			Activator.getDefault().logError(Constants.NoRunMethodFoundInTemporaryOutputFileErrorMessage);
			return null;
		}
		// Delete temporary output file as it is not needed anymore
		f.delete();
		// Get extract from content that we actually need
		return new String[] { fileContent.substring(this.startPosForImports, this.endPosForImports), fileContent.substring(this.startingPositionForRunMethod,
			this.endingPositionForRunMethod) };
	}

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
	private boolean insertCallCodeIntoFile(final String temporaryOutputFile, boolean openFileFlag, boolean authorFlag, boolean tempFlag) throws BadLocationException, CoreException, IOException {

		if (!((openFileFlag && authorFlag) || !openFileFlag)) {
			IDE.openEditor(Utils.getCurrentlyOpenPage(), Utils.getCurrentlyOpenFile());
		}
		IEditorPart currentlyOpenPart = Utils.getCurrentlyOpenEditor();
		if (currentlyOpenPart == null || !(currentlyOpenPart instanceof AbstractTextEditor)) {
			Activator.getDefault().logError(null,
				"Could not open access the editor of the file. Therefore, an outputfile containing calls to the generated classes in the Crypto package was generated.");
			return false;
		}

		ITextEditor currentlyOpenEditor = (ITextEditor) currentlyOpenPart;
		IDocument currentlyOpenDocument = currentlyOpenEditor.getDocumentProvider().getDocument(currentlyOpenEditor.getEditorInput());
		final String docContent = currentlyOpenDocument.get();
		final TreeSet<SimpleEntry<Integer, Integer>> methLims = new TreeSet<>();
		final SimpleEntry<Integer, SimpleEntry<Integer, Integer>> classlims = new SimpleEntry<>(0, null);

		final ASTParser astp = ASTParser.newParser(AST.JLS8);
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

		final int imports = docContent.startsWith("package") ? docContent.indexOf(Constants.lineSeparator) : 0;
		final String[] callsForGenClasses = getCallsForGenClasses(temporaryOutputFile);
		currentlyOpenDocument.replace(cursorPos, 0, callsForGenClasses[1]);
		currentlyOpenDocument.replace(imports, 0, callsForGenClasses[0] + Constants.lineSeparator);
		currentlyOpenEditor.doSave(null);
		cleanUpProject(currentlyOpenEditor);
		return true;
	}

	/**
	 * This method organizes imports for all generated files and the file, in which the call code for the generated classes is inserted.
	 *
	 * @param editor
	 *        Editor with the currently open file
	 * @throws CoreException {@link DeveloperProject#refresh() refresh()} and {@link DeveloperProject#getPackagesOfProject(String) getPackagesOfProject()}
	 */
	private void cleanUpProject(final IEditorPart editor) throws CoreException {

		this.project.refresh();

		final OrganizeImportsAction organizeImportsActionForAllFilesTouchedDuringGeneration = new OrganizeImportsAction(editor.getSite());
		final FormatAllAction faa = new FormatAllAction(editor.getSite());
		final ICompilationUnit[] generatedCUnits = this.project.getPackagesOfProject(Constants.PackageName).getCompilationUnits();
		faa.runOnMultiple(generatedCUnits);
		organizeImportsActionForAllFilesTouchedDuringGeneration.runOnMultiple(generatedCUnits);

		final ICompilationUnit openClass = JavaCore.createCompilationUnitFrom(Utils.getCurrentlyOpenFile(editor));
		organizeImportsActionForAllFilesTouchedDuringGeneration.run(openClass);
		faa.runOnMultiple(new ICompilationUnit[] { openClass });
		editor.doSave(null);
	}

	private void setPosForRunMethod(final int start, final int end) {
		this.startingPositionForRunMethod = start;
		this.endingPositionForRunMethod = end;
	}

	/**
	 * Performs the XSL-Transformation.
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
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		final Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
		transformer.transform(new StreamSource(sourceFile), new StreamResult(new File(resultDir)));
	}

	public String getProvider() {
		return this.provider;
	}

	public void setProvider(final String provider) {
		this.provider = provider;
	}
}
