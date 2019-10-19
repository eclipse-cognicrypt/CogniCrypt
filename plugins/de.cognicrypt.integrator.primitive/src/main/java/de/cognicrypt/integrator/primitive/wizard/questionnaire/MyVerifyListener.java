/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.wizard.questionnaire;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class MyVerifyListener implements VerifyListener {

	public MyVerifyListener() {}

	@Override
	public void verifyText(final VerifyEvent e) {
		{

			final String currentText = ((Text) e.widget).getText();
			final String port = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
			try {
				final int portNum = Integer.valueOf(port);
				if (portNum < 0 || portNum > 65535) {
					e.doit = false;
				}
			}
			catch (final NumberFormatException ex) {
				if (!port.equals("")) {
					e.doit = false;
				}
			}
		}

	}

}
