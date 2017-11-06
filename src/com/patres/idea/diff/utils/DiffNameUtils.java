package com.patres.idea.diff.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.patres.idea.diff.DiffHelperBundle;
import com.patres.idea.diff.config.DiffHelperConfig;
import com.patres.idea.diff.validator.NotEmptyValidator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiffNameUtils {

    private final static String DEFAULT_USER = "Unknown-user";
    private final static String DEFAULT_TASK = "BM-NULL";
    private final static String FORMAT_DATE = "yyyyMMddHHmm";
    private final static InputValidator INPUT_VALIDATOR = new NotEmptyValidator();


    private DiffNameUtils() {
    }

    public static String getFileName(Project project, String taskName) {
        return String.format("%s_%s_%s.sql", getFormattedDataTime(), getNick(project), taskName);
    }

    public static String getFormattedDataTime() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE);
        return date.format(formatter);
    }


    public static String getNick(Project project) {
        return DiffHelperConfig.getInstance(project).isNickNameFromGitCheck() ? getGitUsername() : DiffHelperConfig.getInstance(project).getNickNameFromGit();
    }

    public static String getGitUsername() {
        try {
            Process process = Runtime.getRuntime().exec("git config user.name");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            return DEFAULT_USER;
        }
    }

    public static String getTicketFromInput(Project project) {
        String defaultListName = ChangeListManager.getInstance(project).getDefaultListName();
        return Messages.showInputDialog(DiffHelperBundle.message("action.taskName"), DiffHelperBundle.message("action.createNewDiff"), Messages.getQuestionIcon(), defaultListName, INPUT_VALIDATOR);
    }

    public static String getTicketFromChangelist(Project project) {
        return ChangeListManager.getInstance(project).getDefaultListName();
    }
}
