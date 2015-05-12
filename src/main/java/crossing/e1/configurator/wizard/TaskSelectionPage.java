package crossing.e1.configurator.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.*;

public class TaskSelectionPage extends WizardPage {
	private ComboViewer taskCombo;
	private Composite container;
	private List<AstConcreteClafer> tasks;

	public TaskSelectionPage(List<AstConcreteClafer> items) {
		super("Select Task");
		setTitle("Select Taks");
		setDescription("Here the user selects his task");
		tasks = items;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label label1 = new Label(container, SWT.NONE);
		label1.setText("Select Task");

		taskCombo = new ComboViewer(container, SWT.BORDER | SWT.SINGLE);
		
		taskCombo.setContentProvider(ArrayContentProvider.getInstance());
		taskCombo.setLabelProvider(new LabelProvider() {
		  @Override
		  public String getText(Object element) {
		    if (element instanceof AstClafer) {
		      AstClafer clafer = (AstClafer) element;
		      return clafer.getName();
		    }
		    return super.getText(element);
		  }
		});
		
		taskCombo.setInput(tasks);
		taskCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event){
				
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					setPageComplete(true);
				}
			}

		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//taskCombo.setL setLayoutData(gd);
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}
	
	

	public AstClafer getSelction() {
		return (AstClafer) ((IStructuredSelection) taskCombo.getSelection()).getFirstElement();
	}
}
