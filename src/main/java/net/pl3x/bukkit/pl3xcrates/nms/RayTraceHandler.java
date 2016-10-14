package net.pl3x.bukkit.pl3xcrates.nms;

import net.pl3x.bukkit.pl3xcrates.api.RayTrace;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class RayTraceHandler implements RayTrace {
    private final Vector origin;
    private final Vector direction;

    private RayTraceHandler(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    RayTraceHandler(Location location) {
        this(location.toVector(), location.getDirection().normalize());
    }

    @Override
    public Vector getPosition(double blocksAway) {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }

    @Override
    public ArrayList<Vector> traverse(double blocksAway, double accuracy) {
        ArrayList<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            positions.add(getPosition(d));
        }
        return positions;
    }

    @Override
    public boolean intersects(Vector position, Vector min, Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else if (position.getZ() < min.getZ() || position.getZ() > max.getZ()) {
            return false;
        }
        return true;
    }
}
