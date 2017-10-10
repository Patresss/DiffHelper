package com.patres.idea.diff.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.patres.idea.diff.DiffHelperBundle;
import com.patres.idea.diff.DiffHelperConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiffNameUtils {

    private final static String DEFAULT_USER = "Unknown-user";
    private final static String DEFAULT_TASK = "BM-NULL";
    private final static String FORMAT_DATE = "yyyyMMddHHmm";

    private DiffNameUtils() {
    }

    public static String getFileName(Project project) {
        return String.format("%s_%s_%s.sql", getFormattedDataTime(), getNick(project), getTicket(project));
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

    public static String getTicket(Project project) {
        String defaultListName = ChangeListManager.getInstance(project).getDefaultListName();
        if(DiffHelperConfig.getInstance(project).isTaskNameFromChangelistCheck()) {
            return defaultListName;
        } else {
            String inputDialog = Messages.showInputDialog(DiffHelperBundle.message("action.taskName"), DiffHelperBundle.message("action.createNewDiff"), Messages.getQuestionIcon(), defaultListName, null);
            return inputDialog == null || inputDialog.isEmpty() ? DEFAULT_TASK  : inputDialog;
        }
    }
}
