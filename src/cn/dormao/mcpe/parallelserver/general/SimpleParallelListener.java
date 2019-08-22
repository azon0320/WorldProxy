package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.Parallel;
import cn.dormao.mcpe.parallelserver.ParallelChannel;
import cn.dormao.mcpe.parallelserver.ParallelWorld;
import cn.dormao.mcpe.parallelserver.protocol.WorldSetBlockPacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import static cn.dormao.mcpe.parallelserver.ParallelUtil.*;

public class SimpleParallelListener implements Listener {
    private Parallel main;

    public void blockset(BlockEvent event){blockset(event,false);}
    public void blockset(BlockEvent event, boolean isBreak){
        int[] ids = isBreak ? new int[]{0,0} : bukkitGetBlockValue(event.getBlock());
        blocksetraw(event.getBlock().getWorld(), event.getBlock().getLocation(),ids[0],ids[1], !isBreak && event.getBlock().getType().hasGravity());
    }
    public void blockset(Block block){
        int[] ids = bukkitGetBlockValue(block);
        blocksetraw(block.getWorld(),block.getLocation(),ids[0],ids[1], block.getType().hasGravity());
    }
    public void blockset(BlockState block){ blockset(block.getBlock()); }
    public void blockclear(Location loc){blocksetraw(loc.getWorld(),loc,0,0);}
    public void blockset(Location loc, int typeId){blocksetraw(loc.getWorld(),loc,typeId,0, Material.getMaterial(typeId).hasGravity());}
    public void blocksetraw(World world,Location loc, int id, int meta){
        blocksetraw(world,loc,id,meta,false);
    }
    public void blocksetraw(World world,Location loc, int id, int meta, boolean falling){
        WorldSetBlockPacket pk = new WorldSetBlockPacket();
        pk.pos = vec3FromLocation(loc, true);
        pk.id = id;pk.meta = meta;
        pk.fallingblock = falling;
        for (ParallelChannel channel : parallel().getChannelsByWorld(world.getName())){
            channel.sendPacket(pk);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockbreak(BlockBreakEvent event){
        blockset(event,true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockplace(BlockPlaceEvent event){
        blockset(event);
        event.getPlayer().sendMessage(String.format("Item Hand  [%d:%d]", event.getBlock().getTypeId(),event.getBlock().getData() & 0xFF));
    }

    //@EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockspread(BlockSpreadEvent event){
        blockset(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockform(BlockFormEvent event){blockset(event.getBlock());}

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockfade(BlockFadeEvent event){blockset(event,true);}

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockburn(BlockBurnEvent event){blockset(event,true);}

    //意义不明@EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockcanbuild(BlockCanBuildEvent event){if (!event.isBuildable()){blockclear(event.getBlock().getLocation());}}

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockignite(BlockIgniteEvent event){blockset(event.getIgnitingBlock());}

    @Deprecated
    //@EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockphysics(BlockPhysicsEvent event){blockset(event);}

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockfromto(BlockFromToEvent event){blockset(event.getToBlock());}

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockdecay(LeavesDecayEvent event){blockset(event,true);}

    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void blockmultiplace(BlockMultiPlaceEvent event){for (BlockState s : event.getReplacedBlockStates()){blockset(s);}}


    @EventHandler(priority = EventPriority.HIGHEST)
    public void chunkload(ChunkLoadEvent e){
        ParallelWorld pworld = parallel().getParallelWorld(e.getWorld().getName());
        if (pworld != null){
            pworld.loadChunkCallback(e.getChunk());
        }
    }

    @EventHandler
    public void itemheld(PlayerItemHeldEvent e){
        int id = e.getPlayer().getInventory().getItemInMainHand().getTypeId();
        int meta = e.getPlayer().getInventory().getItemInMainHand().getData().getData() & 0xFF;
    }

    public SimpleParallelListener(Parallel control){ main = control; }
    public boolean checkParallelWorld(World w){
        return parallel().getParallelWorld(w.getName()) != null;
    }
    public Parallel parallel() {
        return main;
    }
}
