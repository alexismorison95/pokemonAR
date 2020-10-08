package com.alexis.morison.pokemonar.Clases

import android.widget.Toast
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment: ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {

        val config = Config(session)

        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.focusMode = Config.FocusMode.AUTO
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        session?.configure(config)

        this.arSceneView.setupSession(session)

        return config
    }
}