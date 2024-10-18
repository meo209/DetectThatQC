package com.meo209.detectthatqc

import com.google.gson.GsonBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import me.x150.renderer.event.RenderEvents
import me.x150.renderer.render.Renderer3d
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.DispenserBlock
import net.minecraft.block.DropperBlock
import net.minecraft.block.PistonBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class DetectThatQCClient : ClientModInitializer {

    private val highlightedBlocks = mutableSetOf<BlockPos>()

    private val configHandler = ConfigClassHandler.createBuilder(Config::class.java)
        .id(Identifier.of("detectthatqc", "config"))
        .serializer { config ->
            GsonConfigSerializerBuilder.create(config)
                .setPath(FabricLoader.getInstance().configDir.resolve("detectthatqc.json5"))
                .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                .setJson5(true)
                .build()
        }.build()

    override fun onInitializeClient() {
        configHandler.load()

        RenderEvents.WORLD.register(this::render)
        Renderer3d.renderThroughWalls()

        ClientTickEvents.END_CLIENT_TICK.register {
            val client = MinecraftClient.getInstance()
            val world = client.world ?: return@register
            val player = client.player ?: return@register

            val newDetectedBlocks = mutableSetOf<BlockPos>()

            val playerPos = player.blockPos
            val range = Config.scanRange
            for (x in -range..range) {
                for (y in -range..range) {
                    for (z in -range..range) {
                        val pos = playerPos.add(x, y, z)
                        if (isBudPowered(world, pos)) {
                            newDetectedBlocks.add(pos)
                        }
                    }
                }
            }

            if (newDetectedBlocks != highlightedBlocks) {
                highlightedBlocks.clear()
                highlightedBlocks.addAll(newDetectedBlocks)
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            configHandler.save()
        }
    }

    fun render(stack: MatrixStack) {
        highlightedBlocks.forEach { pos ->
            val originPos = Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            Renderer3d.renderFilled(stack, Config.highlightBlockColor, originPos, Vec3d(1.0, 1.0, 1.0))
        }
    }

    private fun isBudPowered(world: World, pos: BlockPos): Boolean {
        val blockState = world.getBlockState(pos)
        val block = blockState.block

        val isBudCapable = block is PistonBlock || block is DispenserBlock || block is DropperBlock
        if (!isBudCapable) {
            return false
        }

        if (world.isReceivingRedstonePower(pos)) {
            return false
        }

        if (world.isReceivingRedstonePower(pos.up())) {
            return true
        }

        return false
    }
}
