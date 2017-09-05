import java.util.ArrayList;
import java.util.List;

/**
 * Created by rootlet on 22.09.2016.
 */
public class Order {
    public static List<Order> openedOrders = new ArrayList();
    public static List<Order> closedOrders = new ArrayList();
    public int buy, sell, take_profit, stop_loss, profit;
    int opened;
    public String time;
    public String type;


    public void openBuy(int buy, int take_profit, int stop_loss, String time) {
        this.buy = buy;
        this.take_profit = take_profit;
        this.stop_loss = stop_loss;
        this.time = time;
        opened = 1;
        type = "buy";
    }

    public void openSell(int sell, int take_profit, int stop_loss, String time) {
        this.sell = sell;
        this.take_profit = take_profit;
        this.stop_loss = stop_loss;
        this.time = time;
        opened = 2;
        type = "sell";
    }

    public void close(int profit) {
        this.profit = profit;
        opened = -1;
    }

    public int whatIsOpened() { // -1 Closed, 0 - Not opened, 1 - Opened Buy, 2 - Opened Sell.
        return opened;
    }
}
