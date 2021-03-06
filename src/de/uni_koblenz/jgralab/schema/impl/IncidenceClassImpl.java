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
package de.uni_koblenz.jgralab.schema.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public class IncidenceClassImpl implements IncidenceClass {

	protected IncidenceClassImpl(EdgeClass edgeClass, VertexClass vertexClass,
			String rolename, int minEdgesAtVertex, int maxEdgesAtVertex,
			IncidenceDirection direction, AggregationKind aggregationKind) {
		super();
		if (aggregationKind == null) {
			this.aggregationKind = AggregationKind.NONE;
		} else {
			this.aggregationKind = aggregationKind;
		}
		this.edgeClass = edgeClass;
		this.maxEdgesAtVertex = maxEdgesAtVertex;
		this.minEdgesAtVertex = minEdgesAtVertex;
		if (rolename == null) {
			this.rolename = "";
		} else {
			this.rolename = rolename;
		}
		this.direction = direction;
		this.vertexClass = vertexClass;
		this.subsettedIncidenceClasses = new HashSet<IncidenceClass>();
		this.incidenceClassIdInSchema = ((SchemaImpl) edgeClass.getSchema())
				.getNextIncidenceClassId();
	}

	private AggregationKind aggregationKind;

	private final EdgeClass edgeClass;

	private final VertexClass vertexClass;

	private final int maxEdgesAtVertex;

	private final int minEdgesAtVertex;

	private String rolename;

	private final IncidenceDirection direction;

	private final Set<IncidenceClass> subsettedIncidenceClasses;

	private Set<IncidenceClass> allSubsettedIncidenceClasses;

	private final int incidenceClassIdInSchema;

	@Override
	public AggregationKind getAggregationKind() {
		return aggregationKind;
	}

	@Override
	public IncidenceClass getOpposite() {
		if (edgeClass.getFrom() == this) {
			return edgeClass.getTo();
		} else {
			return edgeClass.getFrom();
		}
	}

	@Override
	public void setAggregationKind(AggregationKind kind) {
		if ((kind != AggregationKind.NONE)
				&& (getOpposite().getAggregationKind() != AggregationKind.NONE)) {
			throw new SchemaException(
					"At least one end of each EdgeClass must be of AggregationKind NONE at EdgeClass "
							+ edgeClass.getQualifiedName());
		}
		this.aggregationKind = kind;
	}

	@Override
	public IncidenceDirection getDirection() {
		return direction;
	}

	@Override
	public EdgeClass getEdgeClass() {
		return edgeClass;
	}

	@Override
	public int getMax() {
		return maxEdgesAtVertex;
	}

	@Override
	public int getMin() {
		return minEdgesAtVertex;
	}

	@Override
	public String getRolename() {
		return rolename;
	}

	@Override
	public Set<IncidenceClass> getOwnSubsettedIncidenceClasses() {
		return subsettedIncidenceClasses;
	}

	@Override
	public Set<IncidenceClass> getSubsettedIncidenceClasses() {
		if (((VertexClassImpl) vertexClass).isFinished()) {
			return this.allSubsettedIncidenceClasses;
		}
		Set<IncidenceClass> result = new HashSet<IncidenceClass>();
		result.addAll(subsettedIncidenceClasses);
		for (IncidenceClass ic : subsettedIncidenceClasses) {
			result.addAll(ic.getSubsettedIncidenceClasses());
		}
		return result;
	}

	@Override
	public VertexClass getVertexClass() {
		return vertexClass;
	}

	public void addSubsettedIncidenceClass(IncidenceClass other) {
		if (((VertexClassImpl) vertexClass).isFinished()) {
			throw new SchemaException("No changes to finished schema!");
		}
		EdgeClassImpl.checkIncidenceClassSpecialization(this, other);
		if (other.getSubsettedIncidenceClasses().contains(this)) {
			throw new SchemaException(
					"Subsetting of IncidenceClasses need to be acyclic");
		}
		subsettedIncidenceClasses.add(other);
	}

	@Override
	public Set<String> getAllRoles() {
		Set<String> result = new HashSet<String>();
		result.add(getRolename());
		for (IncidenceClass ic : getSubsettedIncidenceClasses()) {
			result.add(ic.getRolename());
		}
		return result;
	}

	void finish() {
		this.allSubsettedIncidenceClasses = new HashSet<IncidenceClass>();
		this.allSubsettedIncidenceClasses.addAll(subsettedIncidenceClasses);
		for (IncidenceClass ic : subsettedIncidenceClasses) {
			this.allSubsettedIncidenceClasses.addAll(ic
					.getSubsettedIncidenceClasses());
		}

		this.allSubsettedIncidenceClasses = Collections
				.unmodifiableSet(this.allSubsettedIncidenceClasses);
	}

	@Override
	public int getIncidenceClassIdInSchema() {
		return incidenceClassIdInSchema;
	}

	void reopen() {
		allSubsettedIncidenceClasses = null;
	}
}
