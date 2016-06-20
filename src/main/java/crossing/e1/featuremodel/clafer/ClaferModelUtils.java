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

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;

public class ClaferModelUtils {
	private static AstClafer targetClafer = null;

	/**
	 * Method to find a clafer with a given name in whole model
	 */
	public static AstClafer findClaferByName(final AstClafer inputClafer,
			final String name) {
		targetClafer = null;
		findClaferByNameHelper(inputClafer, name);
		return targetClafer;
	}

	/**
	 * Method takes AstClafer as an input and returns a description of the
	 * clafer if exist, returns name of the clafer otherwise
	 */
	// FIXME check if this method is used in any commented code
	public static String getDescription(final AstClafer inputClafer) {
		for (final AstConstraint child : inputClafer.getConstraints()) {
			final String expr = child.getExpr().toString();
			final int indexEqSign = expr.indexOf('=');
			if (expr.substring(0, indexEqSign > 0 ? indexEqSign : 1).contains(
					"escription . ref")) {
				// return without Quotes,hence replaced the "" with empty
				return expr.substring(indexEqSign + 1, expr.length()).replace(
						"\"", "");
			}
		}
		return inputClafer.getName();
	}

	/**
	 * method to check if the given clafer is an abstract clafer
	 *
	 * @param astClafer
	 * @return
	 */
	public static boolean isAbstract(final AstClafer astClafer) {
		Boolean isAbstract = false;
		if (astClafer.hasRef()) {
			isAbstract = astClafer.getRef().getTargetType().getClass()
					.toGenericString().contains("AstAbstractClafer");
		}
		if (astClafer.getClass().toGenericString()
				.contains("AstAbstractClafer")) {
			isAbstract = true;
		}
		return isAbstract;
	}

	/**
	 * removes scope from name (e.g., c0_) and changes first letter of the
	 * string to Upper case example c0_scope will become Scope
	 */
	public static String removeScopePrefix(final String scope) {
		final String shortenedScope = scope.substring(scope.indexOf('_') + 1,
				scope.length());
		return shortenedScope.substring(0, 1).toUpperCase()
				+ shortenedScope.substring(1, shortenedScope.length());
	}

	/**
	 * Helper method to find the clafer with a given name, if there are
	 * duplicates it always sets the first matching clafer as matchedClafer
	 * object
	 */
	public static void findClaferByNameHelper(final AstClafer inputClafer,
			final String name) {
		if (targetClafer == null) {
			if (inputClafer.getName().equals(name)) {
				targetClafer = inputClafer;
			}
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer childClafer : inputClafer
						.getChildren()) {
					findClaferByNameHelper(childClafer, name);
				}
			}

			if (inputClafer.hasRef()) {
				findClaferByNameHelper(inputClafer.getRef().getTargetType(), name);
			}
			if (inputClafer.getSuperClafer() != null) {
				findClaferByNameHelper(inputClafer.getSuperClafer(), name);
			}
		}
	}
	
	public static void createTaskPropertiesMap(AstConcreteClafer taskClafer, Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> taskPropertiesMap, Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> taskGroupPropertiesMap) {
		 
		if (taskClafer.hasChildren())
			for (AstConcreteClafer childClafer : taskClafer.getChildren()) {
				ArrayList<AstConcreteClafer> propertiesList = new ArrayList<AstConcreteClafer>();
				ArrayList<AstConcreteClafer> groupPropertiesList = new ArrayList<AstConcreteClafer>();
				findClaferProperties(childClafer, propertiesList, groupPropertiesList);
				taskPropertiesMap.put(childClafer, propertiesList);
				taskGroupPropertiesMap.put(childClafer, groupPropertiesList);
			}
	}

	public static void findClaferProperties(AstClafer inputClafer,
			ArrayList<AstConcreteClafer> propertiesList,
			ArrayList<AstConcreteClafer> groupPropertiesList) {

		if (inputClafer.hasChildren()) {
			if (inputClafer.getGroupCard() != null
					&& inputClafer.getGroupCard().getLow() >= 1) {				
				propertiesList.add((AstConcreteClafer) inputClafer);
			} else
				for (AstConcreteClafer childClafer : inputClafer.getChildren()) {
					findClaferProperties(childClafer, propertiesList,
							groupPropertiesList);
				}
		}

		if (inputClafer.hasRef()) {
			if (inputClafer.getRef().getTargetType().isPrimitive()
					&& !(inputClafer.getRef().getTargetType().getName()
							.contains("string"))) {
				if (!ClaferModelUtils.isAbstract(inputClafer)) {
					propertiesList.add((AstConcreteClafer) inputClafer);
				}

			} else if (groupPropertiesList != null && PropertiesMapperUtil.getenumMap().containsKey(
					inputClafer.getRef().getTargetType())) {
				groupPropertiesList.add((AstConcreteClafer) inputClafer);
			} else if (!inputClafer.getRef().getTargetType().isPrimitive()) {
				findClaferProperties(inputClafer.getRef().getTargetType(),
						propertiesList, groupPropertiesList);

			}
		}

		if (inputClafer.getSuperClafer() != null) {
			System.out.println("input clafer: " + inputClafer + " super clafer: " + inputClafer.getSuperClafer());
			findClaferProperties(inputClafer.getSuperClafer(), propertiesList,
					groupPropertiesList);
		}

	}

	public static void findClaferProperties(AstAbstractClafer inputClafer,
			ArrayList<AstConcreteClafer> propertiesList,
			ArrayList<AstConcreteClafer> groupPropertiesList) {
		System.out.println("adding for abstract: "+ inputClafer);
			if (inputClafer.hasChildren()) {
				for (AstConcreteClafer in : inputClafer.getChildren()){
					System.out.println("calling for child: " + in);
					findClaferProperties(in, propertiesList,
							groupPropertiesList);
				}
			}
			if (inputClafer.hasRef()){
				findClaferProperties(inputClafer.getRef().getTargetType(),
						propertiesList, groupPropertiesList);
			}

			if (inputClafer.getSuperClafer() != null){
				findClaferProperties(inputClafer.getSuperClafer(),
						propertiesList, groupPropertiesList);
			}
	}

	public static String getNameWithoutScope(String input){
		return input.substring(input.indexOf("_") + 1);
	}
}
