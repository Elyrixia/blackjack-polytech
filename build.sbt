lazy val javaVersion = "15.0.0"

lazy val commonSettings = Seq(
  organization := "fr.polytech",
  scalaVersion := "2.13.3",
  cancelable in Global := true,
  initialize := {
    initialize.value
    val actualMajorVersion = sys.props("java.specification.version")
    javaVersion.split('.').headOption match {
      case Some(expectedMajorVersion) =>
        assert(
          actualMajorVersion == expectedMajorVersion,
          s"unsupported JDK$actualMajorVersion detected, please use JDK$expectedMajorVersion instead"
        )
      case None =>
        throw new Exception(
          "no correct java.version was defined inside build.properties for the project"
        )
    }
  }
)

lazy val root = (project in file("."))
  .settings(name := "blackjack-polytech")
  .settings(commonSettings)
  .settings(addCommandAlias("run", "core/run"))
  .aggregate(core)

lazy val core = (project in file("core"))
  .settings(name := "blackjack-polytech")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.beachape" %% "enumeratum" % "1.6.0",
      "org.scalactic" %% "scalactic" % "3.2.0" % "test",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test"
    )
  )
