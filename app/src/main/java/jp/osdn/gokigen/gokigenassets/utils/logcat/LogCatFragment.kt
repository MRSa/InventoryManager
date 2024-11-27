package jp.osdn.gokigen.gokigenassets.utils.logcat

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_LABEL_FINISHED_REFRESH
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_MENU_ACTION_REFRESH
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_MENU_LAYOUT_DEBUG_VIEW
import java.util.*

class LogCatFragment : androidx.fragment.app.ListFragment()
{
    private val updater : LogCatUpdater = LogCatUpdater()
    private var myView : View? = null
    private var adapter: ArrayAdapter<String>? = null
    private var myDataItems: List<String> = ArrayList()

    companion object
    {
        private val TAG = LogCatFragment::class.java.simpleName
        fun newInstance() = LogCatFragment().apply { }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View?
    {
        if (myView != null)
        {
            return (myView)
        }

        adapter = ArrayAdapter(inflater.context, android.R.layout.simple_list_item_1, myDataItems)
        listAdapter = adapter
        myView = super.onCreateView(inflater, container, savedInstanceState)!!
        return (myView)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "LogCatFragment::onActivityCreated()")
        setHasOptionsMenu(true)

        val activity: Activity? = activity
        if (activity != null)
        {
            listView.onItemLongClickListener = LogCatExporter(activity)
        }
    }

    override fun onResume()
    {
        super.onResume()

        update()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(ID_MENU_LAYOUT_DEBUG_VIEW, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean
    {
        val ret : Boolean
        when (item.itemId)
        {
            ID_MENU_ACTION_REFRESH -> ret = update()
            else -> ret = super.onOptionsItemSelected(item)
        }
        return (ret)
    }

    fun update() : Boolean
    {
        val thread = Thread {
            Log.v(TAG, "START LOGCAT")
            myDataItems = updater.getLogCat("main", "time", "*:v", "gokigen", Regex(""))
            Log.v(TAG, "FINISH LOGCAT")
            try {
                val activity = activity
                activity?.runOnUiThread {
                    try {
                        // 中身があったらクリアする
                        if (adapter!!.count > 0) {
                            adapter!!.clear()
                        }

                        // リストの内容を更新する
                        adapter!!.addAll(myDataItems)

                        // 最下部にカーソルを移したい
                        val view =
                            activity.findViewById<ListView>(android.R.id.list)
                        view.setSelection(myDataItems.size)

                        // 更新終了通知
                        Toast.makeText(
                            getActivity(),
                            getString(ID_LABEL_FINISHED_REFRESH),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (ee: Exception) {
                        ee.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try
        {
            // 本当は、ここでダイアログを出したい
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        return (true)
    }
}