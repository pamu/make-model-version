import java.io.{PrintWriter, File}

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

    store(getPath(filename)) { writer =>

    }
  }

  def getPath(filename: String): File = new File(s"${sys.props.get("user.name")}/$filename.csv")

  def store(file: File)(f: PrintWriter => Unit): Unit = {
    val writer = new PrintWriter(file)
    f(writer)
    writer.flush()
    writer.close()
  }

  case class Row()

  def process(year: List[Int]): Unit = {

  }

}
