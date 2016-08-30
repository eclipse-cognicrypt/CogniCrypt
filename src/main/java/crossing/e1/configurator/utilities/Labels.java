/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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

package crossing.e1.configurator.utilities;

import java.util.Properties;

public interface Labels {

	Properties prop = new PropertiesFileReader("src/main/resources/Labels.properties").getProperties();
	// Strings for GUI elements
	static final String MEWGENERATIONWINDOWTITLE = "Generate Cryptography Code";
	static final String NEWGENERATIONWIZARDPAGETITLE = "Generate Code";
	static final String DEPENDENCIES = "dependencies";
	static final String DEPENDENCY = "dependency";
	static final String VALUE = "value";
	static final String OPERATOR = "operator";
	static final String REF_CLAFER = "refClafer";
	static final String IS_GROUP = "isGroup";
	static final String REF = "ref";
	static final String ANSWER_LIST = "Answers";
	static final String ANSWER = "Answer";
	static final String DEF = "def";
	static final String DISPLAY = "display";
	static final String TASK = "Task";
	static final String TASK_NAME = "name";
	static final String QUESTION_LIST = "Questions";
	static final String QUESTION = "Question";
	static final String CONFIG_PATH = prop.getProperty("CONFIG_PATH");
	static final String CLAFER_PATH = prop.getProperty("CLAFER_PATH");
	static final String PLUGINID = prop.getProperty("PLUGINID");
	static final String COMPLETE = prop.getProperty("COMPLETE");
	static final String RESULT = prop.getProperty("RESULT");
	static final String DESCRIPTION_VALUE_DISPLAY_PAGE = prop.getProperty("DESCRIPTION_VALUE_DISPLAY_PAGE");
	static final String SECOND_PAGE = prop.getProperty("SECOND_PAGE");
	static final String AVAILABLE_OPTIONS = prop.getProperty("AVAILABLE_OPTIONS");
	static final String DESCRIPTION_INSTANCE_LIST_PAGE = prop.getProperty("DESCRIPTION_INSTANCE_LIST_PAGE");
	static final String instanceList = prop.getProperty("LABEL1");
	static final String SELECT_TASK = prop.getProperty("SELECT_TASK");
	static final String TASK_LIST = prop.getProperty("TASK_LIST");
	static final String TLS_PAGE = prop.getProperty("TLS_PAGE");
	static final String DESCRIPTION_TASK_SELECTION_PAGE = prop.getProperty("DESCRIPTION_TASK_SELECTION_PAGE");
	static final String DESCRIPTION_TLS_PAGE = prop.getProperty("DESCRIPTION_TLS_PAGE");
	static final String NO_TASK = prop.getProperty("NO_TASK");
	static final String LABEL2 = prop.getProperty("LABEL2");
	static final String HOST = prop.getProperty("HOST");
	static final String PORT = prop.getProperty("PORT");
	static final String DESCRIPTION_VALUE_SELECTION_PAGE = prop.getProperty("DESCRIPTION_VALUE_SELECTION_PAGE");
	static final String PROPERTIES = prop.getProperty("PROPERTIES");
	static final String SELECT_PROPERTIES = prop.getProperty("SELECT_PROPERTIES");
	static final String INSTANCE_ERROR_MESSGAE = prop.getProperty("INSTANCE_ERROR_MESSGAE");
	//	static final String EQUALS = prop.getProperty("EQUALS");
	//	static final String GREATER_THAN = prop.getProperty("GREATER_THAN");
	//	static final String LESS_THAN = prop.getProperty("LESS_THAN");
	//	static final String GREATER_THAN_EQUAL = prop.getProperty("GREATER_THAN_EQUAL");
	//	static final String LESS_THAN_EQUAL = prop.getProperty("LESS_THAN_EQUAL");

}
