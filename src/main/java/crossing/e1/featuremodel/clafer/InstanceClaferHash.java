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


	public InstanceClaferHash(InstanceClafer inputInstance) {

		super(inputInstance.getType(), inputInstance.getId(), inputInstance.getRef(), inputInstance.getChildren());
		

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
		/*
		 * This method only 
		 */
//		int hashValue = 0;
//		for (InstanceClafer childInstanceClafer : this.getChildren()) {
//			InstanceClaferHash tempInstanceHash = null;
//			if (childInstanceClafer.hasRef() && (childInstanceClafer.getType().isPrimitive() != true)
//					&& (childInstanceClafer.getRef().getClass().toString().contains("Integer") == false)
//					&& (childInstanceClafer.getRef().getClass().toString().contains("String") == false)
//					&& (childInstanceClafer.getRef().getClass().toString().contains("Boolean") == false)) {
//				tempInstanceHash = new InstanceClaferHash((InstanceClafer) childInstanceClafer.getRef());
//				if (tempInstanceHash != null) {
//
//					hashValue += tempInstanceHash.getHashCode();
//				}
//				if ((childInstanceClafer.getRef().getClass().toString().contains("Integer") == true)
//						|| (childInstanceClafer.getRef().getClass().toString().contains("String") == true)
//						|| (childInstanceClafer.getRef().getClass().toString().contains("Boolean") == true)) {
//					hashValue += childInstanceClafer.getRef().hashCode();
//				}
//			}
//
//		}	
//		if (this.hasRef())
//			hashValue += this.getRef().hashCode();
//		hashValue+=this.getType().hashCode();
//		hashValue+=this.getId();
		return this.getHashCode();
	}
	public int getHashCode() {

		int hashToChildrenInstances = 0;
		for (InstanceClafer childInstanceClafer : this.getChildren()) {
		InstanceClaferHash tempInstanceHash = null;
		if (childInstanceClafer.hasRef() && (childInstanceClafer.getType().isPrimitive() != true)
				&& (childInstanceClafer.getRef().getClass().toString().contains("Integer") == false)
				&& (childInstanceClafer.getRef().getClass().toString().contains("String") == false)
				&& (childInstanceClafer.getRef().getClass().toString().contains("Boolean") == false)) {
			tempInstanceHash = new InstanceClaferHash((InstanceClafer) childInstanceClafer.getRef());
			if (tempInstanceHash != null) {

				hashToChildrenInstances += tempInstanceHash.getHashCode();
			}
			if ((childInstanceClafer.getRef().getClass().toString().contains("Integer") == true)
					|| (childInstanceClafer.getRef().getClass().toString().contains("String") == true)
					|| (childInstanceClafer.getRef().getClass().toString().contains("Boolean") == true)) {
				hashToChildrenInstances += childInstanceClafer.getRef().hashCode();
			}
		}

	}	
		return this.getType().hashCode() ^ this.getId() ^ hashToChildrenInstances ^ Objects.hash(this.getRef());

	}

}
