package xyz.oribuin.eternalcrafting.managers;

import xyz.oribuin.eternalcrafting.EternalCrafting;

public abstract class Manager {

    protected final EternalCrafting plugin;

    public Manager(EternalCrafting plugin) {
        this.plugin = plugin;
    }

    public abstract void reload();
}
