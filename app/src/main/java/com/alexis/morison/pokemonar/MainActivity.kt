package com.alexis.morison.pokemonar

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexis.morison.pokemonar.adapters.RecyclerAdapterPokemons
import com.alexis.morison.pokemonar.controllers.Storage
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener, MenuItem.OnMenuItemClickListener,
    PopupMenu.OnMenuItemClickListener {

    private lateinit var arFragment: ArFragment

    private lateinit var btnPokebola: ImageButton
    private lateinit var btnDeleteModel: ImageButton
    private lateinit var btnOpciones: ImageButton

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var columnWidth = -1

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

        // Limpio la cache
        this.cacheDir.deleteRecursively()

        FirebaseApp.initializeApp(this)

        setViews()
        setListeners()
        setRecyclerView()
    }


    private fun setViews() {

        arFragment = ar_fragment_view as ArFragment

        btnPokebola = btn_pokebola
        btnDeleteModel = btn_delete_model
        btnOpciones = btn_options

        recyclerView = recycler_view

        progressBarModel = progress_bar_model
    }


    private fun setListeners() {

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            val anchor = hitResult.createAnchor()
            loadModel(anchor)
        }

        btnPokebola.setOnClickListener { onClick(btnPokebola) }
        btnOpciones.setOnClickListener { onClick(btnOpciones) }
    }


    private fun loadModel(anchor: Anchor) {

        // Chequeo si el modelo ya fue descargado
        if (modelsDict.containsKey(storageModel.getPokemonSelected())) {

            modelsDict[storageModel.getPokemonSelected()]?.let { makeModel(anchor, it) }
        }
        else {
            progressBarModel.visibility = View.VISIBLE

            // Creo un archivo temporal
            val fileModel = File.createTempFile(storageModel.getPokemonSelected(), "sfb")
            modelsDict[storageModel.getPokemonSelected()] = fileModel

            // Descargo el modelo
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


    private fun calcularColumnas(): Int {

        val displayMetrics = this.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        return (dpWidth / 100).toInt()
    }


    private fun setRecyclerView() {

        viewManager = GridLayoutManager(this, calcularColumnas())
        viewAdapter = RecyclerAdapterPokemons(storageModel.getListOfPokemons())

        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    private fun makeModel(anchor: Anchor, file: File) {

        ModelRenderable.builder()
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

        // Listener para poder seleccionar modelo y poder eliminarlo
        transformableNode.setOnTapListener { hitTestResult, _ ->

            btnDeleteModel.visibility = View.VISIBLE

            btnDeleteModel.setOnClickListener {

                val nodeToRemove = hitTestResult.node
                anchorNode.removeChild(nodeToRemove)

                btnDeleteModel.visibility = View.GONE
            }
        }
    }


    override fun onClick(view: View?) {

        when (view?.id) {

            // Abrir y cerrar menu para elegir pokemon
            R.id.btn_pokebola -> {

                if (gridVisible) {
                    recyclerView.visibility = View.GONE
                }
                else {
                    recyclerView.visibility = View.VISIBLE

                    //layoutOpciones.visibility = View.GONE
                    btnOpciones.setImageResource(R.drawable.ic_baseline_more_vert_24)
                    //layoutView = false
                }
                gridVisible = !gridVisible
            }

            // Menu de opciones
            R.id.btn_options -> {

                PopupMenu(this, view).apply {

                    setOnMenuItemClickListener(this@MainActivity)
                    inflate(R.menu.menu_options)
                    show()
                }
            }
        }
    }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


}