package crossing.e1.configurator.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.swing.JComboBox;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.*;

import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class TaskSelectionPage extends WizardPage {
	private ClaferModel model;
	private Spinner taskCombo;
	private Composite container;
	private List<AstConcreteClafer> tasks;
	private List<Integer> performanceLevel;
	private Button securityLevelSecured;
	private Button securityLevelInSecured;
	private Spinner outPutSize;
	private Label label1;
	private Label label2;
	
	
	
	public TaskSelectionPage(List<AstConcreteClafer> items,ClaferModel claferModel) {
		super("Select Task");
		setTitle("Chonfigure");
		setDescription("Here the user selects her options and security levels");
		new InstanceGenerator(claferModel);
		tasks = items;
		this.model=claferModel;
		
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;


		securityLevelSecured=new Button(container, SWT.RADIO);
		securityLevelSecured.setToolTipText("Secured Encryption");
		securityLevelSecured.setText("Secure");
		securityLevelSecured.setEnabled(true);
		securityLevelInSecured=new Button(container, SWT.RADIO);
		securityLevelInSecured.setToolTipText("Insecure");
		securityLevelInSecured.setText("Do Not Secure");
		securityLevelInSecured.setEnabled(true);
		
		securityLevelSecured.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				securityLevelInSecured.setSelection(false);
				securityLevelSecured.setSelection(true);
				outPutSize.setVisible(true);
				taskCombo.setVisible(true);
				label1.setVisible(true);
				label2.setVisible(true);
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		
		});
		
		securityLevelInSecured.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				securityLevelSecured.setSelection(false);
				securityLevelInSecured.setSelection(true);
				outPutSize.setVisible(false);
				taskCombo.setVisible(false);
				label1.setVisible(false);
				label2.setVisible(false);
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		
		});
		

		label1 = new Label(container, SWT.NONE);
		label1.setText("Select Performance");
		taskCombo = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		System.out.println("here we go for claffer buddy"+model.getChildByName("c0_performance", model.getModel().getChildren()));
		
		taskCombo.setValues(2, 1, 4, 0, 1, 1);		
		label2 = new Label(container, SWT.NONE);
		label2.setText("Select Key length");
		
		outPutSize=new Spinner(container,SWT.BORDER | SWT.SINGLE);
		outPutSize.setValues(128,128,2048,0, 2, 10);
		outPutSize.setToolTipText("key leangth");
		outPutSize.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent outPutSizeEvent) {
				
				List <AstConstraint> ast= model.getConstraints();
				ast=ast.stream().filter(child -> child.getExpr().toString().contains("performance . ref = 4")).collect(Collectors.toList());
				for(AstConstraint x: ast){
					System.out.println(model.getConstraints().size()+" context "+x.getContext()+" "+ x.getContext());
				}
						
			
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//taskCombo.setL setLayoutData(gd);
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(true);

	}
	
	public Integer getKeyLengthSelction() {
		return taskCombo.getSelection();
	}
	public Integer getOutPutSelection(){
		return outPutSize.getSelection();
	}
	public boolean isSecure(){
		return securityLevelSecured.getSelection();
	}
}
