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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class XSLTagDialog extends Dialog {
	private CompositeToHoldSmallerUIElements compositeForXSLAttributes;
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
		GridData gd_comboXSLTags = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboXSLTags.widthHint = 430;
		comboXSLTags.setLayoutData(gd_comboXSLTags);
		
		for(XSLTags tag : Constants.XSLTags.values()){
			comboXSLTags.add(tag.getXSLTagFaceName());
		}
		comboXSLTags.select(0);
		
		Button btnAddAttribute = new Button(container, SWT.NONE);
		
		btnAddAttribute.setText("Add Attribute");
		
		compositeForXSLAttributes = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, true);
		GridData gd_compositeForProperties = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeForProperties.widthHint = 417;
		gd_compositeForProperties.heightHint = 150;
		compositeForXSLAttributes.setLayoutData(gd_compositeForProperties);
		
		btnAddAttribute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				compositeForXSLAttributes.addXSLAttributeUI(comboXSLTags.getText(), true);
			}
		});

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
