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
 * @author Ram
 *
 */

package de.cognicrypt.codegenerator.utilities;

import java.util.Properties;

import de.cognicrypt.codegenerator.Constants;

public interface Labels {

	Properties prop = new PropertiesFileReader(Constants.pathToPropertyfiles).getProperties();
	// Strings for GUI elements
	static final String ALGORITHM_SELECTION_PAGE = prop.getProperty("ALGORITHM_SELECTION_PAGE");
	static final String DESCRIPTION_INSTANCE_LIST_PAGE = prop.getProperty("DESCRIPTION_INSTANCE_LIST_PAGE");
	static final String instanceList = prop.getProperty("LABEL1");
	static final String SELECT_TASK = prop.getProperty("SELECT_TASK");
	static final String TASK_LIST = prop.getProperty("TASK_LIST");
	static final String DESCRIPTION_TASK_SELECTION_PAGE = prop.getProperty("DESCRIPTION_TASK_SELECTION_PAGE");
	static final String DESCRIPTION_VALUE_SELECTION_PAGE = prop.getProperty("DESCRIPTION_VALUE_SELECTION_PAGE");
	static final String PROPERTIES = prop.getProperty("PROPERTIES");
	static final String SELECT_PROPERTIES = prop.getProperty("SELECT_PROPERTIES");
}
