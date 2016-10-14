package net.pl3x.bukkit.pl3xcrates.nms;

import net.minecraft.server.v1_10_R1.AxisAlignedBB;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.WorldServer;
import net.pl3x.bukkit.pl3xcrates.api.BoundingBox;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.util.Vector;

public class BoundingBoxHandler implements BoundingBox {
    private final Vector min;
    private final Vector max;

    BoundingBoxHandler(Block block) {
        BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        WorldServer world = ((CraftWorld) block.getWorld()).getHandle();
        AxisAlignedBB box = world.getType(pos).c(world, pos);
        min = new Vector(pos.getX() + box.a, pos.getY() + box.b, pos.getZ() + box.c);
        max = new Vector(pos.getX() + box.d, pos.getY() + box.e, pos.getZ() + box.f);
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }
}
