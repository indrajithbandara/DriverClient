package com.botann.driverclient.utils;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * 此文件是根据输入的参数进行排序
 * eg： 输入参数为 <Student> (String propertyName ,boolean isASC) 返回的结果为 list<Student>, 不过是根据propertyName升序排列后的结果
 * 根据Student类中的propertyName字段进行排序。
 * T表示是Student类，propertyName是类中的一个字段名称
 *
 * @author YinLinFei
 * @param <T>
 */

/**
 * 字段名的首字母需要大写
 * @author YinLinFei
 *
 */
public class sortList<T> implements Comparator<T> {

    private String  propertyName;
    private boolean isAsc;

    public sortList(String propertyname, boolean isasc) {
        this.propertyName = propertyname;
        this.isAsc = isasc;
    }

    /**
     * 需要的是：根据类中的字段对对象进行排序
     *
     * @return
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compare(T b1, T b2) {

        Class<?> clz = b1.getClass();
        Method mth = getPropertyMethod(clz, propertyName);

        try {
            Object o1 = mth.invoke(b1);
            Object o2 = mth.invoke(b2);

            if (o1 == null || o2 == null) return 0;
            Comparable value1 = (Comparable) o1;
            Comparable value2 = (Comparable) o2;

            if (isAsc) {
                return value1.compareTo(value2);
            } else {
                return value2.compareTo(value1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

        //原先的固定的根据某一个固定的字段进行排序
        // if (b1 == null || b2 == null) {
        // return 0;
        // }
        // if (isAsc) {
        // return b1.getWeight().compareTo(b2.getWeight());
        // } else {
        // return b2.getWeight().compareTo(b1.getWeight());
        // }
    }

    // 获取类名
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Method getPropertyMethod(Class clz, String propertyName) {
        Method mth = null;
        try {
            mth = clz.getMethod("get" + propertyName);
        } catch (Exception e) {
            System.out.println("获取类名发生错误！");
        }
        return mth;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }

}
