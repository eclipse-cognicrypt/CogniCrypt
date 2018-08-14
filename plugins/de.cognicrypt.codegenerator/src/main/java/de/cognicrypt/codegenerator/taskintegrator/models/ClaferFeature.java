/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.FeatureType;

public class ClaferFeature implements Serializable {

	private static final long serialVersionUID = -6403607301359530383L;

	private FeatureType featureType;
	private String featureName;
	private String featureInheritance;
	private ArrayList<ClaferProperty> featureProperties; // <String name, String value>. The names in this collection cannot be repeated.
	private ArrayList<ClaferConstraint> featureConstraints; // each constraint will be generated as a "valid" string in the constraint generator pop up.

	/**
	 * @param featureType
	 * @param featureName
	 * @param featureInheritance
	 * 
	 * 
	 */
	public ClaferFeature(FeatureType featureType, String featureName, String featureInheritance) {
		super();
		this.setFeatureType(featureType);
		this.setFeatureName(featureName);
		this.setFeatureInheritance(featureInheritance);
		this.featureProperties = new ArrayList<ClaferProperty>();
		this.featureConstraints = new ArrayList<ClaferConstraint>();
	}

	/**
	 * @return the featureType
	 */
	public FeatureType getFeatureType() {
		return featureType;
	}

	/**
	 * @param featureType
	 *        the featureType to set
	 */
	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}

	/**
	 * @return the featureName
	 */
	public String getFeatureName() {
		return featureName;
	}

	/**
	 * @param featureName
	 *        the featureName to set
	 */
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureInheritance() {
		return featureInheritance;
	}

	public void setFeatureInheritance(String featureInheritance) {
		this.featureInheritance = featureInheritance;
	}

	public boolean inheritsFrom(String parentFeature) {
		return getFeatureInheritance().equals(parentFeature);
	}

	public boolean inheritsFrom(ClaferFeature parentFeature) {
		return inheritsFrom(parentFeature.getFeatureInheritance());
	}

	/**
	 * @return the properties
	 */
	public ArrayList<ClaferProperty> getFeatureProperties() {
		return featureProperties;
	}

	public void setFeatureProperties(ArrayList<ClaferProperty> featureProperties) {
		this.featureProperties = featureProperties;
	}

	public ArrayList<ClaferProperty> addFeatureProperty(ClaferProperty claferProperty) {
		if (getFeatureProperties() == null) {
			setFeatureProperties(new ArrayList<>());
		}
		getFeatureProperties().add(claferProperty);
		return getFeatureProperties();
	}

	/**
	 * @return <code>true</code> if the feature has non-empty properties, <code>false</code> otherwise
	 */
	public boolean hasProperties() {
		if (!featureProperties.isEmpty()) {
			// check for a non-empty feature property
			for (ClaferProperty featureProperty : getFeatureProperties()) {
				if (!featureProperty.getPropertyName().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * check whether the {@link ClaferFeature} has properties meeting a given constraint
	 * 
	 * @param predicate
	 *        {@link Predicate} that has to be satisfied by at least one {@link ClaferProperty}
	 * @return <code>true</code> if the feature has non-empty properties that satisfy the predicate, <code>false</code> otherwise
	 */
	public boolean hasPropertiesSatisfying(Predicate<? super ClaferProperty> predicate) {
		if (!featureProperties.isEmpty()) {
			// check for a non-empty feature property
			for (ClaferProperty featureProperty : getFeatureProperties()) {
				if (!featureProperty.getPropertyName().isEmpty() && predicate.test(featureProperty)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param needle
	 *        {@link String} name of the property searched for
	 * @return true if the feature has a property with the given name, false otherwise
	 */
	public boolean hasProperty(String needle) {
		for (ClaferProperty featureProperty : getFeatureProperties()) {
			if (featureProperty.getPropertyName() == needle) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<ClaferProperty> getInheritedProperties(ClaferModel refModel) {
		ArrayList<ClaferProperty> inheritedProperties = (ArrayList<ClaferProperty>) getFeatureProperties().clone();

		ClaferFeature parentFeature = refModel.getFeature(getFeatureInheritance());
		if (parentFeature != null) {
			inheritedProperties.addAll(parentFeature.getInheritedProperties(refModel));
		}

		return inheritedProperties;
	}

	/**
	 * get the set of Clafer features this feature relies on, which implies its parent feature and its properties' types
	 * 
	 * @return {@link Set}<{@link String}> of Clafer features names
	 */
	public Set<String> getDependencies() {
		HashSet<String> dependencies = new HashSet<>();

		if (!getFeatureInheritance().isEmpty()) {
			if (getFeatureInheritance().contains(("->"))) {
				for (String feature : getFeatureInheritance().split(" -> ")) {
					dependencies.add(feature);
				}
			} else if (getFeatureInheritance().contains("=")) {
				dependencies.add(getFeatureInheritance().replaceAll("\\s+", "").split("=")[0]);
			} else {
				dependencies.add(getFeatureInheritance());
			}
		}

		for (ClaferProperty fp : getFeatureProperties()) {
			dependencies.add(fp.getPropertyType());
		}

		return dependencies;
	}

	public ArrayList<ClaferConstraint> getFeatureConstraints() {
		return featureConstraints;
	}

	public void setFeatureConstraints(ArrayList<ClaferConstraint> featureConstraints) {
		this.featureConstraints = featureConstraints;
	}

	public ArrayList<ClaferConstraint> addFeatureConstraint(ClaferConstraint claferConstraint) {
		if (getFeatureConstraints() == null) {
			setFeatureConstraints(new ArrayList<>());
		}
		getFeatureConstraints().add(claferConstraint);
		return getFeatureConstraints();
	}

	public boolean hasConstraints() {
		return !featureConstraints.isEmpty();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * return a {@link String} representing of the feature
	 * 
	 * @param includeChildren
	 *        {@link Boolean} whether to include properties and constraints in the output
	 * @return {@link String} representation of the Clafer
	 */
	public String toString(boolean includeChildren) {
		StringBuilder strRepresentation = new StringBuilder();

		if (featureType == Constants.FeatureType.ABSTRACT) {
			strRepresentation.append("abstract ");
		}

		strRepresentation.append(getFeatureName());

		if (!getFeatureInheritance().isEmpty()) {
			strRepresentation.append(": ");
			strRepresentation.append(getFeatureInheritance());
		}

		if (includeChildren) {

			for (ClaferProperty featureProperty : getFeatureProperties()) {
				strRepresentation.append("\n\t");
				strRepresentation.append(featureProperty.toString());
			}

			for (ClaferConstraint featureConstraint : getFeatureConstraints()) {
				strRepresentation.append("\n\t");
				strRepresentation.append(featureConstraint.toString());
			}

			if (!getFeatureProperties().isEmpty() || !getFeatureConstraints().isEmpty()) {
				strRepresentation.append("\n");
			}

		}

		return strRepresentation.toString();
	}

}
