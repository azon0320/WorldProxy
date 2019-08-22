package mcpe.worldproxy.pc.wasted;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.*;

@SuppressWarnings("all")
public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        File f = new File(getServer().getWorldContainer(), "default");
        out.println("World default : " + (f.isDirectory() ? "found" : "not found"));

        World w = getServer().getWorld("defaultworldse");
        out.println("defaultworldse World : " + (w == null ? "NULL" : "SUCC"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        e.getPlayer().teleport(getServer().getWorld("default").getSpawnLocation());
    }

    public Chunk getChunk(String levelname, int cx, int cz) {
        World w = getServer().getWorld(levelname);
        return w.getChunkAt(cx, cz);
    }

    public Chunk getChunkXZ(String levelname, int x, int z) {
        World w = getServer().getWorld(levelname);
        Chunk c = null;
        if (w != null) {
            Chunk[] cs = w.getLoadedChunks();
            for (Chunk cc : cs) {
                if (cc.getX() == x && cc.getZ() == z) {
                    c = cc;
                    break;
                }
            }
        }
        return c;
    }

    public Chunk getChunkXZ(World w, int x, int z) {
        return getChunkXZ(w.getWorldFolder().getName(), x, z);
    }

    public void writeChunk(Chunk c) {
        List<String> list = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < c.getWorld().getMaxHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    String s = "Block(%x,%y,%z) = %b";
                    s = s.replace("%x", String.valueOf(x)).replace("%y", String.valueOf(y)).replace("%z", String.valueOf(z));
                    Block b = c.getBlock(x, y, z);
                    s = s.replace("%b", b.getType().name());
                    list.add(s);
                }
            }
        }
        String fp = "D:\\mcpc\\chunk(%x,%z).txt".replace("%x", String.valueOf(c.getX())).replace("%z", String.valueOf(c.getZ()));
        File f = new File(fp);
        FileOutputStream stream = null;
        try {
            f.createNewFile();
            StringBuffer sb = new StringBuffer();
            for (String n : list) {
                sb.append(n).append("\n");
            }
            stream = new FileOutputStream(f);
            stream.write(sb.toString().getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ee) {
                }
            }
        }
    }

    public int[] getBlockValue(Block block){
        int id = block.getType().getId();
        int meta = block.getData();
        return new int[]{id,meta};
    }
}
