package shriti.spreadsheet.model

import scala.util.{Try, Success}

/**
  * Spreadsheet is a two-dimensional array of Cells
  * @param cells
  */
case class Spreadsheet private(var cells: Array[Array[Cell]]) {
  def n_rows = cells.size
  def n_cols = cells(0).size

  /**
    * Calculate all cells' results of this spreadsheet
    * @return
    */
  def calculate(): List[Stream[String]] =
    cells
      .map(row => {
        row
          .toStream
          .map(Spreadsheet.resultOf)
      }).toList
}

/**
  * Spreadsheet companion object contains utility methods for Spreadsheet
  */
object Spreadsheet{
  import Cell._

  /**
    * Constructs a spreadsheet from list (defined rows) of large stream of columns
    * @param data
    * @return
    */
  def apply(data: List[Stream[String]]): Spreadsheet = {
    val n_rows: Int = data.length
    val n_columns: Int =
      if (n_rows != 0) data(0).length else 0
    val cells = Array.ofDim[Cell](n_rows, n_columns)
    val spreadsheet = Spreadsheet(cells)

    data.zipWithIndex
      .map(v => {
        val row = v._1
        val rowIndex = v._2
        val iterator = row.zipWithIndex.iterator

        // recursively create cells for columns in entire row stream
        def processRow(iterator: Iterator[(String, Int)]): Unit ={
          val colWithIndex = iterator.next()
          val column = colWithIndex._1
          val columnIndex = colWithIndex._2

          cells(rowIndex)(columnIndex) =
              Cell.make(spreadsheet, column.replace("=", ""))

          if(iterator.hasNext) processRow(iterator)
        }
        if(iterator.hasNext) processRow(iterator)
      })

    spreadsheet.cells = cells
    spreadsheet
  }

  /**
    * Gives row and column indexes for a given cell label e.g A12 gives row as 0 and column as 12
    * @param cellLabel
    * @return
    */
  def cellPosition(cellLabel: String): (Int, Int) = {
    val rowIndex = cellLabel.toCharArray()(0) - 65
    val colIndex = cellLabel.splitAt(1)._2.toInt
    (rowIndex, colIndex)
  }

  /**
    * Gives result of a cell in spreadsheet from its label
    * @param spreadsheet
    * @param cellLabel
    * @return
    */
  def resultOf(spreadsheet: Spreadsheet, cellLabel: String): String = {
    val (row, column) = cellPosition(cellLabel)

      // report error if out of limit cell reference is found in expression
    if(row >= spreadsheet.n_rows || column >= spreadsheet.n_cols)
      throw new Exception(s"Invalid Input! non-existing cell referred in Expression for $cellLabel")
    else {
      Try(spreadsheet.cells(row)(column)) match {
        case Success(cell) => resultOf(cell)
        case _ => throw new Exception("Invalid Input! Could not parse expression correctly")
      }
    }
  }

  /**
    * Gives result of a cell
    * @param cell
    * @return
    */
  def resultOf(cell: Cell): String =
      // if already evaluated, give result
    if (cell.isEvaluated) cell.result.toString
      // if not evaluated and visited less than 100 times, try evaluating
    else if (cell.visited < 100) evaluate(cell)
      // if already visited 100 times then report deep link or cyclic dependency error
    else throw new Exception("Invalid Input! Possible case of cyclic dependency")
}