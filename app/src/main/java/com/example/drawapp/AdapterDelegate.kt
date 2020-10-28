package com.example.drawapp

import android.graphics.PorterDuff
import com.example.drawapp.base.Item
import com.example.drawapp.model.MainToolItem
import com.example.drawapp.model.SHAPE
import com.example.drawapp.model.TOOL
import com.example.drawapp.model.ToolItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_color.view.*
import kotlinx.android.synthetic.main.item_shape.view.*
import kotlinx.android.synthetic.main.item_size.view.*
import kotlinx.android.synthetic.main.item_tool.view.*

fun toolsAdapterDelegate(onToolClick: (Int) -> Unit): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<MainToolItem, Item>(
        R.layout.item_tool
    ) {
        bind { list ->
            itemView.ivTool.let {
                it.setImageResource(
                    when (item.tool) {
                        TOOL.PALETTE -> R.drawable.ic_color_24
                        TOOL.SHAPE -> R.drawable.ic_baseline_brush_24
                        TOOL.SIZE -> R.drawable.ic_circle_24
                    }
                )
                if (item.tool == TOOL.PALETTE) {
                    it.setColorFilter(
                        context.resources.getColor(item.paintsState.color.value),
                        PorterDuff.Mode.SRC_IN
                    )
                }
                else {
                    it.setColorFilter(
                        context.resources.getColor(
                            if (item.isSelected) R.color.colorAccent
                            else android.R.color.black
                        ),
                        PorterDuff.Mode.SRC_IN
                    )
                }

            }
            itemView.setOnClickListener {
                onToolClick(adapterPosition)
            }
        }

    }

fun colorAdapterDelegate(onClick: (Int) -> Unit): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<ToolItem.ColorModel, Item>(
        R.layout.item_color
    ) {
        bind { list ->
            itemView.color.setColorFilter(
                context.resources.getColor(item.color),
                PorterDuff.Mode.SRC_IN
            )
            itemView.setOnClickListener { onClick(adapterPosition) }
        }
    }

fun sizeChangeAdapterDelegate(onSizeClick: (Int) -> Unit): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<ToolItem.SizeModel, Item>(
        R.layout.item_size
    ) {
        bind { list ->
            itemView.tvToolsSize.text = item.size.toString()
            itemView.setOnClickListener {
                onSizeClick(adapterPosition)
            }
        }
    }

fun shapeAdapterDelegate(onShapeClick: (Int) -> Unit): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<ToolItem.ShapeModel, Item>(
        R.layout.item_shape
    ) {
        bind { list ->
            itemView.ivShape.setImageResource(
                when (item.shape) {
                    SHAPE.CIRCLE.value -> R.drawable.ic_circle_24
                    SHAPE.SQUARE.value -> R.drawable.ic_square_24
                    else -> error("Wrong shape")
                }
            )
            itemView.setOnClickListener {
                onShapeClick(adapterPosition)
            }
        }
    }
