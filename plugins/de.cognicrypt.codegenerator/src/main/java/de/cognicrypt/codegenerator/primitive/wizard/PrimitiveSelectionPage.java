package de.cognicrypt.codegenerator.primitive.wizard;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.primitive.types.PrimitiveJSONReader;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class PrimitiveSelectionPage extends WizardPage {

	private ComboViewer primitiveComboSelection;
	private Composite container;
	private Label selectTaskLabel;

	/**
	 * Create the wizard.
	 */
	public PrimitiveSelectionPage() {
		super("wizardPage");
		setTitle("Primitive Integration");
		setDescription("Algorithm Type");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NULL);
		this.container.setBounds(10, 10, 200, 300);
		final GridLayout layout = new GridLayout(4, false);
		this.container.setLayout(layout);
		final List<Primitive> primitives = PrimitiveJSONReader.getPrimitiveTypes();

		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblPleaseChooseThe = new Label(container, SWT.NONE);
		lblPleaseChooseThe.setText("Please choose the type of algorithm");
		new Label(container, SWT.NONE);

		this.primitiveComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.primitiveComboSelection.setContentProvider(ArrayContentProvider.getInstance());
		this.primitiveComboSelection.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object primitive) {
				if (primitive instanceof Primitive) {
					final Primitive current = (Primitive) primitive;
					return current.getName();
				}
				return super.getText(primitive);
			}
		});
		// add primitives in combo
		this.primitiveComboSelection.setInput(primitives);

		this.primitiveComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			final Primitive selectedPrimitive = (Primitive) selection.getFirstElement();

			PrimitiveSelectionPage.this.primitiveComboSelection.refresh();
			setPageComplete(selectedPrimitive != null);
		});
		this.primitiveComboSelection.setSelection(new StructuredSelection(primitives.get(0)));
	}

	public Primitive getSelectedPrimitive() {
		return (Primitive) ((IStructuredSelection) this.primitiveComboSelection.getSelection()).getFirstElement();
	}
public boolean canFlipToNextPage(){
	return true;
}

}
