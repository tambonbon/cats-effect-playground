package tutorials.copying

import cats.effect.{IO, Resource}
import cats.syntax.all._
import java.io._

object CopyFiles {

  // resources are streams

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO.blocking(new FileInputStream(f)) // build
    } { inStream =>
      IO.blocking(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO.blocking(new FileOutputStream(f)) // build
    } { outStream =>
      IO.blocking(outStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def inputOutputStreams(
      in: File,
      out: File
  ): Resource[IO, (InputStream, OutputStream)] =
    for { // `Resources` instances can bbe combined in for-expr
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  class CopyWithBracket {
    import cats.syntax.all._

    def copy(origin: File, destination: File): IO[Long] = {
      val inIO: IO[InputStream]   = IO(new FileInputStream(origin))
      val outIO: IO[OutputStream] = IO(new FileOutputStream(destination))

      (inIO, outIO) // Stage 1: Getting resources
        .tupled     // From (IO[InputStream], IO[OutputStream]) to IO[(InputStream, OutputStream)]
        .bracket { case (in, out) =>
          transfer(
            in,
            out
          ) // Stage 2: Using resources (for copying data, in this case)
        } { case (in, out) => // Stage 3: Freeing resources
          (
            IO(in.close()),
            IO(out.close())
          ).tupled // From (IO[Unit], IO[Unit]) to IO[(Unit, Unit)]
            .handleErrorWith(_ => IO.unit)
            .void
        }
    }
  }

  def transmit(
      origin: InputStream,
      destination: OutputStream,
      buffer: Array[Byte],
      acc: Long
  ): IO[Long] =
    for {
      amount <- IO.blocking(
        origin.read(buffer, 0, buffer.size)
      ) // both input&outpout actions are created by invoking IO.bloking
      // .. which returns actions encapsulated in a suspended IO
      // ** when dealing with input/output --> IO.blocking(action) ratehr than IO(action)
      count <-
        if (amount > -1)
          IO.blocking(destination.write(buffer, 0, amount)) >> transmit(
            origin,
            destination,
            buffer,
            acc + amount
          )
        else
          IO.pure(
            acc
          ) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count // Returns the actual amount of bytes transmitted // Returns the actual amount of bytes transmitted

  // transfer will do the real work
  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    transmit(origin, destination, new Array[Byte](1024 * 10), 0L)

  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(origin, destination).use { case (in, out) =>
      transfer(in, out)
    } // copy works, but we need to take CANCELATION into account
  // but we have `Resource` that makes cancelation an easy task

  class PolymorphicCopy {

    /** `IO` is able to suspend side-effects async thanks to `Async[IO]` Because `Async` extend
      * `Sync`, `IO` can also suspend side-effects synch
      */
    // We could have coded its function in terms of some `F[_]: Sync` & `F[_]: Async` instead of IO
    import cats.effect.Sync

    def transmit[F[_]: Sync](
        origin: InputStream,
        destination: OutputStream,
        buffer: Array[Byte],
        acc: Long
    ): F[Long] =
      for {
        amount <- Sync[F].blocking(origin.read(buffer, 0, buffer.length))
        count <-
          if (amount > -1)
            Sync[F].blocking(destination.write(buffer, 0, amount)) >> transmit(
              origin,
              destination,
              buffer,
              acc + amount
            )
          else
            Sync[F].pure(
              acc
            ) // End of read stream reached (by java.io.InputStream contract), nothing to write
      } yield count // Returns the actual amount of bytes transmitted

    def transfer[F[_]: Sync](origin: InputStream, destination: OutputStream): F[Long] =
      transmit(origin, destination, new Array[Byte](1024 * 10), 0L)

    def inputStream[F[_]: Sync](f: File): Resource[F, FileInputStream] =
      Resource.make {
        Sync[F].blocking(new FileInputStream(f))
      } { inStream =>
        Sync[F].blocking(inStream.close()).handleErrorWith(_ => Sync[F].unit)
      }

    def outputStream[F[_]: Sync](f: File): Resource[F, FileOutputStream] =
      Resource.make {
        Sync[F].blocking(new FileOutputStream(f))
      } { outStream =>
        Sync[F].blocking(outStream.close()).handleErrorWith(_ => Sync[F].unit)
      }

    def inputOutputStreams[F[_]: Sync](
        in: File,
        out: File
    ): Resource[F, (InputStream, OutputStream)] =
      for {
        inStream  <- inputStream(in)
        outStream <- outputStream(out)
      } yield (inStream, outStream)

    def copy[F[_]: Sync](origin: File, destination: File): F[Long] =
      inputOutputStreams(origin, destination).use { case (in, out) =>
        transfer(in, out)
      }
  }
}
