package com.alexis.morison.pokemonar

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexis.morison.pokemonar.adapters.RecyclerAdapterPokemons
import com.alexis.morison.pokemonar.clases.Pokemon
import com.alexis.morison.pokemonar.clases.PokemonType
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var arFragment: ArFragment

    private lateinit var btnPokebola: ImageButton

    private lateinit var btnDeleteModel: ImageButton
    private lateinit var frameBtnDelete: FrameLayout

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Para poder acceder desde el Adapter
    companion object {

        var pokemonSelect: PokemonType = PokemonType.CHARMANDER

        private lateinit var recyclerView: RecyclerView
        private var gridVisible: Boolean = false

        fun quitarRecycler() {
            recyclerView.visibility = View.GONE
            gridVisible = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()

        setListeners()

        setRecyclerView()
    }

    private fun setViews() {

        arFragment = ar_fragment_view as ArFragment

        btnPokebola = btn_pokebola
        frameBtnDelete = frame_btn_delete
        btnDeleteModel = btn_delete_model

        recyclerView = recycler_view
    }

    private fun setListeners() {

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            val anchor = hitResult.createAnchor()

            when (pokemonSelect) {

                PokemonType.CHARMANDER -> makeModel(anchor, "charmander")
                PokemonType.MEOWTH -> makeModel(anchor, "meowth")
                PokemonType.PIKACHU -> makeModel(anchor, "pikachu")
                PokemonType.BULBASAUR -> makeModel(anchor, "bulbasaur")
            }
        }

        btnPokebola.setOnClickListener { onClick(btnPokebola) }
    }

    private fun setRecyclerView() {

        val listaPokemones = listOf(
            Pokemon("Bulbasaur", PokemonType.BULBASAUR, R.drawable.ic_bullbasaur),
            Pokemon("Charmander", PokemonType.CHARMANDER, R.drawable.ic_charmander),
            Pokemon("Meowth", PokemonType.MEOWTH, R.drawable.ic_meowth),
            Pokemon("Pikachu", PokemonType.PIKACHU, R.drawable.ic_pikachu_2)
        )

        viewManager = GridLayoutManager(this, 2)
        viewAdapter = RecyclerAdapterPokemons(listaPokemones)

        recyclerView.apply {

            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter
        }
    }

    private fun makeModel(anchor: Anchor, name: String) {

        ModelRenderable.builder()
            .setSource(this, Uri.parse("${name}.sfb"))
            .build()
            .thenAccept { placeModel(anchor, it, name) }
            .exceptionally {

                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")

                val dialog = builder.create()
                dialog.show()

                return@exceptionally null
            }
    }

    private fun placeModel(anchor: Anchor, modelRenderable: ModelRenderable, name: String) {

        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)

        transformableNode.scaleController.maxScale = 2.0f
        transformableNode.scaleController.minScale = 0.2f

        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable

        arFragment.arSceneView.scene.addChild(anchorNode)

        transformableNode.select()

        transformableNode.setOnTapListener { hitTestResult, _ ->

            frameBtnDelete.visibility = View.VISIBLE

            btnDeleteModel.setOnClickListener {

                val nodeToRemove = hitTestResult.node
                anchorNode.removeChild(nodeToRemove)

                frameBtnDelete.visibility = View.GONE
            }
        }
    }

    override fun onClick(view: View?) {

        when (view?.id) {

            // Abrir y cerrar menu para elegir pokemon
            R.id.btn_pokebola -> {

                if (gridVisible) {

                    recyclerView.visibility = View.GONE
                    gridVisible = !gridVisible
                }
                else {

                    recyclerView.visibility = View.VISIBLE
                    gridVisible = !gridVisible
                }
            }
        }
    }


}