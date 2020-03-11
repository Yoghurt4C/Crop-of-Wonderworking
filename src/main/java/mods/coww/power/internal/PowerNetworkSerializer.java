package mods.coww.power.internal;

import mods.coww.api.internal.PowerNetworkInterface;
import mods.coww.api.power.BlockEntitySignature;
import mods.coww.api.power.PowerNetworkEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BinaryOperator;

public final class PowerNetworkSerializer implements PowerNetworkInterface {

    public static final PowerNetworkSerializer INSTANCE = new PowerNetworkSerializer();

    private final WeakHashMap<World, Set<BlockEntitySignature>> powerTanks = new WeakHashMap<>();
    private final WeakHashMap<World, Set<BlockEntitySignature>> powerCollectors = new WeakHashMap<>();
    
    public void onNetworkEvent(PowerNetworkEvent event) {
        Map<World, Set<BlockEntitySignature>> map = event.type == PowerNetworkEvent.PowerBlockType.COLLECTOR ? powerCollectors : powerTanks;
        if(event.action == PowerNetworkEvent.Action.ADD)
            add(map, event.be);
        else remove(map, event.be);
    }

    @Override
    public void clear() {
        powerTanks.clear();
        powerCollectors.clear();
    }

    @Override
    public BlockEntity getClosestTank(BlockPos pos, World world, int limit) {
        if(powerTanks.containsKey(world))
            return getClosest(powerTanks.get(world), pos, world.isClient, limit);
        return null;
    }

    @Override
    public BlockEntity getClosestCollector(BlockPos pos, World world, int limit) {
        if(powerCollectors.containsKey(world))
            return getClosest(powerCollectors.get(world), pos, world.isClient, limit);
        return null;
    }

    public boolean isCollectorIn(BlockEntity be) {
        return isIn(be, powerCollectors);
    }

    public boolean isTankIn(BlockEntity be) {
        return isIn(be, powerTanks);
    }

    private boolean isIn(BlockEntity be, Map<World, Set<BlockEntitySignature>> map) {
        Set<BlockEntitySignature> set = map.get(be.getWorld());
        return set != null && set.contains(new BlockEntitySignature(be, be.getWorld().isClient));
    }

    private BlockEntity getClosest(Set<BlockEntitySignature> bes, BlockPos pos, boolean remoteCheck, int limit) {
        return bes.stream()
                .filter(ts -> ts.isRemote() == remoteCheck)
                .map(BlockEntitySignature::getTile)
                .filter(t -> !t.isRemoved())
                .filter(t -> t.getPos().getSquaredDistance(pos) <= limit * limit)
                .reduce(BinaryOperator.minBy(Comparator.comparing(t -> t.getPos().getSquaredDistance(pos), Double::compare)))
                .orElse(null);
    }

    private void remove(Map<World, Set<BlockEntitySignature>> map, BlockEntity be) {
        World world = be.getWorld();

        if(!map.containsKey(world))
            return;

        map.get(world).remove(new BlockEntitySignature(be, be.getWorld().isClient));
    }

    private void add(Map<World, Set<BlockEntitySignature>> map, BlockEntity be) {
        World world = be.getWorld();
        map.putIfAbsent(world, new HashSet<>());
        map.get(world).add(new BlockEntitySignature(be, be.getWorld().isClient));
    }

    @Override
    public Set<BlockEntitySignature> getAllCollectorsInWorld(World world) {
        return getAllInWorld(powerCollectors, world);
    }

    @Override
    public Set<BlockEntitySignature> getAllTanksInWorld(World world) {
        return getAllInWorld(powerTanks, world);
    }

    private Set<BlockEntitySignature> getAllInWorld(Map<World, Set<BlockEntitySignature>> map, World world) {
        return map.getOrDefault(world, new HashSet<>());
    }
}
