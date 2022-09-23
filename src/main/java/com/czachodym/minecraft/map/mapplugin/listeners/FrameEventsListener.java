package com.czachodym.minecraft.map.mapplugin.listeners;

import com.czachodym.minecraft.map.mapplugin.model.SpecialFrame;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

@Data
public class FrameEventsListener implements Listener {

    private boolean markFrameWithNextInteraction = false;
    private boolean markFrameAsEnabled = false;
    private List<SpecialFrame> specialFrames = new ArrayList<>();
    private ArrayList<ItemFrame> specialMapFrames = new ArrayList<>();

    public FrameEventsListener(){
    }

    @EventHandler
    public void onFrameInteraction(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked() instanceof ItemFrame i) {
            if (markFrameWithNextInteraction) {
                e.setCancelled(true);
                if (markFrameAsEnabled) {
                    markFrame(p, i);
                } else {
                    unmarkFrame(p, i);
                }
                markFrameWithNextInteraction = false;
            } else {
                checkIfToGiveAnItemToPlayerAndGive(e, p, i);
            }
        } else if (markFrameWithNextInteraction) {
            p.sendMessage("Interacted with something different than frame. Action cancelled");
            markFrameWithNextInteraction = false;
        }
    }

    private void markFrame(Player p, ItemFrame i){
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        ItemStack itemInFrame = i.getItem();
        specialFrames.add(new SpecialFrame(i, itemInFrame.getType(), itemInHand.getType()));
        p.sendMessage("Frame marked. Item to get: " + itemInFrame.getType() + ", cost: " + itemInHand.getType());
    }

    private void unmarkFrame(Player p, ItemFrame i){
        for(SpecialFrame sf: specialFrames){
            if(sf.getFrame().getUniqueId() == i.getUniqueId()){
                specialFrames.remove(sf);
                p.sendMessage("Frame unmarked.");
                return;
            }
        }
        p.sendMessage("This frame was not marked. Action cancelled.");
    }

    private void checkIfToGiveAnItemToPlayerAndGive(PlayerInteractEntityEvent e, Player p, ItemFrame i){
        specialFrames.forEach(sf -> {
            if(sf.getFrame().getUniqueId() == i.getUniqueId()){
                e.setCancelled(true);
                PlayerInventory inventory = p.getInventory();
                ItemStack itemInHand = p.getInventory().getItemInMainHand();
                if(sf.getItemCost() == Material.AIR && inventory.firstEmpty() != -1){
                    inventory.setItem(inventory.firstEmpty(), new ItemStack(sf.getItemInFrame()));
                } else if(sf.getItemCost() != Material.AIR && itemInHand.getType() == sf.getItemCost()
                        && inventory.firstEmpty() != -1){
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    inventory.setItem(inventory.firstEmpty(), new ItemStack(sf.getItemInFrame()));
                } else {
                    p.sendMessage("To get this item, you have to hold " + sf.getItemCost().name()
                            + " in a hand");
                }
            }
        });
    }

}
