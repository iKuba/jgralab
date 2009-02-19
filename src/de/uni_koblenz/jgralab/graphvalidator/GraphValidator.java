/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2009 Institute for Software Technology
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
package de.uni_koblenz.jgralab.graphvalidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueSet;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * @author Tassilo Horn <horn@uni-koblenz.de>
 *
 */
public class GraphValidator {

	private Graph graph;

	public GraphValidator(Graph graph) {
		this.graph = graph;
	}

	public SortedSet<MultiplicityConstraintViolation> validateMultiplicities(
			EdgeClass ec) {
		SortedSet<MultiplicityConstraintViolation> brokenConstraints = new TreeSet<MultiplicityConstraintViolation>();

		int toMin = ec.getToMin();
		int toMax = ec.getToMax();
		Set<AttributedElement> badOutgoing = new HashSet<AttributedElement>();
		for (Vertex v : graph.vertices(ec.getFrom())) {
			int degree = v.getDegree(ec);
			if (degree < toMin || degree > toMax) {
				badOutgoing.add(v);
			}
		}
		if (!badOutgoing.isEmpty()) {
			brokenConstraints.add(new MultiplicityConstraintViolation(ec,
					"These vertices have an invalid number of outgoing "
							+ ec.getUniqueName() + " edges, allowed are ("
							+ toMin + ", "
							+ ((toMax == Integer.MAX_VALUE) ? "*" : toMax)
							+ ").", badOutgoing));
		}

		int fromMin = ec.getFromMin();
		int fromMax = ec.getFromMax();
		Set<AttributedElement> badIncoming = new HashSet<AttributedElement>();
		for (Vertex v : graph.vertices(ec.getTo())) {
			int degree = v.getDegree(ec);
			if (degree < fromMin || degree > fromMax) {
				badIncoming.add(v);
			}
		}
		if (!badIncoming.isEmpty()) {
			brokenConstraints.add(new MultiplicityConstraintViolation(ec,
					"These vertices have an invalid number of incoming "
							+ ec.getUniqueName() + " edges, allowed are ("
							+ fromMin + ", "
							+ ((fromMax == Integer.MAX_VALUE) ? "*" : fromMax)
							+ ").", badIncoming));
		}
		return brokenConstraints;
	}

	/**
	 * Validates the graph.
	 *
	 * @return a set of {@link ConstraintViolation} objects, one for each
	 *         violation, sorted by their type.
	 */
	public SortedSet<ConstraintViolation> validate() {
		SortedSet<ConstraintViolation> brokenConstraints = new TreeSet<ConstraintViolation>();

		// Check if all multiplicities are correct
		for (EdgeClass ec : graph.getSchema()
				.getEdgeClassesInTopologicalOrder()) {
			if (ec.isInternal()) {
				continue;
			}
			brokenConstraints.addAll(validateMultiplicities(ec));
		}

		// check if all greql constraints are met
		List<AttributedElementClass> aecs = new ArrayList<AttributedElementClass>();
		aecs.addAll(graph.getSchema().getGraphClassesInTopologicalOrder());
		aecs.addAll(graph.getSchema().getVertexClassesInTopologicalOrder());
		aecs.addAll(graph.getSchema().getEdgeClassesInTopologicalOrder());
		for (AttributedElementClass aec : aecs) {
			if (aec.isInternal()) {
				continue;
			}
			brokenConstraints.addAll(validateConstraints(aec));
		}
		return brokenConstraints;
	}

	public SortedSet<ConstraintViolation> validateConstraints(
			AttributedElementClass aec) {
		SortedSet<ConstraintViolation> brokenConstraints = new TreeSet<ConstraintViolation>();
		for (Constraint constraint : aec.getConstraints()) {
			String query = constraint.getPredicate();
			GreqlEvaluator eval = new GreqlEvaluator(query, graph, null);
			try {
				eval.startEvaluation();
				if (!eval.getEvaluationResult().toBoolean()) {
					if (constraint.getOffendingElements() != null) {
						query = constraint.getOffendingElements();
						GreqlEvaluator eval2 = new GreqlEvaluator(query, graph,
								null);
						eval2.startEvaluation();
						JValueSet resultSet = eval2.getEvaluationResult()
								.toJValueSet();
						brokenConstraints.add(new GReQLConstraintViolation(aec,
								constraint, jvalueSet2Set(resultSet)));
					} else {
						brokenConstraints.add(new GReQLConstraintViolation(aec,
								constraint, null));
					}
				}
			} catch (EvaluateException e) {
				brokenConstraints.add(new BrokenGReQLConstraintViolation(aec,
						constraint, query));
			}
		}
		return brokenConstraints;
	}

	private Set<AttributedElement> jvalueSet2Set(JValueSet resultSet) {
		Set<AttributedElement> set = new HashSet<AttributedElement>();
		for (JValue jv : resultSet) {
			set.add(jv.toAttributedElement());
		}
		return set;
	}

	/**
	 * Do just like {@link GraphValidator#validate()}, but generate a HTML
	 * report saved to <code>fileName</code>, too.
	 *
	 * @param fileName
	 *            the name of the HTML report file
	 * @return a set of {@link GReQLConstraintViolation} objects, one for each
	 *         invalidation
	 * @throws IOException
	 *             if the given file cannot be written
	 */
	public SortedSet<ConstraintViolation> createValidationReport(String fileName)
			throws IOException {
		SortedSet<ConstraintViolation> brokenConstraints = validate();

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				new File(fileName)));
		// The header
		bw.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"
				+ "\"http://www.w3.org/TR/html4/strict.dtd\">\n" + "<html>");
		bw.append("<head><title>");
		bw.append("Validation Report for the "
				+ graph.getM1Class().getSimpleName() + " with id "
				+ graph.getId() + ".");
		bw.append("</title></head>");

		// The body
		bw.append("<body>");

		if (brokenConstraints.size() == 0) {
			bw.append("<p><b>The graph is perfectly valid!</b></p>");
		} else {
			bw.append("<p><b>The " + graph.getM1Class().getSimpleName()
					+ " invalidates " + brokenConstraints.size()
					+ " constraints.</b></p>");
			// Here goes the table
			bw.append("<table border=\"1\">");
			bw.append("<tr>");
			bw.append("<th>#</th>");
			bw.append("<th>ConstraintType</th>");
			bw.append("<th>AttributedElementClass</th>");
			bw.append("<th>Message</th>");
			bw.append("<th>Broken Elements</th>");
			bw.append("</tr>");
			int no = 1;
			for (ConstraintViolation ci : brokenConstraints) {
				bw.append("<tr>");
				bw.append("<td>");
				bw.append(new String(new Integer(no++).toString()));
				bw.append("</td>");
				bw.append("<td>");
				bw.append(ci.getClass().getSimpleName());
				bw.append("</td>");
				bw.append("<td>");
				bw.append(ci.getAttributedElementClass().getQualifiedName());
				bw.append("</td>");
				bw.append("<td>");
				bw.append(ci.getMessage());
				bw.append("</td>");
				bw.append("<td>");
				if (ci.getOffendingElements() != null) {
					for (AttributedElement ae : ci.getOffendingElements()) {
						bw.append(ae.toString());
						bw.append("<br/>");
					}
				}
				bw.append("</td>");
				bw.append("</tr>");
			}
			bw.append("</table>");
		}

		bw.append("</body></html>");
		bw.flush();
		bw.close();
		return brokenConstraints;
	}
}
