/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.taskintegrator.controllers.ClaferCompiler;
import de.cognicrypt.core.Constants;

public class ClaferModel implements Iterable<ClaferFeature>, Serializable {

	private static final long serialVersionUID = -6369043905278063238L;

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

	/**
	 * @return number of clafers in this model as an int
	 */
	public int getFeatureCount() {
		return claferModel.size();
	}

	public void add(ClaferFeature claferFeature) {
		claferModel.add(claferFeature);
	}

	/**
	 * add all of the clafers from the given {@link ClaferModel} to this instance
	 *
	 * @param claferModel
	 *        model the children of which are to be added
	 */
	public void add(ClaferModel claferModel) {
		for (ClaferFeature cfrFeature : claferModel) {
			add(cfrFeature);
		}
	}

	public void remove(ClaferFeature claferFeature) {
		claferModel.remove(claferFeature);
	}

	/**
	 * check whether the model contains a clafer with the given name
	 *
	 * @param featureName
	 *        needle as a {@link String}
	 * @return boolean success of the search
	 */
	public boolean hasFeature(String featureName) {
		boolean featureFound = false;

		for (ClaferFeature cfrFeature : claferModel) {
			featureFound |= cfrFeature.getFeatureName().equals(featureName);
		}

		return featureFound;
	}

	/**
	 * get a clafer from the model by name
	 *
	 * @param featureName
	 *        needle as a {@link String}
	 * @return {@link ClaferFeature} with the given name, <code>null</code> if not found
	 */
	public ClaferFeature getFeature(String featureName) {
		for (ClaferFeature cfrFeature : this) {
			if (cfrFeature.getFeatureName().equals(featureName)) {
				return cfrFeature;
			}
		}

		return null;
	}

	/**
	 * get a shallow copy of the ClaferModel that contains a shallow copy of the feature list (new list but same feature objects)
	 */
	@Override
	public ClaferModel clone() {
		return new ClaferModel((ArrayList<ClaferFeature>) claferModel.clone());
	}

	/**
	 * return a shallow copy containing only features that the predicate is true for
	 * 
	 * @param predicate
	 *        {@link Predicate}&lt;? super {@link ClaferFeature}&gt; to test on the features
	 * @return {@link ClaferModel} containing references to the successfully filtered features
	 */
	public ClaferModel getIf(Predicate<? super ClaferFeature> predicate) {
		ClaferModel shallowCopy = this.clone();
		shallowCopy.getClaferModel().removeIf(predicate.negate());
		return shallowCopy;
	}

	/**
	 * return an iterator over the Clafer features in the model
	 */
	@Override
	public Iterator iterator() {
		return claferModel.iterator();
	}

	/**
	 * return a {@link String} representation of the complete model in Clafer syntax
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// use the iterator to serialize all children
		for (ClaferFeature cfrFeature : this) {
			sb.append(cfrFeature);
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * get missing inheritance or property types in this model with respect to a given feature
	 * 
	 * @param refFeature
	 *        reference feature to sift for unimplemented features
	 * @return a model containing only those features that have been added
	 */
	public ClaferModel getMissingFeatures(ClaferFeature refFeature) {
		ClaferModel addedFeatures = new ClaferModel();

		// find missing inherited feature
		if (!refFeature.getFeatureInheritance().isEmpty()) {
			boolean parentFound = false;

			// TODO consider trimming whitespaces from the string before splitting
			String needleFeature;

			if (refFeature.getFeatureInheritance().contains("->")) {
				needleFeature = refFeature.getFeatureInheritance().split("->")[0].trim();
			} else {
				needleFeature = refFeature.getFeatureInheritance();
			}

			for (String primitive : Constants.CLAFER_PRIMITIVE_TYPES) {
				if (primitive.equals(needleFeature)) {
					parentFound = true;
					break;
				}
			}

			for (ClaferFeature cfrFeature : claferModel) {
				if (cfrFeature.getFeatureName().equals(needleFeature)) {
					parentFound = true;
					break;
				}
			}

			// remember missing inherited feature
			if (!parentFound) {
				ClaferFeature parentFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, needleFeature, "");
				addedFeatures.add(parentFeature);
			}
		}

