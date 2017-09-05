import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by rootlet on 21.09.2016.
 */
public class Main {
    public List<String> listFriday = new ArrayList();
    public List<String> data = new ArrayList();
    public Logic logic;
    public Analyser analyser;

    public static void main(String[] args) throws Exception{
        for (int i = 0; i < 1; i++) {
            Main main = new Main();
            //String filename = "c:\\usd\\years\\201" + i + ".csv";
            String filename = "c:\\usd\\years\\2013.csv";
            //String filename = "c:\\usd\\0\\EURUSD1.csv";
            BufferedReader br = new BufferedReader(new FileReader(filename));
            // 20, 20, 300, 10, 12
            // 20, 20, 400, 80, 10
            // 20, 20, 400, 70, 10
            // 20, 20, 430, 70, 10 (без отрицательного)
            // 20, 20, 470, 70, 10
            // 15, 15, 140, 17, 10
            // 9, 9, 130, 17, 20   по пятницам
            main.logic = new Logic(9, 9, 130, 17, 15);  //(buy_stop, sell_stop, take_profit, stop_loss, update every N bars) ?loss 1558?
            main.fileFilterFriday(br);
            main.data = main.deletePoints(main.listFriday);
            main.startTest();
            main.analyser = new Analyser(Order.closedOrders);
            main.resultTest();
            System.out.println(filename);
            Order.openedOrders = new ArrayList<>();
            Order.closedOrders = new ArrayList<>();
        }


        //System.out.println(main.listFriday.size());
    }

    private void resultTest() {
        /*for (Order order: Order.closedOrders) {
            System.out.println(order.time + " " + order.type  + "  " + order.profit + "   " + order.buy + "   " + order.sell);
        }*/
        System.out.println("-------------------------------------");
        System.out.println(analyser.getProfitSum() + "   - сумма профита");
        System.out.println(Order.closedOrders.size() + "  - ордеров");
        System.out.println(analyser.getLossChain() + "  - макс. кол-во лоссов подряд");
        System.out.println(analyser.getMaxSag() + "  - макс. просадка");
        for (Order o: analyser.getClosedOrders()) {
            System.out.println(o.time);
        }

    }

    private void startTest() {
        for (String line: data) {
            excludeClosedOrders();
            if (Order.openedOrders.size() > 0) {
                logic.checkOrders(line);
            }
            logic.nextTick(line);
            logic.checkFridayEnd(line); // Включить, если считать только по пятницам
        }
    }

    private void excludeClosedOrders() {
        Iterator<Order> iterator = Order.openedOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.opened == -1) {
                Order.closedOrders.add(order);
                iterator.remove();
            }
        }
    }

    private List<String> deletePoints(List<String> listFriday) {
        List<String> result = new ArrayList<>();
        for (String str : listFriday) {
            String[] line = str.split(",");
            line[2] = line[2].replace(".", "");
            line[3] = line[3].replace(".", "");
            line[4] = line[4].replace(".", "");
            line[5] = line[5].replace(".", "");
            StringBuilder sb = new StringBuilder();
            sb.append(line[0]);
            sb.append(",");
            sb.append(line[1]);
            sb.append(",");
            sb.append(line[2]);
            sb.append(",");
            sb.append(line[3]);
            sb.append(",");
            sb.append(line[4]);
            sb.append(",");
            sb.append(line[5]);
            sb.append(",");
            sb.append(line[6]);
            result.add(sb.toString());
        }
        return result;
    }

    private void fileFilterFriday(BufferedReader br) throws IOException {
        Calendar calendar = new GregorianCalendar();
        while (br.ready()) {
            String line = br.readLine();
            String[] string = line.substring(0, 10).split("\\.");
            String[] str = line.split(",");
            String time = str[1].split(":")[0];
            //System.out.println(time);
            int month = Integer.parseInt(string[1]);
            calendar.set(Integer.parseInt(string[0]), month-1, Integer.parseInt(string[2]));
            //if (((calendar.get(Calendar.DAY_OF_WEEK) == 0)  || (calendar.get(Calendar.DAY_OF_WEEK) == 5) || (calendar.get(Calendar.DAY_OF_WEEK) == 6)) && (Integer.parseInt(time) > 13) && (Integer.parseInt(time) < 21)) {
            //if (((calendar.get(Calendar.DAY_OF_WEEK) == 2) || (calendar.get(Calendar.DAY_OF_WEEK) == 6)) && (Integer.parseInt(time) > 14) && (Integer.parseInt(time) < 22)) {
            if ((calendar.get(Calendar.DAY_OF_WEEK) == 6) && (Integer.parseInt(time) > 10) && (Integer.parseInt(time) < 21)) {
                listFriday.add(line);
                System.out.println(line);
            }
        }
    }
}
