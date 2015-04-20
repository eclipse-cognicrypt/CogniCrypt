package crossing.e1.configurator.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.*;

public class TaskSelectionPage extends WizardPage {
	private Combo taskCombo;
	private Composite container;
	private String[] tasks;

	public TaskSelectionPage(String[] items) {
		super("Select Task");
		setTitle("Select Taks");
		setDescription("Here the user selects his task");
		tasks = items;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label label1 = new Label(container, SWT.NONE);
		label1.setText("Select Task");

		taskCombo = new Combo(container, SWT.BORDER | SWT.SINGLE);
		taskCombo.setItems(tasks);
		taskCombo.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				if (taskCombo.getSelectionIndex() >= 0) {
					setPageComplete(true);
				}
			}

		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		taskCombo.setLayoutData(gd);
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public String getSelction() {
		return taskCombo.getText();
	}
}
