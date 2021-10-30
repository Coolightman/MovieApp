package com.coolightman.movieapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Fact

class FactsAdapter() :
    RecyclerView.Adapter<FactsAdapter.FactViewHolder>() {

    private var facts = listOf<Fact>()

    class FactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val warningSpoiler: TextView = itemView.findViewById(R.id.textViewSpoilerWarn)
        val factText: TextView = itemView.findViewById(R.id.textViewFact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.fact_item, parent, false)
        return FactViewHolder(view)
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        val fact = facts[position]
        if (fact.spoiler) {
            holder.warningSpoiler.visibility = VISIBLE
        }
        holder.factText.text = fact.text
    }

    override fun getItemCount(): Int {
        return facts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFacts(facts: List<Fact>) {
        this.facts = facts
        notifyDataSetChanged()
    }
}