/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;


import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.SubGraphMarker;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueImpl;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueSet;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

/**
 * Returns all direct subtypes of the given type. The type can be given as
 * AttributedElementClass or as String which holds the typename.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>SET&lt;ATTRELEMCLASS&gt; subTypes(type:STRING)</code></dd>
 * <dd>
 * <code>SET&lt;ATTRELEMCLASS&gt; subTypes(typeA:ATTRELEMCLASS)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>type</code> - name of the type to return the subtypes for</dd>
 * <dd><code>typeA</code> - attributed element class, which is the type to
 * return the subtypes for</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>a set containing all subtypes of the given type</dd>
 * <dd><code>Null</code> if one of the parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class Subtypes extends Greql2Function {

	{
		JValueType[][] x = { { JValueType.STRING, JValueType.COLLECTION },
				{ JValueType.ATTRELEMCLASS, JValueType.COLLECTION } };
		signatures = x;

		description = "Returns the set of direct subtypes of the given type.\n"
				+ "The type may be given as string or attributed element class.";

		Category[] c = { Category.SCHEMA_ACCESS };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			SubGraphMarker subgraph, JValue[] arguments)
			throws EvaluateException {

		if (arguments[0].toObject() == null) {
			return new JValueImpl();
		}

		AttributedElementClass clazz = null;
		switch (checkArguments(arguments)) {
		case 0:
			clazz = graph.getSchema().getAttributedElementClass(
					arguments[0].toString());
			return getSubTypes(clazz);
		case 1:
			clazz = arguments[0].toAttributedElementClass();
			return getSubTypes(clazz);
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
	}

	private JValue getSubTypes(AttributedElementClass clazz) {
		JValueSet typeSet = new JValueSet();
		for (AttributedElementClass c : clazz.getDirectSubClasses()) {
			typeSet.add(new JValueImpl(c));
		}
		return typeSet;
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 5;
	}

	@Override
	public double getSelectivity() {
		return 1;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}
}
