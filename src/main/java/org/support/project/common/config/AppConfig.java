package org.support.project.common.config;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.support.project.common.log.Log;
import org.support.project.common.log.LogFactory;
import org.support.project.common.util.StringUtils;
import org.support.project.common.util.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * アプリケーションの設定を保持するクラス
 * 
 * @author Koda
 */
public class AppConfig {
    /** 設定ファイルのパス */
    public static final String APP_CONFIG = "/appconfig.xml";
    /** インスタンス */
    private static AppConfig appConfig = null;
    
    /** インスタンス取得 */
    public static AppConfig get() {
        if (appConfig == null) {
            appConfig = ConfigLoader.load(AppConfig.APP_CONFIG, AppConfig.class);
        }
        return appConfig;
    }
    /**
     * コンストラクタ
     */
    public AppConfig() {
        if (StringUtils.isEmpty(AppConfig.envKey) && !initialize) {
            // 初回の一回目だけ、AppConfig上のenvKeyを読み込む
            try {
                Document document = DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(AppConfig.class.getResourceAsStream(APP_CONFIG));
                NodeList envs = document.getElementsByTagName("envKey");
                if (envs.getLength() > 0) {
                    Node env = envs.item(0);
                    String envKey = env.getFirstChild().getNodeValue();
                    if (StringUtils.isNotEmpty(envKey)) {
                        AppConfig.initEnvKey(envKey);
                    }
                }
            } catch (SAXException | IOException | ParserConfigurationException e) {
                System.err.println(e);
            }
            initialize = true;
        }
    }
    
    /** 初期化したか */
    private boolean initialize = false;
    
    /** システム名 */
    private String systemName;
    /** タイムゾーン */
    private String time_zone;
    /** システムのベースのパス */
    private String basePath;
    /** ベースパスの置換処理を実行したかどうかのフラグ */
    private boolean convBasePath = false;

    /** システムの組み込みデータベースのパス */
    private String databasePath;
    /** 組み込みデータベースのパスを置換処理したかどかのフラグ */
    private boolean convDatabasePath = false;

    /** システムのログのパス */
    private String logsPath;
    /** ログのパスを置換処理したかどかのフラグ */
    private boolean convLogsPath = false;
    
    /** 環境変数をデバッグ出力したかどうかのフラグ(一度だけ出力する) */
    private static boolean dispEnvInfo = false;

    /** ユーザのホームディレクトリ(BasePath)を指定する環境変数のキー */
    private static String envKey = "";
    
    /** 環境変数のキー文字列を設定 */
    public static void initEnvKey(String envKey) {
        if (StringUtils.isNotEmpty(AppConfig.envKey)) {
            System.out.println("envKey was already exists. [old]" + AppConfig.envKey + " [new]" + envKey);
        }
        AppConfig.envKey = envKey;
    }
    /** 環境変数のキー文字列を取得 */
    public static String getEnvKey() {
        return envKey;
    }

    /**
     * パスの中に含まれる予約語を置換
     * 
     * @param path
     * @return
     */
    public String convPath(String path) {
        if (path.indexOf("{user.home}") != -1) {
            String userHome = System.getProperty("user.home");
            if (userHome.endsWith("/")) {
                userHome = userHome.substring(0, userHome.length() - 1);
            }
            path = path.replace("{user.home}", userHome);
        }
        if (path.indexOf("{base.path}") != -1) {
            path = path.replace("{base.path}", getBasePath());
        }
        if (path.indexOf("\\") != -1) {
            path = path.replaceAll("\\\\", "/");
        }
        return path;
    }

    /**
     * @return the time_zone
     */
    public String getTime_zone() {
        return time_zone;
    }

    /**
     * @param time_zone
     *            the time_zone to set
     */
    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }
    
    /**
     * 組み込み用データベースのパスを取得
     * @return
     */
    public String getDatabasePath() {
        if (StringUtils.isEmpty(databasePath)) {
            return "";
        }
        if (!convDatabasePath) {
            String path = databasePath;
            this.databasePath = convPath(path);
            this.convDatabasePath = true;
        }
        return databasePath;
    }
    /**
     * 組み込み用データベースのパスをセット
     * @param databasePath
     */
    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * @return the basePath
     */
    public String getBasePath() {
        if (StringUtils.isEmpty(basePath)) {
            return "";
        }
        if (!convBasePath) {
            String path = basePath;
            if (StringUtils.isNotEmpty(envKey)) {
                String envValue = SystemUtils.getenv(envKey);
                if (!dispEnvInfo) {
                    if (StringUtils.isNotEmpty(envValue)) {
                        path = envValue;
                        System.out.println("Env [" + envKey + "] was loaded. value is [" + envValue + "].");
                    } else {
                        System.out.println("Env [" + envKey + "] was not found.");
                    }
                    dispEnvInfo = true;
                }
            }
            this.basePath = convPath(path);
            this.convBasePath = true;
        }
        return basePath;
    }

    /**
     * @param basePath
     *            the basePath to set
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * @return the systemName
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * @param systemName
     *            the systemName to set
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * @return the logsPath
     */
    public String getLogsPath() {
        if (StringUtils.isEmpty(logsPath)) {
            return "";
        }
        if (!convLogsPath) {
            String path = logsPath;
            this.logsPath = convPath(path);
            this.convLogsPath = true;
        }
        return logsPath;
    }

    /**
     * @param logsPath
     *            the logsPath to set
     */
    public void setLogsPath(String logsPath) {
        this.logsPath = logsPath;
    }

}
