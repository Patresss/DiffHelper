import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class NewDiffAction extends AnAction {

    private final static String TEMPLATE_NAME = "template.sql";
    private final static String DIFF_TEMPLATE_NAME = "Diff";
    private final static String TEMPLATE_CONTENT = convertStreamToString(NewDiffAction.class.getResourceAsStream("/" + TEMPLATE_NAME));
    private FileTemplate diffTemplate;

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        try {
            updateFileTemplate(project);
            VirtualFile virtualFile = e.getDataContext().getData(PlatformDataKeys.VIRTUAL_FILE);
            virtualFile = getFile(virtualFile);
            copyTemplate(project, virtualFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void updateFileTemplate(Project project) throws IOException {
        diffTemplate = FileTemplateManager.getInstance(project).getTemplate(DIFF_TEMPLATE_NAME);
        if (diffTemplate == null) {
            diffTemplate = FileTemplateManager.getInstance(project).addTemplate(DIFF_TEMPLATE_NAME, "sql");
            diffTemplate.setText(TEMPLATE_CONTENT);
        }
    }

    private void copyTemplate(Project project, VirtualFile virtualFile) {
        validVirtualFile(virtualFile);
        String fileName = DiffNameUtils.getFileName(project);
        Path targetPath = Paths.get(virtualFile.getCanonicalPath(), fileName);
        if (targetPath.toFile().exists()) {
            Messages.showMessageDialog(project, "File " + targetPath.toString() + " exists in folder", "File Exists", Messages.getWarningIcon());
        } else {
            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(virtualFile);
            CreateFileFromTemplateAction.createFileFromTemplate(fileName, diffTemplate, directory, "", true);
        }
    }

    private void validVirtualFile(VirtualFile virtualFile) throws IllegalArgumentException {
        if (virtualFile == null || virtualFile.getCanonicalPath() == null) {
            throw new IllegalArgumentException("Cannot find directory path");
        }
    }

    private VirtualFile getFile(VirtualFile file) {
        if (file != null && !file.isDirectory()) {
            file = file.getParent();
        }
        return file;
    }

}
