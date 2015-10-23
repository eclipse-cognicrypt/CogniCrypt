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

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;

public class ClaferModelUtils {
	static AstClafer mathedClafer = null;

	public static String getDisplayName(AstClafer inputClafer) {
		return "";
	}

	/*
	 * Method takes AstClafer as an input and returns a description of the
	 * clafer if exist, returns name of the clafer otherwise
	 */
	// FIXME check if this method is used in any commented code
	public static String getDescription(AstClafer inputClafer) {
		if (inputClafer.hasConstraints())
			for (AstConstraint child : inputClafer.getConstraints()) {
				String expr = child.getExpr().toString();
				if (expr.substring(0,
						((expr.indexOf('=') > 0) ? expr.indexOf('=') : 1))
						.contains("escription . ref")) {
					// return without Quotes,hence replaced the "" with empty
					return expr.substring(expr.indexOf('=') + 1, expr.length())
							.replace("\"", "");
				}

			}

		return inputClafer.getName();

	}

	public static boolean isAbstract(AstClafer astClafer) {
		if (astClafer.hasRef())
			return astClafer.getRef().getTargetType().getClass()
					.toGenericString().contains("AstAbstractClafer");
		else
			return astClafer.getClass().toGenericString()
					.contains("AstAbstractClafer");
	}

	/*
	 * Method to find a clafer with a given name
	 */
	public static AstClafer findClaferByName(AstClafer inputClafer, String name) {
		mathedClafer = null;
		setClaferByName(inputClafer, name);
		return mathedClafer;
	}

	/*
	 * Helper method to find the clafer with a given name, if there are
	 * duplicates it always sets the first matching clafer as matchedClafer
	 * object
	 */
	public static void setClaferByName(AstClafer inputClafer, String name) {

		try {
			if (mathedClafer == null) {
				if (inputClafer.getName().equals(name)) {
					mathedClafer = inputClafer;

				}
				if (inputClafer.hasChildren()) {
					for (AstConcreteClafer childClafer : inputClafer
							.getChildren())
						setClaferByName(childClafer, name);
				}

				if (inputClafer.hasRef()) {
					setClaferByName(inputClafer.getRef().getTargetType(), name);

				}
				if (inputClafer.getSuperClafer() != null)
					setClaferByName(inputClafer.getSuperClafer(), name);
			}

		} catch (Exception E) {
			E.printStackTrace();

		}

	}

	/*
	 * removes scope from name (e.g., c0_) and changes first letter of the string to Upper case example c0_scope will become Scope
	 */
	public static String trimScope(String value) {
		String val = value.substring(value.indexOf('_') + 1, value.length());
		val = val.substring(0, 1).toUpperCase()
				+ val.substring(1, val.length());
		return val;
	}

}
