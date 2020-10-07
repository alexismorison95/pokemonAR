package com.alexis.morison.pokemonar

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var btnMeowth: Button
    private lateinit var btnCharmander: Button

    private enum class PokemonType {
        POLIWAG,
        CHARMANDER
    }

    private var pokemonSelect: PokemonType = PokemonType.POLIWAG


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()

        setListeners()
    }

    private fun setViews() {

        arFragment = ar_fragment_view as ArFragment
        btnMeowth = btn_meowth
        btnCharmander = btn_charmander
    }

    private fun setListeners() {

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            val anchor = hitResult.createAnchor()

            when (pokemonSelect) {

                PokemonType.POLIWAG -> makeModel(anchor, "meowth.sfb")
                PokemonType.CHARMANDER -> makeModel(anchor, "charmander.sfb")
            }

            btnMeowth.setOnClickListener {

                pokemonSelect = PokemonType.POLIWAG

                Toast.makeText(this, "Meowth seleccionado", Toast.LENGTH_SHORT).show()
            }

            btnCharmander.setOnClickListener {

                pokemonSelect = PokemonType.CHARMANDER

                Toast.makeText(this, "Charmander seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeModel(anchor: Anchor, name: String) {

        ModelRenderable.builder()
            .setSource(this, Uri.parse(name))
            .build()
            .thenAccept { placeModel(anchor, it) }
            .exceptionally {

                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")

                val dialog = builder.create()
                dialog.show()

                return@exceptionally null
            }
    }

    private fun placeModel(anchor: Anchor, modelRenderable: ModelRenderable) {

        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)

        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable

        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }
}