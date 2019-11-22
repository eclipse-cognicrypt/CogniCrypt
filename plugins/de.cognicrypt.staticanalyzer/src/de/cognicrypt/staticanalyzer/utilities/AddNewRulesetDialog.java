package de.cognicrypt.staticanalyzer.utilities;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddNewRulesetDialog extends TitleAreaDialog {

    private Text txtRulesetUrl;

    private String rulesetUrl;

    public AddNewRulesetDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Add new ruleset");
        setMessage("Please enter the url of the ruleset", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createRulesetUrl(container);

        return area;
    }
    
    private void createRulesetUrl(Composite container) {
        Label lbtRulesetUrl = new Label(container, SWT.NONE);
        lbtRulesetUrl.setText("Url");

        GridData dataRulesetUrl = new GridData();
        dataRulesetUrl.grabExcessHorizontalSpace = true;
        dataRulesetUrl.horizontalAlignment = GridData.FILL;
        txtRulesetUrl = new Text(container, SWT.BORDER);
        txtRulesetUrl.setLayoutData(dataRulesetUrl);
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
        rulesetUrl = txtRulesetUrl.getText();

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getRulesetUrl() {
        return rulesetUrl;
    }
}
