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
package com.shinoow.abyssalcraft.lib.util;

import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;

/**
 * Utility class used for assembling Ritual grounds.
 * @author shinoow
 *
 */
public class RitualUtil {

	private static Map<IBlockState, Integer> ritualBlocks = Maps.newHashMap();
	private static Map<IBlockState, Integer> altarMeta = Maps.newHashMap();

	public static void addBlocks(){
		ritualBlocks.put(Blocks.COBBLESTONE.getDefaultState(), 0);
		ritualBlocks.put(ACBlocks.cobblestone.getStateFromMeta(0), 0);
		ritualBlocks.put(ACBlocks.cobblestone.getStateFromMeta(1), 1);
		ritualBlocks.put(ACBlocks.cobblestone.getStateFromMeta(4), 1);
		ritualBlocks.put(ACBlocks.cobblestone.getStateFromMeta(2), 2);
		ritualBlocks.put(ACBlocks.cobblestone.getStateFromMeta(3), 2);
		ritualBlocks.put(ACBlocks.ethaxium_brick.getDefaultState(), 3);
		ritualBlocks.put(ACBlocks.dark_ethaxium_brick.getDefaultState(), 3);

		altarMeta.put(Blocks.COBBLESTONE.getDefaultState(), 0);
		altarMeta.put(ACBlocks.cobblestone.getStateFromMeta(0), 1);
		altarMeta.put(ACBlocks.cobblestone.getStateFromMeta(1), 2);
		altarMeta.put(ACBlocks.cobblestone.getStateFromMeta(4), 3);
		altarMeta.put(ACBlocks.cobblestone.getStateFromMeta(2), 4);
		altarMeta.put(ACBlocks.cobblestone.getStateFromMeta(3), 5);
		altarMeta.put(ACBlocks.ethaxium_brick.getDefaultState(), 6);
		altarMeta.put(ACBlocks.dark_ethaxium_brick.getDefaultState(), 7);
	}

