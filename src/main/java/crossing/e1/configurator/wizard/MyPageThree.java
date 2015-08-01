package crossing.e1.configurator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jgrapht.util.PrefetchIterator.NextElementFunctor;
/**
 * @author Ram
 *
 */
public class MyPageThree extends WizardPage {

	private Label label1;
	private Composite container;
	String value;

	public MyPageThree(String desc) {
		super("Third page");
		setTitle("Complete");
		setDescription("Below are the values for choosen option");
		value = desc;
		canFlipToNextPage();

	}

	public boolean canFlipToNextPage(){
		return false;
	}
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;

		label1 = new Label(container, SWT.NONE);
		label1.setText(value);
		setPageComplete(true);
		setControl(container);
	}

}
