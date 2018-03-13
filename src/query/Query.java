package query;

import data.RowData;

public class Query {
    public int CKn;

    // range query的位置（对应的就是排序后的第i位,注意编程中从0开始
    public int qi;

    public double r1;
    public boolean lisclosed;
    public double r2;
    public boolean risclosed;

    public double[] CKPoints; // 长度为CKn,从0开始的第qi位数值随便赋值，用不到

    public Query(int CKn, int i, double r1, double r2, boolean l, boolean r, double[] ckpoints) {
        this.CKn=CKn;
        this.qi=i;
        this.r1=r1;
        this.r2=r2;
        this.lisclosed=l;
        this.risclosed=r;
        CKPoints = ckpoints;
    }

    // check whether this row fits or not
    public boolean fit(RowData rowData){
        if(rowData.CKn!=CKn){
            System.out.println("please check CKn is equal");
            return false;
        }
        double lc = rowData.data[qi]-r1;
        if(lisclosed) {
            if(lc<0)
                return false;
        }
        else {
            if(Math.abs(lc-0.0) <= 0.000001)//TODO 这里不确定两个double相等可以不可以
                return false;
            if(lc<0)
                return false;
        }

        double rc = rowData.data[qi]-r2;
        if(risclosed) {
            if(rc>0)
                return false;
        }
        else {
            if(Math.abs(rc-0.0) <= 0.000001)//TODO 这里不确定两个double相等可以不可以
                return false;
            if(rc>0)
                return false;
        }

        for(int j=0;j<CKn;j++){
            if(j==qi)
                continue;
            double c = CKPoints[j]-rowData.data[j];
            if(Math.abs(c-0.0) > 0.000001){
                return false;
            }
        }

        return true;
    }

}
