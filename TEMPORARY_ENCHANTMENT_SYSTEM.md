# Temporary Enchantment System

## Overview

The new temporary enchantment system allows ANY enchantment (vanilla or modded) to be applied temporarily to items. Unlike the old imbuement system which required custom enchantment classes, this system works with existing enchantments.

## Key Features

- **Works with ANY enchantment**: Use vanilla enchantments like Flame, Fire Aspect, Sharpness, etc., or any modded enchantment
- **Flexible application**: Apply enchantments to any item, even non-standard combinations (e.g., Fire Aspect on an axe)
- **Automatic cleanup**: Enchantments are automatically removed when their duration expires
- **Visual feedback**: Items show a blue durability bar and tooltip with remaining time
- **Player data persistence**: All temporary enchantments are saved in player data

## Comparison with Old System

### Old Imbuement System
```java
// Required custom enchantment class
public class TimedEnchantment extends Enchantment implements Imbuement {
    // Custom implementation required
}

// Could only use mod's custom enchantments
data.setImbuementDuration(stack, EBEnchantments.FLAMING_WEAPON.get(), duration);
```

### New Temporary Enchantment System
```java
// Use ANY enchantment - vanilla or modded
data.setTemporaryEnchantment(stack, Enchantments.FLAMING_ARROWS, 1, duration);
data.setTemporaryEnchantment(stack, Enchantments.FIRE_ASPECT, 2, duration);
data.setTemporaryEnchantment(stack, Enchantments.SHARPNESS, 5, duration);
```

## Usage Examples

### Example 1: Apply Flame to Bow (Vanilla)
```java
@Override
public boolean cast(PlayerCastContext ctx) {
    SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.getCaster());
    ItemStack bow = ctx.getCaster().getMainHandItem();
    
    if (bow.getItem() instanceof BowItem) {
        // Apply Flame I for 60 seconds (1200 ticks)
        data.setTemporaryEnchantment(bow, Enchantments.FLAMING_ARROWS, 1, 1200);
        return true;
    }
    return false;
}
```

### Example 2: Apply Fire Aspect to Sword (Vanilla)
```java
@Override
public boolean cast(PlayerCastContext ctx) {
    SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.getCaster());
    ItemStack sword = ctx.getCaster().getMainHandItem();
    
    if (sword.getItem() instanceof SwordItem) {
        // Apply Fire Aspect II for 90 seconds (1800 ticks)
        data.setTemporaryEnchantment(sword, Enchantments.FIRE_ASPECT, 2, 1800);
        return true;
    }
    return false;
}
```

### Example 3: Apply Fire to Axe (Non-Standard Combination)
```java
@Override
public boolean cast(PlayerCastContext ctx) {
    SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.getCaster());
    ItemStack axe = ctx.getCaster().getMainHandItem();
    
    if (axe.getItem() instanceof AxeItem) {
        // Apply Fire Aspect I to axe - works even though axes don't normally have Fire Aspect!
        data.setTemporaryEnchantment(axe, Enchantments.FIRE_ASPECT, 1, 1200);
        return true;
    }
    return false;
}
```

### Example 4: Complete Spell Implementation
See `ImbueWeaponFire.java` for a complete example that handles multiple weapon types:
- Bows get Flame
- Swords get Fire Aspect II
- Axes get Fire Aspect I

## API Methods

### SpellManagerData Interface

```java
// Apply a temporary enchantment
void setTemporaryEnchantment(ItemStack stack, Enchantment enchantment, int level, int duration);

// Get remaining duration of a temporary enchantment
int getTemporaryEnchantmentDuration(ItemStack stack, Enchantment enchantment);

// Remove a temporary enchantment
boolean removeTemporaryEnchantment(ItemStack stack, Enchantment enchantment);

// Get all active temporary enchantments (internal use)
List<TemporaryEnchantmentLoader> getTemporaryEnchantmentLoaders();
```

### TemporaryEnchantmentData Interface (Item Data)

```java
// Check if item has temporary enchantments
boolean hasTemporaryEnchantment();

// Get/set expire time
long getExpireTime();
void setExpireTime(long expireTime);

// Get remaining lifetime
int getRemainingLifetime(long currentGameTime);
```

## Visual Feedback

Items with temporary enchantments display:
1. **Blue durability bar**: Indicates enchantment is temporary
2. **Tooltip**: Shows "Temporary enchantment expires in X seconds"
3. **Bar color gradient**: Cyan to blue gradient as time expires

## Technical Details

### Data Storage
- **ItemStack NBT**: Each temporary enchantment is tagged with a UUID for tracking
- **Player Data**: All active temporary enchantments are stored in SpellManagerData for cleanup

### Automatic Cleanup
The system automatically:
1. Ticks down each enchantment's duration every game tick
2. Removes enchantments when duration reaches 0
3. Cleans up tracking data from both item and player

### Platform Support
- **Forge**: Uses Capabilities system
- **Fabric**: Uses Cardinal Components API
- Both implementations share the same API

## Migration Guide

### From Old Imbuement System

**Old Code:**
```java
if (enchantment instanceof Imbuement) {
    data.setImbuementDuration(stack, enchantment, duration);
}
```

**New Code:**
```java
// No type check needed - works with any enchantment!
data.setTemporaryEnchantment(stack, enchantment, level, duration);
```

### Backwards Compatibility

The old `Imbuement` interface and `TimedEnchantment` class are deprecated but still functional for backwards compatibility.

## Best Practices

1. **Check duration before applying**: Prevent overwriting existing temporary enchantments
   ```java
   if (data.getTemporaryEnchantmentDuration(stack, enchantment) <= 0) {
       data.setTemporaryEnchantment(stack, enchantment, level, duration);
   }
   ```

2. **Use appropriate durations**: Consider game balance
   - Short buffs: 600-1200 ticks (30-60 seconds)
   - Medium buffs: 1200-2400 ticks (60-120 seconds)
   - Long buffs: 2400-6000 ticks (2-5 minutes)

3. **Provide visual/audio feedback**: Let players know the enchantment was applied
   ```java
   playSound(ctx.getLevel(), ctx.getCaster(), 0, -1, ctx.getModifiers());
   ```

4. **Consider enchantment compatibility**: Some enchantment combinations may not work well together

## Future Enhancements

Potential improvements to the system:
- Multiple temporary enchantments on same item
- Enchantment stacking rules
- Custom visual effects per enchantment type
- Duration modifiers based on spell potency
- Enchantment removal on item drop/death configuration
