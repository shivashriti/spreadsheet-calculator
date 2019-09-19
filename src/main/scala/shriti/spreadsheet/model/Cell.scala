package shriti.spreadsheet.model

import scala.collection.mutable.HashMap
import scala.util.Try
import shriti.spreadsheet.util.MathExpressionUtil._

/**
  * Cell is the atomic element of expression, that needs to be evaluated
  * @param spreadsheet  A reference of spreadsheet for look ups of other cell's data
  * @param expression   A value or an expression to be evaluated
  * @param result       Result of the expression after evaluation
  * @param isEvaluated  Flag to find if this cell has been evaluated
  * @param visited      Number of times the cell has been visited for evaluation (to be used to detect deep links and cyclic dependencies)
  */
case class Cell (spreadsheet: Spreadsheet, expression: String, var result: String, var isEvaluated: Boolean, var visited: Int)

/**
  * Cell companion object contains utility methods for Cell
  */
object Cell {

  /**
    * Creates an empty cell for given spreadsheet with expression
    * @param spreadsheet
    * @param expression
    * @return
    */
  def make(spreadsheet: Spreadsheet, expression: String): Cell =
    Cell(spreadsheet, expression, "0", false, 0)

  /**
    * Calculate the expression of cell and store it in cell's result
    * @param cell
    * @return
    */
  def evaluate(cell: Cell): String = {
    cell.visited += 1

    // find dependencies for this cell
    val cellReferences =
      cell.expression
        .split("[+-/*()]")
        .toStream
        .filter(v => v.nonEmpty && !isNumber(v))

    // creates a map of the dependencies and their results
    val referenceMap: HashMap[String, String] =
      cellReferences
        .foldLeft(HashMap.empty[String, String])((map, cellLabel) =>
          map.+=((cellLabel, Spreadsheet.resultOf(cell.spreadsheet, cellLabel))))

    // final expression to be evaluated
    val modifiedExpression = replaceWithResult(cell.expression, referenceMap)
    val result = calculate(modifiedExpression)
    cell.result = result
    cell.isEvaluated = true
    result
  }

  /**
    * Utility to replace references in expression with their values
    * @param expression
    * @param refMap
    * @return
    */
  def replaceWithResult(expression: String, refMap: HashMap[String, String]): String =
    refMap.foldLeft(expression)((a, b) => a.replaceAllLiterally(b._1, b._2))

  /**
    * Utility to check if the given string is a valid number
    * @param s
    * @return
    */
  def isNumber(s: String) =
    Try(s.toDouble).isSuccess
}
