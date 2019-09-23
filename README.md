## Spreadsheet Calculator

### Problem Statement
A spreadsheet consists of a two-dimensional array of cells, labeled A0, A1, etc. Rows are identified using letters, columns by numbers. Each cell contains either an integer (its value) or an expression. Expressions always start with a ‘=’ and can contain integers, cell references, operators ‘+’, ‘-‘, ‘*’, ‘/’ and parentheses ‘(‘, ‘)’ with the usual rules of evaluation.
Write a program (in Java, Scala or Kotlin) to read the input from a file, evaluate the values of all the cells, and write the output to an output file.
The input and output files should be in CSV format. For example, the following CSV input:
```
2,4,1,=A0+A1*A2
=A3*(A0+1),=B2,0,=A0+1
```
should produce the following output file:
```
2.00000,4.00000,1.00000,6.00000
18.00000,0.00000,0.00000,3.00000
```


### Solution

### Design
**Spreadsheet Models:**
- Cell: To represent the individual entity that holds the expression to be evaluated
- Spreadsheet: Two dimensional Array of cells. Array has been used for faster (constant time) look-ups and updates.

**Utils:**
- CsvUtil: To facilitate reading and writing to csv file.
- MathExpressionUtil: To calculate the result of an infix expression using scala compiler and reflection tools.

**Calculator App**
- The Main file Calculator uses all above to read input, create & evaluate spreadsheet and then prepares the output.


### Steps
- Create a spreadsheet of the given data. This will create all cells and include the reference to spreadsheet for look-ups, the expression, result to be calculated, a flag to indicate whether it is calculated and the count of how many times it has been visited for evaluation.
- Once the spreadsheet is created from csv, start calculating it from 1st cell and continue till last.
- If Cell depends on result of some other cell, recursively calculate it, toggle *isEvaluated* flag to true and use its result.
- All cells will thus be calculated only once.
- Facilitate lazy evaluation of cell result calculation to be able to work with large data files. Thus Stream has been used.
- After complete evaluation, write the results to output csv file in as expected.

    **Note**
- Cyclic dependencies if discovered are notified as message in error. To detect them, a count is used in each Cell that maintains the number of time a cell has been visited for evaluation. If it has been visited a pre-configured (100 in our App) large number of times and still not evaluated, it imposes risk of *StackOverflow* leading to **JVM OutOfMemory** errors. This means there are either too deep references (which may be valid prone to risk) or it's due to cyclic dependencies. The maximum allowed visit-count can be configured with one's space requirements and by testing with different input files. This not only catches cyclic dependencies but also highly-nested references that may lead to memory related errors.
- References to cell outside the working spreadsheet limit are also caught and notified in error.
- If any expression can not be calculated because of invalid inpits other than above errors, the respective cells are filled as NaN in the final output csv.
- By default the calculator app reads data from **inputfile.csv** and writes to **outputfile.csv**.
    

### Testing
Unit test cases have been added to check that
- calculator evaluates all cells properly
- detects cyclic dependencies and report in error messages
- detects non-existing cell references in expression and report in error messages


### Build Instructions
**Execute App with jar**

`java –jar spreasheet.jar inputfile.csv outputfile.csv`

**Execute App with sbt**

- On the root of the project, run `sbt compile` to compile the app.

    `~/myWorkspace/spreadsheet 👉 $sbt compile`

- On successful compilation, run `sbt run` to execute the app, this will use default input file **inputfile.csv**  so ensure to keep that file ready before running app.

    `~/myWorkspace/spreadsheet 👉 $sbt run`

*This will execute app for input file and write output to **outputfile.csv** in the root directory. If there are cyclic dependencies or non-existing cell references in the input, they will be reported as meaningful error messages on console.*

- To provide other input/output files, simply give them as arguments when you run the app

    `~/myWorkspace/spreadsheet 👉 $sbt run some-input.csv some-output.csv`

**Test**

- To run unit tests, run `sbt test`

    `~/myWorkspace/spreadsheet 👉 $sbt test`


### Packaging Instructions
- To create a jar for the app, assembly plugin has been used. Simply run `sbt assembly` on the root of the project. It uses main class *Calculator* to launch the created jar.

`~/myWorkspace/spreadsheet 👉 $sbt assembly`

*This will create spreadsheet.jar in spreadsheet/target/scala-2.12/*

### Further Improvements
- For performance improvement, multi-threading can be used during spreadsheet creation and evaluation. One way to achieve this easily is using *io* from **cats.effect** library. The approach is to maintain two **ExecutionContext**s, one default ExecutionContext for the main Calculator and one custom ExecutionContext with increased number of threads (pre-configured) for longer tasks like evaluation of cells. The idea is to shift the ExecutionContext to custom while starting evaluation and as soon as it is done, shift to normal context using `io.shift`.
- For space usage reduction, one can use **LinkedHashMap** for spreadsheet instead of Array. This gives flexibility of having bigger spreadsheets in separate chunks while still maintaining constant time access. However, there may be overhead of maintaining the 2D structure of spreadsheet separately.
- As an alternate way to catch cyclic dependencies, depth-first approach can be used with backtracking. The idea is to start with a cell that has fixed value. Treat this a node of graph and keep adding dependent nodes while checking for any cycles formed in graph. This approach may increase implementation complexity and space requirements.
