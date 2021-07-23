package net.chanakancloud.serverguard.blockgetter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public interface MaterialAccess {
    Material getMaterial(Block block);

    Material getMaterial(Location loc);

    Material getMaterial(ItemStack itemStack);

    EntityType getTypeOfEntity(Entity entity);

    HashSet<Material> nonOccludingMaterials();
}
