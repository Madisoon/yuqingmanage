package com.syx.yuqingmanage.module.setting.web;

import com.syx.yuqingmanage.module.setting.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/11/29.
 */
@RestController
@RequestMapping(value = "/manage")
public class AreaController {
    @Autowired
    private IAreaService iAreaService;

    @RequestMapping(value = "/getTypeArea", method = RequestMethod.POST)
    public String getTypeArea(@RequestParam("areaId") String areaId) {
        String result = iAreaService.getTypeArea(areaId).toString();
        return result;
    }

    @RequestMapping(value = "/postAreaData", method = RequestMethod.POST)
    public String postAreaData(@RequestParam("areaId") String areaId, @RequestParam("areaValue") String areaValue,
                               @RequestParam("areaGrade") String areaGrade) {
        String result = iAreaService.postAreaData(areaId, areaValue, areaGrade).toString();
        return result;
    }

    @RequestMapping(value = "/getAllArea", method = RequestMethod.POST)
    public String getAllArea() {
        String result = iAreaService.getAllArea().toString();
        return result;
    }
}
