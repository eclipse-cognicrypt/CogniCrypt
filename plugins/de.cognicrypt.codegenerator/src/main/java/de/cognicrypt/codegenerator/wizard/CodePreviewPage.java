package de.cognicrypt.codegenerator.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;

public class CodePreviewPage extends WizardPage{

	private InstanceListPage instanceListPage;
	private Composite control;
	private Group codePreviewPanel;
	private Text code;

	public CodePreviewPage(InstanceListPage instanceListPage) {
		super(Constants.CODE_PREVIEW_PAGE);
		setTitle(Constants.CODE_PREVIEW_PAGE_TITLE);
		setDescription(Constants.CODE_PREVIEW_PAGE_DESCRIPTION);
		this.instanceListPage = instanceListPage;
	}

	@Override
	public void createControl(Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.control = new Composite(sc, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		this.control.setLayout(layout);
		
		//Preview of the code for the default algorithm, which will be generated in to the Java project
				this.codePreviewPanel = new Group(this.control, SWT.NONE);
				this.codePreviewPanel.setText(Constants.CODE_PREVIEW);
				GridLayout gridLayout = new GridLayout();
				this.codePreviewPanel.setLayout(gridLayout);
				GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
				gridData.horizontalSpan = 1;
				gridData.heightHint = 200;
				this.codePreviewPanel.setLayoutData(gridData);
				final Font boldFont = new Font(this.codePreviewPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
				this.codePreviewPanel.setFont(boldFont);
				setControl(this.control);

				this.code = new Text(this.codePreviewPanel, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
				Display display = Display.getCurrent();
				this.code.setLayoutData(new GridData(GridData.FILL_BOTH));
				this.code.setBounds(10, 20, 520, 146);
				this.code.setEditable(false);
				Color white = display.getSystemColor(SWT.COLOR_WHITE);
				this.code.setBackground(white);
				new Label(control, SWT.NONE);		
				this.code.setText(instanceListPage.getCodePreview());		
				this.code.setToolTipText(Constants.DEFAULT_CODE_TOOLTIP);	
				
		
		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

}
