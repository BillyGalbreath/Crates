package net.pl3x.bukkit.pl3xcrates.api;

import org.bukkit.util.Vector;

import java.util.ArrayList;

public interface RayTrace {
    Vector getPosition(double blocksAway);

    ArrayList<Vector> traverse(double blocksAway, double accuracy);

    boolean intersects(Vector position, Vector min, Vector max);
}
