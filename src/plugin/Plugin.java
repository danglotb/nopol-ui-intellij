package plugin;

import plugin.actors.ActorManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.lille.repair.common.config.Config;
import plugin.wrapper.LauncherWrapper;

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
		LauncherWrapper dialog = new LauncherWrapper(event);
		dialog.getPeer().setTitle("NoPol");
		dialog.show();
	}

}
