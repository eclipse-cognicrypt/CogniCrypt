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
