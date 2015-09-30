lazy val root = (project in file(".")).
  settings(
    name := "koffio-hll",
    version := "0.0.1",
    scalaVersion := "2.11.7"
  )

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.twitter"  %%  "algebird-core"   % "0.11.0",
  "com.github.prasanthj" % "hyperloglog" % "cec4bb8980",
  "com.clearspring.analytics" % "stream" % "2.7.0",
  "net.agkn" % "hll" % "1.6.0"
)