package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.living.ISpellCaster;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.util.*;
import com.electroblob.wizardry.content.entity.goal.*;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWizard extends PathfinderMob implements ISpellCaster {
    /** Cooldown timer for the wizard's self-healing ability. (Using heal spell) */
    private static final EntityDataAccessor<Integer> HEAL_COOLDOWN = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);

    /** Wizard's Element, saved because it affects their texture, spells and more data internally. */
    private static final EntityDataAccessor<String> ELEMENT = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.STRING);

    /** The spell that is currently being cast continuously (e.g. beam spells) or {@code FlameRay}, it could be {@code NoneSpell} */
    private static final EntityDataAccessor<String> CONTINUOUS_SPELL = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.STRING);

    /** Counter for how long the current continuous spell has been cast for. */
    private static final EntityDataAccessor<Integer> SPELL_COUNTER = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);

    /** Index of the texture variant used by this wizard. */
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);

    /** The entity ID of the target of the current spell being cast. Used client-side for continuous spells. */
    private static final EntityDataAccessor<Integer> SPELL_TARGET_ID = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);

    protected List<Spell> spells = new ArrayList<>(4);

    public AbstractWizard(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedKitingGoal(this, 0.6D));
        this.goalSelector.addGoal(3, new HardLookAtTargetGoal(this, 10.0F, 10.0F));
        this.goalSelector.addGoal(4, new AttackSpellBasicGoal<>(this, 14.0F, 30, 50));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, AbstractWizard.class, 5.0F, 0.02F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(AbstractWizard.class));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Mob.class, 0,
                false, true, (entity) -> entity != null && !entity.isInvisible() && entity instanceof Enemy));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        handleContinuousSpellOnClient();
        handleSelfHealing();
    }

    /**
     * Handles the casting of continuous spells on the client side. This method checks if a continuous spell is being
     * cast and if so, retrieves the target entity and casts the spell using the appropriate context.
     */
    private void handleContinuousSpellOnClient() {
        if (!level().isClientSide) return;
        Spell continuousSpell = this.getContinuousSpell();
        int spellCounter = this.getSpellCounter();

        // No continuous spell is being cast or the counter has run out
        if (continuousSpell == Spells.NONE || spellCounter <= 0) return;

        // Now we will attempt to get the target
        int targetId = this.getSpellTargetId();
        LivingEntity target = null;

        if (targetId != -1) {
            Entity entity = level().getEntity(targetId);
            if (entity instanceof LivingEntity livingEntity) target = livingEntity;
        }

        if (target != null) {
            EntityCastContext ctx = new EntityCastContext(level(), this, InteractionHand.MAIN_HAND, spellCounter, target, this.getModifiers());
            continuousSpell.cast(ctx);
        }
    }

    /**
     * Handles the wizard's self-healing ability using the heal spell. This method checks if the wizard should start
     * healing, performs the healing, manages the cooldown, and plays the appropriate sound and particle effects. It is
     * called every {@code aiStep()}.
     */
    private void handleSelfHealing() {
        int healCooldown = this.getHealCooldown();

        if (shouldStartHealing()) {
            this.heal(this.getElement() == Elements.HEALING ? 8 : 4);
            this.setHealCooldown(-1);
            return;
        }

        // If healCooldown is -1, it means we just healed this tick, so we need to set the cooldown and play sound/particles
        if (healCooldown == -1 && !this.isDeadOrDying()) {
            if (level().isClientSide) {
                ParticleBuilder.spawnHealParticles(level(), this);
                return;
            }
            if (this.getHealth() < 10) this.setHealCooldown(150);
            else this.setHealCooldown(400);
            SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(Spells.HEAL.getLocation().getNamespace(), "spell." + Spells.HEAL.getLocation().getPath()));
            level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.PLAYERS, Spells.HEAL.getVolume(), Spells.HEAL.getPitch() + Spells.HEAL.getPitchVariation() * (level().random.nextFloat() - 0.5f));
        }

        if (healCooldown > 0) this.setHealCooldown(healCooldown - 1);
    }

    /**
     * Determines whether the wizard should start healing based on its current health, heal cooldown, and status effects.
     *
     * @return {@code true} if the wizard should start healing; {@code false} otherwise.
     */
    private boolean shouldStartHealing() {
        return getHealCooldown() == 0 && getHealth() < getMaxHealth() && getHealth() > 0
                && !hasEffect(EBMobEffects.ARCANE_JAMMER.get());
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        setTextureIndex(this.random.nextInt(6));

        Element element = chooseElement();
        this.setElement(element);
        equipArmorAndDisableDrops(element);

        spells.add(Spells.MAGIC_MISSILE);
        SpellTier maxTier = EntityUtil.populateSpells(spells, element, false, 3, random);
        prepareWandWithSpells(element, maxTier);

        this.setHealCooldown(50);
        return super.finalizeSpawn(level, difficulty, mobSpawnType, spawnData, tag);
    }

    /**
     * Select an element for the wizard. There is a 50% chance of it being MAGIC, otherwise it is randomly chosen from
     * the other elements.
     *
     * @return The chosen element.
     */
    private Element chooseElement() {
        if (this.random.nextBoolean()) {
            List<Element> elements = new ArrayList<>(Services.REGISTRY_UTIL.getElements());
            elements.remove(Elements.MAGIC);
            return elements.get(this.random.nextInt(elements.size()));
        }
        return Elements.MAGIC;
    }

    /**
     * Equip the wizard with armor of the given element and disable all armor drops.
     *
     * @param element The element of the armor to equip.
     */
    private void equipArmorAndDisableDrops(Element element) {
        for (EquipmentSlot slot : InventoryUtil.ARMOR_SLOTS) {
            this.setItemSlot(slot, new ItemStack(SpellUtil.getArmor(WizardArmorType.WIZARD, element, slot)));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, 0.0f);
        }
    }

    /**
     * Prepares a wand containing the wizard's spells and equips it.
     *
     * @param element The element of the wand.
     * @param maxTier The maximum tier of spell the wand can contain.
     */
    private void prepareWandWithSpells(Element element, SpellTier maxTier) {
        ArrayList<Spell> list = new ArrayList<>(spells);
        list.add(Spells.HEAL);

        Item item = WandItem.getWand(maxTier, element);
        if (item == null || item == Items.AIR || !(item instanceof WandItem)) {
            EBLogger.warn("Failed to create wand for wizard with element {} and max tier {}. Defaulting to apprentice wand.", element.getName(), maxTier);
            item = EBItems.APPRENTICE_WAND.get();
        }
        ItemStack wand = new ItemStack(item);
        Spell[] spellsArray = list.toArray(new Spell[0]);
        WandHelper.setSpells(wand, java.util.Arrays.asList(spellsArray));
        this.setItemSlot(EquipmentSlot.MAINHAND, wand);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source.getEntity() instanceof Player)
            EBAdvancementTriggers.ANGER_WIZARD.triggerFor((Player) source.getEntity());
        return super.hurt(source, damage);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5F).add(Attributes.MAX_HEALTH, 30);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAL_COOLDOWN, -1);
        this.entityData.define(ELEMENT, Elements.FIRE.getLocation().toString());
        this.entityData.define(CONTINUOUS_SPELL, Spells.NONE.getLocation().toString());
        this.entityData.define(SPELL_COUNTER, 0);
        this.entityData.define(TEXTURE_INDEX, 0);
        this.entityData.define(SPELL_TARGET_ID, -1);
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        Element element = this.getElement();
        nbt.putString("element", element == null ? Elements.FIRE.getLocation().toString() : element.getLocation().toString());
        nbt.putInt("skin", this.getTextureIndex());
        NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToTag(spells, spell -> StringTag.valueOf(spell.getLocation().toString())));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        Element element = Services.REGISTRY_UTIL.getElement(ResourceLocation.tryParse(nbt.getString("element")));
        this.setElement(element == null ? Elements.FIRE : element);
        this.setTextureIndex(nbt.getInt("skin"));
        this.spells = (List<Spell>) NBTExtras.tagToList(nbt.getList("spells", Tag.TAG_STRING), (StringTag tag) -> Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(tag.getAsString())));
    }

    private int getHealCooldown() {
        return this.entityData.get(HEAL_COOLDOWN);
    }

    private void setHealCooldown(int cooldown) {
        this.entityData.set(HEAL_COOLDOWN, cooldown);
    }

    public Element getElement() {
        return Services.REGISTRY_UTIL.getElement(ResourceLocation.tryParse(this.entityData.get(ELEMENT)));
    }

    public void setElement(Element element) {
        this.entityData.set(ELEMENT, element.getLocation().toString());
    }

    public int getTextureIndex() {
        return this.entityData.get(TEXTURE_INDEX);
    }

    public void setTextureIndex(int index) {
        this.entityData.set(TEXTURE_INDEX, index);
    }

    @Override
    public @NotNull List<Spell> getSpells() {
        return this.spells;
    }

    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }

    @Override
    public @NotNull Spell getContinuousSpell() {
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(this.entityData.get(CONTINUOUS_SPELL)));
        return spell == null ? Spells.NONE : spell;
    }

    @Override
    public void setContinuousSpell(Spell spell) {
        this.entityData.set(CONTINUOUS_SPELL, spell.getLocation().toString());
    }

    @Override
    public int getSpellCounter() {
        return this.entityData.get(SPELL_COUNTER);
    }

    @Override
    public void setSpellCounter(int count) {
        this.entityData.set(SPELL_COUNTER, count);
    }

    public int getSpellTargetId() {
        return this.entityData.get(SPELL_TARGET_ID);
    }

    public void setSpellTargetId(int targetId) {
        this.entityData.set(SPELL_TARGET_ID, targetId);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
