package plugin.wrapper;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;
import plugin.gui.ConfigPanel;

import javax.swing.*;

/**
 * Created by bdanglot on 9/21/16.
 */
public class ConfigWrapper extends DialogWrapper {

	private static final JComponent panel = new ConfigPanel();

	public ConfigWrapper() {
		super(false);
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return panel;
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
