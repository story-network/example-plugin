package sh.pancake.plugin.example;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.commands.CommandSourceStack;
import sh.pancake.server.plugin.IPancakePlugin;
import sh.pancake.server.plugin.PluginData;
import sh.pancake.server.command.ICommandStack;

public class ExamplePlugin implements IPancakePlugin {

    private PluginData data;

    @Override
    public void init(PluginData data) {
        this.data = data;
        
        System.out.println("Hello world!");
    }

    @Override
    public void onServerPreInit() {
        CommandDispatcher<ICommandStack> dispatcher = data.getServer().getCommandManager().getDispatcherFor(this);

        // Registering sample explode commands
        dispatcher.register(
            LiteralArgumentBuilder.<ICommandStack>literal("explode")
            // Check if executor has op
            .requires((source) -> source.hasPermission(3))
            // Limit 0.0 to max 100.0
            .then(RequiredArgumentBuilder.<ICommandStack, Float>argument("power", FloatArgumentType.floatArg(0.0f, 100.0f))
                .executes((ctx) -> {
                    float power = ctx.getArgument("power", Float.class);

                    CommandSourceStack source = ctx.getSource().getSourceStack();
                    Entity entity = source.getEntity();

                    if (entity == null) {
                        source.sendFailure(new TextComponent("Cannot run on console"));
                        return 0;
                    }

                    source.getLevel().explode(entity, entity.getX(), entity.getY(), entity.getZ(), power, BlockInteraction.DESTROY);
                    source.sendSuccess(new TextComponent("Created explosion with power " + power), false);
                    
                    // We executed once
                    return 1;
                })
            )
        );
    }

}
