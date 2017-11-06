package com.patres.idea.diff.utils;

import com.intellij.ide.TwoPaneIdeView;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.ui.content.Content;
import com.intellij.util.IncorrectOperationException;

import javax.swing.*;
import java.io.IOException;
import java.util.Collections;

import static com.intellij.refactoring.copy.CopyFilesOrDirectoriesHandler.copyToDirectory;

// Workaround because of private method in CopyHandler and CopyFilesOrDirectoriesHandler (IntellIj SDK)
public class MyCopyHandlerUtils {

	private MyCopyHandlerUtils() {
	}

	public static void copyImpl(final SmartPsiElementPointer[] elements, final String newName, final PsiDirectory targetDirectory, final boolean doClone, final boolean openInEditor) {
		if (doClone && elements.length != 1) {
			throw new IllegalArgumentException("invalid number of elements to clone:" + elements.length);
		}

		if (newName != null && elements.length != 1) {
			throw new IllegalArgumentException("no new name should be set; number of elements is: " + elements.length);
		}

		final Project project = targetDirectory.getProject();
		if (!CommonRefactoringUtil.checkReadOnlyStatus(project, Collections.singleton(targetDirectory), false)) {
			return;
		}

		String title = RefactoringBundle.message(doClone ? "copy,handler.clone.files.directories" : "copy.handler.copy.files.directories");
		try {
			PsiFile firstFile = null;
			final int[] choice = elements.length > 1 || elements[0] instanceof PsiDirectory ? new int[] { -1 } : null;
			for (SmartPsiElementPointer element : elements) {
				PsiElement psiElement = element.getElement();
				if (psiElement == null)
					continue;
				PsiFile f = copyToDirectory((PsiFileSystemItem) psiElement, newName, targetDirectory, choice, title);
				if (firstFile == null) {
					firstFile = f;
				}
			}

			if (firstFile != null && openInEditor) {
				updateSelectionInActiveProjectView(firstFile, project, doClone);
				if (!(firstFile instanceof PsiBinaryFile)) {
					EditorHelper.openInEditor(firstFile);
					ToolWindowManager.getInstance(project).activateEditorComponent();
				}
			}
		} catch (final IncorrectOperationException | IOException ex) {
			Messages.showErrorDialog(project, ex.getMessage(), RefactoringBundle.message("error.title"));
		}
	}

	private static void updateSelectionInActiveProjectView(PsiElement newElement, Project project, boolean selectInActivePanel) {
		String id = ToolWindowManager.getInstance(project).getActiveToolWindowId();
		if (id != null) {
			ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(id);
			Content selectedContent = window.getContentManager().getSelectedContent();
			if (selectedContent != null) {
				JComponent component = selectedContent.getComponent();
				if (component instanceof TwoPaneIdeView) {
					((TwoPaneIdeView) component).selectElement(newElement, selectInActivePanel);
					return;
				}
			}
		}
		if (ToolWindowId.PROJECT_VIEW.equals(id)) {
			ProjectView.getInstance(project).selectPsiElement(newElement, true);
		} else if (ToolWindowId.STRUCTURE_VIEW.equals(id)) {
			VirtualFile virtualFile = newElement.getContainingFile().getVirtualFile();
			FileEditor editor = FileEditorManager.getInstance(newElement.getProject()).getSelectedEditor(virtualFile);
			StructureViewFactoryEx.getInstanceEx(project).getStructureViewWrapper().selectCurrentElement(editor, virtualFile, true);
		}
	}
}
