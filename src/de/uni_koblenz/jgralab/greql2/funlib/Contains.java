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
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.graphmarker.SubGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueBoolean;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueImpl;
import de.uni_koblenz.jgralab.greql2.jvalue.JValuePath;
import de.uni_koblenz.jgralab.greql2.jvalue.JValuePathSystem;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueSlice;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

/**
 * Checks if a given object is included in a given collection. The object can be
 * anything (for example an integer or a string).
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>BOOL contains(c:COLLECTION, obj:OBJECT)</code></dd>
 * <dd><code>BOOL contains(c:PATH, obj:ATTRELEM)</code></dd>
 * <dd><code>BOOL contains(c:PATHSYSTEM, obj:ATTRELEM)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>c</code> - collection or path or pathsystem to check</dd>
 * <dd><code>obj</code> - object or attributed element to check</dd>
 * <dt><b>Returns:</b></dt>
 * <dd><code>true</code> if the given object is included in the given collection
 * </dd>
 * <dd><code>Null</code> if one of the given parameters is <code>Null</code></dd>
 * <dd><code>false</code> otherwise</dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */

public class Contains extends Greql2Function {

	{
		JValueType[][] x = {
				{ JValueType.COLLECTION, JValueType.OBJECT, JValueType.BOOL },
				{ JValueType.PATH, JValueType.ATTRELEM, JValueType.BOOL },
				{ JValueType.PATHSYSTEM, JValueType.ATTRELEM, JValueType.BOOL },
				{ JValueType.SLICE, JValueType.ATTRELEM, JValueType.BOOL },
				{ JValueType.PATHSYSTEM, JValueType.PATH, JValueType.BOOL },
				{ JValueType.MARKER, JValueType.ATTRELEM, JValueType.BOOL } };
		signatures = x;

		description = "Returns true iff the given structure contains the given element.";

		Category[] c = { Category.COLLECTIONS_AND_MAPS,
				Category.PATHS_AND_PATHSYSTEMS_AND_SLICES };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			SubGraphMarker subgraph, JValue[] arguments)
			throws EvaluateException {

		if (isAnyArgumentNull(arguments)) {
			return new JValueImpl();
		}

		switch (checkArguments(arguments)) {
		case 0:
			JValueCollection col = arguments[0].toCollection();
			JValue val = JValueBoolean.getValue(col.contains(arguments[1]));
			return val;
		case 1:
			JValuePath path = arguments[0].toPath();
			return JValueBoolean
					.getValue(path.contains((GraphElement) arguments[1]
							.toAttributedElement()));
		case 2:
			JValuePathSystem pathsys = arguments[0].toPathSystem();
			return JValueBoolean
					.getValue(pathsys.contains((GraphElement) arguments[1]
							.toAttributedElement()));
		case 3:
			JValueSlice slice = arguments[0].toSlice();
			return JValueBoolean
					.getValue(slice.contains((GraphElement) arguments[1]
							.toAttributedElement()));
		case 4:
			JValuePathSystem pathsys2 = arguments[0].toPathSystem();
			JValuePath path2 = arguments[1].toPath();
			return JValueBoolean.getValue(pathsys2.containsPath(path2));
		case 5:
			BooleanGraphMarker m = (BooleanGraphMarker) arguments[0].toObject();
			return JValueBoolean.getValue(m.isMarked(arguments[1]
					.toAttributedElement()));
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
		return 0.2;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

}
