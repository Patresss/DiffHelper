import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.rename.RenameProcessor;

import java.util.regex.Pattern;

public class UpdateDiffNameAction extends AnAction {

    private final static Pattern PATTERN = Pattern.compile("^[0-9]{12}_.*_.*\\.sql$");

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        if (isValidateFile(virtualFile)) {
            renameFile(project, virtualFile);
        } else {
            Messages.showMessageDialog(project, "File does not matches to pattern " + PATTERN.pattern(), "File Exists", Messages.getWarningIcon());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(isValidateFile(virtualFile));
    }

    private boolean isValidateFile(VirtualFile virtualFile) {
        return virtualFile != null && !virtualFile.isDirectory() && PATTERN.matcher(virtualFile.getName()).matches();
    }

    private void renameFile(Project project, VirtualFile virtualFile) {
        String newName = DiffNameUtils.getFileName(project);
        if (!virtualFile.getName().equals(newName)) {
            if (virtualFile.getParent() != null && virtualFile.getParent().getCanonicalPath() != null) {
                PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                RenameProcessor renameProcessor = new RenameProcessor(project, file, newName, false, false);
                renameProcessor.doRun();
            } else {
                throw new RuntimeException("Error with file");
            }
        }
    }


}
