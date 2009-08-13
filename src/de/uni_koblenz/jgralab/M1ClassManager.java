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

package de.uni_koblenz.jgralab;

import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.codegenerator.ClassFileAbstraction;

/**
 * The {@code M1ClassManager} holds the bytecode of M1 classes in a {@code Map}.
 * As a specialization of {@code ClassLoader}, it overwrites the method {@code
 * findClass} so that the {@code Map} is first searched for classes' bytecode
 * before invoking {@code findClass} of the superclass {@code ClassLoader}.
 * 
 * @author ist@uni-koblenz.de
 */
public class M1ClassManager extends ClassLoader {
	private Map<String, ClassFileAbstraction> m1Classes;
	private static int instanceCount = 0;
	private static Map<String, M1ClassManager> instances = new HashMap<String, M1ClassManager>();

	public static M1ClassManager instance(String schemaName) {
		if (!instances.containsKey(schemaName)) {
			instances.put(schemaName, new M1ClassManager());
		}
		return instances.get(schemaName);
	}

	public static void release(String schemaName) {
		System.out.println("Releasing M1CM for " + schemaName);
		instances.remove(schemaName);
		System.out.println("Now, there are " + instances.size()
				+ " M1CMs in the map, and " + instanceCount
				+ " are referenced.");
	}

	private M1ClassManager() {
		m1Classes = new HashMap<String, ClassFileAbstraction>();
		synchronized (this) {
			instanceCount++;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("Finalizing " + this);
		synchronized (this) {
			instanceCount--;
		}
		super.finalize();
	}

	public void putM1Class(String className, ClassFileAbstraction cfa) {
		m1Classes.put(className, cfa);
	}

	public int getNoOfM1Classes() {
		return m1Classes.size();
	}

	/**
	 * Tries to find a class in the internal {@code Map}, If this fails, {@code
	 * findClass} of {@code ClassLoader} is invoked.
	 * 
	 * @param name
	 *            the name of the class to be found
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		ClassFileAbstraction cfa = m1Classes.get(name);
		if (cfa != null) {
			byte[] bytes = cfa.getBytecode();
			Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
			// once the class is defined, we can forget it!
			m1Classes.remove(name);
			return clazz;
		}
		// return super.findClass(name);
		return Class.forName(name);
	}
}
