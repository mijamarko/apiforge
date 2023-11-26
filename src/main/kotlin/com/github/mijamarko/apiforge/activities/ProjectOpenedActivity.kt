package com.github.mijamarko.apiforge.activities

import com.github.mijamarko.apiforge.services.ControllerService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


internal class ProjectOpenedActivity : StartupActivity {
    override fun runActivity(project: Project) {
        val controllerService = project.service<ControllerService>()
        controllerService.loadService(project)
    }

}