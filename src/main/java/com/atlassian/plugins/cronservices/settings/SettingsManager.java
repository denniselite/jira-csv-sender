package com.atlassian.plugins.cronservices.settings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.util.concurrent.Nullable;
import com.atlassian.util.concurrent.NotNull;

public class SettingsManager {
	
	private final PluginSettings pluginSettings;
	
	public SettingsManager(PluginSettingsFactory pluginSettingsFactory){
		pluginSettings = pluginSettingsFactory.createSettingsForKey("jiracsvsender");
	}
	
	@NotNull
	protected static String getKey(@NotNull String prefix, @Nullable Object...arg){
		final StringBuilder key = new StringBuilder(prefix);
		
		if (arg != null){
			for (Object obj:arg){
				key.append(obj.toString());
			}
		}
		
		return key.toString();
	}
	
	public void setValue(@NotNull String key, @Nullable Object value){
		pluginSettings.put(key, value);
	}
	
//	@Nullable
	public Object getValue(@NotNull String key){
		return pluginSettings.get(key);
	}
	
	public void remove(@NotNull String key){
		pluginSettings.remove(key);
	}
}