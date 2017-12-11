package de.cognicrypt.codegenerator.primitive.types;

import java.util.ArrayList;

import de.cognicrypt.codegenerator.question.Answer;

public class Primitive {

	private String name;
	private String xmlFile;
	private boolean isSelected;
	private ArrayList<MethodsQuestions> questions; 

	public String getName() {
		return this.name;
	}

	public String getXmlFile() {
		return this.xmlFile;
	}
	
	public ArrayList<MethodsQuestions> getQuestions(){
		return this.questions;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setXmlFile(String xml) {
		this.xmlFile = xml;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}
	public void setQuestions(ArrayList<MethodsQuestions> questions){
		this.questions=questions;
	}

}
