package com.dalread.util;

import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaInBook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionUtil {
    public static Map<String, IVocaFullPlayTTSItem> convertListToMap(List<IVocaFullPlayTTSItem> list) {
        return list.stream().collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
    }

    public static Map<String, VocaInBook> convertListToMapVocaInBook(List<VocaInBook> list) {
        return list.stream().collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
    }

    public static int getIndexInsideList(int index, List list) {
        int result = index;
        if (list != null) {
            if (index < 0) {
                result = 0;
            } else if (index >= list.size()) {
                result = list.size() - 1;
            }
        }
        return result;
    }
    public static int getIndexInsideListWhenOutOfIndex(int index, List list) {
        int result = index;
        if (list != null) {
            if (index < 0) {
                result = 0;
            } else if (index >= list.size()) {
                result = list.size() - 1;
            }
        }
        return result;
    }
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Collection<T> deepCopy(Collection<T> original) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(original);
            oos.flush();
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Collection<T> copy = (Collection<T>) ois.readObject();
            ois.close();
            return copy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//    public static Map<String, ? extends IVocaFullPlayTTSItem> convertListToMap(List<? extends IVocaFullPlayTTSItem > list) {
//        return list.stream().collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
//    }

    public static <T> List<T> fetchItems(List<T> list, int count) {
        List<T> selectedItems = new ArrayList<>();

        if (count <= list.size()) {
            // Fetch items from the beginning of the list up to the count
            selectedItems = list.subList(0, count);
        } else {
            // The count is greater than the size of the list
            // Handle this scenario accordingly, for example, by fetching all items
            selectedItems = list;
        }

        return selectedItems;
    }

    public static <T> List<T> getRandomElements(List<T> list, int numberOfElements) {
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(numberOfElements, copy.size()));
    }

}
