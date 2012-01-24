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

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluatorImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Comprehension;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;

public abstract class ComprehensionEvaluator extends VertexEvaluator {

	private VariableDeclarationLayer varDeclLayer = null;
	private VertexEvaluator resultDefinitionEvaluator = null;

	@Override
	public abstract Comprehension getVertex();

	public ComprehensionEvaluator(GreqlEvaluatorImpl eval) {
		super(eval);
	}

	protected abstract PCollection<Object> getResultDatastructure();

	protected final VertexEvaluator getResultDefinitionEvaluator() {
		if (resultDefinitionEvaluator == null) {
			Expression resultDefinition = getVertex()
					.getFirstIsCompResultDefOfIncidence(EdgeDirection.IN)
					.getAlpha();
			resultDefinitionEvaluator = vertexEvalMarker
					.getMark(resultDefinition);
		}
		return resultDefinitionEvaluator;
	}

	protected final VariableDeclarationLayer getVariableDeclationLayer(
			Graph graph) {
		if (varDeclLayer == null) {
			Declaration d = getVertex().getFirstIsCompDeclOfIncidence(
					EdgeDirection.IN).getAlpha();
			DeclarationEvaluator declEval = (DeclarationEvaluator) vertexEvalMarker
					.getMark(d);
			varDeclLayer = (VariableDeclarationLayer) declEval.getResult(graph);
		}
		return varDeclLayer;
	}

	@Override
	public Object evaluate(Graph graph) {
		VariableDeclarationLayer declLayer = getVariableDeclationLayer();
		VertexEvaluator resultDefEval = getResultDefinitionEvaluator();
		PCollection<Object> resultCollection = getResultDatastructure();
		declLayer.reset();
		while (declLayer.iterate()) {
			Object localResult = resultDefEval.getResult(graph);
			resultCollection = resultCollection.plus(localResult);
		}
		return resultCollection;
	}

}
