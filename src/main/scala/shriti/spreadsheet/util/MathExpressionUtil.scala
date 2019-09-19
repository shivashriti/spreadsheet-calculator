package shriti.spreadsheet.util

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox
import scala.util.{Try, Success}

/**
  * Utility for evaluating Mathematical expressions using scala reflect
  */
object MathExpressionUtil {

  private lazy val toolbox = currentMirror.mkToolBox()

  /**
    * Evaluate an infix expression and gives result
    * @param expression
    * @return
    */
  def calculate(expression: String): String = {

    Try(toolbox.eval(toolbox.parse(expression))) match {
      case Success(v) => adjustPrecision(v.toString)
      case _ => "NaN"
    }
  }

  /**
    * Sets the precision to five digits after decimal for a number
    * @param n
    * @return
    */
  def adjustPrecision(n: String): String =
    Try(n.toDouble) match {
      case Success(number) => f"$number%.5f"
      case _ => "NaN"
    }
}
