# How to Test the Temporary Enchantment System

## Quick Test Guide

### Step 1: Prepare Test Items
1. Get a bow, sword, and axe in your inventory
2. Make sure you have the `ImbueWeaponFire` spell

### Step 2: Cast the Spell
1. Hold one of the weapons (bow, sword, or axe)
2. Cast `ImbueWeaponFire`
3. The weapon should receive a temporary fire enchantment

### Step 3: Observe Visual Feedback
- **Blue/Cyan Durability Bar**: Appears on the weapon
- **Tooltip**: Shows "Temporary enchantment expires in X seconds"
- **Enchantment**: The weapon will have the fire enchantment in its tooltip

### Step 4: Test Functionality

#### For Bow (Flame Enchantment):
1. Shoot arrows
2. Arrows should set targets on fire
3. Enchantment expires after duration

#### For Sword (Fire Aspect II):
1. Attack mobs
2. Mobs should catch fire
3. Higher damage from fire
4. Enchantment expires after duration

#### For Axe (Fire Aspect I):
1. Attack mobs
2. Mobs should catch fire (even though axes don't normally have Fire Aspect!)
3. This demonstrates the flexibility of the new system
4. Enchantment expires after duration

## Expected Behavior

### Visual Indicators
- ✅ Blue/cyan durability bar on enchanted items
- ✅ Bar decreases as time passes
- ✅ Tooltip shows remaining time in seconds
- ✅ Standard enchantment glow effect

### Gameplay
- ✅ Enchantment works exactly like permanent version
- ✅ Automatic removal when duration expires
- ✅ Cannot apply same enchantment twice to same item
- ✅ Works in both single-player and multiplayer
- ✅ Persists through save/load

## Comparison Examples

### Example 1: Flame on Bow

**What You'll See:**
```
Diamond Bow
  Flame I
  Temporary enchantment expires in 60 seconds
```

**How It Works:**
- Arrows shot from this bow will be on fire
- Sets targets ablaze on hit
- After 60 seconds, enchantment disappears automatically

### Example 2: Fire Aspect on Sword

**What You'll See:**
```
Diamond Sword
  Fire Aspect II
  Temporary enchantment expires in 60 seconds
```

**How It Works:**
- Melee attacks set enemies on fire
- Level II means more fire damage
- After 60 seconds, enchantment disappears automatically

### Example 3: Fire Aspect on Axe (New Capability!)

**What You'll See:**
```
Diamond Axe
  Fire Aspect I
  Temporary enchantment expires in 60 seconds
```

**How It Works:**
- Axe attacks now set enemies on fire
- This is NOT normally possible with vanilla Minecraft
- Demonstrates how the new system can apply ANY enchantment to ANY compatible item
- After 60 seconds, enchantment disappears automatically

## Technical Verification

### Check Player Data
The temporary enchantments are stored in player data:
```json
{
  "temporaryEnchantments": [
    {
      "item": "minecraft:diamond_bow",
      "enchantment": "minecraft:flame",
      "level": 1,
      "timeLimit": 1200,
      "uuid": "..."
    }
  ]
}
```

### Check Item NBT
Each enchanted item has a tracking tag:
```
temp_enchant_enchantment.minecraft.flame: "uuid-string"
has_temp_enchant: true
temp_enchant_duration: 1200
temp_enchant_expire_time: <game_time + 1200>
```

## Common Issues and Solutions

### Issue: "Enchantment doesn't apply"
**Solution:** Check if the item already has that temporary enchantment active

### Issue: "Bar doesn't show up"
**Solution:** 
1. Make sure the item doesn't have full durability (for items with durability)
2. Check that the mixin is properly registered
3. Verify client-side rendering

### Issue: "Enchantment doesn't expire"
**Solution:**
1. Check that the player tick event is firing
2. Verify `temporaryEnchantmentTick()` is being called
3. Check console for any errors

### Issue: "Enchantment lost on relog"
**Solution:**
1. Verify NBT serialization is working
2. Check that capability/component is properly attached
3. Look for save/load errors in logs

## Debug Commands (If Available)

If you add debug commands, these would be useful:
```
/ebwizardry tempenchant give <player> <enchantment> <level> <duration>
/ebwizardry tempenchant list <player>
/ebwizardry tempenchant remove <player> <enchantment>
/ebwizardry tempenchant clear <player>
```

## Performance Notes

- System is designed to be efficient
- Ticks only active enchantments (not all items)
- Cleanup happens automatically
- No performance impact when no temporary enchantments are active

## Mod Compatibility

The system is designed to work with:
- ✅ All vanilla enchantments
- ✅ Most modded enchantments
- ✅ Custom enchantment implementations
- ✅ Enchantment level modifiers
- ✅ Other item modification mods

Potential conflicts:
- ⚠️ Mods that completely override enchantment system
- ⚠️ Mods that modify ItemStack rendering in conflicting ways
- ⚠️ Mods that prevent enchantments on certain items

## Additional Test Scenarios

1. **Multiple weapons**: Apply enchantment to multiple items simultaneously
2. **Different durations**: Test with very short (10s) and long (5min) durations
3. **Death test**: Die with enchanted items, verify cleanup
4. **Drop test**: Drop enchanted items, verify they can't be picked up by others with enchantment
5. **Chest test**: Put enchanted items in chest, verify enchantment persistence
6. **PvP test**: Use enchanted weapons in player combat
7. **Mob test**: Use enchanted weapons against various mob types
