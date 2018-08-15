package com.mcsunnyside.tpsalert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class Main extends JavaPlugin implements Listener {
	private final String name = Bukkit.getServer().getClass().getPackage().getName();
    private final String version = name.substring(name.lastIndexOf('.') + 1);
    private final DecimalFormat format = new DecimalFormat("##.##");
    private Object serverInstance;
    private Field tpsField;
    public String lastTPSstatus =null;
    public String Unknown = null;
    public String Good=null;
    public String Warning = null;
    public String Bad = null;

    public void onEnable() {
    	saveDefaultConfig();
    	Bukkit.getPluginManager().registerEvents(this, this);
    	try {
            serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
            tpsField = serverInstance.getClass().getField("recentTps");
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    	Unknown = getConfig().getString("strings.Unknown");
    	Good=getConfig().getString("strings.Good");
    	Warning=getConfig().getString("strings.Warning");
    	Bad=getConfig().getString("strings.Bad");
    	lastTPSstatus = Unknown;
    	new BukkitRunnable() {
			
			@Override
			public void run() {
				double currentTPS = Double.valueOf(getTPS(0));
				String currentTPSstatus = Unknown;
			//==============================================
				if(currentTPS>=18.25) {
					currentTPSstatus=Good;
				}else if(currentTPS >=17.49) {
					currentTPSstatus=Warning;
				}else {
					currentTPSstatus=Bad;
				}
			//==============================================
				if(lastTPSstatus != currentTPSstatus) {
					printTPSMessage(lastTPSstatus, currentTPSstatus);
				}
				lastTPSstatus = currentTPSstatus;
			}
		}.runTaskTimerAsynchronously(this, getConfig().getInt("settings.checktime"), getConfig().getInt("settings.checktime"));
    }
    
    
    public void printTPSMessage(String lastTPS,String currentTPS) {
    	List<String> list = new ArrayList<String>();
    	list.add("¡ìf");
    	list.add("¡ìf");
    	list.add(getConfig().getString("message.TPSChanged")+lastTPS+getConfig().getString("message.Arrow")+currentTPS);
    	list.add(getConfig().getString("message.Advice")+printTips(currentTPS));
    	list.add("¡ìf");
    	list.add("¡ìf");
    	for (String string : list) {
			Bukkit.broadcastMessage(string);
		}
    }
    public String printTips(String level) {
    	if(level==Unknown) {
    		return getConfig().getString("advice.Unknown");
    	}
    	if(level==Good) {
    		return getConfig().getString("advice.Good");
    	}
    	if(level==Warning) {
    		return getConfig().getString("advice.Warning");
    	}
    	if(level==Bad) {
    		return getConfig().getString("advice.Bad");
    	}
    	return "ÄÚ²¿´íÎó";
    }
	private Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
	public String getTPS(int time) {
        try {
            double[] tps = ((double[]) tpsField.get(serverInstance));
            return format.format(tps[time]);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
