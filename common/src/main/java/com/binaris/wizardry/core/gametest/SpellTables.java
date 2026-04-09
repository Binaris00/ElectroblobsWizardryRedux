package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.item.WizardArmorType;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.event.WizardryEventBus;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.steppschuh.markdowngenerator.table.Table;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Class mostly used in minecraft gametesting framework for checking spell values in different contexts. Made to allow
 * quick rebalance and test of spell cast values, this is made by used a Markdown table to show the spell values based
 * on cast context (rows) and scenarios (columns). The rows contain the whole context and the column uses that context
 * in the wanted scenario to return the result in a readable format.
 */
public class SpellTables {
    private final List<Row> rows = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();

    /** A row represents a context where you're going to cast a spell */
    public record Row(CastContext castContext, Spell spell, String name) {
    }

    /** A column represents the scenario that will return a desired result to be compared */
    public record Column(String name, BiFunction<CastContext, Spell, String> valueProvider) {
        public String getValue(CastContext ctx, Spell spell) {
            return valueProvider.apply(ctx, spell);
        }
    }

    private SpellTables(List<Row> rows, List<Column> columns) {
        this.rows.addAll(rows);
        this.columns.addAll(columns);
    }

    public String generateTable() {
        if (rows.isEmpty() || columns.isEmpty()) return "No rows and/or columns defined for the spell balance test.";

        Table.Builder tableBuilder = new Table.Builder();
        List<String> header = new ArrayList<>();
        header.add("Cases");
        columns.stream().map(column -> column.name).forEach(header::add);
        tableBuilder.addRow(header.toArray());

        for (Row row : rows) {
            List<String> rowData = new ArrayList<>();
            rowData.add(row.name);
            for (Column column : columns) {
                rowData.add(column.getValue(row.castContext, row.spell));
            }
            tableBuilder.addRow(rowData.toArray());
        }

        return "\n\n" + tableBuilder.build().toString();
    }

    @Override
    public String toString() {
        return generateTable();
    }

    public static void addDefaultRows(Builder builder, Spell spell, GameTestHelper helper, Vec3 pos, Element element) {
        builder.addRow(SpellTables.rowPlayer("Vanilla Player", spell, GST.mockPlayer(helper, pos)))
                .addRow(SpellTables.rowPlayer("Player Wizard Armor", spell, GST.mockPlayerWithArmor(helper, pos, element, WizardArmorType.WIZARD)))
                .addRow(SpellTables.rowPlayer("Player Sage Armor", spell, GST.mockPlayerWithArmor(helper, pos, element, WizardArmorType.SAGE)))
                .addRow(SpellTables.rowPlayer("Player Battlemage Armor", spell, GST.mockPlayerWithArmor(helper, pos, element, WizardArmorType.BATTLEMAGE)))
                .addRow(SpellTables.rowPlayer("Player Warlock Armor", spell, GST.mockPlayerWithArmor(helper, pos, element, WizardArmorType.WARLOCK)));
    }

    public static void addDefaultColumns(Builder builder) {
        builder.addColumn(SpellTables.columnByProperty("Cooldown", DefaultProperties.COOLDOWN, SpellModifiers.COOLDOWN))
                .addColumn(SpellTables.columnByProperty("Cost", DefaultProperties.COST, SpellModifiers.COST))
                .addColumn(SpellTables.columnByProperty("Chargeup", DefaultProperties.CHARGEUP, SpellModifiers.CHARGEUP))
                .addColumn(SpellTables.columnByModifiers());
    }

    public static Column columnByProperty(String name, SpellProperty<? extends Number> property, String modifier) {
        return new Column(name, (ctx, spell) -> {
            fireCastEvent(SpellCastEvent.Source.WAND, ctx, spell);
            return String.valueOf(spell.property(property).floatValue() * ctx.modifiers().get(modifier));
        });
    }

    public static Column columnByModifiers() {
        return new Column("Modifiers", (ctx, spell) -> {
            fireCastEvent(SpellCastEvent.Source.WAND, ctx, spell);
            return ctx.modifiers().toString();
        });
    }

    public static Row rowPlayer(String name, Spell spell, Player player) {
        PlayerCastContext ctx = new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, new SpellModifiers());
        return new Row(ctx, spell, name);
    }

    public static void fireCastEvent(SpellCastEvent.Source source, CastContext ctx, Spell spell) {
        WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(source, spell, ctx.caster(), ctx.modifiers()));
    }

    public static class Builder {
        private final List<Row> rows = new ArrayList<>();
        private final List<Column> columns = new ArrayList<>();

        public Builder addRow(Row row) {
            this.rows.add(row);
            return this;
        }

        public Builder addRows(List<Row> rows) {
            this.rows.addAll(rows);
            return this;
        }

        public Builder addColumn(Column column) {
            this.columns.add(column);
            return this;
        }

        public Builder addColumns(List<Column> columns) {
            this.columns.addAll(columns);
            return this;
        }

        public SpellTables build() {
            return new SpellTables(rows, columns);
        }
    }
}
