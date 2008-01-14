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

import de.uni_koblenz.jgralab.EdgeClass;
import de.uni_koblenz.jgralab.GraphClass;
import de.uni_koblenz.jgralab.Schema;
import de.uni_koblenz.jgralab.VertexClass;

/**
 * This class generates the code of the GraphElement Factory.
 * @author dbildh
 *
 */
public class GraphFactoryGenerator extends CodeGenerator {
	
	private Schema schema;
	
	public GraphFactoryGenerator(Schema schema, String schemaPackageName, String implementationName) {
		super(schemaPackageName, implementationName);
		this.schema = schema;
		rootBlock.setVariable("className", schema.getName() + "Factory");
		rootBlock.setVariable("implClassName", schema.getName() + "Factory");
		//rootBlock.setVariable("isClassOnly", "true");
		rootBlock.setVariable("isImplementationClassOnly", "true");
	}

	protected CodeBlock createHeader(boolean createClass) {
		addImports("#schemaPackage#.*");
		addImports("#jgImplPackage#.GraphFactoryImpl");
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("className", schema.getName() + "Factory");
		code.add("public class #className# extends GraphFactoryImpl {");
		return code;
	}
	
	
	
	@Override
	protected CodeBlock createBody(boolean createClass) {
		CodeList code = new CodeList();
		code.add(createConstructor());
		code.add(createFillTableMethod());
		return code;
	}
	
	protected CodeBlock createConstructor() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("className", schema.getName() + "Factory");
		code.add("public #className#() {");
		code.add("\tsuper();");
		code.add("\tfillTable();");
		code.add("}");
		return code;
	}
	
	protected CodeBlock createFillTableMethod() {
		CodeList code = new CodeList();
		CodeSnippet s = new CodeSnippet(true);
		s.add("protected void fillTable() { ");
		code.addNoIndent(s);
		for (GraphClass graphClass : schema.getGraphClasses().values()) {
			code.add(createFillTableForGraph(graphClass));
			for (VertexClass vertexClass : graphClass.getOwnVertexClasses())
				code.add(createFillTableForVertex(vertexClass));
			for (EdgeClass edgeClass : graphClass.getOwnEdgeClasses())
				code.add(createFillTableForEdge(edgeClass));
			for (EdgeClass edgeClass : graphClass.getOwnAggregationClasses())
				code.add(createFillTableForEdge(edgeClass));
			for (EdgeClass edgeClass : graphClass.getOwnCompositionClasses())
				code.add(createFillTableForEdge(edgeClass));
		}
		s = new CodeSnippet(true);
		s.add("}");
		code.addNoIndent(s);
		return code;
	}
	
	protected CodeBlock createFillTableForGraph(GraphClass graphClass) {
		if (graphClass.isAbstract())
			return null;
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", graphClass.getName());
		code.add("/* code for graph #graphName# */");
		code.add("setGraphImplementationClass(#graphName#.class, #graphName#Impl.class);");
		return code;
	}
	
	protected CodeBlock createFillTableForVertex(VertexClass vertexClass) {
		if (vertexClass.isAbstract())
			return null;
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("vertexName", vertexClass.getName());
		code.add("setVertexImplementationClass(#vertexName#.class, #vertexName#Impl.class);");
		return code;
	}
	
	protected CodeBlock createFillTableForEdge(EdgeClass edgeClass) {

			//return null;
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("edgeName", edgeClass.getName());
		if (!edgeClass.isAbstract())
		code.add("setEdgeImplementationClass(#edgeName#.class, #edgeName#Impl.class);");
		return code;
	}
	
	
	
}
