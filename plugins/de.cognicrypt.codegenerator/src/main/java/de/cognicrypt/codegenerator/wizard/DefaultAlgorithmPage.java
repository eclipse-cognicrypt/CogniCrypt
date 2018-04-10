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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

public class DefaultAlgorithmPage extends WizardPage {

	private Composite control;
	private Group codePreviewPanel;
	private final TaskSelectionPage taskSelectionPage;
	private Button defaultAlgorithmCheckBox;
	private Text code;
	private final InstanceGenerator instanceGenerator;
	private Map<Question, Answer> constraints;
	private InstanceClafer value;

	/**
	 * Constructor for DefaultAlgorithmPage.
	 * 
	 * @param instGen Instance Generator
	 * @param constraints 
	 * @param taskSelectionPage Page to select task
	 */
	public DefaultAlgorithmPage(final InstanceGenerator instGen, HashMap<Question, Answer> constraints, final TaskSelectionPage taskSelectionPage) {
		super(Constants.DEFAULT_ALGORITHM_PAGE);
		setTitle("Best solution for task: " + taskSelectionPage.getSelectedTask().getDescription());
		setDescription(Constants.DESCRIPTION_DEFAULT_ALGORITHM_PAGE);
		this.instanceGenerator = instGen;
		this.taskSelectionPage = taskSelectionPage;
		this.constraints = constraints;
	}

	@Override
	public void createControl(final Composite parent) {
		this.control = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		this.control.setLayout(layout);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.control, "de.cognicrypt.codegenerator.help_id_2");

		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		compositeControl.setLayout(new GridLayout(2, false));
		Label labelDefaultAlgorithm = new Label(compositeControl, SWT.NONE);
		labelDefaultAlgorithm.setText(Constants.defaultAlgorithm);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();//Only the first Instance,which is the most secure one, will be displayed

		Label algorithmClass = new Label(compositeControl, SWT.NONE);
		final String firstInstance = inst.keySet().toArray()[0].toString();
		algorithmClass.setText(firstInstance);
		setValue(DefaultAlgorithmPage.this.instanceGenerator.getInstances().get(firstInstance));
		setPageComplete(true);

		algorithmClass.setToolTipText(Constants.DEFAULT_ALGORITHM_COMBINATION_TOOLTIP);

		this.codePreviewPanel = new Group(this.control, SWT.NONE);
		this.codePreviewPanel.setText(Constants.CODE_PREVIEW);
		final Font boldFont = new Font(this.codePreviewPanel.getDisplay(), new FontData(Constants.ARIAL, 10, SWT.BOLD));
		this.codePreviewPanel.setFont(boldFont);
		setControl(this.control);

		this.code = new Text(this.codePreviewPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		this.code.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.code.setBounds(10, 20, 520, 146);
		this.code.setEditable(false);
		new Label(this.control, SWT.NONE);

		this.code.setText(compileCodePreview());

		this.code.setToolTipText(Constants.DEFAULT_CODE_TOOLTIP);

		this.defaultAlgorithmCheckBox = new Button(this.control, SWT.CHECK);
		this.defaultAlgorithmCheckBox.setSelection(true);
		if (this.instanceGenerator.getNoOfInstances() == 1) {
			//if there is only one instance, then the user can generate the code only for the default algorithm combination.
			//Thus, the combo box will be disabled which prevents the user from moving to the next page.
			this.defaultAlgorithmCheckBox.setEnabled(false);
		}
		this.defaultAlgorithmCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		this.defaultAlgorithmCheckBox.setText(Constants.DEFAULT_ALGORITHM_PAGE_CHECKBOX);
		this.defaultAlgorithmCheckBox.setToolTipText(Constants.DEFAULT_CHECKBOX_TOOLTIP);
		final ControlDecoration deco = new ControlDecoration(this.defaultAlgorithmCheckBox, SWT.TOP | SWT.LEFT);
		final Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
		if (this.defaultAlgorithmCheckBox.isEnabled()) {
			deco.setDescriptionText(Constants.DEFAULT_ALGORITHM_CHECKBOX_ENABLE);
		} else {
			deco.setDescriptionText(Constants.DEFAULT_ALGORITHM_CHECKBOX_DISABLE);
		}
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);
	}

	private String compileCodePreview() {
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

	public boolean isDefaultAlgorithm() {
		return this.defaultAlgorithmCheckBox.getSelection();
	}

	public InstanceClafer getValue() {
		return this.value;
	}

	@Override
	public void setPageComplete(final boolean complete) {
		super.setPageComplete(complete);
	}

	public void setValue(final InstanceClafer instanceClafer) {
		this.value = instanceClafer;
	}

	@Override
	public boolean canFlipToNextPage() {
		//Can go to next page only if the check box is unchecked
		if (this.defaultAlgorithmCheckBox.getSelection() == true) {
			return !this.defaultAlgorithmCheckBox.getSelection();
		}
		return true;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.control.setFocus();
		}
	}

}
