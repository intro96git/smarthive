ThisBuild / version         := "1.0.0-SNAPSHOT"
ThisBuild / organization    := "com.kelvaya"
ThisBuild / scalaVersion    := "2.12.7"


lazy val util = (project in file("util"))

lazy val ecobee = (project in file("ecobee-lib")).dependsOn(util)

// NB: Kludge to get subprojects to compile without creating the empty root JAR file
Keys.`package` := file("")
packageBin in Global := file("")
packagedArtifacts := Map()
