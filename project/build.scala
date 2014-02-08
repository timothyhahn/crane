
/** sbt imports **/
import sbt._
import Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import sbt.Project.Initialize

object CraneBuild extends Build {

  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    organization := "net.timothyhahn.crane",
    version := "0.0.1",
    scalaVersion := Dependency.V.Scala,
    EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.Unmanaged, EclipseCreateSrc.Source, EclipseCreateSrc.Resource),
    EclipseKeys.withSource := true
  )

  lazy val compileJdk7Settings = Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-optimize", "-feature", "-language:postfixOps", "-target:jvm-1.7"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")
  )

  lazy val root = Project(id ="root",
                          base = file("."),
                          settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                            mainClass in (Compile, run) := Some("crane.examples.Example")
                          )) dependsOn(crane, examples)

  lazy val crane = Project(id = "crane",
                           base = file("crane"),
                           settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                            libraryDependencies ++= Dependencies.crane))

  lazy val examples = Project(id = "crane-examples",
                           base = file("crane-examples"),
                           settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                            libraryDependencies ++= Dependencies.examples)) dependsOn(crane)

}

object Dependencies {
  import Dependency._
  val crane = Seq(
    Dependency.akkaActor, Dependency.akkaTransactor, Dependency.scalaSTM,
    Dependency.scalatest
  )
  val examples = Seq(
    Dependency.scalatime
  )
}

object Dependency {
  object V {
    val Scala       = "2.10.3"
    val Akka        = "2.2.3"
  }

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.Akka
  val akkaTransactor = "com.typesafe.akka" %% "akka-transactor" % V.Akka
  val scalaSTM = "org.scala-stm" %% "scala-stm" % "0.7"
  val scalatime = "com.github.nscala-time" %% "nscala-time" % "0.8.0"
  val scalatest = "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
}
