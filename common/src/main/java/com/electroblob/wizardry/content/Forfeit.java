package com.electroblob.wizardry.content;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Comparator;

public abstract class Forfeit {
	// Originally BY_2D_DATA on Direction class
	public static final Direction[] HORIZONTALS = Arrays.stream(Direction.values()).filter((direction)
			-> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(Direction::get2DDataValue))
			.toArray(Direction[]::new);

	private final ResourceLocation name;
	protected final SoundEvent sound;

	public Forfeit(ResourceLocation name){
		this.name = name;
		this.sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(name.getNamespace(), "forfeit." + name.getPath()));
	}

	public abstract void apply(Level world, Player player);

	public Component getMessage(Component implementName){
		return Component.translatable("forfeit." + name.getNamespace() + "." + name.getPath(), implementName);
	}
	
	public Component getMessageForWand(){
		return getMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".wand.generic"));
	}

	public Component getMessageForScroll(){
		return getMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".scroll.generic"));
	}
	
	public SoundEvent getSound(){
		return sound;
	}
}
