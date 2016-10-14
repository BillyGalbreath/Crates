package net.pl3x.bukkit.pl3xcrates.nms;

import net.pl3x.bukkit.pl3xcrates.api.BoundingBox;
import net.pl3x.bukkit.pl3xcrates.api.LineOfSight;
import net.pl3x.bukkit.pl3xcrates.api.RayTrace;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LineOfSightHandler implements LineOfSight {
    public Block getTargetBlock(Player player, int distance, double accuracy) {
        World world = player.getWorld();
        RayTrace rayTrace = new RayTraceHandler(player.getEyeLocation());
        ArrayList<Vector> positions = rayTrace.traverse(distance, accuracy);
        ArrayList<BlockVector> blockPositions = new ArrayList<>();
        for (Vector pos : positions) {
            BlockVector blockPos = pos.toBlockVector();
            if (!blockPositions.contains(blockPos)) {
                blockPositions.add(blockPos);
            }
        }
        for (BlockVector blockPos : blockPositions) {
            Block block = blockPos.toLocation(world).getBlock();
            if (block.getType() == Material.AIR) {
                continue; // ignore air blocks
            }
            BoundingBox box = new BoundingBoxHandler(block);
            for (Vector position : positions) {
                if (rayTrace.intersects(position, box.getMin(), box.getMax())) {
                    return block; // return first non-air block in line of sight
                }
            }
        }
        return null;
    }
}
