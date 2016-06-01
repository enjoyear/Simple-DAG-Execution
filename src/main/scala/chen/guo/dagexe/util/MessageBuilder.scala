package chen.guo.dagexe.util

object MessageBuilder {

  def build(subject: String, body: String) =
    s"""
       |-----------------------------------------------------------
       |Subject:
       |$subject
       |Body:
       |$body
       |===========================================================
       |
       """.stripMargin
}
