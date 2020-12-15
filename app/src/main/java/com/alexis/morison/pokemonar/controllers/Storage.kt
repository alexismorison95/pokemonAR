package com.alexis.morison.pokemonar.controllers

import com.alexis.morison.pokemonar.models.Pokemon
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage


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

        // Descargo la referencia del modelo
        modelRef = storage.reference.child("$pokemonSelected/$pokemonSelected.sfb")
    }


    fun getListOfPokemons(): List<Pokemon> = listOfPokemons


    private fun downloadListOfPokemons() {

        // Descargo la lista de pokemones cargados en Firebase

        val listRef = storage.reference

        listRef.listAll()
            .addOnSuccessListener { (_, prefixes) ->

                prefixes.forEach { prefix ->

                    val image = prefix.child("${prefix.name}.png")

                    image.downloadUrl.addOnSuccessListener {
                        listOfPokemons.add(Pokemon(prefix.name, it))
                    }
                }
            }
    }



}