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
 * @author Ram Kamath
 *
 */
package de.cognicrypt.codegenerator.featuremodel.clafer;

import java.util.HashMap;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;

public class PropertiesMapperUtil {

	private volatile static HashMap<AstAbstractClafer, List<AstClafer>> enumMap = null;

	/**
	 * used when there is an enum in the properties
	 *
	 * @return
	 */
	public static HashMap<AstAbstractClafer, List<AstClafer>> getenumMap() {
		if (PropertiesMapperUtil.enumMap == null) {
			PropertiesMapperUtil.enumMap = new HashMap<>();
		}
		return PropertiesMapperUtil.enumMap;
	}

	/**
	 * reset group properties
	 */
	public static void resetEnumMap() {
		PropertiesMapperUtil.enumMap = null;
	}

	/**
	 * private Constructor for singleton pattern
	 */
	private PropertiesMapperUtil() {

	}

}
