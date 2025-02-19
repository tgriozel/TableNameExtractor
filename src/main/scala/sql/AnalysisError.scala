package sql

object AnalysisError {
  sealed trait AnalysisError
  case object InvalidInputError extends AnalysisError
  case object InternalError extends AnalysisError
}
