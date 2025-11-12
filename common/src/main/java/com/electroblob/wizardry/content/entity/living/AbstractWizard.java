package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.living.ISpellCaster;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.NBTExtras;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.content.entity.goal.AttackSpellGoal;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public abstract class AbstractWizard extends PathfinderMob implements ISpellCaster {
    protected Predicate<LivingEntity> entityTargetSelector;

    private static final EntityDataAccessor<Integer> HEAL_COOLDOWN = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> ELEMENT = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> CONTINUOUS_SPELL = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> SPELL_COUNTER = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(AbstractWizard.class, EntityDataSerializers.INT);

    protected List<Spell> spells = new ArrayList<>(4);

    private Set<BlockPos> towerBlocks;

    public AbstractWizard(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAL_COOLDOWN, -1);
        this.entityData.define(ELEMENT, Elements.FIRE.getLocation().toString());
        this.entityData.define(CONTINUOUS_SPELL, Spells.NONE.getLocation().toString());
        this.entityData.define(SPELL_COUNTER, 0);
         this.entityData.define(TEXTURE_INDEX, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(3, new AttackSpellGoal<>(this, 0.5D, 14.0F, 30, 50));

        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, AbstractWizard.class, 5.0F, 0.02F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.entityTargetSelector = entity -> {
            if (entity != null && !entity.isInvisible()) return entity instanceof Enemy;
            return false;
        };

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(AbstractWizard.class));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Mob.class, 0, false, true, this.entityTargetSelector));
    }

    // ============================
    // Wizard data
    // ============================

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5F).add(Attributes.MAX_HEALTH, 30);
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
    public void setContinuousSpell(Spell spell) {
        this.entityData.set(CONTINUOUS_SPELL, spell.getLocation().toString());
    }

    @Override
    public @NotNull Spell getContinuousSpell() {
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(this.entityData.get(CONTINUOUS_SPELL)));
        return spell == null ? Spells.NONE : spell;
    }


    @Override
    public void setSpellCounter(int count) {
        this.entityData.set(SPELL_COUNTER, count);
    }

    @Override
    public int getSpellCounter() {
        return this.entityData.get(SPELL_COUNTER);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        int healCooldown = this.getHealCooldown();

        if (healCooldown == 0 && this.getHealth() < this.getMaxHealth() && this.getHealth() > 0 && !this.hasEffect(EBMobEffects.ARCANE_JAMMER.get())) {
            this.heal(this.getElement() == Elements.HEALING ? 8 : 4);
            this.setHealCooldown(-1);
        } else if (healCooldown == -1 && this.deathTime == 0) {
            if (level().isClientSide) {
                ParticleBuilder.spawnHealParticles(level(), this);
            } else {
                if (this.getHealth() < 10) {
                    this.setHealCooldown(150);
                } else {
                    this.setHealCooldown(400);
                }
                SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(Spells.HEAL.getLocation().getNamespace(), "spell." + Spells.HEAL.getLocation().getPath()));
                level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.PLAYERS, Spells.HEAL.getVolume(), Spells.HEAL.getPitch() + Spells.HEAL.getPitchVariation() * (level().random.nextFloat() - 0.5f));
            }
        }

        if (healCooldown > 0) {
            this.setHealCooldown(healCooldown - 1);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        Element element = this.getElement();
        nbt.putString("element", element == null ? Elements.FIRE.getLocation().toString() : element.getLocation().toString());
        nbt.putInt("skin", this.getTextureIndex());
        NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToTag(spells, spell -> StringTag.valueOf(spell.getLocation().toString())));

        if (this.towerBlocks != null && !this.towerBlocks.isEmpty()) {
            NBTExtras.storeTagSafely(nbt, "towerBlocks", NBTExtras.listToTag(this.towerBlocks, NbtUtils::writeBlockPos));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        Element element = Services.REGISTRY_UTIL.getElement(ResourceLocation.tryParse(nbt.getString("element")));
        this.setElement(element == null ? Elements.FIRE : element);
        this.setTextureIndex(nbt.getInt("skin"));
        this.spells = (List<Spell>) NBTExtras.tagToList(nbt.getList("spells", Tag.TAG_STRING), (StringTag tag) -> Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(tag.getAsString())));

        ListTag tagList = nbt.getList("towerBlocks", Tag.TAG_COMPOUND);
        if (!tagList.isEmpty()) {
            this.towerBlocks = new HashSet<>(NBTExtras.tagToList(tagList, NbtUtils::readBlockPos));
        } else {
            this.towerBlocks = new HashSet<>(NBTExtras.tagToList(nbt.getList("towerBlocks", Tag.TAG_LONG), (LongTag tag) -> BlockPos.of(tag.getAsLong())));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        setTextureIndex(this.random.nextInt(6));

        if (random.nextBoolean()) {
            List<Element> elements = new ArrayList<>(Services.REGISTRY_UTIL.getElements().stream().toList());
            elements.remove(Elements.MAGIC);
            Element element = elements.get(random.nextInt(elements.size()));
            this.setElement(element);
        }

        else {
            this.setElement(Elements.MAGIC);
        }

        Element element = this.getElement();

        for (EquipmentSlot slot : InventoryUtil.ARMOR_SLOTS) this.setItemSlot(slot, new ItemStack(SpellUtil.getArmor(WizardArmorType.WIZARD, element, slot)));
        for (EquipmentSlot slot : EquipmentSlot.values()) this.setDropChance(slot, 0.0f);

        spells.add(Spells.MAGIC_MISSILE);
        SpellTier maxTier = populateSpells(this, spells, element, false, 3, random);

        ItemStack wand = new ItemStack(WandItem.getWand(maxTier, element));
        ArrayList<Spell> list = new ArrayList<>(spells);
        list.add(Spells.HEAL);
        WandHelper.setSpells(wand, List.of(list.toArray(new Spell[5])));
        this.setItemSlot(EquipmentSlot.MAINHAND, wand);

        this.setHealCooldown(50);
        return super.finalizeSpawn(level, difficulty, mobSpawnType, spawnData, tag);
    }

    static SpellTier populateSpells(final Mob wizard, List<Spell> spells, Element e, boolean master, int n, RandomSource random) {
        SpellTier maxTier = SpellTiers.NOVICE;

        List<Spell> npcSpells = SpellUtil.getSpells(Spell::canCastByEntity);

        for (int i = 0; i < n; i++) {
            SpellTier tier;
            Element element = e == Elements.MAGIC ? SpellUtil.getRandomElement(random) : e;

            int randomizer = random.nextInt(20);

            if (randomizer < 10) tier = SpellTiers.NOVICE;
            else if (randomizer < 16)
                tier = SpellTiers.APPRENTICE;
            else if (randomizer < 19 || !master)
                tier = SpellTiers.ADVANCED;
            else
                tier = SpellTiers.MASTER;


            if (tier.level > maxTier.level) maxTier = tier;

            // TODO: Add a filter for NPC spells
            List<Spell> list = SpellUtil.getSpells(spell -> spell.getTier() == tier && spell.getElement() == element);

            list.retainAll(npcSpells);
            list.removeAll(spells);

            if (list.isEmpty()) {
                list = npcSpells;
                list.removeAll(spells);
            }
            if (!list.isEmpty()) spells.add(list.get(random.nextInt(list.size())));
        }
        return maxTier;
    }

    public static ItemStack getBookStackForSpell(Spell spell) {
        ItemStack stack = new ItemStack(EBItems.SPELL_BOOK.get(), 1);
        SpellUtil.setSpell(stack, spell);
        return stack;
    }

    public static ItemStack getItemWithMetadata(Item item, int count, int metadata) {
        ItemStack stack = new ItemStack(item, count);
        CompoundTag tag = new CompoundTag();
        tag.putInt("Spell", metadata);
        stack.addTagElement("Spells", tag);
        return stack;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source.getEntity() instanceof Player) {
            //WizardryAdvancementTriggers.ANGER_WIZARD.triggerFor((Player) source.getEntity());
        }

        return super.hurt(source, damage);
    }

    public void setTowerBlocks(Set<BlockPos> blocks) {
        this.towerBlocks = blocks;
    }

    public boolean isBlockPartOfTower(BlockPos pos) {
        if (this.towerBlocks == null) return false;
        return this.towerBlocks.contains(pos);
    }

//    @SubscribeEvent
//    public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
//        if (!(event.getPlayer() instanceof FakePlayer)) {
//            List<Wizard> wizards = EntityUtils.getEntitiesWithinRadius(64, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPlayer().level, Wizard.class);
//
//            if (!wizards.isEmpty()) {
//                for (Wizard wizard : wizards) {
//                    if (wizard.isBlockPartOfTower(event.getPos())) {
//                        wizard.setLastHurtByMob(event.getPlayer());
//                        WizardryAdvancementTriggers.ANGER_WIZARD.triggerFor(event.getPlayer());
//                    }
//                }
//            }
//        }
//    }
}
