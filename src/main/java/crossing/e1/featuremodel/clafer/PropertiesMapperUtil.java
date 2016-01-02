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
 * @author Ram Kamath
 *
 */
package crossing.e1.featuremodel.clafer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstConcreteClafer;

public class PropertiesMapperUtil {
	private static Map<String, AstConcreteClafer> taskLabelMap = null;
	private static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> propertiesMap = null;
	private static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> groupPropertiesMap = null;
	/**
	 * private Constructor for singleton pattern     
	 */
	private PropertiesMapperUtil() {

	}

	/**
	 * List of clafer which extends abstract Task
	 * 
	 * @return
	 */
	public static Map<String, AstConcreteClafer> getTaskLabelsMap() {
		if (taskLabelMap == null) {
			taskLabelMap = new HashMap<String, AstConcreteClafer>();
		}
		return taskLabelMap;
	}

	/**
	 * Map with list of clafers and their properties 
	 * ex : car : 
	 * 				speed
	 * 				transmission
	 * in above clafer key would be 'car' and values would be
	 * [speed,transmission]
	 * 
	 * @return
	 */
	public static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> getPropertiesMap() {
		if (propertiesMap == null) {
			propertiesMap = new HashMap<AstConcreteClafer, ArrayList<AstConcreteClafer>>();
		}
		return propertiesMap;
	}

	/**
	 * method to reset the properties map
	 */
	public static void resetPropertiesMap() {
		propertiesMap = null;
	}

	/**
	 * used when there is an enum in the properties 
	 * 
	 * @return
	 */
	public static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> getGroupPropertiesMap() {
		if (groupPropertiesMap == null) {
			groupPropertiesMap = new HashMap<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>>();
		}
		return groupPropertiesMap;
	}

	/**
	 * reset group properties
	 */
	public static void resetGroupPropertiesMap() {
		PropertiesMapperUtil.groupPropertiesMap = null;
	}

}
