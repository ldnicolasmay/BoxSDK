package boxsdkhelpers

import com.box.sdk.BoxFolder
import com.box.sdk.BoxItem
import BoxSDKHelpers._

object Main {
  def main(args: Array[String]): Unit = {

    println("This is a simple Scala app to test your Box JWT connection.\n")

    // val api = getBoxAuthenticatedClient(boxJsonConfigPath = args(0))
    val jsonConfigPath = "/Users/ldmay/PycharmProjects/BoxPythonSDK/81663_ldlfw6r6_config.json"
    val api = getBoxAuthenticatedClient(boxJsonConfigPath = jsonConfigPath)

    printServiceAccountInformation(api)

    //  val folder = new BoxFolder(api, args(1))
    val boxFolderID = "87129413447"
    val folder = new BoxFolder(api, boxFolderID)

    val subItems = getSubItems(api, folder)
    subItems.foreach((item: BoxItem#Info) => println(s"`${item.getName}` is a ${item.getType} with ID '${item.getID}'"))
    println()

    printFolderInfo(api, folder); println()

    def printBoxFileInfo(item: BoxItem#Info): Unit = {
      println(s"File `${item.getName}` with ID ${item.getID} is ${item.getSize} bytes")
    }
    walkFolderTreeRec(api, folder, printBoxFileInfo); println()

  }
}
