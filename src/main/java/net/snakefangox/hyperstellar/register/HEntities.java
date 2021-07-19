package net.snakefangox.hyperstellar.register;

import net.snakefangox.hyperstellar.blocks.entities.BackupGeneratorBE;
import net.snakefangox.hyperstellar.blocks.entities.EnergyConduitBE;
import net.snakefangox.hyperstellar.blocks.entities.FusionGeneratorBE;
import net.snakefangox.hyperstellar.blocks.entities.GalacticMapBE;
import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;
import net.snakefangox.hyperstellar.blocks.entities.ShipyardControllerBE;
import net.snakefangox.hyperstellar.ships.ShipEntity;
import net.snakefangox.rapidregister.annotations.RegisterContents;
import net.snakefangox.worldshell.entity.WorldShellEntityType;

import net.minecraft.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

@RegisterContents
public class HEntities {

	// Block Entities
	public static final BlockEntityType<ShipyardControllerBE> SHIPYARD_CONTROLLER = FabricBlockEntityTypeBuilder.create(ShipyardControllerBE::new, HBlocks.SHIPYARD_CONTROLLER).build();
	public static final BlockEntityType<ShipNameplateBE> SHIP_NAMEPLATE = FabricBlockEntityTypeBuilder.create(ShipNameplateBE::new, HBlocks.SHIP_NAMEPLATE).build();
	public static final BlockEntityType<BackupGeneratorBE> BACKUP_GENERATOR = FabricBlockEntityTypeBuilder.create(BackupGeneratorBE::new, HBlocks.BACKUP_GENERATOR).build();
	public static final BlockEntityType<FusionGeneratorBE> FUSION_REACTOR = FabricBlockEntityTypeBuilder.create(FusionGeneratorBE::new, HBlocks.FUSION_REACTOR_CONTROLLER).build();
	public static final BlockEntityType<GalacticMapBE> GALACTIC_MAP = FabricBlockEntityTypeBuilder.create(GalacticMapBE::new, HBlocks.GALACTIC_MAP).build();
	public static final BlockEntityType<EnergyConduitBE> ENERGY_CONDUIT = FabricBlockEntityTypeBuilder.create(EnergyConduitBE::new, HBlocks.ENERGY_CONDUIT).build();


	// Entity Entities
	public static final WorldShellEntityType<ShipEntity> SHIP = new WorldShellEntityType<>(ShipEntity::new, 10, 3, true);
}
