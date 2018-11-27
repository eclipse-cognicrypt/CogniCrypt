/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.TransformerException;

import org.clafer.instance.InstanceClafer;
import org.eclipse.compare.CompareUI;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

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
	private int currentIndex;
	private String selectedItem;

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
		setCurrentIndex(0);
		ComboViewer algorithmClass;
		Label labelInstanceList;
		this.control = new Composite(sc, SWT.NONE);
		final GridLayout layout = new GridLayout(3, false);
		this.control.setLayout(layout);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sc, "de.cognicrypt.codegenerator.InstanceListHelp");

		final Composite compositeControl = new Composite(this.control, SWT.NONE);
		setPageComplete(false);
		compositeControl.setLayout(new GridLayout(2, false));
		labelInstanceList = new Label(compositeControl, SWT.NONE);
		labelInstanceList.setText(Constants.instanceList);
		final Map<String, InstanceClafer> inst = this.instanceGenerator.getInstances();
		algorithmClass = new ComboViewer(compositeControl, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo combo = algorithmClass.getCombo();

		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(this.instanceGenerator.getAlgorithmNames());
		String key = instanceGenerator.getAlgorithmNames().get(0);

		int count = combo.getItemCount();
		int variationCount = instanceGenerator.getAlgorithmCount();
		if (count > variationCount) {
			combo.setToolTipText("There are " + String.format("%d", count) + " solutions ");
		} else {
			combo.setToolTipText("There are " + String.format("%d", variationCount) + " variations of the algorithm " + key);
		}

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

		new Label(control, SWT.NONE);
		new Label(control, SWT.NONE);
		this.instancePropertiesPanel = new Group(this.control, SWT.NONE);
		this.instanceDetails = new StyledText(this.instancePropertiesPanel, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

		Composite composite_Control = new Composite(this.instancePropertiesPanel, SWT.NONE);
		composite_Control.setLayoutData(new GridData(SWT.CENTER, GridData.FILL, true, false));
		composite_Control.setLayout(new GridLayout(3, true));

		//Back button to go to the previous algorithm in the combo box
		Button backIcon = new Button(composite_Control, SWT.NONE);
		backIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		backIcon.setText("<");
		backIcon.setToolTipText(Constants.PREVIOUS_ALGORITHM_BUTTON);

		//Label that displays the current algorithm variation and the total number of variations
		Label algorithmVariation = new Label(composite_Control, SWT.NONE);
		algorithmVariation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

		//Button to go to the next algorithm in the combo box
		Button nextIcon = new Button(composite_Control, SWT.NONE);
		nextIcon.setText(">");
		nextIcon.setToolTipText(Constants.NEXT_ALGORITHM_BUTTON);

		backIcon.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int temp = combo.getSelectionIndex();
				TreeMap<String, InstanceClafer> tempAlgorithmGroup = InstanceListPage.this.instanceGenerator.getSeparatedAlgorithms().get(temp);

				int tempIndex = getCurrentIndex() - 1;
				String tempKey = (String) tempAlgorithmGroup.keySet().toArray()[tempIndex - 1];
				setValue(tempAlgorithmGroup.get(tempKey));

				InstanceListPage.this.instanceDetails.setText(defaultAlgorithmPage.getInstanceProperties(tempAlgorithmGroup.get(tempKey)));
				setCurrentIndex(tempIndex);
				algorithmVariation.setText("       " + "  Variation  " + (getCurrentIndex() + " / " + String.format("%d       ", tempAlgorithmGroup.size())));
				if (combo.getSelectionIndex() == 0 && getCurrentIndex() == 1) {
					//hide the help assist and the text if the selected algorithm is not the default algorithm
					deco.show();
					infoText.setVisible(true);
				} else {
					infoText.setVisible(false);
					deco.hide();
					//disable back button if the selected algorithm in the combo box is the first instance
				}

				if (getCurrentIndex() == 1) {
					backIcon.setEnabled(false);
				} else {
					backIcon.setEnabled(true);
				}

				if (getCurrentIndex() == tempAlgorithmGroup.size()) {
					//disable next button if the selected algorithm in the combo box is the last instance
					nextIcon.setEnabled(false);
				} else {
					nextIcon.setEnabled(true);
				}
			}
		});

		nextIcon.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int temp = combo.getSelectionIndex();
				TreeMap<String, InstanceClafer> tempAlgorithmGroup = InstanceListPage.this.instanceGenerator.getSeparatedAlgorithms().get(temp);

				int tempIndex = getCurrentIndex() + 1;
				String tempKey = (String) tempAlgorithmGroup.keySet().toArray()[tempIndex - 1];
				setValue(tempAlgorithmGroup.get(tempKey));
				InstanceListPage.this.instanceDetails.setText(defaultAlgorithmPage.getInstanceProperties(tempAlgorithmGroup.get(tempKey)));
				setCurrentIndex(tempIndex);
				algorithmVariation.setText("       " + "  Variation  " + (getCurrentIndex() + " / " + String.format("%d       ", tempAlgorithmGroup.size())));

				if (combo.getSelectionIndex() == 0 && getCurrentIndex() == 1) {
					//hide the help assist and the text if the selected algorithm is not the default algorithm
					deco.show();
					infoText.setVisible(true);
				} else {
					infoText.setVisible(false);
					deco.hide();
					//disable back button if the selected algorithm in the combo box is the first instance
				}

				if (getCurrentIndex() == 1) {
					backIcon.setEnabled(false);
				} else {
					backIcon.setEnabled(true);
				}

				if (getCurrentIndex() == tempAlgorithmGroup.size()) {
					//disable next button if the selected algorithm in the combo box is the last instance
					nextIcon.setEnabled(false);
				} else {
					nextIcon.setEnabled(true);
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
			int temp = combo.getSelectionIndex();
			TreeMap<String, InstanceClafer> tempAlgorithmGroup = InstanceListPage.this.instanceGenerator.getSeparatedAlgorithms().get(temp);

			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			InstanceListPage.this.instancePropertiesPanel.setVisible(true);
			final String selectedAlgorithm = selection.getFirstElement().toString();

			setSelectedItem(selectedAlgorithm);
			setValue(tempAlgorithmGroup.get(selectedAlgorithm));
			InstanceListPage.this.instanceDetails.setText(defaultAlgorithmPage.getInstanceProperties(tempAlgorithmGroup.get(selectedAlgorithm)));
			setCurrentIndex(1);
			algorithmVariation.setText("       " + "  Variation  " + (getCurrentIndex() + " / " + String.format("%d       ", tempAlgorithmGroup.size())));

			if (combo.getSelectionIndex() == 0 && getCurrentIndex() == 1) {
				//hide the help assist and the text if the selected algorithm is not the default algorithm
				deco.show();
				infoText.setVisible(true);
			} else {
				infoText.setVisible(false);
				deco.hide();
				//disable back button if the selected algorithm in the combo box is the first instance
			}

			backIcon.setEnabled(false);
			if (tempAlgorithmGroup.size() == 1) {
				nextIcon.setEnabled(false);
			}

			//for compare algorithm page
			setAlgorithmCombinations(tempAlgorithmGroup.keySet());

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
				//open compare dialog to show the difference between the current code(in the user's Java class that is open in his editor)
				//and the new code that will be generated(in the same class) on clicking 'Finish'.	
				//if the user does not have any class opened in his editor, then one side of the compare editor will be empty.
				String codePreviewContent = compileCodePreview();
				if (getCurrentEditorContent() == "") {
					CompareUI.openCompareDialog(new CompareInput(getCurrentEditorContent(), codePreviewContent));
				} else {
					String currentlyOpenPart = getCurrentEditorContent();
					int position = currentlyOpenPart.indexOf("{");
					currentlyOpenPart = new StringBuilder(currentlyOpenPart).insert(position + 1, "\n" + codePreviewContent).toString();
					CompareUI.openCompareDialog(new CompareInput(getCurrentEditorContent(), currentlyOpenPart));
				}
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
	 * Get the code from the user's open file.
	 * 
	 * @return code in the file
	 */
	public String getCurrentEditorContent() {
		IEditorPart currentlyOpenPart = Utils.getCurrentlyOpenEditor();
		if (currentlyOpenPart == null || !(currentlyOpenPart instanceof AbstractTextEditor)) {
			Activator.getDefault().logInfo(
				"Could not open access the editor of the file or there are no files open. Therefore,  the 'Old Source' part remains empty and the newly generated code appears in the 'Modified Source' part.");
			return "";
		}
		ITextEditor currentlyOpenEditor = (ITextEditor) currentlyOpenPart;
		IDocument currentlyOpenDocument = currentlyOpenEditor.getDocumentProvider().getDocument(currentlyOpenEditor.getEditorInput());
		final String docContent = currentlyOpenDocument.get();
		return docContent;
	}

	/**
	 * Assembles code-preview text.
	 * 
	 * @return code snippet
	 * @throws BadLocationException
	 */
	public String compileCodePreview() {
		final CodeGenerator codeGenerator = new XSLBasedGenerator(this.taskSelectionPage.getSelectedProject(), this.taskSelectionPage.getSelectedTask().getXslFile());
		final String claferPreviewPath = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile;
		Configuration codePreviewConfig = new XSLConfiguration(value, this.constraints, claferPreviewPath);
		final String temporaryOutputFile = codeGenerator.getDeveloperProject().getProjectPath() + Constants.innerFileSeparator + Constants.CodeGenerationCallFile;

		try {
			((XSLBasedGenerator) codeGenerator).transform(codePreviewConfig.persistConf(), temporaryOutputFile);
		} catch (TransformerException | IOException e) {
			Activator.getDefault().logError(e, Constants.TransformerErrorMessage);
			return "";
		}

		final File file = new File(temporaryOutputFile);
		try (InputStream in = Files.newInputStream(file.toPath()); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			final StringBuilder preview = new StringBuilder();
			String line = null;
			// If no file is open in user's editor, show the preview of newly generated class
			if (getCurrentEditorContent() == "") {
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("import")) {
						preview.append(line);
						preview.append(Constants.lineSeparator);
					}
				}
				return preview.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
			}
			// If a file is open in user's editor, show the preview of newly generated lines located inside the user's open file.
			else {
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("package") && !line.contains("class") && !line.startsWith("import")) {
						preview.append(line);
						preview.append(Constants.lineSeparator);
					}
				}
				String truncatedPreview = preview.toString();
				int truncateIndex = truncatedPreview.length();
				truncateIndex = truncatedPreview.lastIndexOf("}", truncateIndex - 1);
				truncatedPreview = truncatedPreview.substring(0, truncateIndex);
				return truncatedPreview.replaceAll("(?m)^[ \t]*\r?\n", "");
			}
		} catch (final IOException e) {
			Activator.getDefault().logError(e, Constants.CodePreviewErrorMessage);
		}  finally {
			try {
				Files.walkFileTree(file.getParentFile().toPath(), new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
					
				});
				Files.delete(new File(claferPreviewPath).toPath());
			} catch (IOException e) {
				Activator.getDefault().logError(e, "Could not delete temporary files.");
			}
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

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

}
