package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;
import java.util.Iterator;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.SubGraphMarker;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueImpl;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

public class Successor extends Greql2Function {
	{
		JValueType[][] x = { { JValueType.OBJECT, JValueType.COLLECTION,
				JValueType.OBJECT } };
		signatures = x;

		description = "Returns the successor of the given object in the given collection.";

		Category[] c = { Category.COLLECTIONS_AND_MAPS };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph, SubGraphMarker subgraph,
			JValue[] arguments) throws EvaluateException {
		if (checkArguments(arguments) < 0) {
			throw new WrongFunctionParameterException(this, arguments);
		}
		JValue obj = arguments[0];
		JValueCollection coll = arguments[1].toCollection();
		Iterator<JValue> i = coll.iterator();
		while (i.hasNext()) {
			JValue current = i.next();
			if (current.equals(obj) && i.hasNext()) {
				return i.next();
			}
		}
		return new JValueImpl();
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return inElements.get(1) / 2;
	}

	@Override
	public double getSelectivity() {
		return 1;
	}

}
