/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.items.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.World;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.powerSystem.ItemPowerManager;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import techreborn.TechReborn;
import techreborn.config.ConfigTechReborn;
import techreborn.init.TRContent;

public class ItemLithiumIonBatpack extends ArmorItem implements IEnergyItemInfo, ItemDurabilityExtensions {

	// 8M FE maxCharge and 2k FE\t charge rate. Fully charged in 3 mins.
	public static final int maxCharge = ConfigTechReborn.LithiumBatpackCharge;
	public int transferLimit = 2_000;

	public ItemLithiumIonBatpack() {
		super(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(TechReborn.ITEMGROUP).maxCount(1));
	}

	public static void distributePowerToInventory(World world, PlayerEntity player, ItemStack itemStack, int maxSend) {
		if (world.isClient) {
			return;
		}

		ItemPowerManager capEnergy = new ItemPowerManager(itemStack);

		for (int i = 0; i < player.inventory.getInvSize(); i++) {
			if (!player.inventory.getInvStack(i).isEmpty()) {
				//TODO fix me
				//ExternalPowerSystems.chargeItem(capEnergy, player.inventory.getInvStack(i));
			}
		}
	}

	// Item
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof PlayerEntity) {
			distributePowerToInventory(worldIn, (PlayerEntity) entityIn, stack, (int) transferLimit);
		}
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - ItemUtils.getPowerForDurabilityBar(stack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return PowerSystem.getDisplayPower().colour;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
		if (!isIn(group)) {
			return;
		}
		ItemStack uncharged = new ItemStack(TRContent.LITHIUM_ION_BATPACK);
		ItemStack charged = new ItemStack(TRContent.LITHIUM_ION_BATPACK);
		ItemPowerManager capEnergy = new ItemPowerManager(charged);
		capEnergy.setEnergyStored(capEnergy.getMaxEnergyStored());
		items.add(uncharged);
		items.add(charged);
	}
	
	// IEnergyItemInfo
	@Override
	public int getCapacity() {
		return maxCharge;
	}

	@Override
	public int getMaxInput() {
		return transferLimit;
	}

	@Override
	public int getMaxOutput() {
		return transferLimit;
	}
}
