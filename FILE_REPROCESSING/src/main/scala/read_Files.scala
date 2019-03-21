import java.io.File

class read_Files {
  val path = "/home/gawshalini/Documents/HSS/HSS_FILE_REPROCESSING/src/main/resources/"
  val files = new File(path).list.toList // gives list of file names including extensions in the path `path`

//  println(files)

}
