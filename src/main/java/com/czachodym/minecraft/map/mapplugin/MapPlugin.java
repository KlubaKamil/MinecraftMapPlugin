package com.czachodym.minecraft.map.mapplugin;

import com.czachodym.minecraft.map.mapplugin.listeners.FrameEventsListener;
import com.czachodym.minecraft.map.mapplugin.model.SpecialFrame;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class MapPlugin extends JavaPlugin {
    private FrameEventsListener frameEventsListener = new FrameEventsListener();

    @Override
    public void onEnable() {
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
                frameEventsListener.setMarkFrameWithNextInteraction(true);
                frameEventsListener.setMarkFrameAsEnabled(true);
            } else if(command.getName().equalsIgnoreCase("cancelMark")){
                frameEventsListener.setMarkFrameWithNextInteraction(false);
            } else if(command.getName().equalsIgnoreCase("unmarkFrame")){
                frameEventsListener.setMarkFrameWithNextInteraction(true);
                frameEventsListener.setMarkFrameAsEnabled(false);
            }
        }
        return true;
    }

    private void saveSpecialFramesToFile(){
        System.out.println("Saving frames started.");
        List<SpecialFrame> frames = frameEventsListener.getSpecialFrames();
        FileConfiguration config = getConfig();
        config.set("special-frames.uuid", frames.stream()
                .map(sf -> sf.getFrame().getUniqueId().toString())
                .collect(Collectors.toList()));
        config.set("special-frames.item", frames.stream()
                .map(sf -> sf.getItemInFrame().name())
                .collect(Collectors.toList()));
        config.set("special-frames.cost", frames.stream()
                .map(sf -> sf.getItemCost().name())
                .collect(Collectors.toList()));
        System.out.println("Saving frames finished. Successfully saved " + frames.size() + " frames");
    }

    private void loadSpecialFrames(){
        System.out.println("Loading frames started.");
        FileConfiguration config = getConfig();
        List<SpecialFrame> specialFrames = frameEventsListener.getSpecialFrames();
        List<String> frames = (List<String>) config.getList("special-frames.uuid");
        List<String> items = (List<String>) config.getList("special-frames.item");
        List<String> costs = (List<String>) config.getList("special-frames.cost");
        if(frames == null || items == null || costs == null){
            System.out.println("Cannot find properties in config.yml. Is this the first run?");
        } else {
            for (int i = 0; i < frames.size(); i++) {
                ItemFrame frame = (ItemFrame) this.getServer().getEntity(UUID.fromString(frames.get(i)));
                Material item = Material.getMaterial(items.get(i));
                Material cost = Material.getMaterial(costs.get(i));
                specialFrames.add(new SpecialFrame(frame, item, cost));
            }
            System.out.println("Loading frames finished. Successfully loaded " + frames.size() + " frames.");
        }
    }
}
