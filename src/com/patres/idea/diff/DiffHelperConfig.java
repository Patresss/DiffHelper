package com.patres.idea.diff;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.patres.idea.diff.utils.DiffNameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
        name="DiffHelperConfig",
        storages = {
                @Storage("DiffHelperConfig.xml")}
)
public class DiffHelperConfig implements PersistentStateComponent<DiffHelperConfig> {

    public boolean taskNameFromChangelistCheck = false;
    public boolean nickNameFromGitCheck = true;
    public String nickNameFromGit = "";

    public DiffHelperConfig() { }

    @Nullable
    @Override
    public DiffHelperConfig getState() {
        return this;
    }

    @Override
    public void loadState(DiffHelperConfig singleFileExecutionConfig) {
        XmlSerializerUtil.copyBean(singleFileExecutionConfig, this);
    }

    @NotNull
    public static DiffHelperConfig getInstance(Project project) {
        return ServiceManager.getService(project, DiffHelperConfig.class);
    }

    public boolean isTaskNameFromChangelistCheck() {
        return taskNameFromChangelistCheck;
    }

    public void setTaskNameFromChangelistCheck(boolean taskNameFromChangelistCheck) {
        this.taskNameFromChangelistCheck = taskNameFromChangelistCheck;
    }

    public boolean isNickNameFromGitCheck() {
        return nickNameFromGitCheck;
    }

    public void setNickNameFromGitCheck(boolean nickNameFromGitCheck) {
        this.nickNameFromGitCheck = nickNameFromGitCheck;
    }

    public String getNickNameFromGit() {
        return nickNameFromGitCheck ? DiffNameUtils.getGitUsername() : nickNameFromGit;
    }

    public void setNickNameFromGit(String nickNameFromGit) {
        this.nickNameFromGit = nickNameFromGit;
    }

    public String getTaskName(Project project) {
        return taskNameFromChangelistCheck ? DiffNameUtils.getTicket(project) : nickNameFromGit; //TODO
    }
}