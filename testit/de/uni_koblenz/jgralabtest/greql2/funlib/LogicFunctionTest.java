/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
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
package de.uni_koblenz.jgralabtest.greql2.funlib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralabtest.greql2.GenericTests;

public class LogicFunctionTest extends GenericTests {

	public void testBooleanFunction(String functionName, boolean arg1,
			boolean arg2, boolean expected) throws Exception {
		String queryString = functionName + "(" + arg1 + "," + arg2 + ")";
		assertQueryEquals(queryString, expected);
	}

	public void testBooleanOperant(String functionName, boolean arg1,
			boolean arg2, boolean expected) throws Exception {
		String queryString = arg1 + " " + functionName + " " + arg2;
		assertQueryEquals(queryString, expected);
	}

	public void testBooleanOperation(String functionName, boolean arg1,
			boolean arg2, boolean expected) throws Exception {
		testBooleanFunction(functionName, arg1, arg2, expected);
		testBooleanOperant(functionName, arg1, arg2, expected);
	}

	/*
	 * Test method for the GReQL function 'and'.
	 */
	@Test
	public void testAndInfix() throws Exception {
		assertQueryEquals("false and false", false);
		assertQueryEquals("false and true", false);
		assertQueryEquals("true and false", false);
		assertQueryEquals("true and true", true);
	}

	/*
	 * Test method for the GReQL function 'and'.
	 */
	@Test
	public void testAnd() throws Exception {
		assertQueryEquals("and(false, false)", false);
		assertQueryEquals("and(false, true)", false);
		assertQueryEquals("and(true, false)", false);
		assertQueryEquals("and(true, true)", true);
	}

	/*
	 * Test method for the GReQL function 'or'.
	 */
	@Test
	public void testOrInfix() throws Exception {
		assertQueryEquals("false or false", false);
		assertQueryEquals("false or true", true);
		assertQueryEquals("true or false", true);
		assertQueryEquals("true or true", true);
	}

	/*
	 * Test method for the GReQL function 'or'.
	 */
	@Test
	public void testOr() throws Exception {
		assertQueryEquals("or(false, false)", false);
		assertQueryEquals("or(false, true)", true);
		assertQueryEquals("or(true, false)", true);
		assertQueryEquals("or(true, true)", true);
	}

	/*
	 * Test method for the GReQL function 'xor'.
	 */
	@Test
	public void testXor1() throws Exception {
		testBooleanOperation("xor", false, false, false);
		testBooleanOperation("xor", false, true, true);
		testBooleanOperation("xor", true, false, true);
		testBooleanOperation("xor", true, true, false);
	}
}
