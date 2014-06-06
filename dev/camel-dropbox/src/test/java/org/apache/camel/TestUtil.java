package org.apache.camel;

import java.io.File;

public class TestUtil {
    public static final String TEST_DATA_FOLDER;
    public static final String DATA_FOLDER;

    public static final String ﹍ = System.getProperty("file.separator");

    static {
        final File projectFolder = new File(classLocationPath(TestUtil.class)).getParentFile().getParentFile();
        TEST_DATA_FOLDER = projectFolder.getPath() + ﹍ + "src" + ﹍ + "test" + ﹍ + "resources" + ﹍;
        DATA_FOLDER = projectFolder.getPath() + ﹍ + "src" + ﹍ + "main" + ﹍ + "resources" + ﹍;
    }

    private static String classLocationPath(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static final String OUTPUT_FOLDER = TEST_DATA_FOLDER + ﹍ + "testout" + ﹍;

    public static String path(String... file) {
        StringBuilder builder = new StringBuilder();

        for (String f : file) {
            builder.append(f);
            builder.append(﹍);
        }

        return builder.toString();
    }
}
