package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetValue extends Function {

	public GetValue() {
		super(
				"Returns the value of the attribute/component $name$ of element $el$/record $rec$.",
				Category.GRAPH);
	}

	public Object evaluate(AttributedElement el, String name) {
		return el.getAttribute(name);
	}

	public Object evaluate(Record rec, String name) {
		return rec.getComponent(name);
	}

}
