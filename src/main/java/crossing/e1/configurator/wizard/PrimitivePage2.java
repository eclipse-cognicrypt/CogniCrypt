package crossing.e1.configurator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;


public class PrimitivePage2 extends WizardPage {
	private Text text;
	private Text text_1;
	private Text text_2;

	/**
	 * Create the wizard.
	 */
	public PrimitivePage2() {
		super("wizardPage");
		setTitle("Primitive Integration");
		setDescription("Symmetric Key type");
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
		lblNewLabel.setBounds(20, 10, 238, 25);
		lblNewLabel.setText("Please fill in the description of algorithm");
		setControl(container);
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText("Name");
		lblName.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		lblName.setBounds(20, 41, 59, 25);
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(98, 41, 76, 21);
		
		Label lblTheNameOf = new Label(container, SWT.NONE);
		lblTheNameOf.setText("The name of author/company");
		lblTheNameOf.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		lblTheNameOf.setBounds(20, 87, 181, 25);
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setBounds(230, 87, 76, 21);
		
		Label lblDescriptionOfYour = new Label(container, SWT.NONE);
		lblDescriptionOfYour.setText("Description of your Algorithm");
		lblDescriptionOfYour.setFont(SWTResourceManager.getFont("Tahoma", 10, SWT.NORMAL));
		lblDescriptionOfYour.setBounds(20, 166, 181, 25);
		
		text_2 = new Text(container, SWT.BORDER);
		text_2.setBounds(230, 164, 214, 95);
		
		Button btnCheckExamples = new Button(container, SWT.NONE);
		btnCheckExamples.setBounds(447, 298, 117, 25);
		btnCheckExamples.setText("Check Examples");
	}
}
