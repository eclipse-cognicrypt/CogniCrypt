package crossing.e1.configurator.wizard;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Lables;
import crossing.e1.configurator.utilities.WriteToFileHelper;
import crossing.e1.xml.export.PublishToXML;

/**
 * @author Ram
 *
 */
public class DisplayValuePage extends WizardPage implements Lables {

	private Label label1;
	private Composite container;
	String value;

	public DisplayValuePage(InstanceClafer instanceChosen) {
		super(Lables.COMPLETE);
		setTitle(Lables.RESULT);
		setDescription(Lables.DESCRIPTION_VALUE_DISPLAY_PAGE);
		value = new PublishToXML().displayInstanceValues(instanceChosen,"");
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
		// Create a multiple-line text field
		Text t = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	    t.setLayoutData(new GridData(GridData.FILL_BOTH));
		t.setText(value);
		Button button = new Button(container, SWT.TOGGLE);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setText("Save As");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WriteToFileHelper write= new WriteToFileHelper();
				write.writeToFile(value, write.getFileNameToBeSaved(container, "Algorithm"));

			}
		});		
		
		setPageComplete(true);
		setControl(container);
	}
	
	
}
