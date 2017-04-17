/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
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

	public int hashCode() {
		  assert false : "hashCode not designed";
		  return 42; // any arbitrary constant will do
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