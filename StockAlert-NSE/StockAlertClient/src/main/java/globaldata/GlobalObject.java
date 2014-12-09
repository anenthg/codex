package globaldata;

import android.app.Application;
import android.widget.Adapter;

import java.util.List;

import helper.Stock;
import helper.StockAdapter;

/**
 * Created by ashwin on 14/8/14.
 */
public class GlobalObject extends Application {

    private List<Stock> stockList;
    private StockAdapter stockAdapter;

    public StockAdapter getStockAdapter() {
        return stockAdapter;
    }

    public void setStockAdapter(StockAdapter stockAdapter) {

        this.stockAdapter = stockAdapter;
    }

    public List<Stock> getStockList()
    {
        return stockList;
    }

    public void setStockList(List<Stock> stockList)
    {
        this.stockList=stockList;
    }
}
