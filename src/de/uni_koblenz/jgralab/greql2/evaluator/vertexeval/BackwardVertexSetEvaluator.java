/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * For bug reports, documentation and further information, visit
 *
 *                         https://github.com/jgralab/jgralab
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

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;
import de.uni_koblenz.jgralab.greql2.schema.BackwardVertexSet;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * evaluates a BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BackwardVertexSetEvaluator extends
		PathSearchEvaluator<BackwardVertexSet> {

	public BackwardVertexSetEvaluator(BackwardVertexSet vertex, QueryImpl query) {
		super(vertex, query);
	}

	private boolean initialized = false;

	private VertexEvaluator<? extends Expression> targetEval = null;

	private final void initialize(InternalGreqlEvaluator evaluator) {
		PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(
				EdgeDirection.IN).getAlpha();
		PathDescriptionEvaluator<?> pathDescEval = (PathDescriptionEvaluator<?>) query
				.getVertexEvaluator(p);

		Expression targetExpression = (Expression) vertex
				.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha();
		targetEval = query.getVertexEvaluator(targetExpression);
		NFA revertedNFA = NFA.revertNFA(pathDescEval.getNFA(evaluator));
		searchAutomaton = new DFA(revertedNFA);

		initialized = true;
	}

	@Override
	public PSet<Vertex> evaluate(InternalGreqlEvaluator evaluator) {
		if (!initialized) {
			initialize(evaluator);
		}
		Vertex targetVertex = null;
		targetVertex = (Vertex) targetEval.getResult(evaluator);

		return ReachableVertices.search(evaluator, targetVertex,
				searchAutomaton);
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts() {
		BackwardVertexSet bwvertex = getVertex();
		Expression targetExpression = (Expression) bwvertex
				.getFirstIsTargetExprOfIncidence().getAlpha();
		VertexEvaluator<? extends Expression> vertexEval = query
				.getVertexEvaluator(targetExpression);
		long targetCosts = vertexEval.getCurrentSubtreeEvaluationCosts();
		PathDescription p = (PathDescription) bwvertex
				.getFirstIsPathOfIncidence().getAlpha();
		PathDescriptionEvaluator<? extends PathDescription> pathDescEval = (PathDescriptionEvaluator<? extends PathDescription>) query
				.getVertexEvaluator(p);
		long pathDescCosts = pathDescEval.getCurrentSubtreeEvaluationCosts();
		long searchCosts = Math.round(pathDescCosts * searchFactor
				* Math.sqrt(query.getGraphSize().getEdgeCount()));
		long ownCosts = searchCosts;
		long iteratedCosts = ownCosts * getVariableCombinations();
		long subtreeCosts = targetCosts + pathDescCosts + iteratedCosts;
		return new VertexCosts(ownCosts, iteratedCosts, subtreeCosts);
	}

	@Override
	public long calculateEstimatedCardinality() {
		return 5;
	}

}
