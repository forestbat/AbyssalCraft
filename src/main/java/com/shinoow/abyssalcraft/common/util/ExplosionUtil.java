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
package com.shinoow.abyssalcraft.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.shinoow.abyssalcraft.common.world.ACExplosion;

public class ExplosionUtil {

	public static ACExplosion newODBExplosion(World par0World, Entity par1Entity, double par2, double par4, double par6, float par8, int par9, boolean par10, boolean par11)
	{
		ACExplosion explosion = new ACExplosion(par0World, par1Entity, par2, par4, par6, par8, par9);
		explosion.isAntimatter = par10;
		explosion.isSmoking = par11;
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		return explosion;
	}
}
