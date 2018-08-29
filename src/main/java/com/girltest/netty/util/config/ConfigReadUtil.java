package com.girltest.netty.util.config;

import com.common.util.ReflectHWUtils;
import com.file.hw.props.GenericReadPropsUtil;
import com.girltest.netty.config.ClientConfigDto;
import com.girltest.netty.util.PrintUtil;
import com.string.widget.util.ValueWidget;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

/***
 * 用于读取配置文件 <br>
 */
public class ConfigReadUtil {

    public static void main(String[] args) {
        try {
            ClientConfigDto clientConfigDto = readConfig("/Users/whuanghkl/code/mygit/netty/netty_chat_demo_github/src/main/resource/config.properties");
            PrintUtil.print(clientConfigDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ClientConfigDto readConfig(String path) throws IOException {
        Properties properties = GenericReadPropsUtil.getProperties(false, path);
        ClientConfigDto clientConfigDto = new ClientConfigDto();
        convert(properties, clientConfigDto);
        return clientConfigDto;
    }

    public static <T> Object convert(final Properties properties, T configObject) {
        if (null == properties) {
            return null;
        }
        if (null == configObject) {
            return null;
        }
        List<Field> fieldList = ReflectHWUtils.getAllFieldList(configObject.getClass());
        if (ValueWidget.isNullOrEmpty(fieldList)) {
            return configObject;
        }
        fieldList.stream().forEach(f -> {
            String fieldType = f.getType().getTypeName();
            String fieldName = f.getName();
            System.out.println("fieldType :" + fieldType);
            if (!properties.containsKey(fieldName)) {
                return;
            }
            String val = properties.getProperty(fieldName);
            if (ValueWidget.isNullOrEmpty(val)) {
                return;
            }
            if ("java.lang.String".equals(fieldType)) {
                ReflectHWUtils.setObjectValue(configObject, f, val);
            } else if ("int".equals(fieldType)
                    || "java.lang.Integer".equals(fieldType)) {
                ReflectHWUtils.setObjectValue(configObject, f, Integer.parseInt(val));
            } else if ("long".equals(fieldType)
                    || "java.lang.Long".equals(fieldType)) {
                ReflectHWUtils.setObjectValue(configObject, f, Long.parseLong(val));
            } else if ("boolean".equals(fieldType)
                    || "java.lang.Boolean".equals(fieldType)) {
                ReflectHWUtils.setObjectValue(configObject, f, Boolean.parseBoolean(val));
            }
        });
        return configObject;
    }
}
