package org.scalafmt

import java.io.File
import java.nio.file.Files

import org.scalafmt.cli.Cli
import org.scalafmt.util.DiffAssertions
import org.scalatest.FunSuite

import scala.concurrent.duration.Duration

class CliTest extends FunSuite with DiffAssertions {
  test("cli parses args") {
    val expected = Cli.Config(Some(new File("foo")), inPlace = true)
    val args = Seq("--file", "foo", "-i")
    val init = Cli.Config(None, inPlace = false)
    val obtained = Cli.parser.parse(args, init)
    assert(obtained.contains(expected))
  }

  test("scalafmt -i --file tmpFile") {
    val tmpFile = Files.createTempFile("prefix", "suffix")
    val unformatted =
      """
        |object a    extends   App {
        |println("hello world!")
        |}
      """.stripMargin
    val expected =
      """object a extends App {
        |  println("hello world!")
        |}
      """.stripMargin
    Files.write(tmpFile, unformatted.getBytes)
    val formatInPlace = Cli.Config(Some(tmpFile.toFile),
      inPlace = true, maxDuration = Duration(10, "s"))
    Cli.run(formatInPlace)
    val obtained = new String(Files.readAllBytes(tmpFile))
    assertNoDiff(obtained, expected)
  }

}