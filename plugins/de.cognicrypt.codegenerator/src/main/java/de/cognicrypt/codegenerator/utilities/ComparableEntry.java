package de.cognicrypt.codegenerator.utilities;

import java.util.AbstractMap.SimpleEntry;

public class ComparableEntry<K, V> extends SimpleEntry<K, V> implements Comparable<ComparableEntry<K, V>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7202623997902577640L;

	public ComparableEntry(K key, V value) {
		super(key, value);
	}

	@Override
	public int compareTo(ComparableEntry<K, V> comp) {
		if (this.equals(comp)) {
			return 0;
		}
		if (!(this.getKey() instanceof Comparable && comp.getKey() instanceof Comparable && this.getValue() instanceof Comparable && comp.getValue() instanceof Comparable)) {
			return -1;
		} else {
			@SuppressWarnings("unchecked")
			final Comparable<K> thisX = (Comparable<K>) this.getKey();
			return thisX.compareTo(comp.getKey());
		}
	}

}
