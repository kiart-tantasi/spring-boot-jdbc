package com.kt.jdbcmysql.models;

public class SqlParameter {
    private String parameterName = null;
    private Object value;

    public SqlParameter(String parameterName, Object value) {
        this.parameterName = parameterName;
        this.value = value;
    }

    public SqlParameter(Object value) {
        this.value = value;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public Object getValue() {
        return this.value;
    }
}
