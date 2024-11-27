package jp.osdn.gokigen.gokigenassets.utils

import android.content.Intent
import android.net.Uri
import jp.osdn.gokigen.gokigenassets.utils.imagefile.IImageStoreGrant

interface IScopedStorageAccessPermission
{
    fun requestStorageAccessFrameworkLocation()
    fun responseStorageAccessFrameworkLocation(resultCode: Int, data: Intent?)

    fun requestAccessPermission(requestUri : Uri, grantResponse : IImageStoreGrant)
    fun responseAccessPermission(resultCode: Int, data: Intent?)
}
