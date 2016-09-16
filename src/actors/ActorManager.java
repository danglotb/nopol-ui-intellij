package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.util.lang.UrlClassLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by bdanglot on 9/12/16.
 */
public class ActorManager {

    static String cmd = "-s test-projects/src/main/java -c test-projects/target/classes:test-projects/target/test-classes -t nopol_examples.nopol_example_1.NopolExampleTest -p nopol/lib/z3/z3_for_linux";

    private static ActorSystem system;
    public static ActorRef actor;
    public static ActorRef remoteActor;

    public static String actorSystemNopol;
    public static String addressNopol;
    public static String portNopol;
    public static String actorNopol;

    public static Config config;
    public static Config configNopol;

    public static void createActorSystem(ClassLoader classLoader) {
        configNopol = ConfigFactory.load(classLoader, "nopol");
        config = ConfigFactory.load(classLoader, "common");

        actorSystemNopol = configNopol.getString("nopol.system.name");
        addressNopol = configNopol.getString("akka.remote.netty.tcp.hostname");
        portNopol = configNopol.getString("akka.remote.netty.tcp.port");
        actorNopol = configNopol.getString("nopol.actor.name");

        system = ActorSystem.create("PluginActorSystem", config, classLoader);
        remoteActor = system.actorFor("akka.tcp://" + actorSystemNopol + "@" + addressNopol + ":" + portNopol + "/user/" + actorNopol);
        System.out.println(remoteActor);
        actor = system.actorOf(Props.create(PluginActor.class), "PluginActor");
    }

}