		// find missing property types
		for (ClaferProperty fp : refFeature.getFeatureProperties()) {
			boolean propertyTypeFound = false;

			// do not implement Clafer primitives
			for (String primitive : Constants.CLAFER_PRIMITIVE_TYPES) {
				if (primitive.equals(fp.getPropertyType())) {
					propertyTypeFound = true;
					break;
				}
			}

			for (ClaferFeature cfrFeature : claferModel) {
				if (cfrFeature.getFeatureName().equals(fp.getPropertyType())) {
					propertyTypeFound = true;
					break;
				}
			}

			// prevent implementing features repeatedly
			if (addedFeatures.hasFeature(fp.getPropertyType())) {
				propertyTypeFound = true;
			}

			// remember missing property types			
			if (!fp.getPropertyType().isEmpty() && !propertyTypeFound) {
				ClaferFeature propertyTypeFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, fp.getPropertyType(), "");
				addedFeatures.add(propertyTypeFeature);
			}
		}

		return addedFeatures;
	}

	/**
	 * add the missing features according to getMissingFeatures method
	 *
	 * @param refFeature
	 *        reference feature to consider for search of unused features
	 * @return a model containing only those features that have been added
	 */
	public ClaferModel implementMissingFeatures(ClaferFeature refFeature) {
		ClaferModel missingFeatures = getMissingFeatures(refFeature);

		for (ClaferFeature missingFeature : missingFeatures) {
			add(missingFeature);
		}

		return missingFeatures;
	}

	/**
	 * get a model containing the unused features of this model
	 * 
	 * @return
	 */
	public ClaferModel getUnusedFeatures() {
		// TODO switch logic, copy list and delete everything that is used
		ClaferModel unusedFeatures = new ClaferModel();

		for (ClaferFeature cfrFeature : claferModel) {
			// check usage of cfrFeature			
			boolean used = false;
			if (cfrFeature.hasProperties()) {
				used = true;
			}
			if (cfrFeature.hasConstraints()) {
				used = true;
			}

			// if abstract and somebody inherits -> used
			for (ClaferFeature refFeature : claferModel) {
				if (refFeature.getFeatureInheritance().equals(cfrFeature.getFeatureName())) {
					// usage found: refFeature inherits from cfrFeature
					used = true;
				}
			}

			for (ClaferFeature refFeature : claferModel) {
				for (ClaferProperty featureProp : refFeature.getFeatureProperties()) {
					if (featureProp.getPropertyType().equals(cfrFeature.getFeatureName())) {
						// usage found: featureProp is of type cfrFeature
						used = true;
					}
				}
			}

			if (!used) {
				unusedFeatures.add(cfrFeature);
			}
		}

		return unusedFeatures;
	}

	/**
	 * write the model into a file
	 * 
	 * @param filename
	 *        {@link String} filename of the file to write
	 * @return success of the writing as a {@link Boolean}
	 */
	public boolean toFile(String filename) {
		try {
			FileWriter fileWriter = new FileWriter(filename);
			fileWriter.write(this.toString());
			fileWriter.close();
		} catch (IOException ex) {
			Activator.getDefault().logError(ex);
			return false;
		}

		return true;
	}

	/**
	 * try to compile the model in a given file
	 * 
	 * @param filename
	 *        {@link String} filename of the .cfr file to compile
	 * @return {@link Boolean} success of the compilation
	 */
	public static boolean compile(String filename) {
		if (ClaferCompiler.execute(filename)) {
			return true;
		}

		return false;
	}

	/**
	 * Get the parent feature of a property. Note, that this method finds a feature containing the given reference and does not check equality in terms of values.
	 * 
	 * @param referenceProperty
	 *        reference of the property to be found
	 * @return first {@link ClaferFeature} that contains a matching reference to the given property
	 */
	public ClaferFeature getParentFeatureOfProperty(ClaferProperty referenceProperty) {
		for (ClaferFeature cfrFeature : claferModel) {
			for (ClaferProperty featureProperty : cfrFeature.getFeatureProperties()) {
				// check, if this property is pointed to by referenceProperty
				if (featureProperty == referenceProperty) {
					return cfrFeature;
				}
			}
		}
		return null;
	}

	/**
	 * serialize the model into a binary
	 * 
	 * @param filename
	 *        target filename as a {@link String}
	 * @return success of the serialization
	 */
	public boolean toBinary(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(this);

			oos.close();
			fos.close();

			return true;
		} catch (Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return false;
	}

	/**
	 * factory method to create an instance from a binary
	 * 
	 * @param filename
	 *        source filename as a {@link String}
	 * @return {@link ClaferModel} instance if successful, <code>null</code> else
	 */
	public static ClaferModel createFromBinaries(String filename) {
		ClaferModel result = null;

		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);

			result = (ClaferModel) ois.readObject();

			ois.close();
			fis.close();
		} catch (Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return result;
	}
}
