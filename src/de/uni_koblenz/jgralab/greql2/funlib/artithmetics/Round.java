package de.uni_koblenz.jgralab.greql2.funlib.artithmetics;

import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class Round extends Function {
	public Round() {
		super("Calculates the floor of $a$.", 4, 1, 1.0, Category.ARITHMETICS);
	}

	public Number evaluate(Number a) {
		return Math.round(a.doubleValue());
	}
}
