package helper;

import java.io.Serializable;

/**
 * Created by ashwin on 20/9/14.
 * This class contains the stock details that is fetched from the NSE site
 * Details of the stock in the market is captured in this class like it's last price, 52 week high/low etc
 */
public class StockQuote implements Serializable {

    private String stockCode,lastPrice,dayHigh,dayLow,yearHigh,yearLow,openPrice,closePrice,pChange,sharesTraded,lastUpdatedTime;

    public StockQuote(String stockCode, String lastPrice, String dayHigh, String dayLow, String yearHigh, String yearLow, String openPrice, String closePrice,String pChange,String sharesTraded,String lastUpdatedTime) {
        this.stockCode = stockCode;
        this.lastPrice = lastPrice;
        this.dayHigh = dayHigh;
        this.dayLow = dayLow;
        this.yearHigh = yearHigh;
        this.yearLow = yearLow;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.pChange=pChange;
        this.sharesTraded=sharesTraded;
        this.lastUpdatedTime=lastUpdatedTime;
    }

    public String getpChange() {
        return pChange;
    }

    public String getSharesTraded() {
        return sharesTraded;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setpChange(String pChange) {
        this.pChange = pChange;
    }

    public void setSharesTraded(String sharesTraded) {
        this.sharesTraded = sharesTraded;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getYearLow() {
        return yearLow;
    }

    public void setYearLow(String yearLow) {
        this.yearLow = yearLow;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(String dayHigh) {
        this.dayHigh = dayHigh;
    }

    public String getDayLow() {
        return dayLow;
    }

    public void setDayLow(String dayLow) {
        this.dayLow = dayLow;
    }

    public String getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(String yearHigh) {
        this.yearHigh = yearHigh;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }
}
