package plugin.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by bdanglot on 9/12/16.
 */
public class ActorManager {

    static String cmd = "-s test-projects/src/main/java -c test-projects/target/classes:test-projects/target/test-classes -t nopol_examples.nopol_example_1.NopolExampleTest -p nopol/lib/z3/z3_for_linux";

    private static ActorSystem system;
    public static ActorRef remoteActor;

    private static String actorSystemNopol;
    public static String addressNopol;
    public static String portNopol;
    public static String nameActorNopol;

    public static Config akkaConfig;
    public static Config akkaConfigNoPol;

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
}
