/*
 * generated by Xtext 2.25.0
 */
package de.cognicrypt.order.editor.parser.antlr;

import java.io.InputStream;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;

public class StatemachineAntlrTokenFileProvider implements IAntlrTokenFileProvider {

	@Override
	public InputStream getAntlrTokenFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		return classLoader.getResourceAsStream("de/cognicrypt/order/editor/parser/antlr/internal/InternalStatemachine.tokens");
	}
}