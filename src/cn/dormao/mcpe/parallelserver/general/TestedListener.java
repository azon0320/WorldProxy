package cn.dormao.mcpe.parallelserver.general;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.io.PrintStream;

@Deprecated //Just for test
public class TestedListener implements Listener {
    static String writeEvent(BlockEvent e){
        return e.getEventName() + " Block" + writeState(e.getBlock());
    }
    static String writeNewStateEvent(BlockGrowEvent e){
        return writeNewStateEvent(e,e.getNewState());
    }
    static String writeNewStateEvent(BlockEvent e, BlockState newState){
        return writeEvent(e) + " " + newState.getClass().getSimpleName() + writeState(newState);
    }
    static String writeNewStateEvent(BlockEvent e, Block newState){
        return writeEvent(e) + " " + newState.getClass().getSimpleName() + writeState(newState);
    }
    static String writeState(Block block){
        return String.format("%s[%d,%d]", writeLoc(block.getLocation()), block.getTypeId(), block.getData());
    }
    static String writeState(BlockState state){
        return writeState(state.getBlock());
    }
    static String writeLoc(Location l){return String.format("(%d,%d,%d)", l.getBlockX(),l.getBlockY(),l.getBlockZ());}
    static PrintStream out(){return System.out;}

    @EventHandler
    public void blockbreak(BlockBreakEvent e){ out().println(writeEvent(e) + " [Cancelled:"+e.isCancelled()+"]"); }

    @EventHandler
    public void blockplace(BlockPlaceEvent e){ out().println(writeEvent(e) + " [Cancelled:"+e.isCancelled()+"]"); }

    //@EventHandler
    public void blockspread(BlockSpreadEvent e){ out().println(writeNewStateEvent(e) + " [Cancelled:"+e.isCancelled()+"]"); }

    @EventHandler
    public void blockform(BlockFormEvent e){out().println(writeNewStateEvent(e) + " [Cancelled:"+e.isCancelled()+"]");}

    @EventHandler
    public void blockfade(BlockFadeEvent e){out().println(writeNewStateEvent(e,e.getNewState()) + " [Cancelled:"+e.isCancelled()+"]");}

    @EventHandler
    public void blockburn(BlockBurnEvent e){out().println(writeNewStateEvent(e, e.getIgnitingBlock()) + " [Cancelled:"+e.isCancelled()+"]");}

    @EventHandler
    public void blockcanbuild(BlockCanBuildEvent e){out().println(writeEvent(e));}

    @EventHandler
    public void blockignite(BlockIgniteEvent e){out().println(writeNewStateEvent(e,e.getIgnitingBlock()) + " [Cancelled:"+e.isCancelled()+"]");}

    @Deprecated
    //@EventHandler
    public void blockphysics(BlockPhysicsEvent e){out().println(writeEvent(e) + " NewId : " + e.getChangedTypeId() + " [Cancelled:"+e.isCancelled()+"]");}

    @EventHandler
    public void blockfromto(BlockFromToEvent e){out().println(writeNewStateEvent(e,e.getToBlock()) + " [Cancelled:"+e.isCancelled()+"]");}

    @EventHandler
    public void blockdecay(LeavesDecayEvent e){out().println(writeEvent(e) + " [Cancelled:"+e.isCancelled()+"]");}

    @EventHandler
    public void blockmultiplace(BlockMultiPlaceEvent e){for (BlockState s : e.getReplacedBlockStates()){
        out().println(writeNewStateEvent(e, s) + " [Cancelled:"+e.isCancelled()+"]");
    }}


}
