package actors;

import akka.actor.*;
import fr.inria.lille.repair.common.patch.Patch;
import plugin.Plugin;

import java.util.List;

/**
 * Created by bdanglot on 9/12/16.
 */
public class PluginActor extends UntypedActor {

    @Override
    public void onReceive(Object o) {
        System.out.println("Message receive from " + getSender());
        if (o instanceof List && !((List) o).isEmpty() && ((List) o).get(0) instanceof Patch) {
            List<Patch> patches = (List<Patch>) o;
            for (Patch patch : patches) {
                System.out.println(patch);
            }
        } else
            System.out.println(o);
    }

}
