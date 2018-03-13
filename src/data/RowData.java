package data;
import java.util.ArrayList;

public class RowData implements Comparable<RowData> {
    public int CKn;
    public double[] data;

    RowData() {}

    RowData(int CKn, double[] data){
        this.CKn=CKn;
        this.data=data;
    }

    public int compareTo(RowData row){ // 注意，这里自己注意不同CKn的不要比较
        for(int i=0; i<CKn; i++) { // 从主到次比较
            double c =data[i]-row.data[i];
            if (Math.abs(c-0.0) <= 0.000001) {
                continue;
            }
            else if(c>0)
                return 1;
            else
                return -1;
        }
        return 0;
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + CKn;
        for(int i=0;i<CKn; i++) {
            long bits = Double.doubleToLongBits(data[i]);
            hash = 31*hash + (int)(bits ^ (bits >>> 32));
        }
        return hash;
    }

    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null || (obj.getClass() != this.getClass()))
            return false;

        //能执行到这里，说明Obj和this同类且非null
        RowData objdata = (RowData)obj;
        for(int i=0;i<CKn; i++) {
            if(this.data[i]!=objdata.data[i])
                return false;
        }
        return true;
    }



}
