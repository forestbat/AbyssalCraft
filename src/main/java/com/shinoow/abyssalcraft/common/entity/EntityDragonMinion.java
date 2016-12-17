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
package com.shinoow.abyssalcraft.common.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.ICoraliumEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.lib.ACAchievements;
import com.shinoow.abyssalcraft.lib.ACConfig;

public class EntityDragonMinion extends EntityMob implements IEntityMultiPart, ICoraliumEntity
{

	public static final float innerRotation = 0;
	public double targetX;
	public double targetY;
	public double targetZ;

	/**
	 * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to calculate offsets for the animations.
	 */
	public double[][] ringBuffer = new double[64][3];

	/**
	 * Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer.
	 */
	public int ringBufferIndex = -1;

	/** An array containing all body parts of this dragon */
	public EntityDragonPart[] dragonPartArray;

	/** The head bounding box of a dragon */
	public EntityDragonPart dragonPartHead;

	/** The body bounding box of a dragon */
	public EntityDragonPart dragonPartBody;
	public EntityDragonPart dragonPartTail1;
	public EntityDragonPart dragonPartTail2;
	public EntityDragonPart dragonPartTail3;
	public EntityDragonPart dragonPartWing1;
	public EntityDragonPart dragonPartWing2;

	/** Animation time at previous tick. */
	public float prevAnimTime;

	/**
	 * Animation time, used to control the speed of the animation cycles (wings flapping, jaw opening, etc.)
	 */
	public float animTime;

	/** Force selecting a new flight target at next tick if set to true. */
	public boolean forceNewTarget;

	private Entity target;

	public EntityDragonBoss healingcircle;

	public EntityDragonMinion(World par1World) {
		super(par1World);
		dragonPartArray = new EntityDragonPart[] {dragonPartHead = new EntityDragonPart(this, "head", 3.0F, 3.0F), dragonPartBody = new EntityDragonPart(this, "body", 4.0F, 4.0F), dragonPartTail1 = new EntityDragonPart(this, "tail", 2.0F, 2.0F), dragonPartTail2 = new EntityDragonPart(this, "tail", 2.0F, 2.0F), dragonPartTail3 = new EntityDragonPart(this, "tail", 2.0F, 2.0F), dragonPartWing1 = new EntityDragonPart(this, "wing", 2.0F, 2.0F), dragonPartWing2 = new EntityDragonPart(this, "wing", 2.0F, 2.0F)};
		setHealth(getMaxHealth());
		setSize(8.0F, 4.0F);
		noClip = true;
		targetY = 100.0D;
		isImmuneToFire = true;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		if(ACConfig.hardcoreMode) getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
		else getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
	}

	public double[] getMovementOffsets(int par1, float par2)
	{
		if (getHealth() <= 0.0F)
			par2 = 0.0F;

		par2 = 1.0F - par2;
		int j = ringBufferIndex - par1 * 1 & 63;
		int k = ringBufferIndex - par1 * 1 - 1 & 63;
		double[] adouble = new double[3];
		double d0 = ringBuffer[j][0];
		double d1 = MathHelper.wrapAngleTo180_double(ringBuffer[k][0] - d0);
		adouble[0] = d0 + d1 * par2;
		d0 = ringBuffer[j][1];
		d1 = ringBuffer[k][1] - d0;
		adouble[1] = d0 + d1 * par2;
		adouble[2] = ringBuffer[j][2] + (ringBuffer[k][2] - ringBuffer[j][2]) * par2;
		return adouble;
	}

	@Override
	protected Item getDropItem()
	{
		return ACItems.coralium_plagued_flesh;

	}

