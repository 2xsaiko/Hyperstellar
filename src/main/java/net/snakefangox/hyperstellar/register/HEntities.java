package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;
import net.snakefangox.hyperstellar.blocks.entities.ShipyardControllerBE;
import net.snakefangox.hyperstellar.ships.ShipEntity;
import net.snakefangox.rapidregister.annotations.RegisterContents;
import net.snakefangox.worldshell.entity.WorldShellEntity;
import net.snakefangox.worldshell.entity.WorldShellEntityType;

@RegisterContents
public class HEntities {

	// Block Entities
	public static final BlockEntityType<ShipyardControllerBE> SHIPYARD_CONTROLLER = FabricBlockEntityTypeBuilder.create(ShipyardControllerBE::new, HBlocks.SHIPYARD_CONTROLLER).build();
	public static final BlockEntityType<ShipNameplateBE> SHIP_NAMEPLATE = FabricBlockEntityTypeBuilder.create(ShipNameplateBE::new, HBlocks.SHIP_NAMEPLATE).build();

	// Entity Entities
	public static final WorldShellEntityType<ShipEntity> SHIP = new WorldShellEntityType<>(ShipEntity::new, 10, 3, true);
}
