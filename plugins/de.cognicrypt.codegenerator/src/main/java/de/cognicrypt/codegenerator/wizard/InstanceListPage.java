package de.cognicrypt.codegenerator.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;

/**
 * This class is responsible for displaying the instances the Clafer instance generator generated.
 *
 * @author Ram Kamath
 */
public class InstanceListPage extends WizardPage {

	private Composite control;
	private StyledText instanceDetails;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private Group instancePropertiesPanel;
	private final TaskSelectionPage taskSelectionPage;
	private Map<Question, Answer> constraints;
	private Object algorithmCombinaton;
	private DefaultAlgorithmPage defaultAlgorithmPage;

	public InstanceListPage(final InstanceGenerator inst, Map<Question, Answer> constraints, final TaskSelectionPage taskSelectionPage, final DefaultAlgorithmPage defaultAlgorithmPage) {
		super(Constants.ALGORITHM_SELECTION_PAGE);
		setTitle("Possible solutions for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Constants.DESCRIPTION_INSTANCE_LIST_PAGE);
		this.instanceGenerator = inst;
		this.taskSelectionPage = taskSelectionPage;
		this.defaultAlgorithmPage = defaultAlgorithmPage;
		this.constraints = constraints;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(final Composite parent) {

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		final GridLayout layout = new GridLayout(3, false);
		this.control.setLayout(layout);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_3");

		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Constants.instanceList);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		String firstInstance = inst.keySet().toArray()[0].toString();
		Combo combo = algorithmClass.getCombo();

		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
		String key = instanceGenerator.getAlgorithmName();

		int count = combo.getItemCount();
		int variationCount = instanceGenerator.getAlgorithmCount();
		if (count > variationCount) {
			combo.setToolTipText("There are " + String.format("%d", count) + " solutions ");
		} else {
			combo.setToolTipText("There are " + String.format("%d", variationCount) + " variations of the algorithm " + key);
		}

		setAlgorithmCombinations(algorithmClass.getInput());

		//Display help assist for the first instance in the combo box
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		Text infoText = new Text(control, SWT.BORDER | SWT.WRAP);
		infoText.setText(Constants.DEFAULT_ALGORITHM_NOTIFICATION);
		infoText.setEditable(false);
		infoText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		final ControlDecoration deco = new ControlDecoration(infoText, SWT.RIGHT);
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage();
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instanceDetails = new StyledText(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

		Composite composite_Control = new Composite(this.instancePropertiesPanel, SWT.BOTTOM | SWT.CENTER);
		composite_Control.setLayoutData(new GridData(SWT.CENTER, GridData.FILL, true, false));
		composite_Control.setLayout(new GridLayout(3, true));

		//Back button to go to the previous algorithm in the combo box
		Button backIcon = new Button(composite_Control, SWT.CENTER | SWT.BOTTOM);
		backIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		backIcon.setText("<");
		backIcon.setToolTipText(Constants.PREVIOUS_ALGORITHM_BUTTON);
		backIcon.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int temp = combo.getSelectionIndex();
				if (temp != 0) {
					temp = temp - 1;
					final ISelection selection = new StructuredSelection(inst.keySet().toArray()[temp]);
					algorithmClass.setSelection(selection);
				}
			}
		});

		//Label that displays the current algorithm variation and the total number of variations
		Label algorithmVariation = new Label(composite_Control, SWT.CENTER | SWT.BOTTOM);
		algorithmVariation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

