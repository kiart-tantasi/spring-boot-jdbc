package com.kt.jdbcmysql.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JdbcTemplateTest {

    // DUMMY SP NAME
    private final String SP_NAME = "SP";

    // FOR NO ROW MAPPING CASE
    private final String COLUMN_1 = "COLUMN_1";
    private final String COLUMN_2 = "COLUMN_2";
    private final String COLUMN_3 = "COLUMN_3";
    private final String VALUE_1 = "FOO";
    private final int VALUE_2 = 111;
    private final boolean VALUE_3 = true;

    // FOR ROW MAPPER CASE
    private final String COLUMN_1_RM = "COLUMN_1_RM";
    private final String COLUMN_2_RM = "COLUMN_2_RM";
    private final int VALUE_1_RM = 456;
    private final String VALUE_2_RM = "456";
    private final List<String> ROW_MAPPER = List.of(COLUMN_1_RM, COLUMN_2_RM);

    // FOR MULTIPLE RESULT SETS CASE
    private final int FIRST_RESULT_SET_INDEX = 0;
    private final int SECOND_RESULT_SET_INDEX = 1;
    private final String COLUMN_1_2 = "COLUMN_1_2";
    private final String COLUMN_2_2 = "COLUMN_2_2";
    private final String COLUMN_3_2 = "COLUMN_3_2";
    private final String VALUE_1_2 = "BAR";
    private final int VALUE_2_2 = 222;
    private final boolean VALUE_3_2 = false;

    // FOR MULTIPLE RESULT SETS WITH ROW MAPPERS CASE
    private final String COLUMN_1_2_RM = "COLUMN_1_2_RM";
    private final String COLUMN_2_2_RM = "COLUMN_2_2_RM";
    private final int VALUE_1_2_RM = 789;
    private final String VALUE_2_2_RM = "789";
    private final List<String> ROW_MAPPER_2 = List.of(COLUMN_1_2_RM, COLUMN_2_2_RM);
    private final List<List<String>> ROW_MAPPERS = List.of(ROW_MAPPER, ROW_MAPPER_2);

    // BOOLEAN
    private final boolean RETURN_STATEMENT = true;
    private final boolean NOT_RETURN_STATEMENT = false;
    private final boolean MULTIPLE_RESULT_SETS = true;
    private final boolean NOT_MULTIPLE_RESULT_SETS = false;

    @Mock
    private JdbcTemplateHelper jdbcTemplateHelper;

    @InjectMocks
    private JdbcTemplate jdbcTemplate;

    @Test
    void executeStoredProcedure() throws SQLException {
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, NOT_RETURN_STATEMENT))
            .thenReturn(null);
        jdbcTemplate.executeSp(SP_NAME);
        verify(jdbcTemplateHelper).executeStoredProcedure(SP_NAME, NOT_RETURN_STATEMENT);
    }

    @Test
    void executeSpSingleResultSet() throws SQLException {
        final CallableStatement mockStatement = getMockStatement(NOT_MULTIPLE_RESULT_SETS);
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, RETURN_STATEMENT))
                .thenReturn(mockStatement);

        final Map<String, Object> receivedRow = jdbcTemplate.executeSpSingleResultSet(SP_NAME).get(0);
        assertEquals(VALUE_1, receivedRow.get(COLUMN_1));
        assertEquals(VALUE_2, receivedRow.get(COLUMN_2));
        assertEquals(VALUE_3, receivedRow.get(COLUMN_3));
    }

    @Test
    void executeSpSingleResultSetWithRowMapper() throws SQLException {
        final CallableStatement mockStatement = getMockStatementWithRowMapping(NOT_MULTIPLE_RESULT_SETS);
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, RETURN_STATEMENT))
                .thenReturn(mockStatement);

        final Map<String, Object> receivedRow = jdbcTemplate.executeSpSingleResultSetWithRowMapper(SP_NAME, ROW_MAPPER)
                .get(0);
        assertEquals(VALUE_1_RM, receivedRow.get(COLUMN_1_RM));
        assertEquals(VALUE_2_RM, receivedRow.get(COLUMN_2_RM));
    }

    @Test
    void executeSpSingleResultSetWithEmptyRowMapper() throws SQLException {
        final CallableStatement mockStatement = getMockStatement(NOT_MULTIPLE_RESULT_SETS);
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, RETURN_STATEMENT))
                .thenReturn(mockStatement);

        final List<String> EMPTY_ROW_MAPPER = new ArrayList<>();
        final Map<String, Object> receivedRow = jdbcTemplate
                .executeSpSingleResultSetWithRowMapper(SP_NAME, EMPTY_ROW_MAPPER)
                .get(0);
        assertEquals(VALUE_1, receivedRow.get(COLUMN_1));
        assertEquals(VALUE_2, receivedRow.get(COLUMN_2));
        assertEquals(VALUE_3, receivedRow.get(COLUMN_3));
    }

    @Test
    void executeSpMultipleResultSets() throws SQLException {
        final CallableStatement mockStatement = getMockStatement(MULTIPLE_RESULT_SETS);
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, RETURN_STATEMENT))
                .thenReturn(mockStatement);

        final List<List<Map<String, Object>>> receivedResultSets = jdbcTemplate.executeSpMultipleResultSets(SP_NAME);

        final Map<String, Object> firstResultSetFirstRow = receivedResultSets.get(FIRST_RESULT_SET_INDEX).get(0);
        assertEquals(VALUE_1, firstResultSetFirstRow.get(COLUMN_1));
        assertEquals(VALUE_2, firstResultSetFirstRow.get(COLUMN_2));
        assertEquals(VALUE_3, firstResultSetFirstRow.get(COLUMN_3));

        final Map<String, Object> secondResultSetFirstRow = receivedResultSets.get(SECOND_RESULT_SET_INDEX).get(0);
        assertEquals(VALUE_1_2, secondResultSetFirstRow.get(COLUMN_1_2));
        assertEquals(VALUE_2_2, secondResultSetFirstRow.get(COLUMN_2_2));
        assertEquals(VALUE_3_2, secondResultSetFirstRow.get(COLUMN_3_2));
    }

    @Test
    void executeSpMultipleResultSetsWithRowMappers() throws SQLException {
        final CallableStatement mockStatement = getMockStatementWithRowMapping(MULTIPLE_RESULT_SETS);
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, RETURN_STATEMENT))
                .thenReturn(mockStatement);

        final List<List<Map<String, Object>>> receivedResultSets = jdbcTemplate
                .executeSpMultipleResultSetsWithRowMappers(SP_NAME, ROW_MAPPERS);

        final Map<String, Object> firstResultSetFirstRow = receivedResultSets.get(FIRST_RESULT_SET_INDEX).get(0);
        assertEquals(VALUE_1_RM, firstResultSetFirstRow.get(COLUMN_1_RM));
        assertEquals(VALUE_2_RM, firstResultSetFirstRow.get(COLUMN_2_RM));

        final Map<String, Object> secondResultSetFirstRow = receivedResultSets.get(SECOND_RESULT_SET_INDEX).get(0);
        assertEquals(VALUE_1_2_RM, secondResultSetFirstRow.get(COLUMN_1_2_RM));
        assertEquals(VALUE_2_2_RM, secondResultSetFirstRow.get(COLUMN_2_2_RM));
    }

    @Test
    void executeSpMultipleResultSetsWithEmptyRowMapperList() throws SQLException {
        final CallableStatement mockStatement = getMockStatement(MULTIPLE_RESULT_SETS);
        when(jdbcTemplateHelper.executeStoredProcedure(SP_NAME, RETURN_STATEMENT))
                .thenReturn(mockStatement);

        final List<List<String>> EMPTY_ROW_MAPPER_LIST = new ArrayList<>();
        final List<List<Map<String, Object>>> receivedResultSets = jdbcTemplate
                .executeSpMultipleResultSetsWithRowMappers(SP_NAME, EMPTY_ROW_MAPPER_LIST);

        final Map<String, Object> firstResultSetFirstRow = receivedResultSets.get(FIRST_RESULT_SET_INDEX).get(0);
        assertEquals(VALUE_1, firstResultSetFirstRow.get(COLUMN_1));
        assertEquals(VALUE_2, firstResultSetFirstRow.get(COLUMN_2));
        assertEquals(VALUE_3, firstResultSetFirstRow.get(COLUMN_3));

        final Map<String, Object> secondResultSetFirstRow = receivedResultSets.get(SECOND_RESULT_SET_INDEX).get(0);
        assertEquals(VALUE_1_2, secondResultSetFirstRow.get(COLUMN_1_2));
        assertEquals(VALUE_2_2, secondResultSetFirstRow.get(COLUMN_2_2));
        assertEquals(VALUE_3_2, secondResultSetFirstRow.get(COLUMN_3_2));
    }

    /*
     * Private Utility Methods
     */
    private CallableStatement getMockStatement(boolean multipleResultSets) throws SQLException {

        // META DATA
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        if (multipleResultSets == false) {
            when(metaData.getColumnCount()).thenReturn(3);
            when(metaData.getColumnName(1)).thenReturn(COLUMN_1);
            when(metaData.getColumnName(2)).thenReturn(COLUMN_2);
            when(metaData.getColumnName(3)).thenReturn(COLUMN_3);
        } else {
            when(metaData.getColumnCount()).thenReturn(3).thenReturn(3);
            when(metaData.getColumnName(1)).thenReturn(COLUMN_1).thenReturn(COLUMN_1_2);
            when(metaData.getColumnName(2)).thenReturn(COLUMN_2).thenReturn(COLUMN_2_2);
            when(metaData.getColumnName(3)).thenReturn(COLUMN_3).thenReturn(COLUMN_3_2);
        }

        // RESULT SET
        final ResultSet rs = mock(ResultSet.class);

        // MOCK RETURN VALUE
        when(rs.getMetaData()).thenReturn(metaData);
        when(rs.getObject(COLUMN_1)).thenReturn(VALUE_1);
        when(rs.getObject(COLUMN_2)).thenReturn(VALUE_2);
        when(rs.getObject(COLUMN_3)).thenReturn(VALUE_3);
        if (multipleResultSets == false) {
            // NO STUBBING
        } else {
            when(rs.getObject(COLUMN_1_2)).thenReturn(VALUE_1_2);
            when(rs.getObject(COLUMN_2_2)).thenReturn(VALUE_2_2);
            when(rs.getObject(COLUMN_3_2)).thenReturn(VALUE_3_2);
        }

        // MOCK .NEXT()
        if (multipleResultSets == false) {
            when(rs.next())
                    .thenReturn(true).thenReturn(false);
        } else {
            when(rs.next())
                    .thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(false);
        }

        // STATEMENT
        final CallableStatement statement = mock(CallableStatement.class);
        when(statement.getResultSet()).thenReturn(rs);
        if (multipleResultSets == false) {
            // NO STUBBING
        } else {
            when(statement.getMoreResults()).thenReturn(true).thenReturn(false);
        }

        return statement;
    }

    private CallableStatement getMockStatementWithRowMapping(boolean multipleResultSets) throws SQLException {

        // RESULT SET
        final ResultSet rs = mock(ResultSet.class);

        // MOCK RETURN VALUE
        when(rs.getObject(COLUMN_1_RM)).thenReturn(VALUE_1_RM);
        when(rs.getObject(COLUMN_2_RM)).thenReturn(VALUE_2_RM);
        if (multipleResultSets == false) {
            // NO STUBBING
        } else {
            when(rs.getObject(COLUMN_1_2_RM)).thenReturn(VALUE_1_2_RM);
            when(rs.getObject(COLUMN_2_2_RM)).thenReturn(VALUE_2_2_RM);
        }

        // MOCK .NEXT()
        if (multipleResultSets == false) {
            when(rs.next())
                    .thenReturn(true).thenReturn(false);
        } else {
            when(rs.next())
                    .thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(false);
        }

        // STATEMENT
        final CallableStatement statement = mock(CallableStatement.class);
        when(statement.getResultSet()).thenReturn(rs);
        if (multipleResultSets == false) {
            // NO STUBBING
        } else {
            when(statement.getMoreResults()).thenReturn(true).thenReturn(false);
        }

        return statement;
    }
}
