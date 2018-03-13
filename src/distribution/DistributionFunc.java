package distribution;

abstract public class DistributionFunc {
    /*
    离散分布某一点point的概率
     */
    abstract double getPoint(double point);
    /*
    小于点point的概率,如果离散分布下不包括点point
     */
   abstract double getLeftRangeExclude(double point);
    /*
    大于点point的概率,如果离散分布下不包括点point
     */
    abstract double getRightRangeExclude(double point);
    /*
    随机给出一个满足分布参数的值
     */
    abstract public double getValue();
}
