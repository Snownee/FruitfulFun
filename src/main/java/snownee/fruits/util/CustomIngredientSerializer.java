/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package snownee.fruits.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

/**
 * Serializer for a {@link CustomIngredient}.
 *
 * <p>All instances must be registered using {@link #register} for deserialization to work.
 *
 * @param <T> the type of the custom ingredient
 */
public interface CustomIngredientSerializer<T extends CustomIngredient> {
	/**
	 * Registers a custom ingredient serializer, using the {@linkplain CustomIngredientSerializer#getIdentifier() serializer's identifier}.
	 *
	 * @throws IllegalArgumentException if the serializer is already registered
	 */
	static void register(CustomIngredientSerializer<?> serializer) {
		CraftingHelper.register(serializer.getIdentifier(), new CustomIngredientImpl.Serializer(serializer));
	}

	/**
	 * {@return the identifier of this serializer}.
	 */
	ResourceLocation getIdentifier();

	/**
	 * Deserializes the custom ingredient from a JSON object.
	 *
	 * @throws JsonSyntaxException      if the JSON object does not match the format expected by the serializer
	 * @throws IllegalArgumentException if the JSON object is invalid for some other reason
	 */
	T read(JsonObject json);

	/**
	 * Serializes the custom ingredient to a JSON object.
	 */
	void write(JsonObject json, T ingredient);

	/**
	 * Deserializes the custom ingredient from a packet buffer.
	 */
	T read(FriendlyByteBuf buf);

	/**
	 * Serializes the custom ingredient to a packet buffer.
	 */
	void write(FriendlyByteBuf buf, T ingredient);
}
