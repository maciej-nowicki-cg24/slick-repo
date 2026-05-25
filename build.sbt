import sbt.Tests

ThisBuild / organization := "com.byteslounge"
ThisBuild / scalaVersion := "2.13.18"
ThisBuild / crossScalaVersions := Seq("2.13.18")

lazy val AllDbsTest: Configuration = config("alldbs").extend(Test)
lazy val SqlServerTest: Configuration = config("sqlserver").extend(Test)

lazy val dependencyResolvers = Seq(
  "Typesafe Maven Repository" at "https://repo.typesafe.com/typesafe/maven-releases/"
)

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.h2database" % "h2" % "2.2.220" % Test,
  "mysql" % "mysql-connector-java" % "5.1.38" % Test,
  "org.postgresql" % "postgresql" % "9.4.1211" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.21" % Test,
  "org.apache.derby" % "derby" % "10.11.1.1" % Test,
  "org.hsqldb" % "hsqldb" % "2.3.4" % Test,
  "joda-time" % "joda-time" % "2.9.6" % Test
)

lazy val dbPrefixes = Seq("MySQL", "Oracle", "Postgres", "Derby", "Hsql", "DB2")
lazy val sqlServerPrefix = Seq("SQLServer")

def testName(name: String): String = name.substring(name.lastIndexOf('.') + 1)

def allDbsFilter(name: String): Boolean = dbPrefixes.exists(p => testName(name).startsWith(p))

def sqlServerFilter(name: String): Boolean = sqlServerPrefix.exists(p => testName(name).startsWith(p))

def baseFilter(name: String): Boolean = !allDbsFilter(name) && !sqlServerFilter(name)

lazy val root = (project in file("."))
  .configs(AllDbsTest, SqlServerTest)
  .settings(inConfig(AllDbsTest)(Defaults.testTasks): _*)
  .settings(inConfig(SqlServerTest)(Defaults.testTasks): _*)
  .settings(
    name := "slick-repo",
    description := "CRUD Repositories for Slick based persistence Scala projects",
    version := "1.9.0",

    libraryDependencies ++= dependencies,
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.6.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1" % Test,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    ),

    resolvers ++= dependencyResolvers,

    Test / parallelExecution := false,

    Test / testOptions := Seq(Tests.Filter(baseFilter)),
    AllDbsTest / testOptions := Seq(Tests.Filter(allDbsFilter)),
    SqlServerTest / testOptions := Seq(Tests.Filter(sqlServerFilter)),

    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    Test / publishArtifact := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    pomExtra :=
      <url>https://github.com/gonmarques/slick-repo</url>
      <inceptionYear>2016</inceptionYear>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>https://opensource.org/licenses/MIT</url>
        </license>
      </licenses>
      <developers>
        <developer>
          <id>gonmarques</id>
          <name>Goncalo Marques</name>
          <url>https://github.com/gonmarques</url>
        </developer>
      </developers>
      <contributors>
        <contributor>
          <name>Claudio Diniz</name>
          <url>https://github.com/cdiniz</url>
        </contributor>
        <contributor>
          <name>Barnabas Olah</name>
          <url>https://github.com/stsatlantis</url>
        </contributor>
        <contributor>
          <name>Marco Costa</name>
          <url>https://github.com/mabrcosta</url>
        </contributor>
        <contributor>
          <name>David Poetzsch-Heffter</name>
          <url>https://github.com/dpoetzsch</url>
        </contributor>
        <contributor>
          <name>George</name>
          <url>https://github.com/giannoug</url>
        </contributor>
        <contributor>
          <name>Andy Czerwonka</name>
          <url>https://github.com/andyczerwonka</url>
        </contributor>
      </contributors>
      <scm>
        <url>https://github.com/gonmarques/slick-repo.git</url>
        <connection>scm:git:git://github.com/gonmarques/slick-repo.git</connection>
      </scm>
  )

lazy val itsetup = (project in file("src/docker/itsetup"))
  .settings(
    name := "itsetup"
  )

lazy val db2 = (project in file("src/docker/db2"))
  .settings(
    name := "db2"
  )
