package net.hokiegeek.android.dondeestas.data;

import java.util.Vector;

/**
 * Created by andres on 11/23/16.
 */

public class Model {

    private Vector<UpdateListener> listerners;

    private Model() {}

    public Model newInstance() {
        return new Model();
    }

    void addListener(UpdateListener l) {
       if (!listerners.contains(l)) {
           listerners.add(l);
       }
    }

    boolean removeListener(UpdateListener l) {
        if (listerners.contains(l)) {
            listerners.remove(l);
            return true;
        }
        return false;
    }

    public interface UpdateListener {
        void onModelUpdate();
    }
}
