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
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

/**
 * Returns the end-vertex of a given path.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>VERTEX endVertex(p:PATH)</code></dd>
 * <dd><code>VERTEX endVertex(p:EDGE)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>p</code> - path or edge to determine the end-vertex for</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>the end-vertex of the given path or edge</dd>
 * <dd><code>Null</code> if one of the parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @see StartVertex
 * @author ist@uni-koblenz.de
 * 
 */

public class EndVertex extends Greql2Function {

	{
		JValueType[][] x = { { JValueType.EDGE, JValueType.VERTEX },
				{ JValueType.PATH, JValueType.VERTEX } };
		signatures = x;

		description = "Returns the end-vertex of the given edge or path.";

		Category[] c = { Category.PATHS_AND_PATHSYSTEMS_AND_SLICES,
				Category.GRAPH };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			SubGraphMarker subgraph, JValue[] arguments)
			throws EvaluateException {

		JValue firstParameter = arguments[0];
		if (!firstParameter.isEdge() && !firstParameter.isPath()) {
			return new JValueImpl();
		}

		switch (checkArguments(arguments)) {
		case 0:
			return new JValueImpl(firstParameter.toEdge().getOmega());
		case 1:
			return new JValueImpl(firstParameter.toPath().getEndVertex());
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 2;
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
