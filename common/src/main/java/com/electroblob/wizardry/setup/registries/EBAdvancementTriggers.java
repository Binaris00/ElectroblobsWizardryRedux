package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.content.advancement.SpellCastTrigger;
import com.electroblob.wizardry.content.advancement.SpellDiscoveryTrigger;
import com.electroblob.wizardry.content.advancement.WizardryAdvancementTrigger;
import com.electroblob.wizardry.content.advancement.WizardryContainerTrigger;
import com.electroblob.wizardry.core.mixin.accessor.CriteriaAccessor;

public final class EBAdvancementTriggers {
    public static final WizardryAdvancementTrigger MAX_OUT_WAND = new WizardryAdvancementTrigger("max_out_wand");
    public static final WizardryAdvancementTrigger SPECIAL_UPGRADE = new WizardryAdvancementTrigger("special_upgrade");
    public static final WizardryAdvancementTrigger ANGER_WIZARD = new WizardryAdvancementTrigger("anger_wizard");
    public static final WizardryAdvancementTrigger BUY_MASTER_SPELL = new WizardryAdvancementTrigger("buy_master_spell");
    public static final WizardryAdvancementTrigger WIZARD_TRADE = new WizardryAdvancementTrigger("wizard_trade");
    public static final WizardryAdvancementTrigger SPELL_FAILURE = new WizardryAdvancementTrigger("spell_failure");
    public static final WizardryAdvancementTrigger WAND_LEVELUP = new WizardryAdvancementTrigger("wand_levelup");
    public static final WizardryAdvancementTrigger RESTORE_IMBUEMENT_ALTAR = new WizardryAdvancementTrigger("restore_imbuement_altar");
    public static final SpellCastTrigger CAST_SPELL = new SpellCastTrigger();
    public static final SpellDiscoveryTrigger DISCOVER_SPELL = new SpellDiscoveryTrigger();
    public static final WizardryContainerTrigger ARCANE_WORKBENCH = new WizardryContainerTrigger("arcane_workbench");
    public static final WizardryContainerTrigger IMBUEMENT_ALTAR = new WizardryContainerTrigger("imbuement_altar");
    private EBAdvancementTriggers() {
    }

    public static void register() {
        CriteriaAccessor.callRegister(DISCOVER_SPELL);
        CriteriaAccessor.callRegister(MAX_OUT_WAND);
        CriteriaAccessor.callRegister(SPECIAL_UPGRADE);
        CriteriaAccessor.callRegister(ANGER_WIZARD);
        CriteriaAccessor.callRegister(BUY_MASTER_SPELL);
        CriteriaAccessor.callRegister(SPELL_FAILURE);
        CriteriaAccessor.callRegister(WIZARD_TRADE);
        CriteriaAccessor.callRegister(WAND_LEVELUP);
        CriteriaAccessor.callRegister(RESTORE_IMBUEMENT_ALTAR);
        CriteriaAccessor.callRegister(CAST_SPELL);
        CriteriaAccessor.callRegister(ARCANE_WORKBENCH);
        CriteriaAccessor.callRegister(IMBUEMENT_ALTAR);
    }
}
