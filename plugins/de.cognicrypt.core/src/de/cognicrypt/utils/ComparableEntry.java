package de.cognicrypt.utils;

import java.util.AbstractMap.SimpleEntry;

public class ComparableEntry<K, V> extends SimpleEntry<K, V> implements Comparable<ComparableEntry<K, V>> {

	private static final long serialVersionUID = -7202623997902577640L;

	public ComparableEntry(final K key, final V value) {
		super(key, value);
	}

	@Override
	public int compareTo(final ComparableEntry<K, V> comp) {
		if (equals(comp)) {
			return 0;
		}
		if (!(getKey() instanceof Comparable && comp.getKey() instanceof Comparable && getValue() instanceof Comparable && comp.getValue() instanceof Comparable)) {
			return -1;
		}

		@SuppressWarnings("unchecked")
		final Comparable<K> thisX = (Comparable<K>) getKey();
		return thisX.compareTo(comp.getKey());
	}

}
