package com.syx.yuqingmanage.module.setting.web;

import com.syx.yuqingmanage.module.setting.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/updateArea", method = RequestMethod.POST)
    public String updateArea(@RequestParam("areaId") String areaId, @RequestParam("areaName") String areaName) {
        String result = iAreaService.updateArea(areaId, areaName).toString();
        return result;
    }

    @RequestMapping(value = "/deleteArea", method = RequestMethod.POST)
    public String deleteArea(@RequestParam("areaId") String areaId) {
        String result = iAreaService.deleteArea(areaId).toString();
        return result;
    }

    @RequestMapping(value = "/getAreaMaxId", method = RequestMethod.POST)
    public String getAreaMaxId() {
        String result = iAreaService.getAreaMaxId().toString();
        return result;
    }

    @RequestMapping(value = "/insertArea", method = RequestMethod.POST)
    public String insertArea(@RequestParam("areaData") String areaData) {
        String result = iAreaService.insertArea(areaData).toString();
        return result;
    }


}
