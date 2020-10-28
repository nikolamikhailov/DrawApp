package com.example.drawapp.model

import android.graphics.Paint
import androidx.annotation.ColorRes
import com.example.drawapp.base.Item

sealed class ToolItem : Item {
    data class ColorModel(@ColorRes val color: Int) : ToolItem()
    data class SizeModel(val size: Int) : ToolItem()
    data class ShapeModel(val shape: Paint.Cap) : ToolItem()
}

data class MainToolItem(
    val tool: TOOL,
    val isSelected: Boolean,
    val paintsState: PaintState
): Item



