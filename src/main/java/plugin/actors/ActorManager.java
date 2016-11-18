package plugin.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bdanglot on 9/12/16.
 */
public class ActorManager {

    public static void createActorSystem(ClassLoader classLoader) {
        akkaConfigNoPol = ConfigFactory.load(classLoader, "nopol");
        akkaConfig = ConfigFactory.load(classLoader, "common");

        actorSystemNopol = akkaConfigNoPol.getString("nopol.system.name");
        addressNopol = akkaConfigNoPol.getString("akka.remote.netty.tcp.hostname");
        portNopol = akkaConfigNoPol.getString("akka.remote.netty.tcp.port");
        nameActorNopol = akkaConfigNoPol.getString("nopol.actor.name");

        system = ActorSystem.create("PluginActorSystem", akkaConfig, classLoader);
        remoteActor = system.actorFor("akka.tcp://" + actorSystemNopol + "@" + addressNopol + ":" + portNopol + "/user/" + nameActorNopol);
        System.out.println(remoteActor);
    }

    public static void buildRemoteActor(String address, String port) {
        addressNopol = address;
        portNopol = port;
        remoteActor = system.actorFor("akka.tcp://" + actorSystemNopol + "@" + addressNopol + ":" + portNopol + "/user/" + nameActorNopol);
        System.out.println(remoteActor);
    }

    public static void launchNopol() {
        try {
            final String pathToNopolJar = new File(ActorManager.class.getResource(String.valueOf(Plugin.properties.get("pathToNopolServerJar"))).getPath()).getCanonicalPath();
            final String pathToToolsJar = System.getProperty("java.home") + "/../lib/tools.jar";
            final String fullQualifiedNameMain = String.valueOf(Plugin.properties.get("fullQualifiedOfMainClass"));
            final String cmd = "java -cp " + pathToToolsJar + ":" + pathToNopolJar + " " + fullQualifiedNameMain;
            nopolProcess = Runtime.getRuntime().exec(cmd);
            nopolIsRunning = true;
        } catch (Exception ignored) {
            nopolIsRunning = false;
            //should give to the client the reason that we could not run nopol locally
        }
    }


    public static void stopNopolLocally() {
        if (nopolIsRunning) {
            System.err.println("Stopping nopol locally");
            runNopolLocally = false;
            nopolIsRunning = false;
            nopolProcess.destroy();
            try {
                nopolProcess.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static ActorSystem system;
    public static ActorRef remoteActor;

    private static String actorSystemNopol;
    public static String addressNopol;
    public static String portNopol;
    public static String nameActorNopol;

    public static Config akkaConfig;
    public static Config akkaConfigNoPol;

    public static boolean runNopolLocally = true;
    public static boolean nopolIsRunning = false;
    private static Process nopolProcess;
}