package com.zartio.betterendgame.data.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.item.EmptyWarpRuneItem;
import com.zartio.betterendgame.data.item.InscribedWarpRuneItem;
import com.zartio.betterendgame.data.item.UnstableMatterItem;

public class Items {
    public static final Item UNSTABLE_MATTER_ITEM
            = registerItem("unstable_matter", new UnstableMatterItem(new Item.Settings().rarity(Rarity.EPIC)), ItemGroups.FUNCTIONAL);
    public static final Item EMPTY_WARP_RUNE_ITEM
            = registerItem("empty_warp_rune", new EmptyWarpRuneItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item INSCRIBED_WARP_RUNE_ITEM
            = registerItem("inscribed_warp_rune", new InscribedWarpRuneItem(new Item.Settings().rarity(Rarity.EPIC)));

    public static void init() {}

    private static Item registerItem(String name, Item item) {
        Item registeredItem = Registry.register(Registries.ITEM, Identifier.of(BetterEndgame.MOD_ID, name), item);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SEARCH).register(content -> content.add(registeredItem));
        return registeredItem;
    }

    private static Item registerItem(String name, Item item, RegistryKey<ItemGroup> itemGroup) {
        Item registeredItem = registerItem(name, item);
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(content -> content.add(registeredItem));
        return registeredItem;
    }
}
