package zxc.com.gkdvr.utils.Net;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by dk on 2016/5/31.
 */
public class NetParamas {
    private LinkedHashMap<String,String> params = new LinkedHashMap<>();
    public NetParamas() {
    }
    public void put(String key,String value){
        params.put(key,value);
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }
}
