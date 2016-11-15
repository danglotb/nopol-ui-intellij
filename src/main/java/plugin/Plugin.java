package plugin;

import fr.inria.lille.repair.common.synth.StatementType;
import plugin.actors.ActorManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.lille.repair.common.config.Config;
import plugin.wrapper.LauncherWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by bdanglot on 9/15/16.
 */
public class Plugin extends AnAction {

    private static final String CONFIG_PATHNAME = "config.properties";

    public static final Properties properties = new Properties();

    public static boolean enableFancyRobot = true;

    public static final Config config = new Config();

    public Plugin() {
        super("NoPol");
        ActorManager.createActorSystem(getClass().getClassLoader());
        try {
            properties.load(new FileInputStream(new File(Plugin.class.getClassLoader().getResource(CONFIG_PATHNAME).toURI())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        initConfig();
        Counter.send(Counter.START);
    }

    private void initConfig() {
        config.setSynthesis(Config.NopolSynthesis.DYNAMOTH);
        config.setType(StatementType.PRE_THEN_COND);
//        config.setLocalizer(Config.NopolLocalizer.OCHIAI); //CoCospoon take too much time
        config.setLocalizer(Config.NopolLocalizer.GZOLTAR);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        LauncherWrapper dialog = new LauncherWrapper(event);
        dialog.getPeer().setTitle("NoPol");
        dialog.show();
    }


}