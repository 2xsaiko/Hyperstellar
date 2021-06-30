package net.snakefangox.hyperstellar.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.Direction;
import net.snakefangox.hyperstellar.ships.ShipData;
import net.snakefangox.hyperstellar.ships.ShipPropertyProvider;

public class ShieldBlock extends Block implements ShipPropertyProvider {
	private final double shieldIntegrity;
	private final double shieldRecharge;

	public ShieldBlock(double shieldIntegrity, double shieldRecharge) {
		super(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1)
				.strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).luminance(value -> 8));
		this.shieldIntegrity = shieldIntegrity;
		this.shieldRecharge = shieldRecharge;
	}

	@Override
	public double rawPropChange(String propName, BlockState state, Direction forwards) {
		if (propName.equals(ShipData.SHIELD_INTEGRITY) || propName.equals(ShipData.MAX_SHIELD_INTEGRITY))
			return shieldIntegrity;
		if (propName.equals(ShipData.SHIELD_RECHARGE))
			return shieldRecharge;
		return 0;
	}
}
