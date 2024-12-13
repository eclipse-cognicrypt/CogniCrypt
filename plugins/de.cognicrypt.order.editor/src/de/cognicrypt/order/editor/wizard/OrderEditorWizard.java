/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.order.editor.wizard;

import org.eclipse.jface.wizard.Wizard;
import de.cognicrypt.core.Constants;

public class OrderEditorWizard extends Wizard {

	public OrderEditorWizard() {

	}

	@Override
	public void addPages() {
		addPage(new PageForOrderDiagramWizard(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD, Constants.PAGE_TITLE_FOR_MODE_OF_WIZARD, Constants.PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD));

		//addPage(new PageForTaskIntegratorWizard(Constants.PAGE_NAME_FOR_LINK_ANSWERS, Constants.PAGE_TITLE_FOR_LINK_ANSWERS, Constants.PAGE_DESCIPTION_FOR_LINK_ANSWERS));

	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
}
