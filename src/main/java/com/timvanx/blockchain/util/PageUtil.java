package com.timvanx.blockchain.util;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>BlockChain</h3>
 * <p>自定义分页</p>
 *
 * @author : TimVan
 * @date : 2020-05-16 12:07
 **/
public class PageUtil {

    /**
     * 开始分页
     *
     * @param list  传入的List
     * @param page  页码
     * @param limit 每页多少条数据
     */
    public static List startPage(List list, Integer page,
                                 Integer limit) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }
        // 记录总数
        Integer count = list.size();
        // 总页数
        Integer pageCount = 0;
        if (count % limit == 0) {
            pageCount = count / limit;
        } else {
            pageCount = count / limit + 1;
        }

        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引

        if (!page.equals(pageCount)) {
            fromIndex = (page - 1) * limit;
            toIndex = fromIndex + limit;
        } else {
            fromIndex = (page - 1) * limit;
            toIndex = count;
        }
        //不包含toIndex
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 倒序分页
     *
     * @param list  传入的List
     * @param page  页码
     * @param limit 每页多少条数据
     */
    public static List startReversePage(List list, Integer page,
                                        Integer limit) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }
        // 记录总数
        Integer count = list.size();
        // 总页数
        Integer pageCount = 0;
        if (count % limit == 0) {
            pageCount = count / limit;
        } else {
            pageCount = count / limit + 1;
        }
        // 开始索引
        int fromIndex = 0;
        // 结束索引
        int toIndex = 0;

        if (!page.equals(pageCount)) {
            fromIndex = (count-page*limit);
            toIndex = fromIndex+limit;
        } else {
            fromIndex = 0;
            toIndex = count - (pageCount - 1) * limit;
        }
        //不包含toIndex
        return list.subList(fromIndex, toIndex);
    }


    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 32; i++) {
            list.add(i);
        }

        List<Integer> reverseList = (List<Integer>) PageUtil.startReversePage(list,4,10);
        for (Integer cache :reverseList) {
            System.out.println(cache);
        }

    }
}
