package de.uni_koblenz.jgralab.greql2.funlib.collections;

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.Path;
import de.uni_koblenz.jgralab.greql2.types.PathSystem;

public class Contains extends Function {

	public Contains() {
		super(
				"Returns true iff the collection or path contains the element $el$.",
				2, 1, 0.2, Category.COLLECTIONS_AND_MAPS);
	}

	public <T> Boolean evaluate(PCollection<T> s, T el) {
		return s.contains(el);
	}

	public <T> Boolean evaluate(Path p, GraphElement el) {
		return p.contains(el);
	}

	public <T> Boolean evaluate(PathSystem p, GraphElement el) {
		return p.contains(el);
	}
}