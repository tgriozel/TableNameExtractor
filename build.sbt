ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "TableNameExtractor"
  )

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % "0.23.30",
  "org.http4s" %% "http4s-circe" % "0.23.30",
  "org.http4s" %% "http4s-blaze-server" % "0.23.17",
  "io.circe" %% "circe-generic" % "0.14.10",
  "org.typelevel" %% "cats-effect" % "3.5.7",
  "com.github.jsqlparser" % "jsqlparser" % "5.1",
  "org.typelevel" %% "munit-cats-effect" % "2.0.0" % "test"
)
