package de.cognicrypt.crysl.handler;

import java.io.IOException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import de.cognicrypt.crysl.reader.CrySLModelReader;
import de.cognicrypt.utils.Utils;

public class RunRuleConverterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource cryslRule = Utils.getCurrentlySelectedIResource();
		CrySLModelReader csmr;
		try {
			csmr = new CrySLModelReader(cryslRule.getProject());
			csmr.readRule(cryslRule.getRawLocation().makeAbsolute().toFile());
			Activator.getDefault().logInfo("Converted selected rule " + cryslRule.getName() + " successfully.");
		}
		catch (CoreException | IOException e) {
			Activator.getDefault().logError(e);
		}
		return null;
	}

}
