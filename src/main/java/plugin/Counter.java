package plugin;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bdanglot on 11/10/16.
 */
public class Counter {

	public static final String START = "start";
	public static final String ATTEMPT ="repair-attempt";
	public static final String SUCCESS ="successful-repair";
	public static final String TOY_PROJECT ="toy-project";

	public static void send(String verb) {
		String address_server = String.valueOf(Plugin.properties.get("address_server")) + "/" + verb;
		try {
			URL url = new URL(address_server);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.getInputStream();
		} catch (Exception ignored) {
			System.err.println("Unable to send the request count " + verb + " to " + address_server);
		}
	}

}
