package tutorials.copying

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import java.io.File
import cats.effect.std

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <-
        if (args.length < 2)
          IO.raiseError(new IllegalArgumentException("Need origin and destination files"))
        else IO.unit
      orig = new File(args(0))
      dest = new File(args(1))
      _ <-
        if (dest.exists()) for {
          _       <- std.Console[IO].println("Target file exists. Please confirm to copy (y/N)")
          console <- std.Console[IO].readLine
          _ <-
            if (console.equals("y")) for {
              count <- CopyFiles.copy(orig, dest)
              _     <- IO.println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}")
            } yield count
            else IO.unit
        } yield ExitCode.Success
        else
          for {
            count <- CopyFiles.copy(orig, dest)
            _     <- IO.println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}")
          } yield ExitCode.Success

      // count <- CopyFiles.copy(orig, dest)
      // _     <- IO.println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}")
    } yield ExitCode.Success
}

object MainPoly extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <-
        if (args.length < 2)
          IO.raiseError(new IllegalArgumentException("Need origin and destination files"))
        else IO.unit
      origin = new File(args.head)
      dest   = new File(args.tail.head)
      count <- PolymorphicCopy.copy[IO](origin, dest) // Note [IO] here
      _ <- IO.println(s"$count bytes copied from ${origin.getPath} to ${dest.getPath}")
    } yield ExitCode.Success
}
