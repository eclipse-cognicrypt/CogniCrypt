package de.cognicrypt.codegenerator.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

/**
 * This class is responsible for displaying the instances the Clafer instance generator generated.
 *
 * @author Ram Kamath
 */
public class InstanceListPage extends WizardPage {

	private Composite control;
	private Text instanceDetails;
	private final InstanceGenerator instanceGenerator;
	private InstanceClafer value;
	private Group instancePropertiesPanel;
	private final TaskSelectionPage taskSelectionPage;
	private Map<Question, Answer> constraints;

	public InstanceListPage(final InstanceGenerator inst, Map<Question, Answer> constraints, final TaskSelectionPage taskSelectionPage) {
		super(Constants.ALGORITHM_SELECTION_PAGE);
		setTitle("Possible solutions for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Constants.DESCRIPTION_INSTANCE_LIST_PAGE);
		this.instanceGenerator = inst;
		this.taskSelectionPage = taskSelectionPage;
		this.constraints = constraints;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(final Composite parent) {

		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(parent, SWT.NONE);
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
		final String firstInstance = inst.keySet().toArray()[0].toString();
		final Combo combo = algorithmClass.getCombo();
		final String key = this.instanceGenerator.getAlgorithmName();
		final int count = this.instanceGenerator.getAlgorithmCount();
		combo.setToolTipText("There are " + String.format("%d", count) + " variations of the algorithm " + key);

		//Display help assist for the first instance in the combo box
		final ControlDecoration deco = new ControlDecoration(combo, SWT.TOP | SWT.RIGHT);
		final Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();

		deco.setDescriptionText(Constants.DEFAULT_ALGORITHM_NOTIFICATION);
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
		algorithmClass.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				return element.toString();
			}
		});
		algorithmClass.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			InstanceListPage.this.instancePropertiesPanel.setVisible(true);
			final String b = selection.getFirstElement().toString();
			setValue(InstanceListPage.this.instanceGenerator.getInstances().get(b));
			InstanceListPage.this.instanceDetails.setText(getInstanceProperties(InstanceListPage.this.instanceGenerator.getInstances().get(b)));

			if (!b.equals(firstInstance)) {
				//hide the help assist if the selected algorithm is not the default algorithm
				deco.hide();
			} else {
				deco.show();
			}
			if (selection.size() > 0) {
				setPageComplete(true);
			}
		});
		new Label(this.control, SWT.NONE);
		new Label(this.control, SWT.NONE);

		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instancePropertiesPanel.setText(Constants.INSTANCE_DETAILS);
		final Font boldFont = new Font(this.instancePropertiesPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		this.instancePropertiesPanel.setFont(boldFont);

		this.instanceDetails = new Text(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.instanceDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.instanceDetails.setToolTipText(Constants.INSTANCE_DETAILS_TOOLTIP);
		this.instanceDetails.setBounds(10, 20, 400, 180);
		this.instanceDetails.setEditable(false);
		/*
		 * Initially instance properties panel will be hidden
		 */
		this.instancePropertiesPanel.setVisible(false);
		setControl(this.control);
		final ISelection selection = new StructuredSelection(inst.keySet().toArray()[0]);
		algorithmClass.setSelection(selection);
		new Label(this.control, SWT.NONE);

		//Button to View the code that will be generated into the Java project

		final Button codePreviewButton = new Button(this.control, SWT.NONE);
		codePreviewButton.setText("Code Preview");
		codePreviewButton.addListener(SWT.Selection, event -> {
			final MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
			messageBox.setText("Code Preview");
			messageBox.setMessage(compileCodePreview());
			messageBox.open();
		});

	}

	private void getInstanceDetails(final InstanceClafer inst, final Map<String, String> algorithms) {
		String value;

		if (!inst.getType().getRef().getTargetType().isPrimitive()) {
			String algo = Constants.ALGORITHM + " :" + ClaferModelUtils.removeScopePrefix(inst.getType().getRef().getTargetType().getName()) + Constants.lineSeparator;
			algorithms.put(algo, "");

			final InstanceClafer instan = (InstanceClafer) inst.getRef();
			for (final InstanceClafer in : instan.getChildren()) {
				if (in.getType().getRef() != null && !in.getType().getRef().getTargetType().isPrimitive()) {
					final String superName = ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getSuperClafer().getName());
					if (!superName.equals("Enum")) {
						getInstanceDetails(in, algorithms);
						continue;
					}
				}
				value = "\t" + ClaferModelUtils.removeScopePrefix(in.getType().getName()) + " : " + ((in.getRef() != null) ? in.getRef().toString().replace("\"", "") : "");
				if (value.indexOf("->") > 0) {	// VeryFast -> 4 or Fast -> 3	removing numerical value and "->"
					value = value.substring(0, value.indexOf("->") - 1);
				}
				value = value.replace("\n", "") + Constants.lineSeparator;	// having only one \n at the end of string
				algorithms.put(algo, algorithms.get(algo) + value);
			}
			// Above for loop over children hasn't been executed, then following if
			if (!instan.hasChildren()) {
				value = "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + " : " + inst.getRef().toString().replace("\"", "");
				algo = algorithms.keySet().iterator().next();
				algorithms.put(algo, algorithms.get(algo) + value);
			}
		}
	}

	/**
	 * The user might select an algorithm configuration/instance from the combobox. This method returns the details of the currently selected algorithm, which is passed as a
	 * parameter.
	 *
	 * @param inst
	 *        instance currently selected in the combo box
	 * @return details for chosen algorithm configuration
	 */
	private String getInstanceProperties(final InstanceClafer inst) {
		final Map<String, String> algorithms = new HashMap<>();
		for (final InstanceClafer child : inst.getChildren()) {
			getInstanceDetails(child, algorithms);
		}

		final StringBuilder output = new StringBuilder();
		for (final Map.Entry<String, String> entry : algorithms.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (!value.isEmpty()) {
				output.append(key);
				output.append(value);
				output.append(Constants.lineSeparator);
			}
		}
		return output.toString();
	}

	/**
	 * Assembles code-preview text. 
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

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}
}
