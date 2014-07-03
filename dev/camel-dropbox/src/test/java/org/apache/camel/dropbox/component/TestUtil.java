package org.apache.camel.dropbox.component;

import java.io.File;

public class TestUtil {
    public static final String TEST_DATA_FOLDER;
    public static final String DATA_FOLDER;

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    static {
        final File projectFolder = new File(classLocationPath(TestUtil.class)).getParentFile().getParentFile();
        TEST_DATA_FOLDER = projectFolder.getPath() + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "test" + FILE_SEPARATOR + "resources"
                + FILE_SEPARATOR;
        DATA_FOLDER = projectFolder.getPath() + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "resources"
                + FILE_SEPARATOR;
    }

    private static String classLocationPath(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }


    public static final String OUTPUT_FOLDER = TEST_DATA_FOLDER + FILE_SEPARATOR + "testout" + FILE_SEPARATOR;

    public static String path(String... file) {
        StringBuilder builder = new StringBuilder();

        for (String f : file) {
            builder.append(f);
            builder.append(FILE_SEPARATOR);
        }

        return builder.toString();
    }
}
