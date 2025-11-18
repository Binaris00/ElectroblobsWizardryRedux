# Sistema de Encantamientos Temporales - Resumen de Cambios

## Resumen en Español

### Problema Original
El sistema de imbuement (encantamientos temporales) solo permitía usar encantamientos personalizados creados por el mod. Normalmente estos encantamientos eran simplemente versiones temporales de encantamientos vanilla, lo que limitaba mucho la flexibilidad del sistema.

### Solución Implementada
Se ha creado un nuevo sistema que permite usar CUALQUIER encantamiento (vanilla o de mods) como efecto temporal. Ya no es necesario crear clases de encantamientos personalizadas - el sistema funciona directamente con los encantamientos existentes.

### Ejemplos Implementados

#### 1. FLAME (Llama) para Arcos
- **Encantamiento**: `minecraft:flame` (vanilla)
- **Aplicación**: Arcos
- **Efecto**: Las flechas prenden fuego a los objetivos
- **Duración temporal**: 60 segundos por defecto

#### 2. Fire Aspect (Aspecto Ígneo) para Espadas  
- **Encantamiento**: `minecraft:fire_aspect` (vanilla)
- **Aplicación**: Espadas
- **Nivel**: II (fuego más fuerte)
- **Efecto**: Los ataques cuerpo a cuerpo prenden fuego a los enemigos
- **Duración temporal**: 60 segundos por defecto

#### 3. Fire Aspect para Hachas (¡Nuevo!)
- **Encantamiento**: `minecraft:fire_aspect` (vanilla) 
- **Aplicación**: Hachas (normalmente no tienen este encantamiento)
- **Nivel**: I
- **Efecto**: Las hachas ahora pueden prender fuego a los enemigos
- **Duración temporal**: 60 segundos por defecto
- **Nota**: Esto demuestra la flexibilidad del nuevo sistema - ¡puede aplicar cualquier encantamiento a cualquier item!

## Cambios Técnicos Implementados

### 1. Nuevas Interfaces y Clases

```java
// Nueva interfaz para datos de encantamientos temporales
TemporaryEnchantmentData
  - Similar a ConjureData
  - Almacena tiempo de expiración
  - Gestiona duración

// Nueva clase para rastrear encantamientos
TemporaryEnchantmentLoader  
  - Reemplaza ImbuementLoader
  - Funciona con cualquier encantamiento
  - No requiere interfaces especiales
```

### 2. API Actualizada

```java
// Métodos nuevos en SpellManagerData
void setTemporaryEnchantment(ItemStack stack, Enchantment enchantment, int level, int duration);
int getTemporaryEnchantmentDuration(ItemStack stack, Enchantment enchantment);
boolean removeTemporaryEnchantment(ItemStack stack, Enchantment enchantment);
List<TemporaryEnchantmentLoader> getTemporaryEnchantmentLoaders();
```

### 3. Sistema Visual

- **Barra de durabilidad**: Color azul/cyan para items con encantamientos temporales
- **Tooltip**: Muestra "Temporary enchantment expires in X seconds"
- **Gradiente**: De cyan a azul mientras expira el tiempo

### 4. Implementaciones de Plataforma

- **Forge**: Usa el sistema de Capabilities
- **Fabric**: Usa Cardinal Components API
- Ambas comparten la misma API común

## Comparación: Antes vs Después

### Sistema Antiguo (Deprecado)

```java
// Requería clase de encantamiento personalizada
public class TimedEnchantment extends Enchantment implements Imbuement {
    // Implementación personalizada requerida
}

// Registro del encantamiento personalizado
public static final DeferredObject<Enchantment> FLAMING_WEAPON = 
    enchantment("flaming_weapon", new TimedEnchantment());

// Uso limitado
if (enchantment instanceof Imbuement) {
    data.setImbuementDuration(stack, enchantment, duration);
}
```

**Limitaciones:**
- ❌ Solo encantamientos creados por el mod
- ❌ Requiere crear clases para cada encantamiento
- ❌ No puede usar encantamientos vanilla directamente
- ❌ Inflexible para combinaciones no estándar

### Sistema Nuevo

```java
// No requiere clases personalizadas - usa encantamientos existentes
SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);

// Ejemplo 1: Flame en arco (vanilla)
data.setTemporaryEnchantment(bow, Enchantments.FLAMING_ARROWS, 1, 1200);

// Ejemplo 2: Fire Aspect en espada (vanilla)
data.setTemporaryEnchantment(sword, Enchantments.FIRE_ASPECT, 2, 1200);

// Ejemplo 3: Fire Aspect en hacha (¡combinación no estándar!)
data.setTemporaryEnchantment(axe, Enchantments.FIRE_ASPECT, 1, 1200);

// También funciona con encantamientos de mods
data.setTemporaryEnchantment(item, SomeMod.CUSTOM_ENCHANT, 3, 1200);
```

**Ventajas:**
- ✅ Cualquier encantamiento (vanilla o mods)
- ✅ Sin clases personalizadas necesarias
- ✅ Combinaciones flexibles (ej: Fire Aspect en hacha)
- ✅ Más fácil de usar y mantener
- ✅ Retrocompatible con sistema antiguo

