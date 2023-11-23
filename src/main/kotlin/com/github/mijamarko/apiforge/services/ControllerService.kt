package com.github.mijamarko.apiforge.services

import com.github.mijamarko.apiforge.model.Controller
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.childrenOfType

@Service(Service.Level.PROJECT)
class ControllerService(project: Project) {

    private val SPRING_ANNOTATION_PATH_PREFIX = "org.springframework.web.bind.annotation."
    private val SPRING_STEREOTYPE_PATH_PREFIX = "org.springframework.stereotype."

    enum class SpringComponent(name: String) {
        REST_CONTROLLER("RestController"),
        REQUEST_MAPPING("RequestMapping"),
        GET_MAPPING("GetMapping"),
        POST_MAPPING("PostMapping"),
        PATCH_MAPPING("PatchMapping"),
        PUT_MAPPING("PutMapping"),
        DELETE_MAPPING("DeleteMapping"),
        CONTROLLER("Controller"),
        REQUEST_BODY("RequestBody"),
        REQUEST_PARAM("RequestParam"),
        PATH_VARIABLE("PathVariable")
    }

    private val controllerClasses = ArrayList<PsiJavaFile>()
    private val controllerPackages = ArrayList<String?>()

    val controllerDetails = HashMap<String, Controller>()

    fun loadService(project: Project) {
        controllerClasses.addAll(scanForControllerClasses(project))
        controllerPackages.addAll(extractControllerPackages(controllerClasses))
        extractMethodsFromController(controllerClasses)
    }

    private fun scanForControllerClasses(project: Project): Collection<PsiJavaFile> {
        val psiManager = PsiManager.getInstance(project)
        val reg = Regex("^(@RestController|@Controller)($|\\(\\w+\\))", RegexOption.MULTILINE)
        return ReadAction.compute<List<PsiJavaFile>, Exception> {
            val controllers = ArrayList<PsiJavaFile>()
            ProjectFileIndex.getInstance(project).iterateContent { fileOrDir ->
                if (!fileOrDir.isDirectory && fileOrDir.extension == "java") {
                    psiManager.findFile(fileOrDir)?.let {
                        val contents = it.viewProvider.contents
                        if (reg.containsMatchIn(contents) && it is PsiJavaFile){
                            controllers.add(it)
                        }
                    }
                }
                true
            }
            controllers
        }
    }

    private fun extractControllerPackages(controllerClasses: Collection<PsiJavaFile>): List<String?> {
        return controllerClasses.stream()
            .map { it.packageName }.toList().distinct()
    }

    private fun extractMethodsFromController(controllers: List<PsiJavaFile>) {
        for (controller in controllers) {
            val classes = controller.childrenOfType<PsiClass>()
            for (clazz in classes) {
                println("Klasa -> ${clazz.name}")
                val modList = clazz.modifierList
                clazz.annotations
                val annotations = modList?.annotations
                if (annotations != null) {
                    for (ann in annotations) {
                        println("Annotation name-> ${ann.qualifiedName}")
                        println("Annotation reference element -> ${ann.nameReferenceElement}")
                        val annList = ann.parameterList.attributes
                        for (kvp in annList) {
                            println("Annotation, valjda:")
                            println("name -> ${kvp.name}")
                            println("nameIdentifier -> ${kvp.nameIdentifier}")
                            println("literalValue -> ${kvp.literalValue}")
                            println("value -> ${kvp.value}")
                            println("detachedValue -> ${kvp.detachedValue}")
                            println("attributeName -> ${kvp.attributeName}")
                            println("attributeValue -> ${kvp.attributeValue}")
                        }
                    }
                }

            }
        }
//        controllers.forEach {
//            it.childrenOfType<PsiClass>()
//                .forEach {
//                clazz -> println(AnnotationUtil.getAllAnnotations(clazz, true, null))
////                    println(clazz.name)
////                    run {
////                        val mods = clazz.modifierList
////                        val anns = mods?.annotations
////                        anns?.map { ans -> ans.parameterList }
////                            ?.map { li -> li.attributes }
////                            ?.filter { item -> item.isNotEmpty() }
////                            ?.forEach { pair -> pair.forEach { u -> println("${u.attributeName} -> ${u.attributeValue.toString()}") } }
////                    }
//            }
        }
    }