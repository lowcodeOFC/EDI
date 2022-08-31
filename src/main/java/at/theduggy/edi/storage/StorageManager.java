package at.theduggy.edi.storage;

import at.theduggy.edi.EDIManager;
import at.theduggy.edi.Main;
import at.theduggy.edi.rendering.OrganisedScore;
import at.theduggy.edi.settings.options.Option;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

public class StorageManager implements Listener {

    public File DATA_FILE = new File(Main.getPlugin(Main.class).getDataFolder() + "/settings.data");

    private Gson gson;
    private Gson getGson(){
        if (gson == null){
            return new GsonBuilder().setPrettyPrinting().create();
        }else {
            return gson;
        }
    }

    public  StorageManager() throws IOException {
        if (Files.exists(DATA_FILE.toPath())){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DATA_FILE, StandardCharsets.UTF_8));
            HashMap<String, SettingsStorageData> dataOfPlayers = getGson().fromJson(bufferedReader,  new TypeToken<HashMap<String, SettingsStorageData>>(){}.getType());
            for (String player : dataOfPlayers.keySet()){
                EDIManager ediManager = new EDIManager(null);
                SettingsStorageData storageData = dataOfPlayers.get(player);
                ediManager.getOptionManager().setDisplayEnabled(storageData.isEdiDisplay());
                ediManager.getOptionManager().setFooterEnabled(storageData.isFooter());
                ediManager.getOptionManager().setHeaderEnabled(storageData.isHeader());
                for (String option_identifier : ediManager.getOptionManager().getRegisteredOptions().keySet()){
                    Option option = ediManager.getOptionManager().getRegisteredOptions().get(option_identifier);
                    OptionStorageData optd = dataOfPlayers.get(player).getOptionsData().get(option_identifier);
                    optd.applyDataToStorage(option);
                }
                Collections.sort(ediManager.getOptionManager().getDisplayIndexList(), Comparator.comparing(Option::getDisplayIndex));
                Main.getEdiPlayerData().put(UUID.fromString(player), ediManager);
            }
            bufferedReader.close();
        }else {
            Files.createFile(DATA_FILE.toPath());
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class),0, 60);
    }

    public void registerUser(Player player){
        Main.getEdiPlayerData().put(player.getUniqueId(), new EDIManager(player));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (!Main.getEdiPlayerData().containsKey(player.getUniqueId())){
            registerUser(player);
        }else {
            Main.getEdiPlayerData().get(player.getUniqueId()).setPlayer(player);
        }
    }

    public void save() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE, StandardCharsets.UTF_8));
        HashMap<String, SettingsStorageData> storageData = new HashMap<>();
        for (UUID k : Main.getEdiPlayerData().keySet()){
            HashMap<String, OptionStorageData> options = new HashMap<>();
            for (String s : Main.getEdiPlayerData().get(k).getOptionManager().getRegisteredOptions().keySet()){
                options.put(s, new OptionStorageData(Main.getEdiPlayerData().get(k).getOptionManager().getRegisteredOptions().get(s)));
            }
            storageData.put(k.toString(), new SettingsStorageData(Main.getEdiPlayerData().get(k).getOptionManager()));
        }
        bw.write(getGson().toJson(storageData));
        bw.close();
    }
}
