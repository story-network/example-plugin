package sh.pancake.plugin.example;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion.BlockInteraction;
import sh.pancake.server.plugin.IPancakePlugin;
import sh.pancake.server.plugin.PluginData;

public class ExamplePlugin implements IPancakePlugin {

    private PluginData data;

    @Override
    public void init(PluginData data) {
        this.data = data;
        
        System.out.println("Hello world!");
    }

    @Override
    public void onServerPreInit() {
        CommandDispatcher<CommandSourceStack> dispatcher = data.getServer().getCommandManager().getDispatcherFor(this);

        // Registering sample explode commands
        dispatcher.register(
            LiteralArgumentBuilder.<CommandSourceStack>literal("explode")
            // Check if executor has op
            .requires((source) -> source.hasPermission(3))
            // Limit 0.0 to max 100.0
            .then(RequiredArgumentBuilder.<CommandSourceStack, Float>argument("power", FloatArgumentType.floatArg(0.0f, 100.0f))
                .executes((ctx) -> {
                    float power = ctx.getArgument("power", Float.class);

                    CommandSourceStack source = ctx.getSource();
                    Entity entity = source.getEntity();
                    
                    source.getLevel().explode(entity, entity.getX(), entity.getY(), entity.getX(), power, BlockInteraction.DESTROY);
                    
                    // We executed once
                    return 1;
                })
            )
        );
    }

}
