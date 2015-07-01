package crossing.e1.configurator.wizard;

import java.util.Arrays;
import java.util.List;

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

public class MyPageTwo extends WizardPage {

	private Composite container;
	private ComboViewer algorithmClass;
	private Label label1;
	private Label label2;
	private List<InstanceClafer> options;
	InstanceGenerator instances;

	public MyPageTwo(InstanceGenerator instances) {
		super("Second page");
		setTitle("Available options");
		setDescription("User can choose below mentioned algorithm(s) for encryption");
		this.instances=instances;

	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;

		label1 = new Label(container, SWT.NONE);
		label1.setText("Select Algorithm Type");
		label2 = new Label(container, SWT.NONE);
		algorithmClass = new ComboViewer(container, SWT.BORDER | SWT.SINGLE);
		algorithmClass.setContentProvider(ArrayContentProvider.getInstance());
		algorithmClass.setLabelProvider((new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		}));

		algorithmClass.setInput(instances.getInstances());
		algorithmClass
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {

						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						String value="";
						InstanceClafer b=(InstanceClafer)selection.getFirstElement();
						
						
						for(InstanceClafer in: b.getChildren()){
							
							value=value+in.getRef();
							
						}
						label2.setText(value);
						label2.setVisible(true);
						System.out.println("Vlaue is "+value);
						if (selection.size() > 0) {
							canFlipToNextPage();
						}
					}

				});

		

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

	public void addField(String labelText) {
		System.out.println("adding field");
		Label label = new Label(container, SWT.NONE);
		label.setText(labelText);
		container.layout();
	}

	public boolean canFlipToNextPage() {

		return false;
	}

	public String getText1() {
		return "";
	}
}
