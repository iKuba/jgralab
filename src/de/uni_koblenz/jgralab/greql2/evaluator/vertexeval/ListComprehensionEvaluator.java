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

package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.ArrayList;
import java.util.List;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueList;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueTable;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueTuple;
import de.uni_koblenz.jgralab.greql2.schema.ListComprehension;
import de.uni_koblenz.jgralab.greql2.schema.IsTableHeaderOf;

/**
 * Evaluates a ListComprehensionvertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ListComprehensionEvaluator extends ComprehensionEvaluator {

	/**
	 * The ListComprehension-Vertex this evaluator evaluates
	 */
	private ListComprehension vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public ListComprehension getVertex() {
		return vertex;
	}

	/**
	 * Creates a new ListComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public ListComprehensionEvaluator(ListComprehension vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	private Boolean createHeader = null;

	private List<VertexEvaluator> headerEvaluators = null;

	@Override
	protected JValueCollection getResultDatastructure() {
		if (createHeader == null) {
			if (vertex.getFirstIsTableHeaderOfIncidence(EdgeDirection.IN) != null) {
				headerEvaluators = new ArrayList<VertexEvaluator>();
				createHeader = true;
				for (IsTableHeaderOf tableInc : vertex
						.getIsTableHeaderOfIncidences(EdgeDirection.IN)) {
					VertexEvaluator headerEval = vertexEvalMarker
							.getMark(tableInc.getAlpha());
					headerEvaluators.add(headerEval);
				}
			} else {
				createHeader = false;
			}
		}
		if (createHeader) {
			JValueTuple headerTuple = new JValueTuple();
			for (VertexEvaluator headerEvaluator : headerEvaluators) {
				headerTuple.add(headerEvaluator.getResult(subgraph));
			}
			return new JValueTable(headerTuple, false);
		}
		return new JValueList();
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel()
				.calculateCostsListComprehension(this, graphSize);
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		return greqlEvaluator.getCostModel()
				.calculateCardinalityListComprehension(this, graphSize);
	}

}
