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
import com.alexis.morison.pokemonar.models.Storage
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var arFragment: ArFragment

    private lateinit var btnPokebola: ImageButton

    private lateinit var btnDeleteModel: ImageButton
    private lateinit var frameBtnDelete: FrameLayout

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val modelsDict = hashMapOf<String, File>()

    private lateinit var progressBarModel: ProgressBar


    // Para poder acceder desde el Adapter
    companion object {

        val storageModel = Storage()

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

        FirebaseApp.initializeApp(this)

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

        progressBarModel = progress_bar_model
    }


    private fun setListeners() {

        setModelOnPlaneListener()

        btnPokebola.setOnClickListener { onClick(btnPokebola) }
    }


    private fun setModelOnPlaneListener() {

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            val anchor = hitResult.createAnchor()

            // Chequeo si el modelo ya fue descargado
            if (modelsDict.containsKey(storageModel.getPokemonSelected())) {

                modelsDict[storageModel.getPokemonSelected()]?.let { makeModel(anchor, it) }
            }
            else {
                progressBarModel.visibility = View.VISIBLE

                val fileModel = File.createTempFile(storageModel.getPokemonSelected(), "sfb")
                modelsDict[storageModel.getPokemonSelected()] = fileModel

                storageModel.modelRef.getFile(fileModel).addOnSuccessListener {

                    progressBarModel.visibility = View.GONE

                    makeModel(anchor, fileModel)
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    progressBarModel.visibility = View.GONE
                }
            }
        }
    }


    private fun setRecyclerView() {

        viewManager = GridLayoutManager(this, 3)
        viewAdapter = RecyclerAdapterPokemons(storageModel.getListOfPokemons())

        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    private fun makeModel(anchor: Anchor, file: File) {

        ModelRenderable.builder()
            //.setSource(this, Uri.parse("${name}.sfb"))
            .setSource(this, Uri.parse(file.path))
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