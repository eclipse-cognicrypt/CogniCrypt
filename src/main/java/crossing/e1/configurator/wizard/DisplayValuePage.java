package crossing.e1.configurator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.configurator.Lables;

/**
 * @author Ram
 *
 */
public class DisplayValuePage extends WizardPage implements Lables {

	private Label label1;
	private Composite container;
	String value;

	public DisplayValuePage(String desc) {
		super(Lables.COMPLETE);
		setTitle(Lables.RESULT);
		setDescription(Lables.DESCRIPTION_VALUE_DISPLAY_PAGE);
		value = desc;
		canFlipToNextPage();

	}
	@Override
	public boolean canFlipToNextPage(){
		return false;
		
	}
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		label1 = new Label(container, SWT.NONE);
		label1.setText(value);
		setPageComplete(true);
		setControl(container);
	}
	
	
}
