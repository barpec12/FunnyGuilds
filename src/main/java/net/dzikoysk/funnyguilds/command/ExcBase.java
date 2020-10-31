package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcBase implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!config.regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (!config.baseEnable) {
            player.sendMessage(messages.baseTeleportationDisabled);
            return;
        }

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        Guild guild = user.getGuild();

        if (user.getCache().getTeleportation() != null) {
            player.sendMessage(messages.baseIsTeleportation);
            return;
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.base")
                ? Collections.emptyList()
                : config.baseItems;

        if (! this.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        ItemStack[] items = ItemUtils.toArray(requiredItems);
        player.getInventory().removeItem(items);

        if (config.baseDelay < 1) {
            player.teleport(guild.getHome());
            player.sendMessage(messages.baseTeleport);
            return;
        }

        int time = player.hasPermission("funnyguilds.vip.baseTeleportTime") ? config.baseDelayVip : config.baseDelay;
        Location before = player.getLocation();
        AtomicInteger timeCounter = new AtomicInteger(1);
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                player.sendMessage(messages.baseMove);
                cache.setTeleportation(null);
                player.getInventory().addItem(items);
                return;
            }

            if (timeCounter.getAndIncrement() > time) {
                cache.getTeleportation().cancel();
                player.sendMessage(messages.baseTeleport);
                player.teleport(guild.getHome());
                cache.setTeleportation(null);
            }
        }, 0L, 20L));

        player.sendMessage(messages.baseDontMove.replace("{TIME}", Integer.toString(time)));
    }

}
