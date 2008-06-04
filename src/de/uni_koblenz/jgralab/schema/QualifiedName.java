/**
 * 
 */
package de.uni_koblenz.jgralab.schema;

import java.io.File;

public class QualifiedName implements Comparable<QualifiedName> {
	private String packageName;

	private String simpleName;

	private String qualifiedName;

	private String uniqueName;

	public QualifiedName(String qn) {
		if (qn.startsWith("List<") || qn.startsWith("Set<")) {
			setPackageName("");
			simpleName = qn;
			qualifiedName = qn;
		} else {
			int p = qn.lastIndexOf(".");
			if (p >= 0) {
				packageName = qn.substring(0, p);
				simpleName = qn.substring(p + 1);
				qualifiedName = p == 0 ? simpleName : qn;
			} else {
				setPackageName("");
				simpleName = qn;
				qualifiedName = qn;
			}
		}
		uniqueName = simpleName;
	}

	public QualifiedName(String pn, String sn) {
		packageName = pn;
		simpleName = sn;
		qualifiedName = (pn.length() == 0) ? simpleName : packageName + "."
				+ simpleName;
		uniqueName = simpleName;
	}

	@Override
	public String toString() {
		return qualifiedName;
	}

	public void setPackageName(String pn) {
		packageName = pn;
		qualifiedName = (pn.length() == 0) ? simpleName : packageName + "."
				+ simpleName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public String getDirectoryName() {
		return qualifiedName.replace('.', File.separatorChar);
	}

	public String getName() {
		return qualifiedName;
	}

	public String getPathName() {
		return packageName.replace('.', File.separatorChar);
	}

	/**
	 * @return The unique name of this QualifiedName. This name is, as the
	 *         qualifiedName is, unique in the whole schema but much shorter
	 *         than the qualified one. For instance, if there is only one class
	 *         with simple name "X" in the whole schema, the unique name of this
	 *         class can also be "X" without the package prefix
	 */
	public String getUniqueName() {
		return uniqueName;
	}

	/**
	 * Sets the unique name of this QualifiedName. To ensure that the name is
	 * really unique in the whole schema, the element which should have this
	 * unique name needs to be specified. It there is any other element in the
	 * schema which owns the same unique name, a SchemaExceptiion is throws
	 * 
	 * @param element
	 *            The element which should be identified with the unique name
	 * @param uniqueName
	 *            The new uniqueName of the element
	 * @throws SchemaException
	 *             if the uniqueName is not unique, i.e. if there is any other
	 *             element in the schema with the same unique name
	 */
	public void setUniqueName(NamedElement element, String uniqueName) {
		// Must be 1 because 0 is the default graph-class Graph
		for (GraphClass gc : element.getSchema()
				.getGraphClassesInTopologicalOrder()) {
			if (gc != element)
				if (gc.getUniqueName().equals(uniqueName)) {
					throw new SchemaException("The unique name " + uniqueName
							+ " is already used in this schema");
				}
			for (VertexClass v : gc.getVertexClasses()) {
				if (v != element)
					if (v.getSimpleName().equals(uniqueName)) {
						throw new SchemaException("The unique name "
								+ uniqueName
								+ " is already used in this schema");
					}
			}
			for (EdgeClass e : gc.getEdgeClasses()) {
				if (e != element) {
					if (e.getSimpleName().equals(uniqueName)) {
						throw new SchemaException("The unique name "
								+ uniqueName
								+ " is already used in this schema");
					}
				}
			}
			for (AggregationClass e : gc.getAggregationClasses()) {
				if (e != element) {
					if (e.getSimpleName().equals(uniqueName)) {
						throw new SchemaException("The unique name "
								+ uniqueName
								+ " is already used in this schema");
					}
				}
			}
			for (CompositionClass e : gc.getCompositionClasses()) {
				if (e != element) {
					if (e.getSimpleName().equals(uniqueName)) {
						throw new SchemaException("The unique name "
								+ uniqueName
								+ " is already used in this schema");
					}
				}
			}
		}
		this.uniqueName = uniqueName;
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj)
				|| ((obj instanceof QualifiedName) && qualifiedName
						.equals(((QualifiedName) obj).qualifiedName));
	}

	@Override
	public int hashCode() {
		return qualifiedName.hashCode();
	}

	@Override
	public int compareTo(QualifiedName o) {
		return qualifiedName.compareTo(o.qualifiedName);
	}

	public boolean isSimple() {
		return packageName.length() == 0;
	}

	public boolean isQualified() {
		return packageName.length() != 0;
	}

	public static String toUniqueName(String qualifiedName) {
		return qualifiedName.replace(".", "_");
	}
}