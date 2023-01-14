package com.kt.jdbcmysql.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kt.jdbcmysql.models.SqlParameter;

@Component
public class JdbcTemplateHelper {

    @Autowired
    private Connection connection;

    public CallableStatement executeStoredProcedure(
            String sp, final boolean returnStatement,
            final SqlParameter... params)
            throws SQLException {
        final int paramsSize = params.length;
        sp = this.prepareStoredProcedureToCall(sp, paramsSize);
        final CallableStatement statement = this.connection.prepareCall(sp);

        for (int index = 0; index < paramsSize; index++) {
            final SqlParameter param = params[index];
            final int sqlParameterIndex = index + 1;
            this.setSqlParameter(statement, param, sqlParameterIndex);
        }

        statement.execute();

        if (returnStatement == false) {
            return null;
        }
        return statement;
    }

    private void setSqlParameter(final CallableStatement statement, final SqlParameter param,
            final int sqlParameterIndex)
            throws SQLException {
        final Object value = param.getValue();
        final String parameterName = param.getParameterName();
        if (parameterName == null) {
            statement.setObject(sqlParameterIndex, value);
        } else {
            statement.setObject(parameterName, value);
        }
    }

    private String prepareStoredProcedureToCall(final String sp, final int paramsSize) {
        String questionsMarks = "";
        for (int i = 0; i < paramsSize; i++) {
            if (i == (paramsSize - 1)) {
                questionsMarks += "?";
            } else {
                questionsMarks += "?, ";
            }
        }
        return String.format("{ call %s(%s) }", sp, questionsMarks);
    }
}
