import java.io.{PrintWriter, File}

import scala.concurrent.{Await, Future}

import scala.concurrent.duration._

/**
  * Created by pnagarjuna on 06/11/15.
  */
object Main {
  def main(args: Array[String]): Unit = {
    println(s"Version Make Model")

    val years = (2004 to 2015).toList
    val filename = args(0)

    if (args.length != 1) {
      println(s"Provide filename as the commandline argument")
      sys.exit
    }

    store(getPath(filename), getPath(s"$filename-errors")) { (writer, errors) =>
      years.map { year =>
        val f = getMakesList(year).flatMap { makes =>
          getModelsList(year, makes)
        }.map { eitherList =>
          eitherList.flatMap { either =>
            either match {
              case Right(models) => models
              case Left(fails) =>  List.empty[Atomic]
            }
          }
        }.flatMap { models =>
          getVersionsList(year, models)
        }.flatMap { versions =>
          Future.successful {
            writer.println(s"$year    ${make.}")
          }
        }

        Await.result(f, 100 minutes)
      }
    }
  }

  def getPath(filename: String): File = new File(s"${sys.props.get("user.name")}/$filename.csv")

  def store(dataFile: File, errorsFile: File)(f: (PrintWriter, PrintWriter) => Unit): Unit = {
    val writer = new PrintWriter(dataFile)
    val errors = new PrintWriter(errorsFile)
    f(writer, errors)
    writer.flush()
    errors.flush()
    errors.close()
    writer.close()
  }

  def getMakesList(year: Int): Future[List[Atomic]] = {
    Utils.getMakes(year).flatMap { res =>
      Utils.parse(res.body.toString)
    }
  }

  def getModelsList(year: Int, makes: List[Atomic]): Future[List[Either[(Int, Atomic), List[Atomic]]]] = {
    Future.sequence {
      makes.map { make =>
      val f = Utils.getModels(year, make.Value).flatMap { res =>
        Utils.parse(res.body.toString)
      }.map { models => Right(models) }
      f.recover { case th => Left((year, make)) }
    }}
  }

  def getVersionsList(year: Int, models: List[Atomic]): Future[List[Either[(Int, Atomic), List[Atomic]]]] = {
    Future.sequence {
      models.map { model =>
        val f = Utils.getVersions(year, model.Value).flatMap { res =>
          Utils.parse(res.body.toString)
        }.map { versions => Right(versions) }
        f.recover { case th => Left((year, model)) }
      }
    }
  }

}
