package com.github.mijamarko.apiforge.window

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.*


class MainWindowFactory : ToolWindowFactory {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
       val window = MainWindow(toolWindow)
       val content = ContentFactory.getInstance().createContent(window.getContent(), "API Forge", true)
        toolWindow.contentManager.addContent(content)
    }

    class MainWindow(toolWindow: ToolWindow) {
       fun getContent() : DialogPanel {
           return panel {
               indent {
                   row("My row") {
                       topGap(TopGap.MEDIUM)
                       textField()
                       label("tfield label")
                   }.label("Row label?")

                   row("Row 2") {
                       label("Alligned with previous row?")
                   }

                   row("3") {
                       textField()
                   }.layout(RowLayout.PARENT_GRID)

                   row("4") {
                       checkBox("cb1")
                       label("check box")
                   }.layout(RowLayout.PARENT_GRID)
               }
           }
       }
    }
}
