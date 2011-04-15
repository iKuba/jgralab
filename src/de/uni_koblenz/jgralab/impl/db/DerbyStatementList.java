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
package de.uni_koblenz.jgralab.impl.db;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.SortedSet;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.Schema;

import static de.uni_koblenz.jgralab.impl.db.GraphDatabase.*;

/**
 * Factory that creates Apache Derby/JavaDB specific prepared statements.
 * BEWARE: Strings are limited to 2048 bytes.
 * 
 * @author ultbreit@uni-koblenz.de
 */
public class DerbyStatementList extends SqlStatementList {

	public DerbyStatementList(GraphDatabase graphDatabase)
			throws GraphDatabaseException {
		super(graphDatabase);
	}

	private static final String CREATE_GRAPH_SCHEMA_TABLE = "CREATE TABLE \""
			+ TABLE_SCHEMA + "\"(\"" + COLUMN_SCHEMA_ID
			+ "\" INT GENERATED ALWAYS AS IDENTITY CONSTRAINT \""
			+ PRIMARY_KEY_SCHEMA + "\" PRIMARY KEY," + "\""
			+ COLUMN_SCHEMA_PACKAGE_PREFIX + "\" VARCHAR(2048)," + "\""
			+ COLUMN_SCHEMA_NAME + "\" VARCHAR(2048)," + "\""
			+ COLUMN_SCHEMA_TG + "\" LONG VARCHAR" + ")";

	@Override
	public PreparedStatement createGraphSchemaTableWithConstraints()
			throws SQLException {
		return connection.prepareStatement(CREATE_GRAPH_SCHEMA_TABLE);
	}

	private static final String CREATE_TYPE_TABLE = "CREATE TABLE \""
			+ TABLE_TYPE + "\"(" + "\"" + COLUMN_TYPE_ID
			+ "\" INT GENERATED ALWAYS AS IDENTITY CONSTRAINT \""
			+ PRIMARY_KEY_TYPE + "\" PRIMARY KEY," + "\"" + COLUMN_TYPE_QNAME
			+ "\" VARCHAR(2048)," + "\"" + COLUMN_TYPE_SCHEMA_ID
			+ "\" INT REFERENCES \"" + TABLE_SCHEMA + "\"" + ")";

	@Override
	public PreparedStatement createTypeTableWithConstraints()
			throws SQLException {
		return connection.prepareStatement(CREATE_TYPE_TABLE);
	}

	private static final String CREATE_GRAPH_TABLE = "CREATE TABLE \""
			+ TABLE_GRAPH + "\"(" + "\"" + COLUMN_GRAPH_ID
			+ "\" INT GENERATED ALWAYS AS IDENTITY CONSTRAINT \""
			+ PRIMARY_KEY_GRAPH + "\" PRIMARY KEY," + "\"" + COLUMN_GRAPH_UID
			+ "\" VARCHAR(2048)," + "\"" + COLUMN_GRAPH_VERSION + "\" BIGINT,"
			+ "\"" + COLUMN_GRAPH_VSEQ_VERSION + "\" BIGINT," + "\""
			+ COLUMN_GRAPH_ESEQ_VERSION + "\" BIGINT," + "\""
			+ COLUMN_GRAPH_TYPE_ID + "\" INT REFERENCES \"" + TABLE_TYPE + "\""
			+ ")";

	@Override
	public PreparedStatement createGraphTableWithConstraints()
			throws SQLException {
		return connection.prepareStatement(CREATE_GRAPH_TABLE);
	}

	private static final String CREATE_VERTEX_TABLE = "CREATE TABLE \""
			+ TABLE_VERTEX + "\"(" + "\"" + COLUMN_VERTEX_ID
			+ "\" INT NOT NULL," + "\"" + COLUMN_VERTEX_GRAPH_ID
			+ "\" INT NOT NULL," + "\"" + COLUMN_VERTEX_TYPE_ID
			+ "\" INT NOT NULL," + "\"" + COLUMN_VERTEX_LAMBDA_SEQ_VERSION
			+ "\" BIGINT," + "\"" + COLUMN_VERTEX_SEQUENCE_NUMBER + "\" BIGINT"
			+ ")";

	@Override
	public PreparedStatement createVertexTable() throws SQLException {
		return connection.prepareStatement(CREATE_VERTEX_TABLE);
	}

