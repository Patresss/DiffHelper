package com.patres.idea.diff.validator;

import com.intellij.openapi.ui.InputValidator;

public class NotEmptyValidator implements InputValidator {

    @Override
    public boolean checkInput(String s) {
        return s != null && !s.isEmpty();
    }

    @Override
    public boolean canClose(String s) {
        return true;
    }
}
