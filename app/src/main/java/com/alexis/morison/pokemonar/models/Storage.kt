package com.alexis.morison.pokemonar.models

import com.alexis.morison.pokemonar.clases.Pokemon
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import java.io.File

class Storage {

    private var storage: FirebaseStorage = Firebase.storage
    private var listOfPokemons = mutableListOf<Pokemon>()
    private var pokemonSelected: String = "Charmander"

    lateinit var modelRef: StorageReference


    init {
        downloadListOfPokemons()

        setModelRef()
    }


    fun setPokemonSelected(pokemonName: String) {
        pokemonSelected = pokemonName
    }

    fun getPokemonSelected() = pokemonSelected


    fun setModelRef() {
        modelRef = storage.reference.child("${pokemonSelected}.sfb")
    }


    fun getListOfPokemons(): List<Pokemon> = listOfPokemons


    private fun downloadListOfPokemons() {

        val listRef = storage.reference

        listRef.listAll()
            .addOnSuccessListener { (items, _) ->

                items.forEach { item ->

                    val nameSplited = item.name.split(".")

                    if (nameSplited[1] == "png") {
                        item.downloadUrl.addOnSuccessListener {
                            listOfPokemons.add(Pokemon(nameSplited[0], it))
                        }
                    }
                }
            }
    }



}