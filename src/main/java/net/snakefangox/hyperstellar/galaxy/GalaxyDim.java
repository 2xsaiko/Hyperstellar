package net.snakefangox.hyperstellar.galaxy;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.snakefangox.worldshell.util.DynamicWorldRegister;

public class GalaxyDim {
	private final RegistryKey<World> worldKey;
	private final DimensionOptions dimensionOptions;

	public GalaxyDim(RegistryKey<World> worldKey, DimensionOptions dimensionOptions) {
		this.worldKey = worldKey;
		this.dimensionOptions = dimensionOptions;
	}

	public GalaxyDim(NbtCompound nbt) {
		worldKey = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("worldKey")));
		var loadedOps = DimensionOptions.CODEC.decode(new Dynamic<>(NbtOps.INSTANCE, nbt)).result();
		if (loadedOps.isEmpty()) throw new RuntimeException("Could not load options for dimension: " + worldKey.toString());
		dimensionOptions = loadedOps.get().getFirst();
	}

	public void loadWorld(MinecraftServer server) {
		DynamicWorldRegister.createDynamicWorld(server, worldKey, dimensionOptions);
	}

	public NbtCompound writeNbt() {
		var nbt = new NbtCompound();
		nbt.putString("worldKey", worldKey.getValue().toString());
		var savedOps = DimensionOptions.CODEC.fieldOf("dimensionOps").encode(dimensionOptions, NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder());
		return (NbtCompound) savedOps.build(new NbtCompound()).result().orElse(new NbtCompound());
	}

}
