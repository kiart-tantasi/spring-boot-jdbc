package com.kt.jdbcmysql.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kt.jdbcmysql.models.SqlParameter;

@Component
public class JdbcTemplate {

    @Autowired
    private JdbcTemplateHelper jdbcTemplateHelper;

    public void executeSp(final String sp, final SqlParameter... params) throws SQLException {
        this.jdbcTemplateHelper.executeStoredProcedure(sp, false, params);
    }

    public List<Map<String, Object>> executeSpSingleResultSet(final String sp, final SqlParameter... params)
            throws SQLException {
        final Statement statement = this.jdbcTemplateHelper.executeStoredProcedure(sp, true, params);
        return this.getSingleResultSet(statement, null);
    }

    public List<Map<String, Object>> executeSpSingleResultSetWithRowMapper(
            final String sp,
            final List<String> rowMapper,
            final SqlParameter... params) throws SQLException {
        final Statement statement = this.jdbcTemplateHelper.executeStoredProcedure(sp, true, params);
        return this.getSingleResultSet(statement, rowMapper);
    }

    public List<List<Map<String, Object>>> executeSpMultipleResultSets(final String sp, final SqlParameter... params)
            throws SQLException {
        final Statement statement = this.jdbcTemplateHelper.executeStoredProcedure(sp, true, params);
        return this.getMultipleResultSets(statement, null);
    }

    public List<List<Map<String, Object>>> executeSpMultipleResultSetsWithRowMappers(
            final String sp,
            final List<List<String>> rowMappers, final SqlParameter... params)
            throws SQLException {
        final Statement statement = this.jdbcTemplateHelper.executeStoredProcedure(sp, true, params);
        return this.getMultipleResultSets(statement, rowMappers);
    }

    // private methods

    private List<List<Map<String, Object>>> getMultipleResultSets(
            final Statement statement,
            final List<List<String>> rowMappers)
            throws SQLException {
        final List<List<Map<String, Object>>> multipleResultSets = new ArrayList<>();
        int rowMapperIndex = 0;
        boolean hasNextResultSet = true;

        while (hasNextResultSet) {
            final List<Map<String, Object>> singleResultSet;
            if (rowMappers == null || rowMappers.size() == 0) {
                singleResultSet = this.getSingleResultSet(statement, null);
            } else {
                singleResultSet = this.getSingleResultSet(statement, rowMappers.get(rowMapperIndex));
                rowMapperIndex++;
            }

            multipleResultSets.add(singleResultSet);
            hasNextResultSet = statement.getMoreResults();
        }

        return multipleResultSets;
    }

    private List<Map<String, Object>> getSingleResultSet(final Statement statement, final List<String> rowMapper)
            throws SQLException {
        final ResultSet rs = statement.getResultSet();
        final List<Map<String, Object>> singleResultSet = new ArrayList<>();
        while (rs.next()) {
            final Map<String, Object> row = (rowMapper == null || rowMapper.size() == 0)
                    ? this.getRowWithAllColumns(rs)
                    : this.getRowWithRowMapper(rs, rowMapper);
            singleResultSet.add(row);
        }
        return singleResultSet;
    }

    private Map<String, Object> getRowWithAllColumns(final ResultSet rs) throws SQLException {
        final Map<String, Object> row = new HashMap<>();
        final ResultSetMetaData rsMetaData = rs.getMetaData();
        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            final String columnName = rsMetaData.getColumnName(i);
            row.put(columnName, rs.getObject(columnName));
        }
        return row;
    }

    private Map<String, Object> getRowWithRowMapper(final ResultSet rs, final List<String> rowMapper)
            throws SQLException {
        final Map<String, Object> row = new HashMap<>();
        for (final String columnName : rowMapper) {
            row.put(columnName, rs.getObject(columnName));
        }
        return row;
    }
}
