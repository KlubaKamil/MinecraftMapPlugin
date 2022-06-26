package com.czachodym.minecraft.map.mapplugin;

import jdk.javadoc.internal.doclint.HtmlTag;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.BlockDamageEvent;

public final class MapPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
//        getServer().getPluginManager().registerEvent(new MyPlugin(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onBlockxD(BlockDamageEvent e){
        if(e.getBlock().getType() == Material.MAP) e.getPlayer().sendMessage("O KUWRA TO TO");
        if(e.getBlock().getType() == Material.FILLED_MAP) e.getPlayer().sendMessage("O KUWRA TO TO JESZCZE BARDZIEJ");
        e.getPlayer().sendMessage("XD");
    }

    @EventHandler
    public void onBlockxD(PlayerInteractEntityEvent e){
        e.getPlayer().sendMessage("INTERACT " + e.getPlayer().getLocale());
    }

    @EventHandler
    public void onBlockxD(EntityDamageByEntityEvent e){
        e.setCancelled(true);
        System.out.println(e.getEntityType() + " " + e.getEntity());
        ItemFrame frame = (ItemFrame) e.getEntity();
        System.out.println(frame.getItem());
    }
}
