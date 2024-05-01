/*
 * Copyright (c) 2019-2024 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.session.cache;

import it.unimi.dsi.fastutil.Pair;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class ItemUUIDCache {
    private final Map<DataComponents, Pair<WeakReference<DataComponents>, UUID>> uuids = new WeakHashMap<>();

    // Returns a UUID and the object used as the key. A reference must be held to the key by items using the UUID
    public Pair<DataComponents, UUID> getUUID(DataComponents components) {
        Pair<WeakReference<DataComponents>, UUID> pair = uuids.get(components);
        DataComponents componentsKey = null;
        if (pair != null) {
            componentsKey = pair.key().get();
            // Can't return yet. The key might have been garbage collected at this point
        }

        if (pair == null || componentsKey == null) {
            // Make a copy to ensure the key can't be changed.
            // Also prevents the map key from being garbage collected because one reference was dropped,
            // even though other equal components still exist.
            componentsKey = components.clone();
            UUID uuid = UUID.randomUUID();

            // A reference to the key is stored in the value since there is no method to retrieve the key object
            uuids.put(componentsKey, Pair.of(new WeakReference<>(componentsKey), uuid));
            return Pair.of(componentsKey, uuid);
        }

        return Pair.of(componentsKey, pair.value());
    }
}