## Ejemplo de Hechizo: ImbueWeaponFire

```java
public class ImbueWeaponFire extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.getCaster());
        int duration = 1200; // 60 segundos
        
        for (ItemStack stack : InventoryUtil.getPrioritisedHotbarAndOffhand(ctx.getCaster())) {
            Enchantment enchantment = null;
            int level = 1;
            
            if (isBow(stack)) {
                enchantment = Enchantments.FLAMING_ARROWS;  // Flame
                level = 1;
            }
            else if (isSword(stack)) {
                enchantment = Enchantments.FIRE_ASPECT;
                level = 2;
            }
            else if (isAxe(stack)) {
                enchantment = Enchantments.FIRE_ASPECT;  // ¡Funciona en hacha!
                level = 1;
            }
            
            if (enchantment != null) {
                data.setTemporaryEnchantment(stack, enchantment, level, duration);
                return true;
            }
        }
        return false;
    }
}
```

## Características del Nuevo Sistema

### 1. Almacenamiento de Datos
- **ItemStack NBT**: Cada encantamiento temporal se etiqueta con un UUID
- **Datos del Jugador**: Todos los encantamientos activos se guardan en SpellManagerData
- **Persistencia**: Los datos se guardan y cargan correctamente

### 2. Limpieza Automática
- Cuenta regresiva de duración cada tick del juego
- Eliminación automática cuando la duración llega a 0
- Limpieza de datos de rastreo del item y del jugador
- Sincronización entre cliente y servidor

### 3. Retrocompatibilidad
- Sistema antiguo `Imbuement` marcado como deprecado
- `TimedEnchantment` sigue funcionando
- Código existente no se rompe
- Migración gradual posible

### 4. Feedback Visual
- **Barra azul/cyan**: Distingue de items invocados (morados)
- **Tooltip informativo**: Tiempo restante visible
- **Gradiente de color**: Indica tiempo restante visualmente

## Archivos Creados/Modificados

### Nuevos Archivos
- `TemporaryEnchantmentData.java` - Interfaz de datos
- `TemporaryEnchantmentLoader.java` - Gestor de encantamientos
- `TemporaryEnchantmentDataHolder.java` (Forge y Fabric) - Implementaciones
- `TemporaryEnchantmentMixin.java` - Feedback visual
- `ImbueWeaponFire.java` - Hechizo de ejemplo
- `TEMPORARY_ENCHANTMENT_SYSTEM.md` - Documentación
- `TESTING_GUIDE.md` - Guía de pruebas

### Archivos Modificados
- `SpellManagerData.java` - Nuevos métodos API
- `SpellManagerDataHolder.java` (Forge y Fabric) - Implementaciones
- `IObjectData.java` - Soporte para nuevos datos
- `ForgeObjectData.java` / `FabricObjectData.java` - Implementaciones de plataforma
- `DataEvents.java` - Tick de encantamientos temporales
- `WizardryForgeEvents.java` - Registro de capabilities
- `EBComponents.java` - Registro de componentes (Fabric)
- `ebwizardry.mixins.json` - Registro de mixin
- `en_us.json` - Nueva traducción de tooltip
- `Imbuement.java` - Marcado como deprecado
- `TimedEnchantment.java` - Marcado como deprecado

## Casos de Uso

### Caso 1: Hechizo de Fuego Temporal
Un mago puede encantar temporalmente sus armas con fuego sin necesidad de un yunque o libros de encantamiento.

### Caso 2: Buff de Combate
Durante una batalla, aplica rápidamente Fire Aspect a tu hacha para mayor daño.

### Caso 3: Versatilidad Táctica  
Cambia entre diferentes armas encantadas temporalmente según la situación.

### Caso 4: Compatibilidad con Mods
Funciona con cualquier encantamiento de cualquier mod compatible con Minecraft.

## Próximos Pasos Posibles

1. **Múltiples encantamientos**: Permitir varios encantamientos temporales en el mismo item
2. **Efectos visuales personalizados**: Partículas específicas por tipo de encantamiento
3. **Modificadores de duración**: Basados en potencia del hechizo o nivel del jugador
4. **Configuración**: Opciones para comportamiento al morir o soltar items
5. **Interfaz de usuario**: Panel mostrando encantamientos temporales activos
6. **Sonidos**: Efectos de audio cuando se aplica/expira un encantamiento
7. **Integración con otros sistemas**: Combinar con otros sistemas del mod

## Conclusión

El nuevo sistema de encantamientos temporales proporciona:
- **Mayor flexibilidad**: Usa cualquier encantamiento
- **Mejor experiencia**: Feedback visual claro
- **Más posibilidades**: Combinaciones no estándar
- **Fácil de usar**: API simple y directa
- **Bien documentado**: Guías y ejemplos completos

Este cambio hace que el sistema sea mucho más potente y flexible, permitiendo a los desarrolladores crear experiencias más interesantes y variadas para los jugadores.
