  inThisBuild(List(
    organization    := "com.kelvaya",
    scalaVersion    := "2.12.7"
  ))
  
  name := "monad-lib"
  
  libraryDependencies ++= Seq(
    "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
  )
  