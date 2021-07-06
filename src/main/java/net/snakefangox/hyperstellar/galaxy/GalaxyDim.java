package net.snakefangox.hyperstellar.galaxy;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.register.HWorldGen;
import net.snakefangox.hyperstellar.world_gen.OrbitGenerator;
import net.snakefangox.hyperstellar.world_gen.SpaceGenerator;
import net.snakefangox.worldshell.util.DynamicWorldRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

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
		dimensionOptions = loadedOps.orElseThrow().getFirst();
	}

	public void loadWorld(MinecraftServer server) {
		DynamicWorldRegister.createDynamicWorld(server, worldKey, dimensionOptions);
	}

	public RegistryKey<World> getWorldKey() {
		return worldKey;
	}

	public NbtCompound writeNbt() {
		var nbt = new NbtCompound();
		nbt.putString("worldKey", worldKey.getValue().toString());
		var savedOps = DimensionOptions.CODEC.fieldOf("dimensionOps").encode(dimensionOptions, NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder());
		nbt = (NbtCompound) savedOps.build(nbt).result().orElse(nbt);
		return nbt;
	}

	@NotNull
	public static DimensionOptions getOrbitDimensionOptions(Random random, MinecraftServer server, RegistryKey<DimensionType> typeKey, boolean isOrbit) {
		Supplier<DimensionType> orbitTypeSupplier = () -> server.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(typeKey);

		var source = new FixedBiomeSource(server.getRegistryManager().get(Registry.BIOME_KEY).get(HWorldGen.SPACE_BIOME));
		var config = new StructuresConfig(Optional.empty(), Collections.emptyMap());
		var gen = isOrbit ? new OrbitGenerator(source, config, random.nextLong()) : new SpaceGenerator(source, config, random.nextLong());

		return new DimensionOptions(orbitTypeSupplier, gen);
	}

}
