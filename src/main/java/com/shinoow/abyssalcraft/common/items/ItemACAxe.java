/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2016 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.items;

import com.shinoow.abyssalcraft.AbyssalCraft;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemACAxe extends ItemAxe {

	private EnumChatFormatting format;

	public ItemACAxe(ToolMaterial mat, String name, int harvestlevel){
		this(mat, name, harvestlevel, null);
	}

	public ItemACAxe(ToolMaterial mat, String name, int harvestlevel, EnumChatFormatting format) {
		super(mat);
		setCreativeTab(AbyssalCraft.tabTools);
		setHarvestLevel("axe", harvestlevel);
		setUnlocalizedName(name);
		setTextureName(AbyssalCraft.modid + ":" + name);
		this.format = format;
	}

	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) {

		return format != null ? format + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name") : super.getItemStackDisplayName(par1ItemStack);
	}
}
