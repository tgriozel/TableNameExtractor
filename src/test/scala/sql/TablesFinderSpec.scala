package sql

import munit.FunSuite
import sql.AnalysisError._

class TablesFinderSpec extends FunSuite {
  test("extractTableNames should return InvalidInputError for invalid SQL input") {
    // given
    val invalidSql = "SELECT * FROM"
    // when
    val result = TablesFinder.extractTableNames(invalidSql)
    // then
    assertEquals(result, Left(InvalidInputError))
  }

  test("extractTableNames should return the set of table names contained in a valid SQL input") {
    // given
    val joinQuery = "SELECT t1.col1, t2.col2 FROM table1 t1 JOIN table2 t2 ON t1.id = t2.id WHERE t1.col1 = 'value'"
    val joinExpected = Set("table1", "table2")
    val subqueryQuery = "SELECT col1 FROM (SELECT col1 FROM table1) AS subquery"
    val subqueryExpected = Set("table1")
    val multipleStatementQuery = "SELECT * FROM table1; SELECT * FROM table2"
    val multipleStatementExpected = Set("table1", "table2")
    val updateQuery =
      """
        |UPDATE wine w
        |SET stock = stock - (SELECT SUM (quantity) FROM order WHERE date = CURRENT_DATE AND order.wine_name = w.name)
        |WHERE w.name IN (SELECT order.wine_name FROM order WHERE date = CURRENT_DATE)
        |""".stripMargin
    val updateExpected = Set("wine", "order")
    val insertQuery =
      """
        |INSERT INTO employees(employee_id, first_name, last_name, age, country)
        |VALUES
        |(1, 'Thomas', 'Griozel', 999, 'FR');
        |""".stripMargin
    val insertExpected = Set("employees")
    val withWindowClauseQuery =
      """
        |WITH employee_ranking AS (
        |  SELECT
        |    employee_id,
        |    salary,
        |    RANK() OVER (ORDER BY salary DESC) as ranking
        |  FROM employee
        |)
        |SELECT employee_id, salary
        |FROM employee_ranking
        |WHERE ranking <= 10
        |""".stripMargin
    val withWindowClauseExpected = Set("employee")
    val nestedWithQuery =
      """
        |WITH a as (select 1 from table1)
        |  (WITH b as (select 2 from table2)
        |    (WITH c as (select 3 from table3)
        |      select * from a, b, c))
        |""".stripMargin
    val nestedWithExpected = Set("table1", "table2", "table3")
    val inputWithExpectedPairs = Seq(
      (joinQuery, joinExpected),
      (subqueryQuery, subqueryExpected),
      (multipleStatementQuery, multipleStatementExpected),
      (updateQuery, updateExpected),
      (insertQuery, insertExpected),
      (withWindowClauseQuery, withWindowClauseExpected),
      (nestedWithQuery, nestedWithExpected),
    )
    inputWithExpectedPairs.map { case (query, expected) =>
      // when
      val result = TablesFinder.extractTableNames(query)
      // then
      assertEquals(result, Right(expected))
    }
  }
}
