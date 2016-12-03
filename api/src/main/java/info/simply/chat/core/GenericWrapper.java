package info.simply.chat.core;

import java.util.ArrayList;
import java.util.List;

public class GenericWrapper<T> {
    private T field;

    public T getField() {
        return field;
    }

    public void setField(T field) {
        this.field = field;
    }

    public static<T> GenericWrapper wrap(T field){
        try {
            GenericWrapper wrapper = new GenericWrapper();
            wrapper.setField(field);
            return wrapper;
        }
        catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static<T> List<GenericWrapper> wrap(List<T> fields){
        List<GenericWrapper> list = new ArrayList<GenericWrapper>();
        for (T field: fields)
            list.add(wrap(field));

        return list;
    }

    @Override
    public String toString() {
        return "{" +
                "field:" + field +
                '}';
    }
}
