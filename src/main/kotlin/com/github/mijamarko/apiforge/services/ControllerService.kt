package com.github.mijamarko.apiforge.services

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import org.jetbrains.uast.util.isInstanceOf
import java.util.stream.Collectors
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage
import kotlin.streams.toList

@Service(Service.Level.PROJECT)
class ControllerService(project: Project) {

    private val psiCollection = ArrayList<PsiFile>()
    private val controllerPackages = ArrayList<String?>()

    fun loadService(project: Project) {
        psiCollection.addAll(scanForControllers(project))
        controllerPackages.addAll(extractControllerPackages(psiCollection))
    }

    fun scanForControllers(project: Project): Collection<PsiFile> {
        val psiManager = PsiManager.getInstance(project)
        val reg = Regex("^(@RestController|@Controller)($|\\(\\w+\\))", RegexOption.MULTILINE)
        return ReadAction.compute<List<PsiFile>, Exception> {
            val controllers = ArrayList<PsiFile>()
            ProjectFileIndex.getInstance(project).iterateContent { fileOrDir ->
                if (!fileOrDir.isDirectory && fileOrDir.extension == "java") {
                    psiManager.findFile(fileOrDir)?.let {
                        val contents = it.viewProvider.contents
                        if (reg.containsMatchIn(contents)){
                            controllers.add(it)
                        }
                    }
                }
                true
            }
            controllers
        }
    }

    fun extractControllerPackages(psiCollection: Collection<PsiFile>): List<String?> {
        return psiCollection.stream()
            .map {
                if (it is PsiJavaFile) {
                    it.packageName
                } else {
                    null
                }
            }.filter{ it != null }.toList().distinct()
    }

}