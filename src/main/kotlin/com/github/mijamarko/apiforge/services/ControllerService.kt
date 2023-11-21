package com.github.mijamarko.apiforge.services

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

@Service(Service.Level.PROJECT)
class ControllerService(project: Project) {

    private val psiCollection = ArrayList<PsiFile>()

    fun scanForControllers(project: Project) {
        val psiManager = PsiManager.getInstance(project)
        val reg = Regex("^(@RestController|@Controller)($|\\(\\w+\\))", RegexOption.MULTILINE)
        ProjectFileIndex.getInstance(project).iterateContent { fileOrDir ->
            ReadAction.run<Exception> {
                if (!fileOrDir.isDirectory && fileOrDir.extension == "java") {
                    psiManager.findFile(fileOrDir)?.let {
                        val contents = it.viewProvider.contents
                        if (reg.containsMatchIn(contents)){
                            psiCollection.add(it)
                        }
                    }
                }
            }
            true
        }
    }


}