package de.cognicrypt.codegenerator.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.misc.ContainerContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

import de.cognicrypt.utils.Utils;

public class LocatorPage extends WizardPage {

	private IStructuredSelection selectedResource = null;

	protected LocatorPage(final String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);

		final Label label = new Label(composite, SWT.WRAP);
		label.setText(
			"Please select the file CogniCrypt should generate code into. You may also select a package or \nproject. In this case, CogniCrypt will generate a new Java source file within the selected resource.");
		label.setFont(getFont());

		final DrillDownComposite drillDown = new DrillDownComposite(composite, SWT.BORDER);
		final GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
		spec.widthHint = 320;
		spec.heightHint = 300;
		drillDown.setLayoutData(spec);

		// Create tree viewer inside drill down.
		final TreeViewer treeViewer = new TreeViewer(drillDown, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDown.setChildTree(treeViewer);
		final ContainerContentProvider cp = new ContainerContentProvider();
		cp.showClosedProjects(true);

		new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);

		final Label selResLabel = new Label(composite, SWT.WRAP);
		selResLabel.setText("Selected Resource: ");
		selResLabel.setFont(getFont());

		final Text containerNameField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 240;
		containerNameField.setLayoutData(gd);
		containerNameField.setFont(getFont());

		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		treeViewer.setComparator(new ViewerComparator());
		treeViewer.setUseHashlookup(true);
		treeViewer.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = event.getStructuredSelection();
			final Object firstElement = selection.getFirstElement();
			containerSelectionChanged(firstElement, containerNameField); // allow null
			if (firstElement != null) {
				setPageComplete(true);
				this.selectedResource = selection;
			}

		});
		treeViewer.addDoubleClickListener(event -> {
			final ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection) {
				final Object item = ((IStructuredSelection) selection).getFirstElement();
				if (item == null) {
					return;
				}
				if (treeViewer.getExpandedState(item)) {
					treeViewer.collapseToLevel(item, 1);
				} else {
					treeViewer.expandToLevel(item, 1);
				}
			}
		});

		// This has to be done after the viewer has been laid out
		treeViewer.setInput(ResourcesPlugin.getWorkspace());
		IResource currentlyOpenRes = Utils.getCurrentlyOpenFile();
		if (currentlyOpenRes == null) {
			currentlyOpenRes = Utils.getCurrentlySelectedIProject();
		}
		if (currentlyOpenRes != null) {
			treeViewer.setSelection(new StructuredSelection(currentlyOpenRes));
			treeViewer.expandToLevel(currentlyOpenRes, 1);
		}
		setControl(composite);
	}

	public void containerSelectionChanged(final Object object, final Text containerNameField) {
		String text = "";
		if (object instanceof IContainer) {
			text = TextProcessor.process(((IContainer) object).getFullPath().makeRelative().toString());
		} else if (object instanceof IFile) {
			text = ((IFile) object).getFullPath().makeRelative().toString();
		}
		containerNameField.setText(text);
		containerNameField.setToolTipText(text);
	}

	public IStructuredSelection getSelectedResource() {
		return this.selectedResource;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

}
