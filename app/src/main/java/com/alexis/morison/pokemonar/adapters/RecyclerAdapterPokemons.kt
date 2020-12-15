package com.alexis.morison.pokemonar.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.alexis.morison.pokemonar.MainActivity
import com.alexis.morison.pokemonar.R
import com.alexis.morison.pokemonar.models.Pokemon
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view_item.view.*


class RecyclerAdapterPokemons(private val listaPokemones: List<Pokemon>) :
    RecyclerView.Adapter<RecyclerAdapterPokemons.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(listaPokemones[position])

    override fun getItemCount(): Int = listaPokemones.size


    class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(item: Pokemon) = with(v) {

                item_text.text = item.nombre
                Picasso.get()
                    .load(item.url)
                    .placeholder(R.drawable.ic_baseline_sync_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .resize(100, 100)
                    .into(item_image_btn)

                item_image_btn.setOnClickListener {

                    MainActivity.storageModel.setPokemonSelected(item.nombre)
                    MainActivity.storageModel.setModelRef()

                    MainActivity.quitarRecycler()

                    val toast = Toast.makeText(v.context, "Toque para agregar a ${item.nombre}", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
    }
}

