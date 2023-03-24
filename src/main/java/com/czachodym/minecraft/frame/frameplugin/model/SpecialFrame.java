package com.czachodym.minecraft.frame.frameplugin.model;

import lombok.*;
import org.bukkit.inventory.ItemStack;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SpecialFrame {
    private ItemStack itemInFrame;
    private ItemStack itemCost;
}
