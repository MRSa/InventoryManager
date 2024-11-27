package jp.osdn.gokigen.gokigenassets.liveview.gridframe

class GridFrameFactory
{
    fun getGridFrameDrawer(id: Int): IGridFrameDrawer
    {
        return (GridFrameDrawerDefault())
    }
}
