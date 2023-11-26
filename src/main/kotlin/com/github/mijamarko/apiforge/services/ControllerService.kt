package com.github.mijamarko.apiforge.services

import com.github.mijamarko.apiforge.model.Controller
import com.github.mijamarko.apiforge.model.Controller.*
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.childrenOfType
import com.intellij.util.containers.stream

private const val SPRING_ANNOTATION_PATH_PREFIX = "org.springframework.web.bind.annotation."
private const val SPRING_STEREOTYPE_PATH_PREFIX = "org.springframework.stereotype."

@Service(Service.Level.PROJECT)
class ControllerService(project: Project) {

    enum class SpringComponent(private val fullName: String) {
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
        PATH_VARIABLE("PathVariable");

        fun qualifiedName(): String {
            if (this.name == "CONTROLLER") {
                return "$SPRING_STEREOTYPE_PATH_PREFIX${this.fullName}"
            }
            return "$SPRING_ANNOTATION_PATH_PREFIX${this.fullName}"
        }
    }

    private val httpRequestParamTypes = listOf(SpringComponent.REQUEST_PARAM.qualifiedName(), SpringComponent.REQUEST_BODY
        .qualifiedName(), SpringComponent.PATH_VARIABLE.qualifiedName())

    private val httpRequestMethods = listOf(SpringComponent.GET_MAPPING.qualifiedName(), SpringComponent.POST_MAPPING
        .qualifiedName(), SpringComponent.PATCH_MAPPING.qualifiedName(), SpringComponent.PUT_MAPPING.qualifiedName(),
        SpringComponent.DELETE_MAPPING.qualifiedName())

    private val controllerClasses = ArrayList<PsiJavaFile>()
    private val controllerPackages = ArrayList<String?>()

    val controllerDetails = HashMap<String, Controller>()

    fun loadService(project: Project) {
        controllerClasses.addAll(scanForControllerClasses(project))
        controllerPackages.addAll(extractControllerPackages(controllerClasses))
        mapControllerClasses(controllerClasses)
    }

    private fun scanForControllerClasses(project: Project): Collection<PsiJavaFile> {
        val psimgr = PsiManager.getInstance(project)
        return ReadAction.compute<List<PsiJavaFile>, Exception> {
            val controllers = ArrayList<PsiJavaFile>()
            ProjectFileIndex.getInstance(project).iterateContent { fileOrDir ->
                if (!fileOrDir.isDirectory && fileOrDir.extension == "java") {
                    val jfile = psimgr.findFile(fileOrDir)
                    if (jfile is PsiJavaFile) {
                        val filteredClasses = jfile.childrenOfType<PsiClass>()
                            .stream()
                            .filter { clazz ->
                                clazz.modifierList?.annotations
                                    .stream()
                                    .anyMatch { ann ->
                                        ann.qualifiedName in listOf(
                                            SpringComponent.REST_CONTROLLER.qualifiedName(),
                                            SpringComponent.CONTROLLER.qualifiedName()
                                        )
                                    }
                            }.toList()
                        if (filteredClasses.isNotEmpty()) {
                            controllers.add(jfile)
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

    private fun mapControllerClasses(controllerClasses: List<PsiJavaFile>) {
        for (controller in controllerClasses) {
            controller.childrenOfType<PsiClass>()
            .forEach { clazz ->
//                val name = clazz.name
                val path = clazz.modifierList?.annotations?.filter { ann ->
                    ann.qualifiedName == SpringComponent.REQUEST_MAPPING.qualifiedName()
                }?.map { filtered ->
                    filtered.parameterList.attributes
                }?.first()?.get(0)?.literalValue

                if (path?.isNotEmpty() == true && !clazz.name.isNullOrEmpty()) {
                    controllerDetails["${clazz.name}"] = Controller(path)
                    mapControllerMethods(clazz)
                }
            }
        }
    }
    private fun mapControllerMethods(clazz: PsiClass) {
        val eps = HashMap<String, Endpoint>()
        clazz.childrenOfType<PsiMethod>()
            .forEach { met ->
                val ep = Endpoint()
                eps[met.name] = ep
                println("Method return type -> ${met.returnType.toString()}")
                met.annotations.forEach { ann ->
                    println("Ann qualified name -> ${ann.qualifiedName}")
                    ann.parameterList.attributes.forEach { att ->
                        println("Attribute name -> ${att.name}")
                        println("Attribute literal value -> ${att.literalValue}")
                        println("Attribute value -> ${att.value}")
                        println("Attribute detached value${att.detachedValue}")
                    }
                }
                met.parameterList.parameters.forEach { param ->
                    println("Param name -> ${param.name}")
                    println("Param type -> ${param.type}")
                    if (param.annotations.isNotEmpty()) {
                        param.annotations.forEach { paran ->
                            println("Param annotation name -> ${paran.qualifiedName}")
                        }
                    }
                }
            }
    }
//            for (clazz in classes) {
//                println("Klasa -> ${clazz.name}")
//                val modList = clazz.modifierList
//                clazz.annotations
//                val annotations = modList?.annotations
//                if (annotations != null) {
//                    for (ann in annotations) {
//                        println("Annotation name-> ${ann.qualifiedName}")
//                        println("Annotation reference element -> ${ann.nameReferenceElement}")
//                        val annList = ann.parameterList.attributes
//                        for (kvp in annList) {
//                            println("Annotation, valjda:")
//                            println("name -> ${kvp.name}")
//                            println("nameIdentifier -> ${kvp.nameIdentifier}")
//                            println("literalValue -> ${kvp.literalValue}")
//                            println("value -> ${kvp.value}")
//                            println("detachedValue -> ${kvp.detachedValue}")
//                            println("attributeName -> ${kvp.attributeName}")
//                            println("attributeValue -> ${kvp.attributeValue}")
//                        }
//                    }
//                }
//
//            }
//        }

}