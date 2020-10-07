package com.alexis.morison.pokemonar

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    private lateinit var btnPokebola: ImageButton

    private lateinit var gridLayoutPokemon: ScrollView
    private lateinit var btnGridClose: ImageButton
    private lateinit var btnDeleteModel: ImageButton
    private lateinit var frameBtnDelete: FrameLayout

    private lateinit var btnMeowth: ImageButton
    private lateinit var btnCharmander: ImageButton
    private lateinit var btnbulbasaur: ImageButton
    private lateinit var btnPikachu: ImageButton

    private var gridVisible: Boolean = false

    private enum class PokemonType {
        CHARMANDER,
        MEOWTH,
        PIKACHU,
        BULBASAUR
    }

    private var pokemonSelect: PokemonType = PokemonType.CHARMANDER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()

        setListeners()
    }

    private fun setViews() {

        arFragment = ar_fragment_view as ArFragment
        btnPokebola = btn_pokebola
        gridLayoutPokemon = grid_pokemon
        btnGridClose = btn_grid_close
        frameBtnDelete = frame_btn_delete
        btnDeleteModel = btn_delete_model
        btnMeowth = btn_meowth
        btnCharmander = btn_charmander
        btnPikachu = btn_pikachu
        btnbulbasaur = btn_bullbasaur
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

        btnPokebola.setOnClickListener {

            if (gridVisible) {

                gridLayoutPokemon.visibility = View.GONE
                gridVisible = !gridVisible
            }
            else {

                gridLayoutPokemon.visibility = View.VISIBLE
                gridVisible = !gridVisible
            }
        }

        btnGridClose.setOnClickListener {

            gridLayoutPokemon.visibility = View.GONE
            gridVisible = false
        }

        btnMeowth.setOnClickListener {

            pokemonSelect = PokemonType.MEOWTH

            Toast.makeText(this, "Meowth seleccionado", Toast.LENGTH_SHORT).show()
        }

        btnCharmander.setOnClickListener {

            pokemonSelect = PokemonType.CHARMANDER

            Toast.makeText(this, "Charmander seleccionado", Toast.LENGTH_SHORT).show()
        }

        btnPikachu.setOnClickListener {

            pokemonSelect = PokemonType.PIKACHU

            Toast.makeText(this, "Pikachu seleccionado", Toast.LENGTH_SHORT).show()
        }

        btnbulbasaur.setOnClickListener {

            pokemonSelect = PokemonType.BULBASAUR

            Toast.makeText(this, "Bulbasaur seleccionado", Toast.LENGTH_SHORT).show()
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
}