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
 
package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueTypeCollection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This transition accepts a SimplePathDescription. A SimplePathDescription is for instance
 * something like v -->{isExprOf} w.
 * @author Daniel Bildhauer <dbildh@uni-koblenz.de> 
 * Summer 2006, Diploma Thesis
 *
 */
public class SimpleTransition extends Transition {

	/**
	 * The collection of types that are accepted by this transition 
	 */
	protected JValueTypeCollection typeCollection;

	/**
	 * an edge may have valid roles. This string holds the valid role for this
	 * transition. If the transition is valid for all edges, this string is null
	 */
	protected String validEdgeRole;

	/**
	 * this transition may accept edges in direction in, out or any
	 */
	protected AllowedEdgeDirection validDirection;


	/**
	 * returns a string which describes the edge
	 */
	@Override
	public String edgeString() {
		//String desc = "SimpleTransition";
		String desc = "SimpleTransition (Dir:" + validDirection.toString();
		if (typeCollection != null) {
			desc = desc + "\n " + typeCollection.toString() + "\n ";
		}
		desc += ")";
		return desc;
	}


	/* (non-Javadoc)
	 * @see greql2.evaluator.fa.Transition#equalSymbol(greql2.evaluator.fa.EdgeTransition)
	 */
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof SimpleTransition))
			return false;
		SimpleTransition et = (SimpleTransition) t;
		if (!typeCollection.equals(et.typeCollection))
			return false;
		if (validEdgeRole != et.validEdgeRole)
			return false;
		if (validDirection != et.validDirection)
			return false;
		return true;
	}

	/**
	 * Copy-constructor, creates a copy of the given transition
	 */
	protected SimpleTransition(SimpleTransition t, boolean addToStates) {
		super(t, addToStates);
		validDirection = t.validDirection;
		typeCollection = new JValueTypeCollection(t.typeCollection);
	}
	
	
	/**
	 * returns a copy of this transition
	 */
	public Transition copy(boolean addToStates) {
		return new SimpleTransition(this, addToStates);
	}

	/**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all edges that have the right direction, role, startVertexType,
	 * endVertexType, edgeType and even it's possible to define a specific edge.
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param dir
	 *            The direction of the accepted edges, may be EdeDirection.IN,
	 *            EdgeDirection.OUT or EdgeDirection.ANY
	 */
	public SimpleTransition(State start, State end, AllowedEdgeDirection dir) {
		super(start, end);
		validDirection = dir;
		this.typeCollection = new JValueTypeCollection();
	}

	/**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all edges that have the right direction, role, startVertexType,
	 * endVertexType, edgeType and even it's possible to define a specific edge.
	 * This constructor creates a transition to accept a simplePathDescription
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param dir
	 *            The direction of the accepted edges, may be EdeDirection.IN,
	 *            EdgeDirection.OUT or EdgeDirection.ANY
	 * @param typeCollection
	 *            The types which restrict the possible edges
	 * @param role
	 *            The accepted edge role, or null if any role is accepted
	 */
	public SimpleTransition(State start, State end, AllowedEdgeDirection dir,
			JValueTypeCollection typeCollection, String role) {
		super(start, end);
		validDirection = dir;
		validEdgeRole = role;
		this.typeCollection = typeCollection;
	}



	/* (non-Javadoc)
	 * @see greql2.evaluator.fa.Transition#reverse()
	 */
	public void reverse() {
		super.reverse();
		if (validDirection == AllowedEdgeDirection.IN)
			validDirection = AllowedEdgeDirection.OUT;
		else if (validDirection == AllowedEdgeDirection.OUT)
			validDirection = AllowedEdgeDirection.IN;
	}

	/* (non-Javadoc)
	 * @see greql2.evaluator.fa.Transition#isEpsilon()
	 */
	public boolean isEpsilon() {
		return false;
	}


	/* (non-Javadoc)
	 * @see greql2.evaluator.fa.Transition#accepts(jgralab.Vertex, jgralab.Edge, greql2.evaluator.SubgraphTempAttribute)
	 */
	public boolean accepts(Vertex v, Edge e, BooleanGraphMarker subgraph)
			throws EvaluateException {
		if (e == null)
			return false;
		if (validDirection == AllowedEdgeDirection.OUT) {
			if (!e.isNormal())
				return false;
		} else if (validDirection == AllowedEdgeDirection.IN) {
			if (e.isNormal())
				return false;
		} 
	
		// checks if the subgraphattribute is set and if the edge belongs to
		// this subgraph (if the edge belongs to it, also the endvertex must
		// belong to it)
		if (subgraph != null) {
			if (!subgraph.isMarked(e)) {
				return false;
			}	
		}
		// checks if a role restriction is set and if e has the right role
		if (validEdgeRole != null) {
			// TODO check edge role
		}
		// checks if a edgeTypeRestriction is set and if e has the right type
		AttributedElementClass edgeClass = e.getAttributedElementClass();
		if (!typeCollection.acceptsType(edgeClass))
			return false;
		return true;
	}
	
	/**
	 * returns the vertex of the datagraph which can be visited after this transition has fired.
	 * This is the vertex at the end of the edge
	 */
	public  Vertex getNextVertex(Vertex v, Edge e) {
		return e.getThat();
	}
	

}
