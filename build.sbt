lazy val root = (project in file("."))
  .settings(
    name := "Spreadsheet Calculator",
    version := "0.0.1",
    scalaVersion := "2.12.0",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    assemblyJarName in assembly := "spreasheet.jar",
    test in assembly := {},
    mainClass in assembly := Some("shriti.spreadsheet.Calculator")
  )
