package com.kryuenjoyer.tinymobfarmcreateaddon.mixins;

import com.daqem.tinymobfarm.blockentity.MobFarmBlockEntity;
import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.tinymobfarm.util.NBTHelper;
import com.simibubi.create.Create;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.materials.ExperienceNuggetItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.daqem.tinymobfarm.util.EntityHelper.getEntityFromLasso;

@Mixin(value = EntityHelper.class, remap = false)
public class MobFarmBlockMixin {
    @Inject(method = "generateLoot", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void addExperienceNuggets(ResourceLocation lootTableLocation, ServerLevel level, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cir)
    {
        Entity entity = getEntityFromLasso(stack, BlockPos.ZERO, level);
        List<ItemStack> itemsToReturn = cir.getReturnValue();
        if (entity instanceof LivingEntity livingEntity) {
            int xp = livingEntity.getExperienceReward();

            if (xp > 0) {
                int nuggets = Math.max(1, (int) Math.ceil(xp / 3.0));
                itemsToReturn.add(AllItems.EXP_NUGGET.asStack(nuggets));
            }
        }

        cir.setReturnValue(itemsToReturn);
    }
}
