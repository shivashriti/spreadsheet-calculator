package shriti.spreadsheet

import org.scalatest._
import shriti.spreadsheet.model.Spreadsheet

class SpreadsheetSpec extends FlatSpec with Matchers {

  "Spreadsheet Calculator" should "calculate all cells correctly" in {
    val inputData = List(
      Stream("2","4","1","=A0+A1*A2"),
      Stream("=A3*(A0+1)", "=B2","0","=A0+1")
    )
    val expectedOutput = List(
      Stream("2.00000","4.00000","1.00000","6.00000"),
      Stream("18.00000","0.00000","0.00000","3.00000")
    )

    val spreadsheet = Spreadsheet(inputData)
    spreadsheet.calculate() should be (expectedOutput)
  }

  "Spreadsheet Calculator" should "report invalid references in cell expression" in {
    val inputData = List(
      Stream("2","4","1","=A0+A1*A4"),    // A4 does not exist in spreadsheet
      Stream("=A3*(A0+1)", "=B2","0","=A0+1")
    )

    val spreadsheet = Spreadsheet(inputData)
    val thrown = intercept[Exception]{
      spreadsheet.calculate()
    }
    assert(thrown.getMessage contains("Invalid Input! non-existing cell referred in Expression"))
  }

  "Spreadsheet Calculator" should "report cyclic dependencies in input data" in {
    val inputData = List(
      Stream("=B3-1","4","1","=A0+A1*A4"),    // cyclic dependency in A0 and B3
      Stream("=A3*(A0+1)", "=B2","0","=A0+1")
    )

    val spreadsheet = Spreadsheet(inputData)
    val thrown = intercept[Exception]{
      spreadsheet.calculate()
    }
    assert(thrown.getMessage contains("cyclic dependency"))
  }
}
