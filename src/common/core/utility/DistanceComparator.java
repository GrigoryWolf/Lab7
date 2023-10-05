package common.core.utility;

import common.core.model.Route;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Route> {

    @Override
    public int compare(Route o1, Route o2) {
        return (int) (o2.getDistance()-o1.getDistance());
    }
}
