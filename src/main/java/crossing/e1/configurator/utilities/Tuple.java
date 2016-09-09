package crossing.e1.configurator.utilities;

/***
 * Auxiliary class for tuples.
 * 
 * @author Stefan Krueger
 *
 */
public class Tuple<X, Y> implements Comparable<Tuple<X, Y>> {

	public X x;
	public Y y;

	public Tuple(final X l, final Y r) {
		this.x = l;
		this.y = r;
	}

	@Override
	public int compareTo(final Tuple<X, Y> comp) {
		if (this.equals(comp)) {
			return 0;
		}
		if (!(this.x instanceof Comparable && comp.x instanceof Comparable && this.y instanceof Comparable && comp.y instanceof Comparable)) {
			return -1;
		} else {
			@SuppressWarnings("unchecked")
			final Comparable<X> thisX = (Comparable<X>) this.x;
			return thisX.compareTo(comp.x);
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Tuple)) {
			return false;
		} else {
			@SuppressWarnings("unchecked")
			final Tuple<X, Y> t = (Tuple<X, Y>) o;
			return t.y.equals(this.y) && t.x.equals(this.x);
		}
	}
}