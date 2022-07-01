package com.czachodym.minecraft.map.mapplugin;

import com.czachodym.minecraft.map.mapplugin.model.SpecialFrame;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class MapPlugin extends JavaPlugin implements Listener {
    private boolean markFrameWithNextInteraction = false;
    private boolean markFrameAsEnabled = false;
    private ArrayList<SpecialFrame> specialFrames = new ArrayList<>();
    private ArrayList<ItemFrame> specialMapFrames = new ArrayList<>();

    @Override
    public void onEnable() {
//        getServer().getPluginManager().registerEvent(new MyPlugin(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player p){
            if(command.getName().equalsIgnoreCase("markFrame")){
                markFrameWithNextInteraction = true;
                markFrameAsEnabled = true;
            } else if(command.getName().equalsIgnoreCase("cancelMark")){
                markFrameWithNextInteraction = false;
            } else if(command.getName().equalsIgnoreCase("unmarkFrame")){
                markFrameWithNextInteraction = true;
                markFrameAsEnabled = false;
            }
        }
        return true;
    }

    @EventHandler
    public void onFrameInteraction(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked() instanceof ItemFrame i) {
            e.setCancelled(true);
            if (markFrameWithNextInteraction) {
                if (markFrameAsEnabled) {
                    ItemStack itemInHand = p.getInventory().getItemInMainHand();
                    ItemStack itemInFrame = i.getItem();
                    specialFrames.add(new SpecialFrame(i, itemInFrame, itemInHand));
                    p.sendMessage("Frame marked. Item to get: " + itemInFrame.getType() + ", cost: " + itemInHand.getType());
                } else {
                    final boolean[] found = {false};
                    specialFrames.forEach(sf -> {
                        if (sf.getFrame().getEntityId() == i.getEntityId()) {
                            specialFrames.remove(sf);
                            p.sendMessage("Frame unmarked.");
                            found[0] = true;
                        }
                    });
                    if (!found[0]) {
                        p.sendMessage("This frame was not marked. Action cancelled.");
                    }
                }
                markFrameWithNextInteraction = false;
            } else {
                specialFrames.forEach(sf -> {
                    if(sf.getFrame().getEntityId() == i.getEntityId()){
                        PlayerInventory inventory = p.getInventory();
                        ItemStack itemInHand = p.getInventory().getItemInMainHand();
                        if(sf.getItemCost().getType() == Material.AIR && inventory.firstEmpty() != -1){
                            inventory.setItem(inventory.firstEmpty(), sf.getItemInFrame());
                        } else if(sf.getItemCost().getType() != Material.AIR && itemInHand.getType() == sf.getItemCost().getType()
                                && inventory.firstEmpty() != -1){
                            itemInHand.setAmount(itemInHand.getAmount() - 1);
                            inventory.setItem(inventory.firstEmpty(), sf.getItemInFrame());
                        }
                    }
                });
            }
        } else if (markFrameWithNextInteraction) {
            p.sendMessage("Interacted with something different than frame. Action cancelled");
            markFrameWithNextInteraction = false;
        }
    }


//    @EventHandler
//    public void onBlockxD(PlayerInteractEntityEvent e){
//        e.getPlayer().sendMessage("INTERACT " + e.getRightClicked().getType());
//
//        if(e.getRightClicked() instanceof ItemFrame i// && i.getItem().getType() == Material.FILLED_MAP
//                && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE) {
//            specialMapFrames.add(i);
//            e.getPlayer().sendMessage("Dodano do listy ramkę: " + i);
//        } else if(e.getRightClicked() instanceof ItemFrame i// && i.getItem().getType() == Material.FILLED_MAP
//            && specialMapFrames.contains(i)){
//            e.getPlayer().sendMessage("Interact2");
//            e.setCancelled(true);
//            PlayerInventory pi = e.getPlayer().getInventory();
//            if (//pi.getItemInMainHand().getType() == Material.MAP &&
//                    pi.firstEmpty() != -1){
//                ItemStack itemInHand = pi.getItemInMainHand();
//                itemInHand.setAmount(itemInHand.getAmount() - 1);
//                pi.setItem(pi.firstEmpty(), i.getItem());
//            }
//        } else {
//            e.getPlayer().sendMessage("Interact3");
//        }
//    }

//    @EventHandler
//    public void onBlockxD(EntityDamageByEntityEvent e){
//        e.getEntity().sendMessage("EntityDamageByEntityEvent");
//        e.getDamager().sendMessage("EntityDamageByEntityEvent " + e.getEntity());
//        if(e.getEntity() instanceof ItemFrame i){
////            System.out.println(specialMapFrames.remove(i));
//        }
//            System.out.println(specialFrames.remove(e.getEntity()));
//        System.out.println((e.getEntity() instanceof ItemFrame) + " " + (e.getDamager() instanceof Player p));
//        if(e.getEntity() instanceof ItemFrame i && i.getItem().getType() == Material.FILLED_MAP
//                && e.getDamager() instanceof Player p){
//            PlayerInventory pi = p.getInventory();
//            if(pi.getItemInMainHand().getType() == Material.MAP && pi.firstEmpty() != -1){
//                e.setCancelled(true);
//                ItemStack itemInHand = pi.getItemInMainHand();
//                itemInHand.setAmount(itemInHand.getAmount() - 1);
//                pi.setItem(pi.firstEmpty(), i.getItem());
//            }
//        }

}
