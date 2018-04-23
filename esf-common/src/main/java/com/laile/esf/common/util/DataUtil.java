package com.laile.esf.common.util;

import com.laile.esf.common.model.Hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataUtil {
    public static <E, T extends Hierarchy<E>> List<T> dataListToTree(List<T> dbList) {
        if ((dbList.size() == 0) || (dbList.size() == 1)) {
            return dbList;
        }

        HashMap<E, T> map = listToMap(dbList);

        ArrayList<T> root = new ArrayList();

        for (T info : dbList) {
            E parentCode = info.getParentId();
            if (parentCode == null) {
                root.add(info);

            } else if (parentCode.equals(info.getId())) {
                root.add(info);
            } else {
                T parent = (T) map.get(parentCode);
                if (parent == null) {
                    root.add(info);
                } else {
                    List<Hierarchy<E>> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList();
                        parent.setChildren(children);
                    }
                    children.add(info);
                    info.setParent(parent);
                }
            }
        }
        return root;
    }

    private static <E, T extends Hierarchy<E>> HashMap<E, T> listToMap(List<T> dbList) {
        HashMap<E, T> result = new HashMap(dbList.size());
        for (T info : dbList) {
            result.put(info.getId(), info);
        }
        return result;
    }
}