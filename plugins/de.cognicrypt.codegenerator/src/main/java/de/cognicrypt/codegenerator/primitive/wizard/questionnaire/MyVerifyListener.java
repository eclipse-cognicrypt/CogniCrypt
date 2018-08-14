/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.wizard.questionnaire;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class MyVerifyListener implements VerifyListener {

	public MyVerifyListener() {}

	public void verifyText(VerifyEvent e) {
		{

			String currentText = ((Text) e.widget).getText();
			String port = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
			try {
				int portNum = Integer.valueOf(port);
				if (portNum < 0 || portNum > 65535) {
					e.doit = false;
				}
			} catch (NumberFormatException ex) {
				if (!port.equals(""))
					e.doit = false;
			}
		}

	}

}
