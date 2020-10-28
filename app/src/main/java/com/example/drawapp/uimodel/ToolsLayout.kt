package com.example.drawapp.uimodel

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawapp.base.Item
import com.example.drawapp.colorAdapterDelegate
import com.example.drawapp.shapeAdapterDelegate
import com.example.drawapp.sizeChangeAdapterDelegate
import com.example.drawapp.toolsAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import kotlinx.android.synthetic.main.view_tools.view.*

class ToolsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var onClick: (Int) -> Unit = {}

    private val adapterDelegate = ListDelegationAdapter(
        colorAdapterDelegate {
            onClick(it)
        },
        sizeChangeAdapterDelegate {
            onClick(it)
        },
        shapeAdapterDelegate {
            onClick(it)
        },
        toolsAdapterDelegate {
            onClick(it)
        }
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        toolsList.adapter = adapterDelegate
        toolsList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    fun render(list: List<Item>) {
        adapterDelegate.items = list
        adapterDelegate.notifyDataSetChanged()
    }

    fun setOnClickListener(onClick: (Int) -> Unit) {
        this.onClick = onClick
    }
}