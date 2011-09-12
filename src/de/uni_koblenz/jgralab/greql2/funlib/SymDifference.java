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

/**
 * Returns the symetric difference of two given sets. That means a set, that
 * contains all elements that are either in the first given set or in the second
 * given set. Elements that are in both sets are not in the symetric difference.
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd>
 * <code>SET&lt;OBJECT&gt; symDifference(s1:SET&lt;OBJECT&gt;, s2:SET&lt;OBJECT&gt;)</code>
 * </dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>s1</code> - first set</dd>
 * <dd><code>s2</code> - second set</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>the symetric difference between the two given sets</dd>
 * <dd><code>Null</code> if one of the parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @see Difference
 * @see Intersection
 * @see Union
 * @author ist@uni-koblenz.de
 * 
 */
public class SymDifference extends Greql2Function {
	{
		JValueType[][] x = { { JValueType.COLLECTION, JValueType.COLLECTION,
				JValueType.COLLECTION } };
		signatures = x;

		description = "Returns the symmetric difference of the two given collections.\n"
				+ "Both collections are converted to sets first.  The symmetric\n"
				+ "difference are all elements of both sets, which are not contained\n"
				+ "in both sets.";

		Category[] c = { Category.COLLECTIONS_AND_MAPS };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			SubGraphMarker subgraph, JValue[] arguments)
			throws EvaluateException {
		if (checkArguments(arguments) == -1) {
			throw new WrongFunctionParameterException(this, arguments);
		}
		if (isAnyArgumentNull(arguments)) {
			return new JValueImpl();
		}
		JValueSet firstSet = arguments[0].toCollection().toJValueSet();
		JValueSet secondSet = arguments[1].toCollection().toJValueSet();
		return firstSet.symmetricDifference(secondSet);
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		long elems = 0;
		for (Long i : inElements) {
			elems += i;
		}
		return elems * 3;
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
