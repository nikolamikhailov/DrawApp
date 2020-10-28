package com.example.drawapp

import android.content.ContentResolver
import android.graphics.Bitmap
import com.example.drawapp.base.Event
import com.example.drawapp.model.PaintState
import com.example.drawapp.model.TOOL
import com.example.drawapp.model.ToolItem

data class ViewState(
    val toolsList: List<TOOL>,
    val shapeList: List<ToolItem.ShapeModel>,
    val colorList: List<ToolItem.ColorModel>,
    val sizeList: List<ToolItem.SizeModel>,
    val paintState: PaintState,
    val selectedTool: TOOL?
)

sealed class UiEvent() : Event{
    object OnClearClick : UiEvent()
    object OnSaveClick : UiEvent()
    data class OnColorClick(val index: Int) : UiEvent()
    data class OnSizeClick(val index: Int) : UiEvent()
    data class OnShapeClick(val index: Int) : UiEvent()
    data class OnToolbarClicked(val index: Int): UiEvent()
}
