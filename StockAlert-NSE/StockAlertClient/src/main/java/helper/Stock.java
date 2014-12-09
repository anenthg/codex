package helper;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ashwin on 3/8/14.
 */
public class Stock implements Serializable {
    private String stockName,stockCode,stockExchange,stockNotificationPrice,stockFluctuationLevel,updateInterval,notificationSetting,stockText;
       public static String MINUTES15="15 minutes";
    public static String MINUTES30="30 minutes";
    public static String MINUTES45="45 minutes";
    public static String MINUTES60="60 minutes";

    public Stock(String stockName, String stockCode, String stockExchange, String stockNotificationPrice, String stockFluctuationLevel, String updateInterval, String notificationSetting) {
        this.stockName = stockName;
        this.stockCode = stockCode;
        this.stockExchange = stockExchange;
        this.stockNotificationPrice = stockNotificationPrice;
        this.stockFluctuationLevel = stockFluctuationLevel;
        this.updateInterval = updateInterval;
        this.notificationSetting = notificationSetting;
        this.stockText=stockCode+" - "+stockName;
    }

    public String getStockText()
    {
        return stockText;
    }
    public String getStockName() {
        return stockName;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public String getStockNotificationPrice() {
        return stockNotificationPrice;
    }

    public String getStockFluctuationLevel() {
        return stockFluctuationLevel;
    }

    public String getUpdateInterval() {
        return updateInterval;
    }

    public String getNotificationSetting() {
        return notificationSetting;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public void setStockNotificationPrice(String stockNotificationPrice) {
        this.stockNotificationPrice = stockNotificationPrice;
    }

    public void setStockFluctuationLevel(String stockFluctuationLevel) {
        this.stockFluctuationLevel = stockFluctuationLevel;
    }

    public void setUpdateInterval(String updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setNotificationSetting(String notificationSetting) {
        this.notificationSetting = notificationSetting;
    }
}
