package org.springframework.samples.petclinic.customers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify PostgreSQL profile functionality.
 * This test validates that the application runs correctly with PostgreSQL
 * and that all entries described in V1__Create_customers_schema_postgresql.sql exist in the database.
 */
//@SpringBootTest
//@ActiveProfiles("postgres")
public class PostgreSqlIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Test
    public void verifyTypesTableStructure() {
        // Verify identity column
        verifyIdentityColumn("types", "id");

        // Verify other columns
        verifyVarcharColumn("types", "name", 80, true);

        // Verify primary key constraint
        verifyPrimaryKeyConstraint("types", "pk_types");

        // Verify index
        verifyIndex("types", "idx_types_name", "CREATE INDEX idx_types_name ON public.types USING btree (name)");
    }


//    @Test
    public void verifyOwnersTableStructure() {
        // Verify identity column
        verifyIdentityColumn("owners", "id");

        // Verify other columns
        verifyVarcharColumn("owners", "first_name", 30, true);
        verifyVarcharColumn("owners", "last_name", 30, true);
        verifyVarcharColumn("owners", "address", 255, true);
        verifyVarcharColumn("owners", "city", 80, true);
        verifyVarcharColumn("owners", "telephone", 20, true);

        // Verify primary key constraint
        verifyPrimaryKeyConstraint("owners", "pk_owners");

        // Verify index
        verifyIndex("owners", "idx_owners_last_name", "CREATE INDEX idx_owners_last_name ON public.owners USING btree (last_name)");
    }

//    @Test
    public void verifyPetsTableStructure() {
        // Verify identity column
        verifyIdentityColumn("pets", "id");

        // Verify other columns
        verifyVarcharColumn("pets", "name", 30, true);
        verifyDateColumn("pets", "birth_date", true);
        verifyIntegerColumn("pets", "type_id", false);
        verifyIntegerColumn("pets", "owner_id", false);

        // Verify primary key constraint
        verifyPrimaryKeyConstraint("pets", "pk_pets");

        // Verify index
        verifyIndex("pets", "idx_pets_name", "CREATE INDEX idx_pets_name ON public.pets USING btree (name)");

        // Verify foreign keys
        verifyForeignKey("pets", "fk_pets_on_owner", "owner_id", "owners", "id");
        verifyForeignKey("pets", "fk_pets_on_type", "type_id", "types", "id");
    }

    // Helper methods for database validation
    private void verifyIdentityColumn(String tableName, String columnName) {
        String query = """
            SELECT table_schema, column_name, data_type, is_nullable, is_identity, identity_generation
            FROM information_schema.columns
            WHERE table_name = ? AND column_name = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName, columnName);
        assertThat(results).hasSize(1);
        Map<String, Object> column = results.get(0);
        assertThat(column.get("column_name")).isEqualTo(columnName);
        assertThat(column.get("data_type")).isEqualTo("integer");
        assertThat(column.get("is_nullable")).isEqualTo("NO");
        assertThat(column.get("is_identity")).isEqualTo("YES");
        assertThat(column.get("identity_generation")).isEqualTo("BY DEFAULT");
    }

    private void verifyVarcharColumn(String tableName, String columnName, int maxLength, boolean nullable) {
        String query = """
            SELECT column_name, data_type, character_maximum_length, is_nullable
            FROM information_schema.columns
            WHERE table_name = ? AND column_name = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName, columnName);
        assertThat(results).hasSize(1);
        Map<String, Object> column = results.get(0);
        assertThat(column.get("column_name")).isEqualTo(columnName);
        assertThat(column.get("data_type")).isEqualTo("character varying");
        assertThat(column.get("character_maximum_length")).isEqualTo(maxLength);
        assertThat(column.get("is_nullable")).isEqualTo(nullable ? "YES" : "NO");
    }

    private void verifyIntegerColumn(String tableName, String columnName, boolean nullable) {
        String query = """
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns
            WHERE table_name = ? AND column_name = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName, columnName);
        assertThat(results).hasSize(1);
        Map<String, Object> column = results.get(0);
        assertThat(column.get("column_name")).isEqualTo(columnName);
        assertThat(column.get("data_type")).isEqualTo("integer");
        assertThat(column.get("is_nullable")).isEqualTo(nullable ? "YES" : "NO");
    }

    private void verifyDateColumn(String tableName, String columnName, boolean nullable) {
        String query = """
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns
            WHERE table_name = ? AND column_name = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName, columnName);
        assertThat(results).hasSize(1);
        Map<String, Object> column = results.get(0);
        assertThat(column.get("column_name")).isEqualTo(columnName);
        assertThat(column.get("data_type")).isEqualTo("date");
        assertThat(column.get("is_nullable")).isEqualTo(nullable ? "YES" : "NO");
    }

    private void verifyPrimaryKeyConstraint(String tableName, String constraintName) {
        String query = """
            SELECT constraint_name, table_name, constraint_type
            FROM information_schema.table_constraints
            WHERE table_name = ? AND constraint_name = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName, constraintName);
        assertThat(results).hasSize(1);
        Map<String, Object> constraint = results.get(0);
        assertThat(constraint.get("constraint_name")).isEqualTo(constraintName);
        assertThat(constraint.get("table_name")).isEqualTo(tableName);
        assertThat(constraint.get("constraint_type")).isEqualTo("PRIMARY KEY");
    }

    private void verifyIndex(String tableName, String indexName, String expectedDefinition) {
        String query = """
            SELECT schemaname, tablename, indexname, indexdef
            FROM pg_indexes
            WHERE tablename = ? AND indexname = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, tableName, indexName);
        assertThat(results).hasSize(1);
        Map<String, Object> index = results.get(0);
        assertThat(index.get("indexname")).isEqualTo(indexName);
        assertThat(index.get("tablename")).isEqualTo(tableName);
        assertThat(index.get("indexdef")).isEqualTo(expectedDefinition);
    }

    private void verifyForeignKey(String tableName, String constraintName, String columnName,
                                  String referencedTable, String referencedColumn) {
        String query = """
            SELECT kcu.constraint_name, kcu.column_name, ccu.table_name AS referenced_table_name, ccu.column_name AS referenced_column_name
            FROM information_schema.key_column_usage kcu
            JOIN information_schema.constraint_column_usage ccu
                ON kcu.constraint_name = ccu.constraint_name
            WHERE kcu.constraint_name = ? AND kcu.table_name = ?
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, constraintName, tableName);
        assertThat(results).hasSize(1);
        Map<String, Object> fk = results.get(0);
        assertThat(fk.get("constraint_name")).isEqualTo(constraintName);
        assertThat(fk.get("column_name")).isEqualTo(columnName);
        assertThat(fk.get("referenced_table_name")).isEqualTo(referencedTable);
        assertThat(fk.get("referenced_column_name")).isEqualTo(referencedColumn);
    }
}
