package de.cognicrypt.codegenerator.taskintegrator.models;

import java.util.ArrayList;
import java.util.Iterator;

public class ClaferModel implements Iterable<ClaferFeature> {

	private ArrayList<ClaferFeature> claferModel;

	public ClaferModel() {
		claferModel = new ArrayList<>();
	}
	
	public ClaferModel(ArrayList<ClaferFeature> claferModel) {
		this.claferModel = claferModel;
	}
	
	public ArrayList<ClaferFeature> getClaferModel() {
		return claferModel;
	}
	
	public void add(ClaferFeature claferFeature) {
		claferModel.add(claferFeature);
	}
	
	public void remove(ClaferFeature claferFeature) {
		claferModel.remove(claferFeature);
	}

	@Override
	public Iterator iterator() {
		return claferModel.iterator();
	}
}
