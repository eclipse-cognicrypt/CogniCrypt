/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.FileHelper;

/**
 * This class is responsible for generating code templates by performing an XSL transformation. Currently, Saxon is used as an XSLT- processor.
 * 
 * @author Stefan Krueger
 */
public class XSLBasedGenerator extends CodeGenerator {

	final private File xslFile;

	/**
	 * Constructor to initialize the code template generator.
	 * 
	 * @param targetProject
	 *        Project code is generated into.
	 * @param pathToXSLFile
	 *        Path to the XSL file is read from the Tasks.json file instead of a constant.
	 */

	public XSLBasedGenerator(final IProject targetProject, final String pathToXSLFile) {
		super(targetProject);
		xslFile = CodeGenUtils.getResourceFromWithin(pathToXSLFile);
	}

	public boolean generateCodeTemplates(Configuration chosenConfig, final String pathToAdditionalResources) {
		try {
			// Check whether directories and templates/model exist
			final File configFile = chosenConfig.persistConf();
			if (!configFile.exists()) {
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
			transform(configFile, temporaryOutputFile);
			chosenConfig.deleteConfFromDisk();

			// Trim Output.java
			FileHelper.trimFile(temporaryOutputFile);

			// Add additional resources like jar files
			if (!addAdditionalFiles(pathToAdditionalResources)) {
				return false;
			}
			for (String customProvider : chosenConfig.getProviders()) {
				if (!addAddtionalFile(CodeGenUtils.getResourceFromWithin(Constants.providerPath + "/" + customProvider + Constants.JAR))) {
					return false;
				}
			}

			final IFile currentlyOpenFile = CodeGenUtils.getCurrentlyOpenFile();
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
	public void transform(final File sourceFile, final String resultDir) throws TransformerException {
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		final Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslFile));
		transformer.transform(new StreamSource(sourceFile), new StreamResult(new File(resultDir)));
	}

}
