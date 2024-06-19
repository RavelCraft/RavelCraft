package com.connexal.ravelcraft.mod.cross.types.items.sets;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.google.common.collect.ImmutableMap;

public interface ItemSetDescriptor<T> extends Descriptor {
    ItemDescriptor get(T type);

    ImmutableMap<T, ItemDescriptor> items();
}
