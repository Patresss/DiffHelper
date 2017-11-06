package com.patres.idea.diff.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.util.IncorrectOperationException;
import com.patres.idea.diff.DiffHelperBundle;
import com.patres.idea.diff.config.DiffHelperConfig;
import com.patres.idea.diff.utils.DiffNameUtils;
import com.patres.idea.diff.utils.MyCopyHandlerUtils;

import java.util.Arrays;

public class NewDiffAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		PsiFile templateFile = getDestinationDirectory(project);
		if (templateFile == null) {
			Messages.showMessageDialog(project, DiffHelperBundle.message("error.diffTemplateNotFound"), DiffHelperBundle.message("error.title"), Messages.getWarningIcon());
		} else {
			VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
			virtualFile = getDestinationDirectory(virtualFile);
			PsiDirectory directory = PsiManager.getInstance(project).findDirectory(virtualFile);
			copyFile(project, templateFile, directory);
		}
	}

	private PsiFile getDestinationDirectory(Project project) {
		VirtualFile templateFile = LocalFileSystem.getInstance().findFileByPath(DiffHelperConfig.getInstance(project).getDiffTemplatePath());
		if(templateFile == null) {
			return null;
		}
		return PsiManager.getInstance(project).findFile(templateFile);
	}

	private void copyFile(Project project, PsiElement file, PsiDirectory target) {
		try {
			String taskName = getTaskName(project);
			String fileName = DiffNameUtils.getFileName(project, taskName);
			PsiElement[] files = {file};
			SmartPointerManager manager = SmartPointerManager.getInstance(project);
			CommandProcessor.getInstance().executeCommand(project, () -> MyCopyHandlerUtils.copyImpl(Arrays.stream(files).map(manager::createSmartPsiElementPointer).toArray(SmartPsiElementPointer[]::new),
					fileName , target, false, true),
					RefactoringBundle.message("copy.handler.copy.files.directories"), null);
		} catch (IncorrectOperationException e) {
			Messages.showErrorDialog(project, e.getMessage(), RefactoringBundle.message("error.title"));
		}
	}

	private VirtualFile getDestinationDirectory(VirtualFile file) {
		if (file != null && !file.isDirectory()) {
			file = file.getParent();
		}
		return file;
	}

	private String getTaskName(Project project) {
		if(DiffHelperConfig.getInstance(project).isTaskNameFromChangelistCheck()) {
			return DiffNameUtils.getTicketFromChangelist(project);
		} else {
			return DiffNameUtils.getTicketFromInput(project);
		}
	}

}
