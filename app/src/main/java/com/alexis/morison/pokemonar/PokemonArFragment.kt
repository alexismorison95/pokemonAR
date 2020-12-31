package com.alexis.morison.pokemonar

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class PokemonArFragment: ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config = Config(session).apply {

        lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
        updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
    }
}