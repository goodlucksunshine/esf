package com.laile.esf.web.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Created by sunshine on 16/7/25.
 */
public class MappingJacksonJsonVIew extends MappingJackson2JsonView {

    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> resultMap = new HashMap();
        Map<String, Object> data = new HashMap(model.size());
        Set<String> renderedAttributes = !CollectionUtils.isEmpty(getModelKeys()) ? getModelKeys() : model.keySet();
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if ((!(entry.getValue() instanceof BindingResult)) && (renderedAttributes.contains(entry.getKey()))) {
                if ((!"result".equals(entry.getKey())) && (!"tipsMsg".equals(entry.getKey()))) {
                    data.put(entry.getKey(), entry.getValue());
                }
            }
        }
        String result = (String) model.get("result");
        resultMap.put("result", StringUtils.isEmpty(result) ? "success" : result);
        resultMap.put("tipsMsg", model.get("tipsMsg"));
        if(MapUtils.isNotEmpty(data)){
            resultMap.put("data", data);
        }
        return resultMap;
    }
}
