/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
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
package de.uni_koblenz.jgralab.algolib.algorithms.strong_components.visitors;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.visitors.Visitor;

/**
 * This visitor visits all graph elements of a graph that are relevant for
 * computing a reduced graph. It can be used during the computation of strong
 * components for performing tasks whenever a representative vertex of a strong
 * component or a reduced edge (edge connecting two strong components) is
 * encountered. Each graph element can be visited at most once.
 * 
 * @author strauss@uni-koblenz.de
 * 
 */
public interface ReducedGraphVisitor extends Visitor {

	/**
	 * Visits the representative vertex of a strong component.
	 * 
	 * @param v
	 *            the representative vertex to visit
	 */
	public void visitRepresentativeVertex(Vertex v);

	/**
	 * Visits a reduced edge.
	 * 
	 * @param e
	 *            the reduced edge to visit
	 */
	public void visitReducedEdge(Edge e);
}