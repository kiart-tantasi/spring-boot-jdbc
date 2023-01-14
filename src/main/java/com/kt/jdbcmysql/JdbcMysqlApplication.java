package com.kt.jdbcmysql;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.kt.jdbcmysql.db.JdbcTemplate;
import com.kt.jdbcmysql.models.SqlParameter;

@SpringBootApplication
public class JdbcMysqlApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(JdbcMysqlApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {

			// Getting Single Result Set Without Row Mapper (Returning All Columns)
			System.out.println("\n[----- SINGLE RESULT SET -----]");
			System.out.println("\nEmployees who are 25 year old and work as Engineers:");
			List<Map<String, Object>> singleResultSet = this.jdbcTemplate.executeSpSingleResultSet(
					"get_by_age_and_career",
					new SqlParameter("$age", 25),
					new SqlParameter("$career", "Engineer"));
			this.printPeople(singleResultSet);

			// Executing Stored Procedure (No Return)
			System.out.println("\nInserting Joseph as a new employee...");
			this.jdbcTemplate.executeSp("insert_employee",
					new SqlParameter("$name", "Joseph"),
					new SqlParameter("$age", 29),
					new SqlParameter("$career", "Data Scientist"));

			// Getting Single Result Set With Row Mapper (Returning Specific Columns)
			System.out.println("\n[----- SINGLE RESULT SET WITH ROW MAPPER -----]");
			System.out.println("\nAll employees:");
			final List<String> rowMapper = List.of("name");
			singleResultSet = this.jdbcTemplate.executeSpSingleResultSetWithRowMapper("get_all",
					rowMapper);
			this.printPeopleWithRowMapper(singleResultSet, rowMapper);

			// Cleaning up Joseph
			System.out.println("\nDeleting Joseph...");
			this.jdbcTemplate.executeSp("delete_employee",
					new SqlParameter("$name", "Joseph"),
					new SqlParameter("$age", 29),
					new SqlParameter("$career", "Data Scientist"));

			// Getting Multiple Result Sets Without Row Mappers (Returning All Columns)
			List<List<Map<String, Object>>> multipleResultSets = this.jdbcTemplate.executeSpMultipleResultSets(
					"get_careers_result_sets",
					new SqlParameter("$career_one", "Project Manager"),
					new SqlParameter("$career_two", "CTO"));
			System.out.println("\n[----- MULTIPLE RESULT SETS -----]\n");
			this.printMultipleCareers(multipleResultSets);

			// Getting Multiple Result Sets With Row Mappers (Returning Specific Columns)
			final List<List<String>> rowMappers = List.of(
					List.of("name", "age"),
					List.of("name", "score", "dob"));
			multipleResultSets = this.jdbcTemplate.executeSpMultipleResultSetsWithRowMappers(
					"get_careers_result_sets",
					rowMappers,
					new SqlParameter("$career_one", "Project Manager"),
					new SqlParameter("$career_two", "CTO"));
			System.out.println("[----- MULTIPLE RESULT SETS WITH ROW MAPPERS -----]\n");
			this.printMultipleCareersWithRowMappers(multipleResultSets, rowMappers);
		};
	}

	private void printPeople(final List<Map<String, Object>> singleResultSet) throws SQLException {
		for (final Map<String, Object> sqlRow : singleResultSet) {
			this.printPerson(sqlRow);
		}
	}

	private void printPeopleWithRowMapper(final List<Map<String, Object>> singleResultSet,
			final List<String> rowMapper) {
		for (final Map<String, Object> sqlRow : singleResultSet) {
			this.printPersonWithRowMapper(sqlRow, rowMapper);
		}
	}

	private void printMultipleCareers(final List<List<Map<String, Object>>> multipleResultSets) throws SQLException {
		for (final List<Map<String, Object>> singleResultSet : multipleResultSets) {
			System.out.println("Current result set:");
			for (final Map<String, Object> sqlRow : singleResultSet) {
				this.printPerson(sqlRow);
			}
			System.out.println("");
		}
	}

	private void printMultipleCareersWithRowMappers(final List<List<Map<String, Object>>> multipleResultSets,
			final List<List<String>> rowMappers) throws SQLException {
		int rowMapperIndex = 0;
		for (final List<Map<String, Object>> singleResultSet : multipleResultSets) {
			System.out.println("Current result set:");
			final List<String> rowMapper = rowMappers.get(rowMapperIndex);
			for (final Map<String, Object> sqlRow : singleResultSet) {
				printPersonWithRowMapper(sqlRow, rowMapper);
			}
			rowMapperIndex++;
			System.out.println("");
		}
	}

	private void printPerson(final Map<String, Object> sqlRow) throws SQLException {
		System.out
				.println(String.format("name - %s, career - %s, age - %s, created_timestamp - %s, score - %s, dob - %s",
						sqlRow.get("name"),
						sqlRow.get("career"),
						sqlRow.get("age"),
						sqlRow.get("created_timestamp"),
						sqlRow.get("score"),
						sqlRow.get("dob")));
	}

	private void printPersonWithRowMapper(final Map<String, Object> sqlRow, final List<String> rowMapper) {
		boolean firstRow = true;
		String message = "";
		for (final String column : rowMapper) {
			if (firstRow == true) {
				message += String.format("%s - %s", column, sqlRow.get(column));
				firstRow = false;
			} else {
				message += String.format(", %s - %s", column, sqlRow.get(column));
			}
		}
		System.out.println(message);
	}
}
