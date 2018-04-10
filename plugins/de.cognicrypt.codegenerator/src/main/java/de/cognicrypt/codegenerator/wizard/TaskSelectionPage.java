package de.cognicrypt.codegenerator.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button guidedModeCheckBox;
	private IProject selectedProject = null;

	public TaskSelectionPage() {
		super(Constants.SELECT_TASK);
		setTitle(Constants.TASK_LIST);
		setDescription(Constants.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {

		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 300);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.container, "de.cognicrypt.codegenerator.help_id_1");
		this.container.setLayout(new GridLayout(2, false));

		final Label selectProjectLabel = new Label(this.container, SWT.NONE);
		final GridData gd_selectProjectLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_selectProjectLabel.heightHint = 28;
		gd_selectProjectLabel.widthHint = 158;
		selectProjectLabel.setLayoutData(gd_selectProjectLabel);
		selectProjectLabel.setText(Constants.SELECT_JAVA_PROJECT);

		final ComboViewer projectComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		final Combo projectCombo = projectComboSelection.getCombo();
		final GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 356;
		projectCombo.setLayoutData(gd_combo);
		projectCombo.setToolTipText(Constants.PROJECTLIST_TOOLTIP);
		projectCombo.setEnabled(true);
		projectComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		final Map<String, IProject> javaProjects = new HashMap<>();
		for (final IProject project : CodeGenUtils.complileListOfJavaProjectsInWorkspace()) {
			javaProjects.put(project.getName(), project);
		}

		if (javaProjects.isEmpty()) {
			final String[] errorMessage = { Constants.ERROR_MESSAGE_NO_PROJECT };
			projectComboSelection.setInput(errorMessage);
			projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
		} else {
			projectComboSelection.setInput(javaProjects.keySet().toArray());
			projectComboSelection.addSelectionChangedListener(event -> {
				final IStructuredSelection selected = (IStructuredSelection) event.getSelection();
				this.selectedProject = javaProjects.get(selected.getFirstElement());
				projectComboSelection.refresh();

			});

			final IProject currentProject = CodeGenUtils.getCurrentProject();
			if (currentProject == null) {
				projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
			} else {
				projectComboSelection.setSelection(new StructuredSelection(currentProject.getName()));
			}
		}

		final Label selectTaskLabel = new Label(this.container, SWT.NONE);
		final GridData gd_selectTaskLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_selectTaskLabel.heightHint = 28;
		gd_selectTaskLabel.widthHint = 139;
		selectTaskLabel.setLayoutData(gd_selectTaskLabel);
		selectTaskLabel.setText(Constants.SELECT_TASK);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		final Combo taskCombo = this.taskComboSelection.getCombo();
		final GridData gd_taskCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_taskCombo.widthHint = 223;
		taskCombo.setLayoutData(gd_taskCombo);
		taskCombo.setToolTipText(Constants.TASKLIST_TOOLTIP);
		taskCombo.setEnabled(true);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		final List<Task> tasks = TaskJSONReader.getTasks();

		this.taskComboSelection.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object task) {
				if (task instanceof Task) {
					final Task current = (Task) task;
					return current.getDescription();

				}
				return super.getText(task);
			}
		});

		this.taskComboSelection.setInput(tasks);
		//Label for task description
		final Label taskDescription = new Label(this.container, SWT.NONE);
		final GridData gd_taskDescription = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_taskDescription.widthHint = 139;
		taskDescription.setLayoutData(gd_taskDescription);
		taskDescription.setText(Constants.TASK_DESCRIPTION);

		// Adding description text for the cryptographic task that has been selected from the combo box
		final Text descriptionText = new Text(this.container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		final GridData gd_descriptionText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_descriptionText.widthHint = 297;
		gd_descriptionText.heightHint = 96;
		descriptionText.setLayoutData(gd_descriptionText);
		descriptionText.setToolTipText(Constants.DESCRIPTION_BOX_TOOLTIP);
		descriptionText.setEditable(false);

		this.taskComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			final Task selectedTask = (Task) selection.getFirstElement();
			TaskSelectionPage.this.taskComboSelection.refresh();
			setPageComplete(selectedTask != null && this.selectedProject != null);
			// To display the description text
			descriptionText.setText(selectedTask.getTaskDescription());
		});

		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));
		setControl(this.container);
		new Label(this.container, SWT.NONE);
		new Label(this.container, SWT.NONE);

		//Check box for going to guided mode
		this.guidedModeCheckBox = new Button(this.container, SWT.CHECK);
		this.guidedModeCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		this.guidedModeCheckBox.setToolTipText(Constants.GUIDEDMODE_TOOLTIP);
		this.guidedModeCheckBox.setEnabled(true);
		this.guidedModeCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

			}
		});
		this.guidedModeCheckBox.setText(Constants.GUIDED_MODE);
		this.guidedModeCheckBox.setSelection(true);
		final ControlDecoration deco = new ControlDecoration(this.guidedModeCheckBox, SWT.TOP | SWT.LEFT);
		final Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();

		deco.setDescriptionText(Constants.GUIDED_MODE_CHECKBOX_INFO);
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public Task getSelectedTask() {
		return (Task) ((IStructuredSelection) this.taskComboSelection.getSelection()).getFirstElement();
	}

	/**
	 * Helper method to UI , this flag decides the second page of the wizard.
	 *
	 * @return
	 */
	public boolean isGuidedMode() {
		return this.guidedModeCheckBox.getSelection();
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.container.setFocus();
		}
	}
}
