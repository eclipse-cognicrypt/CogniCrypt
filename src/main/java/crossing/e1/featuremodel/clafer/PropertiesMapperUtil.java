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

	private PropertiesMapperUtil() {

	}

	public static Map<String, AstConcreteClafer> getTaskLabelsMap() {
		if (taskLabelMap == null) {
			taskLabelMap = new HashMap<String, AstConcreteClafer>();
		}
		return taskLabelMap;
	}

	public static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> getPropertiesMap() {
		if (propertiesMap == null) {
			propertiesMap = new HashMap<AstConcreteClafer, ArrayList<AstConcreteClafer>>();
		}
		return propertiesMap;
	}

	public static void resetPropertiesMap() {
		propertiesMap = null;
	}

	public static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> getGroupPropertiesMap() {
		if (groupPropertiesMap == null) {
			groupPropertiesMap = new HashMap<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>>();
		}
		return groupPropertiesMap;
	}

	public static void resetGroupPropertiesMap() {
		PropertiesMapperUtil.groupPropertiesMap = null;
	}

}
