import java.io.{File, PrintWriter}

import scala.io.Source

object LogParserIter {
  def main(args: Array[String]): Unit = {
    val path = args(0)
    val name = args(1)
    val lines = Source.fromFile(path).getLines().toArray
    val patten =
      """\d+\.\d+""".r

    val iterationP = """\[Epoch (\d+) .*\]\[Iteration (\d+)\]""".r("epoch", "iter")

    val pw = new PrintWriter(new File(s"${ name }-train.parse.iter"))
    pw.write("NumIters,epoch,LearningRate,mbox_loss,date\n")

    lines.filter(_.contains("Loss")).foreach(line => {
      val matches = patten.findAllMatchIn(line).toArray
      val date = line.substring(0, line.indexOf(" INFO"))
      val loss = matches(3).group(0)
      val lr = matches(4).group(0)
      val m = iterationP.findFirstMatchIn(line).get
      val epoch = m.group("epoch").toInt
      val iter = m.group("iter").toInt
      pw.write(iter + "," + epoch + "," + lr + "," + loss + "," + date + "\n")
    })
    pw.close()

    val ap = """Mean AP = (\d+\.\d+)""".r("ap")
    val pw2 = new PrintWriter(new File(s"${ name }-test.parse.iter"))
    pw2.write("NumIters,detection_eval\n")
    val text = lines.mkString("\n")
    ap.findAllMatchIn(text).foreach(m => {
      val map = m.group("ap").toFloat
      val iter = iterationP.findFirstMatchIn(text.substring(m.end)).get.group("iter").toInt
      pw2.write(iter + "," + map + "\n")
      print(iter + "," + map + "\n")
    })
    pw2.close()
  }
}
