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

package techreborn.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.widget.GuiButtonUpDown;
import reborncore.common.network.NetworkManager;
import reborncore.common.powerSystem.PowerSystem;
import techreborn.packets.ServerboundPackets;
import techreborn.tiles.storage.TileAdjustableSU;

public class GuiAESU extends GuiBase {

	TileAdjustableSU tile;

	public GuiAESU(final PlayerEntity player, final TileAdjustableSU aesu) {
		super(player, aesu, aesu.createContainer(player));
		this.tile = aesu;
	}

	@Override
	protected void drawBackground(final float f, final int mouseX, final int mouseY) {
		super.drawBackground(f, mouseX, mouseY);
		final Layer layer = Layer.BACKGROUND;

		this.drawSlot(62, 45, layer);
		this.drawSlot(98, 45, layer);
		this.drawArmourSlots(8, 18, layer);
		this.builder.drawEnergyOutput(this, 171, 61, this.tile.getCurrentOutput(), layer);
		this.builder.drawUpDownButtons(this, 121, 79, layer);
	}

	@Override
	protected void drawForeground(final int mouseX, final int mouseY) {
		super.drawForeground(mouseX, mouseY);
		final Layer layer = Layer.FOREGROUND;

		if(GuiBase.slotConfigType == SlotConfigType.NONE){
			GlStateManager.pushMatrix();
			GlStateManager.scaled(0.6, 0.6, 1);
			drawCentredString(PowerSystem.getLocaliszedPowerFormattedNoSuffix((int) tile.getEnergy()) + "/"
				+ PowerSystem.getLocaliszedPowerFormattedNoSuffix((int) tile.getMaxPower()) + " "
				+ PowerSystem.getDisplayPower().abbreviation, 35, 0, 58, layer);
			GlStateManager.popMatrix();
		}
	
		builder.drawMultiEnergyBar(this, 81, 28, (int) tile.getEnergy(), (int) tile.getMaxPower(), mouseX, mouseY, 0, layer);
		
		buttons.add(new GuiButtonUpDown(121, 79, this, layer, this::onClick));
		buttons.add(new GuiButtonUpDown(121 + 12, 79, this, layer, this::onClick));
		buttons.add(new GuiButtonUpDown(121 + 24, 79, this, layer, this::onClick));
		buttons.add(new GuiButtonUpDown(121 + 36, 79, this, layer, this::onClick));
	}

	public void onClick(ButtonWidget button){
		NetworkManager.sendToServer(ServerboundPackets.createPacketAesu(1, tile));
	}
}
