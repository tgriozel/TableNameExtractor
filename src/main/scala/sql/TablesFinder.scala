package sql

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.util.TablesNamesFinder
import sql.AnalysisError._

import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Failure, Success, Try}

object TablesFinder {
  def extractTableNames(sql: String): Either[AnalysisError, Set[String]] =
    Try {
      val statements = CCJSqlParserUtil.parseStatements(sql)
      val allTableNames = statements.asScala.map(_.toString).map(TablesNamesFinder.findTables)
      allTableNames.flatMap(_.asScala).toSet
    } match {
      case Success(tableNames) =>
        Right(tableNames)
      case Failure(_: JSQLParserException) =>
        Left(InvalidInputError)
      case Failure(_) =>
        Left(InternalError)
    }
}
