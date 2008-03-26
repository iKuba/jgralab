/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2007 Institute for Software Technology
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
 
package de.uni_koblenz.jgralab.greql2.evaluator.costmodel;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.GraphClass;

/**
 * This class is needed to propagate the size of the currently used graph along
 * different calculate methods
 * 
 * @author Daniel Bildhauer <dbildh@uni-koblenz.de> Summer 2006, Diploma Thesis
 */
public class GraphSize {

	private int vertexCount;

	private int edgeCount;

	private int knownVertexTypes;

	private int knownEdgeTypes;

	private double averageEdgeSubclasses = 2;

	private double averageVertexSubclasses = 2;

	/**
	 * constructs a new GraphSize object for the given graph
	 * 
	 * @param dataGraph
	 *            the datagaph which is base of this GraphSize object
	 */
	public GraphSize(Graph dataGraph) {
		vertexCount = dataGraph.getVCount();
		edgeCount = dataGraph.getECount();
		GraphClass graphClass = (GraphClass) dataGraph
				.getAttributedElementClass();
		knownVertexTypes = graphClass.getOwnVertexClassCount();
		knownEdgeTypes = graphClass.getOwnEdgeClassCount();
	}

	/**
	 * constructs a new GraphSize object with the given number of vertices and
	 * edges
	 * 
	 * @param vCount
	 *            the number of vertices in this GraphSize object
	 * @param eCount
	 *            the number of edge in this GraphSize object
	 * @param knownVertexTypes
	 *            the number of vertextypes this GraphSize object should know
	 * @param knownEdgeTypes
	 *            the number edgetypes this GraphSize object should know
	 */
	public GraphSize(int vCount, int eCount, int knownVertexTypes,
			int knownEdgeTypes) {
		vertexCount = vCount;
		edgeCount = eCount;
		this.knownEdgeTypes = knownEdgeTypes;
		this.knownVertexTypes = knownVertexTypes;
	}

	/**
	 * constructs a copy of the given GraphSize object
	 */
	public GraphSize(GraphSize copiedGraphSize) {
		vertexCount = copiedGraphSize.getVertexCount();
		edgeCount = copiedGraphSize.getEdgeCount();
		knownVertexTypes = copiedGraphSize.getKnownVertexTypes();
		knownEdgeTypes = copiedGraphSize.getKnownEdgeTypes();
	}

	/**
	 * constructs a new GraphSize object with the given number of vertices and
	 * edges and the given GraphClass Usefull for offline-optimization if the
	 * GraphClass is known but the Graph not
	 * 
	 * @param vCount
	 *            the number of vertices in this GraphSize object
	 * @param eCount
	 *            the number of edge in this GraphSize object
	 * @param graphClass
	 *            the GraphClass of the Datagraph to evaluate
	 */
	public GraphSize(GraphClass graphClass, int vCount, int eCount) {
		vertexCount = vCount;
		edgeCount = eCount;
		this.knownEdgeTypes = graphClass.getOwnEdgeClassCount();
		this.knownVertexTypes = graphClass.getOwnVertexClassCount();
	}

	/**
	 * 
	 * @return the number of EdgeTypes this GraphSize object knows
	 */
	public int getKnownEdgeTypes() {
		return knownEdgeTypes;
	}

	/**
	 * 
	 * @return the number of VertexTypes this GraphSize object knows
	 */
	public int getKnownVertexTypes() {
		return knownVertexTypes;
	}

	/**
	 * @return the number of vertices in this graphsize object
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/**
	 * sets the number of vertices in this GraphSize object
	 */
	public void setVertexCount(int count) {
		vertexCount = count;
	}

	/**
	 * @return the number of edge in this graphsize object
	 */
	public int getEdgeCount() {
		return edgeCount;
	}

	/**
	 * sets the number of edges in this GraphSize object
	 */
	public void setEdgeCount(int count) {
		edgeCount = count;
	}

	/**
	 * @return the average number of subclasses of a vertex class
	 */
	public double getAverageVertexSubclasses() {
		return averageVertexSubclasses;
	}

	/**
	 * @return the average number of subclasses of an edge class
	 */
	public double getAverageEdgeSubclasses() {
		return averageEdgeSubclasses;
	}

}