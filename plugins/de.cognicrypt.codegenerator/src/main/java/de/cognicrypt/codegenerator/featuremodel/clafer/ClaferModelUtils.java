package de.cognicrypt.codegenerator.featuremodel.clafer;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;

/**
 * This class provides helper methods for handling clafer models.
 * 
 * @author Ram Kamath
 * @author Stefan Krueger
 */
public class ClaferModelUtils {

	/**
	 * Method to find a clafer with a given name in whole model
	 * 
	 * @param startingClafer
	 *        Starting point for search in the clafer model
	 * @param name
	 *        name of Clafer that is searched for
	 * @return Requested clafer or <CODE>null</CODE> if clafer was not found
	 */
	public static AstClafer findClaferByName(final AstClafer startingClafer, final String name) {
		final String inputName = getNameWithoutScope(startingClafer.getName());
		if (inputName.equalsIgnoreCase(name)) {
			return startingClafer;
		} else {
			if (startingClafer.hasChildren()) {
				final AstConcreteClafer foundChildClafer = startingClafer.getChildren().stream().filter(child -> getNameWithoutScope(child.getName()).equals(name)).findFirst()
					.orElse(null);
				if (foundChildClafer != null) {
					return foundChildClafer;
				} else {
					for (final AstConcreteClafer childClafer : startingClafer.getChildren()) {
						final AstClafer foundClafer = findClaferByName(childClafer, name);
						if (foundClafer != null) {
							return foundClafer;
						}
					}
				}
				;

			}
			if (startingClafer instanceof AstAbstractClafer) {
				for (final AstAbstractClafer abstractChildClafer : ((AstAbstractClafer) startingClafer).getAbstractChildren()) {
					final AstClafer foundClafer = findClaferByName(abstractChildClafer, name);
					if (foundClafer != null) {
						return foundClafer;
					}
				}
			}

			if (startingClafer.hasRef()) {
				return findClaferByName(startingClafer.getRef().getTargetType(), name);
			}

			if (startingClafer.getSuperClafer() != null) {
				return findClaferByName(startingClafer.getSuperClafer(), name);
			}

			return null;

		}
	}

	/**
	 * Creates a new clafer in the model.
	 * 
	 * @param parentClafer
	 *        clafer the new clafer is a subclafer to
	 * @param name
	 *        Name of new clafer
	 * @param type
	 *        Type of new clafer
	 * @return newly created clafer
	 */
	public static AstConcreteClafer createClafer(final AstClafer parentClafer, final String name, final String type) {
		final AstConcreteClafer newClafer = parentClafer.addChild(name).withCard(1, 1);
		newClafer.refTo(ClaferModelUtils.findClaferByName(parentClafer.getParent(), type));
		return newClafer;
	}

	private static String getNameWithoutScope(final String input) {
		final int underScoreIndex = input.indexOf("_");
		if (underScoreIndex >= 0) {
			return input.substring(underScoreIndex + 1);
		} else {
			return input;
		}
	}

	/**
	 * Method to check if the given clafer is abstract
	 *
	 * @param astClafer
	 *        clafer that is checked
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if passed clafer is concrete/abstract.
	 */
	public static boolean isConcrete(final AstClafer astClafer) {
		Boolean isConcrete = true;
		if (astClafer.hasRef()) {
			isConcrete = !astClafer.getRef().getTargetType().getClass().toGenericString().contains("AstAbstractClafer");
		}
		if (!isConcrete || astClafer.getClass().toGenericString().contains("AstAbstractClafer")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * removes scope from name (e.g., c0_) and changes first letter of the string to Upper case example c0_scope will become Scope
	 */
	public static String removeScopePrefix(final String scope) {
		final String shortenedScope = scope.substring(scope.indexOf('_') + 1, scope.length());
		return shortenedScope.substring(0, 1).toUpperCase() + shortenedScope.substring(1, shortenedScope.length());
	}
}
