package com.example.drawapp

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.example.drawapp.base.BaseViewModel
import com.example.drawapp.base.Event
import com.example.drawapp.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ViewModel(private val contentResolver: ContentResolver) : BaseViewModel<ViewState>() {

    override fun initialViewState(): ViewState = ViewState(
        toolsList = TOOL.values().toList(),
        shapeList = enumValues<SHAPE>().map { ToolItem.ShapeModel(it.value) },
        colorList = enumValues<COLOR>().map { ToolItem.ColorModel(it.value) },
        sizeList = enumValues<SIZE>().map { ToolItem.SizeModel(it.value) },
        paintState = PaintState(SHAPE.CIRCLE, SIZE.SMALL, COLOR.BLACK),
        selectedTool = null
    )

    override fun reduce(event: Event, previousState: ViewState): ViewState? {
        return when (event) {
            is UiEvent.OnToolbarClicked -> {
                previousState.copy(
                    selectedTool = previousState.toolsList[event.index]
                )
            }
            is UiEvent.OnColorClick -> {
                previousState.copy(
                    paintState = previousState.paintState.copy(
                        color = COLOR.from(previousState.colorList[event.index].color),
                    )
                )
            }
            is UiEvent.OnSizeClick -> {
                previousState.copy(
                    paintState = previousState.paintState.copy(
                        size = SIZE.from(previousState.sizeList[event.index].size),
                    )
                )
            }
            is UiEvent.OnShapeClick -> {
                previousState.copy(
                    paintState = previousState.paintState.copy(
                        shape = SHAPE.from(previousState.shapeList[event.index].shape),
                    )
                )
            }
            UiEvent.OnClearClick -> {
                return previousState.copy()
            }
            UiEvent.OnSaveClick -> {
                return previousState.copy()
            }
            else -> null
        }
    }


}