	@Override
	public void onDeath(DamageSource par1DamageSource)
	{
		super.onDeath(par1DamageSource);

		if (par1DamageSource.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer)par1DamageSource.getEntity();
			entityplayer.addStat(ACAchievements.kill_spectral_dragon, 1);
		}
	}

	@Override
	public void onLivingUpdate()
	{
		float f;
		float f1;

		if (worldObj.isRemote)
		{
			f = MathHelper.cos(animTime * (float)Math.PI * 2.0F);
			f1 = MathHelper.cos(prevAnimTime * (float)Math.PI * 2.0F);

			if (f1 <= -0.3F && f >= -0.3F)
				worldObj.playSound(posX, posY, posZ, "mob.enderdragon.wings", 5.0F, 0.8F + rand.nextFloat() * 0.3F, false);
		}

		prevAnimTime = animTime;
		float f2;

		if (getHealth() <= 0.0F)
		{
			f = (rand.nextFloat() - 0.5F) * 8.0F;
			f1 = (rand.nextFloat() - 0.5F) * 4.0F;
			f2 = (rand.nextFloat() - 0.5F) * 8.0F;
			if(ACConfig.particleEntity)
				worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, posX + f, posY + 2.0D + f1, posZ + f2, 0.0D, 0.0D, 0.0D);
		}
		else
		{
			updateHealingCircle();
			f = 0.2F / (MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ) * 10.0F + 1.0F);
			f *= (float)Math.pow(2.0D, motionY);

			animTime += f;


			rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);

			if (ringBufferIndex < 0)
				for (int i = 0; i < ringBuffer.length; ++i)
				{
					ringBuffer[i][0] = rotationYaw;
					ringBuffer[i][1] = posY;
				}

			if (++ringBufferIndex == ringBuffer.length)
				ringBufferIndex = 0;

			ringBuffer[ringBufferIndex][0] = rotationYaw;
			ringBuffer[ringBufferIndex][1] = posY;
			double d0;
			double d1;
			double d2;
			double d3;
			float f3;

			if (worldObj.isRemote)
			{
				if (newPosRotationIncrements > 0)
				{
					d3 = posX + (newPosX - posX) / newPosRotationIncrements;
					d0 = posY + (newPosY - posY) / newPosRotationIncrements;
					d1 = posZ + (newPosZ - posZ) / newPosRotationIncrements;
					d2 = MathHelper.wrapAngleTo180_double(newRotationYaw - rotationYaw);
					rotationYaw = (float)(rotationYaw + d2 / newPosRotationIncrements);
					rotationPitch = (float)(rotationPitch + (newRotationPitch - rotationPitch) / newPosRotationIncrements);
					--newPosRotationIncrements;
					setPosition(d3, d0, d1);
					setRotation(rotationYaw, rotationPitch);
				}
			}
			else
			{
				d3 = targetX - posX;
				d0 = targetY - posY;
				d1 = targetZ - posZ;
				d2 = d3 * d3 + d0 * d0 + d1 * d1;

				if (target != null)
				{
					targetX = target.posX;
					targetZ = target.posZ;
					double d4 = targetX - posX;
					double d5 = targetZ - posZ;
					double d6 = Math.sqrt(d4 * d4 + d5 * d5);
					double d7 = 0.4000000059604645D + d6 / 80.0D - 1.0D;

					if (d7 > 10.0D)
						d7 = 10.0D;

					targetY = target.getEntityBoundingBox().minY + d7;
				}
				else
				{
					targetX += rand.nextGaussian() * 2.0D;
					targetZ += rand.nextGaussian() * 2.0D;
				}

				if (forceNewTarget || d2 < 100.0D || d2 > 22500.0D || isCollidedHorizontally || isCollidedVertically)
					setNewTarget();

				d0 /= MathHelper.sqrt_double(d3 * d3 + d1 * d1);
				f3 = 0.6F;

				if (d0 < -f3)
					d0 = -f3;

				if (d0 > f3)
					d0 = f3;

				motionY += d0 * 0.10000000149011612D;
				rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);
				double d8 = 180.0D - Math.atan2(d3, d1) * 180.0D / Math.PI;
				double d9 = MathHelper.wrapAngleTo180_double(d8 - rotationYaw);

				if (d9 > 50.0D)
					d9 = 50.0D;

				if (d9 < -50.0D)
					d9 = -50.0D;

				Vec3 vec3 = new Vec3(targetX - posX, targetY - posY, targetZ - posZ).normalize();
				Vec3 vec31 = new Vec3(MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F), motionY, -MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F)).normalize();
				float f4 = (float)(vec31.dotProduct(vec3) + 0.5D) / 1.5F;

				if (f4 < 0.0F)
					f4 = 0.0F;

				randomYawVelocity *= 0.8F;
				float f5 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ) * 1.0F + 1.0F;
				double d10 = Math.sqrt(motionX * motionX + motionZ * motionZ) * 1.0D + 1.0D;

				if (d10 > 40.0D)
					d10 = 40.0D;

				randomYawVelocity = (float)(randomYawVelocity + d9 * (0.699999988079071D / d10 / f5));
				rotationYaw += randomYawVelocity * 0.1F;
				float f6 = (float)(2.0D / (d10 + 1.0D));
				float f7 = 0.06F;
				moveFlying(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));


				moveEntity(motionX, motionY, motionZ);


				Vec3 vec32 = new Vec3(motionX, motionY, motionZ).normalize();
				float f8 = (float)(vec32.dotProduct(vec31) + 1.0D) / 2.5F;
				f8 = 0.8F + 0.15F * f8;
				motionX *= f8;
				motionZ *= f8;
				motionY *= 0.9100000262260437D;
			}

			renderYawOffset = rotationYaw;
			dragonPartHead.width = dragonPartHead.height = 1.5F;
			dragonPartTail1.width = dragonPartTail1.height = 1.0F;
			dragonPartTail2.width = dragonPartTail2.height = 1.0F;
			dragonPartTail3.width = dragonPartTail3.height = 1.0F;
			dragonPartBody.height = 1.5F;
			dragonPartBody.width = 2.5F;
			dragonPartWing1.height = 1.0F;
			dragonPartWing1.width = 2.0F;
			dragonPartWing2.height = 1.5F;
			dragonPartWing2.width = 2.0F;
			f1 = (float)(getMovementOffsets(5, 1.0F)[1] - getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
			f2 = MathHelper.cos(f1);
			float f9 = -MathHelper.sin(f1);
			float f10 = rotationYaw * (float)Math.PI / 180.0F;
			float f11 = MathHelper.sin(f10);
			float f12 = MathHelper.cos(f10);
			dragonPartBody.onUpdate();
			dragonPartBody.setLocationAndAngles(posX + f11 * 0.5F, posY, posZ - f12 * 0.5F, 0.0F, 0.0F);
			dragonPartWing1.onUpdate();
			dragonPartWing1.setLocationAndAngles(posX + f12 * 4.5F, posY + 2.0D, posZ + f11 * 4.5F, 0.0F, 0.0F);
			dragonPartWing2.onUpdate();
			dragonPartWing2.setLocationAndAngles(posX - f12 * 4.5F, posY + 2.0D, posZ - f11 * 4.5F, 0.0F, 0.0F);

			if (!worldObj.isRemote && hurtTime == 0)
			{
				attackEntitiesInList(worldObj.getEntitiesWithinAABBExcludingEntity(this, dragonPartWing1.getEntityBoundingBox().expand(1.0D, 0.5D, 1.0D).offset(0.0D, -0.5D, 0.0D)));
				attackEntitiesInList(worldObj.getEntitiesWithinAABBExcludingEntity(this, dragonPartWing2.getEntityBoundingBox().expand(1.0D, 0.5D, 1.0D).offset(0.0D, -0.5D, 0.0D)));
				attackEntitiesInList(worldObj.getEntitiesWithinAABBExcludingEntity(this, dragonPartHead.getEntityBoundingBox().expand(0.25D, 0.25D, 0.25D)));
			}

			double[] adouble = getMovementOffsets(5, 1.0F);
			double[] adouble1 = getMovementOffsets(0, 1.0F);
			f3 = MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F - randomYawVelocity * 0.01F);
			float f13 = MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F - randomYawVelocity * 0.01F);
			dragonPartHead.onUpdate();
			dragonPartHead.setLocationAndAngles(posX + f3 * 5.5F * f2, posY + (adouble1[1] - adouble[1]) * 1.0D + f9 * 5.5F, posZ - f13 * 5.5F * f2, 0.0F, 0.0F);

			for (int j = 0; j < 3; ++j)
			{
				EntityDragonPart entitydragonpart = null;

				if (j == 0)
					entitydragonpart = dragonPartTail1;

				if (j == 1)
					entitydragonpart = dragonPartTail2;

				if (j == 2)
					entitydragonpart = dragonPartTail3;

				double[] adouble2 = getMovementOffsets(12 + j * 2, 1.0F);
				float f14 = rotationYaw * (float)Math.PI / 180.0F + simplifyAngle(adouble2[0] - adouble[0]) * (float)Math.PI / 180.0F * 1.0F;
				float f15 = MathHelper.sin(f14);
				float f16 = MathHelper.cos(f14);
				float f17 = 1.5F;
				float f18 = (j + 1) * 2.0F;
				entitydragonpart.onUpdate();
				entitydragonpart.setLocationAndAngles(posX - (f11 * f17 + f15 * f18) * f2, posY + (adouble2[1] - adouble[1]) * 1.0D - (f18 + f17) * f9 + 1.5D, posZ + (f12 * f17 + f16 * f18) * f2, 0.0F, 0.0F);
			}
		}
	}

	@Override
	public void moveEntity(double x, double y, double z)
	{
		setEntityBoundingBox(getEntityBoundingBox().offset(x, y, z));
		posX = (getEntityBoundingBox().minX + getEntityBoundingBox().maxX) / 2.0D;
		posY = getEntityBoundingBox().minY;
		posZ = (getEntityBoundingBox().minZ + getEntityBoundingBox().maxZ) / 2.0D;
		try
		{
			doBlockCollisions();
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	@Override
	protected void doBlockCollisions()
	{
		BlockPos blockpos = new BlockPos(getEntityBoundingBox().minX + 0.001D, getEntityBoundingBox().minY + 0.001D, getEntityBoundingBox().minZ + 0.001D);
		BlockPos blockpos1 = new BlockPos(getEntityBoundingBox().maxX - 0.001D, getEntityBoundingBox().maxY - 0.001D, getEntityBoundingBox().maxZ - 0.001D);

		if (worldObj.isAreaLoaded(blockpos, blockpos1))
			for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i)
				for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j)
					for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k)
					{
						BlockPos blockpos2 = new BlockPos(i, j, k);
						IBlockState iblockstate = worldObj.getBlockState(blockpos2);

						if(iblockstate.getBlock().getMaterial() == Material.portal)
							addVelocity(motionX > 0 ? -3 : 3, motionY > 0 ? -3 : 3, motionZ > 0 ? -3 : 3);
					}
	}

	private void updateHealingCircle()
	{
		if (healingcircle != null)
			if (healingcircle.isDead)
			{
				if (!worldObj.isRemote)
					attackEntityFromPart(dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 100.0F);

				healingcircle = null;
			}
			else if (ticksExisted % 10 == 0 && getHealth() <= getMaxHealth())
				setHealth(getHealth() - 1.0F);

		if (rand.nextInt(10) == 0)
		{
			float f = 32.0F;
			List<?> list = worldObj.getEntitiesWithinAABB(EntityDragonBoss.class, getEntityBoundingBox().expand(f, f, f));
			EntityDragonBoss entitydragonboss = null;
			double d0 = Double.MAX_VALUE;
			Iterator<?> iterator = list.iterator();

			while (iterator.hasNext())
			{
				EntityDragonBoss entitydragonboss1 = (EntityDragonBoss)iterator.next();
				double d1 = entitydragonboss1.getDistanceSqToEntity(this);

				if (d1 < d0)
				{
					d0 = d1;
					entitydragonboss = entitydragonboss1;
				}
			}

			healingcircle = entitydragonboss;
		}
	}

	private void attackEntitiesInList(List<?> par1List)
	{
		for (int i = 0; i < par1List.size(); ++i)
		{
			Entity entity = (Entity)par1List.get(i);

			if (entity instanceof EntityLivingBase && !EntityUtil.isEntityCoralium((EntityLivingBase)entity))
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(AbyssalCraftAPI.coralium_plague.id, 200));

			if(ACConfig.hardcoreMode && entity instanceof EntityPlayer)
				entity.attackEntityFrom(DamageSource.causeMobDamage(this).setDamageBypassesArmor().setDamageIsAbsolute(), 1);
		}
	}

	private void setNewTarget()
	{
		forceNewTarget = false;

		if (rand.nextInt(2) == 0 && !worldObj.playerEntities.isEmpty())
			target = worldObj.playerEntities.get(rand.nextInt(worldObj.playerEntities.size()));
		else
		{
			boolean flag = false;

			do
			{
				targetX = 0.0D;
				targetY = 70.0F + rand.nextFloat() * 50.0F;
				targetZ = 0.0D;
				targetX += rand.nextFloat() * 120.0F - 60.0F;
				targetZ += rand.nextFloat() * 120.0F - 60.0F;
				double d0 = posX - targetX;
				double d1 = posY - targetY;
				double d2 = posZ - targetZ;
				flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
			}
			while (!flag);

			target = null;
		}
	}

	private float simplifyAngle(double par1)
	{
		return (float)MathHelper.wrapAngleTo180_double(par1);
	}

	@Override
	public boolean attackEntityFromPart(EntityDragonPart par1EntityDragonPart, DamageSource par2DamageSource, float par3)
	{
		float f1 = rotationYaw * (float)Math.PI / 180.0F;
		float f2 = MathHelper.sin(f1);
		float f3 = MathHelper.cos(f1);
		targetX = posX + f2 * 5.0F + (rand.nextFloat() - 0.5F) * 2.0F;
		targetY = posY + rand.nextFloat() * 3.0F + 1.0D;
		targetZ = posZ - f3 * 5.0F + (rand.nextFloat() - 0.5F) * 2.0F;
		target = null;

		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		return par1DamageSource.getEntity() instanceof EntityDragonBoss ? false : super.attackEntityFrom(par1DamageSource, par2);
	}

	@Override
	public Entity[] getParts()
	{
		return dragonPartArray;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public World getWorld()
	{
		return worldObj;
	}

	@Override
	protected String getLivingSound()
	{
		return "mob.enderdragon.growl";
	}

	@Override
	protected String getHurtSound()
	{
		return "mob.enderdragon.hit";
	}
}
