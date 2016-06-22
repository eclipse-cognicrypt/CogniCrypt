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
		if (inputClafer.getName().equals(name)) {
			return inputClafer;
		} else {
			if (inputClafer.hasChildren()) {
				for (final AstConcreteClafer childClafer : inputClafer
						.getChildren()) {
					AstClafer foundClafer = findClaferByName(childClafer, name);
					if(foundClafer != null){
						return foundClafer;
					}
				}
			}

			if (inputClafer.hasRef()) {
				return findClaferByName(inputClafer.getRef().getTargetType(),
						name);
			}
			
			if (inputClafer.getSuperClafer() != null) {
				return findClaferByName(inputClafer.getSuperClafer(), name);
			}
			
			return null;

		}
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

	public static String getNameWithoutScope(String input) {
		return input.substring(input.indexOf("_") + 1);
	}
}
