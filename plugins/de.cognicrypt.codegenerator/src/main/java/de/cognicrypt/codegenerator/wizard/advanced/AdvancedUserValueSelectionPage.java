package de.cognicrypt.codegenerator.wizard.advanced;

import java.util.ArrayList;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.PropertiesMapperUtil;
import de.cognicrypt.core.Constants;

public class AdvancedUserValueSelectionPage extends WizardPage {

	private Composite container;
	private final List<PropertyWidget> userConstraints = new ArrayList<>();
	private final AstConcreteClafer taskClafer;

	public AdvancedUserValueSelectionPage(final ClaferModel claferModel, final AstConcreteClafer taskClafer) {
		super(Constants.SELECT_PROPERTIES);
		setTitle(Constants.PROPERTIES);
		setDescription(Constants.DESCRIPTION_VALUE_SELECTION_PAGE);
		this.taskClafer = taskClafer;
	}

	private void createConstraints(final AstClafer parent, final AstAbstractClafer inputClafer, final Group titledPanel) {

		if (inputClafer.hasChildren()) {
			for (final AstConcreteClafer in : inputClafer.getChildren()) {
				createConstraints(parent, in, titledPanel);
			}
		}
		if (inputClafer.hasRef()) {
			createConstraints(parent, inputClafer.getRef().getTargetType(), titledPanel);
		}

		if (inputClafer.getSuperClafer() != null) {
			createConstraints(parent, inputClafer.getSuperClafer(), titledPanel);
		}
	}

	private void createConstraints(final AstClafer parent, final AstClafer inputClafer, final Group titledPanel) {

		if (inputClafer.hasChildren()) {
			if (inputClafer.getGroupCard() != null && inputClafer.getGroupCard().getLow() >= 1) {
				this.userConstraints
					.add(new PropertyWidget(titledPanel, parent, (AstConcreteClafer) inputClafer, ClaferModelUtils.removeScopePrefix(inputClafer.getName()), 1, 0, 1024, 0, 1, 1));
			} else {
				for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
					createConstraints(parent, childClafer, titledPanel);
				}
			}
		}
		if (inputClafer.hasRef()) {
			if (inputClafer.getRef().getTargetType().isPrimitive() && !(inputClafer.getRef().getTargetType().getName().contains("string"))) {
				if (ClaferModelUtils.isConcrete(inputClafer)) {
					final Group childPanel = createPanel2("", titledPanel);
					this.userConstraints.add(
						new PropertyWidget(childPanel, parent, (AstConcreteClafer) inputClafer, ClaferModelUtils.removeScopePrefix(inputClafer.getName()), 1, 0, 1024, 0, 1, 1));
				}
			} else if (PropertiesMapperUtil.getenumMap().containsKey(inputClafer.getRef().getTargetType())) {
				createConstraints(inputClafer, inputClafer.getRef().getTargetType(), titledPanel);
			} else if (!inputClafer.getRef().getTargetType().isPrimitive()) {
				if (!ClaferModelUtils.removeScopePrefix(inputClafer.getRef().getTargetType().getName()).equals(titledPanel.getText())) {
					if (inputClafer.getRef().getTargetType().hasChildren()) {
						final Group childPanel = createPanel2(ClaferModelUtils.removeScopePrefix(inputClafer.getRef().getTargetType().getName()), titledPanel);
						createConstraints(inputClafer, inputClafer.getRef().getTargetType(), childPanel);
					}
				} else {
					//same panel as main algorithm type (e.g., kda in secure pwd storage)
					createConstraints(inputClafer, inputClafer.getRef().getTargetType(), titledPanel);
				}
			}
		}

		if (inputClafer.getSuperClafer() != null) {
			createConstraints(parent, inputClafer.getSuperClafer(), titledPanel);
		}
	}

	@Override
	public void createControl(final Composite parent) {
		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(20, 10, 450, 200);
		final GridLayout layout = new GridLayout();
		this.container.setLayout(layout);
		layout.numColumns = 1;

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.container, "de.cognicrypt.codegenerator.help_id_2");

		// Add every constraints to its parent and group it as a separate titled
		// panel

		for (final AstClafer taskAlgorithm : this.taskClafer.getChildren()) {
			if (!taskAlgorithm.getRef().getTargetType().hasRef()) {
				final Group titledPanel = createPanel(ClaferModelUtils.removeScopePrefix(taskAlgorithm.getRef().getTargetType().getName()), this.container);
				createConstraints(this.taskClafer, taskAlgorithm, titledPanel);
			}
			setControl(this.container);
		}
	}

	private Group createPanel(final String name, final Composite parent) {
		final Group titledPanel = new Group(parent, SWT.LEFT);
		titledPanel.setText(name);
		final Font boldFont = new Font(titledPanel.getDisplay(), new FontData("Arial", 12, SWT.BOLD));
		titledPanel.setFont(boldFont);
		final GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		titledPanel.setLayout(layout2);
		titledPanel.setLayout((new RowLayout(SWT.VERTICAL)));
		return titledPanel;
	}

	private Group createPanel2(final String name, final Composite parent) {
		final Group titledPanel2 = new Group(parent, SWT.LEFT);
		titledPanel2.setText(name);
		final Font boldFont = new Font(titledPanel2.getDisplay(), new FontData("Arial", 10, SWT.NONE));
		titledPanel2.setFont(boldFont);
		final GridLayout layout3 = new GridLayout();
		layout3.numColumns = 4;
		layout3.makeColumnsEqualWidth = true;
		layout3.horizontalSpacing = 0;
		layout3.marginLeft = 0;
		titledPanel2.setLayout(layout3);
		titledPanel2.setLayout((new RowLayout(SWT.HORIZONTAL)));
		return titledPanel2;

	}

	public List<PropertyWidget> getConstraints() {
		return this.userConstraints;
	}

	public boolean getPageStatus() {
		return PropertyWidget.status;
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.container.setFocus();
		}
	}
}
