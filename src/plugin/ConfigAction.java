package plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.gui.ConfigPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by bdanglot on 9/15/16.
 */
public class ConfigAction extends AnAction {

    private MyDialogWrapper myDialogWrapper;

    public ConfigAction() {
        super("Configure NoPol...");
    }

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (myDialogWrapper == null)
            this.myDialogWrapper = new MyDialogWrapper(anActionEvent.getProject());
        this.myDialogWrapper.show();
    }

    private class MyDialogWrapper extends DialogWrapper {

        private ConfigPanel panel;

        protected MyDialogWrapper(@Nullable Project project) {
            super(project);
            this.panel = new ConfigPanel();
            this.init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return this.panel;
        }

        @Override
        protected void doOKAction() {
            //TODO Apply effectively changes
            super.doOKAction();
        }

        @Nullable
        @Override
        protected ValidationInfo doValidate() {
            //TODO Apply effectively changes
            return super.doValidate();
        }

        @Override
        public void doCancelAction() {
            //TODO CancelAllChanges
            super.doCancelAction();
        }


    }
}