	/**
	 * Checks if an altar can be created
	 * @param world World object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param bookType Level of the current Necronomicon held
	 * @return True if a Ritual Altar can be constructed, otherwise false
	 */
	public static boolean tryAltar(World world, BlockPos pos, int bookType){
		IBlockState ritualBlock = world.getBlockState(pos);
		int x = 0;
		int y = 0;
		int z = 0;
		if(ritualBlock != null && ritualBlocks.containsKey(ritualBlock))
			if(bookType >= ritualBlocks.get(ritualBlock))
				if(world.getBlockState(pos.add(x -3, y, z)) == ritualBlock &&
				world.getBlockState(pos.add(x, y, z -3)) == ritualBlock &&
				world.getBlockState(pos.add(x + 3, y, z)) == ritualBlock &&
				world.getBlockState(pos.add(x, y, z + 3)) == ritualBlock &&
				world.getBlockState(pos.add(x -2, y,z + 2)) == ritualBlock &&
				world.getBlockState(pos.add(x -2, y, z -2)) == ritualBlock &&
				world.getBlockState(pos.add(x + 2, y, z + 2)) == ritualBlock &&
				world.getBlockState(pos.add(x + 2, y, z -2)) == ritualBlock)
					if(world.isAirBlock(pos.add(x -3, y, z -1)) && world.isAirBlock(pos.add(x -3, y, z + 1)) &&
							world.isAirBlock(pos.add(x -4, y, z)) && world.isAirBlock(pos.add(x -4, y, z -1)) &&
							world.isAirBlock(pos.add(x -4, y, z + 1)) && world.isAirBlock(pos.add(x -3, y, z -2)) &&
							world.isAirBlock(pos.add(x -3, y, z -3)) && world.isAirBlock(pos.add(x -2, y, z -3)) &&
							world.isAirBlock(pos.add(x -1, y, z -3)) && world.isAirBlock(pos.add(x -1, y, z -4)) &&
							world.isAirBlock(pos.add(x, y, z -4)) && world.isAirBlock(pos.add(x + 1, y, z -4)) &&
							world.isAirBlock(pos.add(x + 1, y, z -3)) && world.isAirBlock(pos.add(x + 2, y, z -3)) &&
							world.isAirBlock(pos.add(x + 3, y, z -3)) && world.isAirBlock(pos.add(x + 3, y, z -2)) &&
							world.isAirBlock(pos.add(x + 3, y, z -1)) && world.isAirBlock(pos.add(x + 4, y, z -1)) &&
							world.isAirBlock(pos.add(x + 4, y, z)) && world.isAirBlock(pos.add(x + 4, y, z + 1)) &&
							world.isAirBlock(pos.add(x + 3, y, z + 1)) && world.isAirBlock(pos.add(x + 3, y, z + 2)) &&
							world.isAirBlock(pos.add(x + 3, y, z + 3)) && world.isAirBlock(pos.add(x + 2, y, z + 3)) &&
							world.isAirBlock(pos.add(x + 1, y, z + 3)) && world.isAirBlock(pos.add(x + 1, y, z + 4)) &&
							world.isAirBlock(pos.add(x, y, z + 4)) && world.isAirBlock(pos.add(x -1, y, z + 4)) &&
							world.isAirBlock(pos.add(x -1, y, z + 3)) && world.isAirBlock(pos.add(x -2, y, z + 3)) &&
							world.isAirBlock(pos.add(x-3, y, z + 3)) && world.isAirBlock(pos.add(x -3, y, z + 2)) &&
							world.isAirBlock(pos.add(x-1, y, z + 0)) && world.isAirBlock(pos.add(x + 1, y, z)) &&
							world.isAirBlock(pos.add(x, y, z -1)) && world.isAirBlock(pos.add(x, y, z + 1)) &&
							world.isAirBlock(pos.add(x-1, y, z + 1)) && world.isAirBlock(pos.add(x -2, y, z)) &&
							world.isAirBlock(pos.add(x-2, y, z)) && world.isAirBlock(pos.add(x -2, y, z -1)) &&
							world.isAirBlock(pos.add(x-1, y, z -1)) && world.isAirBlock(pos.add(x -1, y, z -2)) &&
							world.isAirBlock(pos.add(x, y, z -2)) && world.isAirBlock(pos.add(x + 1, y, z -2)) &&
							world.isAirBlock(pos.add(x + 1, y, z -1)) && world.isAirBlock(pos.add(x + 2, y, z -1)) &&
							world.isAirBlock(pos.add(x + 2, y, z)) && world.isAirBlock(pos.add(x + 2, y, z + 1)) &&
							world.isAirBlock(pos.add(x + 1, y, z + 1)) && world.isAirBlock(pos.add(x + 1, y, z + 2)) &&
							world.isAirBlock(pos.add(x, y, z + 2)) && world.isAirBlock(pos.add(x -1, y, z + 2)))
						if(RitualRegistry.instance().sameBookType(world.provider.getDimension(), ritualBlocks.get(ritualBlock))){
							createAltar(world, pos, ritualBlock);
							return true;
						}
		return false;
	}

	/**
	 * Creates the altar
	 * @param world World object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param block Ritual Block
	 */
	private static void createAltar(World world, BlockPos pos, IBlockState block){
		if(altarMeta.containsKey(block)){
			int meta = altarMeta.get(block);
			int x = 0;
			int y = 0;
			int z = 0;

			world.destroyBlock(pos, false);
			world.destroyBlock(pos.add(x -3, y, z), false);
			world.destroyBlock(pos.add(x, y, z -3), false);
			world.destroyBlock(pos.add(x + 3, y, z), false);
			world.destroyBlock(pos.add(x, y, z + 3), false);
			world.destroyBlock(pos.add(x -2, y, z + 2), false);
			world.destroyBlock(pos.add(x -2, y, z -2), false);
			world.destroyBlock(pos.add(x + 2, y, z + 2), false);
			world.destroyBlock(pos.add(x + 2, y, z -2), false);
			world.setBlockState(pos, ACBlocks.ritual_altar.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x -3, y, z), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x, y, z -3), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x + 3, y, z), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x, y, z + 3), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x -2, y, z + 2), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x -2, y, z -2), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x + 2, y, z + 2), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
			world.setBlockState(pos.add(x + 2, y, z -2), ACBlocks.ritual_pedestal.getStateFromMeta(meta), 2);
		}
	}
}
