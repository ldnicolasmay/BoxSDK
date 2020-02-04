package boxsdkhelpers

import java.io.FileReader

import scala.jdk.CollectionConverters._

import com.box.sdk.BoxDeveloperEditionAPIConnection
import com.box.sdk.BoxConfig
import com.box.sdk.BoxUser
import com.box.sdk.BoxFolder
import com.box.sdk.BoxItem


object BoxSDKHelpers {

  def getBoxAuthenticatedClient(boxJsonConfigPath: String): BoxDeveloperEditionAPIConnection = {
    val reader = new FileReader(boxJsonConfigPath)
    val config = BoxConfig.readFrom(reader)
    BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(config)
  }

  def printServiceAccountInformation(api: BoxDeveloperEditionAPIConnection): Unit = {
    val user = BoxUser.getCurrentUser(api)
    val userInfo = user.getInfo("login", "name")

    println("Authenticated as")
    println(s"Name: ${userInfo.getLogin}")
    println(s"Login: ${userInfo.getName}\n")
  }

  def getSubItems(api: BoxDeveloperEditionAPIConnection, folder: BoxFolder): Iterator[BoxItem#Info] = {
    folder.iterator().asScala
  }

  def printFolderInfo(api: BoxDeveloperEditionAPIConnection, folder: BoxFolder): Unit = {
    val folderInfo: BoxFolder#Info = folder.getInfo("id", "name", "size", "path_collection")
    val folderInfoPathCollection: List[BoxFolder#Info] = folderInfo.getPathCollection.asScala.toList
    val folderInfoPathCollectionList: List[String] =
      List("") ::: folderInfoPathCollection.map(folderInfo => folderInfo.getName) ::: List("")
    println(s"Folder `${folderInfo.getName}` at `${folderInfoPathCollectionList.mkString("/")}` " +
            s"with ID '${folderInfo.getID}' is ${folderInfo.getSize} bytes")
  }

  def getFolderFromID(api: BoxDeveloperEditionAPIConnection, folderId: String): BoxFolder = {
    new BoxFolder(api, folderId)
  }

  def walkFolderTreeRec(api: BoxDeveloperEditionAPIConnection,
                        folder: BoxFolder,
                        action: BoxItem#Info => Any): Any = {
    val subItems = getSubItems(api, folder).toList
    // Apply `action` to each file BoxItem#Info -- breadth first
    subItems.filter(item => item.getType == "file").foreach(item => action(item))
    // Recurse down through each BoxFolder
    subItems.filter(item => item.getType == "folder").foreach(item =>
      walkFolderTreeRec(api, getFolderFromID(api, item.getID), action)
    )
  }

}
