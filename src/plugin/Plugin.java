package plugin;

import plugin.actors.ActorManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import fr.inria.lille.repair.common.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.action.NoPolAction;
import plugin.gui.ConfigPanel;
import plugin.wrapper.ConfigWrapper;

import javax.swing.*;

/**
 * Created by bdanglot on 9/15/16.
 */
public class Plugin extends AnAction {

	public static final Config config = new Config();

	public Plugin() {
		super("NoPol");
		ActorManager.createActorSystem(getClass().getClassLoader());
	}

	@Override
	public void actionPerformed(AnActionEvent event) {
		ConfigWrapper dialog = new ConfigWrapper(event);
		dialog.getPeer().setTitle("NoPol");
		dialog.show();
	}

}
