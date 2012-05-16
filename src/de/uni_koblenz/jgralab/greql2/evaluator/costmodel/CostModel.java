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

package de.uni_koblenz.jgralab.greql2.evaluator.costmodel;

import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.AggregationPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.AlternativePathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.BackwardVertexSetEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ConditionalExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.DeclarationEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.EdgePathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.EdgeRestrictionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.EdgeSetExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ExponentiatedPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ForwardVertexSetEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.FunctionApplicationEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.Greql2ExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.IntermediateVertexPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.IteratedPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ListConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ListRangeConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.MapComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.MapConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.OptionalPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.PathExistenceEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.QuantifiedExpressionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.RecordConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.RecordElementEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SequentialPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SetComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SetConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SimpleDeclarationEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.SimplePathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TableComprehensionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TransposedPathDescriptionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TupleConstructionEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.TypeIdEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VariableEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexSetExpressionEvaluator;

/**
 * This interface is implemented by all costmodels.
 * 
 * The returntype of the several methods is a VertexCosts, its a 3-Tuple
 * containing
 * 
 * <ul>
 * <li>the costs of evaluating this vertex itself once (ownEvaluationCosts),</li>
 * <li>the costs all evaluations of this vertex that are needed when evaluating
 * the current query and</li> *
 * <li>the costs of evaluating the whole subtree below this vertex plus
 * ownEvaluationCosts (subtreeEvaluationCosts).</li>
 * </ul>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface CostModel {

	/**
	 * @param costModel
	 *            another CostModel
	 * @return <code>true</code>, if this costmodel and the given one are
	 *         logical equivalent, that means, if the costfunction is identical
	 */
	public boolean isEquivalent(CostModel costModel);

	public VertexCosts calculateCostsAlternativePathDescription(
			AlternativePathDescriptionEvaluator e);

	public VertexCosts calculateCostsBackwardVertexSet(
			BackwardVertexSetEvaluator e);

	public VertexCosts calculateCostsListComprehension(ComprehensionEvaluator e);

	public VertexCosts calculateCostsConditionalExpression(
			ConditionalExpressionEvaluator e);

	public VertexCosts calculateCostsDeclaration(DeclarationEvaluator e);

	public VertexCosts calculateCostsEdgePathDescription(
			EdgePathDescriptionEvaluator e);

	public VertexCosts calculateCostsEdgeRestriction(EdgeRestrictionEvaluator e);

	public VertexCosts calculateCostsEdgeSetExpression(
			EdgeSetExpressionEvaluator e);

	public VertexCosts calculateCostsExponentiatedPathDescription(
			ExponentiatedPathDescriptionEvaluator e);

	public VertexCosts calculateCostsForwardVertexSet(
			ForwardVertexSetEvaluator e);

	public VertexCosts calculateCostsFunctionApplication(
			FunctionApplicationEvaluator e);

	public VertexCosts calculateCostsGreql2Expression(
			Greql2ExpressionEvaluator e);

	public VertexCosts calculateCostsIntermediateVertexPathDescription(
			IntermediateVertexPathDescriptionEvaluator e);

	public VertexCosts calculateCostsIteratedPathDescription(
			IteratedPathDescriptionEvaluator e);

	public VertexCosts calculateCostsListConstruction(
			ListConstructionEvaluator e);

	public VertexCosts calculateCostsListRangeConstruction(
			ListRangeConstructionEvaluator e);

	public VertexCosts calculateCostsOptionalPathDescription(
			OptionalPathDescriptionEvaluator e);

	public VertexCosts calculateCostsPathExistence(PathExistenceEvaluator e);

	public VertexCosts calculateCostsQuantifiedExpression(
			QuantifiedExpressionEvaluator e);

	public VertexCosts calculateCostsRecordConstruction(
			RecordConstructionEvaluator e);

	public VertexCosts calculateCostsRecordElement(RecordElementEvaluator e);

	public VertexCosts calculateCostsSequentialPathDescription(
			SequentialPathDescriptionEvaluator e);

	public VertexCosts calculateCostsSetComprehension(
			SetComprehensionEvaluator e);

	public VertexCosts calculateCostsSetConstruction(SetConstructionEvaluator e);

	public VertexCosts calculateCostsSimpleDeclaration(
			SimpleDeclarationEvaluator e);

	public VertexCosts calculateCostsSimplePathDescription(
			SimplePathDescriptionEvaluator e);

	public VertexCosts calculateCostsAggregationPathDescription(
			AggregationPathDescriptionEvaluator e);

	public VertexCosts calculateCostsTableComprehension(
			TableComprehensionEvaluator e);

	public VertexCosts calculateCostsTupleConstruction(
			TupleConstructionEvaluator e);

	public VertexCosts calculateCostsTypeId(TypeIdEvaluator e);

	/**
	 * Calculates the evaluation costs of a TransposedPathDescription.
	 * 
	 * @param e
	 * @param graphSize
	 * @return a tuple (subtreeCosts, vertexCosts) that describes the evaluation
	 *         costs of the given evaluator
	 */
	public VertexCosts calculateCostsTransposedPathDescription(
			TransposedPathDescriptionEvaluator e);

	public VertexCosts calculateCostsVariable(VariableEvaluator e);

	public VertexCosts calculateCostsVertexSetExpression(
			VertexSetExpressionEvaluator e);

	/*
	 * The methods to calculate the cardinality
	 */

	public long calculateCardinalityBackwardVertexSet(
			BackwardVertexSetEvaluator e);

	public long calculateCardinalityListComprehension(ComprehensionEvaluator e);

	public long calculateCardinalityConditionalExpression(
			ConditionalExpressionEvaluator e);

	public long calculateCardinalityDeclaration(DeclarationEvaluator e);

	public long calculateCardinalityEdgeSetExpression(
			EdgeSetExpressionEvaluator e);

	public long calculateCardinalityForwardVertexSet(ForwardVertexSetEvaluator e);

	public long calculateCardinalityFunctionApplication(
			FunctionApplicationEvaluator e);

	public long calculateCardinalityListConstruction(ListConstructionEvaluator e);

	public long calculateCardinalityListRangeConstruction(
			ListRangeConstructionEvaluator e);

	public long calculateCardinalityRecordConstruction(
			RecordConstructionEvaluator e);

	public long calculateCardinalitySetComprehension(SetComprehensionEvaluator e);

	public long calculateCardinalitySetConstruction(SetConstructionEvaluator e);

	public long calculateCardinalitySimpleDeclaration(
			SimpleDeclarationEvaluator e);

	public long calculateCardinalityTableComprehension(
			TableComprehensionEvaluator e);

	public long calculateCardinalityTupleConstruction(
			TupleConstructionEvaluator e);

	public long calculateCardinalityVertexSetExpression(
			VertexSetExpressionEvaluator e);

	/*
	 * The methods to calculate the selectivity
	 */

	public double calculateSelectivityFunctionApplication(
			FunctionApplicationEvaluator e);

	public double calculateSelectivityPathExistence(PathExistenceEvaluator e);

	public double calculateSelectivityTypeId(TypeIdEvaluator e);

	public long calculateVariableAssignments(VariableEvaluator e);

	public VertexCosts calculateCostsMapConstruction(
			MapConstructionEvaluator mapConstructionEvaluator);

	public long calculateCardinalityMapConstruction(
			MapConstructionEvaluator mapConstructionEvaluator);

	public VertexCosts calculateCostsMapComprehension(
			MapComprehensionEvaluator mapComprehensionEvaluator);

	public long calculateCardinalityMapComprehension(
			MapComprehensionEvaluator mapComprehensionEvaluator);

}
