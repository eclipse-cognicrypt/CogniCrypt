package de.cognicrypt.staticanalyzer.utilities;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LocalRulesetDialog extends TitleAreaDialog {

	private Text txtRulesetPath;

    private String rulesetPath;
    
    public LocalRulesetDialog(Shell parentShell) {
        super(parentShell);
    }
    
    @Override
    public void create() {
        super.create();
        setTitle("Add new local ruleset");
        setMessage("Please enter the path of the local ruleset", IMessageProvider.INFORMATION);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createRulesetPath(container);

        return area;
    }
    
    private void createRulesetPath(Composite container) {
        Label lbtRulesetUrl = new Label(container, SWT.NONE);
        lbtRulesetUrl.setText("Path");

        GridData dataRulesetUrl = new GridData();
        dataRulesetUrl.grabExcessHorizontalSpace = true;
        dataRulesetUrl.horizontalAlignment = GridData.FILL;
        txtRulesetPath = new Text(container, SWT.BORDER);
        txtRulesetPath.setLayoutData(dataRulesetUrl);
        
        Button selectLocalRulesButton = new Button(container, SWT.PUSH);
		selectLocalRulesButton.setText("Browse local rules directory");
		selectLocalRulesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(container.getShell());
				// Set the initial filter path according
				// to anything they've selected or typed in
				dlg.setFilterPath(txtRulesetPath.getText());
				// Calling open() will open and run the dialog.
				// It will return the selected directory, or
				// null if user cancels
				String dir = dlg.open();
				if (dir != null) {
					// Set the text box to the new selection
					txtRulesetPath.setText(dir);
				}
			}
	    });
    }
    
    @Override
    protected boolean isResizable() {
        return false;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
        rulesetPath = txtRulesetPath.getText();

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getRulesetPath() {
        return rulesetPath;
    }
	
}
