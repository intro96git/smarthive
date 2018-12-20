ThisBuild / version         := "1.0.0-SNAPSHOT"
ThisBuild / organization    := "com.kelvaya"
ThisBuild / scalaVersion    := "2.12.7"


lazy val monads = (project in file("monad-lib"))

lazy val ecobee = (project in file("ecobee-lib")).dependsOn(monads)