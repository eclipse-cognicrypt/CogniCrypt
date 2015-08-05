package crossing.e1.configurator.wizard;


import java.util.Map;

import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class InstanceListPage extends WizardPage {

	private Composite container;
	private ComboViewer algorithmClass;
	private Label label1;
	private InstanceGenerator instance;
	String value="";
boolean val= false;
	public InstanceListPage(InstanceGenerator inst) {
		super("Second page");
		setTitle("Available options");
		setDescription("Available conbinations have been listed below , choose a value");
		this.instance=inst;
		

	}
	
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);
		setPageComplete(false);

		label1 = new Label(container, SWT.NONE);
		label1.setText("Select Algorithm Type");		
		Map<String,InstanceClafer> inst=instance.getInstances();
		algorithmClass = new ComboViewer(container, SWT.COMPOSITION_SELECTION);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setInput(inst.keySet());
		algorithmClass.setLabelProvider((new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		}));
		algorithmClass
				.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						
						String b=(String)selection.getFirstElement().toString();
						setValue(instance.displayInstanceValues(instance.getInstances().get(b),""));
						if (selection.size() > 0) {
							val=true;
							setPageComplete(true);
						}
					}

				});

		setControl(container);
		;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
@Override
	public boolean canFlipToNextPage() {

		return val;
	}
}
