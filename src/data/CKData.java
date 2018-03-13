package data;

import java.util.List;

abstract public class CKData<E> {
    // the number of clustering keys
    public int CKn;

    // the set of the distribution functions of clustering keys
    public List<E> CKdist;

    //the number of rows
    public int rows;


    /*
    得到按照排序规则排在点point之前的概率
     */
    abstract public double getBefore(RowData point);

    /*
    得到按照排序规则排在两个点之间的概率
    pi是range查询点的列
    要求p1的第qi列的数据和p2的第qi的数据是离散地相邻分布，并且p1在前
    要求p1p2除了第qi列不同之外，其余列代表的点查询相同
     */
    abstract public double getBetween(RowData p1, RowData p2, int qi);
}
