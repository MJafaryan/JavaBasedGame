package my.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import my.game.MyGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        Lwjgl3ApplicationConfiguration config = getDefaultConfiguration();
        return new Lwjgl3Application(new MyGame(), config);
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MyGame");

        // تنظیمات فول‌اسکرین
        config.setFullscreenMode(
            Lwjgl3ApplicationConfiguration.getDisplayMode()
        );

        // یا به صورت دستی رزولوشن را مشخص کنید:
        // config.setFullscreenMode(
        //     new Lwjgl3ApplicationConfiguration.DisplayMode(
        //         1920, 1080, 60, 0
        //     )
        // );

        // غیرفعال کردن حالت پنجره‌ای اختیاری
        config.setWindowedMode(1920, 1080); // به عنوان فال‌بک

        // بهبود عملکرد در فول‌اسکرین
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);

        return config;
    }
}
