/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.featuremodel.clafer;

import java.util.Objects;

import org.clafer.instance.InstanceClafer;

import de.cognicrypt.core.Constants;

/**
 * InstanceClaferHash extends InstanceClafer to only override {@link InstanceClaferHash#hashCode() hashcode()}
 * 
 * @author Ram Kamath
 */
public class InstanceClaferHash extends InstanceClafer {

	public InstanceClaferHash(final InstanceClafer inputInstance) {
		super(inputInstance.getType(), inputInstance.getId(), inputInstance.getRef(), inputInstance.getChildren());
	}

	@Override
	public int hashCode() {
		//Iterates through individual instanceClafer values and summed value is ORed with hash value of id,ref and type.
		int hashToChildrenInstances = 0;
		for (final InstanceClafer childInstanceClafer : this.getChildren()) {
			InstanceClaferHash tempInstanceHash = null;
			if (childInstanceClafer
				.hasRef() && !childInstanceClafer.getType().isPrimitive() && !childInstanceClafer.getRef().getClass().toString().contains(Constants.INTEGER) && !childInstanceClafer
					.getRef().getClass().toString().contains(Constants.STRING) && !childInstanceClafer.getRef().getClass().toString().contains(Constants.BOOLEAN)) {
				/**
				 * recursively find hashcode for all the children for a clafer if it is not primitive
				 */
				tempInstanceHash = new InstanceClaferHash((InstanceClafer) childInstanceClafer.getRef());
				if (tempInstanceHash != null) {
					hashToChildrenInstances += tempInstanceHash.hashCode();
				}
				/**
				 * add hashcode from standard object.hasCode() method for primitive types
				 */
				if (childInstanceClafer.getRef().getClass().toString().contains(Constants.INTEGER) || childInstanceClafer.getRef().getClass().toString()
					.contains(Constants.STRING) || childInstanceClafer.getRef().getClass().toString().contains(Constants.BOOLEAN)) {
					hashToChildrenInstances += childInstanceClafer.getRef().hashCode();
				}
			}
		}
		return getType().hashCode() ^ getId() ^ hashToChildrenInstances ^ Objects.hash(getRef());
	}
}
