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
package com.shinoow.abyssalcraft.common.structures.abyss;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.shinoow.abyssalcraft.api.block.ACBlocks;

public class Chains extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos pos) {

		int h = 5 + rand.nextInt(35);
		int h2 = 255 - h * 6;
		int t = 0;
		while(!worldIn.isAirBlock(new BlockPos(pos.getX(), h2+1, pos.getZ()))){
			h2++;
			t++;
		}

		h -= t/6;

		int height = 255 - h2;
		BlockPos pos1 = new BlockPos(pos.getX(), h2, pos.getZ());
		boolean b = !worldIn.isAirBlock(pos1) || !worldIn.isAirBlock(pos1.down());

		for(int i = -3; i < 4; i++)
			for(int j = -3; j < 4; j++){

				boolean flag = i > -2 && i < 2;

				for(int k = 0; k < 3; k++)
					if((j == -3 || j == 3) && flag){
						if(k == 0){
							if(b){
								setBlockAndNotifyAdequately(worldIn, pos1.add(j, 0, i), ACBlocks.stone.getStateFromMeta(1));
								setBlockAndNotifyAdequately(worldIn, pos1.add(i, 0, j), ACBlocks.stone.getStateFromMeta(1));
							}

							setBlockAndNotifyAdequately(worldIn, pos1.add(j, height, i), ACBlocks.stone.getStateFromMeta(1));
							setBlockAndNotifyAdequately(worldIn, pos1.add(i, height, j), ACBlocks.stone.getStateFromMeta(1));
						}
					} else if(j == -2 || j == 2){
						if(k == 1){
							if(flag){
								if(b){
									setBlockAndNotifyAdequately(worldIn, pos1.add(j, k, i), ACBlocks.stone.getStateFromMeta(1));
									setBlockAndNotifyAdequately(worldIn, pos1.add(i, k, j), ACBlocks.stone.getStateFromMeta(1));
								}

								setBlockAndNotifyAdequately(worldIn, pos1.add(j, height - k, i), ACBlocks.stone.getStateFromMeta(1));
								setBlockAndNotifyAdequately(worldIn, pos1.add(i, height - k, j), ACBlocks.stone.getStateFromMeta(1));
							}
						} else if(k < 2 && i > -3 && i < 3){
							if(b){
								setBlockAndNotifyAdequately(worldIn, pos1.add(j, k, i), ACBlocks.stone.getStateFromMeta(1));
								setBlockAndNotifyAdequately(worldIn, pos1.add(i, k, j), ACBlocks.stone.getStateFromMeta(1));
							}

							setBlockAndNotifyAdequately(worldIn, pos1.add(j, height - k, i), ACBlocks.stone.getStateFromMeta(1));
							setBlockAndNotifyAdequately(worldIn, pos1.add(i, height - k, j), ACBlocks.stone.getStateFromMeta(1));
						}
					}
					else if(j > -2 && j < 2 && flag){
						if(b){
							setBlockAndNotifyAdequately(worldIn, pos1.add(j, k, i), ACBlocks.stone.getStateFromMeta(1));
							setBlockAndNotifyAdequately(worldIn, pos1.add(i, k, j), ACBlocks.stone.getStateFromMeta(1));
						}

						setBlockAndNotifyAdequately(worldIn, pos1.add(j, height - k, i), ACBlocks.stone.getStateFromMeta(1));
						setBlockAndNotifyAdequately(worldIn, pos1.add(i, height - k, j), ACBlocks.stone.getStateFromMeta(1));
					}
			}

		for(int i = 0; i < h; i++){
			setBlockAndNotifyAdequately(worldIn, pos1.up(i*6), ACBlocks.cobblestone.getStateFromMeta(4));

			setBlockAndNotifyAdequately(worldIn, pos1.add(0, 1 + i*6, 1), ACBlocks.cobblestone.getStateFromMeta(4));
			setBlockAndNotifyAdequately(worldIn, pos1.add(0, 1 + i*6, -1), ACBlocks.cobblestone.getStateFromMeta(4));
			setBlockAndNotifyAdequately(worldIn, pos1.add(0, 2 + i*6, 1), ACBlocks.cobblestone.getStateFromMeta(4));
			setBlockAndNotifyAdequately(worldIn, pos1.add(0, 2 + i*6, -1), ACBlocks.cobblestone.getStateFromMeta(4));

			setBlockAndNotifyAdequately(worldIn, pos1.up(3 + i*6), ACBlocks.cobblestone.getStateFromMeta(4));

			setBlockAndNotifyAdequately(worldIn, pos1.add(1, 4 + i*6, 0), ACBlocks.cobblestone.getStateFromMeta(4));
			setBlockAndNotifyAdequately(worldIn, pos1.add(-1, 4 + i*6, 0), ACBlocks.cobblestone.getStateFromMeta(4));
			setBlockAndNotifyAdequately(worldIn, pos1.add(1, 5 + i*6, 0), ACBlocks.cobblestone.getStateFromMeta(4));
			setBlockAndNotifyAdequately(worldIn, pos1.add(-1, 5 + i*6, 0), ACBlocks.cobblestone.getStateFromMeta(4));

			if(i + 1 == h)
				setBlockAndNotifyAdequately(worldIn, pos1.up(6 + i*6), ACBlocks.cobblestone.getStateFromMeta(4));
		}

		return true;
	}
}
