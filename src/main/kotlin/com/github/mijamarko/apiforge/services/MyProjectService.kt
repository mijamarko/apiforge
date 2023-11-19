package com.github.mijamarko.apiforge.services

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    val rootDirFile = ProjectRootManager.getInstance(project).contentRoots.filter { f -> f.path == project.basePath }.first()
    fun checkForgeConfigExists(project: Project): VirtualFile? {
        thisLogger().warn("Root dir file is -> $rootDirFile")
        return rootDirFile.fileSystem.findFileByPath("${project.basePath}/.forge")
//        ProjectFileIndex.getInstance(project).iterateContent {  fileOrDir ->
//            println(fileOrDir.name)
//
//            true
//        }
    }

    fun createDefaultForgeConfig(project: Project): VirtualFile? {
            return WriteAction.compute<VirtualFile?, IOException> {
                rootDirFile.createChildData(null, ".forge")
            }
    }
}
