ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "individual"


lazy val root = (project in file("."))
	.settings(
		name := "root",
		libraryDependencies ++= Seq(
			"org.chipsalliance" %% "chisel" % "7.0.0-RC1",
			"org.scalatest" %% "scalatest" % "3.2.19" % "test",
		),
		scalacOptions ++= Seq(
			"-language:reflectiveCalls",
			"-deprecation",
			"-feature",
			"Xcheckinit",
			"-Ymacro-annotations",
		),
		addCompilerPlugin("org.chipsalliance" %% "chisel-plugin" % "7.0.0-RC1" cross CrossVersion.full),
	)