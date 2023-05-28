package com.connexal.ravelcraft.shared.util;

import com.connexal.ravelcraft.shared.RavelInstance;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class RavelConfig {
    private ConfigSection config;
    private final Path file;
    private boolean correct = false;
    private final RavelLogger logger;

    public RavelConfig(Path file) {
        this.file = file;
        this.logger = RavelInstance.getLogger();

        this.load(new ConfigSection());
    }

    private void load(ConfigSection defaults) {
        this.correct = true;

        if (!Files.exists(this.file)) {
            try {
                Files.createDirectories(this.file.getParent());
                Files.createFile(this.file);
            } catch (IOException e) {
                this.logger.error("Failed to create config file at " + this.file.toAbsolutePath(), e);
                this.correct = false;
                return;
            }

            this.config = defaults;
            this.save();
        } else {
            if (this.correct) {
                String path;
                try {
                    path = Files.readString(this.file);
                } catch (IOException e) {
                    logger.error("Failed to read config file at " + this.file.toAbsolutePath(), e);
                    return;
                }

                this.parseContent(path);

                if (!this.correct) {
                    return;
                }

                if (this.setDefault(defaults) > 0) {
                    this.save();
                }
            }
        }
    }

    public void reload() {
        this.config.clear();
        this.correct = false;

        if (this.file == null) {
            throw new IllegalStateException("Failed to reload Config. File object is undefined.");
        }

        this.load(new ConfigSection());
    }

    public void save() {
        if (this.file == null) {
            throw new IllegalStateException("Failed to save Config. File object is undefined.");
        }

        if (this.correct) {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(dumperOptions);
            StringBuilder content = new StringBuilder(yaml.dump(this.config));

            try {
                Files.writeString(this.file, content.toString());
            } catch (IOException e) {
                this.logger.error("Failed to save config file at " + this.file.toAbsolutePath(), e);
            }
        }
    }
    
    public void set(String path, Object value) {
        this.config.set(path, value);
    }

    public Object get(String path) {
        return this.config.get(path);
    }

    public int getInt(String path) {
        return this.config.get(path, (Number) 0).intValue();
    }

    public boolean isInt(String path) {
        Object val = this.config.get(path);
        return val instanceof Integer;
    }

    public long getLong(String path) {
        return this.config.get(path, (Number) 0).longValue();
    }

    public boolean isLong(String path) {
        Object val = this.config.get(path);
        return val instanceof Long;
    }

    public double getDouble(String path) {
        return this.config.get(path, (Number) 0).doubleValue();
    }

    public boolean isDouble(String path) {
        Object val = this.config.get(path);
        return val instanceof Double;
    }

    public String getString(String path) {
        Object result = this.config.get(path, "");
        return String.valueOf(result);
    }

    public boolean isString(String path) {
        Object val = this.get(path);
        return val instanceof String;
    }

    public boolean getBoolean(String path) {
        return this.config.get(path, false);
    }

    public boolean isBoolean(String path) {
        Object val = this.get(path);
        return val instanceof Boolean;
    }

    public List getList(String path) {
        return this.config.get(path, null);
    }

    public boolean isList(String path) {
        Object val = this.get(path);
        return val instanceof List;
    }

    public List<String> getStringList(String path) {
        List value = this.getList(path);

        if (value == null) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<>();

        for (Object o : value) {
            if (o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof Character) {
                result.add(String.valueOf(o));
            }
        }
        return result;
    }

    public List<Integer> getIntegerList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Integer> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer) object);
            } else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String) object));
                } catch (Exception ex) {
                    //ignore
                }
            } else if (object instanceof Character) {
                result.add((int) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }
        return result;
    }

    public List<Boolean> getBooleanList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Boolean> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            } else if (object instanceof String) {
                if (Boolean.TRUE.toString().equals(object)) {
                    result.add(true);
                } else if (Boolean.FALSE.toString().equals(object)) {
                    result.add(false);
                }
            }
        }
        return result;
    }

    public List<Double> getDoubleList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Double> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Double) {
                result.add((Double) object);
            } else if (object instanceof String) {
                try {
                    result.add(Double.valueOf((String) object));
                } catch (Exception ex) {
                    //ignore
                }
            } else if (object instanceof Character) {
                result.add((double) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }
        return result;
    }

    public List<Float> getFloatList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Float> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Float) {
                result.add((Float) object);
            } else if (object instanceof String) {
                try {
                    result.add(Float.valueOf((String) object));
                } catch (Exception ex) {
                    //ignore
                }
            } else if (object instanceof Character) {
                result.add((float) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }
        return result;
    }

    public List<Long> getLongList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Long> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Long) {
                result.add((Long) object);
            } else if (object instanceof String) {
                try {
                    result.add(Long.valueOf((String) object));
                } catch (Exception ex) {
                    //ignore
                }
            } else if (object instanceof Character) {
                result.add((long) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }
        return result;
    }

    public List<Byte> getByteList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Byte> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Byte) {
                result.add((Byte) object);
            } else if (object instanceof String) {
                try {
                    result.add(Byte.valueOf((String) object));
                } catch (Exception ex) {
                    //ignore
                }
            } else if (object instanceof Character) {
                result.add((byte) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    public List<Character> getCharacterList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Character> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            } else if (object instanceof String) {
                String str = (String) object;

                if (str.length() == 1) {
                    result.add(str.charAt(0));
                }
            } else if (object instanceof Number) {
                result.add((char) ((Number) object).intValue());
            }
        }

        return result;
    }

    public List<Short> getShortList(String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Short> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Short) {
                result.add((Short) object);
            } else if (object instanceof String) {
                try {
                    result.add(Short.valueOf((String) object));
                } catch (Exception ex) {
                    //ignore
                }
            } else if (object instanceof Character) {
                result.add((short) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }

    public List<Map> getMapList(String path) {
        List<Map> list = this.getList(path);
        List<Map> result = new ArrayList<>();

        if (list == null) {
            return result;
        }

        for (Object object : list) {
            if (object instanceof Map) {
                result.add((Map) object);
            }
        }

        return result;
    }

    public boolean contains(String key, boolean ignoreCase) {
        if (ignoreCase) {
            key = key.toLowerCase();
        }

        for (String existKey : this.getKeys(true)) {
            if (ignoreCase) {
                existKey = existKey.toLowerCase();
            }
            if (existKey.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String key) {
        return this.contains(key, false);
    }

    public void remove(String path) {
        this.config.remove(path);
    }

    public List<String> getKeys(boolean deep) {
        return this.config.getKeys(deep);
    }

    public List<String> getKeys() {
        return this.getKeys(true);
    }

    private int setDefault(ConfigSection map) {
        int size = this.config.size();
        this.fillDefaults(map, this.config);
        return this.config.size() - size;
    }

    private ConfigSection fillDefaults(ConfigSection defaultMap, ConfigSection data) {
        for (String key : defaultMap.keySet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaultMap.get(key));
            }
        }
        return data;
    }

    private void parseContent(String content) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        this.config = new ConfigSection(yaml.loadAs(content, LinkedHashMap.class));
    }

    private static class ConfigSection extends LinkedHashMap<String, Object> {
        public ConfigSection() {
            super();
        }

        public ConfigSection(LinkedHashMap<String, Object> map) {
            this();
            if (map.isEmpty()) {
                return;
            }

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof LinkedHashMap) {
                    super.put(entry.getKey(), new ConfigSection((LinkedHashMap) entry.getValue()));
                } else if (entry.getValue() instanceof List) {
                    super.put(entry.getKey(), parseList((List) entry.getValue()));
                } else {
                    super.put(entry.getKey(), entry.getValue());
                }
            }
        }

        private List parseList(List list) {
            List<Object> newList = new ArrayList<>();

            for (Object o : list) {
                if (o instanceof LinkedHashMap) {
                    newList.add(new ConfigSection((LinkedHashMap) o));
                } else {
                    newList.add(o);
                }
            }

            return newList;
        }

        public ConfigSection getAll() {
            return new ConfigSection(this);
        }

        public Object get(String path) {
            return this.get(path, null);
        }

        public <T> T get(String path, T defaultValue) {
            if (path.isEmpty()) {
                return defaultValue;
            }

            if (super.containsKey(path)) {
                return (T) super.get(path);
            }

            String[] keys = path.split("\\.", 2);
            if (!super.containsKey(keys[0])) {
                return defaultValue;
            }
            Object value = super.get(keys[0]);
            if (value instanceof ConfigSection) {
                ConfigSection section = (ConfigSection) value;
                return section.get(keys[1], defaultValue);
            }
            return defaultValue;
        }

        public void set(String path, Object value) {
            String[] subKeys = path.split("\\.", 2);
            if (subKeys.length > 1) {
                ConfigSection childSection = new ConfigSection();
                if (this.containsKey(subKeys[0]) && super.get(subKeys[0]) instanceof ConfigSection) {
                    childSection = (ConfigSection) super.get(subKeys[0]);
                }
                childSection.set(subKeys[1], value);
                super.put(subKeys[0], childSection);
            } else {
                super.put(subKeys[0], value);
            }
        }

        public void remove(String path) {
            if (path.isEmpty()) {
                return;
            }

            if (super.containsKey(path)) {
                super.remove(path);
            } else if (this.containsKey(".")) {
                String[] keys = path.split("\\.", 2);
                if (super.get(keys[0]) instanceof ConfigSection) {
                    ConfigSection section = (ConfigSection) super.get(keys[0]);
                    section.remove(keys[1]);
                }
            }
        }

        public List<String> getKeys(boolean deep) {
            List<String> keys = new LinkedList<>();
            this.forEach((key, value) -> {
                keys.add(key);
                if (value instanceof ConfigSection) {
                    if (deep) {
                        ((ConfigSection) value).getKeys(true).forEach(childKey -> keys.add(key + "." + childKey));
                    }
                }
            });
            return keys;
        }
    }
}
