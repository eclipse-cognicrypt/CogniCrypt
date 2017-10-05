package crossing.e1.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.XSLTags;
import crossing.e1.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;


public class XSLTagDialog extends Dialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public XSLTagDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Combo comboXSLTags = new Combo(container, SWT.NONE);
		comboXSLTags.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		for(XSLTags tag : Constants.XSLTags.values()){
			comboXSLTags.add(tag.getXSLTagFaceName());
		}
		comboXSLTags.select(0);
		
		CompositeToHoldSmallerUIElements compositeForProperties = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null);
		GridData gd_compositeForProperties = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeForProperties.widthHint = 432;
		gd_compositeForProperties.heightHint = 150;
		compositeForProperties.setLayoutData(gd_compositeForProperties);
		
		

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
