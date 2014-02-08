
/** sbt imports **/
import sbt._
import Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import sbt.Project.Initialize

object CraneBuild extends Build {

  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    organization := "crane",
    version := "0.0.1",
    scalaVersion := Dependency.V.Scala,
    EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.Unmanaged, EclipseCreateSrc.Source, EclipseCreateSrc.Resource),
    EclipseKeys.withSource := true
  )

  lazy val compileJdk7Settings = Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-optimize", "-feature", "-language:postfixOps", "-target:jvm-1.7"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")
  )

  lazy val root = Project(id ="crane",
                          base = file("."),
                          settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                            libraryDependencies ++= Dependencies.root
                          ))
}

object Dependencies {
  import Dependency._
  val root = Seq(
    Dependency.akkaActor, Dependency.scalaSTM,
    Dependency.scalatest
  )
}

object Dependency {
  object V {
    val Scala       = "2.10.3"
    val Akka        = "2.2.3"
  }

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.Akka
  val scalaSTM = "org.scala-stm" %% "scala-stm" % "0.7"
  val scalatest = "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
}
