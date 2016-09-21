package plugin.wrapper;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.action.NoPolAction;
import plugin.gui.ConfigPanel;

import javax.swing.*;

public class ConfigWrapper extends DialogWrapper {

	private AnActionEvent event;

	private static final JComponent panel = new ConfigPanel();

	public ConfigWrapper(AnActionEvent event) {
		super(true);
		this.event = event;
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return panel;
	}

	@NotNull
	@Override
	protected Action[] createActions() {
		Action[] defaultActions = super.createActions();
		Action[] actions = new Action[defaultActions.length + 1];
		actions[0] = new NoPolAction(this, this.event);
		System.arraycopy(defaultActions, 0, actions, 1, defaultActions.length);
		return actions;
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