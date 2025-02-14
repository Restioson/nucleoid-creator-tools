package xyz.nucleoid.creator_tools.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;

public final class DimensionOptionsArgument {
    public static final DynamicCommandExceptionType DIMENSION_NOT_FOUND = new DynamicCommandExceptionType(arg ->
            Text.stringifiedTranslatable("text.nucleoid_creator_tools.dimension_options.dimension_not_found", arg)
    );

    public static RequiredArgumentBuilder<ServerCommandSource, Identifier> argument(String name) {
        return CommandManager.argument(name, IdentifierArgumentType.identifier())
                .suggests((context, builder) -> {
                    var source = context.getSource();
                    var registryManager = source.getServer().getCombinedDynamicRegistries().getCombinedRegistryManager();;
                    var dimensions = registryManager.get(RegistryKeys.DIMENSION);

                    return CommandSource.suggestIdentifiers(
                            dimensions.getIds().stream(),
                            builder
                    );
                });
    }

    public static DimensionOptions get(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var identifier = IdentifierArgumentType.getIdentifier(context, name);

        var source = context.getSource();
        var registryManager = source.getServer().getCombinedDynamicRegistries().getCombinedRegistryManager();;
        var dimensions = registryManager.get(RegistryKeys.DIMENSION);

        var dimension = dimensions.get(identifier);
        if (dimension == null) {
            throw DIMENSION_NOT_FOUND.create(identifier);
        }

        return dimension;
    }
}