	private static final String ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_TABLE = "ALTER TABLE \""
			+ TABLE_VERTEX
			+ "\" ADD CONSTRAINT \""
			+ PRIMARY_KEY_VERTEX
			+ "\" PRIMARY KEY ( \""
			+ COLUMN_VERTEX_ID
			+ "\", \""
			+ COLUMN_VERTEX_GRAPH_ID + "\" )";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnVertexTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_TABLE);
	}

	private static final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_TABLE = "ALTER TABLE \""
			+ TABLE_VERTEX
			+ "\" DROP CONSTRAINT \""
			+ PRIMARY_KEY_VERTEX
			+ "\"";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromVertexTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_TABLE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_VERTEX = "ALTER TABLE \""
			+ TABLE_VERTEX
			+ "\" ADD CONSTRAINT \""
			+ FOREIGN_KEY_VERTEX_TO_GRAPH
			+ "\" FOREIGN KEY (\""
			+ COLUMN_VERTEX_GRAPH_ID
			+ "\") REFERENCES \""
			+ TABLE_GRAPH
			+ "\" (\"" + COLUMN_GRAPH_ID + "\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnGraphColumnOfVertexTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_VERTEX);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_TYPE = "ALTER TABLE \""
			+ TABLE_VERTEX
			+ "\" ADD CONSTRAINT \""
			+ FOREIGN_KEY_VERTEX_TO_TYPE
			+ "\" FOREIGN KEY (\""
			+ COLUMN_VERTEX_TYPE_ID
			+ "\") REFERENCES \""
			+ TABLE_TYPE
			+ "\" (\"" + COLUMN_TYPE_ID + "\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnTypeColumnOfVertexTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_TYPE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_VERTEX = "ALTER TABLE \""
			+ TABLE_VERTEX
			+ "\" DROP CONSTRAINT \""
			+ FOREIGN_KEY_VERTEX_TO_GRAPH + "\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromGraphColumnOfVertexTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_VERTEX);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_VERTEX_TYPE = "ALTER TABLE \""
			+ TABLE_VERTEX
			+ "\" DROP CONSTRAINT \""
			+ FOREIGN_KEY_VERTEX_TO_TYPE + "\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromTypeColumnOfVertexTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_VERTEX_TYPE);
	}

	private static final String CREATE_EDGE_TABLE = "CREATE TABLE \""
			+ TABLE_EDGE + "\"(" + "\"" + COLUMN_EDGE_ID + "\" INT NOT NULL,"
			+ "\"" + COLUMN_EDGE_GRAPH_ID + "\" INT NOT NULL," + "\""
			+ COLUMN_EDGE_TYPE_ID + "\" INT NOT NULL," + "\""
			+ COLUMN_EDGE_SEQUENCE_NUMBER + "\" BIGINT" + ")";

	@Override
	public PreparedStatement createEdgeTable() throws SQLException {
		return connection.prepareStatement(CREATE_EDGE_TABLE);
	}

	private static final String ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_TABLE = "ALTER TABLE \""
			+ TABLE_EDGE
			+ "\" ADD CONSTRAINT \""
			+ PRIMARY_KEY_EDGE
			+ "\" PRIMARY KEY ( \""
			+ COLUMN_EDGE_ID
			+ "\", \"+"
			+ COLUMN_EDGE_GRAPH_ID + "\" )";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnEdgeTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_TABLE);
	}

	private static final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_TABLE = "ALTER TABLE \"Edge\" DROP CONSTRAINT \""
			+ PRIMARY_KEY_EDGE + "\"";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromEdgeTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_TABLE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_EDGE = "ALTER TABLE \""
			+ TABLE_EDGE
			+ "\" ADD CONSTRAINT \""
			+ FOREIGN_KEY_EDGE_TO_GRAPH
			+ "\" FOREIGN KEY (\""
			+ COLUMN_EDGE_GRAPH_ID
			+ "\") REFERENCES \""
			+ TABLE_GRAPH + "\" (\"" + COLUMN_GRAPH_ID + "\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnGraphColumnOfEdgeTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_EDGE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_TYPE = "ALTER TABLE \""
			+ TABLE_EDGE
			+ "\" ADD CONSTRAINT \""
			+ FOREIGN_KEY_EDGE_TO_TYPE
			+ "\" FOREIGN KEY (\""
			+ COLUMN_EDGE_TYPE_ID
			+ "\") REFERENCES \""
			+ TABLE_TYPE + "\" (\"" + COLUMN_TYPE_ID + "\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnTypeColumnOfEdgeTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_TYPE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_EDGE = "ALTER TABLE \""
			+ TABLE_EDGE
			+ "\" DROP CONSTRAINT \""
			+ FOREIGN_KEY_EDGE_TO_GRAPH
			+ "\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromGraphColumnOfEdgeTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_EDGE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_TYPE = "ALTER TABLE \""
			+ TABLE_EDGE
			+ "\" DROP CONSTRAINT \""
			+ FOREIGN_KEY_EDGE_TO_TYPE
			+ "\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromTypeColumnOfEdgeTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_TYPE);
	}

	private static final String CREATE_INCIDENCE_TABLE = "CREATE TABLE \""
			+ TABLE_INCIDENCE + "\"(" + "\"" + COLUMN_INCIDENCE_EDGE_ID
			+ "\" INT NOT NULL," + "\"" + COLUMN_INCIDENCE_VERTEX_ID
			+ "\" INT NOT NULL," + "\"" + COLUMN_INCIDENCE_GRAPH_ID
			+ "\" INT NOT NULL," + "\"" + COLUMN_INCIDENCE_DIRECTION
			+ "\" VARCHAR(3) NOT NULL," + "\""
			+ COLUMN_INCIDENCE_SEQUENCE_NUMBER + "\" BIGINT NOT NULL" + ")";

	@Override
	public PreparedStatement createIncidenceTable() throws SQLException {
		return connection.prepareStatement(CREATE_INCIDENCE_TABLE);
	}

	private static final String ADD_PRIMARY_KEY_CONSTRAINT_ON_INCIDENCE_TABLE = "ALTER TABLE \""
			+ TABLE_INCIDENCE
			+ "\" ADD CONSTRAINT \""
			+ PRIMARY_KEY_INCIDENCE
			+ "\" PRIMARY KEY ( \""
			+ COLUMN_INCIDENCE_EDGE_ID
			+ "\", \""
			+ COLUMN_INCIDENCE_GRAPH_ID + "\", \"direction\" )";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnIncidenceTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_INCIDENCE_TABLE);
	}

	private static final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_INCIDENCE_TABLE = "ALTER TABLE \""
			+ TABLE_INCIDENCE
			+ "\" DROP CONSTRAINT \""
			+ PRIMARY_KEY_INCIDENCE
			+ "\"";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromIncidenceTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_INCIDENCE_TABLE);
	}

	// TODO continue here

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_INCIDENCE = "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"gIdIsForeignKeyForIncidence\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnGraphColumnOfIncidenceTable()
			throws SQLException {
		return getPreparedStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_INCIDENCE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_OF_INCIDENCE = "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"eIdIsForeignKeyForIndices\" FOREIGN KEY (\"eId\", \"gId\") REFERENCES \"Edge\" (\"eId\", \"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnEdgeColumnOfIncidenceTable()
			throws SQLException {
		return getPreparedStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_OF_INCIDENCE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_OF_INCIDENCE = "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"vIdIsForeignKeyForIncidence\" FOREIGN KEY (\"vId\", \"gId\") REFERENCES \"Vertex\" (\"vId\", \"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnVertexColumnOfIncidenceTable()
			throws SQLException {
		return getPreparedStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_OF_INCIDENCE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_OF_INCIDENCE = "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"eIdIsForeignKeyForIndices\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromEdgeColumnOfIncidenceTable()
			throws SQLException {
		return getPreparedStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_OF_INCIDENCE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_INCIDENCE = "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"gIdIsForeignKeyForIncidence\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromGraphColumnOfIncidenceTable()
			throws SQLException {
		return getPreparedStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_INCIDENCE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_VERTEX_OF_INCIDENCE = "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"vIdIsForeignKeyForIncidence\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromVertexColumnOfIncidenceTable()
			throws SQLException {
		return getPreparedStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_VERTEX_OF_INCIDENCE);
	}

	private static final String CREATE_INDEX_ON_LAMBDA_SEQ = "CREATE INDEX \"lambdaSeqIndex\" ON \"Incidence\"( \"vId\", \"gId\", \"sequenceNumber\" ASC )";

	@Override
	public PreparedStatement addIndexOnLambdaSeq() throws SQLException {
		return getPreparedStatement(CREATE_INDEX_ON_LAMBDA_SEQ);
	}

	private static final String DROP_INDEX_ON_LAMBDA_SEQ = "DROP INDEX \"lambdaSeqIndex\"";

	@Override
	public PreparedStatement dropIndexOnLambdaSeq() throws SQLException {
		return getPreparedStatement(DROP_INDEX_ON_LAMBDA_SEQ);
	}

	private static final String CREATE_ATTRIBUTE_TABLE = "CREATE TABLE \"Attribute\"("
			+ "\"attributeId\" INT GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_Attribute PRIMARY KEY,"
			+ "\"name\" VARCHAR(2048) NOT NULL,"
			+ "\"schemaId\" INT REFERENCES \"GraphSchema\" NOT NULL" + ")";

	@Override
	public PreparedStatement createAttributeTableWithConstraints()
			throws SQLException {
		return connection.prepareStatement(CREATE_ATTRIBUTE_TABLE);
	}

	private static final String CREATE_GRAPH_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE \"GraphAttributeValue\"("
			+ "\"gId\" INT REFERENCES \"Graph\" NOT NULL,"
			+ "\"attributeId\" INT REFERENCES \"Attribute\" NOT NULL,"
			+ "\"value\" LONG VARCHAR NOT NULL,"
			+ "CONSTRAINT \"gaPrimaryKey\" PRIMARY KEY ( \"gId\", \"attributeId\" )"
			+ ")";

	@Override
	public PreparedStatement createGraphAttributeValueTableWithConstraints()
			throws SQLException {
		return connection.prepareStatement(CREATE_GRAPH_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String CREATE_VERTEX_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE \"VertexAttributeValue\"("
			+ "\"vId\" INT NOT NULL,"
			+ "\"gId\" INT NOT NULL,"
			+ "\"attributeId\" INT NOT NULL,"
			+ "\"value\" VARCHAR(2048) NOT NULL" + ")";

	@Override
	public PreparedStatement createVertexAttributeValueTable()
			throws SQLException {
		return connection.prepareStatement(CREATE_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"vertexAttributeValuePrimaryKey\" PRIMARY KEY ( \"vId\", \"gId\", \"attributeId\" ) ";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"vertexAttributeValuePrimaryKey\" ";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_ATTRIBUTE_OF_VERTEX_ATTRIBUTE_VALUE = "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"attributeIdIsForeignKeyForVertexAttribute\" FOREIGN KEY (\"attributeId\" ) REFERENCES \"Attribute\" (\"attributeId\") ";

	@Override
	public PreparedStatement addForeignKeyConstraintOnAttributeColumnOfVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_ATTRIBUTE_OF_VERTEX_ATTRIBUTE_VALUE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_VERTEX_ATTRIBUTE_VALUE = "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"gIdIsForeignKeyForVertexAttribute\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnGraphColumnOfVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_VERTEX_ATTRIBUTE_VALUE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_OF_ATTRIBUTE_VALUE = "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"vIdIsForeignKeyForVertexAttribute\" FOREIGN KEY (\"vId\", \"gId\") REFERENCES \"Vertex\" (\"vId\", \"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnVertexColumnOfVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_OF_ATTRIBUTE_VALUE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_ATTRIBUTE_OF_VERTEX_ATTRIBUTE_VALUE = "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"attributeIdIsForeignKeyForVertexAttribute\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromAttributeColumnOfVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_ATTRIBUTE_OF_VERTEX_ATTRIBUTE_VALUE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_VERTEX_ATTRIBUTE_VALUE = "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"gIdIsForeignKeyForVertexAttribute\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromGraphColumnOfVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_VERTEX_ATTRIBUTE_VALUE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_VERTEX_OF_ATTRIBUTE_VALUE = "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"vIdIsForeignKeyForVertexAttribute\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromVertexColumnOfVertexAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_VERTEX_OF_ATTRIBUTE_VALUE);
	}

	// tells Derby about the primary key candidate, no pk constraint is defined
	// though
	private static final String CREATE_EDGE_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE \"EdgeAttributeValue\"("
			+ "\"eId\" INT NOT NULL,"
			+ "\"gId\" INT NOT NULL,"
			+ "\"attributeId\" INT NOT NULL,"
			+ "\"value\" LONG VARCHAR NOT NULL" + ")";

	@Override
	public PreparedStatement createEdgeAttributeValueTable()
			throws SQLException {
		return connection.prepareStatement(CREATE_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"edgeAttributeValuePrimaryKey\" PRIMARY KEY ( \"eId\", \"gId\", \"attributeId\" )";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"edgeAttributeValuePrimaryKey\"";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_EDGE_ATTRIBUTE_VALUE = "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"gIdIsForeignKeyForEdgeAttribute\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnGraphColumnOfEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_GRAPH_OF_EDGE_ATTRIBUTE_VALUE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_OF_ATTRIBUTE_VALUE = "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"eIdIsForeignKeyForEdgeAttribute\" FOREIGN KEY (\"eId\", \"gId\") REFERENCES \"Edge\" (\"eId\", \"gId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnEdgeColumnOfEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_OF_ATTRIBUTE_VALUE);
	}

	private static final String ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE = "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"attributeIdIsForeignKeyForEdgeAttribute\" FOREIGN KEY (\"attributeId\") REFERENCES \"Attribute\" (\"attributeId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintOnAttributeColumnOfEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_EDGE_ATTRIBUTE = "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"gIdIsForeignKeyForEdgeAttribute\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromGraphColumnOfEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_GRAPH_OF_EDGE_ATTRIBUTE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_OF_ATTRIBUTE_VALUE = "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"eIdIsForeignKeyForEdgeAttribute\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromEdgeColumnOfEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_OF_ATTRIBUTE_VALUE);
	}

	private static final String DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_ATTRIBUTE = "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"attributeIdIsForeignKeyForEdgeAttribute\"";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromAttributeColumnOfEdgeAttributeValueTable()
			throws SQLException {
		return connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINT_FROM_EDGE_ATTRIBUTE);
	}

	// --- to insert schema information -------------------------------

	private static final String INSERT_SCHEMA = "INSERT INTO \"GraphSchema\" ( \"packagePrefix\", \"name\", \"serializedDefinition\" ) VALUES ( ?, ?, ? )";

	@Override
	public PreparedStatement insertSchema(Schema schema,
			String serializedDefinition) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				INSERT_SCHEMA, Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, schema.getPackagePrefix());
		statement.setString(2, schema.getName());
		statement.setString(3, serializedDefinition);
		return statement;
	}

	private static final String INSERT_TYPE = "INSERT INTO \"Type\"( \"qualifiedName\", \"schemaId\" ) VALUES ( ?, ? )";

	@Override
	public PreparedStatement insertType(String qualifiedName, int schemaId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_TYPE);
		statement.setString(1, qualifiedName);
		statement.setInt(2, schemaId);
		return statement;
	}

	private static final String INSERT_ATTRIBUTE = "INSERT INTO \"Attribute\" ( \"name\", \"schemaId\" ) VALUES ( ?, ? )";

	@Override
	public PreparedStatement insertAttribute(String name, int schemaId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_ATTRIBUTE);
		statement.setString(1, name);
		statement.setInt(2, schemaId);
		return statement;
	}

	// --- to insert a graph ------------------------------------------

	private static final String INSERT_GRAPH = "INSERT INTO \"Graph\" ( \"uid\", \"version\", \"vSeqVersion\", \"eSeqVersion\", \"typeId\" ) VALUES ( ?, ?, ?, ?, ? )";

	@Override
	public PreparedStatement insertGraph(String id, long graphVersion,
			long vertexListVersion, long edgeListVersion, int typeId)
			throws SQLException {
		PreparedStatement statement = connection.prepareStatement(INSERT_GRAPH,
				Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, id);
		statement.setLong(2, graphVersion);
		statement.setLong(3, vertexListVersion);
		statement.setLong(4, edgeListVersion);
		statement.setInt(5, typeId);
		return statement;
	}

	private static final String INSERT_GRAPH_ATTRIBUTE_VALUE = "INSERT INTO \"GraphAttributeValue\" ( \"gId\", \"attributeId\", \"value\" ) VALUES ( ?, ?, ? )";

	@Override
	public PreparedStatement insertGraphAttributeValue(int gId,
			int attributeId, String value) throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_GRAPH_ATTRIBUTE_VALUE);
		statement.setInt(1, gId);
		statement.setInt(2, attributeId);
		statement.setString(3, value);
		return statement;
	}

	// --- to insert a vertex ------------------------------------------

	private static final String INSERT_VERTEX = "INSERT INTO \"Vertex\" ( \"vId\", \"gId\", \"typeId\", \"lambdaSeqVersion\", \"sequenceNumber\" ) VALUES (?, ?, ?, ?, ?)";

	@Override
	public PreparedStatement insertVertex(int vId, int typeId, int gId,
			long incidenceListVersion, long sequenceNumberInVSeq)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		statement.setInt(3, typeId);
		statement.setLong(4, incidenceListVersion);
		statement.setLong(5, sequenceNumberInVSeq);
		return statement;
	}

	@Override
	public PreparedStatement insertVertex(DatabasePersistableVertex vertex)
			throws SQLException, GraphIOException {
		String sqlStatement = createSqlInsertStatementFor(vertex);
		PreparedStatement statement = getPreparedStatement(sqlStatement);
		setParametersForVertex(statement, vertex);
		setAttributeValuesForVertex(statement, vertex);
		return statement;
	}

	private void setAttributeValuesForVertex(PreparedStatement statement,
			DatabasePersistableVertex vertex) throws SQLException,
			GraphIOException {
		int i = 6;
		SortedSet<Attribute> attributes = vertex.getAttributedElementClass()
				.getAttributeList();
		for (Attribute attribute : attributes) {
			statement.setInt(i, vertex.getId());
			i++;
			statement.setInt(i, vertex.getGId());
			i++;
			int attributeId = graphDatabase.getAttributeId(vertex.getGraph(),
					attribute.getName());
			statement.setInt(i, attributeId);
			i++;
			String value = graphDatabase.convertToString(vertex, attribute
					.getName());
			statement.setString(i, value);
			i++;
		}
	}

	private void setParametersForVertex(PreparedStatement statement,
			DatabasePersistableVertex vertex) throws SQLException {
		statement.setInt(1, vertex.getId());
		statement.setInt(2, vertex.getGId());
		int typeId = graphDatabase.getTypeIdOf(vertex);
		statement.setInt(3, typeId);
		statement.setLong(4, vertex.getIncidenceListVersion());
		statement.setLong(5, vertex.getSequenceNumberInVSeq());
	}

	private static final String createSqlInsertStatementFor(
			DatabasePersistableVertex vertex) {
		String sqlStatement = INSERT_VERTEX;
		int attributeCount = vertex.getAttributedElementClass()
				.getAttributeList().size();
		for (int i = 0; i < attributeCount; i++) {
			sqlStatement += INSERT_VERTEX_ATTRIBUTE_VALUE;
		}
		return sqlStatement;
	}

	private static final String INSERT_VERTEX_ATTRIBUTE_VALUE = "INSERT INTO \"VertexAttributeValue\" ( \"vId\", \"gId\", \"attributeId\", \"value\" ) VALUES ( ?, ?, ?, ? )";

	@Override
	public PreparedStatement insertVertexAttributeValue(int vId, int gId,
			int attributeId, String value) throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_VERTEX_ATTRIBUTE_VALUE);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		statement.setString(4, value);
		return statement;
	}

	// --- to insert an edge -------------------------------------------

	private static final String INSERT_EDGE = "INSERT INTO \"Edge\" ( \"eId\", \"gId\", \"typeId\", \"sequenceNumber\" ) VALUES ( ?, ?, ?, ? )";

	@Override
	public PreparedStatement insertEdge(int eId, int gId, int typeId,
			long sequenceNumberInLambdaSeq) throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_EDGE);
		statement.setInt(1, Math.abs(eId));
		statement.setInt(2, gId);
		statement.setInt(3, typeId);
		statement.setLong(4, sequenceNumberInLambdaSeq);
		return statement;
	}

	@Override
	public PreparedStatement insertEdge(DatabasePersistableEdge edge,
			DatabasePersistableVertex alpha, DatabasePersistableVertex omega)
			throws SQLException, GraphIOException {
		String sqlStatement = createSqlInsertStatementFor(edge);
		PreparedStatement statement = getPreparedStatement(sqlStatement);
		setParametersForEdge(statement, edge);

		// insert incidence: normal edge
		statement.setInt(5, edge.getId());
		statement.setInt(6, edge.getGId());
		statement.setInt(7, edge.getIncidentVId());
		statement.setString(8, EdgeDirection.OUT.name());
		statement.setLong(9, edge.getSequenceNumberInLambdaSeq());

		// insert incidence: reversed edge
		DatabasePersistableEdge reversedEdge = (DatabasePersistableEdge) edge
				.getReversedEdge();
		statement.setInt(10, Math.abs(reversedEdge.getId()));
		statement.setInt(11, reversedEdge.getGId());
		statement.setInt(12, reversedEdge.getIncidentVId());
		statement.setString(13, EdgeDirection.IN.name());
		statement.setLong(14, reversedEdge.getSequenceNumberInLambdaSeq());

		// insert attribute values
		int i = 15;
		SortedSet<Attribute> attributes = edge.getAttributedElementClass()
				.getAttributeList();
		for (Attribute attribute : attributes) {
			statement.setInt(i, edge.getId());
			i++;
			statement.setInt(i, edge.getGId());
			i++;
			int attributeId = graphDatabase.getAttributeId(edge.getGraph(),
					attribute.getName());
			statement.setInt(i, attributeId);
			i++;
			String value = graphDatabase.convertToString(edge, attribute
					.getName());
			statement.setString(i, value);
			i++;
		}

		// update incidence list version of alpha
		statement.setLong(i, alpha.getIncidenceListVersion());
		i++;
		statement.setInt(i, alpha.getId());
		i++;
		statement.setInt(i, alpha.getGId());
		i++;

		// update incidence list version of omega
		statement.setLong(i, omega.getIncidenceListVersion());
		i++;
		statement.setInt(i, omega.getId());
		i++;
		statement.setInt(i, omega.getGId());

		return statement;
	}

	private void setParametersForEdge(PreparedStatement statement,
			DatabasePersistableEdge edge) throws SQLException {
		statement.setInt(1, Math.abs(edge.getId()));
		statement.setInt(2, edge.getGId());
		int typeId = graphDatabase.getTypeIdOf(edge);
		statement.setInt(3, typeId);
		statement.setLong(4, edge.getSequenceNumberInESeq());
	}

	private static final String createSqlInsertStatementFor(
			DatabasePersistableEdge edge) {
		String sqlStatement = INSERT_EDGE;
		sqlStatement += INSERT_INCIDENCE;
		sqlStatement += INSERT_INCIDENCE;
		int attributeCount = edge.getAttributedElementClass()
				.getAttributeList().size();
		for (int i = 0; i < attributeCount; i++) {
			sqlStatement += INSERT_EDGE_ATTRIBUTE_VALUE;
		}
		sqlStatement += UPDATE_INCIDENCE_LIST_VERSION;
		sqlStatement += UPDATE_INCIDENCE_LIST_VERSION;
		return sqlStatement;
	}

	private static final String INSERT_INCIDENCE = "INSERT INTO \"Incidence\" ( \"eId\", \"gId\", \"vId\", \"direction\", \"sequenceNumber\" ) VALUES ( ?, ?, ?, ?, ? )";

	@Override
	public PreparedStatement insertIncidence(int eId, int vId, int gId,
			long sequenceNumberInLambdaSeq) throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_INCIDENCE);
		statement.setInt(1, Math.abs(eId));
		statement.setInt(2, gId);
		statement.setInt(3, vId);
		if (eId > 0) {
			statement.setString(4, EdgeDirection.OUT.name());
		} else if (eId < 0) {
			statement.setString(4, EdgeDirection.IN.name());
		} else {
			throw new GraphException(
					"Cannot insert an incidence into database with incident edge id = 0.");
		}
		statement.setLong(5, sequenceNumberInLambdaSeq);
		return statement;
	}

	private static final String INSERT_EDGE_ATTRIBUTE_VALUE = "INSERT INTO \"EdgeAttributeValue\" ( \"eId\", \"gId\", \"attributeId\", \"value\" ) VALUES ( ?, ?, ?, ? )";

	@Override
	public PreparedStatement insertEdgeAttributeValue(int eId, int gId,
			int attributeId, String value) throws SQLException {
		PreparedStatement statement = getPreparedStatement(INSERT_EDGE_ATTRIBUTE_VALUE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		statement.setString(4, value);
		return statement;
	}

	// --- to open a graph schema -------------------------------------------

	private static final String SELECT_SCHEMA_ID = "SELECT \"schemaId\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND \"name\" = ?";

	@Override
	public PreparedStatement selectSchemaId(String packagePrefix, String name)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_SCHEMA_ID);
		statement.setString(1, packagePrefix);
		statement.setString(2, name);
		return statement;
	}

	private static final String SELECT_SCHEMA_DEFINITION_BY_NAME = "SELECT \"serializedDefinition\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND name = ?;";

	@Override
	public PreparedStatement selectSchemaDefinition(String packagePrefix,
			String schemaName) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement(SELECT_SCHEMA_DEFINITION_BY_NAME);
		statement.setString(1, packagePrefix);
		statement.setString(2, schemaName);
		return statement;
	}

	private static final String SELECT_SCHEMA_DEFINITION = "SELECT \"serializedDefinition\" FROM \"GraphSchema\" WHERE \"schemaId\" = ("
			+ "SELECT \"schemaId\" FROM \"Type\" WHERE \"typeId\" = ("
			+ "SELECT \"typeId\" FROM \"Graph\" WHERE \"uid\" = ?" + ")" + ")";

	@Override
	public PreparedStatement selectSchemaDefinitionForGraph(String uid)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_SCHEMA_DEFINITION);
		statement.setString(1, uid);
		return statement;
	}

	private static final String SELECT_SCHEMA_NAME = "SELECT \"packagePrefix\", \"name\" FROM \"GraphSchema\" WHERE \"schemaId\" = ("
			+ "SELECT \"schemaId\" FROM \"Type\" WHERE \"typeId\" = ("
			+ "SELECT \"typeId\" FROM \"Graph\" WHERE \"uid\" = ?" + ")" + ")";

	@Override
	public PreparedStatement selectSchemaNameForGraph(String uid)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_SCHEMA_NAME);
		statement.setString(1, uid);
		return statement;
	}

	private static final String SELECT_TYPES = "SELECT \"qualifiedName\", \"typeId\" FROM \"Type\" WHERE \"schemaId\" = "
			+ "(SELECT \"schemaId\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND \"name\" = ?)";

	@Override
	public PreparedStatement selectTypesOfSchema(String packagePrefix,
			String name) throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_TYPES);
		statement.setString(1, packagePrefix);
		statement.setString(2, name);
		return statement;
	}

	private static final String SELECT_ATTRIBUTES = "SELECT \"name\", \"attributeId\" FROM \"Attribute\" WHERE \"schemaId\" = "
			+ "(SELECT \"schemaId\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND \"name\" = ?)";

	@Override
	public PreparedStatement selectAttributesOfSchema(String packagePrefix,
			String name) throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_ATTRIBUTES);
		statement.setString(1, packagePrefix);
		statement.setString(2, name);
		return statement;
	}

	// --- to open a graph --------------------------------------------

	private static final String SELECT_GRAPH = "SELECT \"gId\", \"version\", \"vSeqVersion\", \"eSeqVersion\" FROM \"Graph\" WHERE \"uid\" = ?";

	@Override
	public PreparedStatement selectGraph(String id) throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_GRAPH);
		statement.setString(1, id);
		return statement;
	}

	private static final String COUNT_VERTICES_IN_GRAPH = "SELECT COUNT (*) FROM \"Vertex\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement countVerticesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(COUNT_VERTICES_IN_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String COUNT_EDGES_IN_GRAPH = "SELECT COUNT (*) FROM \"Edge\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement countEdgesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(COUNT_EDGES_IN_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String SELECT_VERTICES = "SELECT \"vId\", \"sequenceNumber\"  FROM \"Vertex\" WHERE \"gId\" = ? ORDER BY \"sequenceNumber\" ASC";

	@Override
	public PreparedStatement selectVerticesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_VERTICES);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String SELECT_EDGES = "SELECT \"eId\", \"sequenceNumber\"  FROM \"Edge\" WHERE \"gId\" = ? ORDER BY \"sequenceNumber\" ASC";

	@Override
	public PreparedStatement selectEdgesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_EDGES);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String SELECT_ATTRIBUTE_VALUES_OF_GRAPH = "SELECT \"name\", \"value\" FROM \"GraphAttributeValue\" JOIN \"Attribute\" ON \"GraphAttributeValue\".\"attributeId\" = \"Attribute\".\"attributeId\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement selectAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	// --- to get a vertex -------------------------------------------

	private static final String SELECT_VERTEX_WITH_INCIDENCES = "SELECT \"typeId\", \"lambdaSeqVersion\", \"Vertex\".\"sequenceNumber\", \"Incidence\".\"sequenceNumber\", \"direction\", \"eId\" FROM"
			+ "\"Vertex\" LEFT OUTER JOIN \"Incidence\" ON ( \"Vertex\".\"vId\" = \"Incidence\".\"vId\" AND \"Vertex\".\"gId\" = \"Incidence\".\"gId\" )"
			+ "WHERE \"Vertex\".\"vId\" = ? AND \"Vertex\".\"gId\" = ?"
			+ "ORDER BY \"Incidence\".\"sequenceNumber\" ASC";

	@Override
	public PreparedStatement selectVertexWithIncidences(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_VERTEX_WITH_INCIDENCES);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String SELECT_ATTRIBUTE_VALUES_OF_VERTEX = "SELECT \"attributeId\", \"value\" FROM \"VertexAttributeValue\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement selectAttributeValuesOfVertex(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_ATTRIBUTE_VALUES_OF_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to get an edge --------------------------------------------

	private static final String SELECT_EDGE_WITH_INCIDENCES = "SELECT \"typeId\", \"Edge\".\"sequenceNumber\", \"direction\", \"vId\", \"Incidence\".\"sequenceNumber\" FROM"
			+ "\"Edge\" INNER JOIN \"Incidence\" ON ( \"Edge\".\"eId\" = \"Incidence\".\"eId\" AND \"Edge\".\"gId\" = \"Incidence\".\"gId\" )"
			+ "WHERE \"Edge\".\"eId\" = ? AND \"Edge\".\"gId\" = ?";

	@Override
	public PreparedStatement selectEdgeWithIncidences(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_EDGE_WITH_INCIDENCES);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String SELECT_ATTRIBUTE_VALUES_OF_EDGE = "SELECT \"attributeId\", \"value\" FROM \"EdgeAttributeValue\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement selectAttributeValuesOfEdge(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_ATTRIBUTE_VALUES_OF_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to delete a graph ------------------------------------------

	private static final String DELETE_ATTRIBUTE_VALUES_OF_GRAPH = "DELETE FROM \"GraphAttributeValue\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String DELETE_EDGE_ATTRIBUTE_VALUES_OF_GRAPH = "DELETE FROM \"EdgeAttributeValue\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteEdgeAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_EDGE_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String DELETE_VERTEX_ATTRIBUTE_VALUES_OF_GRAPH = "DELETE FROM \"VertexAttributeValue\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteVertexAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_VERTEX_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String DELETE_INCIDENCES_OF_GRAPH = "DELETE FROM \"Incidence\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteIncidencesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_INCIDENCES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String DELETE_VERTICES_OF_GRAPH = "DELETE FROM \"Vertex\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteVerticesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_VERTICES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String DELETE_EDGES_OF_GRAPH = ""
			+ "DELETE FROM \"Edge\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteEdgesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_EDGES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private static final String DELETE_GRAPH = "DELETE FROM \"Graph\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteGraph(int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	// --- to delete a vertex -----------------------------------------

	private static final String DELETE_ATTRIBUTE_VALUES_OF_VERTEX = "DELETE FROM \"VertexAttributeValue\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteAttributeValuesOfVertex(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_ATTRIBUTE_VALUES_OF_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String SELECT_ID_OF_INCIDENT_EDGES_OF_VERTEX = "SELECT \"eId\" FROM \"Incidence\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement selectIncidentEIdsOfVertex(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(SELECT_ID_OF_INCIDENT_EDGES_OF_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String DELETE_VERTEX = "DELETE FROM \"Vertex\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteVertex(int vId, int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to delete an edge ------------------------------------------

	private static final String DELETE_ATTRIBUTE_VALUES_OF_EDGE = "DELETE FROM \"EdgeAttributeValue\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteAttributeValuesOfEdge(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_ATTRIBUTE_VALUES_OF_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String DELETE_INCIDENCES_OF_EDGE = "DELETE FROM \"Incidence\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteIncidencesOfEdge(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_INCIDENCES_OF_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String DELETE_EDGE = "DELETE FROM \"Edge\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteEdge(int eId, int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to update a graph ------------------------------------------

	private static final String UPDATE_ATTRIBUTE_VALUE_OF_GRAPH = "UPDATE \"GraphAttributeValue\" SET \"value\" = ? WHERE \"gId\" = ? AND \"attributeId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfGraph(int gId,
			int attributeId, String serializedValue) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_GRAPH);
		statement.setString(1, serializedValue);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		return statement;
	}

	private static final String UPDATE_ATTRIBUTE_VALUE_OF_GRAPH_AND_GRAPH_VERSION = "UPDATE \"GraphAttributeValue\" SET \"value\" = ? WHERE \"gId\" = ? AND \"attributeId\" = ?;"
			+ "UPDATE \"Graph\" SET \"version\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfGraphAndGraphVersion(
			int gId, int attributeId, String serializedValue, long graphVersion)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_GRAPH_AND_GRAPH_VERSION);
		statement.setString(1, serializedValue);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		statement.setLong(4, graphVersion);
		statement.setInt(5, gId);
		return statement;
	}

	private static final String UPDATE_GRAPH_UID = "UPDATE \"Graph\" SET \"uid\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateGraphId(int gId, String uid)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_GRAPH_UID);
		statement.setString(1, uid);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String UPDATE_GRAPH_VERSION = "UPDATE \"Graph\" SET \"version\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateGraphVersion(int gId, long version)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_GRAPH_VERSION);
		statement.setLong(1, version);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String UPDATE_VERTEX_LIST_VERSION = "UPDATE \"Graph\" SET \"vSeqVersion\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateVertexListVersionOfGraph(int gId,
			long version) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_VERTEX_LIST_VERSION);
		statement.setLong(1, version);
		statement.setInt(2, gId);
		return statement;
	}

	private static final String UPDATE_EDGE_LIST_VERSION = "UPDATE \"Graph\" SET \"eSeqVersion\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateEdgeListVersionOfGraph(int gId, long version)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_EDGE_LIST_VERSION);
		statement.setLong(1, version);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to update a vertex -----------------------------------------

	private static final String UPDATE_VERTEX_ID = "UPDATE \"Vertex\" SET \"vId\" = ? WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateIdOfVertex(int oldVId, int gId, int newVId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_VERTEX_ID);
		statement.setInt(1, newVId);
		statement.setInt(2, oldVId);
		statement.setInt(3, gId);
		return statement;
	}

	private static final String UPDATE_SEQUENCE_NUMBER_OF_VERTEX = "UPDATE \"Vertex\" SET \"sequenceNumber\" = ? WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateSequenceNumberInVSeqOfVertex(int vId,
			int gId, long sequenceNumberInVSeq) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_SEQUENCE_NUMBER_OF_VERTEX);
		statement.setLong(1, sequenceNumberInVSeq);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		return statement;
	}

	private static final String UPDATE_ATTRIBUTE_VALUE_OF_VERTEX = "UPDATE \"VertexAttributeValue\" SET \"value\" = ? WHERE \"vId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfVertex(int vId, int gId,
			int attributeId, String serializedValue) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_VERTEX);
		statement.setString(1, serializedValue);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		return statement;
	}

	private static final String UPDATE_ATTRIBUTE_VALUE_OF_VERTEX_AND_GRAPH_VERSION = "UPDATE \"VertexAttributeValue\" SET \"value\" = ? WHERE \"vId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?"
			+ " UPDATE \"Graph\" SET \"version\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfVertexAndGraphVersion(
			int vId, int gId, int attributeId, String serializedValue,
			long graphVersion) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_VERTEX_AND_GRAPH_VERSION);
		statement.setString(1, serializedValue);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		statement.setLong(5, graphVersion);
		statement.setInt(6, gId);
		return statement;
	}

	private static final String UPDATE_INCIDENCE_LIST_VERSION = "UPDATE \"Vertex\" SET \"lambdaSeqVersion\" = ? WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateLambdaSeqVersionOfVertex(int vId, int gId,
			long lambdaSeqVersion) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_INCIDENCE_LIST_VERSION);
		statement.setLong(1, lambdaSeqVersion);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		return statement;
	}

	// --- to update an edge ------------------------------------------

	private static final String UPDATE_EDGE_ID = "UPDATE \"Edge\" SET \"eId\" = ? WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateIdOfEdge(int oldEId, int gId, int newEId)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_EDGE_ID);
		statement.setInt(1, newEId);
		statement.setInt(2, oldEId);
		statement.setInt(3, gId);
		return statement;
	}

	private static final String UPDATE_SEQUENCE_NUMBER_IN_EDGE_LIST = "UPDATE \"Edge\" SET \"sequenceNumber\" = ? WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateSequenceNumberInESeqOfEdge(int eId, int gId,
			long SequenceNumberInESeq) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_SEQUENCE_NUMBER_IN_EDGE_LIST);
		statement.setLong(1, SequenceNumberInESeq);
		statement.setInt(2, eId);
		statement.setInt(3, gId);
		return statement;
	}

	private static final String UPDATE_ATTRIBUTE_VALUE_OF_EDGE = "UPDATE \"EdgeAttributeValue\" SET \"value\" = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfEdge(int eId, int gId,
			int attributeId, String serializedValue) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_EDGE);
		statement.setString(1, serializedValue);
		statement.setInt(2, eId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		return statement;
	}

	private static final String UPDATE_ATTRIBUTE_VALUE_OF_EDGE_AND_INCREMENT_GRAPH_VERSION = "UPDATE \"EdgeAttributeValue\" SET \"value\" = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?"
			+ " UPDATE \"Graph\" SET \"version\" = ? WHERE \"gId\" = ?;";

	@Override
	public PreparedStatement updateAttributeValueOfEdgeAndGraphVersion(int eId,
			int gId, int attributeId, String serializedValue, long graphVersion)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_EDGE_AND_INCREMENT_GRAPH_VERSION);
		statement.setString(1, serializedValue);
		statement.setInt(2, eId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		statement.setLong(5, graphVersion);
		statement.setInt(6, gId);
		return statement;
	}

	private static final String UPDATE_INCIDENT_VERTEX = "UPDATE \"Incidence\" SET \"vId\" = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"direction\" = ?";

	@Override
	public PreparedStatement updateIncidentVIdOfIncidence(int eId, int vId,
			int gId) throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_INCIDENT_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, Math.abs(eId));
		statement.setInt(3, gId);
		if (eId > 0) {
			statement.setString(4, EdgeDirection.OUT.name());
		} else if (eId < 0) {
			statement.setString(4, EdgeDirection.IN.name());
		}
		return statement;
	}

	private static final String UPDATE_SEQUENCE_NUMBER_IN_INCIDENCE_LIST = "UPDATE \"Incidence\" SET \"sequenceNumber\" = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"vId\" = ?";

	@Override
	public PreparedStatement updateSequenceNumberInLambdaSeqOfIncidence(
			int eId, int vId, int gId, long sequenceNumberInLambdaSeq)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(UPDATE_SEQUENCE_NUMBER_IN_INCIDENCE_LIST);
		statement.setLong(1, sequenceNumberInLambdaSeq);
		statement.setInt(2, Math.abs(eId));
		statement.setInt(3, gId);
		statement.setInt(4, vId);
		return statement;
	}

	private static final String STORED_PROCEDURE_REORGANIZE_VERTEX_LIST = ""; // TODO

	@Override
	public PreparedStatement createStoredProcedureToReorganizeVertexList()
			throws SQLException {
		return getPreparedStatement(STORED_PROCEDURE_REORGANIZE_VERTEX_LIST);
	}

	private static final String STORED_PROCEDURE_REORGANIZE_EDGE_LIST = ""; // TODO

	@Override
	public PreparedStatement createStoredProcedureToReorganizeEdgeList()
			throws SQLException {
		return getPreparedStatement(STORED_PROCEDURE_REORGANIZE_EDGE_LIST);
	}

	private static final String STORED_PROCEDURE_REORGANIZE_INCIDENCE_LIST = ""; // TODO

	@Override
	public PreparedStatement createStoredProcedureToReorganizeIncidenceList()
			throws SQLException {
		return getPreparedStatement(STORED_PROCEDURE_REORGANIZE_INCIDENCE_LIST);
	}

	private static final String STORED_PROCEDURE_INSERT_VERTEX = ""; // TODO

	public PreparedStatement createStoredProcedureToInsertVertex()
			throws SQLException {
		return getPreparedStatement(STORED_PROCEDURE_INSERT_VERTEX);
	}

	private static final String DELETE_SCHEMA = "DELETE FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND \"name\" = ?";

	@Override
	public PreparedStatement deleteSchema(String prefix, String name)
			throws SQLException {
		PreparedStatement statement = getPreparedStatement(DELETE_SCHEMA);
		statement.setString(1, prefix);
		statement.setString(2, name);
		return statement;
	}

	@Override
	public CallableStatement createReorganizeVertexListCall(int gId, long start)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallableStatement createReorganizeEdgeListCall(int gId, long start)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallableStatement createReorganizeIncidenceListCall(int vId,
			int gId, long start) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	private static final String SELECT_ID_OF_GRAPHS = "SELECT \"uid\" FROM \"Graph\"";

	@Override
	public PreparedStatement selectIdOfGraphs() throws SQLException {
		return getPreparedStatement(SELECT_ID_OF_GRAPHS);
	}

	private static final String CLEAR_TABLE_ATTRIBUTE = "DELETE FROM \""
			+ TABLE_ATTRIBUTE + "\"";

	public PreparedStatement clearTableAttribute() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_ATTRIBUTE);
	}

	private static final String CLEAR_TABLE_EDGE_ATTRIBUTE_VALUE = "DELETE FROM \""
			+ TABLE_EDGE_ATTRIBUTE_VALUE + "\"";

	public PreparedStatement clearTableEdgeAttributeValue() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_EDGE_ATTRIBUTE_VALUE);
	}

	private static final String CLEAR_TABLE_EDGE = "DELETE FROM \""
			+ TABLE_EDGE + "\"";

	public PreparedStatement clearTableEdge() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_EDGE);
	}

	private static final String CLEAR_TABLE_GRAPH_ATTRIBUTE_VALUE = "DELETE FROM \""
			+ TABLE_GRAPH_ATTRIBUTE_VALUE + "\"";

	public PreparedStatement clearTableGraphAttributeValue()
			throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_GRAPH_ATTRIBUTE_VALUE);
	}

	private static final String CLEAR_TABLE_GRAPH_SCHEMA = "DELETE FROM \""
			+ TABLE_SCHEMA + "\"";

	public PreparedStatement clearTableGraphSchema() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_GRAPH_SCHEMA);
	}

	private static final String CLEAR_TABLE_GRAPH = "DELETE FROM \""
			+ TABLE_GRAPH + "\"";

	public PreparedStatement clearTableGraph() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_GRAPH);
	}

	private static final String CLEAR_TABLE_INCIDENCE = "DELETE FROM \""
			+ TABLE_INCIDENCE + "\"";

	public PreparedStatement clearTableIncidence() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_INCIDENCE);
	}

	private static final String CLEAR_TABLE_TYPE = "DELETE FROM \""
			+ TABLE_TYPE + "\"";

	public PreparedStatement clearTableType() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_TYPE);
	}

	private static final String CLEAR_TABLE_ATTRIBUTE_VALUE = "DELETE FROM \""
			+ TABLE_VERTEX_ATTRIBUTE_VALUE + "\"";

	public PreparedStatement clearTableAttributeValue() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_ATTRIBUTE_VALUE);
	}

	private static final String CLEAR_TABLE_VERTEX = "DELETE FROM \""
			+ TABLE_VERTEX + "\"";

	public PreparedStatement clearTableVertex() throws SQLException {
		return getPreparedStatement(CLEAR_TABLE_VERTEX);
	}

	@Override
	public PreparedStatement clearAllTables() throws SQLException {
		throw new UnsupportedOperationException(
				"Does not work in Derby implementation.");
	}

}
