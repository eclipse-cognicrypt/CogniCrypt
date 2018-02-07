package de.cognicrypt.codegenerator.featuremodel.clafer;

import java.util.HashMap;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;

public class PropertiesMapperUtil {

	private static volatile HashMap<AstAbstractClafer, List<AstClafer>> enumMap = null;

	/**
	 * Used when there is an enum in the properties
	 *
	 * @return properties map
	 */
	public static HashMap<AstAbstractClafer, List<AstClafer>> getenumMap() {
		if (PropertiesMapperUtil.enumMap == null) {
			PropertiesMapperUtil.enumMap = new HashMap<>();
		}
		return PropertiesMapperUtil.enumMap;
	}

	/**
	 * Reset group properties
	 */
	public static void resetEnumMap() {
		if (PropertiesMapperUtil.enumMap != null) {
			PropertiesMapperUtil.enumMap.clear();
		}
	}

	/**
	 * Private constructor for singleton pattern
	 */
	private PropertiesMapperUtil() {}

}
