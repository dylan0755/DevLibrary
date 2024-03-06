package com.dylan.mylibrary.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dylan.mylibrary.R
import com.dylan.mylibrary.bean.Customer
import com.dylan.mylibrary.util.TextDrawableUtils
import dev.bandb.graphview.graph.Graph
import dev.bandb.graphview.AbstractGraphAdapter
import dev.bandb.graphview.decoration.edge.ArrowEdgeDecoration
import dev.bandb.graphview.layouts.energy.FruchtermanReingoldLayoutManager


class GraphUtils {
     var adapter: AbstractGraphAdapter<NodeViewHolder>? =null
    fun setupGraphView(recycler:RecyclerView,graph:Graph) {
        recycler.layoutManager = FruchtermanReingoldLayoutManager(recycler.context, 1000)

        // 2. Attach item decorations to draw edges
        recycler.addItemDecoration(ArrowEdgeDecoration())
        adapter = object : AbstractGraphAdapter<NodeViewHolder>() {

            // 4.2 ViewHolder should extend from `RecyclerView.ViewHolder`
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.node, parent, false)
                return NodeViewHolder(view)
            }

            override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
                var node=getNode(position)
                node?.data.apply {
                    var customer:Customer=this as Customer
                    var textDrawable= TextDrawableUtils.getTextDrawableInRelationShipGraph(recycler.context,customer.name)
                    holder.cvAvatar.setImageDrawable(textDrawable)
                }
            }
        }.apply {
            // 4.3 Submit the graph
            this.submitGraph(graph)
            recycler.adapter = this
        }
    }

    inner class NodeViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvAvatar: ImageView = itemView.findViewById(R.id.cvAvatar)

        init {
            itemView.setOnClickListener {
               var currentNode = adapter?.getNode(bindingAdapterPosition)

            }
        }
    }
}