/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2017 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.shinoow.abyssalcraft.common.blocks.tile.TileEntityMaterializer;

public class ContainerMaterializer extends Container {

	private TileEntityMaterializer tileMaterializer;

	public ContainerMaterializer(InventoryPlayer par1InventoryPlayer, TileEntityMaterializer par2TileEntityMaterializer)
	{
		tileMaterializer = par2TileEntityMaterializer;
		addSlotToContainer(new SlotCrystalBag(par2TileEntityMaterializer, 0, 14, 17));
		addSlotToContainer(new SlotNecronomicon(par2TileEntityMaterializer, 1, 14, 53));
		int i;

		for(i = 0; i < 3; ++i)
			for(int j = 0; j < 6; ++j)
				addSlotToContainer(new SlotMaterializer(par1InventoryPlayer.player, par2TileEntityMaterializer, 2 + j + i * 6, 44 + j * 17, 17 + i * 17));

		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
	}

	@Override
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2){}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return tileMaterializer.isUsableByPlayer(par1EntityPlayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 == 2)
			{
				if (!mergeItemStack(itemstack1, 3, 39, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (par2 != 1 && par2 != 0)
			{
				//				if (MaterializerRecipes.instance().getMaterializationResult(itemstack1) != null)
				//				{
				//					if (!mergeItemStack(itemstack1, 0, 1, false))
				//						return null;
				//				}
				if (par2 >= 3 && par2 < 30)
				{
					if (!mergeItemStack(itemstack1, 30, 39, false))
						return ItemStack.EMPTY;
				}
				else if (par2 >= 30 && par2 < 39 && !mergeItemStack(itemstack1, 3, 30, false))
					return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(itemstack1, 3, 39, false))
				return ItemStack.EMPTY;

			if (itemstack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}
}
