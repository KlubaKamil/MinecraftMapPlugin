package com.czachodym.minecraft.frame.frameplugin;

import com.czachodym.minecraft.frame.frameplugin.listeners.FrameEventsListener;
import com.czachodym.minecraft.frame.frameplugin.model.SpecialFrame;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class FramePlugin extends JavaPlugin {
    private Logger log;
    private FrameEventsListener frameEventsListener;
    private Economy econ;
    private Map<UUID, SpecialFrame> specialFrames = new HashMap<>();

    @Override
    public void onEnable() {
        log = this.getLogger();
        if (!setupEconomy() ) {
            log.info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            log.info("Vault found. Enabling");
            frameEventsListener.setEconomy(econ);
        }

        frameEventsListener = new FrameEventsListener(log, specialFrames);
        getServer().getPluginManager().registerEvents(frameEventsListener, this);
        loadSpecialFrames();
    }

    @Override
    public void onDisable() {
        saveSpecialFramesToFile();
        this.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player p){
            if(command.getName().equalsIgnoreCase("markFrame")){
                p.sendMessage("Right click an item frame to mark it. Framed item will be given to player, an item that" +
                        " user holds during this action will be a cost. Type /cancelMark to cancel this action.");
                frameEventsListener.setMarkFrameWithNextInteraction(true);
                frameEventsListener.setMarkFrameAsEnabled(true);
            } else if(command.getName().equalsIgnoreCase("cancelMark")){
                p.sendMessage("Action canceled.");
                frameEventsListener.setMarkFrameWithNextInteraction(false);
            } else if(command.getName().equalsIgnoreCase("unmarkFrame")){
                p.sendMessage("Right click an item frame to unmark it.");
                frameEventsListener.setMarkFrameWithNextInteraction(true);
                frameEventsListener.setMarkFrameAsEnabled(false);
            } else if(command.getName().equalsIgnoreCase("frameTest")){
                p.sendMessage("test");
//                frameEventsListener.dupa(p);
                loadSpecialFrames();
            }
        }
        return true;
    }

    private void saveSpecialFramesToFile() {
        try {
            log.info("Saving frames started.");
            Path framesDirPath = Paths.get(this.getDataFolder() + "/frames/");
            if(!Files.exists(framesDirPath)) {
                Files.createDirectories(framesDirPath);
            }
            File framesDir = new File(String.valueOf(framesDirPath));
            for(File frameFile: framesDir.listFiles()) {
                frameFile.delete();
            }
            for(Map.Entry<UUID, SpecialFrame> e : specialFrames.entrySet()){
                File frameFile = new File(this.getDataFolder() + "/frames/" + e.getKey().toString());
                frameFile.createNewFile();

                YamlConfiguration config = YamlConfiguration.loadConfiguration(frameFile);
                config.set("uuid", e.getKey().toString());
                config.set("item_in_frame", e.getValue().getItemInFrame().serialize());
                config.set("item_cost", e.getValue().getItemCost().serialize());

                config.save(frameFile);
            }

            log.info("Saving frames finished. Successfully saved " + specialFrames.size() + " frames");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSpecialFrames(){
        log.info("Loading frames started.");
        Path framesDirPath = Paths.get(this.getDataFolder() + "/frames/");
        if(!Files.exists(framesDirPath)) {
            return;
        }
        File framesDir = new File(String.valueOf(framesDirPath));

        for(File frameFile: framesDir.listFiles()){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(frameFile);
            UUID uuid = UUID.fromString(config.get("uuid") + "");
            Map<String, Object> itemInFrame = ((MemorySection)config.get("item_in_frame")).getValues(true);
            Map<String, Object> itemCost = ((MemorySection)config.get("item_cost")).getValues(true);


            SpecialFrame sf = new SpecialFrame(ItemStack.deserialize(itemInFrame), ItemStack.deserialize(itemCost));
//            sf.setItemInFrame(ItemStack.deserialize(itemInFrame));
//            sf.setItemCost(ItemStack.deserialize(itemCost));

            specialFrames.put(uuid, sf);
        }

        log.info("Loading frames finished. Successfully loaded " + specialFrames.size() + " frames.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
