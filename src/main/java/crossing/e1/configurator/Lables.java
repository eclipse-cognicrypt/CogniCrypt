package crossing.e1.configurator;

import java.io.File;
import java.util.Properties;

/**
 * @author Ram
 *
 */

public interface Lables {
	Properties prop = new ReadLables("src/main/resources/Labels.properties").getProperties();
	static final String CONFIG_PATH = prop.getProperty("CONFIG_PATH");
	static final String CLAFER_PATH = prop.getProperty("CLAFER_PATH");
	static final String PLUGINID = prop.getProperty("PLUGINID");
	static final String COMPLETE = prop.getProperty("COMPLETE");
	static final String RESULT = prop.getProperty("RESULT");
	static final String DESCRIPTION_VALUE_DISPLAY_PAGE = prop
			.getProperty("DESCRIPTION_VALUE_DISPLAY_PAGE");
	static final String SECOND_PAGE = prop.getProperty("SECOND_PAGE");
	static final String AVAILABLE_OPTIONS = prop
			.getProperty("AVAILABLE_OPTIONS");
	static final String DESCRIPTION_INSTANCE_LIST_PAGE = prop
			.getProperty("DESCRIPTION_INSTANCE_LIST_PAGE");
	static final String LABEL1 = prop.getProperty("LABEL1");
	static final String SELECT_TASK = prop.getProperty("SELECT_TASK");
	static final String TASK_LIST = prop.getProperty("TASK_LIST");
	static final String DESCRIPTION_TASK_SELECTION_PAGE = prop
			.getProperty("DESCRIPTION_TASK_SELECTION_PAGE");
	static final String NO_TASK = prop.getProperty("NO_TASK");
	static final String LABEL2 = prop.getProperty("LABEL2");
	static final String DESCRIPTION_VALUE_SELECTION_PAGE = prop
			.getProperty("DESCRIPTION_VALUE_SELECTION_PAGE");
	static final String PROPERTIES = prop.getProperty("PROPERTIES");
	static final String SELECT_PROPERTIES = prop
			.getProperty("SELECT_PROPERTIES");
	static final String INSTANCE_ERROR_MESSGAE = prop
			.getProperty("INSTANCE_ERROR_MESSGAE");
	static final String EQUALS = prop.getProperty("EQUALS");
	static final String GREATER_THAN = prop.getProperty("GREATER_THAN");
	static final String LESS_THAN = prop.getProperty("LESS_THAN");
	static final String GREATER_THAN_EQUAL = prop.getProperty("GREATER_THAN_EQUAL");
	static final String LESS_THAN_EQUAL = prop.getProperty("LESS_THAN_EQUAL");

}
