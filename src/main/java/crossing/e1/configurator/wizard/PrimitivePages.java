package crossing.e1.configurator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class PrimitivePages extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public PrimitivePages() {
		super("wizardPage");
		setTitle("Primitive Integration");
		setDescription("Algorithm Type");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		lblNewLabel.setBounds(22, 49, 214, 25);
		lblNewLabel.setText("Please select the type of Algorithm");

		Combo combo = new Combo(container, SWT.READ_ONLY);
		combo.setTouchEnabled(true);
		combo.setToolTipText("");
		combo.setBounds(254, 47, 112, 23);
		combo.setText("");
//		String[] items = { "Cipher", "Message Digest", "Key Generation" };
		combo.setItems(new String[] { "Cipher", "Message Digest", "Key Generation" });
		combo.select(0);
		setControl(container);
	}
}
