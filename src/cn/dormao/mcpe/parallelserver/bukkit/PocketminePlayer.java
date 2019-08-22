package cn.dormao.mcpe.parallelserver.bukkit;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.Map;

public class PocketminePlayer extends CraftPlayer {
    public PocketminePlayer(CraftServer server, EntityPlayer entity) {
        super(server, entity);
    }
}
