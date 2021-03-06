/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2013 Institute for Software Technology
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
package de.uni_koblenz.jgralab.algolib.algorithms.shortest_paths;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.algorithms.AlgorithmStates;
import de.uni_koblenz.jgralab.algolib.algorithms.AlgorithmTerminatedException;
import de.uni_koblenz.jgralab.algolib.algorithms.StructureOrientedAlgorithm;
import de.uni_koblenz.jgralab.algolib.algorithms.search.BreadthFirstSearch;
import de.uni_koblenz.jgralab.algolib.functions.BooleanFunction;
import de.uni_koblenz.jgralab.algolib.functions.DoubleFunction;
import de.uni_koblenz.jgralab.algolib.functions.Function;
import de.uni_koblenz.jgralab.algolib.functions.IntFunction;
import de.uni_koblenz.jgralab.algolib.functions.adapters.MethodCallToDoubleFunctionAdapter;
import de.uni_koblenz.jgralab.algolib.problems.DistancesFromVertexSolver;
import de.uni_koblenz.jgralab.algolib.problems.ShortestPathsFromVertexSolver;
import de.uni_koblenz.jgralab.algolib.visitors.Visitor;

public class ShortestPathsWithBFS extends StructureOrientedAlgorithm implements
		DistancesFromVertexSolver, ShortestPathsFromVertexSolver {

	private BreadthFirstSearch bfs;

	public ShortestPathsWithBFS(Graph graph, BreadthFirstSearch bfs) {
		this(graph, bfs, null);
	}

	public ShortestPathsWithBFS(Graph graph, BreadthFirstSearch bfs,
			BooleanFunction<Edge> navigable) {
		super(graph, navigable);
		this.bfs = bfs;
	}

	@Override
	public void addVisitor(Visitor visitor) {
		checkStateForSettingVisitors();
		// the algorithm is set implicitly to the bfs
		bfs.addVisitor(visitor);
	}

	@Override
	public void removeVisitor(Visitor visitor) {
		checkStateForSettingVisitors();
		bfs.removeVisitor(visitor);
	}

	@Override
	public ShortestPathsWithBFS normal() {
		super.normal();
		return this;
	}

	@Override
	public ShortestPathsWithBFS reversed() {
		super.reversed();
		return this;
	}

	@Override
	public ShortestPathsWithBFS undirected() {
		super.undirected();
		return this;
	}

	@Override
	public boolean isHybrid() {
		return true;
	}

	@Override
	public void disableOptionalResults() {
	}

	@Override
	public ShortestPathsWithBFS execute(Vertex start)
			throws AlgorithmTerminatedException {
		bfs.reset();
		bfs.setGraph(graph);
		bfs.setNavigable(navigable);
		bfs.setTraversalDirection(traversalDirection);
		startRunning();
		bfs.withLevel().withParent().execute(start);
		done();
		return this;
	}

	@Override
	protected void done() {
		state = bfs.getState() == AlgorithmStates.STOPPED ? AlgorithmStates.FINISHED
				: bfs.getState();
	}

	@Override
	public DoubleFunction<Vertex> getDistance() {
		checkStateForResult();
		return new MethodCallToDoubleFunctionAdapter<Vertex>() {

			private IntFunction<Vertex> level = bfs.getLevel();

			@Override
			public double get(Vertex parameter) {
				return level.get(parameter);
			}

			@Override
			public boolean isDefined(Vertex parameter) {
				return level.isDefined(parameter);
			}

		};
	}

	@Override
	public Function<Vertex, Edge> getParent() {
		checkStateForResult();
		return bfs.getParent();
	}

}
