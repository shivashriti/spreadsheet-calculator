package shriti.spreadsheet

import shriti.spreadsheet.model._
import shriti.spreadsheet.util._
import scala.util.{Try, Success, Failure}

/**
  * Main Spreadsheet Calculator App that facilitates entire processing
  */
object Calculator extends App{

  val inputFilePath = if(args.nonEmpty) args(0) else "inputfile.csv"

  // step 1. Read data from input csv file
  val data = Try(CsvUtil.readCsv(inputFilePath)) match {

    case Success(v) =>
      // step 2. Create spreadsheet for the data read
      val spreadsheet = Spreadsheet(v)

      // step 3. Attempt to calculate entire spreadsheet
      Try(spreadsheet.calculate()) match {

        case Success(results) =>
          // step 4. Prepares output csv file with results
          val outputFilePath = if(args.nonEmpty) args(1) else "outputfile.csv"
          CsvUtil.writeCsv(results, outputFilePath)

        // step 5. Report any errors if occurred
        case Failure(error) =>
          println(s"Error occurred: ${error.getMessage}")
      }

    case Failure(error) => println(s"Error occurred: ${error.getMessage}")
  }
}