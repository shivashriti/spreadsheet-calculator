package shriti.spreadsheet.util

import java.io.{BufferedWriter, FileWriter}

import scala.util.{Try, Success}

/**
  * Utility for dealing with csv file creation and read
  */
object CsvUtil {

  /**
    * Reads data from input csv file
    * @param path
    * @return
    */
  def readCsv(path: String): List[Stream[String]] = {
   val bufferedSource = Try(io.Source.fromFile(path)) match {
     case Success(value) => value
     case _ => throw new Exception("Input file does not exist")
   }
   val data = bufferedSource.getLines().toList
      .map(line => {
        line.split(",")
          .toStream
          .map(_.trim)
      })
    bufferedSource.close()
    data
  }

  /**
    * Writes results in output csv file
    * @param data
    * @param args
    */
  def writeCsv(data: List[Stream[String]], path: String) = {
    val bufferedWriter = new BufferedWriter(new FileWriter(path))

    data.map(row => {
      def processRow(iterator: Iterator[String]): Unit ={
        bufferedWriter.append(iterator.next)
        if(iterator.hasNext) {
          bufferedWriter.append(",")
          processRow(iterator)
        }
      }
      processRow(row.iterator)
      bufferedWriter.newLine()
    })
    bufferedWriter.flush()
    bufferedWriter.close()
    println("Calculated spreadsheet and saved result at outputfile.csv")
  }
}
