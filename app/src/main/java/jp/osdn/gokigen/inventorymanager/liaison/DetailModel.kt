package jp.osdn.gokigen.inventorymanager.liaison

import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.storage.DataContent

class DetailModel(val id: Long)
{
    private var isRefreshing = false
    private var dataContent: DataContent? = null
    init
    {
        update()
    }

    fun getData() : DataContent?
    {
        return (dataContent)
    }

    private fun update()
    {
        try
        {
            val thread = Thread {
                if (!isRefreshing)
                {
                    isRefreshing = true
                    val storageDao = AppSingleton.db.storageDao()
                    dataContent = storageDao.findById(id)
                    isRefreshing = false
                }
            }
            thread.start()
            try
            {
                thread.join()
            }
            catch (ee: Exception)
            {
                ee.printStackTrace()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun deleteContent()
    {
        try
        {
            val thread = Thread {
                val storageDao = AppSingleton.db.storageDao()
                val content = storageDao.findById(id)
                if (content != null)
                {
                    storageDao.delete(content)
                }
                dataContent = null
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
