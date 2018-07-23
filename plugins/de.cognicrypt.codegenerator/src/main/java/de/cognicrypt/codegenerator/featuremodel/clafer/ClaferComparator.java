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

import java.util.Comparator;

import org.clafer.instance.InstanceClafer;

public class ClaferComparator implements Comparator<InstanceClafer> {

	@Override
	public int compare(final InstanceClafer left, final InstanceClafer right) {
		return -Integer.compare(getSecurityLevel(left), getSecurityLevel(right));
	}

	private Integer getSecurityLevel(final InstanceClafer instance) {
		for (final InstanceClafer innerInst : instance.getChildren()) {
			// check if the clafer has a child named security
			if (innerInst.getType().getName().contains("security")) {
				final Object securityClafer = innerInst.getRef();
				// if the security clafer is an integer, use it for sorting
				if (securityClafer instanceof Integer) {
					return (Integer) securityClafer;
				}
				// if the security clafer is a reference clafer (e.g., a child of "abstract Security : Enum -> integer") use the target value of the reference for sorting
				else if (securityClafer instanceof InstanceClafer) {
					InstanceClafer instanceClaferSecurity = (InstanceClafer) securityClafer;
					if (instanceClaferSecurity.getRef() instanceof Integer) {
						return (Integer) instanceClaferSecurity.getRef();
					}
				}
			}
		}
		return -1;
	}

}
