package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
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
	String path;

	public JavaProjectBrowserPage(String pageName) {
		super(pageName);
		setDescription("Please choose the Algorithm project from your computer.");
		setTitle("File browser");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setBounds(10, 10, 200, 300);

		container.setLayout(new GridLayout(3, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		Label question = new Label(container, SWT.NULL);
		question.setText("Select a Java Project: ");
		
				text = new Text(container, SWT.BORDER | SWT.READ_ONLY);
				text.setEditable(true);
				//gd_text.widthHint = 107;
				text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				text.addModifyListener(e -> {
					this.selectedJavaFile=new File(getAbsolutePath());
					System.out.println(getAbsolutePath());
				});
		
				Button btnBrowse = new Button(container, SWT.PUSH);
				GridData gd_btnBrowse = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
				gd_btnBrowse.widthHint = 85;
				btnBrowse.setLayoutData(gd_btnBrowse);
				btnBrowse.setBounds(295, 140, 75, 25);
				btnBrowse.setText("Browse");
				btnBrowse.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog dialog = new FileDialog(btnBrowse.getShell(), SWT.NULL);
						dialog.setFilterExtensions(new String [] {".project"});
						dialog.setFilterPath("c:\\");
						 path = dialog.open();
						if (path != null) {

							File file = new File(path);
							if (file.isFile())
								displayFiles(new String[] { file.toString() });
							else
								displayFiles(file.list());
						}
						try {
						IProjectDescription description = ResourcesPlugin
							   .getWorkspace().loadProjectDescription(new Path(path));
						IProject project = ResourcesPlugin.getWorkspace()
							   .getRoot().getProject(description.getName());
							project.create(description, null);
							project.open(null); 
							project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
							System.out.println("le projet existe:"+project.exists());
							SearchRequestor request = null;
							findMainMethodInCurrentProject((IJavaProject) project, request );
							
						}
						catch(CoreException e1) {
							e1.printStackTrace();
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
	
	public static void findMainMethodInCurrentProject(final IJavaProject project, final SearchRequestor requestor) {
		final SearchPattern sp = SearchPattern.createPattern("main", IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);

		final SearchEngine se = new SearchEngine();
		final SearchParticipant[] searchParticipants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
		final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { project });

		try {
			se.search(sp, searchParticipants, scope, requestor, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	// Get the absolute path of the selected file 
	private String getAbsolutePath() {
		return text.getText();
	}
	
	public File getSelectedFile(){
		return this.selectedJavaFile;
	}
	

}
