/*
 * Decompiled with CFR 0_102.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package net.inkyquill.equestria.ca;

import net.inkyquill.equestria.ca.commands.*;
import net.inkyquill.equestria.ca.handlers.*;
import net.inkyquill.equestria.ca.runnable.ConfigUpdater;
import net.inkyquill.equestria.ca.runnable.TimeUpdater;
import net.inkyquill.equestria.ca.settings.CASettings;
import net.inkyquill.equestria.ca.settings.RCsettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.equestria.minecraft.common.commands.MonsterCommandExecutor;
import org.equestria.minecraft.common.monsters.MonstersListener;

import java.util.logging.Logger;

public class CommonAbilities
extends JavaPlugin {
    private Logger log = Logger.getLogger("CommonAbilities");

    public void onEnable() {
        CASettings.plugin = this;
        CASettings.L = getLogger();
        CASettings.L.info("Initializing...");
        CASettings.L.info("Loading configs...");
        CASettings.LoadSettings();
        for(World w: getServer().getWorlds())
        {
            CASettings.GetWorldConfig(w);
        }
        CASettings.chat = new RCsettings();
        CASettings.loadRCConfig();


        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();

        CASettings.L.info("Watching new ponies...");
        LoginListener loginListener = new LoginListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(loginListener, this);
        CASettings.L.info("Brawling...");
        DamageListener damageListener = new DamageListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(damageListener, this);
        CASettings.L.info("Spawning monsters...");
        MonstersListener targetListener = new MonstersListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(targetListener, this);
        CASettings.L.info("Generating epic loot...");
        ItemsListener itemListener = new ItemsListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(itemListener, this);
        CASettings.L.info("Awaiting user commands...");
        MonsterCommandExecutor monstersExecutor = new MonsterCommandExecutor(this);
        this.getCommand("restrictTarget").setExecutor(monstersExecutor);


        this.getCommand("meteo").setExecutor(new WeatherCommand());
        this.getCommand("gms").setExecutor(new GMCommand());
        this.getCommand("gmi").setExecutor(new GMItemCommand());
        this.getCommand("timemanager").setExecutor(new CelestialCommand());
        this.getCommand("eff").setExecutor(new EffectsCommand());
        this.getCommand("death").setExecutor(new DECommands());

        CASettings.L.info("Validating permissions...");
        PluginManager manager = getServer().getPluginManager();
        manager.addPermission(CASettings.weather);
        manager.addPermission(CASettings.gm);
        manager.addPermission(CASettings.gmi);
        manager.addPermission(CASettings.time);
        manager.addPermission(CASettings.effects);
        manager.addPermission(CASettings.death);
        CASettings.L.info("Deploying bugs...");
        manager.registerEvents(new PlayerChatHandler(),this);
        manager.registerEvents(new WorldListener(this),this);

        if (CASettings.TimeEnabled)
            new TimeUpdater().runTaskLater(this, 5);

        ConfigUpdater repeater = new ConfigUpdater();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, repeater, 600);

        CASettings.L.info("Plugin successfully initialized...");
    }

    @Override
    public void onDisable(){
        PluginManager manager = getServer().getPluginManager();
        manager.removePermission(CASettings.weather);
        manager.removePermission(CASettings.gm);
        manager.removePermission(CASettings.gmi);
        manager.removePermission(CASettings.time);
        manager.removePermission(CASettings.effects);
        manager.removePermission(CASettings.death);
        try{CASettings.SaveConfigs();}
        catch(Exception e){log.info("Couldn't save configs: " + e.getMessage());}
        CASettings.L.info("Plugin successfully deinitialized...");
    }


    public String getConfigItem(String s) {
        return this.getConfig().getString(s);
    }

    public boolean getBoolConfigItem(String s) {
        return this.getConfig().getBoolean(s);
    }
}

