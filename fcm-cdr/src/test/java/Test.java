import java.io.File;

/**
 * Java测试
 * @author johnson liu
 * @date 2021/6/2 14:44
 */
public class Test {
    public static void main(String[] args) {

    }

    public int getFileCount(File file) {
        int count = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if(f.isFile()){
                    count++;
                }
                count += getFileCount(f);
            }
        }
        return count;
    }
}
