package de.uni_koblenz.jgralab.algolib.algorithms.search;

import java.util.Iterator;
import java.util.Stack;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.functions.BooleanFunction;
import de.uni_koblenz.jgralab.algolib.problems.TraversalFromVertexSolver;
import de.uni_koblenz.jgralab.graphmarker.ArrayVertexMarker;

/**
 * This is the iterative implementation of depth first search. It behaves the
 * same way as the <code>RecursiveDepthFirstSearch</code> without depending on
 * the call stack of the Java VM. This avoids the problem arising from the
 * possible <code>StackOverflowError</code> that can occur using the
 * <code>RecursiveDepthFirstSearch</code>. However, this implementation requires
 * to make the optional result <code>parent</code> mandatory. This and the
 * additional overhead for runtime variables makes this implementation using
 * more memory than its recursive variant.
 * 
 * @author strauss@uni-koblenz.de
 * 
 */
public class IterativeDepthFirstSearch extends DepthFirstSearch {

	private ArrayVertexMarker<Iterator<Edge>> remainingIncidences;
	private Stack<Vertex> incompleteVertices;

	public IterativeDepthFirstSearch(Graph graph,
			BooleanFunction<GraphElement> subgraph, boolean directed,
			BooleanFunction<Edge> navigable) {
		super(graph, subgraph, navigable);
	}

	public IterativeDepthFirstSearch(Graph graph) {
		super(graph);
	}

	@Override
	public void reset() {
		super.reset();
		parent = new ArrayVertexMarker<Edge>(graph);
		remainingIncidences = new ArrayVertexMarker<Iterator<Edge>>(graph);
		incompleteVertices = new Stack<Vertex>();
	}

	@Override
	public void disableOptionalResults() {
		checkStateForSettingParameters();
		level = null;
		rorder = null;
	}

	@Override
	public DepthFirstSearch withParent() {
		checkStateForSettingParameters();
		throw new UnsupportedOperationException(
				"The result \"parent\" is mandatory for iterative DFS and doesn't need to be explicitly activated.");
	}

	@Override
	public DepthFirstSearch withoutParent() {
		checkStateForSettingParameters();
		throw new UnsupportedOperationException(
				"The result \"parent\" is mandatory for iterative DFS and cannot be deactivated.");
	}

	@Override
	public TraversalFromVertexSolver execute(Vertex root) {
		if (subgraph != null && !subgraph.get(root)
				|| visitedVertices.get(root)) {
			return this;
		}
		startRunning();

		if (level != null) {
			level.set(root, 0);
		}
		number.set(root, num);
		visitors.visitRoot(root);

		// remainingIncidences.mark(root, root.incidences().iterator());
		incompleteVertices.push(root);

		while (!incompleteVertices.isEmpty()) {
			// get next vertex from stack and visit it if it is the first time
			Vertex currentVertex = incompleteVertices.pop();

			if (!visitedVertices.get(currentVertex)) {
				vertexOrder[num] = currentVertex;

				number.set(currentVertex, num);
				remainingIncidences.mark(currentVertex, currentVertex
						.incidences(searchDirection).iterator());
				visitors.visitVertex(currentVertex);

				visitedVertices.set(currentVertex, true);
				num++;
			}

			Iterator<Edge> currentIncidences = remainingIncidences
					.getMark(currentVertex);
			if (currentIncidences.hasNext()) {
				Edge currentEdge = currentIncidences.next();
				if (visitedEdges.get(currentEdge) || subgraph != null
						&& !subgraph.get(currentEdge) || navigable != null
						&& !navigable.get(currentEdge)) {
					incompleteVertices.push(currentVertex);
					continue;
				}
				Vertex nextVertex = currentEdge.getThat();
				assert (subgraph == null || subgraph.get(nextVertex));
				// visit current edge
				edgeOrder[eNum] = currentEdge;
				visitors.visitEdge(currentEdge);
				visitedEdges.set(currentEdge, true);
				eNum++;

				if (visitedVertices.get(nextVertex)) {
					visitors.visitFrond(currentEdge);
					if (!rnumber.isDefined(nextVertex)) {
						visitors.visitBackwardArc(currentEdge);
					} else if (number.get(nextVertex) > number
							.get(currentVertex)) {
						visitors.visitForwardArc(currentEdge);
					} else {
						visitors.visitCrosslink(currentEdge);
					}
					incompleteVertices.push(currentVertex);
				} else {
					if (level != null) {
						level.set(nextVertex, level.get(currentVertex) + 1);
					}

					parent.set(currentEdge.getThat(), currentEdge);

					visitors.visitTreeEdge(currentEdge);
					incompleteVertices.push(currentVertex);
					incompleteVertices.push(nextVertex);
				}
			} else {
				rnumber.set(currentVertex, rNum);
				if (rorder != null) {
					rorder[rNum] = currentVertex;
				}
				visitors.leaveVertex(currentVertex);
				rNum++;
				remainingIncidences.removeMark(currentVertex);
				// leave tree edge leading to the current vertex
				if (currentVertex != root) {
					visitors.leaveTreeEdge(parent.get(currentVertex));
				}
			}
			// nothing may follow here!!!
		}

		done();
		return this;
	}

}
