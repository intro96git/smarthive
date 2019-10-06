ThisBuild / version         := "1.0.0-SNAPSHOT"
ThisBuild / organization    := "com.kelvaya"
ThisBuild / scalaVersion    := "2.12.10"
ThisBuild / autoAPIMappings := true

lazy val util = (project in file("util"))

lazy val ecobee = (project in file("ecobee-lib")).dependsOn(util)

// NB: Kludge to get subprojects to compile without creating the empty root JAR file
Keys.`package` := file("")
packageBin in Global := file("")
packagedArtifacts := Map()


// Most comments came from https://tpolecat.github.io/2017/04/25/scalac-flags.html
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Needed by Cats for some of the type checking
  "-Ywarn-unused-import",
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard",
  "-Xlint:_",
  "-unchecked"
)
