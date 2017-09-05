/**
 * Created by rootlet on 22.09.2016.
 */
public class Logic {

    private final int buy_stop_delta;
    private final int sell_stop_delta;
    private final int take_profit_delta;
    private final int stop_loss_delta;
    private final int update_time_delta;
    private int buy_stop;
    private int sell_stop;
    private int take_profit_buy;
    private int take_profit_sell;
    private int stop_loss_buy;
    private int stop_loss_sell;
    private int update_time;
    private boolean isNewDay = true;
    private String lastDay = new String();
    private int spread = 2;

    public Logic(int buy_stop_delta, int sell_stop_delta, int take_profit_delta, int stop_loss_delta, int update_time_delta) {
        this.buy_stop_delta = buy_stop_delta;
        this.sell_stop_delta = sell_stop_delta;
        this.take_profit_delta = take_profit_delta;
        this.stop_loss_delta = stop_loss_delta;
        this.update_time_delta = update_time_delta;
    }

    public void checkOrders(String line) {
        String[] str = line.split(",");
        int highBid = Integer.parseInt(str[3]);
        int lowBid = Integer.parseInt(str[4]);
        int highAsk = Integer.parseInt(str[3]) + spread;
        int lowAsk = Integer.parseInt(str[4]) + spread;

        for (Order order: Order.openedOrders) {
            int type = order.whatIsOpened();
            if (type == 1) { //opened buy
                if ((order.take_profit <= highBid) && (order.stop_loss >= lowBid)) System.out.println("Спорная свеча: TP/SL");
                if (order.take_profit <= highBid) {
                    order.close(order.take_profit - order.buy);
                    isNewDay = true;
                } else {
                    if (order.stop_loss >= lowBid) {
                        order.close(order.stop_loss - order.buy);
                        isNewDay = true;
                    }
                }
            } else {
                if (type == 2) { //opened sell
                    if ((order.take_profit >= lowAsk) && (order.stop_loss <= highAsk)) System.out.println("Спорная свеча: TP/SL");
                    if (order.take_profit >= lowAsk) {
                        order.close(order.sell - (order.take_profit - spread));
                        isNewDay = true;
                    } else {
                        if (order.stop_loss <= highAsk) {
                            order.close(order.sell - (order.stop_loss - spread));
                            isNewDay = true;
                        }
                    }
                }
            }
        }
    }

    public void nextTick(String line) {
        String[] str = line.split(",");
        String time = str[0] + "," + str[1];
        int open = Integer.parseInt(str[2]);
        int highBid = Integer.parseInt(str[3]);
        int lowBid = Integer.parseInt(str[4]);
        int highAsk = Integer.parseInt(str[3]) + spread;
        int lowAsk = Integer.parseInt(str[4]) + spread;
        if (!lastDay.equals(str[0])) {  // если наступил следующий день...
            isNewDay = true;
        //    System.out.println(" NEW DAY!!! " + time);
        }
        lastDay = str[0];
        if ((isNewDay) || (update_time_delta <= update_time)) {
            isNewDay = false;
            calcOrder(open);
        }

        if (Order.openedOrders.size() == 0) {
            if ((highAsk >= buy_stop) && (lowBid <= sell_stop)) System.out.println("Спроное открытие ордера");
            if (highAsk >= buy_stop){
                //System.out.println("high >= buy_stop");
                Order order = new Order();
                order.openBuy(buy_stop, take_profit_buy, stop_loss_buy, time);
                Order.openedOrders.add(order);
            }else{
                if (lowBid <= sell_stop){
                    //System.out.println("low <= sell_stop");
                    Order order = new Order();
                    order.openSell(sell_stop, take_profit_sell, stop_loss_sell, time);
                    Order.openedOrders.add(order);
                }
            }
        }

        update_time++;
    }

    private void calcOrder(int open) {
        //if (update_time_delta <= update_time) { // обновляем условия ордера
            //System.out.println("calcOrder");
            buy_stop = open + buy_stop_delta + spread; //сравнивается по Ask
            sell_stop = open - sell_stop_delta;
            take_profit_buy = buy_stop + take_profit_delta;
            take_profit_sell = sell_stop - take_profit_delta + spread; // сравнивается по Ask
            stop_loss_buy = buy_stop - stop_loss_delta;
            stop_loss_sell = sell_stop + stop_loss_delta + spread; // сравнивается по Ask
            update_time = 0;
       // }
    }

    public void checkFridayEnd(String line) {
        String[] str = line.split(",");
        int open = Integer.parseInt(str[2]);
        String[] time = str[1].split(":");
        int hour = Integer.parseInt(time[0]);
        int mitute = Integer.parseInt(time[1]);
        if ((hour == 20) && (mitute >= 59)) {   // время закрытия всех сделок в пятницу
            if (Order.openedOrders.size() > 0) {
                System.out.println(Order.openedOrders.size());
                for (Order order: Order.openedOrders) {
                    int type = order.whatIsOpened();
                    if (type == 1) { //opened buy
                        order.close(open - order.buy);
                        System.out.println("Закрыли в пятницу вечером " + (open - order.buy));
                    } else {
                        if (type == 2) { //opened sell
                            order.close(order.sell - open);
                            System.out.println("Закрыли в пятницу вечером " + (order.sell - open));
                        }
                    }
                }


            }
        }

    }
}
