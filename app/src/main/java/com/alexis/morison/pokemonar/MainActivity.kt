package com.alexis.morison.pokemonar

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.alexis.morison.pokemonar.Clases.CustomArFragment
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var arFragment: ArFragment

    private lateinit var btnPokebola: ImageButton

    private lateinit var gridLayoutPokemon: ScrollView
    private lateinit var btnGridClose: ImageButton
    private lateinit var btnDeleteModel: ImageButton
    private lateinit var frameBtnDelete: FrameLayout

    private lateinit var btnMeowth: ImageButton
    private lateinit var btnCharmander: ImageButton
    private lateinit var btnBulbasaur: ImageButton
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

        arFragment = ar_fragment_view as CustomArFragment

        btnPokebola = btn_pokebola
        frameBtnDelete = frame_btn_delete
        btnDeleteModel = btn_delete_model

        gridLayoutPokemon = findViewById(R.id.grid_pokemon)
        btnGridClose = findViewById(R.id.btn_grid_close)
        btnMeowth = findViewById(R.id.btn_meowth)
        btnCharmander = findViewById(R.id.btn_charmander)
        btnPikachu = findViewById(R.id.btn_pikachu)
        btnBulbasaur = findViewById(R.id.btn_bullbasaur)
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
        btnGridClose.setOnClickListener { onClick(btnGridClose) }
        btnMeowth.setOnClickListener { onClick(btnMeowth) }
        btnCharmander.setOnClickListener { onClick(btnCharmander) }
        btnPikachu.setOnClickListener { onClick(btnPikachu) }
        btnBulbasaur.setOnClickListener { onClick(btnBulbasaur) }
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
                    gridLayoutPokemon.visibility = View.GONE
                    gridVisible = !gridVisible
                }
                else {
                    gridLayoutPokemon.visibility = View.VISIBLE
                    gridVisible = !gridVisible
                }
            }

            // Cerrar menu para elegir pokemon
            R.id.btn_grid_close -> {
                gridLayoutPokemon.visibility = View.GONE
                gridVisible = false
            }

            else -> {

                when (view?.id) {

                    // Btns de Pokemones
                    R.id.btn_meowth -> pokemonSelect = PokemonType.MEOWTH

                    R.id.btn_charmander -> pokemonSelect = PokemonType.CHARMANDER

                    R.id.btn_pikachu -> pokemonSelect = PokemonType.PIKACHU

                    R.id.btn_bullbasaur -> pokemonSelect = PokemonType.BULBASAUR
                }

                Toast.makeText(this, "Toque la pantalla para colocar", Toast.LENGTH_SHORT).show()

                gridLayoutPokemon.visibility = View.GONE
                gridVisible = false
            }
        }
    }


}