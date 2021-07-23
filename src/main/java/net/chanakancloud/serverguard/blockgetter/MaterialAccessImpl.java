package net.chanakancloud.serverguard.blockgetter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class MaterialAccessImpl implements MaterialAccess {


    @Override
    public Material getMaterial(Block block) {
        return block.getType();
    }

    @Override
    public Material getMaterial(Location loc) {
        return loc.getBlock().getType();
    }

    @Override
    public Material getMaterial(ItemStack itemStack) {
        return itemStack.getType();
    }

    @Override
    public EntityType getTypeOfEntity(Entity entity) {
        return entity.getType();
    }

    @Override
    public HashSet<Material> nonOccludingMaterials() {
        HashSet<Material> trasparentMaterials = new HashSet<Material>();
        for(Material material : Material.values()) {
            if(!material.isOccluding()) {
                trasparentMaterials.add(material);
            }
        }
        return trasparentMaterials;
    }
}
