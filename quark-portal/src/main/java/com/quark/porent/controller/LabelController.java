package com.quark.porent.controller;

import com.quark.common.base.BaseController;
import com.quark.common.dto.QuarkResult;
import com.quark.common.entity.Label;
import com.quark.porent.service.LabelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author LHR
 * Create By 2017/9/4
 */
@RequestMapping("/label")
@Api(value = "标签接口",description = "获取标签")
@Controller
public class LabelController extends BaseController {

    @Autowired
    private LabelService labelService;



    @RequestMapping("/detail")
    public String getLabelDetail(){
        return "label/detail";
    }

    @ApiOperation("获取标签")
    @GetMapping("/api")
    @ResponseBody
    public QuarkResult getAllLabel(){

        QuarkResult result = restProcessor(() -> {
            List<Label> labels = labelService.findAll();
            return QuarkResult.ok(labels);
        });

        return result;
    }

}
