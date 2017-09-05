import java.util.List;

/**
 * Created by pavlenko on 28.09.16.
 */
public class Analyser {
    private List<Order> closedOrders;

    public List<Order> getClosedOrders() {
        return closedOrders;
    }

    public Analyser(List<Order> closedOrders) {
        this.closedOrders = closedOrders;
    }

    public int getProfitSum() {
        int profitSum = 0;
        for (Order order : closedOrders) {
            profitSum += order.profit;
        }
        return profitSum;
    }

    public int getLossChain() {
        int max = 0;
        int maxLossChain = 0;
        for (Order order : closedOrders) {
            if (order.profit < 0) {
                max ++;
            } else {
                max = 0;
            }
            if (maxLossChain < max) {
                maxLossChain = max;
            }

        }
        return maxLossChain;
    }

    public int getMaxSag() { // максимальная просадка
        int ballance = 0;
        int maxBallance = 0;
        int minBallance = 0;
        int delta ;
        int maxSag = 0;
        for (Order order : closedOrders) {
            if (order.profit < 0) {
                ballance += order.profit;
            } else {
                ballance += order.profit;
            }
           // System.out.println(ballance);
            if (maxBallance < ballance) {
                maxBallance = ballance;
                minBallance = ballance;
            }

            if (minBallance > ballance) {
                minBallance = ballance;
            }

            delta = maxBallance - minBallance;
            if (maxSag < delta) {
                maxSag = delta;
            }

        }
        return maxSag;

    }
}
