package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.clafer.instance.InstanceClafer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeForXsl;
import de.cognicrypt.codegenerator.utilities.XMLParser;

public class XslPage extends PageForTaskIntegratorWizard {

	private CompositeForXsl compositeForXsl = null;

	public XslPage() {
		super(Constants.PAGE_NAME_FOR_XSL_FILE_CREATION, Constants.PAGE_TITLE_FOR_XSL_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_XSL_FILE_CREATION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		setCompositeForXsl(new CompositeForXsl(container, SWT.NONE));
		// fill the available space on the with the big composite
		getCompositeForXsl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		Button btnAddXSLTag = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
		btnAddXSLTag.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddXSLTag.setText("Add Xsl Tag");
		Button btnReadCode = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
		btnReadCode.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnReadCode.setText("Get the code");

		btnReadCode.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);

				if (getCompositeForXsl().getXslTxtBox().getText().trim().length() > 0) {
					MessageBox infoBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
					infoBox.setText("Updating code");
					infoBox.setMessage(
						"Some code already appears to be added. \n\nIf you choose an XSL file, all of the existing code will be replaced. If you choose a Java or text file, the contents of said file will be added at the location of the cursor.");
					infoBox.open();
				}
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);

				fileDialog.setFilterExtensions(new String[] { "*.xsl", "*.java", "*.txt" });
				fileDialog.setText("Choose the code file:");

