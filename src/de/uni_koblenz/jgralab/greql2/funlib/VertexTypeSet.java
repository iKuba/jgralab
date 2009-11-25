/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2009 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueSet;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

/**
 * Returns a set of all vertex-types that occure in the given structure.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd>
 * <code>SET&lt;ATTRIBUTEDELEMENTCLASS&gt; vertexTypeSet(c:COLLECTION)</code></dd>
 * <dd><code>SET&lt;ATTRIBUTEDELEMENTCLASS&gt; vertexTypeSet(p:PATH)</code></dd>
 * <dd>
 * <code>SET&lt;ATTRIBUTEDELEMENTCLASS&gt; vertexTypeSet(ps:PATHSYSTEM)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>c</code> - collection to return vertex-types for</dd>
 * <dd><code>p</code> - path to return vertex-types for</dd>
 * <dd><code>ps</code> - path system to return vertex-types for</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>a set of all types that occure in the given structure</dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */

public class VertexTypeSet extends AbstractGreql2Function {
	{
		JValueType[][] x = { { JValueType.COLLECTION }, { JValueType.PATH },
				{ JValueType.PATHSYSTEM } };
		signatures = x;
	}

	public JValue evaluate(Graph graph, BooleanGraphMarker subgraph,
			JValue[] arguments) throws EvaluateException {
		switch (checkArguments(arguments)) {
		case 0:
			JValueSet resultSet = new JValueSet();
			for (JValue v : arguments[0].toCollection()) {
				if (v.isVertex()) {
					AttributedElement elem = v.toVertex();
					resultSet.add(new JValue(elem.getAttributedElementClass(),
							elem));
				}
			}
			return resultSet;
		case 1:
			return arguments[0].toPath().vertexTypes();
		case 2:
			return arguments[0].toPathSystem().vertexTypes();
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
	}

	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 30;
	}

	public double getSelectivity() {
		return 1;
	}

	public long getEstimatedCardinality(int inElements) {
		return 10;
	}

}
