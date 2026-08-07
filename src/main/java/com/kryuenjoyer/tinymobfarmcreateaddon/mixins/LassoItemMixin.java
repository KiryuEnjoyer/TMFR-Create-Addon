package com.kryuenjoyer.tinymobfarmcreateaddon.mixins;

import com.daqem.tinymobfarm.item.LassoItem;
import com.daqem.tinymobfarm.util.EntityHelper;
import com.daqem.tinymobfarm.util.NBTHelper;
import com.kryuenjoyer.tinymobfarmcreateaddon.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LassoItem.class, remap = false)
public class LassoItemMixin {
    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void interactMobWithBlacklist(ItemStack stack, Player player, LivingEntity target, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if (!NBTHelper.hasMob(stack) && target.isAlive() && target instanceof Mob) {
            if (player.level() instanceof ServerLevel)
            {
                ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
                //Blacklist Check and Fail
                if (entityId != null && Config.BLACKLISTED_MOBS.get().contains(entityId.toString())) {
                    player.sendSystemMessage(Component.translatable("tmfrcreateaddon.error.mob_blacklist", true));
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    cir.cancel();
                    return;
                }

                //Capturing
                CompoundTag nbt = NBTHelper.getBaseTag(stack);
                CompoundTag mobData = target.saveWithoutId(new CompoundTag());
                mobData.put("Rotation", NBTHelper.createNBTList(new Tag[]{DoubleTag.valueOf((double)0.0F), DoubleTag.valueOf((double)0.0F)}));
                mobData.remove("Fire");
                mobData.remove("HurtTime");
                nbt.put("mobData", mobData);
                nbt.putString("mobName", target.getName().getString());
                nbt.putString("mobId", String.valueOf(target.getType()));
                nbt.putString("mobLootTableLocation", EntityHelper.getLootTableLocation(target));
                nbt.putDouble("mobHealth", (double)Math.round(target.getHealth() * 10.0F) / (double)10.0F);
                nbt.putDouble("mobMaxHealth", (double)target.getMaxHealth());
                nbt.putBoolean("mobHostile", target instanceof Monster);
                target.discard();
                player.getInventory().setChanged();
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            cir.cancel();
        }
        else {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