				String fileDialogResult = fileDialog.open();
				if (fileDialogResult != null) {
					((CompositeForXsl) getCompositeForXsl()).updateTheTextFieldWithFileData(fileDialogResult);
				}

			}

		});

		btnAddXSLTag.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// this is needed to get the name and the description of the task from the wizard.
				ModelAdvancedMode objectForDataInGuidedMode = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD))
					.getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
				String taskName = objectForDataInGuidedMode.getNameOfTheTask();
				String taskDescription = objectForDataInGuidedMode.getTaskDescription();

				// Get the path for the javascript file from the clafer page.
				String jsFilePath = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getJSFilePath();
				if (jsFilePath != null) {
					InstanceGenerator instanceGenerator = new InstanceGenerator(jsFilePath, "c0_" + taskName, taskDescription);

					// This will contain the xml strings that are generated for every -> operator encountered.
					List<Document> xmlStrings = new ArrayList<Document>();

					XMLParser xmlParser = new XMLParser();
					// this will remain empty for the first instance, that contains no -> operators.
					HashMap<Question, Answer> constraints = new HashMap<>();
					List<InstanceClafer> instances = instanceGenerator.generateInstances(constraints);
					if (instances.size() > 0) {
						InstanceClafer initialInstance = instanceGenerator.generateInstances(constraints).get(0);
						xmlStrings.add(xmlParser.displayInstanceValues(initialInstance, constraints));

						// Questions needed to get the answer that has a constraint with the -> operator.
						//QuestionsJSONReader reader = new QuestionsJSONReader();
						// TODO update this to read the data generated in the questions page.

						List<Question> questions = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_LINK_ANSWERS)).getCompositeToHoldGranularUIElements()
							.getListOfAllQuestions();
						//List<Page> pages = reader.getPages("/src/main/resources/TaskDesc/SymmetricEncryption.json");

						//for (Page page : pages) {
						for (Question question : questions) {
							for (Answer answer : question.getAnswers()) {
								if (answer.getClaferDependencies() != null) {
									for (ClaferDependency claferDependency : answer.getClaferDependencies()) {
										if ("->".equals(claferDependency.getOperator())) {
											xmlStrings.add(getXMLForNewAlgorithmInsertion(question, answer, xmlParser, instanceGenerator, claferDependency));

										}
									} // clafer dependency loop
								} // clafer dependency check
								if (answer.getCodeDependencies() != null) {
									for (CodeDependency codeDependency : answer.getCodeDependencies()) {
										//xmlStrings.get(0).elementByID(Constants.Code).addElement(codeDependency.getOption()).addText(codeDependency.getValue() + "");
										Element root = xmlStrings.get(0).getRootElement();

										for (Iterator<Element> element = root.elementIterator(Constants.Code); element.hasNext();) {
											Element codeElement = element.next();
											codeElement.addElement(codeDependency.getOption()).addText(codeDependency.getValue() + "");
										}
									} // code dependency loop
								} // code dependency check
							} // answer loop
						} // question loop
							//} // page loop

						// Process each xml document that is generated.
						for (Document xmlDocument : xmlStrings) {
							processXMLDocument(xmlDocument);
						}
					}

				}
				XSLTagDialog dialog;
				// Show an empty dialog if no clafer feature has been defined.
				if (getTagValueTagData().size() > 0) {
					dialog = new XSLTagDialog(getShell(), getTagValueTagData());
				} else {
					dialog = new XSLTagDialog(getShell());
				}

				if (dialog.open() == Window.OK) {
					// To locate the position of the xsl tag to be introduced in the code.				
					Point selected = getCompositeForXsl().getXslTxtBox().getSelection();
					String xslTxtBoxContent = getCompositeForXsl().getXslTxtBox().getText();
					xslTxtBoxContent = xslTxtBoxContent.substring(0, selected.x) + dialog.getTag().toString() + xslTxtBoxContent.substring(selected.y, xslTxtBoxContent.length());
					getCompositeForXsl().getXslTxtBox().setText(xslTxtBoxContent);
					getCompositeForXsl().colorizeTextBox();
				}

			}

			/**
			 * Process the XML document here to generate values to be displayed to the user for selection.
			 * 
			 * @param xmlDocument
			 *        The serialized object representing the generated XML string.
			 */
			private void processXMLDocument(Document xmlDocument) {
				Element root = xmlDocument.getRootElement();
				// send a slash as a parameter to keep the recursive method as generic as possible.
				processElement(root, "", Constants.SLASH, true);
			}

			/**
			 * This method will process each element individually, and is called recursively to process nested tags.
			 * 
			 * @param xmlElement
			 *        The element under consideration.
			 * @param existingNameToBeDisplayed
			 *        The string that will be displayed to the user for selection.
			 * @param existingDataForXSLDocument
			 *        The actual string that will be added to the code base on the selection that is done by the user.
			 * @param isRoot
			 *        true if the element is the root element.
			 */
			private void processElement(Element xmlElement, String existingNameToBeDisplayed, String existingDataForXSLDocument, boolean isRoot) {
				StringBuilder tagNameToBeDisplayed = new StringBuilder();
				StringBuilder tagDataForXSLDocument = new StringBuilder();

				tagNameToBeDisplayed.append(existingNameToBeDisplayed);
				tagDataForXSLDocument.append(existingDataForXSLDocument);

				if (!isRoot) {
					tagNameToBeDisplayed.append(Constants.DOT);
				}
				tagNameToBeDisplayed.append(xmlElement.getName());
				tagDataForXSLDocument.append(Constants.SLASH);
				tagDataForXSLDocument.append(xmlElement.getName());

				int builderDisplayDataSizeTillRoot = tagNameToBeDisplayed.length();
				int builderTagDataSizeTillRoot = tagDataForXSLDocument.length();

				if (xmlElement.attributeCount() == 0 && !xmlElement.elementIterator().hasNext()) {
					// adding the tag, if there are no attributes.
					getTagValueTagData().put(tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString());
				} else {
					for (Iterator<Attribute> attribute = xmlElement.attributeIterator(); attribute.hasNext();) {
						Attribute attributeData = attribute.next();
						// TODO the name of the task can be fixed here based on what is chosen before.	

						if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
							tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
						}

						if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
							tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
						}

						tagNameToBeDisplayed.append(Constants.DOT);
						tagNameToBeDisplayed.append("@" + attributeData.getName());

						tagDataForXSLDocument.append(Constants.ATTRIBUTE_BEGIN);
						tagDataForXSLDocument.append(attributeData.getName());
						tagDataForXSLDocument.append(Constants.ATTRIBUTE_END);

						getTagValueTagData().put(tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString());

						// Adding the loop for the remaining elements within the attribute loop to have unique tags based on the attributes. 
						for (Iterator<Element> element = xmlElement.elementIterator(); element.hasNext();) {
							Element currentElement = element.next();
							// do not consider the imports tag. The data is not relevant.
							if (!currentElement.getName().equals("Imports")) {
								if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
									tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
								}

								if (isRoot) {
									if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
										tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
									}
								}
								// recursive call
								processElement(currentElement, tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString(), false);
							}
						}
					}
				}

				// A similar loop outside the attribute loop to check the tags that are not nested.
				for (Iterator<Element> element = xmlElement.elementIterator(); element.hasNext();) {
					Element currentElement = element.next();
					// do not consider the imports tag. The data is not relevant.
					if (!currentElement.getName().equals("Imports")) {
						if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
							tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
						}

						if (isRoot) {
							if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
								tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
							}
						}
						// recursive call
						processElement(currentElement, tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString(), false);
					}
				}
			}

			/**
			 * This method is created to be able to exit the nested loops as soon as the correct instance is found.
			 * 
			 * @param question
			 *        The question object from the outer loop.
			 * @param answer
			 *        The answer object from the outer loop.
			 * @param xmlParser
			 *        This object is needed to generate the xml string.
			 * @param instanceGenerator
			 *        This object is needed to generate the instances
			 * @param claferDependency
			 *        The claferDependency from the outer loop
			 * @return
			 */
			private Document getXMLForNewAlgorithmInsertion(Question question, Answer answer, XMLParser xmlParser, InstanceGenerator instanceGenerator, ClaferDependency claferDependency) {
				HashMap<Question, Answer> constraints = new HashMap<>();
				constraints.put(question, answer);
				String constraintOnType = claferDependency.getAlgorithm();
				for (InstanceClafer instance : instanceGenerator.generateInstances(constraints)) {
					for (InstanceClafer childInstance : instance.getChildren()) {
						// check if the name of the constraint on the clafer instance is the same as the one on the clafer dependency from the outer loop.
						if (childInstance.getType().getName().equals(constraintOnType)) {
							return xmlParser.displayInstanceValues(instance, constraints);
						}
					} // child instance loop
				} // instance loop
				return null;
			}
		});
	}

	/**
	 * Return the composite for the XSL page.
	 * 
	 * @return the compositeForXsl
	 */
	public CompositeForXsl getCompositeForXsl() {
		return compositeForXsl;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * 
	 * @param compositeForXsl
	 *        the compositeForXsl to set
	 */
	public void setCompositeForXsl(CompositeForXsl compositeForXsl) {
		this.compositeForXsl = compositeForXsl;

	}
}
