package de.cognicrypt.staticanalyzer.utilities;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Reporter extends TitleAreaDialog {

	private Text txtTitle;
	private Text txtIssue;
	private Combo combo;

	private String issueTitle;
	private String issueText;
	private int attachmentIndex;
	
	private String fileName;

	public Reporter(Shell parentShell, String fileName) {
		super(parentShell);
		this.fileName = fileName;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Welcome to CogniCrypt Issue Reporter");
		setMessage("Here you have the possibility to report an issue", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		txtTitle = new Text(container, SWT.BORDER);		
		txtTitle.setMessage("Please enter issue title...");
		txtTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		txtIssue = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		txtIssue.setMessage("Please enter issue description...");
		txtIssue.setFocus();
		txtIssue.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite container2 = new Composite(container, SWT.NONE);
		GridLayout layout2 = new GridLayout(2, false);
		container2.setLayout(layout2);

		Label lbtInfo = new Label(container2, SWT.NONE);
		lbtInfo.setText("Additonaly, to the issue description CogniCrypt will send:");
		GridData dataGrid = new GridData();
		dataGrid.grabExcessHorizontalSpace = true;
		dataGrid.horizontalAlignment = GridData.FILL;

		combo = new Combo(container2, SWT.DROP_DOWN | SWT.READ_ONLY);
		String[] items = new String[] {fileName, "Method", "None" };
		combo.setItems(items);
		combo.setLayoutData(dataGrid);
		combo.select(1);

		return area;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Send", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		issueTitle = txtTitle.getText();
		issueText = txtIssue.getText();
		attachmentIndex = combo.getSelectionIndex();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(550, 320);
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getIssueTitle() {
		return issueTitle;
	}

	public String getIssueText() {
		return issueText;
	}

	public int getAttachmentIndex() {
		return attachmentIndex;
	}


}
