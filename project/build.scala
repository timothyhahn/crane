
/** sbt imports **/
import sbt._
import Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import sbt.Project.Initialize

object CraneBuild extends Build {

  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    organization := "net.timothyhahn",
    version := "0.1.0",
    scalaVersion := Dependency.V.Scala,
    EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.Unmanaged, EclipseCreateSrc.Source, EclipseCreateSrc.Resource),
    EclipseKeys.withSource := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://timothyhahn.github.io/crane/</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://www.opensource.org/licenses/bsd-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:timothyhahn/crane.git</url>
        <connection>scm:git:git@github.com:timothyhahn/crane.git</connection>
      </scm>
      <developers>
        <developer>
          <id>timothyhahn</id>
          <name>Timothy Hahn</name>
          <url>http://timothyhahn.net</url>
        </developer>
      </developers>
    )
  )

  lazy val compileJdk7Settings = Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-optimize", "-feature", "-language:postfixOps", "-target:jvm-1.7"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")
  )

  lazy val root = Project(id ="root",
                          base = file("."),
                          settings = defaultSettings ++ Unidoc.settings ++ Seq(
                            Unidoc.unidocExclude := Seq(examples.id),
                            publishArtifact := false,
                            mainClass in (Compile, run) := Some("crane.examples.Example")
                          )) aggregate(crane) dependsOn(examples)

  lazy val crane = Project(id = "crane",
                           base = file("crane"),
                           settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                             scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/root-doc.html"),
                             libraryDependencies ++= Dependencies.crane))

  lazy val examples = Project(id = "crane-examples",
                           base = file("crane-examples"),
                           settings = defaultSettings ++ compileJdk7Settings ++ Seq(
                             publishArtifact := false,
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