		//Button to go to the next algorithm in the combo box
		Button nextIcon = new Button(composite_Control, SWT.CENTER | SWT.BOTTOM);
		nextIcon.setText(">");
		nextIcon.setToolTipText(Constants.NEXT_ALGORITHM_BUTTON);
		nextIcon.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int temp = combo.getSelectionIndex();
				if (temp != (count - 1)) {
					temp = temp + 1;
					final ISelection selection = new StructuredSelection(inst.keySet().toArray()[temp]);
					algorithmClass.setSelection(selection);
				}

			}
		});

		algorithmClass.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return element.toString();
			}
		});
		algorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			InstanceListPage.this.instancePropertiesPanel.setVisible(true);
			final String selectedAlgorithm = selection.getFirstElement().toString();
			setValue(InstanceListPage.this.instanceGenerator.getInstances().get(selectedAlgorithm));
			InstanceListPage.this.instanceDetails
				.setText(defaultAlgorithmPage.getInstanceProperties(InstanceListPage.this.instanceGenerator.getInstances().get(selectedAlgorithm)));
			int index = combo.getSelectionIndex();
			if (count > variationCount) {
				algorithmVariation.setText("  Solution  " + (index + 1) + " / " + String.format("%d  ", count));
			} else {
				algorithmVariation.setText("  Variation  " + (index + 1) + " / " + String.format("%d  ", variationCount));
			}
			if (!selectedAlgorithm.equals(firstInstance)) {
				//hide the help assist and the text if the selected algorithm is not the default algorithm
				deco.hide();
				infoText.setVisible(false);
				backIcon.setEnabled(true);
			} else {
				infoText.setVisible(true);
				deco.show();
				//disable back button if the selected algorithm in the combo box is the first instance
				backIcon.setEnabled(false);
			}
			if (combo.getSelectionIndex() == count - 1) {
				//disable next button if the selected algorithm in the combo box is the last instance
				nextIcon.setEnabled(false);
			} else {
				nextIcon.setEnabled(true);
			}
			if (selection.size() > 0) {
				setPageComplete(true);
			}
		});

		this.instancePropertiesPanel.setText(Constants.INSTANCE_DETAILS);
		GridLayout gridLayout = new GridLayout();
		this.instancePropertiesPanel.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		gridData.heightHint = 200;
		this.instancePropertiesPanel.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);
		this.instancePropertiesPanel.setLayoutData(gridData);
		final Font boldFont = new Font(this.instancePropertiesPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		this.instancePropertiesPanel.setFont(boldFont);

		Display display = Display.getCurrent();
		this.instanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.instanceDetails.setBounds(10, 20, 400, 180);
		this.instanceDetails.setEditable(false);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		this.instanceDetails.setBackground(white);
		this.instanceDetails.setAlwaysShowScrollBars(false);

		// Initially instance properties panel will be hidden		
		this.instancePropertiesPanel.setVisible(false);
		setControl(this.control);

		final ISelection selection = new StructuredSelection(inst.keySet().toArray()[0]);
		algorithmClass.setSelection(selection);
		new Label(control, SWT.NONE);

		final Composite composite = new Composite(control, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		//Button to View the code that will be generated into the Java project
		InstanceListPage instanceListPage = this;
		Button codePreviewButton = new Button(composite, SWT.NONE);
		GridData gd_codePreviewButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_codePreviewButton.widthHint = 149;
		codePreviewButton.setLayoutData(gd_codePreviewButton);
		codePreviewButton.setText(Constants.LABEL_CODE_PREVIEW_BUTTON);
		codePreviewButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				//Opens a new wizard to show the code preview 
				final WizardDialog dialog = new WizardDialog(new Shell(), new CodePreviewWizard(instanceListPage, instanceGenerator)) {

					@Override
					protected void configureShell(Shell newShell) {
						super.configureShell(newShell);
						//newShell.setSize(650, 500);
					}
				};
				dialog.open();
			}
		});

		new Label(composite, SWT.NONE);

		//Button to compare two selected algorithms 
		Button compareAlgorithmButton = new Button(composite, SWT.NONE);
		compareAlgorithmButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {}
		});
		compareAlgorithmButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		compareAlgorithmButton.setText(Constants.LABEL_COMPARE_ALGORITHMS_BUTTON);
		compareAlgorithmButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final WizardDialog dialog = new WizardDialog(new Shell(), new CompareWizard(instanceListPage, instanceGenerator)) {

					@Override
					protected void configureShell(Shell newShell) {
						super.configureShell(newShell);
						newShell.setSize(1000, 600);
					}
				};
				dialog.open();
			}
		});
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		sc.setContent(this.control);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(this.control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	/**
	 * Assembles code-preview text.
	 * 
	 * @return code snippet
	 */
	public String compileCodePreview() {
		final CodeGenerator codeGenerator = new XSLBasedGenerator(this.taskSelectionPage.getSelectedProject(), this.taskSelectionPage.getSelectedTask().getXslFile());
		final String claferPreviewPath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
		Configuration codePreviewConfig = new Configuration(value, this.constraints, claferPreviewPath);
		final String temporaryOutputFile = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.CodeGenerationCallFile;

		try {
			((XSLBasedGenerator) codeGenerator).transform(codePreviewConfig.persistConf(), temporaryOutputFile);
		} catch (TransformerException | IOException e) {
			Activator.getDefault().logError(e, Constants.TransformerErrorMessage);
			return "";
		}

		final Path file = new File(temporaryOutputFile).toPath();
		try (InputStream in = Files.newInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			final StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("import")) {
					sb.append(line);
					sb.append(Constants.lineSeparator);
				}
			}
			return sb.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
		} catch (final IOException e) {
			Activator.getDefault().logError(e, Constants.CodePreviewErrorMessage);
		}

		return "";
	}

	public TaskSelectionPage getTaskSelectionPage() {
		return this.taskSelectionPage;
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	public InstanceClafer getValue() {
		return this.value;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.control.setFocus();
		}
	}

	public Object getAlgorithmCombinations() {
		return this.algorithmCombinaton;
	}

	public void setAlgorithmCombinations(Object input) {
		this.algorithmCombinaton = input;

	}

}
