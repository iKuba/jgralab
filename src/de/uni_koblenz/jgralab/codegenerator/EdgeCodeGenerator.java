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
 
package de.uni_koblenz.jgralab.codegenerator;

import java.util.TreeSet;

import de.uni_koblenz.jgralab.AggregationClass;
import de.uni_koblenz.jgralab.AttributedElementClass;
import de.uni_koblenz.jgralab.CompositionClass;
import de.uni_koblenz.jgralab.EdgeClass;

public class EdgeCodeGenerator extends AttributedElementCodeGenerator {

	public EdgeCodeGenerator(EdgeClass edgeClass, String schemaPackageName,
			String implementationName) {
		super(edgeClass, schemaPackageName, implementationName);
		if (edgeClass instanceof CompositionClass) {
			rootBlock.setVariable("graphElementClass", "Composition");
		} else if (edgeClass instanceof AggregationClass) {
			rootBlock.setVariable("graphElementClass", "Aggregation");
		} else {
			rootBlock.setVariable("graphElementClass", "Edge");
		}
	}

	protected CodeBlock createHeader(boolean createClass) {
		CodeList code = new CodeList();
		EdgeClass ec = (EdgeClass) aec;
		code.setVariable("fromVertexClass", ec.getFrom().getName());
		code.setVariable("toVertexClass", ec.getTo().getName());
		code.setVariable("fromRoleName", ec.getFromRolename());
		code.setVariable("toRoleName", ec.getToRolename());
		code.setVariable("ecName", ec.getName());
		CodeSnippet snippet = new CodeSnippet();
		snippet.add("/**");
		snippet.add("FromVertexClass: #fromVertexClass#");
		snippet.add("FromRoleName : #fromRoleName#");
		snippet.add("ToVertexClass: #toVertexClass#");
		snippet.add("toRoleName : #toRoleName#");
		snippet.add(" */");
		code.addNoIndent(snippet);
		code.addNoIndent(super.createHeader(createClass));
		return code;
	}

	protected CodeBlock createBody(boolean createClass) {
		CodeList code = (CodeList) super.createBody(createClass);
		if (createClass) {
			if (aec.getAllSuperClasses().contains(aec.getSchema().getAttributedElementClass("Composition")))
				rootBlock.setVariable("baseClassName", "CompositionImpl");		
			else if (aec.getAllSuperClasses().contains(aec.getSchema().getAttributedElementClass("Aggregation")))
					rootBlock.setVariable("baseClassName", "AggregationImpl");
			else rootBlock.setVariable("baseClassName", "EdgeImpl");
			addImports("#jgImplPackage#.#jgImplementation#.#baseClassName#");
		}
		code.add(createNextEdgeInGraphMethods(createClass));
		code.add(createNextEdgeAtVertexMethods(createClass));
		return code;
	}

	@Override
	protected CodeBlock createGetM1ClassMethod() {
		// TODO Auto-generated method stub
		return super.createGetM1ClassMethod();
	}

	@Override
	protected CodeBlock createSpecialConstructorCode() {
		addImports("#schemaImplPackage#.Reversed#className#Impl");
		return new CodeSnippet(
				"reversedEdge = new Reversed#className#Impl(this, g);");
	}

	private CodeBlock createNextEdgeInGraphMethods(boolean createClass) {
		CodeList code = new CodeList();

		TreeSet<AttributedElementClass> superClasses = new TreeSet<AttributedElementClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		for (AttributedElementClass ec : superClasses) {
			if (ec.isInternal()) {
				continue;
			}
			EdgeClass ecl = (EdgeClass) ec;
			code.addNoIndent(createNextEdgeInGraphMethod(ecl, false, createClass));
			if (!ecl.isAbstract()) {
				code.addNoIndent(createNextEdgeInGraphMethod(ecl, true,	createClass));
			}
		}
		return code;
	}

	private CodeBlock createNextEdgeInGraphMethod(EdgeClass ec,
			boolean withTypeFlag, boolean createClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("ecName", ec.getName());
		code.setVariable("ecCamelName", camelCase(ec.getName()));
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"	: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses" : ""));

		if (!createClass) {
			code.add("/**",
			 		 " * @return the next #ecName# edge in the global edge sequence");
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #ecName# are accepted");
			}
			code.add(" */",
				     "public #ecName# getNext#ecCamelName#InGraph(#formalParams#);");
		} else {
			code.add(
					"public #ecName# getNext#ecCamelName#InGraph(#formalParams#) {",
					"\treturn (#ecName#)getNextEdgeOfClassInGraph(#ecName#.class#actualParams#);",
					"}");
		}
		return code;
	}

	private CodeBlock createNextEdgeAtVertexMethods(boolean createClass) {
		CodeList code = new CodeList();

		TreeSet<AttributedElementClass> superClasses = new TreeSet<AttributedElementClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		for (AttributedElementClass ec : superClasses) {
			if (ec.isInternal()) {
				continue;
			}
			addImports("#jgPackage#.EdgeDirection");
			EdgeClass ecl = (EdgeClass) ec;
			code.addNoIndent(createNextEdgeAtVertexMethod(ecl, false, false,
					createClass));
			code.addNoIndent(createNextEdgeAtVertexMethod(ecl, true, false,
					createClass));
			if (!ecl.isAbstract()) {
				code.addNoIndent(createNextEdgeAtVertexMethod(ecl, false, true,
						createClass));
				code.addNoIndent(createNextEdgeAtVertexMethod(ecl, true, true,
						createClass));
			}
		}
		return code;
	}

	private CodeBlock createNextEdgeAtVertexMethod(EdgeClass ec,
			boolean withOrientation, boolean withTypeFlag, boolean createClass) {

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("ecName", ec.getName());
		code.setVariable("ecCamelName", camelCase(ec.getName()));
		code.setVariable("formalParams",
				(withOrientation ? "EdgeDirection orientation" : "")
						+ (withOrientation && withTypeFlag ? ", " : "")
						+ (withTypeFlag ? "boolean noSubClasses" : ""));
		code.setVariable("actualParams",
				(withOrientation || withTypeFlag ? ", " : "")
						+ (withOrientation ? "orientation" : "")
						+ (withOrientation && withTypeFlag ? ", " : "")
						+ (withTypeFlag ? "noSubClasses" : ""));
		if (!createClass) {
			code
					.add("/**",
							" * @return the next edge of class #ecName# at the \"this\" vertex");

			if (withOrientation) {
				code.add(" * @param orientation the orientation of the edge");
			}
			if (withTypeFlag) {
				code
						.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #ecName# are accepted");
			}
			code.add(" */",
					"public #ecName# getNext#ecCamelName#(#formalParams#);");
		} else {
			code
					.add(
							"public #ecName# getNext#ecCamelName#(#formalParams#) {",
							"\treturn (#ecName#)getNextEdgeOfClass(#ecName#.class#actualParams#);",
							"}");

		}

		return code;
	}

}
