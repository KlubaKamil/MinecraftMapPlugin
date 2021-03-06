package com.czachodym.minecraft.map.mapplugin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class SpecialFrame {
    private ItemFrame frame;
    private ItemStack itemInFrame;
    private ItemStack itemCost;
}
