ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.3"

lazy val root = (project in file("."))
  .settings(
    name := "TableNameExtractor"
  )

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % "0.23.33",
  "org.http4s" %% "http4s-circe" % "0.23.33",
  "org.http4s" %% "http4s-blaze-server" % "0.23.17",
  "io.circe" %% "circe-generic" % "0.14.15",
  "org.typelevel" %% "cats-effect" % "3.6.3",
  "com.github.jsqlparser" % "jsqlparser" % "5.3",
  "org.typelevel" %% "munit-cats-effect" % "2.1.0" % "test"
)
