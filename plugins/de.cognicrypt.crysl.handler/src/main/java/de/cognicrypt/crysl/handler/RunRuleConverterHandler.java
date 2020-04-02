/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.crysl.handler;

import java.io.IOException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import de.cognicrypt.crysl.Activator;
import de.cognicrypt.crysl.reader.CrySLParser;
import de.cognicrypt.utils.Utils;

public class RunRuleConverterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource cryslRule = Utils.getCurrentlySelectedIResource();
		CrySLParser csmr;
		try {
			csmr = new CrySLParser(cryslRule.getProject());
			csmr.readRule(cryslRule.getRawLocation().makeAbsolute().toFile());
			Activator.getDefault().logInfo("Converted selected rule " + cryslRule.getName() + " successfully.");
		}
		catch (CoreException | IOException e) {
			Activator.getDefault().logError(e);
		}
		return null;
	}

}
