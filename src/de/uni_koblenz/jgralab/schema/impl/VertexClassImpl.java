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

package de.uni_koblenz.jgralab.schema.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

public final class VertexClassImpl extends GraphElementClassImpl implements
		VertexClass {

	/**
	 * the own in IncidenceClasses
	 */
	private Set<IncidenceClass> inIncidenceClasses = new HashSet<IncidenceClass>();

	/**
	 * the in IncidenceClasses - only set if schema is finish
	 */
	private Set<IncidenceClass> allInIncidenceClasses;

	/**
	 * the own out IncidenceClasses
	 */
	private Set<IncidenceClass> outIncidenceClasses = new HashSet<IncidenceClass>();

	/**
	 * the out IncidenceClasses - only set if schema is finish
	 */
	private Set<IncidenceClass> allOutIncidenceClasses;

	/**
	 * the valid from far IncidenceClasses - only set if schema is finished
	 */
	private Set<IncidenceClass> validFromFarIncidenceClasses;

	/**
	 * the valid from EdgeClasses - only set if schema is finished
	 */
	private Set<EdgeClass> validFromEdgeClasses;

	/**
	 * the valid to EdgeClasses - only set if schema is finished
	 */
	private Set<EdgeClass> validToEdgeClasses;

	/**
	 * the valid to far IncidenceClasses - only set if schema is finished
	 */
	private Set<IncidenceClass> validToFarIncidenceClasses;

	static VertexClass createDefaultVertexClass(Schema schema) {
		assert schema.getDefaultGraphClass() != null : "DefaultGraphClass has not yet been created!";
		assert schema.getDefaultVertexClass() == null : "DefaultVertexClass already created!";
		VertexClass vc = schema.getDefaultGraphClass().createVertexClass(
				DEFAULTVERTEXCLASS_NAME);
		vc.setAbstract(true);
		return vc;
	}

	/**
	 * builds a new vertex class object
	 *
	 * @param qn
	 *            the unique identifier of the vertex class in the schema
	 */
	protected VertexClassImpl(String simpleName, Package pkg,
			GraphClass aGraphClass) {
		super(simpleName, pkg, aGraphClass);
		register();
	}

	@Override
	protected void register() {
		((PackageImpl) parentPackage).addVertexClass(this);
		((GraphClassImpl) graphClass).addVertexClass(this);
	}

	@Override
	public String getVariableName() {
		return "vc_" + getQualifiedName().replace('.', '_');
	}

	void addInIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			throwSchemaException();
		}
		checkDuplicateRolenames(incClass);
		inIncidenceClasses.add(incClass);
	}

	void addOutIncidenceClass(IncidenceClass incClass) {
		if (incClass.getVertexClass() != this) {
			throwSchemaException();
		}
		checkDuplicateRolenames(incClass);
		outIncidenceClasses.add(incClass);
	}

	private void checkDuplicateRolenames(IncidenceClass incClass) {

		String rolename = incClass.getOpposite().getRolename();

		if (rolename.isEmpty()) {
			return;
		}

		checkDuplicatedRolenameForACyclicIncidence(incClass);

		checkDuplicatedRolenameForAllIncidences(incClass,
				getAllInIncidenceClasses());
		checkDuplicatedRolenameForAllIncidences(incClass,
				getAllOutIncidenceClasses());
	}

	private void checkDuplicatedRolenameForACyclicIncidence(
			IncidenceClass incClass) {

		String rolename = incClass.getOpposite().getRolename();
		VertexClass oppositeVertexClass = incClass.getOpposite()
				.getVertexClass();

		boolean equalRolenames = incClass.getRolename().equals(rolename);
		boolean identicalClasses = this == oppositeVertexClass;

		if (equalRolenames && identicalClasses) {
			throwSchemaException(incClass);
		}
	}

	private void checkDuplicatedRolenameForAllIncidences(
			IncidenceClass incClass, Set<IncidenceClass> incidenceSet) {

		String rolename = incClass.getOpposite().getRolename();

		if (rolename.isEmpty()) {
			return;
		}

		for (IncidenceClass incidence : incidenceSet) {
			if (incidence == incClass) {
				continue;
			}
			if (incidence.getOpposite().getRolename().equals(rolename)) {
				throwSchemaExceptionRolenameUsedTwice(incidence);
			}
		}
	}

	private void throwSchemaExceptionRolenameUsedTwice(IncidenceClass incidence) {
		throw new SchemaException("The rolename "
				+ incidence.getOpposite().getRolename()
				+ " is used twice at class " + getQualifiedName());
	}

	private void throwSchemaException(IncidenceClass incClass) {
		throw new SchemaException("The rolename " + incClass.getRolename()
				+ " may be not used at both ends of the reflexive edge class "
				+ incClass.getEdgeClass().getQualifiedName());
	}

	private void throwSchemaException() {
		throw new SchemaException(
				"IncidenceClasses may be added only to vertices they are connected to");
	}

	@Override
	public void addSuperClass(VertexClass superClass) {
		// Checked in super class
		// if(isFinished()){
		// throw new SchemaException("No changes to finished schema!");
		// }
		checkDuplicateRolenames(superClass);
		super.addSuperClass(superClass);
	}

	private void checkDuplicateRolenames(VertexClass superClass) {

		if (superClass == null) {
			return;
		}
		checkDuplicatedRolenamesAgainstAllIncidences(superClass
				.getAllInIncidenceClasses());
		checkDuplicatedRolenamesAgainstAllIncidences(superClass
				.getAllOutIncidenceClasses());
	}

	private void checkDuplicatedRolenamesAgainstAllIncidences(
			Set<IncidenceClass> incidences) {
		for (IncidenceClass incidence : incidences) {
			checkDuplicateRolenames(incidence);
		}
	}

	/**
	 * For a vertexclass A are all edgeclasses valid froms, which (1) run from A
	 * to a B or (2) run from a superclass of A to a B and whose end b at B is
	 * not redefined by A or a superclass of A
	 *
	 */

	@Override
	public Set<IncidenceClass> getValidFromFarIncidenceClasses() {
		if (isFinished()) {
			return this.validFromFarIncidenceClasses;
		}

		Set<IncidenceClass> validFromInc = new HashSet<IncidenceClass>();
		for (IncidenceClass ic : getAllOutIncidenceClasses()) {
			IncidenceClass farInc = ic.getEdgeClass().getTo();
			validFromInc.add(farInc);
		}
		for (AttributedElementClass aec : getAllSuperClasses()) {
			VertexClass vc = (VertexClass) aec;
			if (vc.isInternal()) {
				continue;
			}
			for (IncidenceClass ic : vc.getAllOutIncidenceClasses()) {
				IncidenceClass farInc = ic.getEdgeClass().getTo();
				validFromInc.add(farInc);
			}
		}
		Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validFromInc);
		for (IncidenceClass ic : temp) {
			validFromInc.removeAll(ic.getRedefinedIncidenceClasses());
		}

		return validFromInc;
	}

	@Override
	public Set<IncidenceClass> getValidToFarIncidenceClasses() {
		if (isFinished()) {
			return this.validToFarIncidenceClasses;
		}
		Set<IncidenceClass> validToInc = new HashSet<IncidenceClass>();
		for (IncidenceClass ic : getAllInIncidenceClasses()) {
			IncidenceClass farInc = ic.getEdgeClass().getFrom();
			validToInc.add(farInc);
		}
		for (AttributedElementClass aec : getAllSuperClasses()) {
			VertexClass vc = (VertexClass) aec;
			if (vc.isInternal()) {
				continue;
			}
			for (IncidenceClass ic : vc.getAllInIncidenceClasses()) {
				IncidenceClass farInc = ic.getEdgeClass().getFrom();
				validToInc.add(farInc);
			}
		}
		Set<IncidenceClass> temp = new HashSet<IncidenceClass>(validToInc);
		for (IncidenceClass ic : temp) {
			validToInc.removeAll(ic.getRedefinedIncidenceClasses());
		}

		return validToInc;
	}

	@Override
	public Set<EdgeClass> getValidFromEdgeClasses() {
		if (isFinished()) {
			return this.validFromEdgeClasses;
		}
		Set<EdgeClass> validFrom = new HashSet<EdgeClass>();
		for (IncidenceClass ic : getValidFromFarIncidenceClasses()) {
			if (!ic.getEdgeClass().isInternal()) {
				validFrom.add(ic.getEdgeClass());
			}
		}

		return validFrom;
	}

	@Override
	public Set<EdgeClass> getValidToEdgeClasses() {
		if (isFinished()) {
			return this.validToEdgeClasses;
		}
		Set<EdgeClass> validTo = new HashSet<EdgeClass>();
		for (IncidenceClass ic : getValidToFarIncidenceClasses()) {
			if (!ic.getEdgeClass().isInternal()) {
				validTo.add(ic.getEdgeClass());
			}
		}
		return validTo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Vertex> getSchemaClass() {
		return (Class<? extends Vertex>) super.getSchemaClass();
	}

	public Set<IncidenceClass> getOwnInIncidenceClasses() {
		return inIncidenceClasses;
	}

	public Set<IncidenceClass> getOwnOutIncidenceClasses() {
		return outIncidenceClasses;
	}

	@Override
	public Set<IncidenceClass> getAllInIncidenceClasses() {
		if (isFinished()) {
			return this.allInIncidenceClasses;
		}
		Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
		incidenceClasses.addAll(inIncidenceClasses);
		for (AttributedElementClass vc : getDirectSuperClasses()) {
			incidenceClasses.addAll(((VertexClass) vc)
					.getAllInIncidenceClasses());
		}
		return incidenceClasses;
	}

	@Override
	public Set<IncidenceClass> getAllOutIncidenceClasses() {
		if (isFinished()) {
			return this.allOutIncidenceClasses;
		}
		Set<IncidenceClass> incidenceClasses = new HashSet<IncidenceClass>();
		incidenceClasses.addAll(outIncidenceClasses);
		for (AttributedElementClass vc : getDirectSuperClasses()) {
			incidenceClasses.addAll(((VertexClass) vc)
					.getAllOutIncidenceClasses());
		}
		return incidenceClasses;
	}

	@Override
	public Set<IncidenceClass> getOwnAndInheritedFarIncidenceClasses() {
		Set<IncidenceClass> result = new HashSet<IncidenceClass>();
		for (IncidenceClass ic : getAllInIncidenceClasses()) {
			result.add(ic.getEdgeClass().getFrom());
			for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
				result.add(sup.getEdgeClass().getFrom());
			}
		}
		for (IncidenceClass ic : getAllOutIncidenceClasses()) {
			result.add(ic.getEdgeClass().getTo());
			for (IncidenceClass sup : ic.getSubsettedIncidenceClasses()) {
				result.add(sup.getEdgeClass().getTo());
			}
		}
		return result;
	}

	@Override
	public Set<EdgeClass> getConnectedEdgeClasses() {
		Set<EdgeClass> result = new HashSet<EdgeClass>();
		for (IncidenceClass ic : getAllInIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		for (IncidenceClass ic : getAllOutIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		return result;
	}

	@Override
	public Set<EdgeClass> getOwnConnectedEdgeClasses() {
		Set<EdgeClass> result = new HashSet<EdgeClass>();
		for (IncidenceClass ic : getOwnInIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		for (IncidenceClass ic : getOwnOutIncidenceClasses()) {
			result.add(ic.getEdgeClass());
		}
		return result;
	}

	@Override
	protected void finish() {
		this.allInIncidenceClasses = new HashSet<IncidenceClass>();
		this.allInIncidenceClasses.addAll(inIncidenceClasses);

		this.allOutIncidenceClasses = new HashSet<IncidenceClass>();
		this.allOutIncidenceClasses.addAll(outIncidenceClasses);

		for (AttributedElementClass vc : getDirectSuperClasses()) {
			this.allInIncidenceClasses.addAll(((VertexClass) vc)
					.getAllInIncidenceClasses());
			this.allOutIncidenceClasses.addAll(((VertexClass) vc)
					.getAllOutIncidenceClasses());
		}

		this.allInIncidenceClasses = Collections
				.unmodifiableSet(this.allInIncidenceClasses);
		this.allOutIncidenceClasses = Collections
				.unmodifiableSet(this.allOutIncidenceClasses);

		this.validFromFarIncidenceClasses = Collections.unmodifiableSet(this
				.getValidFromFarIncidenceClasses());
		this.validToFarIncidenceClasses = Collections.unmodifiableSet(this
				.getValidToFarIncidenceClasses());

		this.validFromEdgeClasses = Collections.unmodifiableSet(this
				.getValidFromEdgeClasses());
		this.validToEdgeClasses = Collections.unmodifiableSet(this
				.getValidToEdgeClasses());

		this.inIncidenceClasses = Collections
				.unmodifiableSet(this.inIncidenceClasses);
		this.outIncidenceClasses = Collections
				.unmodifiableSet(this.outIncidenceClasses);

		for (IncidenceClass ic : this.inIncidenceClasses) {
			((IncidenceClassImpl) ic).finish();
		}
		for (IncidenceClass ic : this.outIncidenceClasses) {
			((IncidenceClassImpl) ic).finish();
		}

		super.finish();
	}

	@Override
	public boolean isValidFromFor(EdgeClass ec) {
		return getValidFromEdgeClasses().contains(ec);
	}

	@Override
	public boolean isValidToFor(EdgeClass ec) {
		return getValidToEdgeClasses().contains(ec);
	}

	@Override
	protected void reopen() {
		this.allInIncidenceClasses = null;
		this.allOutIncidenceClasses = null;
		this.validFromFarIncidenceClasses = null;
		this.validToFarIncidenceClasses = null;
		this.validFromEdgeClasses = null;
		this.validToEdgeClasses = null;
		this.inIncidenceClasses = new HashSet<IncidenceClass>(
				this.inIncidenceClasses);
		this.outIncidenceClasses = new HashSet<IncidenceClass>(
				this.outIncidenceClasses);

		for (IncidenceClass ic : this.inIncidenceClasses) {
			((IncidenceClassImpl) ic).reopen();
		}
		for (IncidenceClass ic : this.outIncidenceClasses) {
			((IncidenceClassImpl) ic).reopen();
		}

		super.reopen();
	}

}
