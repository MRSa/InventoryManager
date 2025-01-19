package jp.osdn.gokigen.inventorymanager.recognize

data class UpdateRecordInformation(val id: Long, val title: String, val subTitle: String, val author: String, val publisher: String, val category: String, val description: String)
data class RecognizedData(val isHit: Boolean, val title: String, val author: String, val publisher: String, val appendData: String)

enum class RecognizeDataProgress { READY, CHECK_ISBN, POSTPROCESS_ISBN }

interface IRecognizedDataCallback
{
    fun recognizedData(data: UpdateRecordInformation, isOverwrite: Boolean)
    fun finishRecognizedData(needUpdate: Boolean)
}

interface IRecognizeDataFromInternetCallback
{
    fun startRecognizeFromInternet()
    fun progressRecognizeFromInternet(status: RecognizeDataProgress, count: Int, totalCount: Int)
    fun finishRecognizeFromInternet()
}
