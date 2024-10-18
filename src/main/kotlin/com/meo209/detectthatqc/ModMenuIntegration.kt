package com.meo209.detectthatqc

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import net.minecraft.text.Text
import java.awt.Color

class ModMenuIntegration : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent ->
            YetAnotherConfigLib.createBuilder()
                .title(Text.of("QC Detector Config"))
                .category(ConfigCategory.createBuilder()
                    .name(Text.of("General"))
                    .option(Option.createBuilder<Int>()
                        .name(Text.of("Scan range"))
                        .description(OptionDescription.of(Text.of("Range to scan for blocks")))
                        .binding(10, Config::scanRange) { Config.scanRange = it }
                        .controller(IntegerFieldControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Color>()
                        .name(Text.of("Highlight Color"))
                        .description(OptionDescription.of(Text.of("Color to highlight detected blocks")))
                        .binding(Color(250, 0, 250, 100), Config::highlightBlockColor) { Config.highlightBlockColor = it }
                        .controller(ColorControllerBuilder::create)
                        .build())
                    .build())
                .build()
                .generateScreen(parent)
        }
    }

}