package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.JavaCore;
import de.cognicrypt.utils.Utils;

public class RunAnalysisHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final AnalysisKickOff akf = new AnalysisKickOff();
		if (akf.setUp(JavaCore.create(Utils.getCurrentlySelectedIProject()))) {
			akf.run();
		}
		return null;
	}

	

}
