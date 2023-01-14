-- 1: select db

USE mydb;

-- 2: create 'test' table and insert some dummy data

DROP TABLE IF EXISTS test;

CREATE TABLE test (
    name VARCHAR(254) NOT NULL,
    age INT NOT NULL,
    career VARCHAR(254) NOT NULL,
    created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score DECIMAL(10, 2) DEFAULT 50.00,
    dob DATE DEFAULT "2022-01-01"
);

INSERT INTO test (name, age, career) VALUES
('Kiart', 25, 'Engineer'),
('Polo', 32, 'CTO'),
('James', 25, 'Project Manager'),
('Keith', 25, 'Engineer'),
('Mark', 32, 'Project Manager');

-- 3: create stored procedures

DROP PROCEDURE IF EXISTS get_all;

DELIMITER //

CREATE PROCEDURE get_all()
BEGIN
    SELECT *  FROM test;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS get_by_age;

DELIMITER //

CREATE PROCEDURE get_by_age(
    IN $age INT
)
BEGIN
    SELECT * FROM test t
    WHERE t.age = $age
    ;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS get_by_career;

DELIMITER //

CREATE PROCEDURE get_by_career(
    IN $career VARCHAR(254)
)
BEGIN
    SELECT * FROM test t
    WHERE t.career = $career
    ;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS get_by_age_and_career;

DELIMITER //

CREATE PROCEDURE get_by_age_and_career(
    IN $age INT,
    IN $career VARCHAR(254)
)
BEGIN
	SELECT * FROM test t
    WHERE t.age = $age
    and t.career = $career
    ;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS insert_employee;

DELIMITER //

CREATE PROCEDURE insert_employee(
    IN $name VARCHAR(254),
    IN $age INT,
    IN $career VARCHAR(254)
)
BEGIN
    INSERT INTO test    (name, age, career)
    VALUES              ($name, $age, $career)
    ;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS insert_employee_full;

DELIMITER //

CREATE PROCEDURE insert_employee_full(
    IN $name VARCHAR(254),
    IN $age INT,
    IN $career VARCHAR(254),
    IN $created_timestamp TIMESTAMP,
    IN $score DECIMAL(10, 2),
    IN $dob DATE
)
BEGIN
    INSERT INTO test    (name, age, career, created_timestamp, score, dob)
    VALUES              ($name, $age, $career, $created_timestamp, $score, $dob)
    ;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS delete_employee;

DELIMITER //

CREATE PROCEDURE delete_employee(
    IN $name VARCHAR(254),
    IN $age INT,
    IN $career VARCHAR(254)
)
BEGIN
    DELETE FROM test
    WHERE name = $name
    AND age = $age
    AND career = $career
    ;
END //

DELIMITER ;

--

DROP PROCEDURE IF EXISTS get_careers_result_sets;

DELIMITER //

CREATE PROCEDURE get_careers_result_sets(
    IN $career_one VARCHAR(254),
    IN $career_two VARCHAR(254)
)
BEGIN
    SELECT * FROM test t
    WHERE t.career = $career_one
    ;
    SELECT * FROM test t
    WHERE t.career = $career_two
    ;
END //

DELIMITER ;
