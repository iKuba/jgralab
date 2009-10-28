package de.uni_koblenz.jgralab.trans;

/**
 * This enumeration defines the possible states for a <code>Transaction</code>.
 * 
 * @author Jos� Monte(monte@uni-koblenz.de)
 */
public enum TransactionState {
	NOTRUNNING, STARTING, RUNNING, ABORTING, ABORTED, COMMITTING, VALIDATING, WRITING, COMMITTED
}
