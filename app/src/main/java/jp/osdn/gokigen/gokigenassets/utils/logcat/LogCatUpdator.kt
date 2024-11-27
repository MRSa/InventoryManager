package jp.osdn.gokigen.gokigenassets.utils.logcat

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class LogCatUpdater
{
    /**
     *
     * @param ringbuffer : main - メイン ログバッファ, radio - 無線通信や電話に関連するメッセージが含まれるバッファ, events - イベントに関連するメッセージが含まれるバッファ
     * @param logFormat : brief - 優先度 / タグとメッセージ発行元プロセスの PID, process - PID のみ, tag - 優先度 / タグのみ, raw - 生のログ, time - 日付、起動時刻、優先度 / タグ、メッセージ発行元プロセスの PID , threadtime - 日付、起動時刻、優先度、タグ、メッセージ発行元スレッドの PID および TID, long - すべてのメタデータ フィールド
     * @param filterSpec　:  レベル : SFEWIDV
     * @param filterString : 指定した文字列がログに含まれている場合に表示
     * @param filterRegEx :  指定した正規表現の文字列がログに含まれている場合に表示
     * @return ログのリスト
     */
    fun getLogCat(ringbuffer: String, logFormat: String, filterSpec: String, filterString: String, filterRegEx: Regex): List<String>
    {
        val bufferSize = 8192
        val listItems = ArrayList<String>()
        try {
            val commandLine = ArrayList<String>()
            commandLine.add("logcat")
            commandLine.add("-d") //  -d:  dump the log and then exit (don't block)
            commandLine.add("-b") //  -b <buffer> : request alternate ring buffer ('main' (default), 'radio', 'events')
            commandLine.add(ringbuffer) //     <buffer> option.
            commandLine.add("-v") //  -v <format> :  Sets the log print format, where <format> is one of:
            commandLine.add(logFormat) //                 brief process tag thread raw time threadtime long
            commandLine.add(filterSpec) //
            val process = Runtime.getRuntime().exec(commandLine.toTypedArray())
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream), bufferSize)
            //var line: String?
            do
            {
                val line = bufferedReader.readLine()
                try
                {
                    val filterLength = filterString.length
                    if ((filterLength == 0) || ((filterLength > 0)&&(line.contains(filterString))) || (line.matches(filterRegEx)))
                    {
                        listItems.add(line)
                    }
                }
                catch (ee: Exception)
                {
                    ee.printStackTrace()
                }
            } while (line != null)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (listItems)
    }
}
