package com.paradisecloud.fcm.fme.model.websocket.callinfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call list 更新
 *
 * @author zt1994 2019/8/30 14:12
 */
@Getter
@Setter
@ToString
public class CallInfoUpdate extends CallListUpdate
{
    
    /**
     * new participants will be muted when joining the conference
     */
    private Boolean joinAudioMuteOverride;
    
    /**
     * <pre>获取所有字段名</pre>
     * 
     * @author lilinhai
     * @since 2020-12-11 18:56
     * @return String[]
     */
    public static String[] getAllFieldNames()
    {
        Field[] fs = CallInfoUpdate.class.getDeclaredFields();
        List<String> fl = new ArrayList<String>(fs.length);
        for (Field field : fs)
        {
            fl.add(field.getName());
        }
        fl.addAll(Arrays.asList(CallListUpdate.getAllFieldNames()));
        return fl.toArray(new String[fl.size()]);
    }
}
