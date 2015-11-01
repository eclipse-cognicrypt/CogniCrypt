/**
 * Copyright 2015 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package crossing.e1.featuremodel.clafer;

import java.util.Objects;

import org.clafer.ast.AstClafer;
import org.clafer.instance.InstanceClafer;

/**
 *
 * @author Ram
 * 
 *         InstanceClaferHash extends InstanceClafer by overriding only hashCode
 *         method
 */
public class InstanceClaferHash extends InstanceClafer {

	private AstClafer type;;
	private int id;
	private Object ref;
	private InstanceClafer[] children;

	public InstanceClaferHash(InstanceClafer inputInstance) {

		super(inputInstance.getType(), inputInstance.getId(), inputInstance
				.getRef(), inputInstance.getChildren());
		type = inputInstance.getType();
		id = inputInstance.getId();
		ref = inputInstance.getRef();
		children = inputInstance.getChildren();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode() Iterates through individual
	 * instanceClafer values and summed value is ORed with hash value of id,ref
	 * and type.
	 */
	@Override
	public int hashCode() {
		int hashValue = 0;
		for (InstanceClafer childInstanceClafer : children) {
			InstanceClaferHash tempInstanceHash = null;
			if (childInstanceClafer.hasRef()
					&& (childInstanceClafer.getType().isPrimitive() != true)
					&& (childInstanceClafer.getRef().getClass().toString().contains("Integer") == false)
					&& (childInstanceClafer.getRef().getClass().toString().contains("String") == false)
					&& (childInstanceClafer.getRef().getClass().toString().contains("Boolean") == false))
				tempInstanceHash = new InstanceClaferHash((InstanceClafer) childInstanceClafer.getRef());
			if (tempInstanceHash != null) {

				hashValue += tempInstanceHash.getHashCode();
			}
		}

		return hashValue;
	}

	public int getHashCode() {

		int hashToChildrenInstances = 0;

		for (InstanceClafer childInstanceClafer : children) {

			if (childInstanceClafer.hasRef()) {
				hashToChildrenInstances += childInstanceClafer.getRef().hashCode();
			}
			hashToChildrenInstances += childInstanceClafer.getType().hashCode();
		}
		return type.hashCode() ^ id ^ hashToChildrenInstances
				^ Objects.hash(ref);

	}

}
