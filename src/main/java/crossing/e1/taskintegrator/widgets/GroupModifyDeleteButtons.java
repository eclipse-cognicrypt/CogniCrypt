package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.taskintegrator.models.ClaferFeature;
import crossing.e1.taskintegrator.wizard.QuestionDialog;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;

import java.util.ArrayList;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class GroupModifyDeleteButtons extends Group {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	int counter=0;
	public GroupModifyDeleteButtons(Composite parent, Question questionParam,ArrayList<ClaferFeature> claferFeatures) {
		super(parent, SWT.RIGHT_TO_LEFT);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		setLayout(rowLayout);
		
		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING
		            | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Question");
		        int response = confirmationMessageBox.open();
		        if (response == SWT.YES){
		        	((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent().getParent()).deleteQuestion(((CompositeGranularUIForHighLevelQuestions)btnDelete.getParent().getParent()).getQuestion());// (1) CompositeGranularUIForClaferFeature, (2) composite inside (3) CompositeToHoldGranularUIElements
		        }
			}
		});
		btnDelete.setLayoutData(new RowData(66, SWT.DEFAULT));
		btnDelete.setText("Delete");
		
		Button btnModify = new Button(this, SWT.NONE);
		btnModify.setLayoutData(new RowData(66, SWT.DEFAULT));
		btnModify.setText("Modify");
		QuestionDialog qstnDialog=new QuestionDialog(parent.getShell(),questionParam,claferFeatures);
		btnModify.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				int response=qstnDialog.open();
				if(response==Window.OK){
					Question modifiedQuestion=qstnDialog.getQuestionDetails();
					((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent().getParent()).modifyHighLevelQuestion(questionParam, modifiedQuestion);
				}
			}
		});

		this.setSize(SWT.DEFAULT, 40);
	}

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
