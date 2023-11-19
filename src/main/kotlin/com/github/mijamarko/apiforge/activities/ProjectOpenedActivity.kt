package com.github.mijamarko.apiforge.activities

import com.github.mijamarko.apiforge.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


internal class ProjectOpenedActivity : StartupActivity {
    override fun runActivity(project: Project) {
        thisLogger().warn(project.name)
        val service = project.service<MyProjectService>()
        val cfgExists = service.checkForgeConfigExists(project)
        val config = if (cfgExists == null) service.createDefaultForgeConfig(project) else null
        println(config)
    }

}
