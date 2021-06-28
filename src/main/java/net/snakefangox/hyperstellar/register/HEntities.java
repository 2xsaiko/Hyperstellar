package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.snakefangox.hyperstellar.blocks.entities.ShipyardControllerBE;
import net.snakefangox.rapidregister.annotations.RegisterContents;

@RegisterContents
public class HEntities {

	public static final BlockEntityType<ShipyardControllerBE> SHIPYARD_CONTROLLER = FabricBlockEntityTypeBuilder.create(ShipyardControllerBE::new, HBlocks.SHIPYARD_CONTROLLER).build();
}
