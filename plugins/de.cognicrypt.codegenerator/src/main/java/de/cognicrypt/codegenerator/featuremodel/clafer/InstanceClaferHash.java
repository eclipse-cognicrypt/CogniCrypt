package de.cognicrypt.codegenerator.featuremodel.clafer;

import java.util.Objects;

import org.clafer.instance.InstanceClafer;

import de.cognicrypt.codegenerator.Constants;

/**
 * @author Ram InstanceClaferHash extends InstanceClafer by overriding only hashCode method
 */
public class InstanceClaferHash extends InstanceClafer {

	public InstanceClaferHash(final InstanceClafer inputInstance) {
		super(inputInstance.getType(), inputInstance.getId(), inputInstance.getRef(), inputInstance.getChildren());
	}

	public int getHashCode() {
		/**
		 * Method calculates hash for all the children and ORs it with hashcode for ID ,Type and Ref
		 */
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
					hashToChildrenInstances += tempInstanceHash.getHashCode();
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

	/**
	 * Iterates through individual instanceClafer values and summed value is ORed with hash value of id,ref and type.
	 */
	@Override
	public int hashCode() {
		return getHashCode();
	}
}
