package gjum.minecraft.forge.civrelay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Config {
    @Expose
    public boolean modEnabled = true;

    @Expose
    public List<Filter> filters = new ArrayList<>();

    public static final Config instance = new Config();

    private File configFile;

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public boolean load(File file) {
        if (file != null) configFile = file;

        try (FileReader reader = new FileReader(configFile)) {
            Config newConf = gson.fromJson(reader, this.getClass());
            modEnabled = newConf.modEnabled;
            filters = newConf.filters;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(File file) {
        if (file != null) configFile = file;

        String json = gson.toJson(this);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            fos.write(json.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
