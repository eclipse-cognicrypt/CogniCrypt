/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

<<<<<<< HEAD
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
import org.eclipse.core.resources.IResource;
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
import de.cognicrypt.utils.FileUtils;

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
	 * @param iResource
	 *        Project code is generated into.
	 * @param pathToXSLFile
	 *        Path to the XSL file is read from the Tasks.json file instead of a constant.
	 */

	public XSLBasedGenerator(final IResource iResource, final String pathToXSLFile) {
		super(iResource);
		this.xslFile = CodeGenUtils.getResourceFromWithin(pathToXSLFile);
	}

	@Override
	public boolean generateCodeTemplates(final Configuration chosenConfig, final String pathToAdditionalResources) {
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
			final Path path = Paths.get(temporaryOutputFile);
			boolean tempFlag;
			if (Files.exists(path)) {
				final StringBuilder sb = new StringBuilder(temporaryOutputFile);
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
			if (!Activator.getDefault().getPreferenceStore().getBoolean(Constants.PERSIST_CONFIG)) {
				chosenConfig.deleteConfFromDisk();
			}

			// Trim Output.java
			FileUtils.trimFile(temporaryOutputFile);

			// Add additional resources like jar files
			if (!addAdditionalFiles(pathToAdditionalResources)) {
				return false;
			}
			for (final String customProvider : chosenConfig.getProviders()) {
				if (!addAddtionalFile(CodeGenUtils.getResourceFromWithin(Constants.providerPath + "/" + customProvider + Constants.JAR))) {
					return false;
				}
			}

			if (targetFile != null && this.project.equals(targetFile.getProject())) {
				Activator.getDefault().logInfo(Constants.OpenFile + targetFile.getName());

				if (FileUtils.checkFileForString(targetFile.getRawLocation().toOSString(), Constants.AuthorTag)) {
					Activator.getDefault().logInfo(Constants.ContainsAuthorTag + targetFile.getName());
					insertCallCodeIntoFile(temporaryOutputFile, true, true, tempFlag);
					removeCryptoPackageIfEmpty();
				} else {
					Activator.getDefault().logInfo(Constants.ContainsNotAuthorTag + targetFile.getName());
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
		final Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(this.xslFile));
		transformer.transform(new StreamSource(sourceFile), new StreamResult(new File(resultDir)));
	}

}
