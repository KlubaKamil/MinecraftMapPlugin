package com.czachodym.minecraft.frame.frameplugin.listeners;

import com.czachodym.minecraft.frame.frameplugin.model.SpecialFrame;
import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Data
public class FrameEventsListener implements Listener {

    private boolean markFrameWithNextInteraction = false;
    private boolean markFrameAsEnabled = false;
    private Map<UUID, SpecialFrame> specialFrames;
    private ArrayList<ItemFrame> specialMapFrames = new ArrayList<>();
    private Economy economy;
    private Logger log;

    public FrameEventsListener(Logger log, Map<UUID, SpecialFrame> specialFrames){
        this.log = log;
        this.specialFrames = specialFrames;
    }

    @EventHandler
    public void onHanging(HangingBreakEvent e){
        UUID entityUuid = e.getEntity().getUniqueId();
        if(e.getCause() == HangingBreakEvent.RemoveCause.ENTITY && specialFrames.containsKey(entityUuid)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){
        UUID entityUuid = e.getEntity().getUniqueId();
        if(specialFrames.containsKey(entityUuid)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFrameInteraction(PlayerInteractEntityEvent e) {
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
        specialFrames.put(i.getUniqueId(), new SpecialFrame(itemInFrame.clone(), itemInHand.clone()));
        p.sendMessage("Frame marked. Item to get: " + itemInFrame.getType() + ", cost: " + itemInHand.getType());
    }

    private void unmarkFrame(Player p, ItemFrame i){
        if(specialFrames.remove(i.getUniqueId()) != null){
            p.sendMessage("Frame unmarked.");
        } else {
            p.sendMessage("This frame was not marked. Action cancelled.");
        }
    }

    private void checkIfToGiveAnItemToPlayerAndGive(PlayerInteractEntityEvent e, Player p, ItemFrame i){
        SpecialFrame sf;
        if((sf = specialFrames.get(i.getUniqueId())) != null){
            e.setCancelled(true);
            PlayerInventory inventory = p.getInventory();
            ItemStack itemInHand = p.getInventory().getItemInMainHand();
            int costAmount = sf.getItemCost().getAmount();
            Material costType = sf.getItemCost().getType();
            if(costType == Material.AIR && inventory.firstEmpty() != -1){
                inventory.setItem(inventory.firstEmpty(), new ItemStack(sf.getItemInFrame()));
            } else if(costType != Material.AIR && itemInHand.getType() == costType
                    && itemInHand.getAmount() >= costAmount && inventory.firstEmpty() != -1){
                itemInHand.setAmount(itemInHand.getAmount() - costAmount);
                inventory.setItem(inventory.firstEmpty(), new ItemStack(sf.getItemInFrame()));
            } else {
                p.sendMessage("To get this item, you have to hold " + costAmount + " of " + costType.name()
                        + " in a hand and have at least one free slot in equipment");
            }
        }
    }

}
