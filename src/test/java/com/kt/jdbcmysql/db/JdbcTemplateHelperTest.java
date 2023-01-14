package com.kt.jdbcmysql.db;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kt.jdbcmysql.models.SqlParameter;

@ExtendWith(MockitoExtension.class)
public class JdbcTemplateHelperTest {

    // DUMMY SP NAME
    final private String SP_NAME = "SP";

    // PARAMETERS
    final private String PARAM_NAME_1 = "PARAM_NAME_1";
    final private String PARAM_VALUE_1 = "PARAM_VALUE_1";
    final private String PARAM_VALUE_2 = "PARAM_VALUE_2";
    final private String PARAM_NAME_2 = "PARAM_NAME_2";

    // CALLS
    final private String CALL_NO_PARAMS = "{ call SP() }";
    final private String CALL_ONE_PARAM = "{ call SP(?) }";
    final private String CALL_TWO_PARAMS = "{ call SP(?, ?) }";

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    @InjectMocks
    private JdbcTemplateHelper jdbcTemplateHelper;

    @Test
    void executeStoredProcedureNoParamAndNoStatementReturn() throws SQLException {
        final String expectedCall = CALL_NO_PARAMS;
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, false);
        assertNull(statement);
    }

    @Test
    void executeStoredProcedureNoParam() throws SQLException {
        final String expectedCall = CALL_NO_PARAMS;
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, true);
        assertNotNull(statement);
    }

    @Test
    void executeStoredProcedureWithParamNoParamName() throws SQLException {
        final String expectedCall = CALL_ONE_PARAM;
        final SqlParameter param = new SqlParameter(PARAM_VALUE_1);
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, true, param);
        assertNotNull(statement);
    }

    @Test
    void executeStoredProcedureWithParamsNoParamName() throws SQLException {
        final String expectedCall = CALL_TWO_PARAMS;
        final SqlParameter param1 = new SqlParameter(PARAM_VALUE_1);
        final SqlParameter param2 = new SqlParameter(PARAM_VALUE_2);
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, true, param1, param2);
        assertNotNull(statement);
    }

    @Test
    void executeStoredProcedureWithParamWithParamName() throws SQLException {
        final String expectedCall = CALL_ONE_PARAM;
        final SqlParameter param = new SqlParameter(PARAM_NAME_1, PARAM_VALUE_1);
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, true, param);
        assertNotNull(statement);
    }

    @Test
    void executeStoredProcedureWithParamsWithParamNames() throws SQLException {
        final String expectedCall = CALL_TWO_PARAMS;
        final SqlParameter param1 = new SqlParameter(PARAM_NAME_1, PARAM_VALUE_1);
        final SqlParameter param2 = new SqlParameter(PARAM_NAME_2, PARAM_VALUE_2);
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, true, param1, param2);
        assertNotNull(statement);
    }

    @Test
    void executeStoredProcedureWithMixedParams() throws SQLException {
        final String expectedCall = CALL_TWO_PARAMS;

        final SqlParameter param1 = new SqlParameter(PARAM_NAME_1, PARAM_VALUE_1);
        final SqlParameter param2 = new SqlParameter(PARAM_VALUE_2);
        when(connection.prepareCall(expectedCall)).thenReturn(callableStatement);
        final CallableStatement statement = jdbcTemplateHelper.executeStoredProcedure(SP_NAME, true, param1, param2);
        assertNotNull(statement);

        // VERIFY THAT STATEMENT ARE SET WITH MIXED-TYPE PARAMETERS
        // WIH PARAM NAME
        verify(statement).setObject(PARAM_NAME_1, PARAM_VALUE_1);
        // WITHOUT PARAM NAME
        verify(statement).setObject(2, PARAM_VALUE_2);

        // OPPOSITE TEST
        verify(statement, never()).setObject(1, PARAM_VALUE_1);
        verify(statement, never()).setObject(PARAM_NAME_2, PARAM_VALUE_2);
    }
}
