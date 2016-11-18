package plugin.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bdanglot on 9/12/16.
 */
public class ActorManager {

	private static ActorSystem system;
	public static ActorRef remoteActor;

	private static String actorSystemNopol;
	public static String addressNopol;
	public static String portNopol;
	public static String nameActorNopol;

	public static Config akkaConfig;
	public static Config akkaConfigNoPol;

	public static boolean runNopolLocally;
	public static boolean nopolIsRunning = false;

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
			final String pathToNopolJar = new File(ActorManager.class.getResource("/lib/nopol-0.2-SNAPSHOT-allinone.jar").getPath()).getCanonicalPath();
			final String pathToToolsJar = new File(ActorManager.class.getResource("/lib/tools.jar").getPath()).getCanonicalPath();
			final String fullQualifiedNameMain = "fr.inria.lille.repair.actor.NoPolActor";
			final String cmd = "java -cp " + pathToToolsJar + ":" + pathToNopolJar + " " + fullQualifiedNameMain;
			final Process nopolProcess = Runtime.getRuntime().exec(cmd);
			nopolIsRunning = true;
			new Thread() {
				@Override
				public void run() {
					System.out.println("Running Thread");
					final InputStream output = nopolProcess.getInputStream();
					final InputStream errorStream = nopolProcess.getErrorStream();
					int read;
					try {
						while (nopolIsRunning) {
							while ((read = output.read()) != -1)
								System.out.print((char) read);
							while ((read = errorStream.read()) != -1)
								System.out.print((char) read);
						}
					} catch (IOException ignored) {

					}
				}
			}.start();
			Thread.sleep(5000);
		} catch (Exception ignored) {
		}
	}

}