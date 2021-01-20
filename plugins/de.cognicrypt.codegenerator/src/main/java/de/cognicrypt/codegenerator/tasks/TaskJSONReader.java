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

package de.cognicrypt.codegenerator.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.CodeGenerators;

public class TaskJSONReader {

	private static volatile List<Task> tasks;

	/**
	 * Getter method for tasks.
	 *
	 * @return List of Tasks in JSON file
	 */
	public static List<Task> getTasks() {

		if (TaskJSONReader.tasks == null) {
			try {
				final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(Constants.jsonTaskFile)));
				final Gson gson = new Gson();
				TaskJSONReader.tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());
				reader.close();

				for (Task t : TaskJSONReader.tasks) {
					t.setQuestionsJSONFile(Constants.rsrcPath + "TaskDesc" + Constants.innerFileSeparator + t.getName() + ".json");
					t.setAdditionalResources(Constants.rsrcPath + "AdditionalResources" + Constants.innerFileSeparator + t.getName());

					if (t.getCodeGen() == CodeGenerators.XSL) {
						t.setCodeTemplate(Constants.rsrcPath + "XSLTemplates" + Constants.innerFileSeparator + t.getName() + ".xsl");
						t.setModelFile(Constants.rsrcPath + "ClaferModel" + Constants.innerFileSeparator + t.getName() + ".js");
					} else if (t.getCodeGen() == CodeGenerators.CrySL) {
						t.setCodeTemplate(Constants.codeTemplateFolder + t.getName());
					}
				}

			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			} catch (final IOException e) {
				Activator.getDefault().logError(e);
			}
		}

		return TaskJSONReader.tasks;
	}

}
