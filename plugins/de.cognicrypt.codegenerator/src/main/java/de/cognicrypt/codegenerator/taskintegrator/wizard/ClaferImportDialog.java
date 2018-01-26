package de.cognicrypt.codegenerator.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.ClaferModelContentProvider;
import de.cognicrypt.codegenerator.taskintegrator.widgets.ClaferModelLabelProvider;


public class ClaferImportDialog extends Dialog {
	
	private ListViewer listViewer;
	private TreeViewer treeViewer;

	protected ClaferImportDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.RESIZE | SWT.TITLE);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(2, false);
		container.setLayout(gl_container);

		getShell().setMinimumSize(600, 400);

		Text textBox = new Text(container, SWT.BORDER);			 
		textBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button browseButton = new Button(container, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		browseButton.setText(Constants.LABEL_BROWSE_BUTTON);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {						        
				FileDialog fileDialog = new FileDialog(getShell(),SWT.OPEN);		
		        
				String selectedFile = fileDialog.open();
				
				if (selectedFile != null) {
					textBox.setText(selectedFile);
					
					ClaferModel importedModel = ClaferModel.createFromBinaries(selectedFile);
					if (importedModel != null) {
						treeViewer.setInput(importedModel);
						treeViewer.expandAll();
					}
				}
			}
		});
		
		treeViewer = new TreeViewer(container);
		treeViewer.setContentProvider(new ClaferModelContentProvider());
		treeViewer.setLabelProvider(new ClaferModelLabelProvider());

		treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		treeViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (treeViewer.getSelection() instanceof TreeSelection) {
					TreeSelection ts = (TreeSelection) treeViewer.getSelection();

					// toggle expansion if feature name clicked
					if (ts.getFirstElement() instanceof ClaferFeature) {
						Object selectedElem = ts.getFirstElement();
						treeViewer.setExpandedState(selectedElem, !treeViewer.getExpandedState(selectedElem));
					}
				}
				super.mouseDoubleClick(e);
			}
		});

		return container;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 600);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

}
