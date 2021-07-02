package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.rapidregister.annotations.RegisterContents;

@RegisterContents
public class HItems {
	// Resources
	public static final Item TRITANIUM_INGOT = new Item(new FabricItemSettings().group(Hyperstellar.ITEM_GROUP));
	public static final Item ASTERIA_CRYSTAL = new Item(new FabricItemSettings().group(Hyperstellar.ITEM_GROUP));
	public static final Item EMPTY_FUSION_CELL = new Item(new FabricItemSettings().group(Hyperstellar.ITEM_GROUP));
	public static final Item FILLED_FUSION_CELL = new Item(new FabricItemSettings().group(Hyperstellar.ITEM_GROUP));
}
