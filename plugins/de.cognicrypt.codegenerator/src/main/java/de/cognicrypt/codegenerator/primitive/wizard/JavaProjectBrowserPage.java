package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import java.lang.reflect.Method;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Ahmed Ben Tahar
 */
public class JavaProjectBrowserPage extends WizardPage {

	Text text;
	File selectedJavaFile;

	public JavaProjectBrowserPage(String pageName) {
		super(pageName);
		setDescription("Getting the algorithm project");
		setTitle("File browser");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setBounds(10, 10, 200, 300);

		container.setLayout(new GridLayout(2, true));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		Label question = new Label(container, SWT.NULL);
		question.setText("Please select your Java Project from your local");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
				new Label(container, SWT.NONE);
		
				text = new Text(container, SWT.BORDER | SWT.READ_ONLY);
				GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_text.widthHint = 242;
				text.setLayoutData(gd_text);
		
				Button btnBrowse = new Button(container, SWT.PUSH);
				GridData gd_btnBrowse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_btnBrowse.widthHint = 85;
				btnBrowse.setLayoutData(gd_btnBrowse);
				btnBrowse.setBounds(295, 140, 75, 25);
				btnBrowse.setText("Browse");
				btnBrowse.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog dialog = new FileDialog(btnBrowse.getShell(), SWT.NULL);
						dialog.setFilterExtensions(new String [] {"*.java"});
						dialog.setFilterPath("c:\\");
						String path = dialog.open();
						if (path != null) {

							File file = new File(path);
							if (file.isFile())
								displayFiles(new String[] { file.toString() });
							else
								displayFiles(file.list());
						}
					}

				});
	}

	public void displayFiles(String[] files) {
		for (int i = 0; files != null && i < files.length; i++) {
			text.setText(files[i]);
			text.setEditable(true);

		}
	}

	// Test if the file selected is Java project or not
	public boolean isJavaProject(File file) {
    
		return true;
	}
	

}
