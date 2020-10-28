package com.example.drawapp.model

import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.drawapp.R
import com.example.drawapp.base.Item

data class PaintState(val shape: SHAPE, val size: SIZE, val color: COLOR)

enum class SHAPE(
    val value: Paint.Cap
) {
    CIRCLE(Paint.Cap.ROUND),
    SQUARE(Paint.Cap.SQUARE);

    companion object {
        private val map = values().associateBy(SHAPE::value)
        fun from(value: Paint.Cap) = map[value] ?: CIRCLE
    }
}

enum class COLOR(
    @ColorRes
    val value: Int
) {
    BLACK(R.color.colorPaintBlack),
    WHITE(R.color.colorPaintWhite),
    RED(R.color.colorPaintRed),
    ORANGE(R.color.colorPaintOrange),
    YELLOW(R.color.colorPaintYellow),
    GREEN(R.color.colorPaintGreen),
    BLUE(R.color.colorPaintBlue),
    PURPLE(R.color.colorPaintPurple);

    companion object {
        private val map = values().associateBy(COLOR::value)
        fun from(color: Int) = map[color] ?: BLACK
    }
}

enum class SIZE(
    val value: Int
) {
    TINY(4),
    SMALL(8),
    MEDIUM(16),
    LARGE(32),
    HUGE(48),
    SURER_HUGE(64);

    companion object {
        private val map = values().associateBy(SIZE::value)
        fun from(size: Int) = map[size] ?: SMALL
    }
}

enum class TOOL(
    @DrawableRes
    val value: Int
): Item {
    PALETTE(R.drawable.ic_color_24),
    SHAPE(R.drawable.ic_baseline_brush_24),
    SIZE(R.drawable.ic_size_24)
}

