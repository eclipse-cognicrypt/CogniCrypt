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
import org.clafer.ast.AstConstraint;

public class ClaferModelUtils {

	public static String getDisplayName(AstClafer inputClafer) {
		return "";
	}

	public static void displayProperties(AstClafer inputClafer) {

		if (inputClafer != null) {
			inputClafer.getChildren().forEach(
					child -> System.out.println("attr: " + child));

			displayProperties(inputClafer.getSuperClafer());
		}
	}

	/*
	 * Method takes AstClafer as an input and returns a description of the
	 * clafer if exist, returns name of the clafer otherwise
	 */
	String getDescription(AstClafer inputClafer) {
		if (inputClafer.hasConstraints())
			for (AstConstraint child : inputClafer.getConstraints()) {
				String expr = child.getExpr().toString();
				if (expr.substring(0,
						((expr.indexOf('=') > 0) ? expr.indexOf('=') : 1))
						.contains("escription . ref")) {
					//return without Quotes,hence replaced the "" with empty
					return expr.substring(expr.indexOf('=') + 1, expr.length())
							.replace("\"","");
				}

			}

		return inputClafer.getName();

	}
}
